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

package com.example.xr.projected

import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

private const val TAG = "ProjectedCameraSetup"

/**
 * Demonstrates how to set up the audio and display glasses' camera.
 */
// [START androidxr_projected_camera_setup]
@RequiresApi(Build.VERSION_CODES.BAKLAVA)
fun setUpGlassesCamera(activity: ComponentActivity) {
    monitorProjectedDeviceConnectivity(activity) { projectedContext ->
        val cameraManager = projectedContext.getSystemService(CameraManager::class.java)
            ?: run {
                Log.e(TAG, "CameraManager not available on projected context")
                return@monitorProjectedDeviceConnectivity null
            }

        val cameraProvider = try {
            // Acquire CameraX provider specifically bound to the projected context.
            // If the device disconnects while suspending, collectLatest cancels this coroutine.
            ProcessCameraProvider.awaitInstance(projectedContext)
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Failed to initialize projected camera services", e)
            return@monitorProjectedDeviceConnectivity null
        }

        // Set up the listener to react to camera availability changes asynchronously.
        val availabilityCallback = registerAvailabilityListener(
            cameraManager = cameraManager,
            cameraProvider = cameraProvider,
            executor = ContextCompat.getMainExecutor(activity),
            onCameraAvailable = { cameraId ->
                // Delegate camera selection, resolution querying, and use-case binding.
                bindGlassesImageCapture(cameraProvider, cameraId, activity)
            }
        )

        // Return a clean-up closure invoked automatically by monitorProjectedDeviceConnectivity
        // on disconnect, coroutine cancellation, or Activity destruction.
        return@monitorProjectedDeviceConnectivity {
            Log.i(TAG, "Projected camera setup scope ended. Tearing down resources...")
            try {
                cameraManager.unregisterAvailabilityCallback(availabilityCallback)
            } finally {
                cameraProvider.unbindAll()
            }
        }
    }
}
// [END androidxr_projected_camera_setup]

/**
 * Registers a [CameraManager.AvailabilityCallback] to react when the projected camera becomes
 * available or unavailable (for example, when the user takes off the glasses).
 *
 * Avoid opening the camera immediately when the glasses connect or are put on—asynchronous setup
 * between the glasses and host must finish before the OS finishes recreating the glasses' camera.
 */
// [START androidxr_projected_camera_availability]
fun registerAvailabilityListener(
    cameraManager: CameraManager,
    cameraProvider: ProcessCameraProvider,
    executor: Executor,
    onCameraAvailable: (cameraId: String) -> Unit
): CameraManager.AvailabilityCallback {
    val callback = object : CameraManager.AvailabilityCallback() {
        override fun onCameraAvailable(cameraId: String) {
            super.onCameraAvailable(cameraId)
            Log.i(TAG, "Glasses camera available (ID: $cameraId). Binding use case...")
            // Fires only after the OS finishes re-registering the glasses' camera.
            // Pass the fresh cameraId directly—never cache it, as it may change when recreated.
            onCameraAvailable(cameraId)
        }

        override fun onCameraUnavailable(cameraId: String) {
            super.onCameraUnavailable(cameraId)
            Log.i(TAG, "Glasses camera unavailable (ID: $cameraId). Releasing resources...")
            // Gracefully release the pipeline when the projected camera becomes unavailable.
            cameraProvider.unbindAll()
        }
    }

    cameraManager.registerAvailabilityCallback(executor, callback)
    return callback
}
// [END androidxr_projected_camera_availability]
