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

package com.example.compose.preview.generator

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.sp
import com.example.compose.preview.wasm.registry.PreviewFontWeights
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Checks that the screenshots are drawn with the typeface and weights we expect.
 *
 * If the variable font's `wght` axis were silently dropped (for example, because a static face was
 * resolved instead of the variable one), every weight would render with the same advance widths.
 * Measuring the same string at each weight catches that without comparing pixels.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], qualifiers = "w384dp-h216dp-xhdpi")
class PreviewFontTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun variableFontAppliesEachWeight() {
        val widths = mutableMapOf<FontWeight, Int>()
        composeTestRule.setContent {
            val measurer = rememberTextMeasurer()
            PreviewFontWeights.forEach { weight ->
                widths[weight] = measurer.measure(
                    text = SAMPLE,
                    style = TextStyle(fontFamily = PreviewFontFamily, fontWeight = weight, fontSize = 14.sp),
                ).size.width
            }
        }
        composeTestRule.waitForIdle()

        println("Preview font widths for \"$SAMPLE\": " + widths.entries.joinToString { "${it.key.weight}=${it.value}px" })
        val ordered = PreviewFontWeights.map { widths.getValue(it) }
        assertTrue(
            "Expected Roboto Flex to get wider as wght increases, got $widths",
            ordered.zipWithNext().all { (lighter, heavier) -> heavier > lighter },
        )
    }

    private companion object {
        const val SAMPLE = "Filled tonal button 0123456789"
    }
}
