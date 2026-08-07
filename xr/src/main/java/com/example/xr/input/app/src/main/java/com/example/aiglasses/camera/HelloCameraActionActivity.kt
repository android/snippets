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

package com.example.aiglasses.camera

import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import androidx.activity.compose.setContent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.projected.ProjectedActivityCompat
import androidx.xr.projected.ProjectedInputEvent.ProjectedInputAction.Companion.TOGGLE_APP_CAMERA
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Demonstrates how to observe hardware camera action inputs (double-press)
 * and camera intents in a Projected Activity running on AI Glasses.
 */
class HelloCameraActionActivity : BaseProjectedActivity("Camera Action") {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // [START androidxr_projected_camera_action_observe]
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                withContext(Dispatchers.Default) {
                    try {
                        val projectedActivity = ProjectedActivityCompat.create(this@HelloCameraActionActivity)
                        try {
                            projectedActivity.projectedInputEvents.collect { inputEvent ->
                                if (inputEvent.inputAction == TOGGLE_APP_CAMERA) {
                                    withContext(Dispatchers.Main) {
                                        triggerCameraAction("Glasses Double-Press (TOGGLE_APP_CAMERA)")
                                    }
                                }
                            }
                        } finally {
                            projectedActivity.close()
                        }
                    } catch (e: Exception) {
                        Log.w("HelloCameraActionActivity", "ProjectedActivityCompat unavailable: ${e.message}")
                    }
                }
            }
        }
        // [END androidxr_projected_camera_action_observe]

        // 2. Render unified sample UI
        setContent {
            GlimmerTheme {
                ProjectedSampleScreen(
                    activity = this@HelloCameraActionActivity,
                    onGestureAction = { source -> triggerCameraAction(source) }
                )
            }
        }
    }

    override fun onTextToSpeechReady() = handleIntentAction(intent)
    override fun onNewIntent(intent: Intent) { super.onNewIntent(intent); setIntent(intent); handleIntentAction(intent) }

    private fun handleIntentAction(incomingIntent: Intent?) {
        val action = incomingIntent?.action ?: return
        if (action == MediaStore.ACTION_IMAGE_CAPTURE || action.contains("CAMERA", ignoreCase = true) || action == Intent.ACTION_CAMERA_BUTTON) {
            triggerCameraAction("System Camera Intent ($action)")
        }
    }

    private fun triggerCameraAction(source: String) = triggerFeedback("Camera action detected.", source, "camera_action")

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_UP && (
                event.keyCode == KeyEvent.KEYCODE_CAMERA ||
                event.keyCode == KeyEvent.KEYCODE_ENTER ||
                event.keyCode == KeyEvent.KEYCODE_DPAD_CENTER
            )) {
            triggerCameraAction("Camera Button / Side Tap")
            return true
        }
        return super.dispatchKeyEvent(event)
    }
}

