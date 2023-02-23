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

@file:Suppress("UnusedReceiverParameter")

package com.example.compose.snippets.animation

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import org.junit.Rule
import org.junit.Test

/*
* Copyright 2023 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/

class AnimationTestingSnippets {
    // [START android_compose_animations_testing_example]
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun testAnimationWithClock() {
        // Pause animations
        rule.mainClock.autoAdvance = false
        var enabled by mutableStateOf(false)
        rule.setContent {
            val color by animateColorAsState(
                targetValue = if (enabled) Color.Red else Color.Green,
                animationSpec = tween(durationMillis = 250)
            )
            Box(Modifier.size(64.dp).background(color))
        }

        // Initiate the animation.
        enabled = true

        // Let the animation proceed.
        rule.mainClock.advanceTimeBy(50L)

        // Compare the result with the image showing the expected result.
        // `assertAgainGolden` needs to be implemented in your code.
        rule.onRoot().captureToImage().assertAgainstGolden()
    }
    // [END android_compose_animations_testing_example]
}

/*
Fakes needed for snippets to build:
 */

private fun ImageBitmap.assertAgainstGolden() {
}
