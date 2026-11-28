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

package com.example.compose.snippets.touchinput.scroll

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.DeviceConfigurationOverride
import androidx.compose.ui.test.LayoutDirection
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DraggableComposableElementTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun horizontalDragging_followsPointer_inLtr() {
        assertHorizontalDraggingFollowsPointer(LayoutDirection.Ltr)
    }

    @Test
    fun horizontalDragging_followsPointer_inRtl() {
        assertHorizontalDraggingFollowsPointer(LayoutDirection.Rtl)
    }

    private fun assertHorizontalDraggingFollowsPointer(layoutDirection: LayoutDirection) {
        composeTestRule.setContent {
            DeviceConfigurationOverride(DeviceConfigurationOverride.LayoutDirection(layoutDirection)) {
                // Leave room for the preview to move in either direction.
                Box(Modifier.fillMaxSize().padding(64.dp)) {
                    DraggableComposableElement()
                }
            }
        }

        val preview = composeTestRule.onNodeWithText("Video Preview")
        val initialBounds = preview.getUnclippedBoundsInRoot()
        val dragDistance = with(composeTestRule.density) { 48.dp.toPx() }

        preview.performTouchInput {
            swipe(start = center, end = center + Offset(dragDistance, 0f))
        }

        val boundsAfterDraggingRight = preview.getUnclippedBoundsInRoot()
        assertTrue(
            "Dragging right should move the preview right in $layoutDirection",
            boundsAfterDraggingRight.left > initialBounds.left
        )
        assertEquals(initialBounds.top.value, boundsAfterDraggingRight.top.value, 0.5f)

        preview.performTouchInput {
            swipe(start = center, end = center - Offset(dragDistance, 0f))
        }

        val boundsAfterDraggingLeft = preview.getUnclippedBoundsInRoot()
        assertTrue(
            "Dragging left should move the preview left in $layoutDirection",
            boundsAfterDraggingLeft.left < boundsAfterDraggingRight.left
        )
        assertEquals(initialBounds.top.value, boundsAfterDraggingLeft.top.value, 0.5f)
    }
}
