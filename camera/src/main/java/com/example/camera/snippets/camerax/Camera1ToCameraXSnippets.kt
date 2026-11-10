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
import android.util.Log
import android.view.MotionEvent
import android.view.Surface
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.concurrent.Executor

// Option A (Android Views): initialize the provider and bind the use cases.
// [START android_camerax_view_preview]
/**
 * Binds Preview and ImageCapture to [lifecycleOwner]. Call it from
 * `lifecycleScope.launch { }` in the Activity or Fragment that owns
 * [previewView]. Returns null when the device has no camera for [lensFacing].
 */
suspend fun bindCameraUseCases(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    lensFacing: Int = CameraSelector.LENS_FACING_BACK,
): Pair<Camera, ImageCapture>? {
    // awaitInstance is a suspend function in camera-lifecycle; no extra
    // coroutines artifact is needed.
    val cameraProvider = ProcessCameraProvider.awaitInstance(context)

    val cameraSelector = CameraSelector.Builder()
        .requireLensFacing(lensFacing)
        .build()
    if (!cameraProvider.hasCamera(cameraSelector)) {
        Log.w("CameraX", "No camera for lens facing $lensFacing")
        return null
    }

    // Attach the surface provider before binding, or the preview stays blank.
    val preview = Preview.Builder().build()
    preview.setSurfaceProvider(previewView.surfaceProvider)

    val imageCapture = ImageCapture.Builder()
        .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
        .build()

    cameraProvider.unbindAll() // Unbind before rebinding
    val camera = cameraProvider.bindToLifecycle(
        lifecycleOwner,
        cameraSelector,
        preview,
        imageCapture,
    )
    return camera to imageCapture
}
// [END android_camerax_view_preview]

// [START android_camerax_view_tap_to_focus]
fun enableTapToFocus(previewView: PreviewView, cameraControl: CameraControl) {
    previewView.setOnTouchListener { view, event ->
        if (event.action == MotionEvent.ACTION_UP) {
            // meteringPointFactory maps view coordinates to sensor coordinates,
            // including rotation and scale type.
            val point = previewView.meteringPointFactory.createPoint(event.x, event.y)
            val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                .build()
            cameraControl.startFocusAndMetering(action)
            view.performClick()
        }
        true
    }
}
// [END android_camerax_view_tap_to_focus]

// Option B (Jetpack Compose): a self-contained camera screen.
// [START android_camerax_compose_preview]
@Composable
fun CameraPreviewScreen(
    modifier: Modifier = Modifier,
    lensFacing: Int = CameraSelector.LENS_FACING_BACK,
    onImageCaptureReady: (ImageCapture) -> Unit = {},
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val view = LocalView.current
    val configuration = LocalConfiguration.current

    var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }
    var cameraControl by remember { mutableStateOf<CameraControl?>(null) }
    // Maps viewfinder tap offsets to surface coordinates for tap-to-focus.
    val coordinateTransformer = remember { MutableCoordinateTransformer() }

    // Build the Preview once, with its surface provider attached.
    val preview = remember {
        Preview.Builder().build().apply {
            setSurfaceProvider { request -> surfaceRequest = request }
        }
    }
    val imageCapture = remember {
        ImageCapture.Builder()
            .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
            .build()
    }

    // Rebinds whenever the lifecycle owner or the lens changes.
    LaunchedEffect(lifecycleOwner, lensFacing) {
        val cameraProvider = ProcessCameraProvider.awaitInstance(context)
        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(lensFacing)
            .build()
        if (!cameraProvider.hasCamera(cameraSelector)) return@LaunchedEffect
        cameraProvider.unbindAll()
        val camera = cameraProvider.bindToLifecycle(
            lifecycleOwner,
            cameraSelector,
            preview,
            imageCapture,
        )
        cameraControl = camera.cameraControl
        onImageCaptureReady(imageCapture)
    }

    // Keep the capture orientation in sync with the display.
    LaunchedEffect(configuration) {
        if (!view.isInEditMode) {
            val rotation = view.display?.rotation ?: Surface.ROTATION_0
            imageCapture.targetRotation = rotation
            preview.targetRotation = rotation
        }
    }

    surfaceRequest?.let { request ->
        CameraXViewfinder(
            surfaceRequest = request,
            coordinateTransformer = coordinateTransformer,
            modifier = modifier.pointerInput(request, cameraControl) {
                detectTapGestures { offset ->
                    val surfaceCoords = with(coordinateTransformer) { offset.transform() }
                    val factory = SurfaceOrientedMeteringPointFactory(
                        request.resolution.width.toFloat(),
                        request.resolution.height.toFloat(),
                    )
                    val point = factory.createPoint(surfaceCoords.x, surfaceCoords.y)
                    val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF)
                        .build()
                    cameraControl?.startFocusAndMetering(action)
                }
            },
        )
    }
}
// [END android_camerax_compose_preview]

// [START android_camerax_capture_photo]
/**
 * Captures a photo in memory. [executor] runs the callback: pass an
 * app-owned background executor, or `ContextCompat.getMainExecutor(context)`.
 */
fun takePhoto(
    imageCapture: ImageCapture,
    executor: Executor,
    lensFacing: Int,
    onPhoto: (Bitmap) -> Unit,
) {
    imageCapture.takePicture(
        executor,
        object : ImageCapture.OnImageCapturedCallback() {
            override fun onCaptureSuccess(image: ImageProxy) {
                // use {} closes the ImageProxy, which unblocks the next capture.
                image.use { proxy ->
                    val buffer = proxy.planes[0].buffer
                    val bytes = ByteArray(buffer.remaining())
                    buffer.get(bytes)
                    val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                        ?: return

                    // Rotate using the rotation that CameraX reports.
                    val matrix = Matrix()
                    matrix.postRotate(proxy.imageInfo.rotationDegrees.toFloat())
                    if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
                        matrix.postScale(-1f, 1f) // Mirror for front camera
                    }
                    onPhoto(
                        Bitmap.createBitmap(
                            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
                        )
                    )
                }
            }

            override fun onError(exception: ImageCaptureException) {
                Log.e("CameraX", "Capture failed: ${exception.message}", exception)
            }
        },
    )
}
// [END android_camerax_capture_photo]

// [START android_camerax_switch_cameras]
/**
 * Flips between the back and front cameras and rebinds. Returns the lens
 * that is now bound. Keeps the current lens on devices with only one camera.
 */
suspend fun switchCamera(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    currentLensFacing: Int,
): Int {
    val cameraProvider = ProcessCameraProvider.awaitInstance(context)
    val targetLensFacing =
        if (currentLensFacing == CameraSelector.LENS_FACING_BACK) {
            CameraSelector.LENS_FACING_FRONT
        } else {
            CameraSelector.LENS_FACING_BACK
        }
    val targetSelector = CameraSelector.Builder()
        .requireLensFacing(targetLensFacing)
        .build()
    if (!cameraProvider.hasCamera(targetSelector)) {
        return currentLensFacing
    }
    // Rebind with the same function used for the initial bind.
    bindCameraUseCases(context, lifecycleOwner, previewView, targetLensFacing)
    return targetLensFacing
}
// [END android_camerax_switch_cameras]
