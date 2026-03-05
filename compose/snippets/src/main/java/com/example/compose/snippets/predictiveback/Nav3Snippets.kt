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

@file:Suppress("unused", "UNUSED_VARIABLE")

package com.example.compose.snippets.predictiveback

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent.EDGE_LEFT
import android.view.MotionEvent.EDGE_RIGHT
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.navigationevent.NavigationEventInfo
import androidx.navigationevent.NavigationEventTransitionState
import androidx.navigationevent.compose.NavigationEventState
import androidx.navigationevent.compose.rememberNavigationEventState


// [START android_compose_predictiveback_navevent_animation]
object Routes {
    const val SCREEN_A = "Screen A"
    const val SCREEN_B = "Screen B"
}

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var state by remember { mutableStateOf(Routes.SCREEN_A) }
            val backEventState = rememberNavigationEventState<NavigationEventInfo>(currentInfo = NavigationEventInfo.None)

            when (state) {
                Routes.SCREEN_A -> {
                    ScreenA { state = Routes.SCREEN_B }
                }
                else -> {
                    if (backEventState.transitionState is NavigationEventTransitionState.InProgress) {
                        ScreenA { }
                    }
                    ScreenB(
                        backEventState = backEventState,
                        onBackCompleted = { state = Routes.SCREEN_A }
                    )
                }
            }
        }
    }
}

@Composable
fun ScreenB(
    backEventState: NavigationEventState<NavigationEventInfo>,
    onBackCompleted: () -> Unit = {},
) {

    var backProgress by remember { mutableFloatStateOf(0f) }
    var swipeEdge by remember { mutableIntStateOf(0) }

    when (val transitionState = backEventState.transitionState) {
        is NavigationEventTransitionState.InProgress -> {
            backProgress = transitionState.latestEvent.progress
            swipeEdge = transitionState.latestEvent.swipeEdge
            Log.d("BackGesture", "Progress: ${transitionState.latestEvent.progress}")
        }
        is NavigationEventTransitionState.Idle -> {
            Log.d("BackGesture", "Idle")
        }
    }

    val animatedScale by animateFloatAsState(
        targetValue = 1f - (backProgress * 0.1f),
        label = "ScaleAnimation"
    )

    val maxShift = (LocalConfiguration.current.screenWidthDp / 20f) - 8

    val animatedOffsetX by animateDpAsState(
        targetValue = when (swipeEdge) {
            EDGE_LEFT -> (backProgress * maxShift).dp
            EDGE_RIGHT -> (-backProgress * maxShift).dp
            else -> 0.dp
        },
        label = "OffsetXAnimation"
    )

    NavigationBackHandler(
        state = backEventState,
        onBackCompleted = onBackCompleted,
        isBackEnabled = true
    )

    // Rest of UI
}
// [END android_compose_predictiveback_navevent_animation]

@Composable
fun ScreenA(onNavigate: () -> Unit) {
    // Basic ScreenA implementation for snippet
}

