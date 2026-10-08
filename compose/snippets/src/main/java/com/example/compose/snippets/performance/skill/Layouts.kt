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

package com.example.compose.snippets.performance.skill

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.input.pointer.PointerEvent
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.node.PointerInputModifierNode
import androidx.compose.ui.node.requireLayoutCoordinates
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp

private fun VisibilityTrackingModifier(itemId: String): Modifier =
    // [START android_compose_performance_layouts_visibility_changed]
    // Optimized: Viewport entry with dwell threshold
    Modifier.onVisibilityChanged(
        minFractionVisible = 0.5f,
        minDurationMs = 500L,
    ) { isVisible ->
        if (isVisible) {
            analytics.logImpression(itemId)
        }
    }
    // [END android_compose_performance_layouts_visibility_changed]

// [START android_compose_performance_layouts_require_coordinates]
// In a custom PointerInputModifierNode:
class InteractiveTooltipNode : Modifier.Node(), PointerInputModifierNode {
    override fun onPointerEvent(
        pointerEvent: PointerEvent,
        pass: PointerEventPass,
        bounds: IntSize,
    ) {
        // Query coordinates on-demand at the exact moment of the interaction
        if (isAttached) {
            val coordinates = requireLayoutCoordinates()
            val bounds = coordinates.boundsInRoot()
            showTooltip(bounds)
        }
    }
    // [START_EXCLUDE silent]
    override fun onCancelPointerInput() {}
    // [END_EXCLUDE]
}
// [END android_compose_performance_layouts_require_coordinates]

private fun TapGesturePositionModifier(): Modifier =
    // [START android_compose_performance_layouts_pointer_input]
    // Optimized: Captures touch offset on event without onGloballyPositioned
    Modifier.pointerInput(Unit) {
        detectTapGestures { offset ->
            showMenuAt(offset)
        }
    }
    // [END android_compose_performance_layouts_pointer_input]

@Composable
private fun OffsetPhaseDeferralBad(scrollState: ScrollState) {
    // [START android_compose_performance_layouts_offset_bad]
    val offset = scrollState.value
    Modifier.offset(x = offset.dp, y = 0.dp)
    // [END android_compose_performance_layouts_offset_bad]
}

private fun OffsetPhaseDeferralGood(scrollState: ScrollState) {
    // [START android_compose_performance_layouts_offset_good]
    Modifier.offset { IntOffset(scrollState.value, 0) }
    // [END android_compose_performance_layouts_offset_good]
}

private object analytics {
    fun logImpression(itemId: String) {}
}

private fun showTooltip(bounds: Rect) {}

private fun showMenuAt(offset: Offset) {}
