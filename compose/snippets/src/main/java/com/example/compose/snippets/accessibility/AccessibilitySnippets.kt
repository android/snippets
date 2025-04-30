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

@file:Suppress("unused", "UNUSED_PARAMETER", "ClassName")

package com.example.compose.snippets.accessibility

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
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
import androidx.compose.ui.semantics.CollectionInfo
import androidx.compose.ui.semantics.CollectionItemInfo
import androidx.compose.ui.semantics.CustomAccessibilityAction
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.collectionInfo
import androidx.compose.ui.semantics.collectionItemInfo
import androidx.compose.ui.semantics.customActions
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.hideFromAccessibility
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.state.ToggleableState
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
                .toggleable(
                    value = checked,
                    role = Role.Checkbox,
                    onValueChange = { checked = !checked }
                )
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
                .size(1.dp)
        )
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
                .sizeIn(minWidth = 48.dp, minHeight = 48.dp)
        )
    }
}
// [END android_compose_accessibility_box_min_size]

// [START android_compose_accessibility_click_label]
@Composable
private fun ArticleListItem(openArticle: () -> Unit = {}) {
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
    Canvas(
        Modifier.semantics {
            onClick(label = readArticleLabel, action = openArticle)
        }
    ) {
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
    Row(
        modifier = Modifier
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
                stateDescription = if (selected) stateSubscribed else stateNotSubscribed
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

// [START android_compose_accessibility_traversal_isTraversalGroup_initial]
// CardBox() function takes in top and bottom sample text.
@Composable
fun CardBox(
    topSampleText: String,
    bottomSampleText: String,
    modifier: Modifier = Modifier
) {
    Box(modifier) {
        Column {
            Text(topSampleText)
            Text(bottomSampleText)
        }
    }
}

@Composable
fun TraversalGroupDemo() {
    val topSampleText1 = "This sentence is in "
    val bottomSampleText1 = "the left column."
    val topSampleText2 = "This sentence is "
    val bottomSampleText2 = "on the right."
    Row {
        CardBox(
            topSampleText1,
            bottomSampleText1
        )
        CardBox(
            topSampleText2,
            bottomSampleText2
        )
    }
}
// [END android_compose_accessibility_traversal_isTraversalGroup_initial]

// [START android_compose_accessibility_traversal_isTraversalGroup_modified]
@Composable
fun TraversalGroupDemo2() {
    val topSampleText1 = "This sentence is in "
    val bottomSampleText1 = "the left column."
    val topSampleText2 = "This sentence is"
    val bottomSampleText2 = "on the right."
    Row {
        CardBox(
//      1,
            topSampleText1,
            bottomSampleText1,
            Modifier.semantics { isTraversalGroup = true }
        )
        CardBox(
//      2,
            topSampleText2,
            bottomSampleText2,
            Modifier.semantics { isTraversalGroup = true }
        )
    }
}
// [END android_compose_accessibility_traversal_isTraversalGroup_modified]

private object ClockFaceBefore {
    // [START android_compose_accessibility_traversal_clock_face_initial]
    @Composable
    fun ClockFaceDemo() {
        CircularLayout {
            repeat(12) { hour ->
                ClockText(hour)
            }
        }
    }

    @Composable
    private fun ClockText(value: Int) {
        Box(modifier = Modifier) {
            Text((if (value == 0) 12 else value).toString())
        }
    }
    // [END android_compose_accessibility_traversal_clock_face_initial]

    @Composable
    private fun CircularLayout(content: @Composable () -> Unit) {
    }
}

private object ClockFaceAfter {
    // [START android_compose_accessibility_traversal_clock_face_modified]
    @Composable
    fun ClockFaceDemo() {
        CircularLayout(Modifier.semantics { isTraversalGroup = true }) {
            repeat(12) { hour ->
                ClockText(hour)
            }
        }
    }

    @Composable
    private fun ClockText(value: Int) {
        Box(modifier = Modifier.semantics { this.traversalIndex = value.toFloat() }) {
            Text((if (value == 0) 12 else value).toString())
        }
    }
    // [END android_compose_accessibility_traversal_clock_face_modified]

    @Composable
    private fun CircularLayout(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    }
}

// [START android_compose_accessibility_traversal_fab]
@Composable
fun FloatingBox() {
    Box(modifier = Modifier.semantics { isTraversalGroup = true; traversalIndex = -1f }) {
        FloatingActionButton(onClick = {}) {
            Icon(imageVector = Icons.Default.Add, contentDescription = "fab icon")
        }
    }
}
// [END android_compose_accessibility_traversal_fab]

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun InteractiveElements(
    openArticle: () -> Unit = {},
    addToBookmarks: () -> Unit = {},
) {
// [START android_compose_accessibility_interactive_clickable]
    Row(
        // Uses `mergeDescendants = true` under the hood
        modifier = Modifier.clickable { openArticle() }
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_logo),
            contentDescription = "Open",
        )
        Text("Accessibility in Compose")
    }
// [END android_compose_accessibility_interactive_clickable]

// [START android_compose_accessibility_interactive_click_label]
    Row(
        modifier = Modifier
            .clickable(onClickLabel = "Open this article") {
                openArticle()
            }
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_logo),
            contentDescription = "Open"
        )
        Text("Accessibility in Compose")
    }
// [END android_compose_accessibility_interactive_click_label]

// [START android_compose_accessibility_interactive_long_click]
    Row(
        modifier = Modifier
            .combinedClickable(
                onLongClickLabel = "Bookmark this article",
                onLongClick = { addToBookmarks() },
                onClickLabel = "Open this article",
                onClick = { openArticle() },
            )
    ) {}
// [END android_compose_accessibility_interactive_long_click]
}

// [START android_compose_accessibility_interactive_nested_click]
@Composable
private fun ArticleList(openArticle: () -> Unit) {
    NestedArticleListItem(
        // Clickable is set separately, in a nested layer:
        onClickAction = openArticle,
        // Semantics are set here:
        modifier = Modifier.semantics {
            onClick(
                label = "Open this article",
                action = {
                    // Not needed here: openArticle()
                    true
                }
            )
        }
    )
}
// [END android_compose_accessibility_interactive_nested_click]

@Composable
private fun NestedArticleListItem(
    onClickAction: () -> Unit,
    modifier: Modifier = Modifier,
) {
}

@Composable
private fun Semantics(
    removeArticle: () -> Unit,
    openArticle: () -> Unit,
    addToBookmarks: () -> Unit,
) {

// [START android_compose_accessibility_semantics_alert_polite]
    PopupAlert(
        message = "You have a new message",
        modifier = Modifier.semantics {
            liveRegion = LiveRegionMode.Polite
        }
    )
// [END android_compose_accessibility_semantics_alert_polite]

// [START android_compose_accessibility_semantics_alert_assertive]
    PopupAlert(
        message = "Emergency alert incoming",
        modifier = Modifier.semantics {
            liveRegion = LiveRegionMode.Assertive
        }
    )
// [END android_compose_accessibility_semantics_alert_assertive]

    Box() {
// [START android_compose_accessibility_semantics_window]
        ShareSheet(
            message = "Choose how to share this photo",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .semantics { paneTitle = "New bottom sheet" }
        )
// [END android_compose_accessibility_semantics_window]
    }

// [START android_compose_accessibility_semantics_error]
    Error(
        errorText = "Fields cannot be empty",
        modifier = Modifier
            .semantics {
                error("Please add both email and password")
            }
    )
// [END android_compose_accessibility_semantics_error]

    val progress by remember { mutableFloatStateOf(0F) }
// [START android_compose_accessibility_semantics_progress]
    ProgressInfoBar(
        modifier = Modifier
            .semantics {
                progressBarRangeInfo =
                    ProgressBarRangeInfo(
                        current = progress,
                        range = 0F..1F
                    )
            }
    )
// [END android_compose_accessibility_semantics_progress]

    val milkyWay = List(10) { it.toString() }
// [START android_compose_accessibility_semantics_long_list]
    MilkyWayList(
        modifier = Modifier
            .semantics {
                collectionInfo = CollectionInfo(
                    rowCount = milkyWay.count(),
                    columnCount = 1
                )
            }
    ) {
        milkyWay.forEachIndexed { index, text ->
            Text(
                text = text,
                modifier = Modifier.semantics {
                    collectionItemInfo =
                        CollectionItemInfo(index, 0, 0, 0)
                }
            )
        }
    }
// [END android_compose_accessibility_semantics_long_list]

// [START android_compose_accessibility_semantics_custom_action_swipe]
    SwipeToDismissBox(
        modifier = Modifier.semantics {
            // Represents the swipe to dismiss for accessibility
            customActions = listOf(
                CustomAccessibilityAction(
                    label = "Remove article from list",
                    action = {
                        removeArticle()
                        true
                    }
                )
            )
        },
        state = rememberSwipeToDismissBoxState(),
        backgroundContent = {}
    ) {
        ArticleListItem()
    }
// [END android_compose_accessibility_semantics_custom_action_swipe]

// [START android_compose_accessibility_semantics_custom_action_long_list]
    ArticleListItemRow(
        modifier = Modifier
            .semantics {
                customActions = listOf(
                    CustomAccessibilityAction(
                        label = "Open article",
                        action = {
                            openArticle()
                            true
                        }
                    ),
                    CustomAccessibilityAction(
                        label = "Add to bookmarks",
                        action = {
                            addToBookmarks()
                            true
                        }
                    ),
                )
            }
    ) {
        Article(
            modifier = Modifier.clearAndSetSemantics { },
            onClick = openArticle,
        )
        BookmarkButton(
            modifier = Modifier.clearAndSetSemantics { },
            onClick = addToBookmarks,
        )
    }
// [END android_compose_accessibility_semantics_custom_action_long_list]
}

@Composable
private fun PopupAlert(
    message: String,
    modifier: Modifier = Modifier,
) {
}

@Composable
fun ShareSheet(
    message: String,
    modifier: Modifier = Modifier,
) {
}

@Composable
private fun Error(
    errorText: String,
    modifier: Modifier = Modifier,
) {
}

@Composable
private fun ProgressInfoBar(
    modifier: Modifier = Modifier,
) {
}

@Composable
private fun MilkyWayList(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    content()
}

@Composable
private fun ArticleListItemRow(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    content()
}

@Composable
fun Article(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
}

@Composable
fun BookmarkButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
}

// [START android_compose_accessibility_merging]
@Composable
private fun ArticleListItem(
    openArticle: () -> Unit,
    addToBookmarks: () -> Unit,
) {

    Row(modifier = Modifier.clickable { openArticle() }) {
        // Merges with parent clickable:
        Icon(
            painter = painterResource(R.drawable.ic_logo),
            contentDescription = "Article thumbnail"
        )
        ArticleDetails()

        // Defies the merge due to its own clickable:
        BookmarkButton(onClick = addToBookmarks)
    }
}
// [END android_compose_accessibility_merging]

@Composable
fun ArticleDetails(
    modifier: Modifier = Modifier,
) {
}

// [START android_compose_accessibility_clearing]
// Developer might intend this to be a toggleable.
// Using `clearAndSetSemantics`, on the Row, a clickable modifier is applied,
// a custom description is set, and a Role is applied.

@Composable
fun FavoriteToggle() {
    val checked = remember { mutableStateOf(true) }
    Row(
        modifier = Modifier
            .toggleable(
                value = checked.value,
                onValueChange = { checked.value = it }
            )
            .clearAndSetSemantics {
                stateDescription = if (checked.value) "Favorited" else "Not favorited"
                toggleableState = ToggleableState(checked.value)
                role = Role.Switch
            },
    ) {
        Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null // not needed here

        )
        Text("Favorite?")
    }
}
// [END android_compose_accessibility_clearing]

// [START android_compose_accessibility_hiding]
@Composable
fun WatermarkExample(
    watermarkText: String,
    content: @Composable () -> Unit,
) {
    Box {
        WatermarkedContent()
        // Mark the watermark as hidden to accessibility services.
        WatermarkText(
            text = watermarkText,
            color = Color.Gray.copy(alpha = 0.5f),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .semantics { hideFromAccessibility() }
        )
    }
}

@Composable
fun DecorativeExample() {
    Text(
        modifier =
        Modifier.semantics {
            hideFromAccessibility()
        },
        text = "A dot character that is used to decoratively separate information, like •"
    )
}
// [END android_compose_accessibility_hiding]

@Composable
private fun WatermarkedContent() {
}

@Composable
private fun WatermarkText(
    text: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
}

private object ColumnWithFab {
    // [START android_compose_accessibility_traversal_fab_scaffold]
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun ColumnWithFABFirstDemo() {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Top App Bar") }) },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = { FloatingBox() },
            content = { padding -> ContentColumn(padding = padding) },
            bottomBar = { BottomAppBar { Text("Bottom App Bar") } }
        )
    }
    // [END android_compose_accessibility_traversal_fab_scaffold]

    @Composable
    private fun ContentColumn(padding: PaddingValues) {
    }
}

private class Post(val imageThumb: Painter? = null)
private class Metadata(
    val author: Author = Author(),
    val date: String? = null,
    val readTimeMinutes: String? = null
)

private class Author(val name: String = "fake")
private class BookmarkButton(isBookmarked: Boolean, onClick: () -> Boolean, modifier: Modifier)
