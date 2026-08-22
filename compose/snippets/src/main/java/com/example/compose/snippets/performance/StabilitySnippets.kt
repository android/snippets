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

@file:Suppress("unused", "UNUSED_PARAMETER", "UNUSED_VARIABLE")

package com.example.compose.snippets.performance

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DontMemoize
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.NonSkippableComposable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.collections.immutable.ImmutableList
import kotlinx.collections.immutable.ImmutableSet
import kotlinx.collections.immutable.persistentSetOf

private object StabilityContactImmutable {
    // [START android_compose_performance_stability_contact_immutable]
    data class Contact(val name: String, val number: String)
    // [END android_compose_performance_stability_contact_immutable]
}

private object StabilityContactRow {
    data class Contact(val name: String, val number: String)
    @Composable fun ContactDetails(contact: Contact) {}
    @Composable fun ToggleButton(selected: Boolean, onToggled: () -> Unit) {}

    // [START android_compose_performance_stability_contact_row]
    @Composable
    fun ContactRow(contact: Contact, modifier: Modifier = Modifier) {
       var selected by remember { mutableStateOf(false) }

       Row(modifier) {
          ContactDetails(contact)
          ToggleButton(selected, onToggled = { selected = !selected })
       }
    }
    // [END android_compose_performance_stability_contact_row]
}

private object StabilityContactMutable {
    // [START android_compose_performance_stability_contact_mutable]
    data class Contact(var name: String, var number: String)
    // [END android_compose_performance_stability_contact_mutable]
}

private object StabilityDiagnoseSnack {
    // [START android_compose_performance_stability_diagnose_snack]
    data class Snack(
        val id: Long,
        val name: String,
        val imageUrl: String,
        val price: Long,
        val tagline: String = "",
        val tags: Set<String> = emptySet()
    )
    // [END android_compose_performance_stability_diagnose_snack]
}

private object StabilityFixImmutableSet {
    // [START android_compose_performance_stability_snack_immutable_set]
    data class Snack(
        // [START_EXCLUDE]
        val id: Long = 0L,
        val name: String = "",
        val imageUrl: String = "",
        val price: Long = 0L,
        val tagline: String = "",
        // [END_EXCLUDE]
        // ...
        val tags: ImmutableSet<String> = persistentSetOf()
        // ...
    )
    // [END android_compose_performance_stability_snack_immutable_set]
}

private object StabilityFixImmutableAnnotation {
    // [START android_compose_performance_stability_snack_immutable_annotation]
    @Immutable
    data class Snack(
        // [START_EXCLUDE]
        val id: Long = 0L,
        val name: String = "",
        val imageUrl: String = "",
        val price: Long = 0L,
        val tagline: String = "",
        val tags: Set<String> = emptySet()
        // [END_EXCLUDE]
        // ...
    )
    // [END android_compose_performance_stability_snack_immutable_annotation]
}

private object StabilityFixImmutableList {
    data class Snack(val id: Long = 0L)

    // [START android_compose_performance_stability_highlighted_snacks_immutable_list]
    @Composable
    private fun HighlightedSnacks(
        // [START_EXCLUDE]
        index: Int = 0,
        // [END_EXCLUDE]
        // ...
        snacks: ImmutableList<Snack>,
        // ...
        // [START_EXCLUDE]
        onSnackClick: (Long) -> Unit = {},
        modifier: Modifier = Modifier
        // [END_EXCLUDE]
    ) /* [START_EXCLUDE] */ { } /* [END_EXCLUDE] */
    // [END android_compose_performance_stability_highlighted_snacks_immutable_list]
}

private object StabilityFixSnackCollection {
    data class Snack(val id: Long = 0L)

    // [START android_compose_performance_stability_snack_collection]
    @Immutable
    data class SnackCollection(
       val snacks: List<Snack>
    )
    // [END android_compose_performance_stability_snack_collection]
}

private object StabilityFixHighlightedSnacksWrapper {
    data class Snack(val id: Long = 0L)
    @Immutable
    data class SnackCollection(val snacks: List<Snack>)

    // [START android_compose_performance_stability_highlighted_snacks_wrapper]
    @Composable
    private fun HighlightedSnacks(
        index: Int,
        snacks: SnackCollection,
        onSnackClick: (Long) -> Unit,
        modifier: Modifier = Modifier
    ) /* [START_EXCLUDE] */ { } /* [END_EXCLUDE] */
    // [END android_compose_performance_stability_highlighted_snacks_wrapper]
}

private object StabilityStrongSkippingNonSkippable {
    // [START android_compose_performance_stability_non_skippable_composable]
    @NonSkippableComposable
    @Composable
    fun MyNonSkippableComposable() {}
    // [END android_compose_performance_stability_non_skippable_composable]
}

private object StabilityStrongSkippingMemoizationBefore {
    class Unstable
    class Stable
    fun use(obj: Any) {}

    // [START android_compose_performance_stability_memoization_before]
    @Composable
    fun MyComposable(unstableObject: Unstable, stableObject: Stable) {
        val lambda = {
            use(unstableObject)
            use(stableObject)
        }
    }
    // [END android_compose_performance_stability_memoization_before]
}

private object StabilityStrongSkippingMemoizationAfter {
    class Unstable
    class Stable
    fun use(obj: Any) {}

    // [START android_compose_performance_stability_memoization_after]
    @Composable
    fun MyComposable(unstableObject: Unstable, stableObject: Stable) {
        val lambda = remember(unstableObject, stableObject) {
            {
                use(unstableObject)
                use(stableObject)
            }
        }
    }
    // [END android_compose_performance_stability_memoization_after]
}

private object StabilityStrongSkippingDontMemoize {
    @Composable
    fun Demo() {
        // [START android_compose_performance_stability_dont_memoize]
        val lambda = @DontMemoize {
            // ...
        }
        // [END android_compose_performance_stability_dont_memoize]
    }
}
