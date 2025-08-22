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

package com.example.compose.snippets.touchinput.pointerinput

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel

/*
 * Copyright 2023 The Android Open Source Project
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

/**
 * This sample demonstrates a Composable function that provides a canvas for drawing and
 * processes touch input events to draw lines. It uses a ViewModel to manage the drawing state.
 *
 * Gradle Dependencies:
 * implementation "androidx.compose.ui:ui:1.x.x"
 * implementation "androidx.compose.foundation:foundation:1.x.x"
 * implementation "androidx.lifecycle:lifecycle-viewmodel-compose:2.x.x"
 *
 * Manifest Permissions:
 * None
 */

const val TAG = "DrawAreaSample"

class DrawAreaSampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Column(Modifier.fillMaxSize().background(Color.LightGray)) {
                DrawArea(modifier = Modifier.weight(1f))
            }
        }
    }
}

// [START android_compose_draw_area_sample]
@Composable
@OptIn(ExperimentalComposeUiApi::class)
fun DrawArea(modifier: Modifier = Modifier, viewModel: DrawAreaViewModel = viewModel()) {
    Canvas(
        modifier = modifier
            .clipToBounds()
            .pointerInteropFilter { motionEvent ->
                viewModel.processMotionEvent(motionEvent)
                true
            }
    ) {
        viewModel.paths.forEach { path ->
            drawPath(
                path = path,
                color = Color.Black,
                style = Stroke(width = 8f, cap = StrokeCap.Round)
            )
        }
    }
}
// [END android_compose_draw_area_sample]

class DrawAreaViewModel : ViewModel() {
    private var currentPath = Path()
    val paths: SnapshotStateList<Path> = mutableStateListOf()

    @OptIn(ExperimentalComposeUiApi::class)
    fun processMotionEvent(motionEvent: android.view.MotionEvent) {
        when (motionEvent.actionMasked) {
            android.view.MotionEvent.ACTION_DOWN -> {
                Log.d(TAG, "ACTION_DOWN at (${motionEvent.x}, ${motionEvent.y})")
                currentPath = Path().apply {
                    moveTo(motionEvent.x, motionEvent.y)
                }
                paths.add(currentPath)
            }
            android.view.MotionEvent.ACTION_MOVE -> {
                Log.d(TAG, "ACTION_MOVE at (${motionEvent.x}, ${motionEvent.y})")
                // To trigger recomposition, we need to replace the path object
                // in the list with a new one.
                val newPath = Path().apply {
                    addPath(currentPath)
                    lineTo(motionEvent.x, motionEvent.y)
                }

                // Replace the last path with the updated one.
                if (paths.isNotEmpty()) {
                    paths[paths.size - 1] = newPath
                }
                currentPath = newPath
            }
            android.view.MotionEvent.ACTION_UP,
            android.view.MotionEvent.ACTION_CANCEL -> {
                Log.d(TAG, "ACTION_UP/CANCEL")
                // Path is complete, no further action needed
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewDrawArea() {
    Column(Modifier.fillMaxSize().background(Color.LightGray)) {
        DrawArea(modifier = Modifier.weight(1f))
    }
}
