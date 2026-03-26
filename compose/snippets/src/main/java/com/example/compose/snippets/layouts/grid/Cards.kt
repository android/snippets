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

@file:OptIn(ExperimentalGridApi::class)

package com.example.compose.snippets.layouts.grid

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ExperimentalGridApi
import androidx.compose.foundation.layout.Grid
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.ui.theme.PastelBlue
import com.example.compose.snippets.ui.theme.PastelGreen
import com.example.compose.snippets.ui.theme.PastelPink
import com.example.compose.snippets.ui.theme.PastelRed
import com.example.compose.snippets.ui.theme.PastelYellow

@Composable
fun TextCard(
    label: String,
    modifier: Modifier = Modifier,
    containerColor: Color = CardDefaults.cardColors().containerColor,
    contentColor: Color = CardDefaults.cardColors().contentColor,

    shape: Shape = CardDefaults.shape,
    onClick: () -> Unit = {},
) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        contentAlignment = Alignment.Center,
        modifier =
            modifier
                .fillMaxSize()
                .clickable(interactionSource = interactionSource, onClick = onClick)
                .semantics {
                    role = Role.Button
                }
                .clip(shape)
                .background(containerColor)
                .alternatePattern()


    ) {
        Text(label, color = contentColor, modifier = Modifier.padding(8.dp))
    }
}


@Composable
internal fun Card1(
    modifier: Modifier = Modifier,
    containerColor: Color = CardDefaults.cardColors().containerColor,
    contentColor: Color = contentColorFor(containerColor),
    onClick: () -> Unit = {}
) {
    TextCard(
        label = "#1",
        containerColor = containerColor,
        contentColor = contentColor,
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
internal fun Card2(
    modifier: Modifier = Modifier,
    containerColor: Color = CardDefaults.cardColors().containerColor,
    contentColor: Color = contentColorFor(containerColor),
    onClick: () -> Unit = {}
) {
    TextCard(
        label = "#2",
        containerColor = containerColor,
        contentColor = contentColor,
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
internal fun Card3(
    modifier: Modifier = Modifier,
    containerColor: Color = CardDefaults.cardColors().containerColor,
    contentColor: Color = contentColorFor(containerColor),
    onClick: () -> Unit = {}
) {
    TextCard(
        label = "#3",
        containerColor = containerColor,
        contentColor = contentColor,
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
internal fun Card4(
    modifier: Modifier = Modifier,
    containerColor: Color = CardDefaults.cardColors().containerColor,
    contentColor: Color = contentColorFor(containerColor),
    onClick: () -> Unit = {}
) {
    TextCard(
        label = "#4",
        containerColor = containerColor,
        contentColor = contentColor,
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
internal fun Card5(
    modifier: Modifier = Modifier,
    containerColor: Color = CardDefaults.cardColors().containerColor,
    contentColor: Color = contentColorFor(containerColor),
    onClick: () -> Unit = {}
) {
    TextCard(
        label = "#5",
        containerColor = containerColor,
        contentColor = contentColor,
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
internal fun Card6(
    modifier: Modifier = Modifier,
    containerColor: Color = CardDefaults.cardColors().containerColor,
    contentColor: Color = contentColorFor(containerColor),
    onClick: () -> Unit = {}
) {
    TextCard(
        label = "#6",
        containerColor = containerColor,
        contentColor = contentColor,
        modifier = modifier,
        onClick = onClick
    )
}

@Composable
internal fun PastelRedCard(label: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    TextCard(label = label, containerColor = PastelRed, modifier = modifier, onClick = onClick)
}


@Composable
internal fun PastelGreenCard(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    TextCard(label = label, containerColor = PastelGreen, modifier = modifier, onClick = onClick)
}

@Composable
internal fun PastelBlueCard(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    TextCard(label = label, containerColor = PastelBlue, modifier = modifier, onClick = onClick)
}

@Composable
internal fun PastelYellowCard(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    TextCard(label = label, containerColor = PastelYellow, modifier = modifier, onClick = onClick)
}

internal val LocalEnableAlternativePattern = compositionLocalOf { false }

@Composable
internal fun EnableAlternativePattern(enable: Boolean = true, content: @Composable () -> Unit) {
    CompositionLocalProvider(
        value = LocalEnableAlternativePattern provides enable,
        content = content,
    )
}

internal fun Modifier.diagonalLines(
    color: Color = Color.White.copy(alpha = 0.5f),
    strokeWidth: Dp = 1.dp,
    spacing: Dp = 8.dp,
): Modifier {
    return drawBehind {
        val strokePx = strokeWidth.toPx()
        val spacingPx = spacing.toPx()
        val (width, height) = size
        val path = Path()

        var x = -height
        while (x < width) {
            path.moveTo(x, 0f)
            path.lineTo(x + height, height)
            x += spacingPx
        }
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = strokePx),
        )
    }
}

@Composable
internal fun Modifier.alternatePattern(): Modifier {
    return if (LocalEnableAlternativePattern.current) {
        diagonalLines()
    } else {
        this
    }
}

@Preview
@Composable
fun TextCardPreview() {
    TextCard("Preview")
}

@Preview
@Composable
fun CardsPreview() {
    Grid(
        config = {
            row(180.dp)
            row(180.dp)
            column(180.dp)
            column(180.dp)
        }
    ) {
        Card1()
        EnableAlternativePattern {
            Card2()
        }
        Card3(containerColor = PastelPink)
        EnableAlternativePattern {
            Card4(containerColor = PastelPink)
        }
    }
}