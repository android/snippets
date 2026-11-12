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

package com.example.compose.snippets.lists

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.layout.LazyLayoutCacheWindow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp

@Composable
fun CacheWindowConfiguration(itemsList: List<String>) {
    // [START android_compose_lists_cache_window_configuration]
    // Fraction-based cache window: prefetch 50% of viewport ahead, keep 20% behind
    LazyColumn(
        cacheWindow = LazyLayoutCacheWindow(aheadFraction = 0.5f, behindFraction = 0.2f)
    ) {
        items(itemsList) { item -> Text("Item $item") }
    }

    // Dp-based cache window: prefetch 150.dp ahead, keep 50.dp behind
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        cacheWindow = LazyLayoutCacheWindow(
            ahead = 150.dp,
            behind = 50.dp,
            isNonScrollCachingEnabled = true
        )
    ) {
        items(itemsList) { item -> Text("Item $item") }
    }
    // [END android_compose_lists_cache_window_configuration]
}

private object CacheWindowInterface {
    // [START android_compose_lists_cache_window_interface]
    interface LazyLayoutCacheWindow {
        fun Density.calculateAheadWindow(viewport: Int): Int = 0
        fun Density.calculateBehindWindow(viewport: Int): Int = 0
        val isNonScrollCachingEnabled: Boolean get() = false
    }
    // [END android_compose_lists_cache_window_interface]
}

typealias LayoutState = LazyListState

private fun LayoutState.specialDynamicCalculation(): Int =
    layoutInfo.visibleItemsInfo.firstOrNull()?.size ?: 0

// [START android_compose_lists_cache_window_custom]
class ScaledCacheWindow(
    val someLayoutState: LayoutState
) : LazyLayoutCacheWindow {
    override fun Density.calculateAheadWindow(viewport: Int): Int {
        return someLayoutState.specialDynamicCalculation().coerceAtMost(viewport * 5)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ScaledCacheWindow) return false
        return someLayoutState == other.someLayoutState
    }

    override fun hashCode(): Int {
        return someLayoutState.hashCode()
    }
}

@Composable
fun ScaledCacheWindowSample() {
    val state = rememberLazyListState()
    val newCacheWindow = remember { ScaledCacheWindow(state) }
    LazyColumn(
        state = state,
        cacheWindow = newCacheWindow,
    ) {
        items(100) { index ->
            Text(
                text = "Item #$index",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            )
        }
    }
}
// [END android_compose_lists_cache_window_custom]

@OptIn(ExperimentalFoundationApi::class)
@Suppress("DEPRECATION")
@Composable
fun CacheWindowMigrationBefore(itemsList: List<String>) {
    // [START android_compose_lists_cache_window_migration_before]
    // Before (Experimental in Compose 1.9 to 1.12, now Deprecated)
    val state = rememberLazyListState(
        cacheWindow = LazyLayoutCacheWindow(aheadFraction = 0.5f, behindFraction = 0.2f)
    )
    LazyColumn(state = state) {
        items(itemsList) { item -> Text("Item $item") }
    }
    // [END android_compose_lists_cache_window_migration_before]
}

@Composable
fun CacheWindowMigrationAfter(itemsList: List<String>) {
    // [START android_compose_lists_cache_window_migration_after]
    // Now in Compose 1.13 (Stable)
    val state = rememberLazyListState()
    LazyColumn(
        state = state,
        cacheWindow = LazyLayoutCacheWindow(aheadFraction = 0.5f, behindFraction = 0.2f)
    ) {
        items(itemsList) { item -> Text("Item $item") }
    }
    // [END android_compose_lists_cache_window_migration_after]
}

@Composable
fun CacheWindowLegacyPrefetch(itemsList: List<String>) {
    // [START android_compose_lists_cache_window_legacy_prefetch]
    val state = rememberLazyListState()
    val legacyCacheWindow = remember(state) {
        object : LazyLayoutCacheWindow {
            override fun Density.calculateAheadWindow(viewport: Int): Int {
                val visibleItems = state.layoutInfo.visibleItemsInfo
                if (visibleItems.isEmpty()) return 0
                val averageItemHeight = visibleItems.sumOf { it.size } / visibleItems.size
                return averageItemHeight
            }
        }
    }
    LazyColumn(
        state = state,
        cacheWindow = legacyCacheWindow
    ) {
        items(itemsList) { item -> Text("Item $item") }
    }
    // [END android_compose_lists_cache_window_legacy_prefetch]
}
