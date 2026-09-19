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

package androidx.compose.material3

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@RequiresOptIn(
    message = "This Material 3 Expressive API is experimental and is likely to change or to be removed in the future.",
    level = RequiresOptIn.Level.WARNING
)
@Retention(AnnotationRetention.BINARY)
annotation class ExperimentalMaterial3ExpressiveApi





fun ButtonDefaults.contentPaddingFor(size: Dp, hasStartIcon: Boolean = false): PaddingValues {
    val horizontal = if (hasStartIcon) 16.dp else 24.dp
    val vertical = if (size <= 32.dp) 6.dp else 10.dp
    return PaddingValues(horizontal = horizontal, vertical = vertical)
}

fun ButtonDefaults.iconSizeFor(size: Dp): Dp = when {
    size <= 32.dp -> 16.dp
    size >= 56.dp -> 28.dp
    else -> 20.dp
}

fun ButtonDefaults.iconSpacingFor(size: Dp): Dp = 8.dp

@Composable
fun ButtonDefaults.textStyleFor(size: Dp): androidx.compose.ui.text.TextStyle = when {
    size <= 32.dp -> MaterialTheme.typography.labelMedium
    size >= 56.dp -> MaterialTheme.typography.titleMedium
    else -> MaterialTheme.typography.labelLarge
}

// Overloads for Button and button variants taking `shapes`
@Composable
fun Button(
    onClick: () -> Unit,
    shapes: ButtonShapes,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    elevation: ButtonElevation? = ButtonDefaults.buttonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    val interaction = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val cornerRadius by animateDpAsState(if (isPressed) 24.dp else 12.dp)
    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(cornerRadius),
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interaction,
        content = content
    )
}

@Composable
fun FilledTonalButton(
    onClick: () -> Unit,
    shapes: ButtonShapes,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.filledTonalButtonColors(),
    elevation: ButtonElevation? = ButtonDefaults.filledTonalButtonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    val interaction = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val cornerRadius by animateDpAsState(if (isPressed) 24.dp else 12.dp)
    FilledTonalButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(cornerRadius),
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interaction,
        content = content
    )
}

@Composable
fun ElevatedButton(
    onClick: () -> Unit,
    shapes: ButtonShapes,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.elevatedButtonColors(),
    elevation: ButtonElevation? = ButtonDefaults.elevatedButtonElevation(),
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    val interaction = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val cornerRadius by animateDpAsState(if (isPressed) 24.dp else 12.dp)
    ElevatedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(cornerRadius),
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interaction,
        content = content
    )
}

@Composable
fun OutlinedButton(
    onClick: () -> Unit,
    shapes: ButtonShapes,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = ButtonDefaults.outlinedButtonBorder(enabled),
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    val interaction = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val cornerRadius by animateDpAsState(if (isPressed) 24.dp else 12.dp)
    OutlinedButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(cornerRadius),
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interaction,
        content = content
    )
}

@Composable
fun TextButton(
    onClick: () -> Unit,
    shapes: ButtonShapes,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    colors: ButtonColors = ButtonDefaults.textButtonColors(),
    elevation: ButtonElevation? = null,
    border: BorderStroke? = null,
    contentPadding: PaddingValues = ButtonDefaults.TextButtonContentPadding,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    val interaction = interactionSource ?: remember { MutableInteractionSource() }
    val isPressed by interaction.collectIsPressedAsState()
    val cornerRadius by animateDpAsState(if (isPressed) 24.dp else 12.dp)
    TextButton(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = RoundedCornerShape(cornerRadius),
        colors = colors,
        elevation = elevation,
        border = border,
        contentPadding = contentPadding,
        interactionSource = interaction,
        content = content
    )
}





// LoadingIndicator & ContainedLoadingIndicator
@Composable
fun LoadingIndicator(modifier: Modifier = Modifier) {
    CircularProgressIndicator(
        modifier = modifier.size(36.dp),
        strokeWidth = 4.dp,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
fun ContainedLoadingIndicator(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.size(56.dp),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 3.dp
    ) {
        Box(contentAlignment = Alignment.Center) {
            CircularProgressIndicator(
                modifier = Modifier.size(28.dp),
                strokeWidth = 3.dp,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

// ButtonGroup & ToggleButton
@Composable
fun ButtonGroup(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        content()
    }
}

@Composable
fun ToggleButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    if (checked) {
        FilledTonalButton(
            onClick = { onCheckedChange(!checked) },
            modifier = modifier,
            content = content
        )
    } else {
        OutlinedButton(
            onClick = { onCheckedChange(!checked) },
            modifier = modifier,
            content = content
        )
    }
}
