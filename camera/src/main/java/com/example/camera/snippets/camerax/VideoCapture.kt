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
import android.hardware.camera2.CameraMetadata
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.camera2.interop.Camera2CameraInfo
import androidx.camera.camera2.interop.ExperimentalCamera2Interop
import androidx.camera.core.CameraSelector
import androidx.camera.core.MirrorMode.MIRROR_MODE_ON_FRONT_ONLY
import androidx.camera.core.Preview
import androidx.camera.core.UseCase
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.FallbackStrategy
import androidx.camera.video.MediaStoreOutputOptions
import androidx.camera.video.Quality
import androidx.camera.video.QualitySelector
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.camera.video.VideoRecordEvent
import androidx.core.content.ContextCompat
import androidx.core.util.Consumer
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor

private const val TAG = "VideoCapture"

private fun qualitySelectorSnippet() {
    // [START android_camerax_video_capture_quality_selector]
    val qualitySelector = QualitySelector.fromOrderedList(
             listOf(Quality.UHD, Quality.FHD, Quality.HD, Quality.SD),
             FallbackStrategy.lowerQualityOrHigherThan(Quality.SD))
    // [END android_camerax_video_capture_quality_selector]
}

private class VideoCapabilitiesActivity : AppCompatActivity() {
    private lateinit var viewBinding: Any
    private lateinit var cameraProvider: ProcessCameraProvider

    private fun Quality.qualityToString(): String {
        return when (this) {
            Quality.UHD -> "UHD"
            Quality.FHD -> "FHD"
            Quality.HD -> "HD"
            Quality.SD -> "SD"
            else -> throw IllegalArgumentException()
        }
    }

    @androidx.annotation.OptIn(ExperimentalCamera2Interop::class)
    private fun queryCapabilitiesSnippet(context: Context) {
        val simpleQualityListView = android.widget.ListView(context)
        // [START android_camerax_video_capture_query_capabilities]
        val cameraInfo = cameraProvider.availableCameraInfos.filter {
            Camera2CameraInfo
            .from(it)
            .getCameraCharacteristic(CameraCharacteristics.LENS_FACING) == CameraMetadata.LENS_FACING_BACK
        }

        val supportedQualities = QualitySelector.getSupportedQualities(cameraInfo[0])
        val filteredQualities = (arrayListOf(Quality.UHD, Quality.FHD, Quality.HD, Quality.SD) as List<Quality>)
                               .filter { supportedQualities.contains(it) }

        // Use a simple ListView with the id of simple_quality_list_view
        simpleQualityListView.apply {
            adapter = ArrayAdapter(context,
                                   android.R.layout.simple_list_item_1,
                                   filteredQualities.map { it.qualityToString() })

            // Set up the user interaction to manually show or hide the system UI.
            setOnItemClickListener { _, _, position, _ ->
                // Inside View.OnClickListener,
                // convert Quality.* constant to QualitySelector
                val qualitySelector = QualitySelector.from(filteredQualities[position])

                // Create a new Recorder/VideoCapture for the new quality
                // and bind to lifecycle
                val recorder = Recorder.Builder()
                    .setQualitySelector(qualitySelector).build()

                 // ...
            }
        }

        // A helper function to translate Quality to a string
        fun Quality.qualityToString() : String {
            return when (this) {
                Quality.UHD -> "UHD"
                Quality.FHD -> "FHD"
                Quality.HD -> "HD"
                Quality.SD -> "SD"
                else -> throw IllegalArgumentException()
            }
        }
        // [END android_camerax_video_capture_query_capabilities]
    }
}

private class VideoCaptureBindActivity : AppCompatActivity() {
    private lateinit var cameraExecutor: Executor
    private lateinit var qualitySelector: QualitySelector
    private lateinit var cameraProvider: ProcessCameraProvider
    private lateinit var preview: Preview

    private fun bindVideoCaptureSnippet() {
        // [START android_camerax_video_capture_bind]
        val recorder = Recorder.Builder()
            .setExecutor(cameraExecutor).setQualitySelector(qualitySelector)
            .build()
        val videoCapture = VideoCapture.withOutput(recorder)

        try {
            // Bind use cases to camera
            cameraProvider.bindToLifecycle(
                    this, CameraSelector.DEFAULT_BACK_CAMERA, preview, videoCapture)
        } catch(exc: Exception) {
            Log.e(TAG, "Use case binding failed", exc)
        }
        // [END android_camerax_video_capture_bind]
    }
}

private class MediaStoreRecordActivity : AppCompatActivity() {
    private lateinit var videoCapture: VideoCapture<Recorder>
    private lateinit var captureListener: Consumer<VideoRecordEvent>
    private val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"

    private fun recordSnippet(context: Context): Recording {
        // [START android_camerax_video_capture_mediastore]
        // Create MediaStoreOutputOptions for our recorder
        val name = "CameraX-recording-" +
                SimpleDateFormat(FILENAME_FORMAT, Locale.US)
                        .format(System.currentTimeMillis()) + ".mp4"
        val contentValues = ContentValues().apply {
           put(MediaStore.Video.Media.DISPLAY_NAME, name)
        }
        val mediaStoreOutput = MediaStoreOutputOptions.Builder(this.contentResolver,
                                      MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
                                      .setContentValues(contentValues)
                                      .build()

        // 2. Configure Recorder and Start recording to the mediaStoreOutput.
        val recording = videoCapture.output
                        .prepareRecording(context, mediaStoreOutput)
                        .withAudioEnabled()
                        .start(ContextCompat.getMainExecutor(this), captureListener)
        // [END android_camerax_video_capture_mediastore]
        return recording
    }
}

private fun mirrorModeSnippet(useCases: MutableList<UseCase>) {
    // [START android_camerax_video_capture_mirror_mode]
    val recorder = Recorder.Builder().build()

    val videoCapture = VideoCapture.Builder(recorder)
        .setMirrorMode(MIRROR_MODE_ON_FRONT_ONLY)
        .build()

    useCases.add(videoCapture)
    // [END android_camerax_video_capture_mirror_mode]
}
