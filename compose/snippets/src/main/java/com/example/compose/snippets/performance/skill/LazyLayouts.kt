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

package com.example.compose.snippets.performance.skill

import android.content.Context
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
private fun LazyKeysAndContentType(itemList: List<LazyItem>) {
    // [START android_compose_performance_lazy_keys_content_type]
    LazyColumn {
        items(
            items = itemList,
            key = { item -> item.id },
            contentType = { item -> item.type }
        ) { item ->
            ItemRow(item = item)
        }
    }
    // [END android_compose_performance_lazy_keys_content_type]
}

@Composable
private fun LazyIndexScanBad(itemList: List<LazyItem>) {
    // [START android_compose_performance_lazy_index_bad]
    LazyColumn {
        items(items = itemList, key = { it.id }) { item ->
            // O(N) lookup inside each item!
            val index = itemList.indexOf(item)
            ItemRow(index = index, item = item)
        }
    }
    // [END android_compose_performance_lazy_index_bad]
}

@Composable
private fun LazyIndexScanGood(itemList: List<LazyItem>) {
    // [START android_compose_performance_lazy_index_good]
    LazyColumn {
        itemsIndexed(
            items = itemList,
            key = { _, item -> item.id },
        ) { index, item ->
            ItemRow(index = index, item = item)
        }
    }
    // [END android_compose_performance_lazy_index_good]
}

private object TopicSelectionBad {
    // [START android_compose_performance_lazy_selection_bad]
    @Composable
    fun TopicGrid(sections: List<TopicSection>) {
        val selectedTopicIds = remember { mutableStateListOf<String>() }
        LazyColumn {
            items(sections, key = { it.id }) { section ->
                Column {
                    Text(text = section.title)
                    section.topics.forEach { topic ->
                        // Avoid: O(N) List.contains() read in items() scope
                        // invalidates the whole section!
                        val isSelected = selectedTopicIds.contains(topic.id)
                        TopicChip(
                            topic = topic,
                            isSelected = isSelected,
                            onToggle = {
                                if (isSelected) {
                                    selectedTopicIds.remove(topic.id)
                                } else {
                                    selectedTopicIds.add(topic.id)
                                }
                            }
                        )
                    }
                }
            }
        }
    }
    // [END android_compose_performance_lazy_selection_bad]

    @Composable
    private fun TopicChip(
        topic: Topic,
        isSelected: Boolean,
        onToggle: () -> Unit,
    ) {}
}

private object TopicSelectionGood {
    // [START android_compose_performance_lazy_selection_good]
    @Composable
    fun TopicGrid(sections: List<TopicSection>) {
        var selectedTopicIds by remember {
            mutableStateOf(emptySet<String>())
        }
        LazyColumn {
            items(
                items = sections,
                key = { it.id },
                contentType = { "section" },
            ) { section ->
                Column {
                    Text(text = section.title)
                    section.topics.forEach { topic ->
                        TopicChip(
                            topic = topic,
                            // O(1) Set read deferred to TopicChip's scope
                            isSelectedProvider = {
                                selectedTopicIds.contains(topic.id)
                            },
                            onToggle = {
                                val selected =
                                    selectedTopicIds.contains(topic.id)
                                selectedTopicIds = if (selected) {
                                    selectedTopicIds - topic.id
                                } else {
                                    selectedTopicIds + topic.id
                                }
                            }
                        )
                    }
                }
            }
        }
    }

    @Composable
    fun TopicChip(
        topic: Topic,
        isSelectedProvider: () -> Boolean,
        onToggle: () -> Unit
    ) {
        // Read occurs inside leaf scope
        val isSelected = isSelectedProvider()
        val status = if (isSelected) "Selected" else "Unselected"
        Box(modifier = Modifier.clickable { onToggle() }) {
            Text(text = "${topic.title} ($status)")
        }
    }
    // [END android_compose_performance_lazy_selection_good]
}

@Composable
private fun SmallListBad() {
    // [START android_compose_performance_lazy_small_list_bad]
    LazyRow {
        items(items = listOf("Work", "Personal", "Family")) { tag ->
            FilterChip(tag = tag)
        }
    }
    // [END android_compose_performance_lazy_small_list_bad]
}

@Composable
private fun SmallListGood() {
    // [START android_compose_performance_lazy_small_list_good]
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf("Work", "Personal", "Family").forEach { tag ->
            FilterChip(tag = tag)
        }
    }
    // [END android_compose_performance_lazy_small_list_good]
}

private object HoistFlowBad {
    // [START android_compose_performance_lazy_hoist_flow_bad]
    @Composable
    fun FeedItem(item: Post) {
        // Avoid: Multiplied subscriptions across all visible items!
        val secondsSinceLastScroll by MainFeedIdleTracker
            .secondsSinceLastScrollFlow
            .collectAsState(0L)
        val isDwellTriggered = secondsSinceLastScroll >= 5L
        PostActions(isDwellTriggered = isDwellTriggered)
    }

    @Composable
    fun FeedList(posts: List<Post>) {
        LazyColumn {
            items(posts, key = { it.id }) { post ->
                FeedItem(post)
            }
        }
    }
    // [END android_compose_performance_lazy_hoist_flow_bad]
}

private object HoistFlowGood {
    // [START android_compose_performance_lazy_hoist_flow_good]
    @Composable
    fun FeedList(posts: List<Post>) {
        // Hoist once: Parent owns the single subscription
        val secondsSinceLastScroll by MainFeedIdleTracker
            .secondsSinceLastScrollFlow
            .collectAsStateWithLifecycle(0L)
        val isDwellTriggered = secondsSinceLastScroll >= 5L

        LazyColumn {
            items(posts, key = { it.id }) { post ->
                FeedItem(post = post, isDwellTriggered = isDwellTriggered)
            }
        }
    }

    @Composable
    fun FeedItem(post: Post, isDwellTriggered: Boolean) {
        PostActions(isDwellTriggered = isDwellTriggered)
    }
    // [END android_compose_performance_lazy_hoist_flow_good]
}

@OptIn(ExperimentalSharedTransitionApi::class)
private object CompositionLocalBad {
    // [START android_compose_performance_lazy_composition_local_bad]
    @Composable
    fun SnackItem(snack: Snack) {
        val sharedScope = LocalSharedTransitionScope.current
            ?: error("No scope")
        val context = LocalContext.current
        // ...
    }
    // [END android_compose_performance_lazy_composition_local_bad]
}

@OptIn(ExperimentalSharedTransitionApi::class)
private object CompositionLocalGood {
    // [START android_compose_performance_lazy_composition_local_good]
    @Composable
    fun SnackList(
        snacks: List<Snack>,
        sharedScope: SharedTransitionScope,
    ) {
        val context = LocalContext.current
        LazyColumn {
            items(snacks, key = { it.id }, contentType = { "snack" }) { snack ->
                SnackItem(
                    snack = snack,
                    sharedScope = sharedScope,
                    context = context,
                )
            }
        }
    }
    // [END android_compose_performance_lazy_composition_local_good]

    @Composable
    private fun SnackItem(
        snack: Snack,
        sharedScope: SharedTransitionScope,
        context: Context,
    ) {}
}

private data class LazyItem(val id: String, val type: String)

@Composable
private fun ItemRow(item: LazyItem) {}

@Composable
private fun ItemRow(index: Int, item: LazyItem) {}

private data class Topic(val id: String, val title: String)

private data class TopicSection(
    val id: String,
    val title: String,
    val topics: List<Topic>,
)

@Composable
private fun FilterChip(tag: String) {}

internal data class Post(
    val id: String,
    val title: String = "",
    val timestamp: Long = 0L,
)

private object MainFeedIdleTracker {
    val secondsSinceLastScrollFlow: Flow<Long> = emptyFlow()
}

@Composable
private fun PostActions(isDwellTriggered: Boolean) {}

private data class Snack(val id: String)

@OptIn(ExperimentalSharedTransitionApi::class)
private val LocalSharedTransitionScope =
    compositionLocalOf<SharedTransitionScope?> { null }
