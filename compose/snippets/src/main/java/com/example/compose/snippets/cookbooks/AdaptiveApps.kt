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

package com.example.compose.snippets.cookbooks

// [START android_compose_stylus_palm_rejection_acquire]
// [START android_compose_stylus_palm_rejection_determine_action]
import androidx.compose.ui.input.pointer.pointerInput
// [END android_compose_stylus_palm_rejection_acquire]
// [END android_compose_stylus_palm_rejection_determine_action]

import android.content.res.Configuration
import android.os.Build
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.core.util.Consumer

@Composable
fun StylusPalmRejectionAcquireSample() {
    // [START android_compose_stylus_palm_rejection_acquire]
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val motionEvent = event.motionEvent
                        if (motionEvent != null) {
                            // Process motion event.
                        }
                    }
                }
            }
    )
    // [END android_compose_stylus_palm_rejection_acquire]
}

@Composable
fun StylusPalmRejectionActionSample() {
    // [START android_compose_stylus_palm_rejection_determine_action]
    Box(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        val event = awaitPointerEvent()
                        val motionEvent = event.motionEvent ?: continue

                        when (motionEvent.actionMasked) {
                            MotionEvent.ACTION_CANCEL -> {
                                // Process canceled single-pointer motion event for all SDK versions.
                            }
                            MotionEvent.ACTION_POINTER_UP -> {
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                                    (motionEvent.flags and MotionEvent.FLAG_CANCELED) == MotionEvent.FLAG_CANCELED
                                ) {
                                    // Process canceled multi-pointer motion event for Android 13 and higher.
                                }
                            }
                        }
                    }
                }
            }
    )
    // [END android_compose_stylus_palm_rejection_determine_action]
}

@Composable
fun MediaPlaybackSpacebarPreview() {
    // [START android_compose_media_playback_spacebar_preview]
    Column(
        modifier = Modifier.onPreviewKeyEvent { event ->
            if (event.type == KeyEventType.KeyUp && event.key == Key.Spacebar) {
                // Handle spacebar keypress to pause/resume playback.
                true
            } else {
                false
            }
        }
    ) {
        // Content
    }
    // [END android_compose_media_playback_spacebar_preview]
}

@Composable
fun MediaPlaybackSpacebarOnKeyEvent() {
    // [START android_compose_media_playback_spacebar_onkeyevent]
    Column(
        modifier = Modifier.onKeyEvent { event ->
            if (event.type == KeyEventType.KeyUp && event.key == Key.Spacebar) {
                // Handle spacebar keypress to pause/resume playback.
                true
            } else {
                false
            }
        }
    ) {
        // Content
    }
    // [END android_compose_media_playback_spacebar_onkeyevent]
}

@Composable
fun DetachableKeyboardReactive() {
    // [START android_compose_detachable_keyboard_reactive]
    val configuration = LocalConfiguration.current
    val isPhysicalKeyboardAttached = configuration.keyboard == Configuration.KEYBOARD_QWERTY

    if (isPhysicalKeyboardAttached) {
        // Render layout optimized for physical keyboard
    } else {
        // Render default layout
    }
    // [END android_compose_detachable_keyboard_reactive]
}

@Composable
fun DetachableKeyboardListener() {
    // [START android_compose_detachable_keyboard_listener]
    val context = LocalContext.current
    DisposableEffect(context) {
        val activity = context as? ComponentActivity
        val listener = Consumer<Configuration> { newConfig ->
            val hasKeyboard = newConfig.keyboard == Configuration.KEYBOARD_QWERTY
            // Trigger non-UI actions, analytics, etc.
        }
        activity?.addOnConfigurationChangedListener(listener)
        onDispose {
            activity?.removeOnConfigurationChangedListener(listener)
        }
    }
    // [END android_compose_detachable_keyboard_listener]
}
