/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.compose.snippets.animations.demo.fade

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.animations.demo.CheeseImages
import com.example.compose.snippets.animations.demo.SimpleScaffold

/**
 *  A fade creates a smooth sequence between elements that fully overlap each other, such as
 *  photos inside of a card or another container. When a new element enters, it fades in
 *  over the current element.
 */
@Preview
@Composable
fun FadeDemo() {
    SimpleScaffold(title = "Fade") {

        val painters = CheeseImages.map { painterResource(it) }
        var index by remember { mutableIntStateOf(0) }

        AnimatedContent(
            targetState = index,
            modifier = Modifier.align(Alignment.Center),
            transitionSpec = fade()
        ) { targetIndex ->
            Image(
                painter = painters[targetIndex],
                contentDescription = "Cheese",
                modifier = Modifier
                    .size(256.dp, 192.dp)
                    .clip(shape = RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
        }

        Button(
            onClick = { index = (index + 1) % painters.size },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(64.dp)
        ) {
            Text(text = "NEXT")
        }
    }
}

/**
 * Creates a transitionSpec for configuring [AnimatedContent] to the fade pattern.
 */
private fun fade(
    durationMillis: Int = 300
): AnimatedContentTransitionScope<Int>.() -> ContentTransform {
    return {
        ContentTransform(
            // The initial content should stay until the target content is completely opaque.
            initialContentExit = fadeOut(animationSpec = snap(delayMillis = durationMillis)),
            // The target content fades in. This is shown on top of the initial content.
            targetContentEnter = fadeIn(
                animationSpec = tween(
                    durationMillis = durationMillis,
                    // LinearOutSlowInEasing is suitable for incoming elements.
                    easing = LinearOutSlowInEasing
                )
            )
        )
    }
}
