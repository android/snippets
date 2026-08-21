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

package com.example.camerax.snippets.lowlight

import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCaptureSession.CaptureCallback
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CaptureResult
import android.hardware.camera2.TotalCaptureResult
import android.os.Build
import android.os.Handler
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
private class LowLightBoostAeSnippets(
    private val cameraManager: CameraManager,
    private val cameraId: String,
    private val camera: CameraDevice,
    private val session: CameraCaptureSession,
    private val cameraHandler: Handler
) {

    private fun checkAvailability() {
        // [START android_camera_lowlight_boost_ae_check_availability]
        val characteristics = cameraManager.getCameraCharacteristics(cameraId)
        val autoExposureModes =
            characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES)!!
        val lowLightBoostSupported = autoExposureModes.contains(
            CameraMetadata.CONTROL_AE_MODE_ON_LOW_LIGHT_BOOST_BRIGHTNESS_PRIORITY
        )

        if (lowLightBoostSupported) {
            // Enable Low Light Boost AE Mode (next section)
        } else {
            // Proceed without Low Light Boost AE Mode
        }
        // [END android_camera_lowlight_boost_ae_check_availability]
    }

    private fun isLowLightBoostAvailable(cameraId: String): Boolean = true

    private fun enableLowLightBoost() {
        // [START android_camera_lowlight_boost_ae_enable]
        val captureRequestBuilder = camera.createCaptureRequest(
            CameraDevice.TEMPLATE_PREVIEW
        )
        if (isLowLightBoostAvailable(cameraId)) {
            captureRequestBuilder.set(
                CaptureRequest.CONTROL_AE_MODE,
                CameraMetadata.CONTROL_AE_MODE_ON_LOW_LIGHT_BOOST_BRIGHTNESS_PRIORITY
            )
        }
        // other capture request params

        session.setRepeatingRequest(
            captureRequestBuilder.build(),
            object : CaptureCallback() {
                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
                ) {
                    // verify Low Light Boost AE Mode AE mode set successfully
                    result.get(CaptureResult.CONTROL_AE_MODE) ==
                        CameraMetadata.CONTROL_AE_MODE_ON_LOW_LIGHT_BOOST_BRIGHTNESS_PRIORITY
                }
            },
            cameraHandler
        )
        // [END android_camera_lowlight_boost_ae_enable]
    }

    private fun monitorLowLightBoost(captureRequestBuilder: CaptureRequest.Builder) {
        // [START android_camera_lowlight_boost_ae_monitor]
        session.setRepeatingRequest(
            captureRequestBuilder.build(),
            object : CaptureCallback() {
                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
                ) {
                    // check if Low Light Boost AE Mode is active or inactive
                    if (result.get(CaptureResult.CONTROL_LOW_LIGHT_BOOST_STATE) ==
                        CameraMetadata.CONTROL_LOW_LIGHT_BOOST_STATE_ACTIVE
                    ) {
                        // Low Light Boost AE Mode state is active
                        // Show Moon Icon
                    } else {
                        // Low Light Boost AE Mode state is inactive or AE mode is not set
                        // to Low Light Boost AE Mode
                        // Hide Moon Icon
                    }
                }
            },
            cameraHandler
        )
        // [END android_camera_lowlight_boost_ae_monitor]
    }
}
