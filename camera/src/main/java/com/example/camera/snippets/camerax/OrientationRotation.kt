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

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider

private fun imageProxyRotationSnippet(imageProxy: ImageProxy) {
    // [START android_camerax_orientation_rotation_image_proxy]
    val rotation = imageProxy.imageInfo.rotationDegrees
    // [END android_camerax_orientation_rotation_image_proxy]
}

private object CameraUseCasesSetupSnippet {
    @RequiresApi(Build.VERSION_CODES.P)
    // [START android_camerax_orientation_rotation_use_cases_setup]
    class CameraActivity : ComponentActivity() {
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
    }
    // [END android_camerax_orientation_rotation_use_cases_setup]

    private val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

    private fun buildPreview(): Preview = Preview.Builder().build()

    private fun buildImageAnalysis(): ImageAnalysis = ImageAnalysis.Builder().build()

    private fun buildImageCapture(): ImageCapture = ImageCapture.Builder().build()
}
