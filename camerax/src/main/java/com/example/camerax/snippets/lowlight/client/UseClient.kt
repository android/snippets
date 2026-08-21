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

package com.example.camerax.snippets.lowlight.client

import android.content.Context
import android.hardware.camera2.CameraAccessException
import android.util.Log
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private class UseClientActivity(
    private val context: Context,
    private val cameraId: String,
    private val coroutineScope: CoroutineScope
) {
    private val TAG = "UseClientActivity"
    private var lowLightBoostClient: LowLightBoostClient = LowLightBoost.getClient(context)

    private fun snippetCreate() {
        // [START android_camera_lowlight_boost_client_create]
        val lowLightBoostClient = LowLightBoost.getClient(context)
        // [END android_camera_lowlight_boost_client_create]
    }

    private fun snippetCheckSupported() {
        coroutineScope.apply {
            // [START android_camera_lowlight_boost_client_check_supported]
            launch {
                try {
                    // Await the result of the Task in a non-blocking way
                    val isSupported: Boolean = lowLightBoostClient
                        .isCameraSupported(cameraId).await()
                    Log.d(TAG, "isCameraSupported: $isSupported")
                    if (isSupported) {
                        // Create the low light boost session here
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "isCameraSupported failed", e)
                }
            }
            // [END android_camera_lowlight_boost_client_check_supported]
        }
    }

    private fun snippetCheckInstalled() {
        coroutineScope.apply {
            // [START android_camera_lowlight_boost_client_check_installed]
            // Handle the Google Play services Task API with Kotlin coroutines
            // (kotlinx-coroutines-play-services)
            launch {
                try {
                    val isInstalled: Boolean = lowLightBoostClient
                        .isModuleInstalled(context).await()

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
            // [END android_camera_lowlight_boost_client_check_installed]
        }
    }

    // [START android_camera_lowlight_boost_client_install_request]
    private suspend fun launchInstallRequest() {
        Log.v(TAG, "Launching install request")

        try {
            // Check if this device can support Google LLB.
            val isDeviceSupported: Boolean = lowLightBoostClient
                .isDeviceSupported(context).await()

            if (isDeviceSupported) {
                Log.d(TAG, "Device is supported")
                // Show download indicator, if needed.

                try {
                    val isInstallSuccessful: Boolean = lowLightBoostClient
                        .installModule(
                            context,
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
    // [END android_camera_lowlight_boost_client_install_request]

    // [START android_camera_lowlight_boost_client_create_install_callback]
    private fun createInstallStatusCallback(): LowLightBoostClient.InstallStatusCallback =
        object : LowLightBoostClient.InstallStatusCallback() {
            override fun onDownloadPending() {
                Log.d(TAG, "onDownloadPending")
                // Code here...
            }

            override fun onDownloadStart() {
                Log.d(TAG, "onDownloadStart")
                // Code here...
            }

            // other overrides here...
        }
    // [END android_camera_lowlight_boost_client_create_install_callback]

    private fun openCamera(id: String = cameraId) {}
}

private object LowLightBoost {
    fun getClient(context: Context): LowLightBoostClient = LowLightBoostClient()
}

private class LowLightBoostClient {
    fun isCameraSupported(cameraId: String): Task<Boolean> = Tasks.forResult(true)
    fun isModuleInstalled(context: Context): Task<Boolean> = Tasks.forResult(true)
    fun isDeviceSupported(context: Context): Task<Boolean> = Tasks.forResult(true)
    fun installModule(context: Context, callback: InstallStatusCallback): Task<Boolean> = Tasks.forResult(true)

    abstract class InstallStatusCallback {
        open fun onDownloadPending() {}
        open fun onDownloadStart() {}
    }
}
