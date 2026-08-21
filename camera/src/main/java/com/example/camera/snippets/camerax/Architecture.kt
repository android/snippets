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

package com.example.camerax.snippets

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraMetadata
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.Camera
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

private class ArchitectureActivity : AppCompatActivity() {

    private class ViewBinding(val previewView: PreviewView)

    private fun cameracontrollerSnippet(
        viewBinding: ViewBinding,
        baseContext: Context,
    ) {
        // [START android_camerax_architecture_camera_controller]
        val previewView: PreviewView = viewBinding.previewView
        var cameraController = LifecycleCameraController(baseContext)
        cameraController.bindToLifecycle(this)
        cameraController.cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
        previewView.controller = cameraController
        // [END android_camerax_architecture_camera_controller]
    }

    private fun cameraProviderPreviewSnippet(
        cameraProvider: ProcessCameraProvider,
        lifecycleOwner: LifecycleOwner,
        cameraSelector: CameraSelector,
    ) {
        // [START android_camerax_architecture_preview_provider]
        val preview = Preview.Builder().build()
        val viewFinder: PreviewView = findViewById(R.id.previewView)

        // The use case is bound to an Android Lifecycle with the following code
        val camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)

        // PreviewView creates a surface provider and is the recommended provider
        preview.setSurfaceProvider(viewFinder.getSurfaceProvider())
        // [END android_camerax_architecture_preview_provider]
    }
}

// [START android_camerax_architecture_custom_lifecycle]
class CustomLifecycle : LifecycleOwner {
    private val lifecycleRegistry: LifecycleRegistry

    init {
        lifecycleRegistry = LifecycleRegistry(this)
        lifecycleRegistry.markState(Lifecycle.State.CREATED)
    }
    /* [START_EXCLUDE silent] */
    /* [END_EXCLUDE] */
    fun doOnResume() {
        lifecycleRegistry.markState(Lifecycle.State.RESUMED)
    }
    /* [START_EXCLUDE silent] */
    /* [END_EXCLUDE] */
    override val lifecycle: Lifecycle
        get() = lifecycleRegistry
}
// [END android_camerax_architecture_custom_lifecycle]

private class ConcurrentUseCasesActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView

    // [START android_camerax_architecture_concurrent_use_cases]
    private lateinit var imageCapture: ImageCapture

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.preview_view)

        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener(
            Runnable {
                // Camera provider is now guaranteed to be available
                val cameraProvider = cameraProviderFuture.get()

                // Set up the preview use case to display camera preview.
                val preview = Preview.Builder().build()

                // Set up the capture use case to allow users to take photos.
                imageCapture = ImageCapture.Builder()
                    .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                    .build()

                // Choose the camera by requiring a lens facing
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                    .build()

                // Attach use cases to the camera with the same lifecycle owner
                val camera = cameraProvider.bindToLifecycle(
                    this as LifecycleOwner, cameraSelector, preview, imageCapture
                )

                // Connect the preview use case to the previewView
                preview.setSurfaceProvider(
                    previewView.getSurfaceProvider()
                )
            },
            ContextCompat.getMainExecutor(this)
        )
    }
    // [END android_camerax_architecture_concurrent_use_cases]
}

// [START android_camerax_architecture_check_level_3]
@androidx.annotation.OptIn(ExperimentalCamera2Interop::class)
fun isBackCameraLevel3Device(cameraProvider: ProcessCameraProvider): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        return CameraSelector.DEFAULT_BACK_CAMERA
            .filter(cameraProvider.availableCameraInfos)
            .firstOrNull()
            ?.let { Camera2CameraInfo.from(it) }
            ?.getCameraCharacteristic(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL) ==
            CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL_3
    }
    return false
}
// [END android_camerax_architecture_check_level_3]

private fun ProcessCameraProvider.getAvailableCameraInfos(): List<CameraInfo> = availableCameraInfos
private fun CameraInfo.getLensFacing(): Int = lensFacing
private val CameraInfo.cameraSelector: CameraSelector get() = CameraSelector.DEFAULT_FRONT_CAMERA

@androidx.annotation.OptIn(ExperimentalCamera2Interop::class)
private fun streamUseCaseSnippet(
    cameraProvider: ProcessCameraProvider,
    lifecycleOwner: LifecycleOwner,
    screenAspectRatio: Int,
    rotation: Int,
) {
    var camera: Camera? = null
    // [START android_camerax_architecture_video_call_stream]
    // Set underlying Camera2 stream use case to optimize for video calls.

    val videoCallStreamId =
        CameraMetadata.SCALER_AVAILABLE_STREAM_USE_CASES_VIDEO_CALL.toLong()

    // Check available CameraInfos to find the first one that supports
    // the video call stream use case.
    val frontCameraInfo = cameraProvider.getAvailableCameraInfos()
        .first { cameraInfo ->
            val isVideoCallStreamingSupported = Camera2CameraInfo.from(cameraInfo)
                .getCameraCharacteristic(
                    CameraCharacteristics.SCALER_AVAILABLE_STREAM_USE_CASES
                )?.contains(videoCallStreamId)
            val isFrontFacing = (
                cameraInfo.getLensFacing() == 
                    CameraSelector.LENS_FACING_FRONT
                )
            (isVideoCallStreamingSupported == true) && isFrontFacing
        }

    val cameraSelector = frontCameraInfo.cameraSelector

    // Start with a Preview Builder.
    val previewBuilder = Preview.Builder()
        .setTargetAspectRatio(screenAspectRatio)
        .setTargetRotation(rotation)

    // Use Camera2Interop.Extender to set the video call stream use case.
    Camera2Interop.Extender(previewBuilder).setStreamUseCase(videoCallStreamId)

    // Bind the Preview UseCase and the corresponding CameraSelector.
    val preview = previewBuilder.build()
    camera = cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)
    // [END android_camerax_architecture_video_call_stream]
}
