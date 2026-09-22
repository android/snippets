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

package com.example.camera.snippets.camera2

import android.app.Activity
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraMetadata
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.params.OutputConfiguration
import android.hardware.camera2.params.SessionConfiguration
import android.media.ImageReader
import android.os.Build
import android.view.Surface
import android.view.SurfaceView
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private class CaptureSessionSetupActivity(
    private val surfaceViewId: Int,
    private val imageReaderWidth: Int,
    private val imageReaderHeight: Int,
    private val imageReaderFormat: Int,
    private val imageReaderMaxImages: Int,
    private val session: SessionConfiguration
) : Activity() {

    fun prepareTargets() {
        // [START android_camera2_capture_sessions_requests_configure_session]
        // Retrieve the target surfaces, which might be coming from a number of places:
        // 1. SurfaceView, if you want to display the image directly to the user
        // 2. ImageReader, if you want to read each frame or perform frame-by-frame
        // analysis
        // 3. OpenGL Texture or TextureView, although discouraged for maintainability
        // reasons
        // 4. RenderScript.Allocation, if you want to do parallel processing
        val surfaceView = findViewById<SurfaceView>(
            // [START_EXCLUDE silent]
            surfaceViewId
            // [END_EXCLUDE]
            // ...
        )
        val imageReader = ImageReader.newInstance(
            // [START_EXCLUDE silent]
            imageReaderWidth, imageReaderHeight, imageReaderFormat, imageReaderMaxImages
            // [END_EXCLUDE]
            // ...
        )

        // Remember to call this only *after* SurfaceHolder.Callback.surfaceCreated()
        val previewSurface = surfaceView.holder.surface
        val imReaderSurface = imageReader.surface
        val targets = listOf(previewSurface, imReaderSurface)

        // Create a capture session using the predefined targets; this also involves
        // defining the session state callback to be notified of when the session is
        // ready
        // Setup Stream Use Case while setting up your Output Configuration.
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        fun configureSession(device: CameraDevice, targets: List<Surface>) {
            val configs = mutableListOf<OutputConfiguration>()
            val streamUseCase = CameraMetadata
                .SCALER_AVAILABLE_STREAM_USE_CASES_PREVIEW_VIDEO_STILL

            targets.forEach {
                val config = OutputConfiguration(it)
                config.streamUseCase = streamUseCase.toLong()
                configs.add(config)
            }
            // ...
            device.createCaptureSession(session)
        }
        // [END android_camera2_capture_sessions_requests_configure_session]
    }
}

private object CaptureSessionsRequestsSnippets {

    fun buildSingleRequest(
        sessionFromStateCallback: CameraCaptureSession,
        previewSurface: Surface
    ) {
        // [START android_camera2_capture_sessions_requests_single_request]
        val session: CameraCaptureSession = /* ... */ // from CameraCaptureSession.StateCallback
            // [START_EXCLUDE silent]
            sessionFromStateCallback
        // [END_EXCLUDE]
        val captureRequest = session.device.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW)
        captureRequest.addTarget(previewSurface)
        // [END android_camera2_capture_sessions_requests_single_request]
    }

    fun captureRequestSample(
        sessionFromStateCallback: CameraCaptureSession,
        captureRequestBuilder: CaptureRequest.Builder
    ) {
        /*
        // [START android_camera2_capture_sessions_requests_capture]
        val session: CameraCaptureSession = ...  // from CameraCaptureSession.StateCallback
        val captureRequest: CaptureRequest.Builder = ...  // from CameraDevice.createCaptureRequest()
        // [START_EXCLUDE silent]
         */
        val session = sessionFromStateCallback
        val captureRequest = captureRequestBuilder
        // [END_EXCLUDE]

        // The first null argument corresponds to the capture callback, which you
        // provide if you want to retrieve frame metadata or keep track of failed capture
        // requests that can indicate dropped frames; the second null argument
        // corresponds to the Handler used by the asynchronous callback, which falls
        // back to the current thread's looper if null
        session.capture(captureRequest.build(), null, null)
        // [END android_camera2_capture_sessions_requests_capture]
    }

    fun repeatingRequestSample(
        sessionFromStateCallback: CameraCaptureSession,
        captureRequestBuilder: CaptureRequest.Builder
    ) {
        /*
        // [START android_camera2_capture_sessions_requests_repeating_request]
        val session: CameraCaptureSession = ... // from CameraCaptureSession.StateCallback
        val captureRequest: CaptureRequest.Builder = ... // from CameraDevice.createCaptureRequest()
        // [START_EXCLUDE silent]
         */
        val session = sessionFromStateCallback
        val captureRequest = captureRequestBuilder
        // [END_EXCLUDE]

        // This keeps sending the capture request as frequently as possible until
        // the session is torn down or session.stopRepeating() is called
        session.setRepeatingRequest(captureRequest.build(), null, null)
        // [END android_camera2_capture_sessions_requests_repeating_request]
    }

    fun interleavedRequestsSample(
        sessionFromStateCallback: CameraCaptureSession,
        previewSurface: Surface,
        imReaderSurface: Surface
    ) {
        // [START android_camera2_capture_sessions_requests_interleaved_requests]
        val session: CameraCaptureSession = /* ... */ // from CameraCaptureSession.StateCallback
            // [START_EXCLUDE silent]
            sessionFromStateCallback
        // [END_EXCLUDE]

        // Create the repeating request and dispatch it
        val repeatingRequest = session.device.createCaptureRequest(
            CameraDevice.TEMPLATE_PREVIEW
        )
        repeatingRequest.addTarget(previewSurface)
        session.setRepeatingRequest(repeatingRequest.build(), null, null)

        // Some time later...

        // Create the single request and dispatch it
        // NOTE: This can disrupt the ongoing repeating request momentarily
        val singleRequest = session.device.createCaptureRequest(
            CameraDevice.TEMPLATE_STILL_CAPTURE
        )
        singleRequest.addTarget(imReaderSurface)
        session.capture(singleRequest.build(), null, null)
        // [END android_camera2_capture_sessions_requests_interleaved_requests]
    }
}
