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

package com.example.compose.snippets.camerax

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.util.Log
import android.view.Surface
import android.view.View
import androidx.camera.compose.CameraXViewfinder
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.viewfinder.compose.MutableCoordinateTransformer
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executor

fun InitializeCameraSnippet(context: Context, lifecycleOwner: LifecycleOwner) {
  // [START android_camerax_initialize_provider]
  val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
  cameraProviderFuture.addListener({
                                     val cameraProvider = cameraProviderFuture.get()

                                     val cameraSelector = CameraSelector.Builder()
                                       .requireLensFacing(CameraSelector.LENS_FACING_BACK)
                                       .build()

                                     val preview = Preview.Builder().build()
                                     val imageCapture = ImageCapture.Builder()
                                       .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                                       .build()

                                     cameraProvider.unbindAll() // Unbind before rebinding

                                     val camera = cameraProvider.bindToLifecycle(
                                       lifecycleOwner,
                                       cameraSelector,
                                       preview,
                                       imageCapture
                                     )
                                     val cameraControl = camera.cameraControl
                                   }, ContextCompat.getMainExecutor(context))
  // [END android_camerax_initialize_provider]
}

fun ViewPreviewSnippet(preview: Preview, previewView: androidx.camera.view.PreviewView) {
  // [START android_camerax_view_preview]
  preview.setSurfaceProvider(previewView.surfaceProvider)
  // [END android_camerax_view_preview]
}

fun ViewTapToFocusSnippet(previewView: androidx.camera.view.PreviewView, x: Float, y: Float, cameraControl: androidx.camera.core.CameraControl?) {
  // [START android_camerax_view_tap_to_focus]
  val factory = previewView.meteringPointFactory
  val point = factory.createPoint(x, y) // x, y from touch event
  val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF).build()
  cameraControl?.startFocusAndMetering(action)
  // [END android_camerax_view_tap_to_focus]
}

@Composable
fun ComposePreviewSnippet() {
  // [START android_camerax_compose_preview]
  var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }
  val preview = remember {
    Preview.Builder().build().apply {
      setSurfaceProvider { request -> surfaceRequest = request }
    }
  }
  // [END android_camerax_compose_preview]
}

@Composable
fun ComposeRenderViewfinderSnippet(surfaceRequest: SurfaceRequest?, coordinateTransformer: MutableCoordinateTransformer) {
  // [START android_camerax_compose_viewfinder]
  surfaceRequest?.let { request ->
    CameraXViewfinder(
      surfaceRequest = request,
      coordinateTransformer = coordinateTransformer,
      modifier = Modifier
    )
  }
  // [END android_camerax_compose_viewfinder]
}

@Composable
fun ComposeTapToFocusSnippet(coordinateTransformer: MutableCoordinateTransformer, offset: Offset, request: SurfaceRequest, cameraControl: androidx.camera.core.CameraControl?) {
  // [START android_camerax_compose_tap_to_focus]
  // Inside your tap gesture handler...
  val surfaceCoords = with(coordinateTransformer) { offset.transform() }
  val factory = SurfaceOrientedMeteringPointFactory(
    request.resolution.width.toFloat(),
    request.resolution.height.toFloat()
  )
  val point = factory.createPoint(surfaceCoords.x, surfaceCoords.y)
  val action = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF).build()
  cameraControl?.startFocusAndMetering(action)
  // [END android_camerax_compose_tap_to_focus]
}

@Composable
fun ComposeRotationSnippet(view: View, imageCapture: ImageCapture, preview: Preview, configuration: Any) {
  // [START android_camerax_compose_rotation]
  LaunchedEffect(configuration) {
    if (!view.isInEditMode) {
      val rotation = view.display?.rotation ?: Surface.ROTATION_0
      imageCapture.targetRotation = rotation
      preview.targetRotation = rotation
    }
  }
  // [END android_camerax_compose_rotation]
}

fun CapturePhotoSnippet(imageCapture: ImageCapture, cameraExecutor: Executor, lensFacing: Int) {
  // [START android_camerax_capture_photo]
  imageCapture.takePicture(
    cameraExecutor,
    object : ImageCapture.OnImageCapturedCallback() {
      override fun onCaptureSuccess(image: ImageProxy) {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)

        // Adjust rotation natively via ImageProxy
        val matrix = Matrix()
        matrix.postRotate(image.imageInfo.rotationDegrees.toFloat())
        if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
          matrix.postScale(-1f, 1f) // Mirror for front camera
        }

        val rotatedBitmap = Bitmap.createBitmap(
          bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
        )

        // MUST close proxy
        image.close()
      }

      override fun onError(exception: ImageCaptureException) {
        Log.e("CameraX", "Capture failed: ${exception.message}", exception)
      }
    }
  )
  // [END android_camerax_capture_photo]
}

fun SwitchCamerasSnippet() {
  var lensFacing = CameraSelector.LENS_FACING_BACK
  // [START android_camerax_switch_cameras]
  lensFacing = if (lensFacing == CameraSelector.LENS_FACING_BACK) {
    CameraSelector.LENS_FACING_FRONT
  } else {
    CameraSelector.LENS_FACING_BACK
  }
  // [END android_camerax_switch_cameras]
}
