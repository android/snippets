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

import android.app.Application
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.os.Bundle
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.OrientationEventListener
import android.view.Surface
import android.view.SurfaceView
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.camera2.Camera2Config
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.AspectRatio
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraXConfig
import androidx.camera.core.ConcurrentCamera
import androidx.camera.core.DisplayOrientedMeteringPointFactory
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executor
import java.util.concurrent.TimeUnit

private fun imageCaptureBuilderSnippet() {
    // [START android_camerax_configuration_image_capture_builder]
    val imageCapture = ImageCapture.Builder()
        .setFlashMode(ImageCapture.FLASH_MODE_AUTO)
        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
        .build()
    // [END android_camerax_configuration_image_capture_builder]
}

// [START android_camerax_configuration_logging]
class CameraApplication : Application(), CameraXConfig.Provider {
    override fun getCameraXConfig(): CameraXConfig {
        return CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
            .setMinimumLoggingLevel(Log.ERROR).build()
    }
}
// [END android_camerax_configuration_logging]

// [START android_camerax_configuration_camera_limiter]
class MainApplication : Application(), CameraXConfig.Provider {
    override fun getCameraXConfig(): CameraXConfig {
        return CameraXConfig.Builder.fromConfig(Camera2Config.defaultConfig())
            .setAvailableCamerasLimiter(CameraSelector.DEFAULT_BACK_CAMERA)
            .build()
    }
}
// [END android_camerax_configuration_camera_limiter]

private class ConfigurationOrientationActivity : AppCompatActivity() {
    // [START android_camerax_configuration_orientation_listener]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imageCapture = ImageCapture.Builder().build()

        val orientationEventListener = object : OrientationEventListener(this as Context) {
            override fun onOrientationChanged(orientation: Int) {
                // Monitors orientation values to determine the target rotation value
                val rotation: Int = when (orientation) {
                    in 45..134 -> Surface.ROTATION_270
                    in 135..224 -> Surface.ROTATION_180
                    in 225..314 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageCapture.targetRotation = rotation
            }
        }
        orientationEventListener.enable()
    }
    // [END android_camerax_configuration_orientation_listener]
}

private fun cropRectSnippet(
    cameraProvider: ProcessCameraProvider,
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    preview: Preview,
    imageAnalysis: ImageAnalysis,
    imageCapture: ImageCapture,
    width: Int,
    height: Int,
    display: android.view.Display,
) {
    // [START android_camerax_configuration_crop_rect]
    val viewPort = ViewPort.Builder(Rational(width, height), display.rotation).build()
    val useCaseGroup = UseCaseGroup.Builder()
        .addUseCase(preview)
        .addUseCase(imageAnalysis)
        .addUseCase(imageCapture)
        .setViewPort(viewPort)
        .build()
    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, useCaseGroup)
    // [END android_camerax_configuration_crop_rect]
}

private class GetViewPortActivity : AppCompatActivity() {
    private fun getViewportSnippet() {
        // [START android_camerax_configuration_get_viewport]
        val viewport = findViewById<PreviewView>(R.id.preview_view).viewPort
        // [END android_camerax_configuration_get_viewport]
    }
}

@androidx.annotation.OptIn(ExperimentalCamera2Interop::class)
// [START android_camerax_configuration_select_external_camera]
fun selectExternalOrBestCamera(provider: ProcessCameraProvider): CameraSelector? {
    val cam2Infos = provider.availableCameraInfos.map {
        Camera2CameraInfo.from(it)
    }.sortedByDescending {
        // HARDWARE_LEVEL is Int type, with the order of:
        // LEGACY < LIMITED < FULL < LEVEL_3 < EXTERNAL
        it.getCameraCharacteristic(CameraCharacteristics.INFO_SUPPORTED_HARDWARE_LEVEL)
    }

    return when {
        cam2Infos.isNotEmpty() -> {
            CameraSelector.Builder()
                .addCameraFilter {
                    it.filter { camInfo ->
                        // cam2Infos[0] is either EXTERNAL or best built-in camera
                        val thisCamId = Camera2CameraInfo.from(camInfo).cameraId
                        thisCamId == cam2Infos[0].cameraId
                    }
                }.build()
        }
        else -> null
    }
}
// [END android_camerax_configuration_select_external_camera]

private fun bindSelectedExternalCamera(
    processCameraProvider: ProcessCameraProvider,
    lifecycleOwner: LifecycleOwner,
    preview: Preview,
    analysis: ImageAnalysis,
) {
    // [START_EXCLUDE]
    val selector = selectExternalOrBestCamera(processCameraProvider) ?: return
    processCameraProvider.bindToLifecycle(lifecycleOwner, selector, preview, analysis)
    // [END_EXCLUDE]
}

private fun concurrentCameraSnippet(
    cameraProvider: ProcessCameraProvider,
    primaryCameraSelector: CameraSelector,
    secondaryCameraSelector: CameraSelector,
    useCaseGroup: UseCaseGroup,
    lifecycleOwner: LifecycleOwner,
) {
    // [START android_camerax_configuration_concurrent_camera]
    // Build ConcurrentCameraConfig
    val primary = ConcurrentCamera.SingleCameraConfig(
        primaryCameraSelector,
        useCaseGroup,
        lifecycleOwner
    )

    val secondary = ConcurrentCamera.SingleCameraConfig(
        secondaryCameraSelector,
        useCaseGroup,
        lifecycleOwner
    )

    val concurrentCamera = cameraProvider.bindToLifecycle(
        listOf(primary, secondary)
    )

    val primaryCamera = concurrentCamera.cameras[0]
    val secondaryCamera = concurrentCamera.cameras[1]
    // [END android_camerax_configuration_concurrent_camera]
}

private fun targetResolutionSnippet() {
    // [START android_camerax_configuration_target_resolution]
    val imageAnalysis = ImageAnalysis.Builder()
        .setTargetResolution(Size(1280, 720))
        .build()
    // [END android_camerax_configuration_target_resolution]
}

private fun cameraControlCameraInfoSnippet(
    processCameraProvider: ProcessCameraProvider,
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    preview: Preview,
) {
    // [START android_camerax_configuration_cameracontrol_camerainfo]
    val camera = processCameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, preview)

    // For performing operations that affect all outputs.
    val cameraControl = camera.cameraControl
    // For querying information and states.
    val cameraInfo = camera.cameraInfo
    // [END android_camerax_configuration_cameracontrol_camerainfo]
}

private fun meteringPointFactorySnippet(
    previewView: PreviewView,
    surfaceView: SurfaceView,
    camera: Camera,
    imageAnalysis: ImageAnalysis,
    imageWidth: Int,
    imageHeight: Int,
) {
    // [START android_camerax_configuration_metering_point_factory]
    // Use PreviewView.getMeteringPointFactory if PreviewView is used for preview.
    previewView.setOnTouchListener { view, motionEvent ->
        val meteringPoint = previewView.meteringPointFactory
            .createPoint(motionEvent.x, motionEvent.y)
        /* [START_EXCLUDE silent] */
        true
        /* [END_EXCLUDE] */
    }

    // Use DisplayOrientedMeteringPointFactory if SurfaceView / TextureView is used for
    // preview. Please note that if the preview is scaled or cropped in the View,
    // it’s the application's responsibility to transform the coordinates properly
    // so that the width and height of this factory represents the full Preview FOV.
    // And the (x,y) passed to create MeteringPoint might need to be adjusted with
    // the offsets.
    val meteringPointFactory = DisplayOrientedMeteringPointFactory(
        surfaceView.display,
        camera.cameraInfo,
        surfaceView.width.toFloat(),
        surfaceView.height.toFloat()
    )

    // Use SurfaceOrientedMeteringPointFactory if the point is specified in
    // ImageAnalysis ImageProxy.
    val meteringPointFactoryAnalysis = SurfaceOrientedMeteringPointFactory(
        imageWidth.toFloat(),
        imageHeight.toFloat(),
        imageAnalysis
    )
    // [END android_camerax_configuration_metering_point_factory]
}

private class FocusMeteringActivity : AppCompatActivity() {
    private fun focusMeteringSnippet(
        meteringPointFactory: DisplayOrientedMeteringPointFactory,
        cameraControl: CameraControl,
        x1: Float,
        y1: Float,
        x2: Float,
        y2: Float,
    ) {
        // [START android_camerax_configuration_focus_metering_action]
        val meteringPoint1 = meteringPointFactory.createPoint(x1, y1)
        val meteringPoint2 = meteringPointFactory.createPoint(x2, y2)
        val action = FocusMeteringAction.Builder(meteringPoint1) // default AF|AE|AWB
            // Optionally add meteringPoint2 for AF/AE.
            .addPoint(meteringPoint2, FocusMeteringAction.FLAG_AF or FocusMeteringAction.FLAG_AE)
            // The action is canceled in 3 seconds (if not set, default is 5s).
            .setAutoCancelDuration(3, TimeUnit.SECONDS)
            .build()

        val result = cameraControl.startFocusAndMetering(action)
        // Adds listener to the ListenableFuture if you need to know the focusMetering result.
        result.addListener({
            // result.get().isFocusSuccessful returns if the auto focus is successful or not.
        }, ContextCompat.getMainExecutor(this))
        // [END android_camerax_configuration_focus_metering_action]
    }

    private fun exposureCompensationSnippet(
        camera: Camera,
        exposureCompensationIndex: Int,
        mainExecutor: Executor,
    ) {
        // [START android_camerax_configuration_exposure_compensation]
        camera.cameraControl.setExposureCompensationIndex(exposureCompensationIndex)
            .addListener({
                // Get the current exposure compensation index, it might be
                // different from the asked value in case this request was
                // canceled by a newer setting request.
                val currentExposureIndex = camera.cameraInfo.exposureState.exposureCompensationIndex
                /* [START_EXCLUDE silent] */
                // [END_EXCLUDE] */
            }, mainExecutor)
        // [END android_camerax_configuration_exposure_compensation]
    }

    private fun seekBarSnippet(
        camera: Camera,
        binding: Any,
    ) {
        val seekBar = android.widget.SeekBar(this)
        // [START android_camerax_configuration_exposure_seekbar]
        val exposureState = camera.cameraInfo.exposureState
        seekBar.apply {
            isEnabled = exposureState.isExposureCompensationSupported
            max = exposureState.exposureCompensationRange.upper
            min = exposureState.exposureCompensationRange.lower
            progress = exposureState.exposureCompensationIndex
        }
        // [END android_camerax_configuration_exposure_seekbar]
    }
}
