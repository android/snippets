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

import android.annotation.SuppressLint
import android.os.Build
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.camera.camera2.Camera2Config
import androidx.camera.core.CameraIdentifier
import androidx.camera.core.CameraPresenceListener
import androidx.camera.core.CameraXConfig
import androidx.camera.lifecycle.ExperimentalCameraProviderConfiguration
import androidx.camera.lifecycle.LifecycleCameraProvider
import androidx.core.content.ContextCompat
import java.util.concurrent.Executor

private const val TAG = "ProjectedCameraSetup"

/**
 * Demonstrates how to set up the audio and display glasses' camera.
 */
// [START androidxr_projected_camera_setup]
@OptIn(ExperimentalCameraProviderConfiguration::class)
@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@SuppressLint("RestrictedApi")
fun setUpGlassesCamera(activity: ComponentActivity) {
    monitorProjectedDeviceConnectivity(activity) { projectedContext ->
        val cameraConfig = CameraXConfig.Builder
            .fromConfig(Camera2Config.defaultConfig())
            .setRepeatingStreamForced(false)
            .build()

        val cameraProvider = try {
            // Acquire CameraX provider specifically bound to the projected context.
            // If the device disconnects while suspending, collectLatest cancels this coroutine.
            LifecycleCameraProvider.createInstance(projectedContext, cameraConfig)
        } catch (e: IllegalStateException) {
            Log.e(TAG, "Failed to initialize projected camera services", e)
            return@monitorProjectedDeviceConnectivity null
        }

        // Listen for camera additions/removals.
        val presenceListener = registerPresenceListener(
            cameraProvider = cameraProvider,
            executor = ContextCompat.getMainExecutor(activity),
            onCameraAdded = { cameraIdentifier ->
                bindGlassesImageCapture(cameraProvider, cameraIdentifier, activity)
            }
        )

        // Return a clean-up closure invoked automatically by monitorProjectedDeviceConnectivity
        // on disconnect, coroutine cancellation, or Activity destruction.
        return@monitorProjectedDeviceConnectivity {
            Log.i(TAG, "Projected camera setup scope ended. Tearing down resources...")
            try {
                cameraProvider.removeCameraPresenceListener(presenceListener)
            } finally {
                cameraProvider.unbindAll()
            }
        }
    }
}
// [END androidxr_projected_camera_setup]

/**
 * Registers a [CameraPresenceListener] to react when the projected camera is added or removed.
 *
 * Avoid opening the camera immediately when the glasses connect, asynchronous setup between
 * the glasses and host must finish before the OS finishes recreating the glasses' camera.
 */
// [START androidxr_projected_camera_availability]
@OptIn(ExperimentalCameraProviderConfiguration::class)
fun registerPresenceListener(
    cameraProvider: LifecycleCameraProvider,
    executor: Executor,
    onCameraAdded: (cameraIdentifier: CameraIdentifier) -> Unit
): CameraPresenceListener {
    val listener = object : CameraPresenceListener {
        override fun onCamerasAdded(cameraIdentifiers: Set<CameraIdentifier>) {
            // Fires after availableCameraInfos has been updated with the newly added cameras.
            val cameraIdentifier = cameraIdentifiers.firstOrNull() ?: return
            Log.i(TAG, "Glasses camera added ($cameraIdentifier). Binding use case...")
            onCameraAdded(cameraIdentifier)
        }

        override fun onCamerasRemoved(cameraIdentifiers: Set<CameraIdentifier>) {
            Log.i(TAG, "Glasses camera removed ($cameraIdentifiers). Releasing resources...")
            // Tracks camera presence rather than in-use state, so this only fires on removal.
            cameraProvider.unbindAll()
        }
    }

    cameraProvider.addCameraPresenceListener(executor, listener)
    return listener
}
// [END androidxr_projected_camera_availability]
