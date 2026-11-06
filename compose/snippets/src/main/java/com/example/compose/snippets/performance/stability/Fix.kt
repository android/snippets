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

package com.example.compose.snippets.performance.stability

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

private object SnackImmutableSetExample {
    // [START android_compose_stability_fix_snack_immutable_set]
    data class Snack(
        // ...
        val tags: ImmutableSet<String> = persistentSetOf()
        // ...
    )
    // [END android_compose_stability_fix_snack_immutable_set]
}

private object SnackAnnotatedImmutableExample {
    // [START android_compose_stability_fix_snack_annotated_immutable]
    @Immutable
    data class Snack(
        // [START_EXCLUDE]
        val id: Long,
        val name: String,
        val imageUrl: String,
        val price: Long,
        val tagline: String = "",
        val tags: Set<String> = emptySet()
        // [END_EXCLUDE]
    )
    // [END android_compose_stability_fix_snack_annotated_immutable]
}

private object HighlightedSnacksImmutableListExample {
    // [START android_compose_stability_fix_highlighted_snacks_immutable_list]
    @Composable
    private fun HighlightedSnacks(
        // ...
        snacks: ImmutableList<Snack>,
        // ...
    )
    // [END android_compose_stability_fix_highlighted_snacks_immutable_list]
    {}
}

private object SnackCollectionWrapperExample {
    // [START android_compose_stability_fix_snack_collection_wrapper]
    @Immutable
    data class SnackCollection(
        val snacks: List<Snack>
    )
    // [END android_compose_stability_fix_snack_collection_wrapper]

    // [START android_compose_stability_fix_highlighted_snacks_wrapper]
    @Composable
    private fun HighlightedSnacks(
        index: Int,
        snacks: SnackCollection,
        onSnackClick: (Long) -> Unit,
        modifier: Modifier = Modifier
    )
    // [END android_compose_stability_fix_highlighted_snacks_wrapper]
    {}
}

private data class Snack(val id: Long)
