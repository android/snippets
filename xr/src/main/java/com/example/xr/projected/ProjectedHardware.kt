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

import android.util.Log
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.LifecycleOwner

private const val TAG = "ProjecteHardware"

// [START androidxr_projected_camera_capture]
/**
 * Filters [CameraSelector.DEFAULT_BACK_CAMERA] by dynamic [cameraId], verifies availability,
 * and binds an [ImageCapture] use case targeting a 16:9 resolution to [lifecycleOwner].
 *
 * Note: Depending on your application's needs, [ImageCapture] can be swapped out for other
 * standard CameraX use cases:
 * - Use [ImageAnalysis] for real-time streaming workloads (e.g., OCR, AI vision, or VPS).
 * - Use [VideoCapture] for video recording pipelines.
 */
@OptIn(ExperimentalCamera2Interop::class)
fun bindGlassesImageCapture(
    cameraProvider: ProcessCameraProvider,
    cameraId: String,
    lifecycleOwner: LifecycleOwner,
    preferredResolution: Size = Size(1920, 1080)
): ImageCapture? {
    // [START androidxr_projected_camera_dynamic_id]
    // Within a projected context, the glasses' primary outward-facing camera maps to
    // `DEFAULT_BACK_CAMERA`. Narrow it down by the dynamic `cameraId` because the projected
    // camera's ID may change when recreated.
    val cameraSelector = CameraSelector.Builder()
        .addCameraFilter { cameraInfos ->
            // Filter the `DEFAULT_BACK_CAMERA` candidates by our target dynamic ID.
            CameraSelector.DEFAULT_BACK_CAMERA.filter(cameraInfos).filter { info ->
                Camera2CameraInfo.from(info).cameraId == cameraId
            }
        }
        .build()

    // Pre-binding check: Verify the selected camera is registered and available on the device.
    // The projected camera may be unavailable (for example, if the user took off the glasses).
    if (!cameraProvider.hasCamera(cameraSelector)) {
        Log.w(TAG, "Glasses camera (ID: $cameraId) is not currently available.")
        return null
    }
    // [END androidxr_projected_camera_dynamic_id]

    val resolutionSelector = ResolutionSelector.Builder()
        .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
        .setResolutionStrategy(
            ResolutionStrategy(
                preferredResolution,
                ResolutionStrategy.FALLBACK_RULE_CLOSEST_LOWER
            )
        )
        .build()

    // Initialize the ImageCapture use case.
    val imageCapture = ImageCapture.Builder()
        .setResolutionSelector(resolutionSelector)
        .build()

    return try {
        // Unbind any previously bound use cases before rebinding to the lifecycle owner.
        cameraProvider.unbindAll()

        // Bind the configured use case. You can swap `imageCapture` out for an `ImageAnalysis`
        // (e.g., from `setupCameraStreamingFps`) or `VideoCapture` instance here to match your use case.
        // `bindToLifecycle()` can throw IllegalArgumentException if use cases
        // are incompatible, or IllegalStateException if the LifecycleOwner is destroyed.
        cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            imageCapture
        )
        Log.i(TAG, "ImageCapture successfully bound for camera ID $cameraId")
        imageCapture
    } catch (exc: IllegalArgumentException) {
        Log.e(TAG, "Invalid camera configuration or use case combination for ID $cameraId", exc)
        null
    } catch (exc: IllegalStateException) {
        Log.e(TAG, "Camera is already bound to another lifecycle or closed", exc)
        null
    }
}
// [END androidxr_projected_camera_capture]
