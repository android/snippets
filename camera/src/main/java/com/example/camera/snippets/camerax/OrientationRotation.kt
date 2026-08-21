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

import android.content.ContentResolver
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Bundle
import android.view.OrientationEventListener
import android.view.Surface
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.impl.utils.Exif
import androidx.camera.lifecycle.ProcessCameraProvider
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.concurrent.Executor

private fun imageAnalysisRotationSnippet(imageProxy: ImageProxy) {
    // [START android_camerax_orientation_rotation_image_analysis]
    val rotation = imageProxy.imageInfo.rotationDegrees
    // [END android_camerax_orientation_rotation_image_analysis]
}

private fun imageProxyRotationSnippet(imageProxy: ImageProxy) {
    // [START android_camerax_orientation_rotation_image_proxy]
    val rotation = imageProxy.imageInfo.rotationDegrees
    // [END android_camerax_orientation_rotation_image_proxy]
}

private fun fileRotationSnippet(file: File) {
    // [START android_camerax_orientation_rotation_file]
    val exif = Exif.createFromFile(file)
    val rotation = exif.rotation
    // [END android_camerax_orientation_rotation_file]
}

private fun outputStreamRotationSnippet(outputStream: ByteArrayOutputStream) {
    // [START android_camerax_orientation_rotation_output_stream]
    val byteArray = outputStream.toByteArray()
    val exif = Exif.createFromInputStream(ByteArrayInputStream(byteArray))
    val rotation = exif.rotation
    // [END android_camerax_orientation_rotation_output_stream]
}

private fun mediaStoreRotationSnippet(
    contentResolver: ContentResolver,
    outputFileResults: ImageCapture.OutputFileResults,
) {
    // [START android_camerax_orientation_rotation_mediastore]
    val inputStream = contentResolver.openInputStream(outputFileResults.savedUri ?: return)
    val exif = Exif.createFromInputStream(inputStream ?: return)
    val rotation = exif.rotation
    // [END android_camerax_orientation_rotation_mediastore]
}

// [START android_camerax_orientation_rotation_use_cases_setup]
private class OrientationCameraActivity : AppCompatActivity() {
    private lateinit var mainExecutor: Executor
    private lateinit var cameraSelector: CameraSelector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val cameraProcessFuture = ProcessCameraProvider.getInstance(this)
        cameraProcessFuture.addListener(
            Runnable {
                val cameraProvider = cameraProcessFuture.get()

                // By default, the use cases set their target rotation to match the
                // display’s rotation.
                val preview = buildPreview()
                val imageAnalysis = buildImageAnalysis()
                val imageCapture = buildImageCapture()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalysis, imageCapture
                )
            },
            mainExecutor
        )
    }

    /* [START_EXCLUDE silent] */
    private fun buildPreview(): Preview = Preview.Builder().build()
    private fun buildImageAnalysis(): ImageAnalysis = ImageAnalysis.Builder().build()
    private fun buildImageCapture(): ImageCapture = ImageCapture.Builder().build()
    /* [END_EXCLUDE] */
}
// [END android_camerax_orientation_rotation_use_cases_setup]

// [START android_camerax_orientation_rotation_orientation_listener]
private class OrientationListenerActivity : AppCompatActivity() {
    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var imageCapture: ImageCapture

    private val orientationEventListener by lazy {
        object : OrientationEventListener(this) {
            override fun onOrientationChanged(orientation: Int) {
                if (orientation == ORIENTATION_UNKNOWN) {
                    return
                }

                val rotation = when (orientation) {
                    in 45 until 135 -> Surface.ROTATION_270
                    in 135 until 225 -> Surface.ROTATION_180
                    in 225 until 315 -> Surface.ROTATION_90
                    else -> Surface.ROTATION_0
                }

                imageAnalysis.targetRotation = rotation
                imageCapture.targetRotation = rotation
            }
        }
    }

    override fun onStart() {
        super.onStart()
        orientationEventListener.enable()
    }

    override fun onStop() {
        super.onStop()
        orientationEventListener.disable()
    }
}
// [END android_camerax_orientation_rotation_orientation_listener]

// [START android_camerax_orientation_rotation_display_listener]
private class DisplayListenerActivity : AppCompatActivity() {
    private lateinit var rootView: View
    private lateinit var imageAnalysis: ImageAnalysis
    private lateinit var imageCapture: ImageCapture

    private val displayListener = object : DisplayManager.DisplayListener {
        override fun onDisplayChanged(displayId: Int) {
            if (rootView.display.displayId == displayId) {
                val rotation = rootView.display.rotation
                imageAnalysis.targetRotation = rotation
                imageCapture.targetRotation = rotation
            }
        }

        override fun onDisplayAdded(displayId: Int) {
        }

        override fun onDisplayRemoved(displayId: Int) {
        }
    }

    override fun onStart() {
        super.onStart()
        val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        displayManager.registerDisplayListener(displayListener, null)
    }

    override fun onStop() {
        super.onStop()
        val displayManager = getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
        displayManager.unregisterDisplayListener(displayListener)
    }
}
// [END android_camerax_orientation_rotation_display_listener]
