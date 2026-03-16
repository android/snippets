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

@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.example.compose.snippets.designsystems.styles.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics

val baseButtonStyle = Style {

}

@ExperimentalFoundationStyleApi
// [START android_compose_styles_base_button]
@Composable
fun BaseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: Style = Style,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    val effectiveInteractionSource = interactionSource ?: remember {
        MutableInteractionSource()
    }
    val styleState = remember(effectiveInteractionSource) {
        MutableStyleState(effectiveInteractionSource)
    }
    styleState.isEnabled = enabled
    Row(
        modifier = modifier
            .semantics(properties = {
                role = Role.Button
            })
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = effectiveInteractionSource,
                indication = null,
            )
            .styleable(styleState, baseButtonStyle, style),
        content = content,
        verticalAlignment = Alignment.CenterVertically
    )
}
// [END android_compose_styles_base_button]