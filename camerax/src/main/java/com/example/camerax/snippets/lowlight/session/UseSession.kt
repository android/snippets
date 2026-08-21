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

package com.example.camerax.snippets.lowlight.session

import android.app.Activity
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCaptureSession
import android.hardware.camera2.CaptureRequest
import android.hardware.camera2.TotalCaptureResult
import android.util.Log
import android.view.Surface
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import java.util.concurrent.CancellationException
import java.util.concurrent.Executor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

private class MainActivity(
    private val coroutineScope: CoroutineScope,
    private val lowLightBoostClient: LowLightBoostClient
) : Activity() {
    private val TAG = "MainActivity"
    private var lowLightBoostSession: LowLightBoostSession? = null

    // [START android_camera_lowlight_boost_session_create_callback]
    private fun createLowLightBoostCallback(): LowLightBoostCallback =
        object : LowLightBoostCallback() {
            override fun onSessionDestroyed() {
                Log.d(TAG, "onSessionDestroyed")
                lowLightBoostSession = null
            }

            override fun onSessionDisconnected(statusCode: Int) {
                Log.d(TAG, "onSessionDisconnected: error=$statusCode")
                lowLightBoostSession = null
            }
        }
    // [END android_camera_lowlight_boost_session_create_callback]

    private fun snippetCreateSession(
        previewSurface: Surface,
        cameraId: String,
        previewWidth: Int,
        previewHeight: Int,
        enableLowLightBoost: Boolean
    ) {
        coroutineScope.apply {
            // [START android_camera_lowlight_boost_session_create]
            val options = LowLightBoostOptions(
                previewSurface,
                cameraId,
                previewWidth,
                previewHeight,
                enableLowLightBoost
            )

            launch {
                try {
                    val lowLightBoostSession = lowLightBoostClient
                        .createSession(options, createLowLightBoostCallback()).await()

                    Log.d(TAG, "Session created successfully")

                    // Get the surface from the LLB session;
                    // give it to camera so camera can write frames to it
                } catch (e: CancellationException) {
                    Log.w(TAG, "Session creation was canceled", e)
                    lowLightBoostSession = null
                } catch (e: ApiException) {
                    Log.e(TAG, "Session creation failed with ApiException:", e)
                    lowLightBoostSession = null
                } catch (e: Exception) {
                    Log.e(TAG, "Session creation failed with Exception", e)
                    lowLightBoostSession = null
                }
            }
            // [END android_camera_lowlight_boost_session_create]
        }
    }

    private fun snippetCaptureCallback() {
        // [START android_camera_lowlight_boost_session_capture_callback]
        val captureCallback = object : CameraCaptureSession.CaptureCallback() {
            override fun onCaptureCompleted(
                session: CameraCaptureSession,
                request: CaptureRequest,
                result: TotalCaptureResult
            ) {
                super.onCaptureCompleted(session, request, result)
                lowLightBoostSession?.processCaptureResult(result)
            }
        }
        // [END android_camera_lowlight_boost_session_capture_callback]
    }

    private fun snippetStartPreview(
        lowLightBoostSession: LowLightBoostSession,
        lowLightBoostExecutor: Executor
    ) {
        // [START android_camera_lowlight_boost_session_start_preview]
        this.lowLightBoostSession =
            lowLightBoostSession
        this.lowLightBoostSession
            ?.setSceneDetectorCallback(
                { lowLightBoostSession, boostStrength ->
                    Log.d(
                        TAG,
                        "onSceneBrightnessChanged: " +
                            "boostStrength=$boostStrength"
                    )
                    // boostStrength > 0.5 indicates a low light scene.
                    // Update UI accordingly.
                },
                lowLightBoostExecutor
            )
        try {
            startCaptureSession(
                lowLightBoostSession.getCameraSurface()
            )
            // Start a Camera2 session here. Pass the LLB surface
            // to the camera so the camera can write frames to it.
        } catch (e: CameraAccessException) {
            Log.e(TAG, "Failed to start capture session", e)
            // Must try again or start the capture session without LLB.
        }
        // [END android_camera_lowlight_boost_session_start_preview]
    }

    // [START android_camera_lowlight_boost_session_release]
    override fun onDestroy() {
        super.onDestroy()
        if (lowLightBoostSession != null) {
            lowLightBoostSession?.release()
            lowLightBoostSession = null
        }
    }
    // [END android_camera_lowlight_boost_session_release]

    private fun startCaptureSession(surface: Surface) {}
}

private class LowLightBoostOptions(
    val previewSurface: Surface,
    val cameraId: String,
    val previewWidth: Int,
    val previewHeight: Int,
    val enableLowLightBoost: Boolean
)

private abstract class LowLightBoostCallback {
    open fun onSessionDestroyed() {}
    open fun onSessionDisconnected(statusCode: Int) {}
}

private class LowLightBoostSession {
    fun processCaptureResult(result: TotalCaptureResult) {}
    fun setSceneDetectorCallback(callback: SceneDetectorCallback, executor: Executor) {}
    fun getCameraSurface(): Surface = Surface(null)
    fun release() {}
}

private fun interface SceneDetectorCallback {
    fun onSceneBrightnessChanged(session: LowLightBoostSession, boostStrength: Float)
}

private class LowLightBoostClient {
    fun createSession(options: LowLightBoostOptions, callback: LowLightBoostCallback): Task<LowLightBoostSession> =
        Tasks.forResult(LowLightBoostSession())
}
