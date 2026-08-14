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

import android.view.View
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.concurrent.Executor

private fun setUpCamera(
    view: View,
    cameraProvider: ProcessCameraProvider,
    lifecycleOwner: LifecycleOwner,
    cameraSelector: CameraSelector,
    preview: Preview,
) {
    // [START android_camerax_take_photo_setup]
    val imageCapture = ImageCapture.Builder()
        .setTargetRotation(view.display.rotation)
        .build()

    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageCapture, preview)
    // [END android_camerax_take_photo_setup]
}

private class TakePhoto(
    private val imageCapture: ImageCapture,
    private val cameraExecutor: Executor,
) {
    // [START android_camerax_take_photo_save_to_file]
    fun onClick() {
        val outputFileOptions = ImageCapture.OutputFileOptions.Builder(File(/* [START_EXCLUDE] */ "" /* [END_EXCLUDE] */)).build()
        imageCapture.takePicture(
            outputFileOptions, cameraExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(error: ImageCaptureException) {
                    // insert your code here.
                }
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    // insert your code here.
                }
            }
        )
    }
    // [END android_camerax_take_photo_save_to_file]
}
