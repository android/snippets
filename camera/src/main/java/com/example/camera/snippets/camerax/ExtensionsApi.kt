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
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.extensions.ExtensionMode
import androidx.camera.extensions.ExtensionsManager
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat

private const val TAG = "ExtensionsApi"

private class ExtensionsActivity : AppCompatActivity() {
    private lateinit var surfaceProvider: Preview.SurfaceProvider

    // [START android_camerax_extensions_enable_night]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val lifecycleOwner = this

        val cameraProviderFuture = ProcessCameraProvider.getInstance(applicationContext)
        cameraProviderFuture.addListener({
            // Obtain an instance of a process camera provider
            // The camera provider provides access to the set of cameras associated with the device.
            // The camera obtained from the provider will be bound to the activity lifecycle.
            val cameraProvider = cameraProviderFuture.get()

            val extensionsManagerFuture =
                ExtensionsManager.getInstanceAsync(applicationContext, cameraProvider)
            extensionsManagerFuture.addListener({
                // Obtain an instance of the extensions manager
                // The extensions manager enables a camera to use extension capabilities available on
                // the device.
                val extensionsManager = extensionsManagerFuture.get()

                // Select the camera
                val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

                // Query if extension is available.
                // Not all devices will support extensions or might only support a subset of
                // extensions.
                if (extensionsManager.isExtensionAvailable(cameraSelector, ExtensionMode.NIGHT)) {
                    // Unbind all use cases before enabling different extension modes.
                    try {
                        cameraProvider.unbindAll()

                        // Retrieve a night extension enabled camera selector
                        val nightCameraSelector =
                            extensionsManager.getExtensionEnabledCameraSelector(
                                cameraSelector,
                                ExtensionMode.NIGHT
                            )

                        // Bind image capture and preview use cases with the extension enabled camera
                        // selector.
                        val imageCapture = ImageCapture.Builder().build()
                        val preview = Preview.Builder().build()
                        // Connect the preview to receive the surface the camera outputs the frames
                        // to. This will allow displaying the camera frames in either a TextureView
                        // or SurfaceView. The SurfaceProvider can be obtained from the PreviewView.
                        preview.setSurfaceProvider(surfaceProvider)

                        // Returns an instance of the camera bound to the lifecycle
                        // Use this camera object to control various operations with the camera
                        // Example: flash, zoom, focus metering etc.
                        val camera = cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            nightCameraSelector,
                            imageCapture,
                            preview
                        )
                    } catch (e: Exception) {
                        Log.e(TAG, "Use case binding failed", e)
                    }
                }
            }, ContextCompat.getMainExecutor(this))
        }, ContextCompat.getMainExecutor(this))
    }
    // [END android_camerax_extensions_enable_night]
}
