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

import android.content.ContentValues
import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.GestureDetector.SimpleOnGestureListener
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.View
import android.widget.Toast
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
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import java.io.File
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import kotlinx.coroutines.launch

private const val TAG = "Camera1ToCameraX"

private class Camera1ToCameraXActivity : AppCompatActivity() {

    private fun selectCameraControllerSnippet(baseContext: Context) {
        // [START android_camerax_camera1_to_camerax_select_camera_controller]
        var cameraController = LifecycleCameraController(baseContext)
        val selector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK).build()
        cameraController.cameraSelector = selector
        // [END android_camerax_camera1_to_camerax_select_camera_controller]
    }

    // [START android_camerax_camera1_to_camerax_select_camera_provider]
    // Use await() within a suspend function to get CameraProvider instance.
    // For more details on await(), see the preceding "Android development concepts"
    // section.
    private suspend fun startCamera() {
        val cameraProvider = ProcessCameraProvider.getInstance(this).await()

        // Set up UseCases (more on UseCases in later scenarios)
        var useCases: Array<UseCase> = /* [START_EXCLUDE silent] */ emptyArray() /* [END_EXCLUDE] */

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

        } catch(exc: Exception) {
            Log.e(TAG, "UseCase binding failed", exc)
        }
    }

    /* [START_EXCLUDE silent] */
    // [END_EXCLUDE] */
    // Call startCamera in the setup flow of your app, such as in onViewCreated.
    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]
        lifecycleScope.launch {
            startCamera()
        }
    }
    // [END android_camerax_camera1_to_camerax_select_camera_provider]

    @androidx.annotation.OptIn(ExperimentalCamera2Interop::class)
    private fun focalLengthSnippet(cameraProvider: ProcessCameraProvider) {
        // [START android_camerax_camera1_to_camerax_focal_length]
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

    private fun checkFocalLength(focalLengths: FloatArray?): Boolean = true
}

private class ActivityMainBinding(val cameraPreview: PreviewView) {
    val root: View = cameraPreview
    companion object {
        fun inflate(inflater: Any): ActivityMainBinding = ActivityMainBinding(PreviewView(null as Context? ?: error("")))
    }
}

// [START android_camerax_camera1_to_camerax_preview_controller]
private class Camera1MainActivity : AppCompatActivity() {
    private lateinit var viewBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /* [START_EXCLUDE silent] */
        // [END_EXCLUDE] */
        setContentView(R.layout.preview_view)

        // Create the CameraController and set it on the previewView.
        var cameraController = LifecycleCameraController(baseContext)
        cameraController.bindToLifecycle(this)
        val previewView: PreviewView = viewBinding.cameraPreview
        previewView.controller = cameraController
    }
}
// [END android_camerax_camera1_to_camerax_preview_controller]

private class PreviewProviderActivity : AppCompatActivity() {
    private lateinit var previewView: PreviewView

    // [START android_camerax_camera1_to_camerax_preview_provider]
    // Use await() within a suspend function to get CameraProvider instance.
    // For more details on await(), see the preceding "Android development concepts"
    // section.
    private suspend fun startCamera() {
        val cameraProvider = ProcessCameraProvider.getInstance(this).await()

        // Create Preview UseCase.
        val preview = Preview.Builder()
            .build()
            .also {
                it.setSurfaceProvider(
                    previewView.surfaceProvider
                )
            }

        // Select default back camera.
        val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

        try {
            // Unbind UseCases before rebinding.
            cameraProvider.unbindAll()

            // Bind UseCases to camera. This function returns a camera
            // object which can be used to perform operations like zoom,
            // flash, and focus.
            var camera = cameraProvider.bindToLifecycle(
                this, cameraSelector, preview)

        } catch(exc: Exception) {
            Log.e(TAG, "UseCase binding failed", exc)
        }
    }

    /* [START_EXCLUDE silent] */
    // [END_EXCLUDE] */
    // Call startCamera() in the setup flow of your app, such as in onViewCreated.
    fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // [START_EXCLUDE silent]
        // [END_EXCLUDE]
        lifecycleScope.launch {
            startCamera()
        }
    }
    // [END android_camerax_camera1_to_camerax_preview_provider]
}

private fun tapToFocusControllerSnippet(
    cameraController: LifecycleCameraController,
    lifecycleOwner: LifecycleOwner,
) {
    // [START android_camerax_camera1_to_camerax_tap_to_focus_controller]
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

    cameraController.tapToFocusState.observe(lifecycleOwner, tapToFocusStateObserver)
    // [END android_camerax_camera1_to_camerax_tap_to_focus_controller]
}

private class TapToFocusProviderActivity : AppCompatActivity() {
    private var previewView: PreviewView? = null
    private var camera: Camera? = null
    private lateinit var scaleGestureDetector: ScaleGestureDetector

    private fun setupGesture(context: Context) {
        // [START android_camerax_camera1_to_camerax_tap_to_focus_provider]
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
                        if (!focusResult.isFocusSuccessful) {
                            Log.d(TAG, "tap-to-focus failed")
                        }
                    }
                    return true
                }
            }
        )

        /* [START_EXCLUDE silent] */
        // [END_EXCLUDE] */
        // Set the gestureDetector in a touch listener on the PreviewView.
        previewView?.setOnTouchListener { _, event ->
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

private fun pinchToZoomControllerSnippet(
    cameraController: LifecycleCameraController,
    lifecycleOwner: LifecycleOwner,
) {
    // [START android_camerax_camera1_to_camerax_pinch_to_zoom_controller]
    val pinchToZoomStateObserver = Observer<ZoomState> { state ->
        val zoomRatio = state.zoomRatio
        Log.d(TAG, "ptz-zoom-ratio $zoomRatio")
    }

    cameraController.zoomState.observe(lifecycleOwner, pinchToZoomStateObserver)
    // [END android_camerax_camera1_to_camerax_pinch_to_zoom_controller]
}

private class PinchToZoomProviderActivity : AppCompatActivity() {
    private var previewView: PreviewView? = null
    private var camera: Camera? = null
    private lateinit var gestureDetector: GestureDetectorCompat

    private fun setupPinch(context: Context) {
        // [START android_camerax_camera1_to_camerax_pinch_to_zoom_provider]
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

        /* [START_EXCLUDE silent] */
        // [END_EXCLUDE] */
        // Set the scaleGestureDetector in a touch listener on the PreviewView.
        previewView?.setOnTouchListener { _, event ->
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

    // [START android_camerax_camera1_to_camerax_take_photo_controller]
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

    private fun takePhoto() {
       // Create time stamped name and MediaStore entry.
       val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                  .format(System.currentTimeMillis())
       val contentValues = ContentValues().apply {
           put(MediaStore.MediaColumns.DISPLAY_NAME, name)
           put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
           if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
               put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
           }
       }

       // Create output options object which contains file + metadata.
       val outputOptions = ImageCapture.OutputFileOptions
           .Builder(contentResolver,
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

private class TakePhotoProviderActivity : AppCompatActivity() {
    private fun bindSnippet(
        cameraProvider: ProcessCameraProvider,
        cameraSelector: CameraSelector,
        preview: Preview,
    ) {
        // [START android_camerax_camera1_to_camerax_take_photo_provider_bind]
        // Make a reference to the ImageCapture UseCase at a scope that can be accessed
        // throughout the camera logic in your app.
        var imageCapture: ImageCapture? = null

        /* [START_EXCLUDE silent] */
        // [END_EXCLUDE] */
        // Create an ImageCapture instance (can be added with other
        // UseCase definitions).
        imageCapture = ImageCapture.Builder().build()

        /* [START_EXCLUDE silent] */
        // [END_EXCLUDE] */
        // Bind UseCases to camera (adding imageCapture along with preview here, but
        // preview is not required to use imageCapture). This function returns a camera
        // object which can be used to perform operations like zoom, flash, and focus.
        var camera = cameraProvider.bindToLifecycle(
            this, cameraSelector, preview, imageCapture)
        // [END android_camerax_camera1_to_camerax_take_photo_provider_bind]
    }

    private var imageCapture: ImageCapture? = null

    // [START android_camerax_camera1_to_camerax_take_photo_provider_call]
    private fun takePhoto() {
        // Get a stable reference of the modifiable ImageCapture UseCase.
        val imageCapture = imageCapture ?: return

        /* [START_EXCLUDE silent] */
        // [END_EXCLUDE] */
        // Call takePicture on imageCapture instance.
        imageCapture.takePicture(
            /* [START_EXCLUDE silent] */
            ImageCapture.OutputFileOptions.Builder(File("")).build(),
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(e: ImageCaptureException) {}
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {}
            }
            /* [END_EXCLUDE] */
        )
    }
    // [END android_camerax_camera1_to_camerax_take_photo_provider_call]
}

private fun enableVideoSnippet(cameraController: LifecycleCameraController) {
    // [START android_camerax_camera1_to_camerax_video_controller_enable]
    cameraController.setEnabledUseCases(CameraController.VIDEO_CAPTURE)
    // [END android_camerax_camera1_to_camerax_video_controller_enable]
}

private class OutputFileOptions {
    class Builder(val file: File) {
        fun build() = OutputFileOptions()
    }
}
private class OutputFileResults(val savedUri: String? = null)
private interface OnVideoSavedCallback {
    fun onVideoSaved(outputFileResults: OutputFileResults)
    fun onError(videoCaptureError: Int, message: String, cause: Throwable?)
}
private fun CameraController.isRecording(): Boolean = false
private fun CameraController.stopRecording() {}
private fun CameraController.startRecording(options: Any, executor: Any, callback: Any) {}

private class VideoControllerActivity : AppCompatActivity() {
    private lateinit var cameraController: LifecycleCameraController

    // [START android_camerax_camera1_to_camerax_video_controller]
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

    // Define a VideoSaveCallback class for handling success and error states.
    class VideoSaveCallback : OnVideoSavedCallback {
        override fun onVideoSaved(outputFileResults: OutputFileResults) {
            val msg = "Video capture succeeded: ${outputFileResults.savedUri}"
            /* [START_EXCLUDE] */
            Log.d(TAG, msg)
            /* [END_EXCLUDE] */
        }

        override fun onError(videoCaptureError: Int, message: String,
                             cause: Throwable?) {
            Log.d(TAG, "error saving video: $message", cause)
        }
    }

    private fun startStopVideo() {
        if (cameraController.isRecording()) {
            // Stop the current recording session.
            cameraController.stopRecording()
            return
        }

        // Define the File options for saving the video.
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())

        val outputFileOptions = OutputFileOptions
            .Builder(File(this.filesDir, name))
            .build()

        // Call startRecording on the CameraController.
        cameraController.startRecording(
            outputFileOptions,
            ContextCompat.getMainExecutor(this),
            VideoSaveCallback()
        )
    }
    // [END android_camerax_camera1_to_camerax_video_controller]
}

private class VideoProviderActivity : AppCompatActivity() {
    private fun bindVideoSnippet(
        cameraProvider: ProcessCameraProvider,
        cameraSelector: CameraSelector,
        preview: Preview,
    ) {
        // [START android_camerax_camera1_to_camerax_video_provider_bind]
        // Make a reference to the VideoCapture UseCase and Recording at a
        // scope that can be accessed throughout the camera logic in your app.
        var videoCapture: VideoCapture<Recorder>
        var recording: Recording? = null

        /* [START_EXCLUDE silent] */
        // [END_EXCLUDE] */
        // Create a Recorder instance to set on a VideoCapture instance (can be
        // added with other UseCase definitions).
        val recorder = Recorder.Builder()
            .setQualitySelector(QualitySelector.from(Quality.FHD))
            .build()
        videoCapture = VideoCapture.withOutput(recorder)

        /* [START_EXCLUDE silent] */
        // [END_EXCLUDE] */
        // Bind UseCases to camera (adding videoCapture along with preview here, but
        // preview is not required to use videoCapture). This function returns a camera
        // object which can be used to perform operations like zoom, flash, and focus.
        var camera = cameraProvider.bindToLifecycle(
            this, cameraSelector, preview, videoCapture)
        // [END android_camerax_camera1_to_camerax_video_provider_bind]
    }

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null
    private lateinit var viewBinding: Any

    // [START android_camerax_camera1_to_camerax_video_provider_record]
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

    private fun startStopVideo() {
       val videoCapture = this.videoCapture ?: return

       if (recording != null) {
           // Stop the current recording session.
           recording?.stop()
           recording = null
           return
       }

       // Create and start a new recording session.
       val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
           .format(System.currentTimeMillis())
       val contentValues = ContentValues().apply {
           put(MediaStore.MediaColumns.DISPLAY_NAME, name)
           put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
           if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
               put(MediaStore.Video.Media.RELATIVE_PATH, "Movies/CameraX-Video")
           }
       }

       val mediaStoreOutputOptions = MediaStoreOutputOptions
           .Builder(contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
           .setContentValues(contentValues)
           .build()

       recording = videoCapture.output
           .prepareRecording(this, mediaStoreOutputOptions)
           .withAudioEnabled()
           .start(ContextCompat.getMainExecutor(this)) { recordEvent ->
               when(recordEvent) {
                   is VideoRecordEvent.Start -> {
                       /* [START_EXCLUDE silent] */
                       // [END_EXCLUDE] */
                   }
                   is VideoRecordEvent.Finalize -> {
                       if (!recordEvent.hasError()) {
                           val msg = "Video capture succeeded: " +
                               "${recordEvent.outputResults.outputUri}"
                           Toast.makeText(
                               baseContext, msg, Toast.LENGTH_SHORT
                           ).show()
                           Log.d(TAG, msg)
                       } else {
                           recording?.close()
                           recording = null
                           Log.e(TAG, "video capture ends with error: ${recordEvent.error}")
                       }
                   }
               }
           }
    }
    // [END android_camerax_camera1_to_camerax_video_provider_record]
}
