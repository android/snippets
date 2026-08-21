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

import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.lifecycle.LifecycleOwner
import java.util.concurrent.Executor

private fun bindImageAnalysis(
    cameraProvider: ProcessCameraProvider,
    cameraSelector: CameraSelector,
    preview: Preview,
    executor: Executor,
    lifecycleOwner: LifecycleOwner,
) {
    val imageAnalysis =
        // [START android_camerax_analyze_bind_lifecycle]
        ImageAnalysis.Builder()
            // enable the following line if RGBA output is needed.
            // .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
            .setTargetResolution(Size(1280, 720))
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
    imageAnalysis.setAnalyzer(
        executor,
        ImageAnalysis.Analyzer { imageProxy ->
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            // insert your code here.
            /* [START_EXCLUDE] */
            // [END_EXCLUDE] */
            // after done, release the ImageProxy object
            imageProxy.close()
        }
    )

    cameraProvider.bindToLifecycle(lifecycleOwner, cameraSelector, imageAnalysis, preview)
    // [END android_camerax_analyze_bind_lifecycle]
}
