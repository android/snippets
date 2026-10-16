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

package com.example.compose.snippets.text
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

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.ExpandPolicy
import androidx.compose.foundation.text.input.InputTransformation
import androidx.compose.foundation.text.input.TextFieldBuffer
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.TrackedRange
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.FormatAlignLeft
import androidx.compose.material.icons.automirrored.filled.FormatAlignRight
import androidx.compose.material.icons.filled.FormatAlignCenter
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.ParagraphStyle
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CharacterLevelStyling(modifier: Modifier = Modifier) {
    // [START android_compose_text_rich_text_character_styling]
    TextField(
        state = rememberTextFieldState(),
        // [START_EXCLUDE silent]
        modifier = modifier
            .padding(16.dp),
        textStyle = LocalTextStyle.current.copy(
            fontSize = 18.sp,
            lineHeight = 26.sp,
            color = MaterialTheme.colorScheme.onSurface
        ),
        // [END_EXCLUDE]
        inputTransformation = InputTransformation {
            for (i in 0 until changes.changeCount) {
                val range = changes.getRange(i)
                if (!range.collapsed) {
                    addStyle(
                        spanStyle = SpanStyle(
                            fontWeight = FontWeight.ExtraBold,
                            fontStyle = FontStyle.Italic,
                            color = Color.Red,
                        ),
                        range = range,
                        expandPolicy = ExpandPolicy.AtEnd
                    )
                }
            }
        }
    )
    // [END android_compose_text_rich_text_character_styling]
}

@Composable
fun ActionButton(
    icon: ImageVector,
    contentDescription: String,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        colors = IconButtonDefaults.iconButtonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    ) {
        Icon(imageVector = icon, contentDescription = contentDescription)
    }
}

@Composable
fun ParagraphLevelStyling(modifier: Modifier = Modifier) {
    val state = rememberTextFieldState(
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Fusce nisl mi, porta at eros quis, dignissim dignissim tortor."
    )
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        TextField(
            state = state,
            modifier = Modifier
                .fillMaxWidth()
                .height(140.dp),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ActionButton(
                icon = Icons.AutoMirrored.Filled.FormatAlignLeft,
                contentDescription = "Align Start",
                onClick = { setParagraphAlignment(state, TextAlign.Start) }
            )
            ActionButton(
                icon = Icons.Default.FormatAlignCenter,
                contentDescription = "Align Center",
                onClick = { setParagraphAlignment(state, TextAlign.Center) }
            )
            ActionButton(
                icon = Icons.AutoMirrored.Filled.FormatAlignRight,
                contentDescription = "Align End",
                onClick = { setParagraphAlignment(state, TextAlign.End) }
            )
        }
    }
}

/**
 * Applies paragraph alignment directly to the selected text range.
 */
// [START android_compose_text_rich_text_paragraph_styling]
fun setParagraphAlignment(state: TextFieldState, alignment: TextAlign) {
    val targetRange = if (state.selection.collapsed) {
        TextRange(0, state.text.length)
    } else {
        state.selection
    }
    if (targetRange.collapsed) return
    state.edit {
        // 1. Remove existing paragraph styles in this range
        getParagraphStyles(targetRange).forEach { removeStyle(it) }
        // 2. Apply the new paragraph alignment
        addStyle(
            paragraphStyle = ParagraphStyle(textAlign = alignment),
            range = targetRange,
            expandPolicy = ExpandPolicy.InsideOnly
        )
    }
}
// [END android_compose_text_rich_text_paragraph_styling]

private fun TextFieldBuffer.trimSpanStyleInPlace(style: TrackedRange<SpanStyle>) {
    // [START android_compose_text_rich_text_tracked_range_trim]
    // Trim an existing bold span in place when the selection overlaps one edge
    val range = style.textRange
    if (range.start < selection.min && range.end <= selection.max) {
        // Selection covers the end of the span - shrink end boundary in place
        style.textRange = TextRange(range.start, selection.min)
    } else if (range.start >= selection.min && range.end > selection.max) {
        // Selection covers the start of the span - shrink start boundary in place
        style.textRange = TextRange(selection.max, range.end)
    }
    // [END android_compose_text_rich_text_tracked_range_trim]
}

private fun queryBoldStyles(state: TextFieldState) {
    // [START android_compose_text_rich_text_query_styles]
    state.edit {
        if (selection.collapsed) return@edit
        // Query all bold spans that overlap with the current selection
        val boldStyles: List<TrackedRange<SpanStyle>> = getSpanStyles(selection).filter {
            it.spanStyle.fontWeight == FontWeight.Bold
        }
        // [START_EXCLUDE]
        if (boldStyles.isEmpty()) {
            addStyle(
                spanStyle = SpanStyle(fontWeight = FontWeight.Bold),
                range = selection,
                expandPolicy = ExpandPolicy.AtEnd
            )
        }
        // [END_EXCLUDE]
    }
    // [END android_compose_text_rich_text_query_styles]
}

// [START android_compose_text_rich_text_toggle_bold]
fun toggleBold(state: TextFieldState) {
    state.edit {
        if (selection.collapsed) return@edit
        // Query all bold spans that overlap with the current selection
        val boldStyles: List<TrackedRange<SpanStyle>> = getSpanStyles(selection).filter {
            it.spanStyle.fontWeight == FontWeight.Bold
        }
        if (boldStyles.isNotEmpty()) {
            // Remove or trim existing bold spans covering the selection
            for (style in boldStyles) {
                val range = style.textRange
                if (range.start >= selection.min && range.end <= selection.max) {
                    removeStyle(style)
                } else if (range.start < selection.min && range.end > selection.max) {
                    val oldEnd = range.end
                    style.textRange = TextRange(range.start, selection.min)
                    addStyle(
                        spanStyle = SpanStyle(fontWeight = FontWeight.Bold),
                        range = TextRange(selection.max, oldEnd),
                        expandPolicy = ExpandPolicy.AtEnd
                    )
                } else if (range.start < selection.min) {
                    style.textRange = TextRange(range.start, selection.min)
                } else {
                    style.textRange = TextRange(selection.max, range.end)
                }
            }
        } else {
            // No bold spans intersect the selection - apply bold
            addStyle(
                spanStyle = SpanStyle(fontWeight = FontWeight.Bold),
                range = selection,
                expandPolicy = ExpandPolicy.AtEnd
            )
        }
    }
}
// [END android_compose_text_rich_text_toggle_bold]

// [START android_compose_text_rich_text_to_annotated_string]
fun TextFieldState.toAnnotatedString(): AnnotatedString {
    val fullRange = TextRange(0, text.length)
    return AnnotatedString(
        text = text.toString(),
        spanStyles = textStyles.getSpanStyles(fullRange),
        paragraphStyles = textStyles.getParagraphStyles(fullRange)
    )
}
// [END android_compose_text_rich_text_to_annotated_string]
