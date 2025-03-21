/*
 * Copyright 2025 The Android Open Source Project
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

@file:Suppress("unused")

package com.example.compose.snippets.stylus

import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.lifecycle.ViewModel

class UserViewModel : ViewModel() {
    fun processMotionEvent(e: MotionEvent): Boolean {
        return true
    }
}

val viewModel = UserViewModel()

// [START android_compose_stylus_motion_event_access]
@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun DrawArea(modifier: Modifier = Modifier) {
    Canvas(
        modifier = modifier
            .clipToBounds()
            .pointerInteropFilter {
                viewModel.processMotionEvent(it)
            }

    ) {
        // Drawing code here.
    }
}
// [END android_compose_stylus_motion_event_access]
