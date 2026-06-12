package com.example.camerax.snippets

import androidx.camera.core.*
import androidx.camera.video.*
import android.content.Context
import android.graphics.Rect
import android.util.Log

// Auto-generated snippets from Camera Master Skill

fun snippet_immutability_1() {
  // [START android_camerax_skill_immutability_1]
  // WRONG
  val pending = recorder.prepareRecording(context, opts)
  pending.withAudioEnabled() // This returns a new instance which is ignored
  val active = pending.start(exec, listener)
  
  // CORRECT
  val pending = recorder.prepareRecording(context, opts)
      .withAudioEnabled() // Chaining works
  val active = pending.start(exec, listener)
  
  // ALSO CORRECT
  var pending = recorder.prepareRecording(context, opts)
  pending = pending.withAudioEnabled() // Reassignment
  val active = pending.start(exec, listener)
  
  // [END android_camerax_skill_immutability_1]
}

fun snippet_immutability_2() {
  // [START android_camerax_skill_immutability_2]
  val viewport = ViewPort.Builder(Rational(width, height))
      .setScaleType(ViewPort.FILL_CENTER)
      .setRotation(displayRotation)
      .build()
  
  // [END android_camerax_skill_immutability_2]
}

fun snippet_xr_1() {
  // [START android_camerax_skill_xr_1]
  // Example: Querying the spatial pose for the current camera frame
  val headPose = xrSession.getHeadPose(frameTime)
  val projectionMatrix = headPose.getProjectionMatrix(eyeIndex)
  
  // [END android_camerax_skill_xr_1]
}

fun snippet_low_light_1() {
  // [START android_camerax_skill_low_light_1]
  val extensionsManager = ExtensionsManager.getInstanceAsync(context, cameraProvider).await()
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

fun snippet_low_light_3() {
  // [START android_camerax_skill_low_light_3]
      if (camera.cameraInfo.isCurrentExtensionModeStrengthSupported) {
          camera.cameraControl.setExtensionStrength(strength) // 0 - 100
      }
      
  // [END android_camerax_skill_low_light_3]
}

fun snippet_low_light_4() {
  // [START android_camerax_skill_low_light_4]
      imageCapture.takePicture(outputOptions, executor, object : OnImageSavedCallback {
          override fun onCaptureProcessProgressed(progress: Int) {
              // Update UI progress bar (0-100)
          }
      })
      
  // [END android_camerax_skill_low_light_4]
}

fun snippet_low_light_5() {
  // [START android_camerax_skill_low_light_5]
      camera.cameraControl.enableLowLightBoostAsync(true)
      
  // [END android_camerax_skill_low_light_5]
}

fun snippet_low_light_6() {
  // [START android_camerax_skill_low_light_6]
      val effect = CameraEffect(
          PREVIEW or VIDEO_CAPTURE,
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

fun snippet_external_1() {
  // [START android_camerax_skill_external_1]
  val cameraSelector = CameraSelector.Builder()
      .requireLensFacing(CameraSelector.LENS_FACING_EXTERNAL)
      .build()
  
  // [END android_camerax_skill_external_1]
}

fun snippet_external_2() {
  // [START android_camerax_skill_external_2]
  val hasFlash = cameraInfo.hasFlashUnit()
  if (hasFlash) {
      cameraControl.enableTorch(true)
  } else {
      // Gracefully disable flash UI/buttons
  }
  
  // [END android_camerax_skill_external_2]
}

fun snippet_external_3() {
  // [START android_camerax_skill_external_3]
  val manager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
  manager.registerAvailabilityCallback(object : CameraManager.AvailabilityCallback() {
      override fun onCameraAvailable(cameraId: String) {
          // Check if this is the external camera and reconnect
      }
  
      override fun onCameraUnavailable(cameraId: String) {
          // Fallback to internal camera if the external one is pulled
      }
  }, handler)
  
  // [END android_camerax_skill_external_3]
}

fun snippet_mlkit_spatial_1() {
  // [START android_camerax_skill_mlkit_spatial_1]
  val transform = previewView.viewPort?.let { viewPort ->
      // Use CameraX's built-in coordinate mapper
      viewPort.getTransformationMatrix(imageProxy.imageInfo.rotationDegrees)
  }
  
  // [END android_camerax_skill_mlkit_spatial_1]
}

fun snippet_mlkit_spatial_2() {
  // [START android_camerax_skill_mlkit_spatial_2]
  // Example: Converting a Pose landmark to a Screen Coordinate
  val screenX = landmark.position.x / analysisWidth * screenWidth
  val screenY = landmark.position.y / analysisHeight * screenHeight
  
  // [END android_camerax_skill_mlkit_spatial_2]
}

fun snippet_foldables_1() {
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

fun snippet_foldables_2() {
  // [START android_camerax_skill_foldables_2]
  val viewport = ViewPort.Builder(Rational(viewfinder.width, viewfinder.height))
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
              CameraMetadata.SCALER_AVAILABLE_STREAM_USE_CASES_VIDEO_CALL
          )
      }
      .build()
  
  // [END android_camerax_skill_thermals_1]
}

fun snippet_thermals_2() {
  // [START android_camerax_skill_thermals_2]
  val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
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

fun snippet_testing_1() {
  // [START android_camerax_skill_testing_1]
  // Setup a Fake Camera for a Unit Test
  val fakeCameraConfig = FakeCameraConfig.Builder()
      .setLensFacing(CameraSelector.LENS_FACING_BACK)
      .setSensorRotation(90)
      .setHasFlashUnit(false) // Test the "No Flash" logic
      .build()
  
  val cameraProvider = ProcessCameraProvider.getInstance(context).get()
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

fun snippet_wear_os_1() {
  // [START android_camerax_skill_wear_os_1]
  // Example: Sending a viewfinder frame to the watch
  val bitmap = previewView.bitmap // Capture current frame
  val compressed = compressToJpeg(bitmap, quality = 50)
  val request = PutDataMapRequest.create("/camera/preview").apply {
      dataMap.putAsset("image", Asset.createFromBytes(compressed))
  }
  Wearable.getDataClient(context).putDataItem(request.asPutDataRequest())
  
  // [END android_camerax_skill_wear_os_1]
}

fun snippet_wear_os_2() {
  // [START android_camerax_skill_wear_os_2]
  // Watch sends a trigger to the phone
  Wearable.getMessageClient(context).sendMessage(nodeId, "/camera/capture", null)
  
  // [END android_camerax_skill_wear_os_2]
}

