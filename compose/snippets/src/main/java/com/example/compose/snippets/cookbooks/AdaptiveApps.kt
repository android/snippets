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

import android.os.Build
import android.view.MotionEvent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

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
