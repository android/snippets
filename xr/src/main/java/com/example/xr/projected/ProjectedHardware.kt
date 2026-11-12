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
import android.hardware.camera2.CameraManager
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
 * Demonstrates how to set up the audio and display glasses' camera.
 */
@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@androidx.annotation.OptIn(ExperimentalCamera2Interop::class)
@OptIn(ExperimentalProjectedApi::class, ExperimentalCoroutinesApi::class)
// [START androidxr_projected_camera_setup]
fun setUpGlassesCamera(activity: ComponentActivity) {
    activity.lifecycleScope.launch {
        var registeredCameraManager: CameraManager? = null
        var availabilityCallback: CameraManager.AvailabilityCallback? = null
        var activeCameraProvider: ProcessCameraProvider? = null

        try {
            // Before creating a projected context, verify that the projected device is connected.
            ProjectedContext.isProjectedDeviceConnected(activity, coroutineContext)
                .collectLatest { isConnected ->
                    // Clean up any existing listeners/providers from a previous connection run
                    // to prevent callback leaks if the flow re-collects rapidly.
                    availabilityCallback?.let { registeredCameraManager?.unregisterAvailabilityCallback(it) }
                    activeCameraProvider?.unbindAll()
                    availabilityCallback = null
                    registeredCameraManager = null

                    // Return early if the projected device is not connected.
                    if (!isConnected) {
                        return@collectLatest
                    }

                    try {
                        val projectedContext =
                            ProjectedContext.createProjectedDeviceContext(activity)

                        val cameraManager =
                            projectedContext.getSystemService(CameraManager::class.java)
                                ?: throw IllegalStateException("CameraManager not available on projected context")

                        registeredCameraManager = cameraManager

                        val cameraProviderFuture = ProcessCameraProvider.getInstance(projectedContext)
                        cameraProviderFuture.addListener({
                            val cameraProvider = cameraProviderFuture.get()
                            activeCameraProvider = cameraProvider

                            // Set up the listener to react to camera availability changes asynchronously.
                            val callback = object : CameraManager.AvailabilityCallback() {
                                override fun onCameraAvailable(cameraId: String) {
                                    super.onCameraAvailable(cameraId)
                                    Log.i(TAG, "Glasses camera available (ID: $cameraId). Binding use case...")

                                    // Delegate camera selection, resolution querying, and use-case binding.
                                    bindGlassesImageCapture(cameraProvider, cameraId, activity)
                                }

                                override fun onCameraUnavailable(cameraId: String) {
                                    super.onCameraUnavailable(cameraId)
                                    Log.i(TAG, "Glasses camera unavailable (ID: $cameraId). Releasing resources...")

                                    // Gracefully release the pipeline
                                    cameraProvider.unbindAll()
                                }
                            }

                            availabilityCallback = callback
                            cameraManager.registerAvailabilityCallback(
                                ContextCompat.getMainExecutor(activity),
                                callback
                            )
                        }, ContextCompat.getMainExecutor(activity))
                    } catch (e: IllegalStateException) {
                        Log.e(TAG, "Failed to initialize projected camera services", e)
                    }
                }
        } finally {
            // Resource cleanup on coroutine scope cancellation or activity destruction.
            Log.i(TAG, "Projected camera setup scope ended. Tearing down resources...")
            availabilityCallback?.let { registeredCameraManager?.unregisterAvailabilityCallback(it) }
            activeCameraProvider?.unbindAll()
            availabilityCallback = null
            registeredCameraManager = null
            activeCameraProvider = null
        }
    }
}
// [END androidxr_projected_camera_setup]

// [START androidxr_projected_camera_capture]
/**
 * Handles camera verification, supported resolution querying, and dynamic binding of
 * the [ImageCapture] use case.
 */
@RequiresApi(Build.VERSION_CODES.BAKLAVA)
@androidx.annotation.OptIn(ExperimentalCamera2Interop::class)
fun bindGlassesImageCapture(
    cameraProvider: ProcessCameraProvider,
    cameraId: String,
    activity: ComponentActivity
) {
    // Build a `CameraSelector` starting from `DEFAULT_BACK_CAMERA`
    // and narrowing it down by the dynamic `cameraId` to handle recreation changes.
    // When the projected camera is recreated, its `cameraId` may change dynamically.
    val cameraSelector = CameraSelector.Builder()
        .addCameraFilter { cameraInfos ->
            // Filter the `DEFAULT_BACK_CAMERA` candidates by our target dynamic ID.
            CameraSelector.DEFAULT_BACK_CAMERA.filter(cameraInfos).filter { info ->
                Camera2CameraInfo.from(info).cameraId == cameraId
            }
        }
        .build()

    // Pre-binding check: Verify the selected camera is registered and available on the device.
    // If the glasses are unworn (DOFF), the projected camera device is detached.
    if (!cameraProvider.hasCamera(cameraSelector)) {
        throw IllegalStateException("Glasses camera (ID: $cameraId) is not currently available.")
    }

    // Query supported streaming resolutions dynamically using Camera2 Interop.
    // While CameraX is the primary API, Camera2Interop is used here to query hardware stream characteristics.
    val cameraInfo = cameraProvider.getCameraInfo(cameraSelector)
    val map = Camera2CameraInfo.from(cameraInfo).getCameraCharacteristic(
        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP
    )
    val supportedResolutions = map?.getOutputSizes(ImageFormat.JPEG)?.toList() ?: emptyList()
    Log.i(TAG, "Supported JPEG resolutions: ${supportedResolutions.joinToString()}")

    // Dynamically select the best 16:9 target resolution.
    val preferredResolution = Size(1920, 1080)
    val dynamicTargetResolution = when {
        supportedResolutions.contains(preferredResolution) -> preferredResolution
        else -> {
            // If 1080p is unavailable, filter for 16:9 aspect ratio resolutions (within a 1% aspect ratio tolerance)
            // and choose the resolution with the largest total pixel count (width * height).
            // Fall back to the first supported resolution, or preferredResolution if the list is empty.
            val targetAspectRatio = 16.0 / 9.0
            val tolerance = 0.01
            supportedResolutions
                .filter { abs((it.width.toDouble() / it.height) - targetAspectRatio) < tolerance }
                .maxByOrNull { it.width * it.height }
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

    // Initialize the ImageCapture use case.
    val imageCapture = ImageCapture.Builder()
        .setResolutionSelector(resolutionSelector)
        .build()

    try {
        // Unbind any previously bound use cases before rebinding to the lifecycle owner.
        cameraProvider.unbindAll()

        // `bindToLifecycle()` can throw IllegalArgumentException if use cases
        // are incompatible, or IllegalStateException if the LifecycleOwner is destroyed.
        cameraProvider.bindToLifecycle(
            activity,
            cameraSelector,
            imageCapture
        )
        Log.i(TAG, "ImageCapture successfully bound for camera ID $cameraId")
    } catch (exc: IllegalArgumentException) {
        Log.e(TAG, "Invalid camera configuration or use case combination for ID $cameraId", exc)
    } catch (exc: IllegalStateException) {
        Log.e(TAG, "Camera is already bound to another lifecycle or closed", exc)
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
