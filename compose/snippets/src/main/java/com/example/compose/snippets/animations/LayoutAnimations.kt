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

package com.example.compose.snippets.animations

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateBounds
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.unit.dp

// [START android_compose_animations_layout_animate_bounds]
@Composable
fun FlowTagList(tags: List<TagItem>, modifier: Modifier = Modifier) {
    LookaheadScope {
        FlowRow(
            modifier = modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            tags.forEach { tag ->
                key(tag.id) {
                    TagChip(
                        tag = tag,
                        modifier = Modifier.animateBounds(lookaheadScope = this@LookaheadScope),
                    )
                }
            }
        }
    }
}
// [END android_compose_animations_layout_animate_bounds]

// [START android_compose_animations_layout_target_content_z_index]
@Composable
fun HierarchicalNavigation(
    currentDestination: Destination,
    modifier: Modifier = Modifier,
) {
    AnimatedContent(
        targetState = currentDestination,
        transitionSpec = {
            val transform = if (targetState.depth > initialState.depth) {
                slideInHorizontally { it } togetherWith ExitTransition.KeepUntilTransitionsFinished
            } else {
                EnterTransition.None togetherWith slideOutHorizontally { it }
            }
            transform.apply {
                targetContentZIndex = targetState.depth.toFloat()
            }
        },
        label = "navigationTransition",
        modifier = modifier,
    ) { destination ->
        DestinationContent(destination)
    }
}
// [END android_compose_animations_layout_target_content_z_index]

// [START android_compose_animations_layout_placeholder_size]
@Composable
fun ExpandableCardFeed(
    isExpanded: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SharedTransitionLayout(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = isExpanded,
            label = "expandableCardTransition",
        ) { expanded ->
            Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
                HeaderCard(
                    isExpanded = expanded,
                    onToggle = onToggle,
                    modifier = Modifier.sharedBounds(
                        sharedContentState = rememberSharedContentState(key = "header-card"),
                        animatedVisibilityScope = this@AnimatedContent,
                        placeholderSize = SharedTransitionScope.PlaceholderSize.AnimatedSize,
                    ),
                )
                SiblingContentList(modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
// [END android_compose_animations_layout_placeholder_size]

// [START android_compose_animations_layout_render_in_overlay]
@Composable
fun SharedTransitionCanvas(
    onActionClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SharedTransitionLayout(modifier = modifier.fillMaxSize()) {
        Box(modifier = Modifier.fillMaxSize()) {
            MainGalleryContent(modifier = Modifier.fillMaxSize())
            FloatingActionButton(
                onClick = onActionClick,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
                    .renderInSharedTransitionScopeOverlay(zIndexInOverlay = 1f),
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Item")
            }
        }
    }
}
// [END android_compose_animations_layout_render_in_overlay]

// [START android_compose_animations_layout_remeasure_to_bounds]
@Composable
fun AdaptiveSummary(
    isDetailed: Boolean,
    modifier: Modifier = Modifier,
) {
    SharedTransitionLayout(modifier = modifier.fillMaxSize()) {
        AnimatedContent(targetState = isDetailed, label = "summaryMorph") { detailed ->
            SummaryView(
                detailed = detailed,
                modifier = Modifier.sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "adaptive-summary"),
                    animatedVisibilityScope = this@AnimatedContent,
                    resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                ),
            )
        }
    }
}
// [END android_compose_animations_layout_remeasure_to_bounds]

// [START android_compose_animations_layout_skip_to_lookahead_position]
@Composable
fun ExpandingSheetContainer(
    isExpanded: Boolean,
    onExpand: () -> Unit,
    onCollapse: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SharedTransitionLayout(modifier = modifier.fillMaxSize()) {
        AnimatedContent(
            targetState = isExpanded,
            label = "sheetTransition",
        ) { expanded ->
            if (expanded) {
                Column(
                    modifier = Modifier
                        .sharedBounds(
                            sharedContentState = rememberSharedContentState(key = "sheet"),
                            animatedVisibilityScope = this@AnimatedContent,
                            resizeMode = SharedTransitionScope.ResizeMode.RemeasureToBounds,
                        )
                        .clipToBounds()
                        .skipToLookaheadSize()
                        .skipToLookaheadPosition()
                        .fillMaxSize(),
                ) {
                    SheetHeader(onCollapse = onCollapse)
                    SheetBody(modifier = Modifier.weight(1f))
                }
            } else {
                Box(modifier = Modifier.fillMaxSize()) {
                    CompactSheetBar(
                        onExpand = onExpand,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .sharedBounds(
                                sharedContentState = rememberSharedContentState(key = "sheet"),
                                animatedVisibilityScope = this@AnimatedContent,
                            ),
                    )
                }
            }
        }
    }
}
// [END android_compose_animations_layout_skip_to_lookahead_position]

// Public because FlowTagList takes it as a parameter.
data class TagItem(val id: Int, val label: String)

@Composable
private fun TagChip(tag: TagItem, modifier: Modifier = Modifier) {
    Surface(modifier = modifier) {
        Text(text = tag.label, modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp))
    }
}

// Public because HierarchicalNavigation takes it as a parameter.
data class Destination(val title: String, val depth: Int)

@Composable
private fun DestinationContent(destination: Destination) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Text(text = destination.title, modifier = Modifier.padding(16.dp))
    }
}

@Composable
private fun HeaderCard(isExpanded: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    Card(modifier = modifier.fillMaxWidth().clickable(onClick = onToggle)) {
        Text(text = "Header", modifier = Modifier.padding(16.dp))
        if (isExpanded) {
            Text(text = "Details", modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
private fun SiblingContentList(modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        repeat(5) { index ->
            Text(text = "Item $index", modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
private fun MainGalleryContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier)
}

@Composable
private fun SummaryView(detailed: Boolean, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Text(text = "Summary", modifier = Modifier.padding(16.dp))
        if (detailed) {
            Text(text = "Details", modifier = Modifier.padding(16.dp))
        }
    }
}

@Composable
private fun SheetHeader(onCollapse: () -> Unit) {
    TextButton(onClick = onCollapse) {
        Text(text = "Collapse")
    }
}

@Composable
private fun SheetBody(modifier: Modifier = Modifier) {
    Box(modifier = modifier.fillMaxWidth())
}

@Composable
private fun CompactSheetBar(onExpand: () -> Unit, modifier: Modifier = Modifier) {
    Surface(modifier = modifier.height(64.dp).clickable(onClick = onExpand)) {
        Text(text = "Now playing", modifier = Modifier.padding(16.dp))
    }
}
