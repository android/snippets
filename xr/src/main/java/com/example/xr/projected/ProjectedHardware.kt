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
import androidx.camera.core.CameraIdentifier
import androidx.camera.core.CameraInfoUnavailableException
import androidx.camera.core.ImageCapture
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ExperimentalCameraProviderConfiguration
import androidx.camera.lifecycle.LifecycleCameraProvider
import androidx.lifecycle.LifecycleOwner

private const val TAG = "ProjectedHardware"

// [START androidxr_projected_camera_capture]
/**
 * Selects the projected camera matching [cameraIdentifier] from
 * [LifecycleCameraProvider.availableCameraInfos], verifies availability, and binds an
 * [ImageCapture] use case targeting an example 16:9 resolution to [lifecycleOwner].
 *
 * Note: Depending on your application's needs, [ImageCapture] can be swapped out for other
 * standard CameraX use cases:
 * - Use [ImageAnalysis](https://developer.android.com/reference/androidx/camera/core/ImageAnalysis) for real-time streaming workloads (e.g., OCR, AI vision, or VPS).
 * - Use [`VideoCapture`](https://developer.android.com/reference/androidx/camera/video/VideoCapture) for video recording pipelines.
 */
@OptIn(ExperimentalCameraProviderConfiguration::class)
fun bindGlassesImageCapture(
    cameraProvider: LifecycleCameraProvider,
    cameraIdentifier: CameraIdentifier,
    lifecycleOwner: LifecycleOwner,
    preferredResolution: Size = Size(1920, 1080)
): ImageCapture? {
    return try {
        // [START androidxr_projected_camera_dynamic_id]
        // Discover available cameras dynamically via `availableCameraInfos` and match by
        // CameraIdentifier because the projected camera's ID may change when recreated.
        val cameraInfo = cameraProvider.availableCameraInfos.firstOrNull { info ->
            cameraIdentifier.isOf(info)
        }

        // Pre-binding check: Verify the selected camera is registered and available on the device.
        // The projected camera may be unavailable (for example, if the user took off the glasses).
        if (cameraInfo == null) {
            Log.w(TAG, "Glasses camera ($cameraIdentifier) is not currently available.")
            return null
        }
        val cameraSelector = cameraInfo.cameraSelector
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

        // Unbind any previously bound use cases before rebinding to the lifecycle owner.
        cameraProvider.unbindAll()
        cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageCapture)
        imageCapture
    } catch (e: CameraInfoUnavailableException) {
        Log.e(TAG, "Unable to query camera availability", e)
        null
    } catch (e: IllegalArgumentException) {
        Log.e(TAG, "Failed to bind ImageCapture use case to lifecycle", e)
        null
    }
}
// [END androidxr_projected_camera_capture]
