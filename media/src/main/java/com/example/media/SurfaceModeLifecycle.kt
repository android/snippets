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
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.media.effect.enhancement.Enhancement
import com.google.android.gms.media.effect.enhancement.EnhancementClient
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private object SurfaceModeLifecycleSnippet {
    // [START android_media_ai_enhancement_surface_initialize_engine]
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
                    // Handle potential errors during session creation or image
                    // processing.
                    handleInitializationError(e)
                }
            }
        }
        // [START_EXCLUDE silent]
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
        // [END_EXCLUDE]
    }
    // [END android_media_ai_enhancement_surface_initialize_engine]
}
