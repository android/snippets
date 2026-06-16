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

@file:android.annotation.SuppressLint("MissingPermission", "NewApi")
package com.example.camerax.snippets

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.Rect
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.media.Image
import android.os.Handler
import android.os.PowerManager
import android.util.Rational
import android.view.Display
import android.view.View
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.core.Camera
import androidx.camera.core.CameraControl
import androidx.camera.core.CameraEffect
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageInfo
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceProcessor
import androidx.camera.core.UseCase
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.core.takePicture
import androidx.camera.extensions.CameraExtensionsControl
import androidx.camera.extensions.ExtensionMode
import androidx.camera.extensions.ExtensionsManager
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.video.FileOutputOptions
import androidx.camera.video.PendingRecording
import androidx.camera.video.Recorder
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.common.util.concurrent.ListenableFuture
import com.google.mlkit.vision.face.FaceLandmark
import java.util.concurrent.Executor
import kotlinx.coroutines.guava.await
import kotlinx.coroutines.launch
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

private fun snippet_immutability_1(
    context: Context,
    recorder: Recorder,
    opts: FileOutputOptions,
    exec: Executor,
    listener: Consumer<VideoRecordEvent>
) {
  // [START android_camerax_skill_immutability_1]
  // WRONG
  run {
    val pending = recorder.prepareRecording(context, opts)
    pending.withAudioEnabled() // This returns a new instance which is ignored
    val active = pending.start(exec, listener)
  }
  
  // CORRECT
  run {
    val pending = recorder.prepareRecording(context, opts)
        .withAudioEnabled() // Chaining works
    val active = pending.start(exec, listener)
  }
  
  // ALSO CORRECT
  run {
    var pending = recorder.prepareRecording(context, opts)
    pending = pending.withAudioEnabled() // Reassignment
    val active = pending.start(exec, listener)
  }
  // [END android_camerax_skill_immutability_1]
}

private fun snippet_immutability_2(width: Int, height: Int, displayRotation: Int) {
  // [START android_camerax_skill_immutability_2]
  val viewport = ViewPort.Builder(Rational(width, height), displayRotation)
      .setScaleType(ViewPort.FILL_CENTER)
      .build()
  // [END android_camerax_skill_immutability_2]
}


private fun snippet_xr_1(xrSession: XrSession, frameTime: Long, eyeIndex: Int) {
  // [START android_camerax_skill_xr_1]
  // Example: Querying the spatial pose for the current camera frame
  val headPose = xrSession.getHeadPose(frameTime)
  val projectionMatrix = headPose.getProjectionMatrix(eyeIndex)
  // [END android_camerax_skill_xr_1]
}

private suspend fun snippet_low_light_1(
    context: Context, 
    cameraProvider: ProcessCameraProvider, 
    cameraSelector: CameraSelector, 
    lifecycleOwner: LifecycleOwner, 
    imageCapture: ImageCapture, 
    preview: Preview
) {
  // [START android_camerax_skill_low_light_1]
  // Use ListenableFuture.await() extension function for coroutine support
  val extensionsManager = ExtensionsManager.getInstanceAsync(context, cameraProvider).await()
  if (extensionsManager.isExtensionAvailable(cameraSelector, ExtensionMode.NIGHT)) {
      val nightSelector = extensionsManager.getExtensionEnabledCameraSelector(
          cameraSelector, ExtensionMode.NIGHT
      )
      cameraProvider.bindToLifecycle(lifecycleOwner, nightSelector, imageCapture, preview)
  }
  // [END android_camerax_skill_low_light_1]
}

private fun snippet_low_light_2() {
  // [START android_camerax_skill_low_light_2]
      val imageCapture = ImageCapture.Builder()
          .setPostviewEnabled(true)
          .build()
  // [END android_camerax_skill_low_light_2]
}

private suspend fun snippet_low_light_3(context: Context, cameraProvider: ProcessCameraProvider, camera: Camera, strength: Int) {
  // [START android_camerax_skill_low_light_3]
      // Set the strength of the active extension (e.g. NIGHT mode intensity)
      val extensionsManager = ExtensionsManager.getInstanceAsync(context, cameraProvider).await()
      val extensionsControl = extensionsManager.getCameraExtensionsControl(camera.cameraControl)
      extensionsControl?.setExtensionStrength(strength)
  // [END android_camerax_skill_low_light_3]
}

private suspend fun snippet_low_light_4(imageCapture: ImageCapture, outputOptions: ImageCapture.OutputFileOptions) {
  // [START android_camerax_skill_low_light_4]
      // Use the suspend extension function for takePicture to avoid callback boilerplate
      try {
          val result = imageCapture.takePicture(outputOptions)
          // Use result.savedUri or other fields
      } catch (e: ImageCaptureException) {
          // Handle capture failure
      }
  // [END android_camerax_skill_low_light_4]
}


private fun snippet_low_light_5(camera: Camera) {
  // [START android_camerax_skill_low_light_5]
      // Enable Low Light Boost (LLB) natively in CameraX 1.4+
      camera.cameraControl.enableLowLightBoostAsync(true)
  // [END android_camerax_skill_low_light_5]
}

private const val PREVIEW = 1
private const val VIDEO_CAPTURE = 2

private fun snippet_low_light_6(executor: Executor, llbSurfaceProcessor: SurfaceProcessor, preview: Preview, videoCapture: UseCase) {
  // [START android_camerax_skill_low_light_6]
      val effect = SimpleCameraEffect(
          CameraEffect.PREVIEW or CameraEffect.VIDEO_CAPTURE,
          executor,
          llbSurfaceProcessor
      ) { throw it }
      
      // Add to UseCaseGroup
      val useCaseGroup = UseCaseGroup.Builder()
          .addUseCase(preview)
          .addUseCase(videoCapture)
          .addEffect(effect)
          .build()
  // [END android_camerax_skill_low_light_6]
}

private fun snippet_effects_1(executor: Executor, useCaseGroupBuilder: UseCaseGroup.Builder) {
  // [START android_camerax_skill_effects_1]
  // 1. Create the CameraEffect
  // The targets bitmask can be any combination of PREVIEW, VIDEO_CAPTURE, or IMAGE_CAPTURE
  val targets = CameraEffect.PREVIEW or CameraEffect.VIDEO_CAPTURE or CameraEffect.IMAGE_CAPTURE
  
  val effect = SimpleCameraEffect(
      targets,
      executor,
      GrayscaleSurfaceProcessor() 
  ) { throw it }
  
  // 2. Apply to UseCaseGroup
  useCaseGroupBuilder.addEffect(effect)
  // [END android_camerax_skill_effects_1]
}


private fun snippet_mlkit_spatial_1(previewView: PreviewView, imageProxy: ImageProxy) {
  // [START android_camerax_skill_mlkit_spatial_1]
  val transform = previewView.viewPort?.let { viewPort ->
      // Use CameraX's built-in coordinate mapper
      viewPort.getTransformationMatrix(imageProxy.imageInfo.rotationDegrees)
  }
  // [END android_camerax_skill_mlkit_spatial_1]
}

private fun snippet_mlkit_spatial_2(landmark: FaceLandmark, analysisWidth: Float, screenWidth: Float, analysisHeight: Float, screenHeight: Float) {
  // [START android_camerax_skill_mlkit_spatial_2]
  // Example: Converting a Pose landmark to a Screen Coordinate
  val screenX = landmark.position.x / analysisWidth * screenWidth
  val screenY = landmark.position.y / analysisHeight * screenHeight
  // [END android_camerax_skill_mlkit_spatial_2]
}

private fun snippet_foldables_1(lifecycleScope: LifecycleCoroutineScope, lifecycle: Lifecycle, windowInfoTracker: WindowInfoTracker, activity: Activity, updateCameraLayout: (FoldingFeature?) -> Unit) {
  // [START android_camerax_skill_foldables_1]
  lifecycleScope.launch {
      lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
          windowInfoTracker.windowLayoutInfo(activity)
              .collect { layoutInfo ->
                  val displayFeature = layoutInfo.displayFeatures
                      .filterIsInstance<FoldingFeature>()
                      .firstOrNull()
                  
                  updateCameraLayout(displayFeature)
              }
      }
  }
  // [END android_camerax_skill_foldables_1]
}

private fun snippet_foldables_2(viewfinder: View, display: Display, preview: Preview) {
  // [START android_camerax_skill_foldables_2]
  val viewport = ViewPort.Builder(Rational(viewfinder.width, viewfinder.height), display.rotation)
      .setScaleType(ViewPort.FILL_CENTER)
      .build()
  
  val useCaseGroup = UseCaseGroup.Builder()
      .addUseCase(preview)
      .setViewPort(viewport)
      .build()
  // [END android_camerax_skill_foldables_2]
}


@android.annotation.SuppressLint("UnsafeOptInUsageError", "WrongConstant")
private fun snippet_thermals_1() {
  // [START android_camerax_skill_thermals_1]
  // In CameraX: Set the hint on your Use Case
  val preview = Preview.Builder()
      .setTargetName("Preview")
      .apply {
          Camera2Interop.Extender(this).setStreamUseCase(
              CameraMetadata.SCALER_AVAILABLE_STREAM_USE_CASES_VIDEO_CALL.toLong()
          )
      }
      .build()
  // [END android_camerax_skill_thermals_1]
}

private fun snippet_thermals_2(context: Context) {
  // [START android_camerax_skill_thermals_2]
  val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
  powerManager.addThermalStatusListener { status ->
      when (status) {
          PowerManager.THERMAL_STATUS_MODERATE -> {
              // Signal to UI: "Device is warming up"
          }
          PowerManager.THERMAL_STATUS_SEVERE -> {
              // ACTION: Reduce Frame Rate from 60fps to 30fps
              // ACTION: Disable HDR or High-Quality Post-processing
          }
          PowerManager.THERMAL_STATUS_CRITICAL -> {
              // ACTION: Close the camera session to prevent hardware damage
          }
      }
  }
  // [END android_camerax_skill_thermals_2]
}

private suspend fun snippet_testing_1(context: Context) {
  // [START android_camerax_skill_testing_1]
  // Use awaitInstance() extension function for coroutine-based provider retrieval
  val cameraProvider = ProcessCameraProvider.awaitInstance(context)
  // [END android_camerax_skill_testing_1]
}


private fun snippet_testing_2() {
  // [START android_camerax_skill_testing_2]
  // Create a Fake ImageProxy for ML Testing (Fakes over Mocks)
  val fakeImage = FakeImageProxy(w = 640, h = 480)
  
  // Feed the fake buffer into your analyzer
  // [END android_camerax_skill_testing_2]
}


private fun snippet_wear_os_1(context: Context, previewView: PreviewView) {
  // [START android_camerax_skill_wear_os_1]
  // Example: Sending a viewfinder frame to the watch
  val bitmap = previewView.bitmap // Capture current frame
  if (bitmap != null) {
      val compressed = compressToJpeg(bitmap, quality = 50)
      val request = PutDataMapRequest.create("/camera/preview").apply {
          dataMap.putAsset("image", Asset.createFromBytes(compressed))
      }
      Wearable.getDataClient(context).putDataItem(request.asPutDataRequest())
  }
  
  // [END android_camerax_skill_wear_os_1]
}

private fun snippet_wear_os_2(context: Context, nodeId: String) {
  // [START android_camerax_skill_wear_os_2]
  // Watch sends a trigger to the phone
  Wearable.getMessageClient(context).sendMessage(nodeId, "/camera/capture", null)
  // [END android_camerax_skill_wear_os_2]
}

// --- PLACEHOLDERS AND HELPERS ---

// Placeholder classes for XR (App-specific or 3rd party SDK)
private class XrSession { fun getHeadPose(t: Long): HeadPose = HeadPose() }
private class HeadPose { fun getProjectionMatrix(i: Int): FloatArray = FloatArray(16) }

// FakeImageProxy for tests
private class FakeImageProxy(private val w: Int, private val h: Int) : ImageProxy {
    override fun close() {}
    override fun getCropRect(): Rect = Rect(0, 0, w, h)
    override fun setCropRect(rect: Rect?) {}
    override fun getFormat(): Int = 0
    override fun getHeight(): Int = h
    override fun getWidth(): Int = w
    override fun getPlanes(): Array<ImageProxy.PlaneProxy> = emptyArray()
    override fun getImageInfo(): ImageInfo = mock(ImageInfo::class.java)
    @android.annotation.SuppressLint("UnsafeOptInUsageError")
    override fun getImage(): Image? = null
}

// Helper function for Wear OS
private fun compressToJpeg(bitmap: Bitmap, quality: Int): ByteArray {
    val stream = java.io.ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, quality, stream)
    return stream.toByteArray()
}

// Fake Extension for MLKit Spatial Blueprint
private fun ViewPort.getTransformationMatrix(i: Int): Matrix = Matrix()

// Stand-in Implementation for CameraEffect
private class SimpleCameraEffect(targets: Int, executor: Executor, processor: SurfaceProcessor, consumer: Consumer<Throwable>) : CameraEffect(targets, executor, processor, consumer)

// Placeholder for Media3-based Grayscale Processor
private class GrayscaleSurfaceProcessor : SurfaceProcessor {
    override fun onInputSurface(request: androidx.camera.core.SurfaceRequest) {}
    override fun onOutputSurface(request: androidx.camera.core.SurfaceOutput) {}
}
