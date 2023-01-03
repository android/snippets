/*
 * Copyright 2022 The Android Open Source Project
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

@file:Suppress("unused", "UNUSED_PARAMETER", "UNUSED_VARIABLE")

package com.example.compose.snippets.accessibility

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.R

@Preview
// [START android_compose_accessibility_checkbox_expanded_touch_target]
@Composable
private fun CheckableCheckbox() {
    Checkbox(checked = true, onCheckedChange = {})
}
// [END android_compose_accessibility_checkbox_expanded_touch_target]

@Preview
// [START android_compose_accessibility_checkbox_no_touch_target]
@Composable
private fun NonClickableCheckbox() {
    Checkbox(checked = true, onCheckedChange = null)
}
// [END android_compose_accessibility_checkbox_no_touch_target]

@Preview
// [START android_compose_accessibility_checkable_row]
@Composable
private fun CheckableRow() {
    MaterialTheme {
        var checked by remember { mutableStateOf(false) }
        Row(
            Modifier
                .toggleable(value = checked,
                    role = Role.Checkbox,
                    onValueChange = { checked = !checked })
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Text("Option", Modifier.weight(1f))
            Checkbox(checked = checked, onCheckedChange = null)
        }
    }
}
// [END android_compose_accessibility_checkable_row]

@Preview
// [START android_compose_accessibility_box_touch_target]
@Composable
private fun SmallBox() {
    var clicked by remember { mutableStateOf(false) }
    Box(
        Modifier
            .size(100.dp)
            .background(if (clicked) Color.DarkGray else Color.LightGray)
    ) {
        Box(
            Modifier
                .align(Alignment.Center)
                .clickable { clicked = !clicked }
                .background(Color.Black)
                .size(1.dp))
    }
}
// [END android_compose_accessibility_box_touch_target]

@Preview
// [START android_compose_accessibility_box_min_size]
@Composable
private fun LargeBox() {
    var clicked by remember { mutableStateOf(false) }
    Box(
        Modifier
            .size(100.dp)
            .background(if (clicked) Color.DarkGray else Color.LightGray)
    ) {
        Box(
            Modifier
                .align(Alignment.Center)
                .clickable { clicked = !clicked }
                .background(Color.Black)
                .sizeIn(minWidth = 48.dp, minHeight = 48.dp))
    }
}
// [END android_compose_accessibility_box_min_size]

// [START android_compose_accessibility_click_label]
@Composable
private fun ArticleListItem(openArticle: () -> Unit) {
    Row(
        Modifier.clickable(
            // R.string.action_read_article = "read article"
            onClickLabel = stringResource(R.string.action_read_article),
            onClick = openArticle
        )
    ) {
        // ..
    }
}
// [END android_compose_accessibility_click_label]

// [START android_compose_accessibility_low_level_click]
@Composable
private fun LowLevelClickLabel(openArticle: () -> Boolean) {
    // R.string.action_read_article = "read article"
    val readArticleLabel = stringResource(R.string.action_read_article)
    Canvas(Modifier.semantics {
        onClick(label = readArticleLabel, action = openArticle)
    }) {
        // ..
    }
}
// [END android_compose_accessibility_low_level_click]

// [START android_compose_accessibility_content_descr]
@Composable
private fun ShareButton(onClick: () -> Unit) {
    IconButton(onClick = onClick) {
        Icon(
            imageVector = Icons.Filled.Share,
            contentDescription = stringResource(R.string.label_share)
        )
    }
}
// [END android_compose_accessibility_content_descr]

private object ContentDescrNull {
    // hard-code drawable id
    private object R {
        object drawable {
            const val placeholder_1_1 = 1
        }
    }

    // [START android_compose_accessibility_content_descr_null]
    @Composable
    private fun PostImage(post: Post, modifier: Modifier = Modifier) {
        val image = post.imageThumb ?: painterResource(R.drawable.placeholder_1_1)

        Image(
            painter = image,
            // Specify that this image has no semantic meaning
            contentDescription = null,
            modifier = modifier
                .size(40.dp, 40.dp)
                .clip(MaterialTheme.shapes.small)
        )
    }
// [END android_compose_accessibility_content_descr_null]
}

// [START android_compose_accessibility_merge]
@Composable
private fun PostMetadata(metadata: Metadata) {
    // Merge elements below for accessibility purposes
    Row(modifier = Modifier.semantics(mergeDescendants = true) {}) {
        Image(
            imageVector = Icons.Filled.AccountCircle,
            contentDescription = null // decorative
        )
        Column {
            Text(metadata.author.name)
            Text("${metadata.date} • ${metadata.readTimeMinutes} min read")
        }
    }
}
// [END android_compose_accessibility_merge]

// [START android_compose_accessibility_custom_action]
@Composable
private fun PostCardSimple(
    /* ... */
    isFavorite: Boolean,
    onToggleFavorite: () -> Boolean
) {
    val actionLabel = stringResource(
        if (isFavorite) R.string.unfavorite else R.string.favorite
    )
    Row(modifier = Modifier
        .clickable(onClick = { /* ... */ })
        .semantics {
            // Set any explicit semantic properties
            customActions = listOf(
                CustomAccessibilityAction(actionLabel, onToggleFavorite)
            )
        }
    ) {
        /* ... */
        BookmarkButton(
            isBookmarked = isFavorite,
            onClick = onToggleFavorite,
            // Clear any semantics properties set on this node
            modifier = Modifier.clearAndSetSemantics { }
        )
    }
}
// [END android_compose_accessibility_custom_action]

// [START android_compose_accessibility_state_descr]
@Composable
private fun TopicItem(itemTitle: String, selected: Boolean, onToggle: () -> Unit) {
    val stateSubscribed = stringResource(R.string.subscribed)
    val stateNotSubscribed = stringResource(R.string.not_subscribed)
    Row(
        modifier = Modifier
            .semantics {
                // Set any explicit semantic properties
                stateDescription = if(selected) stateSubscribed else stateNotSubscribed
            }
            .toggleable(
                value = selected,
                onValueChange = { onToggle() }
            )
    ) {
        /* ... */
    }
}
// [END android_compose_accessibility_state_descr]

// [START android_compose_accessibility_headings]
@Composable
private fun Subsection(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier.semantics { heading() }
    )
}
// [END android_compose_accessibility_headings]

private class Post(val imageThumb: Painter? = null)
private class Metadata(
    val author: Author = Author(), val date: String? = null, val readTimeMinutes: String? = null
)

private class Author(val name: String = "fake")
private class BookmarkButton(isBookmarked: Boolean, onClick: () -> Boolean, modifier: Modifier)