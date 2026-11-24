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
import android.graphics.Rect
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CaptureRequest
import android.util.Range
import android.util.Size
import androidx.annotation.OptIn
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraIdentifier
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.resolutionselector.AspectRatioStrategy
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ExperimentalCameraProviderConfiguration
import androidx.camera.lifecycle.LifecycleCameraProvider
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executor
import kotlin.math.abs
import kotlin.math.roundToInt

// [START androidxr_projected_camera_center_crop_rect]
private const val DEFAULT_MAX_DIGITAL_ZOOM = 4.0f

fun computeCenterCropRect(
    activeArray: Rect,
    zoomFactor: Float,
    reportedMaxDigitalZoom: Float = DEFAULT_MAX_DIGITAL_ZOOM
): Rect {
    val maxZoom = if (reportedMaxDigitalZoom > 1.0f) {
        reportedMaxDigitalZoom
    } else {
        DEFAULT_MAX_DIGITAL_ZOOM
    }
    val effectiveZoom = zoomFactor.coerceIn(1.0f, maxZoom)

    // Align the active array bounds to even integers for YUV_420_888 2x2 chroma planes.
    val baseLeft = activeArray.left and 1.inv()
    val baseTop = activeArray.top and 1.inv()
    val activeWidth = activeArray.width() and 1.inv()
    val activeHeight = activeArray.height() and 1.inv()
    if (activeWidth <= 0 || activeHeight <= 0) return Rect(activeArray)

    val minWidth = (activeWidth / maxZoom).roundToInt().coerceAtLeast(2)
    val minHeight = (activeHeight / maxZoom).roundToInt().coerceAtLeast(2)
    val targetWidth = (activeWidth / effectiveZoom).roundToInt()
        .coerceIn(minWidth, activeWidth)
    val targetHeight = (activeHeight / effectiveZoom).roundToInt()
        .coerceIn(minHeight, activeHeight)

    // Apply even-aligned insets on opposite edges so the crop stays centered and even-sized.
    val horizontalInset = ((activeWidth - targetWidth) / 2) and 1.inv()
    val verticalInset = ((activeHeight - targetHeight) / 2) and 1.inv()

    return Rect(
        baseLeft + horizontalInset,
        baseTop + verticalInset,
        baseLeft + activeWidth - horizontalInset,
        baseTop + activeHeight - verticalInset
    )
}
// [END androidxr_projected_camera_center_crop_rect]

// [START androidxr_projected_camera_bind_cropped_analysis]
@OptIn(ExperimentalCamera2Interop::class, ExperimentalCameraProviderConfiguration::class)
fun bindCroppedHighResAnalysis(
    cameraProvider: LifecycleCameraProvider,
    cameraIdentifier: CameraIdentifier,
    lifecycleOwner: LifecycleOwner,
    analyzerExecutor: Executor,
    analyzer: ImageAnalysis.Analyzer,
    zoomFactor: Float = 1.4f,
    targetFps: Int = 1
): ImageAnalysis? {
    val cameraInfo = cameraProvider.availableCameraInfos.firstOrNull { info ->
        cameraIdentifier.isOf(info)
    } ?: return null

    val camera2Info = Camera2CameraInfo.from(cameraInfo)
    val activeArray = camera2Info.getCameraCharacteristic(
        CameraCharacteristics.SENSOR_INFO_ACTIVE_ARRAY_SIZE
    )
    val reportedMaxZoom = camera2Info.getCameraCharacteristic(
        CameraCharacteristics.SCALER_AVAILABLE_MAX_DIGITAL_ZOOM
    ) ?: DEFAULT_MAX_DIGITAL_ZOOM

    // Query supported YUV_420_888 sizes and per-size maximum FPS.
    val maxFpsBySize = getMaxFpsBySize(cameraInfo, ImageFormat.YUV_420_888)
    val targetSize = maxFpsBySize.keys.maxByOrNull { it.width * it.height }
        ?: Size(1920, 1080)
    val maxSupportedFps = maxFpsBySize[targetSize] ?: 1
    val minFps = minOf(targetFps, maxSupportedFps).coerceAtLeast(1)
    val maxFps = minFps * 2

    val isSixteenByNine = targetSize.height > 0 &&
        abs(targetSize.width.toFloat() / targetSize.height - 16f / 9f) < 0.05f
    val aspectRatioStrategy = if (isSixteenByNine) {
        AspectRatioStrategy.RATIO_16_9_FALLBACK_AUTO_STRATEGY
    } else {
        AspectRatioStrategy.RATIO_4_3_FALLBACK_AUTO_STRATEGY
    }

    val resolutionSelector = ResolutionSelector.Builder()
        .setAspectRatioStrategy(aspectRatioStrategy)
        .setResolutionStrategy(
            ResolutionStrategy(
                targetSize,
                ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
            )
        )
        .build()

    val analysisBuilder = ImageAnalysis.Builder()
        .setResolutionSelector(resolutionSelector)
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_YUV_420_888)

    Camera2Interop.Extender(analysisBuilder).apply {
        if (activeArray != null && activeArray.width() > 0 && activeArray.height() > 0) {
            val cropRect = computeCenterCropRect(activeArray, zoomFactor, reportedMaxZoom)
            setCaptureRequestOption(CaptureRequest.SCALER_CROP_REGION, cropRect)
            setCaptureRequestOption(CaptureRequest.CONTROL_ZOOM_RATIO, 1.0f)
        }
        setCaptureRequestOption(
            CaptureRequest.CONTROL_AE_TARGET_FPS_RANGE,
            Range(minFps, maxFps)
        )
    }

    val imageAnalysis = analysisBuilder.build().apply {
        setAnalyzer(analyzerExecutor, analyzer)
    }

    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraInfo.cameraSelector,
        imageAnalysis
    )
    return imageAnalysis
}
// [END androidxr_projected_camera_bind_cropped_analysis]
