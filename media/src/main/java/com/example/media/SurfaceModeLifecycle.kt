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
import android.media.ImageReader
import android.view.Surface
import android.view.SurfaceView
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import java.util.concurrent.Executor
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

// [START android_media_ai_enhancement_surface_initialize_engine]
class SurfaceMediaSetupViewModel(application: Application) : AndroidViewModel(application) {
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
                // Handle potential errors during session creation or image
                // processing.
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
// [END android_media_ai_enhancement_surface_initialize_engine]

private suspend fun singleFrameSnapshot(
    imageReader: ImageReader,
    surfaceView: SurfaceView,
    enhancementClient: EnhancementClient,
    executor: Executor
) {
    // [START android_media_ai_enhancement_surface_snapshot]
    // Provisions input Surface (for example, ImageReader) and output Surface (for
    // example, SurfaceView)
    val inputSurface: Surface = imageReader.surface
    val outputSurface: Surface = surfaceView.holder.surface
    // 1. Configure parameters for SURFACE mode
    val surfaceOptions = EnhancementOptions(
        imageReader.width,
        imageReader.height,
        EnhancementMode.SURFACE,
        enableTonemap = true,
        enableDeblurDenoise = true,
        enableFaceDetection = false
    ).also {
        // 2. Bind hardware surfaces
        it.setInputSurface(inputSurface)
        it.setOutputSurface(outputSurface)
    }

    // 3. Create the session to process the hardware frame
    val singleFrameSession = enhancementClient.createSessionAsync(surfaceOptions, executor)
    // The API processes the single frame. Upon completion, release the session.
    singleFrameSession.release()
    // [END android_media_ai_enhancement_surface_snapshot]
}
