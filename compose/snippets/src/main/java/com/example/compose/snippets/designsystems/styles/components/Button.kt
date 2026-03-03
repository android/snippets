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

val baseButtonStyle = Style {

}


@ExperimentalFoundationStyleApi
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