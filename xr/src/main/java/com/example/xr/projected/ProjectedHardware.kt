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
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CaptureRequest
import android.os.Build
import android.util.Log
import android.util.Range
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.xr.projected.ProjectedContext
import androidx.xr.projected.experimental.ExperimentalProjectedApi
import kotlin.math.abs
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val TAG = "ProjectedHardware"

/**
 * Demonstrates how to obtain a context for the projected device (audio and display glasses)
 * from the host device (phone) and how to monitor the projected device's
 * connectivity to manage resources.
 */
// [START androidxr_projected_context_get_projected]
@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@OptIn(ExperimentalProjectedApi::class, ExperimentalCoroutinesApi::class)
private fun monitorProjectedConnectivity(activity: ComponentActivity) {
    activity.lifecycleScope.launch {
        // Before creating a projected context, check to see if the projected device is connected.
        // While this method returns true, the projected context remains valid.
        ProjectedContext.isProjectedDeviceConnected(activity, coroutineContext)
            .collectLatest { isConnected ->
                if (isConnected) {
                    // From a phone Activity or Service, get a context for the audio and display glasses.
                    // Re-initialize on reconnect: Obtain another context instance.
                    val projectedContext = try {
                        ProjectedContext.createProjectedDeviceContext(activity)
                    } catch (e: IllegalStateException) {
                        Log.e(TAG, "Failed to create projected context", e)
                        return@collectLatest
                    }

                    // Use the projectedContext to initialize system services (e.g., CameraManager).
                    Log.i(TAG, "Projected device connected. Initializing hardware...")
                } else {
                    // The projected context is destroyed when the device disconnects.
                    // Clean up on disconnect: Listen for 'false' and release resources.
                    Log.i(TAG, "Projected device disconnected. Cleaning up hardware resources...")
                }
            }
    }
}
// [END androidxr_projected_context_get_projected]

/**
 * Demonstrates how to obtain a context for the host device (phone)
 * from the projected device (audio and display glasses).
 */
// [START androidxr_projected_context_get_host]
@OptIn(ExperimentalProjectedApi::class)
private fun getPhoneContext(activity: ComponentActivity): Context? {
    return try {
        // From a projected Activity, get a context for the phone.
        ProjectedContext.createHostDeviceContext(activity)
    } catch (e: IllegalStateException) {
        Log.e(TAG, "Failed to create host device context", e)
        null
    }
}
// [END androidxr_projected_context_get_host]

/**
 * Demonstrates how to set up the audio and display glasses' camera for an image capture use case,
 * utilizing dynamically queried and verified resolutions.
 */
@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@androidx.annotation.OptIn(ExperimentalCamera2Interop::class)
@OptIn(ExperimentalProjectedApi::class, ExperimentalCoroutinesApi::class)
// [START androidxr_projected_camera_capture]
private fun setUpGlassesCamera(activity: ComponentActivity) {
    activity.lifecycleScope.launch {
        // Before creating a projected context, check to see if the projected device is connected.
        ProjectedContext.isProjectedDeviceConnected(activity, coroutineContext)
            .collectLatest { isConnected ->
                if (isConnected) {
                    // 1. Get the CameraProvider using the projected context.
                    // When using the projected context, DEFAULT_BACK_CAMERA maps to the audio and display glasses' camera.
                    val projectedContext = try {
                        ProjectedContext.createProjectedDeviceContext(activity)
                    } catch (e: IllegalStateException) {
                        Log.e(TAG, "Projected context could not be created", e)
                        return@collectLatest
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

                        // 3. Query supported streaming resolutions dynamically using Camera2 Interop.
                        val cameraInfo = cameraProvider.getCameraInfo(cameraSelector)
                        val map = Camera2CameraInfo.from(cameraInfo).getCameraCharacteristic(
                            CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
                        )
                        val supportedResolutions = map?.getOutputSizes(ImageFormat.JPEG)?.toList() ?: emptyList()
                        Log.i(TAG, "Supported resolutions for JPEG: ${supportedResolutions.joinToString()}")

                        // 4. Dynamically select the best target resolution from the queried list.
                        val preferredResolution = Size(1920, 1080)
                        val dynamicTargetResolution = when {
                            // Case A: The preferred resolution is explicitly supported by the glasses.
                            supportedResolutions.contains(preferredResolution) -> preferredResolution

                            // Case B: Find the highest supported 16:9 resolution as a fallback.
                            else -> {
                                val targetAspectRatio = 16.0 / 9.0
                                val tolerance = 0.01
                                supportedResolutions
                                    .filter { abs((it.width.toDouble() / it.height) - targetAspectRatio) < tolerance }
                                    .maxByOrNull { it.width * it.height }
                                    // Case C: Absolute fallback if no 16:9 resolutions are found.
                                    ?: supportedResolutions.firstOrNull()
                                    ?: preferredResolution
                            }
                        }
                        Log.i(TAG, "Selected target resolution: $dynamicTargetResolution")

                        val resolutionStrategy = ResolutionStrategy(
                            dynamicTargetResolution,
                            ResolutionStrategy.FALLBACK_RULE_CLOSEST_LOWER
                        )
                        val resolutionSelector = ResolutionSelector.Builder()
                            .setResolutionStrategy(resolutionStrategy)
                            .build()

                        // 5. Initialize the ImageCapture use case with options.
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
            }
    }
}
// [END androidxr_projected_camera_capture]

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
private fun setupCameraStreamingFps(cameraInfo: CameraInfo, size: Size, desiredFps: Int): ImageAnalysis {
    // Calculate target min and max FPS using dynamic resolution checks
    // [START androidxr_projected_camera_target_fps]
    val maxFpsBySize = getMaxFpsBySize(cameraInfo, ImageFormat.YUV_420_888)
    val maxSupportedFps = maxFpsBySize[Size(640, 480)] ?: return ImageAnalysis.Builder().build()

    // minFps is the target streaming frame rate and should be at most maxSupportedFps.
    val minFps = minOf(desiredFps, maxSupportedFps)
    // Set the upper bound to 2x minFps so the stream can catch up if frames are delayed.
    val maxFps = minFps * 2
    // [END androidxr_projected_camera_target_fps]

    // [START androidxr_projected_camera_set_fps_analysis]
    // Configure Resolution Strategy for the stream
    val resolutionStrategy = ResolutionStrategy(
        size,
        ResolutionStrategy.FALLBACK_RULE_CLOSEST_LOWER
    )
    val resolutionSelector = ResolutionSelector.Builder()
        .setResolutionStrategy(resolutionStrategy)
        .build()

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
