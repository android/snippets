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

import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CaptureRequest
import android.os.Build
import android.util.Range
import android.util.Size
import androidx.annotation.RequiresApi
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraInfo
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy

/**
 * Query the maximum frame rate for each resolution using minimum frame durations.
 */
// [START androidxr_projected_camera_max_fps]
@androidx.annotation.OptIn(ExperimentalCamera2Interop::class)
fun getMaxFpsBySize(cameraInfo: CameraInfo, format: Int): Map<Size, Int> {
    val map = Camera2CameraInfo.from(cameraInfo).getCameraCharacteristic(
        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
    ) ?: return emptyMap()

    return map.getOutputSizes(format).associateWith { size ->
        val minFrameDurationNs = map.getOutputMinFrameDuration(format, size)
        if (minFrameDurationNs > 0) (1_000_000_000L / minFrameDurationNs).toInt() else 0
    }
}
// [END androidxr_projected_camera_max_fps]

/**
 * Configure the camera stream frame rate dynamically using ranges for audio and display glasses.
 */
@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@androidx.annotation.OptIn(ExperimentalCamera2Interop::class)
fun setupCameraStreamingFps(cameraInfo: CameraInfo, size: Size, desiredFps: Int): ImageAnalysis {
    // Calculate target min and max FPS using dynamic resolution checks
    // [START androidxr_projected_camera_target_fps]
    val maxFpsBySize = getMaxFpsBySize(cameraInfo, ImageFormat.YUV_420_888)
    val maxSupportedFps = maxFpsBySize[size] ?: return ImageAnalysis.Builder().build()

    // minFps is the target streaming frame rate and should be at most maxSupportedFps.
    val minFps = minOf(desiredFps, maxSupportedFps)
    // Set the upper bound to 2x minFps so the stream can catch up if frames are delayed.
    val maxFps = minFps * 2
    // [END androidxr_projected_camera_target_fps]

    // [START androidxr_projected_camera_resolution_selector]
    // Configure Resolution Strategy for the stream
    val resolutionStrategy = ResolutionStrategy(
        size,
        ResolutionStrategy.FALLBACK_RULE_CLOSEST_LOWER
    )
    val resolutionSelector = ResolutionSelector.Builder()
        .setAspectRatioStrategy(AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY)
        .setResolutionStrategy(resolutionStrategy)
        .build()
    // [END androidxr_projected_camera_resolution_selector]

    // [START androidxr_projected_camera_set_fps_analysis]
    // Create and return the ImageAnalysis builder with proper frame rate ranges
    // ImageAnalysis: OCR, AI streaming, and VPS
    val analysisBuilder = ImageAnalysis.Builder()
        .setResolutionSelector(resolutionSelector)

    Camera2Interop.Extender(analysisBuilder)
        .setCaptureRequestOption(
            CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE,
            Range(minFps, maxFps)
        )
    return analysisBuilder.build()
    // [END androidxr_projected_camera_set_fps_analysis]
}
