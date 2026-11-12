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

package com.example.camera.snippets.lowlight

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.android.gms.cameralowlight.LowLightBoost
import com.google.android.gms.cameralowlight.LowLightBoostClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private const val TAG = "LowLightBoostGpClient"

@RequiresApi(Build.VERSION_CODES.R)
private fun createLowLightBoostClient(context: Context) {
    // [START android_camera_lowlight_gp_client_create]
    val lowLightBoostClient = LowLightBoost.getClient(context)
    // [END android_camera_lowlight_gp_client_create]
}

@RequiresApi(Build.VERSION_CODES.R)
private class LowLightBoostGpClientSnippets(
    private val context: Context,
    private val cameraId: String
) {
    private val lowLightBoostClient: LowLightBoostClient = LowLightBoost.getClient(context)

    fun CoroutineScope.checkCameraSupported() {
        // [START android_camera_lowlight_gp_client_check_camera_supported]
        launch {
            try {
                // Await the result of the Task in a non-blocking way.
                val isSupported: Boolean = lowLightBoostClient
                    .isCameraSupported(cameraId).await()
                Log.d(TAG, "isCameraSupported: $isSupported")
                if (isSupported) {
                    // Create the low light boost session here.
                }
            } catch (e: Exception) {
                Log.e(TAG, "isCameraSupported failed", e)
            }
        }
        // [END android_camera_lowlight_gp_client_check_camera_supported]
    }

    fun CoroutineScope.checkModuleInstalled() {
        // [START android_camera_lowlight_gp_client_check_module_installed]
        // Handle the Google Play services Task API with Kotlin coroutines
        // (kotlinx-coroutines-play-services).
        launch {
            try {
                val isInstalled: Boolean = lowLightBoostClient
                    .isModuleInstalled().await()

                if (isInstalled) {
                    Log.d(TAG, "Module is installed")
                    try {
                        openCamera(cameraId)
                    } catch (e: CameraAccessException) {
                        Log.e(TAG, "Failed to open camera", e)
                    }
                } else {
                    Log.d(TAG, "Module is not installed")
                    launchInstallRequest()
                }
            } catch (e: Exception) {
                Log.e(TAG, "Failed to check module availability", e)
            }
        }
        // [END android_camera_lowlight_gp_client_check_module_installed]
    }

    // [START android_camera_lowlight_gp_client_install_module]
    private suspend fun launchInstallRequest() {
        Log.v(TAG, "Launching install request")

        try {
            // Check if this device can support Google LLB.
            val isDeviceSupported: Boolean = lowLightBoostClient
                .isDeviceSupported().await()

            if (isDeviceSupported) {
                Log.d(TAG, "Device is supported")
                // Show download indicator, if needed.

                try {
                    val isInstallSuccessful: Boolean = lowLightBoostClient
                        .installModule(
                            createInstallStatusCallback()
                        ).await()

                    if (isInstallSuccessful) {
                        Log.d(TAG, "Module installed")
                        // Hide download indicator, if needed.
                        try {
                            openCamera()
                        } catch (e: CameraAccessException) {
                            Log.e(TAG, "Failed to open camera", e)
                        }
                    } else {
                        Log.d(TAG, "Module install failed")
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "An error occurred installing the module:", e)
                }
            } else {
                Log.d(TAG, "Device is not supported")
            }
        } catch (e: Exception) {
            Log.e(TAG, "An error occurred checking device support:", e)
        }
    }
    // [END android_camera_lowlight_gp_client_install_module]

    // [START android_camera_lowlight_gp_client_install_status_callback]
    private fun createInstallStatusCallback(): LowLightBoostClient.InstallStatusCallback =
        object : LowLightBoostClient.InstallStatusCallback {
            override fun onDownloadPending() {
                Log.d(TAG, "onDownloadPending")
                // Code here...
            }

            override fun onDownloadStart() {
                Log.d(TAG, "onDownloadStart")
                // Code here...
            }

            // other overrides here...
            // [START_EXCLUDE]
            override fun onError(description: String) {}

            override fun onCancelled() {}

            override fun onDownloadProgressUpdate(progress: Int) {}

            override fun onDownloadPaused() {}

            override fun onDownloadComplete() {}

            override fun onInstalled() {}
            // [END_EXCLUDE]
        }
    // [END android_camera_lowlight_gp_client_install_status_callback]

    private fun openCamera(cameraId: String) = Unit

    private fun openCamera() = Unit
}
