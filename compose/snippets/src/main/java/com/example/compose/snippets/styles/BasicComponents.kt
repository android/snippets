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

package com.example.compose.snippets.styles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextOverflow

@Composable
fun BaseCheckbox(modifier: Modifier = Modifier, style: Style = Style) {
    // Your custom component applying the style via the styleable modifier
}

@ExperimentalFoundationStyleApi
@Composable
fun BaseText(
    text: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    onTextLayout: ((TextLayoutResult) -> Unit)? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    autoSize: TextAutoSize? = null,
) {
    BasicText(
        text = text,
        modifier = modifier.styleable(null, style),
        onTextLayout = onTextLayout,
        overflow = overflow,
        softWrap = softWrap,
        maxLines = maxLines,
        minLines = minLines,
        autoSize = autoSize,
    )
}

// Example of using a Style within your own component.
@ExperimentalFoundationStyleApi
@Composable
fun BaseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: Style = Style,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember {
        MutableInteractionSource()
    },
    content: @Composable RowScope.() -> Unit
) {
    val styleState = remember(interactionSource) {
        MutableStyleState(interactionSource)
    }
    styleState.isEnabled = enabled
    Row(
        modifier = modifier
            .hoverable(interactionSource = interactionSource)
            .focusable(true, interactionSource = interactionSource)
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = interactionSource,
                indication = null,
            )
            .styleable(styleState, StylesThemes.LocalAppStyles.current.baseButtonStyle, style),
        content = content,
        verticalAlignment = Alignment.CenterVertically
    )
}

