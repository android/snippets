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

package com.example.camera.snippets.camerax

import android.content.ContentValues
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.GestureDetector
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.core.ZoomState
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.view.CameraController
import androidx.camera.view.CameraController.VIDEO_CAPTURE
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import kotlinx.coroutines.launch

private const val TAG = "Camera1ToCameraX"

private fun selectCameraWithController(baseContext: Context) {
    // [START android_camerax_camera1_to_camerax_select_camera_controller]
    // CameraX: select a camera with CameraController.

    var cameraController = LifecycleCameraController(baseContext)
    val selector = CameraSelector.Builder()
        .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
    cameraController.cameraSelector = selector
    // [END android_camerax_camera1_to_camerax_select_camera_controller]
}

class Camera1ToCameraXSelectCameraFragment : Fragment() {
    // [START android_camerax_camera1_to_camerax_select_camera_provider]
    // CameraX: select a camera with CameraProvider.

    // Use await() within a suspend function to get CameraProvider instance.
    // For more details on await(), see the preceding "Android development concepts"
    // section.
    private suspend fun startCamera() {
        val cameraProvider = ProcessCameraProvider.getInstance(requireContext()).await()

        // Set up UseCases (more on UseCases in later scenarios).
        // [START_EXCLUDE silent]
        /*
        // [END_EXCLUDE]
        var useCases:Array<UseCase> = ...
        // [START_EXCLUDE silent]
         */
        var useCases: Array<UseCase> = emptyArray()
        // [END_EXCLUDE]

        // Set the cameraSelector to use the default front-facing (selfie)
        // camera.
        val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        try {
            // Unbind UseCases before rebinding.
            cameraProvider.unbindAll()

            // Bind UseCases to camera. This function returns a camera
            // object which can be used to perform operations like zoom,
            // flash, and focus.
            var camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, *useCases)

        } catch (exc: Exception) {
            Log.e(TAG, "UseCase binding failed", exc)
        }
    }

    // ...

    // Call startCamera in the setup flow of your app, such as in onViewCreated.
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // ...

        lifecycleScope.launch {
            startCamera()
        }
    }
    // [END android_camerax_camera1_to_camerax_select_camera_provider]
}

@OptIn(ExperimentalCamera2Interop::class)
private fun selectCameraByFocalLength(cameraProvider: ProcessCameraProvider) {
    // [START android_camerax_camera1_to_camerax_focal_length]
    // CameraX: get a cameraSelector for first camera that matches the criteria
    // defined in checkFocalLength().

    val cameraInfo = cameraProvider.availableCameraInfos
        .first { cameraInfo ->
            val focalLengths = Camera2CameraInfo.from(cameraInfo)
                .getCameraCharacteristic(
                    CameraCharacteristics.LENS_INFO_AVAILABLE_FOCAL_LENGTHS
                )
            return@first checkFocalLength(focalLengths)
        }
    val cameraSelector = cameraInfo.cameraSelector
    // [END android_camerax_camera1_to_camerax_focal_length]
}

private class ControllerStateObserversActivity : AppCompatActivity() {
    private lateinit var cameraController: LifecycleCameraController

    @Suppress("DEPRECATION")
    private fun observeTapToFocusState() {
        // [START android_camerax_camera1_to_camerax_tap_to_focus_controller]
        // CameraX: track the state of tap-to-focus over the Lifecycle of a PreviewView,
        // with handlers you can define for focused, not focused, and failed states.

        val tapToFocusStateObserver = Observer<Int> { state ->
            when (state) {
                CameraController.TAP_TO_FOCUS_NOT_STARTED ->
                    Log.d(TAG, "tap-to-focus init")
                CameraController.TAP_TO_FOCUS_STARTED ->
                    Log.d(TAG, "tap-to-focus started")
                CameraController.TAP_TO_FOCUS_FOCUSED ->
                    Log.d(TAG, "tap-to-focus finished (focus successful)")
                CameraController.TAP_TO_FOCUS_NOT_FOCUSED ->
                    Log.d(TAG, "tap-to-focus finished (focused unsuccessful)")
                CameraController.TAP_TO_FOCUS_FAILED ->
                    Log.d(TAG, "tap-to-focus failed")
            }
        }

        cameraController.getTapToFocusState().observe(this, tapToFocusStateObserver)
        // [END android_camerax_camera1_to_camerax_tap_to_focus_controller]
    }

    private fun observeZoomState() {
        // [START android_camerax_camera1_to_camerax_pinch_to_zoom_controller]
        // CameraX: track the state of pinch-to-zoom over the Lifecycle of
        // a PreviewView, logging the linear zoom ratio.

        val pinchToZoomStateObserver = Observer<ZoomState> { state ->
            val zoomRatio = state.getZoomRatio()
            Log.d(TAG, "ptz-zoom-ratio $zoomRatio")
        }

        cameraController.getZoomState().observe(this, pinchToZoomStateObserver)
        // [END android_camerax_camera1_to_camerax_pinch_to_zoom_controller]
    }
}

private class TapToFocusProviderActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private var camera: Camera? = null
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    @Suppress("DEPRECATION")
    private fun setUpTapToFocus(context: Context) {
        // [START android_camerax_camera1_to_camerax_tap_to_focus_provider]
        // CameraX: implement tap-to-focus with CameraProvider.

        // Define a gesture detector to respond to tap events and call
        // startFocusAndMetering on CameraControl. If you want to use a
        // coroutine with await() to check the result of focusing, see the
        // preceding "Android development concepts" section.
        val gestureDetector = GestureDetectorCompat(context,
            object : SimpleOnGestureListener() {
                override fun onSingleTapUp(e: MotionEvent): Boolean {
                    val previewView = previewView ?: return false
                    val camera = camera ?: return false
                    val meteringPointFactory = previewView.meteringPointFactory
                    val focusPoint = meteringPointFactory.createPoint(e.x, e.y)
                    val meteringAction = FocusMeteringAction
                        .Builder(focusPoint).build()
                    lifecycleScope.launch {
                        val focusResult = camera.cameraControl
                            .startFocusAndMetering(meteringAction).await()
                        if (!focusResult.isFocusSuccessful()) {
                            Log.d(TAG, "tap-to-focus failed")
                        }
                    }
                    return true
                }
            }
        )

        // ...

        // Set the gestureDetector in a touch listener on the PreviewView.
        previewView.setOnTouchListener { _, event ->
            // See pinch-to-zoom scenario for scaleGestureDetector definition.
            var didConsume = scaleGestureDetector.onTouchEvent(event)
            if (!scaleGestureDetector.isInProgress) {
                didConsume = gestureDetector.onTouchEvent(event)
            }
            didConsume
        }
        // [END android_camerax_camera1_to_camerax_tap_to_focus_provider]
    }
}

private class PinchToZoomProviderActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView
    private var camera: Camera? = null
    private lateinit var gestureDetector: GestureDetector

    private fun setUpPinchToZoom(context: Context) {
        // [START android_camerax_camera1_to_camerax_pinch_to_zoom_provider]
        // CameraX: implement pinch-to-zoom with CameraProvider.

        // Define a scale gesture detector to respond to pinch events and call
        // setZoomRatio on CameraControl.
        val scaleGestureDetector = ScaleGestureDetector(context,
            object : ScaleGestureDetector.SimpleOnScaleGestureListener() {
                override fun onScale(detector: ScaleGestureDetector): Boolean {
                    val camera = camera ?: return false
                    val zoomState = camera.cameraInfo.zoomState
                    val currentZoomRatio: Float = zoomState.value?.zoomRatio ?: 1f
                    camera.cameraControl.setZoomRatio(
                        detector.scaleFactor * currentZoomRatio
                    )
                    return true
                }
            }
        )

        // ...

        // Set the scaleGestureDetector in a touch listener on the PreviewView.
        previewView.setOnTouchListener { _, event ->
            var didConsume = scaleGestureDetector.onTouchEvent(event)
            if (!scaleGestureDetector.isInProgress) {
                // See pinch-to-zoom scenario for gestureDetector definition.
                didConsume = gestureDetector.onTouchEvent(event)
            }
            didConsume
        }
        // [END android_camerax_camera1_to_camerax_pinch_to_zoom_provider]
    }
}

private class TakePhotoControllerActivity : AppCompatActivity() {
    private lateinit var cameraController: LifecycleCameraController
    private val context: Context
        get() = this

    // [START android_camerax_camera1_to_camerax_take_photo_controller]
    // CameraX: define a function that uses CameraController to take a photo.

    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

    private fun takePhoto() {
        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata.
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(context.getContentResolver(),
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has
        // been taken.
        cameraController.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(e: ImageCaptureException) {
                    Log.e(TAG, "photo capture failed", e)
                }

                override fun onImageSaved(
                    output: ImageCapture.OutputFileResults
                ) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }
    // [END android_camerax_camera1_to_camerax_take_photo_controller]
}

private class ImageCaptureBindActivity : AppCompatActivity() {
    private fun bindImageCapture(
        cameraProvider: ProcessCameraProvider,
        cameraSelector: CameraSelector,
        preview: Preview,
    ) {
        // [START android_camerax_camera1_to_camerax_take_photo_provider_bind]
        // CameraX: create and bind an ImageCapture UseCase.

        // Make a reference to the ImageCapture UseCase at a scope that can be accessed
        // throughout the camera logic in your app.
        var imageCapture: ImageCapture? = null

        // ...

        // Create an ImageCapture instance (can be added with other
        // UseCase definitions).
        imageCapture = ImageCapture.Builder().build()

        // ...

        // Bind UseCases to camera (adding imageCapture along with preview here, but
        // preview is not required to use imageCapture). This function returns a camera
        // object which can be used to perform operations like zoom, flash, and focus.
        var camera = cameraProvider.bindToLifecycle(
            this, cameraSelector, preview, imageCapture)
        // [END android_camerax_camera1_to_camerax_take_photo_provider_bind]
    }
}

private class TakePhotoProviderActivity : AppCompatActivity() {
    private var imageCapture: ImageCapture? = null

    // [START android_camerax_camera1_to_camerax_take_photo_provider_call]
    // CameraX: define a function that uses CameraController to take a photo.

    private fun takePhoto() {
        // Get a stable reference of the modifiable ImageCapture UseCase.
        val imageCapture = imageCapture ?: return

        // [START_EXCLUDE]
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(File(filesDir, "photo.jpg"))
            .build()
        // [END_EXCLUDE]

        // Call takePicture on imageCapture instance.
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(e: ImageCaptureException) {
                    Log.e(TAG, "photo capture failed", e)
                }

                override fun onImageSaved(
                    output: ImageCapture.OutputFileResults
                ) {
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
                    Log.d(TAG, msg)
                }
            }
        )
    }
    // [END android_camerax_camera1_to_camerax_take_photo_provider_call]
}

private fun enableVideoCapture(cameraController: LifecycleCameraController) {
    // [START android_camerax_camera1_to_camerax_video_controller_enable]
    // CameraX: Enable VideoCapture UseCase on CameraController.

    cameraController.setEnabledUseCases(VIDEO_CAPTURE)
    // [END android_camerax_camera1_to_camerax_video_controller_enable]
}

private class Camera1VideoCaptureBindActivity : AppCompatActivity() {
    private fun bindVideoCapture(
        cameraProvider: ProcessCameraProvider,
        cameraSelector: CameraSelector,
        preview: Preview,
    ) {
        // [START android_camerax_camera1_to_camerax_video_provider_bind]
        // CameraX: create and bind a VideoCapture UseCase with CameraProvider.

        // Make a reference to the VideoCapture UseCase and Recording at a
        // scope that can be accessed throughout the camera logic in your app.
        lateinit var videoCapture: VideoCapture<Recorder>
        var recording: Recording? = null

        // ...

        // Create a Recorder instance to set on a VideoCapture instance (can be
        // added with other UseCase definitions).
        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.FHD))
            .build()
        videoCapture = VideoCapture.withOutput(recorder)

        // ...

        // Bind UseCases to camera (adding videoCapture along with preview here, but
        // preview is not required to use videoCapture). This function returns a camera
        // object which can be used to perform operations like zoom, flash, and focus.
        var camera = cameraProvider.bindToLifecycle(
            this, cameraSelector, preview, videoCapture)
        // [END android_camerax_camera1_to_camerax_video_provider_bind]
    }
}

private fun checkFocalLength(focalLengths: FloatArray?): Boolean = focalLengths != null
