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

package com.example.compose.snippets.touchinput.pointerinput

import androidx.compose.animation.core.exponentialDecay
import androidx.compose.animation.core.spring
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.AnchoredDraggableState
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeableState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterialApi::class)
private object SwipeableStateExample {
    // [START android_compose_touchinput_pointerinput_migrate_swipeable_state_legacy]
    class MySwitchState : SwipeableState<DragValue>(initialValue = DragValue.Start)
    // [END android_compose_touchinput_pointerinput_migrate_swipeable_state_legacy]
}

@OptIn(ExperimentalFoundationApi::class)
private object AnchoredDraggableStateExample {
    // [START android_compose_touchinput_pointerinput_migrate_swipeable_state_anchored]
    class MySwitchState {
        private val anchoredDraggableState = AnchoredDraggableState(
            // [START_EXCLUDE]
            initialValue = DragValue.Start,
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { 125f },
            snapAnimationSpec = spring(),
            decayAnimationSpec = exponentialDecay()
            // [END_EXCLUDE]
        )
    }
    // [END android_compose_touchinput_pointerinput_migrate_swipeable_state_anchored]
}

@OptIn(ExperimentalFoundationApi::class)
// [START android_compose_touchinput_pointerinput_migrate_swipeable_access_offset]
@Composable
fun AnchoredDraggableBox() {
    val state = remember {
        AnchoredDraggableState(
            // [START_EXCLUDE]
            initialValue = DragValue.Start,
            positionalThreshold = { distance: Float -> distance * 0.5f },
            velocityThreshold = { 125f },
            snapAnimationSpec = spring(),
            decayAnimationSpec = exponentialDecay()
            // [END_EXCLUDE]
        )
    }
    val density = LocalDensity.current
    val anchors = remember {
        DraggableAnchors {
            // [START_EXCLUDE]
            DragValue.Start at -100f
            DragValue.Center at 0f
            DragValue.End at 100f
            // [END_EXCLUDE]
        }
    }
    SideEffect {
        state.updateAnchors(anchors)
    }
    Box(
        Modifier.offset { IntOffset(x = state.requireOffset().toInt(), y = 0) }
    )
}
// [END android_compose_touchinput_pointerinput_migrate_swipeable_access_offset]

private object AnchoredDraggableConstructorExample {
    // [START android_compose_touchinput_pointerinput_migrate_swipeable_anchors_constructor]
    enum class DragValue { Start, Center, End }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun AnchoredDraggableBox() {
        val anchors = DraggableAnchors {
            DragValue.Start at -100f
            DragValue.Center at 0f
            DragValue.End at 100f
        }
        val state = remember {
            AnchoredDraggableState(
                initialValue = DragValue.Start,
                anchors = anchors,
                positionalThreshold = { distance: Float -> distance * 0.5f },
                velocityThreshold = { 125f },
                snapAnimationSpec = spring(),
                decayAnimationSpec = exponentialDecay()
            )
        }
        Box(
            Modifier.offset { IntOffset(x = state.requireOffset().toInt(), y = 0) }
        )
    }
    // [END android_compose_touchinput_pointerinput_migrate_swipeable_anchors_constructor]
}

private object AnchoredDraggableUpdateAnchorsExample {
    // [START android_compose_touchinput_pointerinput_migrate_swipeable_anchors_update]
    enum class DragValue { Start, Center, End }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun AnchoredDraggableBox() {
        val state = remember {
            AnchoredDraggableState(
                // [START_EXCLUDE]
                initialValue = DragValue.Start,
                positionalThreshold = { distance: Float -> distance * 0.5f },
                velocityThreshold = { 125f },
                snapAnimationSpec = spring(),
                decayAnimationSpec = exponentialDecay()
                // [END_EXCLUDE]
            )
        }
        val density = LocalDensity.current
        val anchors = with(density) {
            DraggableAnchors {
                DragValue.Start at -100.dp.toPx()
                DragValue.Center at 0f
                DragValue.End at 100.dp.toPx()
            }
        }
        SideEffect {
            state.updateAnchors(anchors)
        }
        Box(
            Modifier.offset { IntOffset(x = state.requireOffset().toInt(), y = 0) }
        )
    }
    // [END android_compose_touchinput_pointerinput_migrate_swipeable_anchors_update]
}

@OptIn(ExperimentalFoundationApi::class)
private fun PositionalThresholdFraction() {
    // [START android_compose_touchinput_pointerinput_migrate_swipeable_positional_threshold_fraction]
    val anchoredDraggableState = AnchoredDraggableState(
        positionalThreshold = { distance -> distance * 0.5f },
        // [START_EXCLUDE]
        initialValue = DragValue.Start,
        velocityThreshold = { 125f },
        snapAnimationSpec = spring(),
        decayAnimationSpec = exponentialDecay()
        // [END_EXCLUDE]
    )
    // [END android_compose_touchinput_pointerinput_migrate_swipeable_positional_threshold_fraction]
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PositionalThresholdDp() {
    // [START android_compose_touchinput_pointerinput_migrate_swipeable_positional_threshold_dp]
    val density = LocalDensity.current
    val anchoredDraggableState = AnchoredDraggableState(
        positionalThreshold = { with(density) { 56.dp.toPx() } },
        // [START_EXCLUDE]
        initialValue = DragValue.Start,
        velocityThreshold = { 125f },
        snapAnimationSpec = spring(),
        decayAnimationSpec = exponentialDecay()
        // [END_EXCLUDE]
    )
    // [END android_compose_touchinput_pointerinput_migrate_swipeable_positional_threshold_dp]
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun VelocityThreshold() {
    // [START android_compose_touchinput_pointerinput_migrate_swipeable_velocity_threshold]
    val density = LocalDensity.current
    val anchoredDraggableState = AnchoredDraggableState(
        velocityThreshold = { with(density) { 125.dp.toPx() } },
        // [START_EXCLUDE]
        initialValue = DragValue.Start,
        positionalThreshold = { distance: Float -> distance * 0.5f },
        snapAnimationSpec = spring(),
        decayAnimationSpec = exponentialDecay()
        // [END_EXCLUDE]
    )
    // [END android_compose_touchinput_pointerinput_migrate_swipeable_velocity_threshold]
}

enum class DragValue { Start, Center, End }
