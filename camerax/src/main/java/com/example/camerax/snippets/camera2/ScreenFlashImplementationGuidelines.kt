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

import android.app.Activity
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraDevice
import android.hardware.camera2.CameraManager
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.CaptureResult
import android.hardware.camera2.TotalCaptureResult
import android.os.Build
import android.os.Handler
import android.os.HandlerThread
import android.view.Surface
import android.view.View
import android.widget.Button
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

private class ScreenFlashBrightnessHelper(private val activity: Activity?) {

    // [START android_camera2_screen_flash_brightness_control]
    private var previousBrightness: Float = -1.0f

    private fun maximizeScreenBrightness() {
        activity?.window?.let { window ->
            window.attributes?.apply {
                previousBrightness = screenBrightness
                screenBrightness = 1f
                window.attributes = this
            }
        }
    }

    private fun restoreScreenBrightness() {
        activity?.window?.let { window ->
            window.attributes?.apply {
                screenBrightness = previousBrightness
                window.attributes = this
            }
        }
    }
    // [END android_camera2_screen_flash_brightness_control]
}

private class ScreenFlashAeModeHelper(
    private val cameraManager: CameraManager,
    private val cameraId: String
) {
    // [START android_camera2_screen_flash_check_ae_mode]
    private val characteristics: CameraCharacteristics by lazy {
        cameraManager.getCameraCharacteristics(cameraId)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun isExternalFlashAeModeAvailable() =
        characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES)
            ?.contains(CaptureRequest.CONTROL_AE_MODE_ON_EXTERNAL_FLASH) ?: false
    // [END android_camera2_screen_flash_check_ae_mode]
}

private object ScreenFlashCallbackAeModeSnippets {
    // [START android_camera2_screen_flash_capture_callback_ae_mode]
    private val repeatingCaptureCallback = object : CameraCaptureSession.CaptureCallback() {
        private var targetAeMode: Int? = null
        private var aeModeUpdateDeferred: CompletableDeferred<Unit>? = null

        suspend fun awaitAeModeUpdate(targetAeMode: Int) {
            this.targetAeMode = targetAeMode
            aeModeUpdateDeferred = CompletableDeferred()
            // Makes the current coroutine wait until aeModeUpdateDeferred is completed. It is
            // completed once targetAeMode is found in the following capture callbacks
            aeModeUpdateDeferred?.await()
        }

        private fun process(result: CaptureResult) {
            // Checks if AE mode is updated and completes any awaiting Deferred
            aeModeUpdateDeferred?.let {
                val aeMode = result[CaptureResult.CONTROL_AE_MODE]
                if (aeMode == targetAeMode) {
                    it.complete(Unit)
                }
            }
        }

        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            super.onCaptureCompleted(session, request, result)
            process(result)
        }
    }
    // [END android_camera2_screen_flash_capture_callback_ae_mode]
}

private class ScreenFlashRepeatingRequestHelper(
    private val camera: CameraDevice,
    private val session: CameraCaptureSession,
    private val previewSurface: Surface,
    private val cameraManager: CameraManager,
    private val cameraId: String
) {
    private val characteristics: CameraCharacteristics by lazy {
        cameraManager.getCameraCharacteristics(cameraId)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun isExternalFlashAeModeAvailable() =
        characteristics.get(CameraCharacteristics.CONTROL_AE_AVAILABLE_MODES)
            ?.contains(CaptureRequest.CONTROL_AE_MODE_ON_EXTERNAL_FLASH) ?: false

    private val repeatingCaptureCallback = object : CameraCaptureSession.CaptureCallback() {
        fun awaitAeModeUpdate(targetAeMode: Int) {}
    }

    // [START android_camera2_screen_flash_enable_disable_ae_mode]
    /** [HandlerThread] where all camera operations run */
    private val cameraThread = HandlerThread("CameraThread").apply { start() }

    /** [Handler] corresponding to [cameraThread] */
    private val cameraHandler = Handler(cameraThread.looper)

    private suspend fun enableExternalFlashAeMode() {
        if (Build.VERSION.SDK_INT >= 28 && isExternalFlashAeModeAvailable()) {
            session.setRepeatingRequest(
                camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                    addTarget(previewSurface)
                    set(
                        CaptureRequest.CONTROL_AE_MODE,
                        CaptureRequest.CONTROL_AE_MODE_ON_EXTERNAL_FLASH
                    )
                }.build(),
                repeatingCaptureCallback, cameraHandler
            )

            // Wait for the request to be processed by camera
            repeatingCaptureCallback.awaitAeModeUpdate(CaptureRequest.CONTROL_AE_MODE_ON_EXTERNAL_FLASH)
        }
    }

    private fun disableExternalFlashAeMode() {
        if (Build.VERSION.SDK_INT >= 28 && isExternalFlashAeModeAvailable()) {
            session.setRepeatingRequest(
                camera.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW).apply {
                    addTarget(previewSurface)
                }.build(),
                repeatingCaptureCallback, cameraHandler
            )
        }
    }
    // [END android_camera2_screen_flash_enable_disable_ae_mode]
}

private object ScreenFlashConvergenceCallbackSnippets {
    // [START android_camera2_screen_flash_capture_callback_convergence]
    private val repeatingCaptureCallback = object : CameraCaptureSession.CaptureCallback() {
        private var targetAeMode: Int? = null
        private var aeModeUpdateDeferred: CompletableDeferred<Unit>? = null

        private var convergenceDeferred: CompletableDeferred<Unit>? = null

        suspend fun awaitAeModeUpdate(targetAeMode: Int) {
            this.targetAeMode = targetAeMode
            aeModeUpdateDeferred = CompletableDeferred()
            // Makes the current coroutine wait until aeModeUpdateDeferred is completed. It is
            // completed once targetAeMode is found in the following capture callbacks
            aeModeUpdateDeferred?.await()
        }

        suspend fun awaitAeAwbConvergence() {
            convergenceDeferred = CompletableDeferred()
            // Makes the current coroutine wait until convergenceDeferred is completed, it will be
            // completed once both AE & AWB are reported as converged in the capture callbacks below
            convergenceDeferred?.await()
        }

        private fun process(result: CaptureResult) {
            // Checks if AE mode is updated and completes any awaiting Deferred
            aeModeUpdateDeferred?.let {
                val aeMode = result[CaptureResult.CONTROL_AE_MODE]
                if (aeMode == targetAeMode) {
                    it.complete(Unit)
                }
            }

            // Checks for convergence and completes any awaiting Deferred
            convergenceDeferred?.let {
                val aeState = result[CaptureResult.CONTROL_AE_STATE]
                val awbState = result[CaptureResult.CONTROL_AWB_STATE]

                val isAeReady = (
                    aeState == null ||
                        // May be null in some devices (e.g. legacy camera HW level)
                        aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED ||
                        aeState == CaptureResult.CONTROL_AE_STATE_FLASH_REQUIRED
                    )

                val isAwbReady = (
                    awbState == null ||
                        // May be null in some devices (e.g. legacy camera HW level)
                        awbState == CaptureResult.CONTROL_AWB_STATE_CONVERGED
                    )

                if (isAeReady && isAwbReady) {
                    // if any non-null convergenceDeferred is set, complete it
                    it.complete(Unit)
                }
            }
        }

        override fun onCaptureCompleted(
            session: CameraCaptureSession,
            request: CaptureRequest,
            result: TotalCaptureResult
        ) {
            super.onCaptureCompleted(session, request, result)
            process(result)
        }
    }
    // [END android_camera2_screen_flash_capture_callback_convergence]
}

private class ScreenFlashSetupHelper(
    private val cameraManager: CameraManager,
    private val cameraId: String,
    private val cameraHandler: Handler,
    private val previewSurface: Surface,
    private val imageReaderSurface: Surface,
    private val repeatingCaptureCallback: CameraCaptureSession.CaptureCallback
) {
    private lateinit var camera: CameraDevice
    private lateinit var session: CameraCaptureSession

    private fun openCamera(manager: CameraManager, id: String, handler: Handler): CameraDevice {
        throw NotImplementedError()
    }

    private fun createCameraCaptureSession(device: CameraDevice, surfaces: List<Surface>, handler: Handler): CameraCaptureSession {
        throw NotImplementedError()
    }

    fun setupCamera() {
        // [START android_camera2_screen_flash_setup_repeating_request]
        // Open the selected camera
        camera = openCamera(cameraManager, cameraId, cameraHandler)

        // Creates list of Surfaces where the camera will output frames
        val targets = listOf(previewSurface, imageReaderSurface)

        // Start a capture session using our open camera and list of Surfaces where frames will go
        session = createCameraCaptureSession(camera, targets, cameraHandler)

        val captureRequest = camera.createCaptureRequest(
            CameraDevice.TEMPLATE_PREVIEW
        ).apply { addTarget(previewSurface) }

        // This will keep sending the capture request as frequently as possible until the
        // session is torn down or session.stopRepeating() is called
        session.setRepeatingRequest(captureRequest.build(), repeatingCaptureCallback, cameraHandler)
        // [END android_camera2_screen_flash_setup_repeating_request]
    }

    // [START android_camera2_screen_flash_run_precapture_sequence]
    private suspend fun runPrecaptureSequence() {
        // Creates a new capture request with CONTROL_AE_PRECAPTURE_TRIGGER_START
        val captureRequest = session.device.createCaptureRequest(
            CameraDevice.TEMPLATE_PREVIEW
        ).apply {
            addTarget(previewSurface)
            set(
                CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START
            )
        }

        val precaptureDeferred = CompletableDeferred<Unit>()
        session.capture(
            captureRequest.build(),
            object : CameraCaptureSession.CaptureCallback() {
                override fun onCaptureCompleted(
                    session: CameraCaptureSession,
                    request: CaptureRequest,
                    result: TotalCaptureResult
                ) {
                    // Waiting for this callback ensures the precapture request has been processed
                    precaptureDeferred.complete(Unit)
                }
            },
            cameraHandler
        )

        precaptureDeferred.await()

        // Precapture trigger request has been processed, we can wait for AE & AWB convergence now
        (repeatingCaptureCallback as? ConvergenceAwaitable)?.awaitAeAwbConvergence()
    }
    // [END android_camera2_screen_flash_run_precapture_sequence]

    interface ConvergenceAwaitable {
        suspend fun awaitAeAwbConvergence()
    }
}

private class ScreenFlashActivity : AppCompatActivity() {

    private lateinit var captureButton: Button
    private lateinit var whiteColorOverlayView: View

    private fun maximizeScreenBrightness() {}
    private fun restoreScreenBrightness() {}
    private suspend fun enableExternalFlashAeMode() {}
    private suspend fun runPrecaptureSequence() {}
    private suspend fun takePhoto() {}
    private fun disableExternalFlashAeMode() {}

    fun setupClickListeners() {
        // [START android_camera2_screen_flash_stitch_together]
        // User clicks captureButton to take picture
        captureButton.setOnClickListener { v ->
            // Apply the screen flash related UI changes
            whiteColorOverlayView.visibility = View.VISIBLE
            maximizeScreenBrightness()

            // Perform I/O heavy operations in a different scope
            lifecycleScope.launch(Dispatchers.IO) {
                // Enable external flash AE mode and wait for it to be processed
                enableExternalFlashAeMode()

                // Run precapture sequence and wait for it to complete
                runPrecaptureSequence()

                // Start taking picture and wait for it to complete
                takePhoto()

                disableExternalFlashAeMode()
                v.post {
                    // Clear the screen flash related UI changes
                    restoreScreenBrightness()
                    whiteColorOverlayView.visibility = View.INVISIBLE
                }
            }
        }
        // [END android_camera2_screen_flash_stitch_together]
    }
}
