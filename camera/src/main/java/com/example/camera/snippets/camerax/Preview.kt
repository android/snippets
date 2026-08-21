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

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import com.google.common.util.concurrent.ListenableFuture

// [START android_camerax_preview_request_provider]
private class MainActivity : AppCompatActivity() {
    private lateinit var cameraProviderFuture: ListenableFuture<ProcessCameraProvider>
    override fun onCreate(savedInstanceState: Bundle?) {
        cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        /* [START_EXCLUDE silent] */
        checkProviderSnippet(cameraProviderFuture)
        /* [END_EXCLUDE] */
    }
// [END android_camerax_preview_request_provider]

    private fun checkProviderSnippet(cameraProviderFuture: ListenableFuture<ProcessCameraProvider>) {
        // [START android_camerax_preview_check_provider]
        cameraProviderFuture.addListener(
            Runnable {
                val cameraProvider = cameraProviderFuture.get()
                bindPreview(cameraProvider)
            },
            ContextCompat.getMainExecutor(this)
        )
        // [END android_camerax_preview_check_provider]
    }

    private lateinit var previewView: PreviewView

    // [START android_camerax_preview_bind]
    fun bindPreview(cameraProvider: ProcessCameraProvider) {
        var preview: Preview = Preview.Builder()
            .build()

        var cameraSelector: CameraSelector = CameraSelector.Builder()
            .requireLensFacing(CameraSelector.LENS_FACING_BACK)
            .build()

        preview.setSurfaceProvider(previewView.surfaceProvider)

        var camera = cameraProvider.bindToLifecycle(this as LifecycleOwner, cameraSelector, preview)
    }
    // [END android_camerax_preview_bind]
}

private fun previewControlsSnippet(viewFinder: PreviewView) {
    // [START android_camerax_preview_implementation_mode]
    // viewFinder is a PreviewView instance
    viewFinder.implementationMode = PreviewView.ImplementationMode.COMPATIBLE
    // [END android_camerax_preview_implementation_mode]

    // [START android_camerax_preview_scale_type]
    // viewFinder is a PreviewView instance
    viewFinder.scaleType = PreviewView.ScaleType.FIT_CENTER
    // [END android_camerax_preview_scale_type]
}
