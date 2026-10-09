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

import androidx.compose.animation.core.Transition
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.computedStateOf
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.neverEqualPolicy
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
private fun StateOffsetBad(scrollState: ScrollState) {
    // [START android_compose_performance_state_offset_bad]
    val offset = scrollState.value
    Box(modifier = Modifier.offset(x = offset.dp, y = 0.dp))
    // [END android_compose_performance_state_offset_bad]
}

@Composable
private fun StateOffsetGood(scrollState: ScrollState) {
    // [START android_compose_performance_state_offset_good]
    Box(modifier = Modifier.offset { IntOffset(scrollState.value, 0) })
    // [END android_compose_performance_state_offset_good]
}

@Composable
private fun StateAlphaBad(targetValue: Float) {
    // [START android_compose_performance_state_alpha_bad]
    val alpha by animateFloatAsState(targetValue)
    Box(modifier = Modifier.alpha(alpha))
    // [END android_compose_performance_state_alpha_bad]
}

@Composable
private fun StateAlphaGood(targetValue: Float) {
    // [START android_compose_performance_state_alpha_good]
    val alpha by animateFloatAsState(targetValue)
    Box(modifier = Modifier.graphicsLayer { this.alpha = alpha })
    // [END android_compose_performance_state_alpha_good]
}

// [START android_compose_performance_state_param_direct]
@Composable
fun TitleText(text: String) {
    Text(text = text)
}
// [END android_compose_performance_state_param_direct]

// [START android_compose_performance_state_param_lambda]
@Composable
fun FadingBox(alphaProvider: () -> Float) {
    Box(Modifier.graphicsLayer { alpha = alphaProvider() })
}
// [END android_compose_performance_state_param_lambda]

// [START android_compose_performance_state_backwards_write_bad]
@Composable
fun BadCounter() {
    var count by remember { mutableIntStateOf(0) }
    Text("Count: $count") // 1. State read in Composition
    Button(onClick = {}) {
        // 2. State write in Composition AFTER read (Backwards write!)
        count++
    }
}
// [END android_compose_performance_state_backwards_write_bad]

// [START android_compose_performance_state_redundant_wrapper]
// NOT a backwards write because the write happens BEFORE any read in
// Composition (and before Canvas reads it in the Draw phase).
// However, wrapping a synchronous value in mutableStateOf is redundant
// and error-prone if a read is later added before the write.
@Composable
fun RedundantStateWrapper(viewModel: MyViewModel) {
    var path by remember { mutableStateOf(Path()) }
    // 1. Write occurs first (no prior read in scope)
    path = viewModel.getPath()
    Canvas(Modifier.fillMaxSize()) {
        // 2. Read occurs later in Draw phase (Forward flow)
        drawPath(path, Color.Red)
    }
}

// Optimized: Remove redundant mutableStateOf wrapper and read in Draw
@Composable
fun OptimizedWrapper(viewModel: MyViewModel) {
    Canvas(Modifier.fillMaxSize()) {
        val path = viewModel.getPath()
        drawPath(path, Color.Red)
    }
}
// [END android_compose_performance_state_redundant_wrapper]

// [START android_compose_performance_state_layout_write_bad]
@Composable
fun BadLayout() {
    var componentHeight by remember { mutableStateOf(0.dp) }

    // Composition reads state before Layout measures it
    if (componentHeight > 100.dp) {
        Banner()
    }

    Box(
        modifier = Modifier.onSizeChanged { size ->
            // Backwards write: Layout -> Composition
            componentHeight = size.height.dp
        }
    )
}
// [END android_compose_performance_state_layout_write_bad]

// [START android_compose_performance_state_layout_write_good]
@Composable
fun OptimizedLayout(modifier: Modifier = Modifier) {
    // Measurement and placement handled in Layout without recomposition
    Box(
        modifier = modifier.layout { measurable, constraints ->
            val placeable = measurable.measure(constraints)
            layout(placeable.width, placeable.height) {
                placeable.placeRelative(0, 0)
            }
        }
    )
}
// [END android_compose_performance_state_layout_write_good]

// [START android_compose_performance_state_never_equal_policy]
class ActiveQueueManager {
    private val list = mutableListOf<Item>()
    val items = mutableStateOf(list, neverEqualPolicy())

    fun enqueue(item: Item) {
        list.add(item)
        items.value = list // Load-bearing assignment triggers recomposition
    }
}
// [END android_compose_performance_state_never_equal_policy]

private object SnapshotListBad {
    // [START android_compose_performance_state_snapshot_list_bad]
    @Composable
    fun ItemSelector(
        items: List<Item>,
        selectedIds: SnapshotStateList<String>,
    ) {
        Column {
            items.forEach { item ->
                // Avoid: Repeatedly querying the snapshot list inside the loop
                val isSelected = selectedIds.contains(item.id)
                ItemRow(item = item, isSelected = isSelected)
            }
        }
    }
    // [END android_compose_performance_state_snapshot_list_bad]
}

private object SnapshotListGood {
    // [START android_compose_performance_state_snapshot_list_good]
    @Composable
    fun ItemSelector(
        items: List<Item>,
        selectedIds: SnapshotStateList<String>,
    ) {
        // Convert to a local Set once per recomposition for fast O(1) lookups
        val selectedSet = selectedIds.toSet()
        Column {
            items.forEach { item ->
                val isSelected = selectedSet.contains(item.id)
                ItemRow(item = item, isSelected = isSelected)
            }
        }
    }
    // [END android_compose_performance_state_snapshot_list_good]
}

@Composable
private fun RepeatedReadsBad(transition: Transition<State>): String {
    // [START android_compose_performance_state_repeated_reads_bad]
    val label = when (transition.currentState) {
        State.Idle -> "Idle"
        State.Running -> "Running: ${transition.currentState}"
        State.Finished -> "Finished: ${transition.currentState}"
    }
    // [END android_compose_performance_state_repeated_reads_bad]
    return label
}

@Composable
private fun RepeatedReadsGood(transition: Transition<State>): String {
    // [START android_compose_performance_state_repeated_reads_good]
    val currentState = transition.currentState
    val label = when (currentState) {
        State.Idle -> "Idle"
        State.Running -> "Running: $currentState"
        State.Finished -> "Finished: $currentState"
    }
    // [END android_compose_performance_state_repeated_reads_good]
    return label
}

// [START android_compose_performance_state_computed_state_of]
@Composable
fun ScrollToTopButton(scrollState: ScrollState) {
    // Compose 1.13+: Invalidates composition only when > 0 changes,
    // with minimal invalidation overhead compared to derivedStateOf.
    // On Compose < 1.13, use remember { derivedStateOf { ... } }.
    val showButton by remember { computedStateOf { scrollState.value > 0 } }

    if (showButton) {
        FloatingActionButton(onClick = { /* ... */ }) {
            Icon(Icons.Default.ArrowUpward, "Scroll to Top")
        }
    }
}
// [END android_compose_performance_state_computed_state_of]

private class PlainGetterExample {
    // [START android_compose_performance_state_plain_getter]
    var isLocked by mutableStateOf(false)
    val isReady: Boolean get() = !isLocked
    // [END android_compose_performance_state_plain_getter]
}

@Composable
private fun ProduceStateDerivationBad(scrollState: LazyListState) {
    // [START android_compose_performance_state_produce_state_bad]
    val isScrolledToTop by produceState(initialValue = true, scrollState) {
        snapshotFlow { scrollState.firstVisibleItemIndex == 0 }
            .collect { value = it }
    }
    // [END android_compose_performance_state_produce_state_bad]
}

@Composable
private fun ComputedDerivedGood1(scrollState: LazyListState) {
    // [START android_compose_performance_state_computed_derived_good]
    // Compose 1.13+:
    val isScrolledToTop by remember {
        computedStateOf { scrollState.firstVisibleItemIndex == 0 }
    }
    // [START_EXCLUDE silent]
}

@Composable
private fun ComputedDerivedGood2(scrollState: LazyListState) {
    // [END_EXCLUDE]
    // Compose < 1.13:
    val isScrolledToTop by remember {
        derivedStateOf { scrollState.firstVisibleItemIndex == 0 }
    }
    // [END android_compose_performance_state_computed_derived_good]
}

@OptIn(ExperimentalMaterial3Api::class)
private object ParameterUnwrappingBad {
    // [START android_compose_performance_stability_unwrapping_bad]
    // Bad: Both HomeScreen (via data class .equals()) and HomeContent evaluate
    // O(N) structural equality on the same ImmutableList in a single frame
    data class HomeScreenModel(
        val header: String,
        val items: ImmutableList<ItemModel>
    )

    @Composable
    private fun HomeScreen(model: HomeScreenModel) {
        TopAppBar(title = { Text(model.header) })
        // Unwrapping model.items to pass to a child @Composable
        HomeContent(model.items)
    }

    // Because contentList is an ImmutableList, HomeContent must re-evaluate O(N)
    // .equals() on the list itself, even though HomeScreen already compared model
    @Composable
    private fun HomeContent(contentList: ImmutableList<ItemModel>) {
        LazyColumn {
            items(contentList) { /* ... */ }
        }
    }
    // [END android_compose_performance_stability_unwrapping_bad]
}

private object StabilityFeedGood {
    // [START android_compose_performance_stability_feed_good]
    // Unstable model skips using === pointer check when instance is unchanged
    data class FeedState(
        val feedId: String,
        val items: List<FeedItem>
    )

    @Composable
    fun FeedScreen(state: FeedState) {
        // Pass only the primitive ID needed by the header
        FeedHeader(feedId = state.feedId)
        // Pass the state or items directly
        FeedList(items = state.items)
    }
    // [END android_compose_performance_stability_feed_good]
}

@Composable
private fun RememberDerivedKeysBad(scrollState: LazyListState) {
    // [START android_compose_performance_remember_derived_keys]
    // Bad: passing snapshot state property as a key breaks derivedStateOf
    val showButton by remember(scrollState.firstVisibleItemIndex) {
        derivedStateOf { scrollState.firstVisibleItemIndex > 0 }
    }
    // [START_EXCLUDE silent]
}

@Composable
private fun RememberDerivedKeysGood(scrollState: LazyListState) {
    // [END_EXCLUDE]

    // Optimized: derivedStateOf tracks firstVisibleItemIndex internally
    val showButton by remember {
        derivedStateOf { scrollState.firstVisibleItemIndex > 0 }
    }
    // [END android_compose_performance_remember_derived_keys]
}

private object RememberKeysBad {
    // [START android_compose_performance_remember_keys_bad]
    @Composable
    fun FormattedDateLabel(timestamp: Long, locale: Locale) {
        val formattedDate = remember {
            SimpleDateFormat("yyyy-MM-dd", locale).format(Date(timestamp))
        }
        Text(text = formattedDate)
    }
    // [END android_compose_performance_remember_keys_bad]
}

private object RememberKeysGood {
    // [START android_compose_performance_remember_keys_good]
    @Composable
    fun FormattedDateLabel(timestamp: Long, locale: Locale) {
        val formattedDate = remember(timestamp, locale) {
            SimpleDateFormat("yyyy-MM-dd", locale).format(Date(timestamp))
        }
        Text(text = formattedDate)
    }
    // [END android_compose_performance_remember_keys_good]
}

class MyViewModel {
    fun getPath(): Path = Path()
}

@Composable
private fun Banner() {}

data class Item(val id: String)

@Composable
private fun ItemRow(item: Item, isSelected: Boolean) {}

private enum class State {
    Idle,
    Running,
    Finished,
}

private typealias ImmutableList<T> = List<T>

private data class ItemModel(val id: String)

@Composable
private fun FeedHeader(feedId: String) {}

@Composable
private fun FeedList(items: List<FeedItem>) {}
