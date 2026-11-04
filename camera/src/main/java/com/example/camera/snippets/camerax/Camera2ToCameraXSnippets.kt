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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.camera2.CaptureRequest
import android.util.Log
import androidx.annotation.OptIn
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.view.PreviewView
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.lifecycleScope
import java.util.concurrent.Executor
import kotlinx.coroutines.launch

// [START android_camerax_initialize_provider_camera2]
/**
 * Use cases shared by both UI options. Each UI option builds its own Preview
 * with a surface provider and passes it to [bindUseCases].
 */
class CameraUseCases {
    val imageCapture: ImageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()

    // Replaces a Camera2 ImageReader that was used for frame processing.
    val imageAnalysis: ImageAnalysis = ImageAnalysis.Builder()
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()
}

/**
 * Replaces CameraManager.openCamera and the session callbacks. Returns null
 * when the device has no camera for [lensFacing].
 */
suspend fun bindUseCases(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    preview: Preview,
    useCases: CameraUseCases,
    lensFacing: Int = CameraSelector.LENS_FACING_BACK,
): Camera? {
    val cameraProvider = ProcessCameraProvider.awaitInstance(context)
    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()
    if (!cameraProvider.hasCamera(cameraSelector)) return null

    cameraProvider.unbindAll()
    return cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        preview,
        useCases.imageCapture,
        useCases.imageAnalysis,
    )
}
// [END android_camerax_initialize_provider_camera2]

// [START android_camerax_view_preview_camera2]
fun startCamera(
    activity: ComponentActivity,
    previewView: PreviewView,
    useCases: CameraUseCases,
) {
    // Attach the surface provider before binding, or the preview stays blank.
    val preview = Preview.Builder().build()
    preview.setSurfaceProvider(previewView.surfaceProvider)

    activity.lifecycleScope.launch {
        val camera = bindUseCases(activity, activity, preview, useCases)
            ?: return@launch

        // Tap-to-focus: meteringPointFactory handles rotation and scaling.
        previewView.setOnTouchListener { view, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                val point = previewView.meteringPointFactory.createPoint(event.x, event.y)
                camera.cameraControl.startFocusAndMetering(
                    FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF).build()
                )
                view.performClick()
            }
            true
        }
    }
}
// [END android_camerax_view_preview_camera2]

// [START android_camerax_compose_preview_camera2]
@Composable
fun CameraXPreview(useCases: CameraUseCases, modifier: Modifier = Modifier) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    val coordinateTransformer = remember { MutableCoordinateTransformer() }

    // Build the Preview once, with its surface provider attached.
    val preview = remember {
        Preview.Builder().build().apply {
            setSurfaceProvider { request -> surfaceRequest = request }
        }
    }

    LaunchedEffect(lifecycleOwner) {
        cameraControl =
            bindUseCases(context, lifecycleOwner, preview, useCases)?.cameraControl
    }

    surfaceRequest?.let { request ->
        CameraXViewfinder(
            surfaceRequest = request,
            coordinateTransformer = coordinateTransformer,
            modifier = modifier.pointerInput(request, cameraControl) {
                detectTapGestures { offset ->
                    val surfaceCoords = with(coordinateTransformer) { offset.transform() }
                    val point = SurfaceOrientedMeteringPointFactory(
                        request.resolution.width.toFloat(),
                        request.resolution.height.toFloat(),
                    ).createPoint(surfaceCoords.x, surfaceCoords.y)
                    cameraControl?.startFocusAndMetering(
                        FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF).build()
                    )
                }
            },
        )
    }
}
// [END android_camerax_compose_preview_camera2]

// [START android_camerax_capture_photo_camera2]
/**
 * Replaces an ImageReader and TEMPLATE_STILL_CAPTURE flow. CameraX still
 * needs an app-supplied [executor] for the callback, for example
 * `ContextCompat.getMainExecutor(context)` or an app-owned background
 * executor.
 */
fun capturePhoto(
    imageCapture: ImageCapture,
    executor: Executor,
    lensFacing: Int,
    onPhoto: (Bitmap) -> Unit,
) {
    imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                image.use { proxy ->
                    val buffer = proxy.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        ?: return
                    val matrix = Matrix()
                    matrix.postRotate(proxy.imageInfo.rotationDegrees.toFloat())
                    if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                        matrix.postScale(-1f, 1f)
                    }
                    onPhoto(
                        Bitmap.createBitmap(
                            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                        )
                    )
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraX", "Capture failed", exception)
            }
        },
    )
}
// [END android_camerax_capture_photo_camera2]

// [START android_camerax_image_analysis]
/**
 * For raw frame access only. For ML Kit barcode, face, or text detection,
 * use MlKitAnalyzer instead. [analysisExecutor] is app-owned, for example
 * `Executors.newSingleThreadExecutor()`; shut it down when the camera closes.
 */
fun setUpAnalysis(imageAnalysis: ImageAnalysis, analysisExecutor: Executor) {
    imageAnalysis.setAnalyzer(analysisExecutor) { imageProxy ->
        // use {} closes the ImageProxy so the next frame can arrive.
        imageProxy.use { proxy ->
            val rotationDegrees = proxy.imageInfo.rotationDegrees
            val yPlane = proxy.planes[0].buffer
            Log.d("CameraX", "Frame ${proxy.width}x${proxy.height}, " +
                "rotation=$rotationDegrees, bytes=${yPlane.remaining()}")
        }
    }
}
// [END android_camerax_image_analysis]

// [START android_camerax_camera2_interop]
/**
 * Applies Camera2 manual exposure to ImageCapture. Camera2Interop.Extender is
 * deprecated from CameraX 1.7.0 in favor of the camera2Interop Kotlin builder.
 */
@OptIn(ExperimentalCamera2Interop::class)
fun buildManualExposureImageCapture(
    exposureTimeNs: Long,
    iso: Int,
): ImageCapture {
    val builder = ImageCapture.Builder()
    @Suppress("DEPRECATION") // Deprecated in CameraX 1.7.0; see the note in the guide.
    Camera2Interop.Extender(builder)
        // AE off: the app supplies exposure time and sensitivity.
        .setCaptureRequestOption(
            CaptureRequest.CONTROL_AE_MODE,
            CaptureRequest.CONTROL_AE_MODE_OFF,
        )
        .setCaptureRequestOption(CaptureRequest.SENSOR_EXPOSURE_TIME, exposureTimeNs)
        .setCaptureRequestOption(CaptureRequest.SENSOR_SENSITIVITY, iso)
    return builder.build()
}
// [END android_camerax_camera2_interop]
