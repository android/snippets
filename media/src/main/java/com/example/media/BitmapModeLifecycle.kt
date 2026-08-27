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
import android.content.Context
import android.graphics.Bitmap
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.tasks.Task
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// [START android_media_ai_enhancement_bitmap_initialize_engine]
class MediaSetupViewModel(application: Application) : AndroidViewModel(application) {
    private val enhancementClient = Enhancement.getClient(application)
    fun initializeEnhancementEngine() {
        viewModelScope.launch {
            try {
                // 1. Verify hardware capability
                val isSupported = enhancementClient.isDeviceSupportedAsync()
                if (!isSupported) {
                    notifyUiDeviceIncompatible()
                    return@launch
                }
                // 2. Verify and download the Google Play services ML modules
                val isInstalled = enhancementClient.isModuleInstalledAsync()
                if (!isInstalled) {
                    notifyUiDownloadingModels()
                    enhancementClient.installModule().await() 
                }
                notifyUiEngineReady()
            } catch (e: Exception) {

                // Handle potential errors during session creation or image processing.
                handleInitializationError(e)
            }
        }
    }
    // [START_EXCLUDE silent]
    private fun notifyUiDeviceIncompatible() {}
    private fun notifyUiDownloadingModels() {}
    private fun notifyUiEngineReady() {}
    private fun handleInitializationError(e: Exception) {}
    // [END_EXCLUDE]
}
// [END android_media_ai_enhancement_bitmap_initialize_engine]

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
        // [START_EXCLUDE silent]
        /*
        // [END_EXCLUDE]
        this.createSession(options, callback).addOnFailureListener(executor) { e ->
            if (continuation.isActive) {
                continuation.resumeWithException(e)
            }
        }
        // [START_EXCLUDE silent]
        */
        this@createSessionAsync.createSession(options, callback).addOnFailureListener(executor) { e ->
            if (continuation.isActive) {
                continuation.resumeWithException(e)
            }
        }
        // [END_EXCLUDE]
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

// Primary function to trigger the enhancement workflow for a provided bitmap.
    fun enhanceImage(bitmap: Bitmap) {
        viewModelScope.launch(Dispatchers.IO) {
            _uiState.update { it.copy(isLoading = true, enhancementError = null) }
            try {
                // 1. Establish the session lazily on demand

// Define enhancement options (for example, enable upscale, tonemapping) based
// on bitmap dimensions.
                if (enhancementSession == null) {
                    val options = EnhancementOptions(
                        bitmap.width,
                        bitmap.height,
                        EnhancementMode.BITMAP,
                        enableTonemap = true,
                        enableDeblurDenoise = true,
                        enableDenoiseOnly = false,
                        enableUpscale = false,
                    )
                    enhancementSession = enhancementClient.createSessionAsync(options, enhancementExecutor)
                }
                val session = enhancementSession ?: throw IllegalStateException("Session unavailable.")
                // 2. Dispatch image through the neural pipeline
                val enhancedBitmap = session.processBitmapAsync(bitmap, session.defaultOptions)
                // 3. Render output to UI
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
        // 4. Critical: Release native GPU hardware resources
        enhancementSession?.release()
        enhancementSession = null
        enhancementExecutor.shutdown()
        super.onCleared()
    }
}
// [END android_media_ai_enhancement_bitmap_viewmodel]

// Shims for Media Enhancement API types if not provided by standalone SDK
object Enhancement {
    fun getClient(context: Context): EnhancementClient = object : EnhancementClient {
        override fun isDeviceSupported(): Task<Boolean> = TODO()
        override fun isModuleInstalled(): Task<Boolean> = TODO()
        override fun installModule(): Task<Void> = TODO()
        override fun createSession(options: EnhancementOptions, callback: EnhancementSessionCallback): Task<Void> = TODO()
    }
}

interface EnhancementClient {
    fun isDeviceSupported(): Task<Boolean>
    fun isModuleInstalled(): Task<Boolean>
    fun installModule(): Task<Void>
    fun createSession(options: EnhancementOptions, callback: EnhancementSessionCallback): Task<Void>
    suspend fun isDeviceSupportedAsync(): Boolean = false
    suspend fun isModuleInstalledAsync(): Boolean = false
}

interface EnhancementSession {
    val defaultOptions: EnhancementOptions
    fun process(bitmap: Bitmap, options: EnhancementOptions, callback: EnhancementCallback)
    fun release()
}

interface EnhancementSessionCallback {
    fun onSessionCreated(session: EnhancementSession)
    fun onSessionCreationFailed(status: Status)
    fun onSessionDestroyed()
    fun onSessionDisconnected(status: Status)
}

interface EnhancementCallback {
    fun onBitmapProcessed(enhancedBitmap: Bitmap)
    fun onError(statusCode: Int)
    fun onSurfaceProcessed(timestamp: Long)
}

enum class EnhancementMode {
    BITMAP,
    SURFACE
}

data class EnhancementOptions(
    val width: Int,
    val height: Int,
    val enhancementMode: EnhancementMode,
    val enableTonemap: Boolean = false,
    val enableDeblurDenoise: Boolean = false,
    val enableDenoiseOnly: Boolean = false,
    val enableUpscale: Boolean = false,
    val enableFaceDetection: Boolean = false
) {
    fun setInputSurface(surface: android.view.Surface) {}
    fun setOutputSurface(surface: android.view.Surface) {}
}

data class Status(val statusCode: Int, val statusMessage: String)
