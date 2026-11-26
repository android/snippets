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

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import kotlinx.collections.immutable.ImmutableList

private object SnackCategoryExample {
    // [START android_compose_stability_fix_snack_category]
    data class SnackCategory(
        val name: String,
        val snacks: ImmutableList<Snack>
    )

    @Composable
    fun CategorySection(category: SnackCategory) {
        Text(category.name)
        // Compose compares category.snacks twice: once in SnackCategory.equals()
        // for CategorySection, and again for HighlightedSnacks.
        HighlightedSnacks(snacks = category.snacks)
    }
    // [END android_compose_stability_fix_snack_category]

    @Composable
    private fun HighlightedSnacks(snacks: ImmutableList<Snack>) {}
}

private object SnackSummaryAnnotatedImmutableExample {
    // [START android_compose_stability_fix_snack_annotated_immutable]
    @Immutable
    data class SnackSummary(
        val id: Long,
        val name: String,
        val price: Long
    )
    // [END android_compose_stability_fix_snack_annotated_immutable]
}

private data class Snack(val id: Long)
