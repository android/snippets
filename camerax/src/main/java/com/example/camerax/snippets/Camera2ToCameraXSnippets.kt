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

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.hardware.camera2.CaptureRequest
import android.util.Log
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceRequest
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import java.util.concurrent.Executor

@Composable
private fun InitializeCamera2Snippet() {
  // [START android_camerax_initialize_provider_camera2]
  val context = LocalContext.current
  val lifecycleOwner = LocalLifecycleOwner.current
  LaunchedEffect(context, lifecycleOwner) {
    val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    cameraProviderFuture.addListener(
      {
        val cameraProvider = cameraProviderFuture.get()

        val cameraSelector = CameraSelector.Builder()
          .requireLensFacing(CameraSelector.LENS_FACING_BACK)
          .build()

        val preview = Preview.Builder().build()
        val imageCapture = ImageCapture.Builder()
          .setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
          .build()

        // ImageAnalysis is common when migrating from Camera2 ImageReader
        val imageAnalysis = ImageAnalysis.Builder()
          .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
          .build()

        cameraProvider.unbindAll()

        val camera = cameraProvider.bindToLifecycle(
          lifecycleOwner,
          cameraSelector,
          preview,
          imageCapture,
          imageAnalysis
        )
      }, ContextCompat.getMainExecutor(context)
    )
  }
  // [END android_camerax_initialize_provider_camera2]
}

private fun ViewPreviewSnippet2(preview: Preview, previewView: androidx.camera.view.PreviewView) {
  // [START android_camerax_view_preview_camera2]
  preview.setSurfaceProvider(previewView.surfaceProvider)
  // [END android_camerax_view_preview_camera2]
}

@Composable
private fun ComposePreviewSnippet2() {
  // [START android_camerax_compose_preview_camera2]
  var surfaceRequest by remember { mutableStateOf<SurfaceRequest?>(null) }
  val preview = remember {
    Preview.Builder().build().apply {
      setSurfaceProvider { request -> surfaceRequest = request }
    }
  }
  // [END android_camerax_compose_preview_camera2]
}

private fun CapturePhotoSnippet2(imageCapture: ImageCapture, cameraExecutor: Executor, lensFacing: Int) {
  // [START android_camerax_capture_photo_camera2]
  imageCapture.takePicture(
    cameraExecutor,
    object : ImageCapture.OnImageCapturedCallback() {
      override fun onCaptureSuccess(image: ImageProxy) {
        val buffer = image.planes[0].buffer
        val bytes = ByteArray(buffer.remaining())
        buffer.get(bytes)
        val bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
        if (bitmap != null) {
          val matrix = Matrix()
          matrix.postRotate(image.imageInfo.rotationDegrees.toFloat())
          if (lensFacing == CameraSelector.LENS_FACING_FRONT) {
            matrix.postScale(-1f, 1f)
          }

          val rotatedBitmap = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true
          )
        }

        // MUST close proxy
        image.close()
      }

      override fun onError(exception: ImageCaptureException) {
        Log.e("CameraX", "Capture failed: ${exception.message}", exception)
      }
    }
  )
  // [END android_camerax_capture_photo_camera2]
}

private fun ImageAnalysisSnippet(imageAnalysis: ImageAnalysis, cameraExecutor: Executor) {
  // [START android_camerax_image_analysis]
  imageAnalysis.setAnalyzer(cameraExecutor) { imageProxy ->
    try {
      val rotationDegrees = imageProxy.imageInfo.rotationDegrees
      // Process image here (e.g., run object detection)
      // ...
    } finally {
      // MUST close the imageProxy to avoid blocking the pipeline
      imageProxy.close()
    }
  }
  // [END android_camerax_image_analysis]
}

@ExperimentalCamera2Interop
private fun Camera2InteropSnippet(imageCaptureBuilder: ImageCapture.Builder) {
  // [START android_camerax_camera2_interop]
  // Use Camera2Interop to set Camera2-specific capture options
  val extender = Camera2Interop.Extender(imageCaptureBuilder)
  extender.setCaptureRequestOption(
    CaptureRequest.CONTROL_AE_MODE,
    CaptureRequest.CONTROL_AE_MODE_OFF
  ).setCaptureRequestOption(
    CaptureRequest.FLASH_MODE,
    CaptureRequest.FLASH_MODE_TORCH
  )
  // [END android_camerax_camera2_interop]
}
