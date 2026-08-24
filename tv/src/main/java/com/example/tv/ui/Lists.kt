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

package com.example.tv.ui

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.gestures.BringIntoViewSpec
import androidx.compose.foundation.gestures.LocalBringIntoViewSpec
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import com.example.tv.data.Movie
import com.example.tv.data.Section

// [START android_tv_compose_lists_movie_catalog]
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items

@Composable
fun MovieCatalog(movies: List<Movie>) {
    LazyRow {
        items(movies) { movie ->
            MovieCard(
                movie = movie,
                onClick = { /* Handle click */ }
            )
        }
    }
}
// [END android_tv_compose_lists_movie_catalog]

// [START android_tv_compose_lists_position_focused_item]
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PositionFocusedItemInLazyLayout(
    parentFraction: Float = 0.3f,
    childFraction: Float = 0f,
    content: @Composable () -> Unit,
) {
    val bringIntoViewSpec = remember(parentFraction, childFraction) {
        object : BringIntoViewSpec {
            override fun calculateScrollDistance(
                offset: Float,       // Item's initial position
                size: Float,         // Item's size
                containerSize: Float // Container's size
            ): Float {
                // Calculate the offset position of the item's leading edge.
                val initialTargetForLeadingEdge =
                    parentFraction * containerSize - (childFraction * size)
                // If the item fits in the container, and scrolling would cause
                // its trailing edge to be clipped, adjust targetForLeadingEdge
                // to prevent over-scrolling near the end of list.
                val targetForLeadingEdge = if (size <= containerSize &&
                    (containerSize - initialTargetForLeadingEdge) < size) {
                    // If clipped, align the item's trailing edge with the
                    // container's trailing edge.
                    containerSize - size
                } else {
                    initialTargetForLeadingEdge
                }
                // Return scroll distance relative to initial item position.
                return offset - targetForLeadingEdge
            }
        }
    }

    // Apply the spec to all scrollables in the hierarchy
    CompositionLocalProvider(
        LocalBringIntoViewSpec provides bringIntoViewSpec,
        content = content,
    )
}
// [END android_tv_compose_lists_position_focused_item]

@Composable
private fun CustomSpecCatalog(sectionList: List<Section>) {
    // [START android_tv_compose_lists_apply_custom_spec]
    PositionFocusedItemInLazyLayout(
        parentFraction = 0.3f, // Pivot 30% from the edge
        childFraction = 0.5f   // Center of the item aligns with the pivot
    ) {
        LazyColumn {
            items(sectionList) { section ->
                // This row and its items will respect the 30% pivot
                LazyRow { /* ... */ }
            }
        }
    }
    // [END android_tv_compose_lists_apply_custom_spec]
}

@OptIn(ExperimentalFoundationApi::class)
// [START android_tv_compose_lists_opt_out]
private val DefaultBringIntoViewSpec = object : BringIntoViewSpec {}

// [START_EXCLUDE silent]
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun OptOutCatalog() {
// [END_EXCLUDE]
PositionFocusedItemInLazyLayout {
    LazyColumn {
        item {
            // This row will ignore the custom pivot and use default behavior
            CompositionLocalProvider(LocalBringIntoViewSpec provides DefaultBringIntoViewSpec) {
                LazyRow { /* ... */ }
            }
        }
    }
}
// [END android_tv_compose_lists_opt_out]
}

