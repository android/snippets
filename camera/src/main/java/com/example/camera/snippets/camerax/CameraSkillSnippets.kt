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

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.RectF
import android.hardware.camera2.CameraMetadata
import android.os.Build
import android.os.PowerManager
import android.provider.MediaStore
import android.util.Log
import android.util.Rational
import android.view.Surface
import android.view.View
import androidx.activity.ComponentActivity
import androidx.annotation.OptIn
import androidx.annotation.RequiresApi
import androidx.camera.camera2.interop.Camera2Interop
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.Camera
import androidx.camera.core.CameraEffect
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.LowLightBoostState
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceProcessor
import androidx.camera.core.UseCaseGroup
import androidx.camera.core.ViewPort
import androidx.camera.core.takePicture
import androidx.camera.extensions.ExtensionMode
import androidx.camera.extensions.ExtensionSessionConfig
import androidx.camera.extensions.ExtensionsManager
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.lifecycle.awaitInstance
import androidx.camera.mlkit.vision.MlKitAnalyzer
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.camera.view.TransformExperimental
import androidx.camera.view.transform.CoordinateTransform
import androidx.camera.view.transform.ImageProxyTransformFactory
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.Wearable
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutionException
import java.util.concurrent.Executor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout

// Replaces android_camerax_skill_immutability_1.
// [START android_camerax_skill_recording_1]
fun buildVideoCapture(): VideoCapture<Recorder> {
    // CameraX builders mutate and return themselves. Chaining works, and
    // reassigning the result is optional.
    val recorder = Recorder.Builder()
        .setQualitySelector(
            QualitySelector.from(
                Quality.FHD,
                FallbackStrategy.lowerQualityOrHigherThan(Quality.SD),
            )
        )
        .build()
    return VideoCapture.withOutput(recorder)
}
// [END android_camerax_skill_recording_1]

// [START android_camerax_skill_recording_2]
/** Starts a recording to MediaStore. Call [Recording.stop] to finish it. */
fun startRecording(
    context: Context,
    videoCapture: VideoCapture<Recorder>,
    onRecordingChanged: (isRecording: Boolean) -> Unit,
): Recording {
    val contentValues = ContentValues().apply {
        put(MediaStore.MediaColumns.DISPLAY_NAME, "CameraX-${System.currentTimeMillis()}")
        put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
    }
    val outputOptions = MediaStoreOutputOptions.Builder(
        context.contentResolver,
        MediaStore.Video.Media.EXTERNAL_CONTENT_URI,
    ).setContentValues(contentValues).build()

    val pendingRecording = videoCapture.output.prepareRecording(context, outputOptions)
    // withAudioEnabled throws SecurityException without RECORD_AUDIO, and
    // IllegalStateException if the Recorder doesn't support audio.
    if (ContextCompat.checkSelfPermission(context, Manifest.permission.RECORD_AUDIO) ==
        PackageManager.PERMISSION_GRANTED
    ) {
        pendingRecording.withAudioEnabled()
    }

    // Recording starts asynchronously: update the UI from events, not here.
    return pendingRecording.start(ContextCompat.getMainExecutor(context)) { event ->
        when (event) {
            is VideoRecordEvent.Start -> onRecordingChanged(true)
            is VideoRecordEvent.Finalize -> {
                onRecordingChanged(false)
                if (event.hasError()) {
                    Log.e("CameraX", "Recording failed: error ${event.error}", event.cause)
                } else {
                    Log.d("CameraX", "Saved ${event.outputResults.outputUri}")
                }
            }
        }
    }
}
// [END android_camerax_skill_recording_2]

// [START android_camerax_skill_low_light_1]
/** Binds Night mode with ExtensionSessionConfig (CameraX 1.6.0 and higher). */
suspend fun bindNightMode(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    cameraProvider: ProcessCameraProvider,
    preview: Preview,
    imageCapture: ImageCapture,
): Boolean {
    // Suspend version of getInstanceAsync (CameraX 1.6.0 and higher).
    val extensionsManager = ExtensionsManager.getInstance(context, cameraProvider)
    val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    if (!extensionsManager.isExtensionAvailable(cameraSelector, ExtensionMode.NIGHT)) {
        return false // Bind without the extension instead.
    }
    // ImageAnalysis isn't supported in extension sessions.
    val config = ExtensionSessionConfig.Builder(ExtensionMode.NIGHT, extensionsManager)
        .addUseCase(preview)
        .addUseCase(imageCapture)
        .build()
    cameraProvider.unbindAll()
    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, config)
    return true
}
// [END android_camerax_skill_low_light_1]

// [START android_camerax_skill_low_light_3]
/** Sets the active extension's strength, from 0 to 100. */
suspend fun setExtensionStrength(
    context: Context,
    cameraProvider: ProcessCameraProvider,
    camera: Camera,
    strength: Int,
) {
    val extensionsManager = ExtensionsManager.getInstance(context, cameraProvider)
    // Null when the bound camera has no active extension.
    extensionsManager.getCameraExtensionsControl(camera.cameraControl)
        ?.setExtensionStrength(strength)
}
// [END android_camerax_skill_low_light_3]

// [START android_camerax_skill_low_light_5]
/**
 * Enables AE-mode Low Light Boost. Requires CameraX 1.5.0 or higher,
 * Android 15 (API level 35) or higher, and device support.
 */
fun enableLowLightBoost(
    camera: Camera,
    lifecycleOwner: LifecycleOwner,
    executor: Executor,
): Boolean {
    // lowLightBoostState is always OFF on unsupported devices, so it can't
    // be used as the availability check.
    if (!camera.cameraInfo.isLowLightBoostSupported) return false

    val future = camera.cameraControl.enableLowLightBoostAsync(true)
    future.addListener({
        try {
            future.get()
        } catch (e: ExecutionException) {
            // IllegalStateException: unsupported or a settings conflict.
            Log.w("CameraX", "Low Light Boost not enabled", e.cause)
        }
    }, executor)

    camera.cameraInfo.lowLightBoostState.observe(lifecycleOwner) { state ->
        val isBoosting = state == LowLightBoostState.ACTIVE
        Log.d("CameraX", "Low Light Boost active: $isBoosting")
    }
    return true
}
// [END android_camerax_skill_low_light_5]

// [START android_camerax_skill_low_light_6]
/** CameraEffect's constructors are protected, so subclass it. */
class LowLightBoostEffect(
    surfaceProcessor: SurfaceProcessor,
    executor: Executor,
    onError: Consumer<Throwable>,
) : CameraEffect(
    CameraEffect.PREVIEW or CameraEffect.VIDEO_CAPTURE,
    executor,
    surfaceProcessor,
    onError,
)

fun buildLowLightBoostUseCaseGroup(
    preview: Preview,
    videoCapture: VideoCapture<Recorder>,
    llbSurfaceProcessor: SurfaceProcessor,
    executor: Executor,
): UseCaseGroup {
    val effect = LowLightBoostEffect(llbSurfaceProcessor, executor) { error ->
        Log.e("CameraX", "Low Light Boost effect failed", error)
    }
    // Bind the group with cameraProvider.bindToLifecycle(owner, selector, group).
    return UseCaseGroup.Builder()
        .addUseCase(preview)
        .addUseCase(videoCapture)
        .addEffect(effect)
        .build()
}
// [END android_camerax_skill_low_light_6]

// [START android_camerax_skill_mlkit_spatial_1]
fun startBarcodeScanning(
    context: Context,
    lifecycleOwner: LifecycleOwner,
    previewView: PreviewView,
    onBarcodes: (List<Barcode>) -> Unit,
) {
    val scanner = BarcodeScanning.getClient()
    val executor = ContextCompat.getMainExecutor(context)
    val controller = LifecycleCameraController(context)

    // COORDINATE_SYSTEM_VIEW_REFERENCED: MlKitAnalyzer maps every result to
    // PreviewView coordinates, including rotation, crop, and mirroring.
    controller.setImageAnalysisAnalyzer(
        executor,
        MlKitAnalyzer(
            listOf(scanner),
            ImageAnalysis.COORDINATE_SYSTEM_VIEW_REFERENCED,
            executor,
        ) { result ->
            onBarcodes(result.getValue(scanner).orEmpty())
        },
    )
    controller.bindToLifecycle(lifecycleOwner)
    previewView.controller = controller
}
// [END android_camerax_skill_mlkit_spatial_1]

// [START android_camerax_skill_mlkit_spatial_3]
/**
 * Maps a Rect from analysis-buffer coordinates to PreviewView coordinates
 * when you bind ImageAnalysis yourself with ProcessCameraProvider. Call on
 * the main thread. Returns null until the preview is streaming.
 */
@OptIn(TransformExperimental::class)
fun mapRectToPreview(imageProxy: ImageProxy, previewView: PreviewView, box: Rect): RectF? {
    val target = previewView.outputTransform ?: return null
    val source = ImageProxyTransformFactory().getOutputTransform(imageProxy)
    val rect = RectF(box)
    CoordinateTransform(source, target).mapRect(rect)
    return rect
}
// [END android_camerax_skill_mlkit_spatial_3]

// [START android_camerax_skill_low_light_4]
/**
 * Captures to a file and reports progress for long exposures. The callbacks
 * fire only when ImageCapture.getImageCaptureCapabilities(cameraInfo) reports
 * support.
 */
suspend fun captureWithProgress(
    imageCapture: ImageCapture,
    outputOptions: ImageCapture.OutputFileOptions,
    onProgress: (percent: Int) -> Unit,
    onPostview: (Bitmap) -> Unit,
) {
    try {
        val result = imageCapture.takePicture(
            outputOptions,
            onCaptureProcessProgressed = onProgress,
            onPostviewBitmapAvailable = onPostview,
        )
        Log.d("CameraX", "Saved ${result.savedUri}")
    } catch (e: ImageCaptureException) {
        Log.e("CameraX", "Capture failed", e)
    }
}
// [END android_camerax_skill_low_light_4]

// [START android_camerax_skill_foldables_1]
fun observeFoldingFeature(
    activity: ComponentActivity,
    onFoldChanged: (FoldingFeature?) -> Unit,
) {
    activity.lifecycleScope.launch {
        activity.repeatOnLifecycle(Lifecycle.State.STARTED) {
            WindowInfoTracker.getOrCreate(activity)
                .windowLayoutInfo(activity)
                .collect { layoutInfo ->
                    val foldingFeature = layoutInfo.displayFeatures
                        .filterIsInstance<FoldingFeature>()
                        .firstOrNull()
                    onFoldChanged(foldingFeature)
                }
        }
    }
}
// [END android_camerax_skill_foldables_1]

// [START android_camerax_skill_foldables_2]
/** Rebuild the group and rebind after a fold changes the viewfinder size. */
fun buildUseCaseGroup(
    viewfinder: View,
    preview: Preview,
    imageCapture: ImageCapture,
): UseCaseGroup {
    val rotation = viewfinder.display?.rotation ?: Surface.ROTATION_0
    val viewPort = ViewPort.Builder(Rational(viewfinder.width, viewfinder.height), rotation)
        .setScaleType(ViewPort.FILL_CENTER)
        .build()
    return UseCaseGroup.Builder()
        .addUseCase(preview)
        .addUseCase(imageCapture)
        .setViewPort(viewPort)
        .build()
}
// [END android_camerax_skill_foldables_2]

// [START android_camerax_skill_mlkit_spatial_2]
/** Converts a landmark position in the analysis frame to screen pixels. */
fun toScreenPoint(
    landmarkX: Float,
    landmarkY: Float,
    analysisWidth: Int,
    analysisHeight: Int,
    screenWidth: Int,
    screenHeight: Int,
): Pair<Float, Float> {
    // Normalize to 0.0-1.0 first, then scale to the screen.
    val normalizedX = landmarkX / analysisWidth
    val normalizedY = landmarkY / analysisHeight
    return normalizedX * screenWidth to normalizedY * screenHeight
}
// [END android_camerax_skill_mlkit_spatial_2]

// [START android_camerax_skill_testing_1]
/** In an instrumented test: wait for the provider with an explicit timeout. */
suspend fun awaitCameraProviderForTest(context: Context): ProcessCameraProvider =
    withTimeout(10_000) { ProcessCameraProvider.awaitInstance(context) }
// [END android_camerax_skill_testing_1]

// [START android_camerax_skill_thermals_2]
/** Requires Android 10 (API level 29) or higher. */
@RequiresApi(Build.VERSION_CODES.Q)
fun registerThermalListener(context: Context): PowerManager.OnThermalStatusChangedListener {
    val powerManager = context.getSystemService(Context.POWER_SERVICE) as PowerManager
    val listener = PowerManager.OnThermalStatusChangedListener { status ->
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
    powerManager.addThermalStatusListener(listener)
    return listener // Remove it with removeThermalStatusListener when done.
}
// [END android_camerax_skill_thermals_2]

// [START android_camerax_skill_wear_os_1]
/** Phone side: sends a compressed viewfinder frame to the watch. */
suspend fun sendPreviewFrame(context: Context, previewView: PreviewView) {
    val bitmap = previewView.bitmap ?: return // Capture current frame on the main thread
    val compressed = withContext(Dispatchers.Default) { // Compress off the main thread
        ByteArrayOutputStream().use { stream ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, stream)
            stream.toByteArray()
        }
    }
    val request = PutDataMapRequest.create("/camera/preview").apply {
        dataMap.putAsset("image", Asset.createFromBytes(compressed))
    }
    Wearable.getDataClient(context).putDataItem(request.asPutDataRequest())
}
// [END android_camerax_skill_wear_os_1]

// [START android_camerax_skill_wear_os_2]
/** Watch side: sends a capture trigger to the phone node. */
fun sendCaptureTrigger(context: Context, phoneNodeId: String) {
    Wearable.getMessageClient(context).sendMessage(phoneNodeId, "/camera/capture", null)
}
// [END android_camerax_skill_wear_os_2]

// [START android_camerax_skill_low_light_2]
fun buildPostviewImageCapture(): ImageCapture =
    ImageCapture.Builder()
        .setPostviewEnabled(true)
        .build()
// [END android_camerax_skill_low_light_2]

// [START android_camerax_skill_thermals_1]
/** Hints the stream use case to the camera HAL (Android 13+, API level 33). */
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@OptIn(ExperimentalCamera2Interop::class)
fun buildVideoCallPreview(): Preview {
    val builder = Preview.Builder().setTargetName("Preview")
    @Suppress("DEPRECATION") // Deprecated in CameraX 1.7.0 in favor of camera2Interop { }.
    Camera2Interop.Extender(builder).setStreamUseCase(
        CameraMetadata.SCALER_AVAILABLE_STREAM_USE_CASES_VIDEO_CALL.toLong()
    )
    return builder.build()
}
// [END android_camerax_skill_thermals_1]
