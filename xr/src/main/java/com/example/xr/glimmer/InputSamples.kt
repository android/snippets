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

package com.example.xr.glimmer

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.TitleChip
import androidx.xr.glimmer.onIndirectPointerGesture
import androidx.xr.projected.ProjectedActivityCompat
import androidx.xr.projected.ProjectedInputEvent.ProjectedInputAction.Companion.TOGGLE_APP_CAMERA
import androidx.xr.projected.experimental.ExperimentalProjectedApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CancellationException

// [START androidxr_projected_touchpad_input]

@Composable
fun TouchpadInputSnippet(
    modifier: Modifier = Modifier,
    onTap: () -> Unit = {},
    onSwipeForward: () -> Unit = {},
    onSwipeBackward: () -> Unit = {}
) {
    val focusRequester = remember { FocusRequester() }

    // Request focus so this Composable receives indirect pointer events
    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color.Black)
            .focusRequester(focusRequester)
            .focusTarget()
            .onIndirectPointerGesture(
                enabled = true,
                onClick = onTap,
                onSwipeForward = onSwipeForward,
                onSwipeBackward = onSwipeBackward
            ),
        contentAlignment = Alignment.Center
    ) {
        TitleChip {
            Text(
                text = "Touchpad Input Active",
                style = GlimmerTheme.typography.bodyMedium,
                modifier = Modifier.padding(12.dp)
            )
        }
    }
}
// [END androidxr_projected_touchpad_input]

// [START androidxr_projected_camera_action]

@OptIn(ExperimentalProjectedApi::class)
class CameraActionInputSnippetActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Observe hardware camera action events when RESUMED
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.RESUMED) {
                withContext(Dispatchers.Default) {
                    try {
                        val projectedActivity = ProjectedActivityCompat.create(this@CameraActionInputSnippetActivity)
                        try {
                            projectedActivity.projectedInputEvents.collect { inputEvent ->
                                if (inputEvent.inputAction == TOGGLE_APP_CAMERA) {
                                    withContext(Dispatchers.Main) {
                                        onCameraActionTriggered()
                                    }
                                }
                            }
                        } finally {
                            projectedActivity.close()
                        }
                    } catch (e: CancellationException) {
                        throw e
                    } catch (e: Exception) {
                        Log.w("CameraActionSnippet", "ProjectedActivityCompat unavailable: ${e.message}")
                    }
                }
            }
        }

        setContent {
            GlimmerTheme {
                TouchpadInputSnippet(
                    onTap = { Log.i("CameraActionSnippet", "Touchpad tapped") }
                )
            }
        }
    }

    private fun onCameraActionTriggered() {
        Log.i("CameraActionSnippet", "Camera action button double-pressed!")
    }
}
// [END androidxr_projected_camera_action]

// [START androidxr_projected_motion_gesture]

class MotionGestureInputSnippetActivity : ComponentActivity() {

    override fun dispatchGenericMotionEvent(ev: MotionEvent): Boolean {
        val actionName = when (ev.actionMasked) {
            MotionEvent.ACTION_DOWN -> "ACTION_DOWN" // First contact with the glasses touchpad
            MotionEvent.ACTION_MOVE -> "ACTION_MOVE" // Contact moving across the glasses touchpad
            MotionEvent.ACTION_UP -> "ACTION_UP"     // Contact lifted from the glasses touchpad
            else -> "ACTION_${ev.actionMasked}"
        }

        Log.d(
            "MotionGestureSnippet",
            "MotionEvent: action=$actionName x=${ev.x} y=${ev.y}"
        )

        // Delegate to super unless intentionally consuming the event
        return super.dispatchGenericMotionEvent(ev)
    }
}
// [END androidxr_projected_motion_gesture]

// [START androidxr_projected_back_gesture]

class BackGestureInputSnippetActivity : ComponentActivity() {

    private val backCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            onBackGestureReceived()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Register back gesture callback with Activity's onBackPressedDispatcher
        onBackPressedDispatcher.addCallback(this, backCallback)
    }

    private fun onBackGestureReceived() {
        Log.i("BackGestureSnippet", "System back gesture intercepted")
    }
}
// [END androidxr_projected_back_gesture]
