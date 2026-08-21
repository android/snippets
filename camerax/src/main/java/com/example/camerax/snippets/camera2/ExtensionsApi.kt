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

package com.example.camerax.snippets.camera2

import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraExtensionCharacteristics
import android.hardware.camera2.CameraExtensionSession
import android.hardware.camera2.CameraManager
import android.hardware.camera2.params.ExtensionSessionConfiguration
import android.hardware.camera2.params.OutputConfiguration
import android.media.ImageReader
import android.os.Build
import android.view.Surface
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.asExecutor

private object CameraExtensionsSnippets {
    // [START android_camera2_extensions_api_get_extension_camera_ids]
    private fun getExtensionCameraIds(cameraManager: CameraManager): List<String> =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            cameraManager.cameraIdList.filter { cameraId ->
                val characteristics = cameraManager.getCameraCharacteristics(cameraId)
                val extensionCharacteristics =
                    cameraManager.getCameraExtensionCharacteristics(cameraId)
                val capabilities =
                    characteristics.get(CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES)
                extensionCharacteristics.supportedExtensions.isNotEmpty() &&
                    capabilities?.contains(
                        CameraCharacteristics.REQUEST_AVAILABLE_CAPABILITIES_BACKWARD_COMPATIBLE
                    ) ?: false
            }
        } else emptyList()
    // [END android_camera2_extensions_api_get_extension_camera_ids]
}

@RequiresApi(Build.VERSION_CODES.S)
private class CameraExtensionFragment(
    private val cameraDevice: CameraDevice,
    private val previewSurface: Surface,
    private val stillImageReader: ImageReader,
    private val previewView: View
) : Fragment() {

    private var cameraExtensionSession: CameraExtensionSession? = null

    // [START android_camera2_extensions_api_start_extension_session]
    private val captureCallbacks = object : CameraExtensionSession.ExtensionCaptureCallback() {
        // Implement Capture Callbacks
    }
    private val extensionSessionStateCallback = object : CameraExtensionSession.StateCallback() {
        override fun onConfigured(session: CameraExtensionSession) {
            cameraExtensionSession = session
            try {
                val captureRequest =
                    cameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                        addTarget(previewSurface)
                    }.build()
                session.setRepeatingRequest(
                    captureRequest,
                    Dispatchers.IO.asExecutor(),
                    captureCallbacks
                )
            } catch (e: CameraAccessException) {
                Snackbar.make(
                    previewView,
                    "Failed to preview capture request",
                    Snackbar.LENGTH_SHORT
                ).show()
                requireActivity().finish()
            }
        }

        override fun onClosed(session: CameraExtensionSession) {
            super.onClosed(session)
            cameraDevice.close()
        }

        override fun onConfigureFailed(session: CameraExtensionSession) {
            Snackbar.make(
                previewView,
                "Failed to start camera extension preview",
                Snackbar.LENGTH_SHORT
            ).show()
            requireActivity().finish()
        }
    }

    private fun startExtensionSession() {
        val outputConfig = arrayListOf(
            OutputConfiguration(stillImageReader.surface),
            OutputConfiguration(previewSurface)
        )
        val extensionConfiguration = ExtensionSessionConfiguration(
            CameraExtensionCharacteristics.EXTENSION_NIGHT,
            outputConfig,
            Dispatchers.IO.asExecutor(),
            extensionSessionStateCallback
        )
        cameraDevice.createExtensionSession(extensionConfiguration)
    }
    // [END android_camera2_extensions_api_start_extension_session]
}
