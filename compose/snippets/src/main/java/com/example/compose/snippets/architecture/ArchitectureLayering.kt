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

package com.example.compose.snippets.architecture

import androidx.compose.animation.Animatable
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ProvideTextStyle
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun ArchitectureLayering1(condition: Boolean) {
    // [START android_compose_architecture_layering1]
    val color = animateColorAsState(if (condition) Color.Green else Color.Red)
    // [END android_compose_architecture_layering1]
}

@Composable
fun ArchitectureLayering2(condition: Boolean) {
    // [START android_compose_architecture_layering2]
    val color = remember { Animatable(Color.Gray) }
    LaunchedEffect(condition) {
        color.animateTo(if (condition) Color.Green else Color.Red)
    }
    // [END android_compose_architecture_layering2]
}

// [START android_compose_architecture_layering3]
@Composable
fun Button(
    // …
    content: @Composable RowScope.() -> Unit
) {
    Surface(/* … */) {
        CompositionLocalProvider(/* … */) { // set LocalContentAlpha
            ProvideTextStyle(MaterialTheme.typography.button) {
                Row(
                    // …
                    content = content
                )
            }
        }
    }
}
// [END android_compose_architecture_layering3]

// [START android_compose_architecture_layering4]
@Composable
fun GradientButton(
    // …
    background: List<Color>,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        // …
        modifier = modifier
            .clickable(onClick = {})
            .background(
                Brush.horizontalGradient(background)
            )
    ) {
        CompositionLocalProvider(/* … */) { // set material LocalContentAlpha
            ProvideTextStyle(MaterialTheme.typography.button) {
                content()
            }
        }
    }
}
// [END android_compose_architecture_layering4]

// [START android_compose_architecture_layering5]
@Composable
fun BespokeButton(
    // …
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        // …
        modifier = modifier
            .clickable(onClick = {})
            .background(backgroundColor)
    ) {
        // No Material components used
        content()
    }
}
// [END android_compose_architecture_layering5]
