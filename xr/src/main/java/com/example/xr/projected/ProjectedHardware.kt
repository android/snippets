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

import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log
import android.util.Size
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraSelector
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
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

private const val TAG = "ProjectedHardware"

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
                            // GUARD: If the connection scope has been canceled while waiting for
                            // the ProcessCameraProvider instance, abort immediately to prevent callback leaks.
                            if (!this@launch.isActive) {
                                Log.w(TAG, "ProcessCameraProvider resolved after scope cancellation. Aborting setup.")
                                return@addListener
                            }

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
            // Guarantee final resource cleanup on coroutine scope cancellation or activity destruction.
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
