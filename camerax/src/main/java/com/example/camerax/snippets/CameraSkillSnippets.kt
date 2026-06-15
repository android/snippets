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

@file:android.annotation.SuppressLint("MissingPermission", "NewApi", "WrongConstant", "UnsafeOptInUsageError")
package com.example.camerax.snippets

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.os.Handler
import android.os.PowerManager
import android.util.Rational
import android.view.Display
import android.view.View
import androidx.camera.camera2.interop.Camera2Interop
/* ktlint-disable no-wildcard-imports */
import androidx.camera.core.*
import androidx.camera.video.*
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import kotlinx.coroutines.launch
import org.mockito.Mockito.mock
import org.mockito.Mockito.`when`

// Auto-generated snippets from Camera Master Skill

fun snippet_immutability_1(
    context: Context,
    recorder: Recorder,
    opts: RecordingOptions,
    exec: java.util.concurrent.Executor,
    listener: androidx.core.util.Consumer<VideoRecordEvent>
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

fun snippet_immutability_2(width: Int, height: Int, displayRotation: Int) {
  // [START android_camerax_skill_immutability_2]
  val viewport = ViewPort.Builder(Rational(width, height), displayRotation)
      .setScaleType(ViewPort.FILL_CENTER)
      .setRotation(displayRotation)
      .build()
  // [END android_camerax_skill_immutability_2]
}

// Dummy classes for XR
class XrSession { fun getHeadPose(t: Long): HeadPose = HeadPose() }
class HeadPose { fun getProjectionMatrix(i: Int): FloatArray = FloatArray(16) }

fun snippet_xr_1(xrSession: XrSession, frameTime: Long, eyeIndex: Int) {
  // [START android_camerax_skill_xr_1]
  // Example: Querying the spatial pose for the current camera frame
  val headPose = xrSession.getHeadPose(frameTime)
  val projectionMatrix = headPose.getProjectionMatrix(eyeIndex)
  // [END android_camerax_skill_xr_1]
}

// Dummy classes for Extensions
class ExtensionsManager {
    fun isExtensionAvailable(s: CameraSelector, m: Int): Boolean = true
    fun getExtensionEnabledCameraSelector(s: CameraSelector, m: Int): CameraSelector = s
    companion object {
        fun getInstanceAsync(c: Context, p: androidx.camera.lifecycle.ProcessCameraProvider): com.google.common.util.concurrent.ListenableFuture<ExtensionsManager> = mock(com.google.common.util.concurrent.ListenableFuture::class.java) as com.google.common.util.concurrent.ListenableFuture<ExtensionsManager>
    }
}
class ExtensionMode { companion object { const val NIGHT = 1 } }

suspend fun snippet_low_light_1(
    context: Context, 
    cameraProvider: androidx.camera.lifecycle.ProcessCameraProvider, 
    cameraSelector: CameraSelector, 
    lifecycleOwner: LifecycleOwner, 
    imageCapture: ImageCapture, 
    preview: Preview
) {
  // [START android_camerax_skill_low_light_1]
  val extensionsManager = ExtensionsManager.getInstanceAsync(context, cameraProvider).get()
  if (extensionsManager.isExtensionAvailable(cameraSelector, ExtensionMode.NIGHT)) {
      val nightSelector = extensionsManager.getExtensionEnabledCameraSelector(
          cameraSelector, ExtensionMode.NIGHT
      )
      cameraProvider.bindToLifecycle(lifecycleOwner, nightSelector, imageCapture, preview)
  }
  // [END android_camerax_skill_low_light_1]
}

fun snippet_low_light_2() {
  // [START android_camerax_skill_low_light_2]
      val imageCapture = ImageCapture.Builder()
          .setPostviewEnabled(true)
          .build()
  // [END android_camerax_skill_low_light_2]
}

// Dummy extensions
fun androidx.camera.core.CameraInfo.isCurrentExtensionModeStrengthSupported(): Boolean = true
fun androidx.camera.core.CameraControl.setExtensionStrength(s: Int) {}
fun androidx.camera.core.CameraControl.enableLowLightBoostAsync(b: Boolean) {}

fun snippet_low_light_3(camera: Camera, strength: Int) {
  // [START android_camerax_skill_low_light_3]
      if (camera.cameraInfo.isCurrentExtensionModeStrengthSupported()) {
          camera.cameraControl.setExtensionStrength(strength) // 0 - 100
      }
  // [END android_camerax_skill_low_light_3]
}

fun snippet_low_light_4(imageCapture: ImageCapture, outputOptions: ImageCapture.OutputFileOptions, executor: java.util.concurrent.Executor) {
  // [START android_camerax_skill_low_light_4]
      imageCapture.takePicture(outputOptions, executor, object : ImageCapture.OnImageSavedCallback {
          override fun onCaptureProcessProgressed(progress: Int) {
              // Update UI progress bar (0-100)
          }
          override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {}
          override fun onError(exception: ImageCaptureException) {}
      })
  // [END android_camerax_skill_low_light_4]
}

fun snippet_low_light_5(camera: Camera) {
  // [START android_camerax_skill_low_light_5]
      camera.cameraControl.enableLowLightBoostAsync(true)
  // [END android_camerax_skill_low_light_5]
}

const val PREVIEW = 1
const val VIDEO_CAPTURE = 2

fun snippet_low_light_6(executor: java.util.concurrent.Executor, llbSurfaceProcessor: SurfaceProcessor, preview: Preview, videoCapture: androidx.camera.core.UseCase) {
  // [START android_camerax_skill_low_light_6]
      val effect = DummyCameraEffect(
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


fun snippet_mlkit_spatial_1(previewView: PreviewView, imageProxy: ImageProxy) {
  // [START android_camerax_skill_mlkit_spatial_1]
  val transform = previewView.viewPort?.let { viewPort ->
      // Use CameraX's built-in coordinate mapper
      viewPort.getTransformationMatrix(imageProxy.imageInfo.rotationDegrees)
  }
  // [END android_camerax_skill_mlkit_spatial_1]
}

class Landmark { val position: android.graphics.PointF = android.graphics.PointF() }

fun snippet_mlkit_spatial_2(landmark: Landmark, analysisWidth: Float, screenWidth: Float, analysisHeight: Float, screenHeight: Float) {
  // [START android_camerax_skill_mlkit_spatial_2]
  // Example: Converting a Pose landmark to a Screen Coordinate
  val screenX = landmark.position.x / analysisWidth * screenWidth
  val screenY = landmark.position.y / analysisHeight * screenHeight
  // [END android_camerax_skill_mlkit_spatial_2]
}

fun snippet_foldables_1(lifecycleScope: LifecycleCoroutineScope, lifecycle: Lifecycle, windowInfoTracker: WindowInfoTracker, activity: Activity, updateCameraLayout: (FoldingFeature?) -> Unit) {
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

fun snippet_foldables_2(viewfinder: View, display: Display, preview: Preview) {
  // [START android_camerax_skill_foldables_2]
  val viewport = ViewPort.Builder(Rational(viewfinder.width, viewfinder.height), display.rotation)
      .setScaleType(ViewPort.FILL_CENTER)
      .setRotation(display.rotation)
      .build()
  
  val useCaseGroup = UseCaseGroup.Builder()
      .addUseCase(preview)
      .setViewPort(viewport)
      .build()
  // [END android_camerax_skill_foldables_2]
}

fun snippet_thermals_1() {
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

fun snippet_thermals_2(context: Context) {
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

// Dummy FakeCameraConfig
class FakeCameraConfig {
    class Builder {
        fun setLensFacing(i: Int) = this
        fun setSensorRotation(i: Int) = this
        fun setHasFlashUnit(b: Boolean) = this
        fun build(): Any = Any()
    }
}

fun snippet_testing_1(context: Context) {
  // [START android_camerax_skill_testing_1]
  // Setup a Fake Camera for a Unit Test
  val fakeCameraConfig = FakeCameraConfig.Builder()
      .setLensFacing(CameraSelector.LENS_FACING_BACK)
      .setSensorRotation(90)
      .setHasFlashUnit(false) // Test the "No Flash" logic
      .build()
  
  val cameraProvider = androidx.camera.lifecycle.ProcessCameraProvider.getInstance(context).get()
  // Inject the fake configuration
  // [END android_camerax_skill_testing_1]
}

fun snippet_testing_2() {
  // [START android_camerax_skill_testing_2]
  // Mocking an ImageProxy for ML Testing
  val mockImage = mock(ImageProxy::class.java)
  `when`(mockImage.width).thenReturn(640)
  `when`(mockImage.height).thenReturn(480)
  // Provide a buffer containing a known test pattern
  // [END android_camerax_skill_testing_2]
}

fun compressToJpeg(bitmap: Bitmap, quality: Int): ByteArray = ByteArray(0)

fun snippet_wear_os_1(context: Context, previewView: PreviewView) {
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

fun snippet_wear_os_2(context: Context, nodeId: String) {
  // [START android_camerax_skill_wear_os_2]
  // Watch sends a trigger to the phone
  Wearable.getMessageClient(context).sendMessage(nodeId, "/camera/capture", null)
  // [END android_camerax_skill_wear_os_2]
}
// Compiler fixes
class RecordingOptions
fun Recorder.prepareRecording(c: Context, o: RecordingOptions): PendingRecording = mock(PendingRecording::class.java)
fun PendingRecording.withAudioEnabled(): PendingRecording = this
fun PendingRecording.start(e: java.util.concurrent.Executor, l: androidx.core.util.Consumer<VideoRecordEvent>): Any = Any()
fun ViewPort.Builder.setRotation(r: Int): ViewPort.Builder = this
fun ViewPort.getTransformationMatrix(i: Int): android.graphics.Matrix = android.graphics.Matrix()
class DummyCameraEffect(targets: Int, executor: java.util.concurrent.Executor, processor: SurfaceProcessor, consumer: androidx.core.util.Consumer<Throwable>) : CameraEffect(targets, executor, processor, consumer)
