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

import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.os.Build
import android.os.Handler
import android.view.Surface
import java.util.concurrent.Executors

private object CaptureSessionsRequestsSnippets {

    // [START android_camera2_capture_sessions_requests_configure_session]
    private fun configureSession(
        camera: CameraDevice,
        previewSurface: Surface,
        recordSurface: Surface,
        stateCallback: CameraCaptureSession.StateCallback,
        cameraHandler: Handler
    ) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val previewConfig = OutputConfiguration(previewSurface)
            val recordConfig = OutputConfiguration(recordSurface)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                previewConfig.streamUseCase =
                    CameraMetadata.SCALER_AVAILABLE_STREAM_USE_CASES_PREVIEW.toLong()
                recordConfig.streamUseCase =
                    CameraMetadata.SCALER_AVAILABLE_STREAM_USE_CASES_VIDEO_RECORD.toLong()
            }

            val sessionConfig = SessionConfiguration(
                SessionConfiguration.SESSION_REGULAR,
                listOf(previewConfig, recordConfig),
                Executors.newSingleThreadExecutor(),
                stateCallback
            )
            camera.createCaptureSession(sessionConfig)
        } else {
            camera.createCaptureSession(
                listOf(previewSurface, recordSurface),
                stateCallback,
                cameraHandler
            )
        }
    }
    // [END android_camera2_capture_sessions_requests_configure_session]

    fun buildSingleRequest(camera: CameraDevice, previewSurface: Surface) {
        // [START android_camera2_capture_sessions_requests_single_request]
        // Build a single capture request.
        val captureRequest = camera.createCaptureRequest(
            CameraDevice.TEMPLATE_PREVIEW
        ).apply { addTarget(previewSurface) }
        // [END android_camera2_capture_sessions_requests_single_request]
    }

    fun captureRequestSample(
        session: CameraCaptureSession,
        captureRequest: CaptureRequest.Builder,
        cameraHandler: Handler
    ) {
        // [START android_camera2_capture_sessions_requests_capture]
        session.capture(
            captureRequest.build(),
            object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
                ) {
                    // Process the result
                }
            },
            cameraHandler
        )
        // [END android_camera2_capture_sessions_requests_capture]
    }

    fun repeatingRequestSample(
        session: CameraCaptureSession,
        previewRequest: CaptureRequest,
        cameraHandler: Handler
    ) {
        // [START android_camera2_capture_sessions_requests_repeating_request]
        session.setRepeatingRequest(
            previewRequest,
            object : CameraCaptureSession.CaptureCallback() {
                // [START_EXCLUDE]
                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
                ) {}
                /*
                // [END_EXCLUDE]
                override fun onCaptureCompleted(...) { ... }
                // [START_EXCLUDE]
                 */
                // [END_EXCLUDE]
            },
            cameraHandler
        )
        // [END android_camera2_capture_sessions_requests_repeating_request]
    }

    fun interleavedRequestsSample(
        camera: CameraDevice,
        session: CameraCaptureSession,
        imageReaderSurface: Surface,
        previewRequest: CaptureRequest.Builder,
        previewCaptureCallback: CameraCaptureSession.CaptureCallback,
        stillCaptureCallback: CameraCaptureSession.CaptureCallback,
        cameraHandler: Handler
    ) {
        // [START android_camera2_capture_sessions_requests_interleaved_requests]
        // Build the single still capture request
        val stillCaptureRequest = camera.createCaptureRequest(
            CameraDevice.TEMPLATE_STILL_CAPTURE
        ).apply { addTarget(imageReaderSurface) }

        // Set repeating request for continuous preview stream
        session.setRepeatingRequest(
            previewRequest.build(),
            previewCaptureCallback,
            cameraHandler
        )

        // Capture single still frame without interrupting repeating request
        session.capture(
            stillCaptureRequest.build(),
            stillCaptureCallback,
            cameraHandler
        )
        // [END android_camera2_capture_sessions_requests_interleaved_requests]
    }
}
