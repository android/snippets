/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.media

import android.app.Application
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.common.api.Status
import com.google.android.gms.media.effect.enhancement.Enhancement
import com.google.android.gms.media.effect.enhancement.EnhancementCallback
import com.google.android.gms.media.effect.enhancement.EnhancementClient
import com.google.android.gms.media.effect.enhancement.EnhancementMode
import com.google.android.gms.media.effect.enhancement.EnhancementOptions
import com.google.android.gms.media.effect.enhancement.EnhancementSession
import com.google.android.gms.media.effect.enhancement.EnhancementSessionCallback
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.coroutines.cancellation.CancellationException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

private object BitmapModeLifecycleSnippet {
    // [START android_media_ai_enhancement_bitmap_initialize_engine]
    class MediaSetupViewModel(application: Application) : AndroidViewModel(application) {
        private val enhancementClient = Enhancement.getClient(application)
        fun initializeEnhancementEngine() {
            viewModelScope.launch {
                try {
                    // 1. Verify hardware capability.
                    val isSupported = enhancementClient.isDeviceSupportedAsync()
                    if (!isSupported) {
                        notifyUiDeviceIncompatible()
                        return@launch
                    }
                    // 2. Verify and download the Google Play services ML modules.
                    val isInstalled = enhancementClient.isModuleInstalledAsync()
                    if (!isInstalled) {
                        notifyUiDownloadingModels()
                        enhancementClient.installModule(installStatusCallback).await()
                    }
                    notifyUiEngineReady()
                } catch (e: Exception) {

                    // Handle potential errors during session creation or image processing.
                    handleInitializationError(e)
                }
            }
        }
    }
    // [END android_media_ai_enhancement_bitmap_initialize_engine]

    // Private copies so this file doesn't depend on GetStartedEnhancement.kt.
    private suspend fun EnhancementClient.isDeviceSupportedAsync(): Boolean = isDeviceSupported().await()

    private suspend fun EnhancementClient.isModuleInstalledAsync(): Boolean = isModuleInstalled().await()
}

// [START android_media_ai_enhancement_bitmap_wrappers]
// Wraps the task-based createSession callback into a suspending function.
suspend fun EnhancementClient.createSessionAsync(
    options: EnhancementOptions,
    executor: Executor
): EnhancementSession = withContext(Dispatchers.Main) {
    suspendCancellableCoroutine { continuation ->

        // EnhancementSessionCallback handles session success or failure.
        val callback = object : EnhancementSessionCallback {
            override fun onSessionCreated(session: EnhancementSession) {
                continuation.resume(session)
            }
            override fun onSessionCreationFailed(status: Status) {
                continuation.resumeWithException(
                    Exception("Session creation failed: ${status.statusMessage} (${status.statusCode})")
                )
            }
            override fun onSessionDestroyed() {}
            override fun onSessionDisconnected(status: Status) {}
        }

        // Handles errors during the initial request trigger.
        this@createSessionAsync.createSession(options, callback).addOnFailureListener(executor) { e ->
            if (continuation.isActive) {
                continuation.resumeWithException(e)
            }
        }
    }
}

// Wraps this process in a suspending function for cleaner execution.
suspend fun EnhancementSession.processBitmapAsync(
    bitmap: Bitmap,
    options: EnhancementOptions
): Bitmap = suspendCancellableCoroutine { continuation ->

    // EnhancementCallback returns the processed bitmap or an error code.
    val callback = object : EnhancementCallback {
        override fun onBitmapProcessed(enhancedBitmap: Bitmap) {
            continuation.resume(enhancedBitmap)
        }
        override fun onError(statusCode: Int) {
            continuation.resumeWithException(
                Exception("Bitmap processing failed with status code: $statusCode")
            )
        }
        override fun onSurfaceProcessed(timestamp: Long) {}
        override fun onCancelled(statusCode: Int) {}
    }
    this.process(bitmap, options, callback)
}
// [END android_media_ai_enhancement_bitmap_wrappers]

// [START android_media_ai_enhancement_bitmap_viewmodel]
// Define a data class to hold image information.
data class ImageInfo(val bitmap: Bitmap)
// Define a UI state class to hold loading status, errors, and enhanced image.
data class EnhancementUiState(
    val isLoading: Boolean = false,
    val enhancementError: String? = null,
    val enhancedImage: ImageInfo? = null
)

class EnhancementViewModel(application: Application) : AndroidViewModel(application) {

    // Backing field for UI state, initialized with default values.
    private val _uiState = MutableStateFlow(EnhancementUiState())
    // Publicly exposed UI state flow for observation.
    val uiState: StateFlow<EnhancementUiState> = _uiState.asStateFlow()

    // Initialize client to interact with the Media Enhancement service.
    private val enhancementClient: EnhancementClient = Enhancement.getClient(application)

    // Single-thread executor for processing background enhancement tasks.
    private val enhancementExecutor = Executors.newSingleThreadExecutor()

    // Track session state to enable reuse across multiple processing calls.
    private var enhancementSession: EnhancementSession? = null
    private var sessionOptions: EnhancementOptions? = null

    // Primary function to trigger the enhancement workflow for a provided bitmap.
    fun enhanceImage(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, enhancementError = null) }
            try {
                // Define enhancement options (for example, enable upscale, tonemapping) based
                // on bitmap dimensions.
                val options = EnhancementOptions(
                    bitmap.width,
                    bitmap.height,
                    EnhancementMode.BITMAP,
                    isTonemappingEnabled = true,
                    isDeblurAndDenoisePhotoEnabled = true,
                    isUpscalePhotoEnabled = false,
                )
                // 1. Establish the session lazily on demand. A session is configured for
                // specific input dimensions, so replace it when the bitmap size changes.
                if (enhancementSession == null ||
                    sessionOptions?.width != options.width ||
                    sessionOptions?.height != options.height
                ) {
                    enhancementSession?.release()
                    enhancementSession = null
                    enhancementSession = enhancementClient.createSessionAsync(options, enhancementExecutor)
                    sessionOptions = options
                }
                val session = enhancementSession ?: throw IllegalStateException("Session unavailable.")
                // 2. Dispatch image through the neural pipeline.
                val enhancedBitmap = session.processBitmapAsync(bitmap, options)
                // 3. Render output to UI.
                _uiState.update {
                    it.copy(enhancedImage = ImageInfo(bitmap = enhancedBitmap))
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(enhancementError = e.message) }
            } finally {

                // Ensure loading state is reset regardless of the outcome.
                _uiState.update { it.copy(isLoading = false) }
            }
        }
    }
    override fun onCleared() {
        // 4. Critical: Release native GPU hardware resources.
        enhancementSession?.release()
        enhancementSession = null
        enhancementExecutor.shutdown()
        super.onCleared()
    }
}
// [END android_media_ai_enhancement_bitmap_viewmodel]

// [START android_media_ai_enhancement_bitmap_progressive_enhancer]
class ProgressiveImageEnhancer(
    private val enhancementClient: EnhancementClient,
    private val executor: Executor,
) {
    // Callers can request enhancements concurrently, but they all share one
    // session, so process one image at a time.
    private val mutex = Mutex()
    private var session: EnhancementSession? = null
    private var sessionOptions: EnhancementOptions? = null

    // Enhancement is optional for this pattern, so return null instead of
    // throwing. The caller can keep the low-resolution image without extra
    // error handling.
    suspend fun enhance(lowResBitmap: Bitmap): Bitmap? = mutex.withLock {
        val options = EnhancementOptions(
            width = lowResBitmap.width,
            height = lowResBitmap.height,
            enhancementMode = EnhancementMode.BITMAP,
            isDeblurAndDenoisePhotoEnabled = true,
            isUpscalePhotoEnabled = true,
        )
        try {
            getOrCreateSession(options).processBitmapAsync(lowResBitmap, options)
        } catch (e: CancellationException) {
            // A cancelled request (for example, the user scrolled away) isn't
            // an enhancement failure, so let the caller's scope handle it.
            throw e
        } catch (e: Exception) {
            null
        }
    }

    // Sessions are expensive to create and are configured for specific input
    // dimensions, so reuse the current one until the image size changes.
    private suspend fun getOrCreateSession(options: EnhancementOptions): EnhancementSession {
        val currentSession = session
        val currentOptions = sessionOptions
        if (currentSession != null &&
            currentOptions != null &&
            currentOptions.width == options.width &&
            currentOptions.height == options.height
        ) {
            return currentSession
        }
        releaseSession()
        return enhancementClient.createSessionAsync(options, executor).also {
            session = it
            sessionOptions = options
        }
    }

    // A session holds GPU memory until it's released, so free it as soon as
    // the images it enhances are no longer on screen. Taking the lock first
    // waits for any in-progress enhancement, so a session that's still being
    // created isn't leaked.
    suspend fun release() = mutex.withLock { releaseSession() }

    private fun releaseSession() {
        session?.release()
        session = null
        sessionOptions = null
    }
}
// [END android_media_ai_enhancement_bitmap_progressive_enhancer]

private val installStatusCallback = object : EnhancementClient.InstallStatusCallback {
    override fun onError(description: String) {}
    override fun onCancelled() {}
    override fun onDownloadProgressUpdate(progress: Int) {}
    override fun onDownloadPending() {}
    override fun onDownloadStart() {}
    override fun onDownloadPaused() {}
    override fun onDownloadComplete() {}
    override fun onInstalled() {}
}

private fun notifyUiDeviceIncompatible() {}

private fun notifyUiDownloadingModels() {}

private fun notifyUiEngineReady() {}

private fun handleInitializationError(e: Exception) {}
