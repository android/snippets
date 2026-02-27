/*
 * Copyright 2025 The Android Open Source Project
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

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CaptureRequest
import android.util.Log
import android.util.Range
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.CaptureRequestOptions
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.xr.projected.ProjectedContext
import androidx.xr.projected.experimental.ExperimentalProjectedApi

private const val TAG = "ProjectedHardware"

/**
 * Demonstrates how to obtain a context for the projected device (AI glasses)
 * from the host device (phone).
 */
// [START androidxr_projected_context_get_projected]
@OptIn(ExperimentalProjectedApi::class)
private fun getGlassesContext(context: Context): Context? {
    return try {
        // From a phone Activity or Service, get a context for the AI glasses.
        ProjectedContext.createProjectedDeviceContext(context)
    } catch (e: IllegalStateException) {
        Log.e(TAG, "Failed to create projected device context", e)
        null
    }
}
// [END androidxr_projected_context_get_projected]

/**
 * Demonstrates how to obtain a context for the host device (phone)
 * from the projected device (AI glasses).
 */
// [START androidxr_projected_context_get_host]
@OptIn(ExperimentalProjectedApi::class)
private fun getPhoneContext(activity: ComponentActivity): Context? {
    return try {
        // From an AI glasses Activity, get a context for the phone.
        ProjectedContext.createHostDeviceContext(activity)
    } catch (e: IllegalStateException) {
        Log.e(TAG, "Failed to create host device context", e)
        null
    }
}
// [END androidxr_projected_context_get_host]

/**
 * Demonstrates how to capture an image using the AI glasses' camera.
 */
@androidx.annotation.OptIn(ExperimentalCamera2Interop::class)
@OptIn(ExperimentalProjectedApi::class)
// [START androidxr_projected_camera_capture]
private fun startCameraOnGlasses(activity: ComponentActivity) {
    // 1. Get the CameraProvider using the projected context.
    // When using the projected context, DEFAULT_BACK_CAMERA maps to the AI glasses' camera.
    val projectedContext = try {
        ProjectedContext.createProjectedDeviceContext(activity)
    } catch (e: IllegalStateException) {
        Log.e(TAG, "AI Glasses context could not be created", e)
        return
    }

    val cameraProviderFuture = ProcessCameraProvider.getInstance(projectedContext)

    cameraProviderFuture.addListener({
        val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        // 2. Check for the presence of a camera.
        if (!cameraProvider.hasCamera(cameraSelector)) {
            Log.w(TAG, "The selected camera is not available.")
            return@addListener
        }

        // 3. Query supported streaming resolutions using Camera2 Interop.
        val cameraInfo = cameraProvider.getCameraInfo(cameraSelector)
        val camera2CameraInfo = Camera2CameraInfo.from(cameraInfo)
        val cameraCharacteristics = camera2CameraInfo.getCameraCharacteristic(
            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
        )

        // 4. Define the resolution strategy.
        val targetResolution = Size(1920, 1080)
        val resolutionStrategy = ResolutionStrategy(
            targetResolution,
            ResolutionStrategy.FALLBACK_RULE_CLOSEST_LOWER
        )
        val resolutionSelector = ResolutionSelector.Builder()
            .setResolutionStrategy(resolutionStrategy)
            .build()

        // 5. If you have other continuous use cases bound, such as Preview or ImageAnalysis,
        // you can use  Camera2 Interop's CaptureRequestOptions to set the FPS
        val fpsRange = Range(30, 60)
        val captureRequestOptions = CaptureRequestOptions.Builder()
            .setCaptureRequestOption(CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE, fpsRange)
            .build()

        // 6. Initialize the ImageCapture use case with options.
        val imageCapture = ImageCapture.Builder()
            // Optional: Configure resolution, format, etc.
            .setResolutionSelector(resolutionSelector)
            .build()

        try {
            // Unbind use cases before rebinding.
            cameraProvider.unbindAll()

            // Bind use cases to camera using the Activity as the LifecycleOwner.
            cameraProvider.bindToLifecycle(
                activity,
                cameraSelector,
                imageCapture
            )
        } catch (exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
    }, ContextCompat.getMainExecutor(activity))
}
// [END androidxr_projected_camera_capture]
