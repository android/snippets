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

import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier

private object ImmutableContactExample {
    // [START android_compose_stability_contact_immutable]
    data class Contact(val name: String, val number: String)
    // [END android_compose_stability_contact_immutable]

    // [START android_compose_stability_contact_row]
    @Composable
    fun ContactRow(contact: Contact, modifier: Modifier = Modifier) {
        var selected by remember { mutableStateOf(false) }

        Row(modifier) {
            ContactDetails(contact)
            ToggleButton(selected, onToggled = { selected = !selected })
        }
    }
    // [END android_compose_stability_contact_row]

    @Composable
    private fun ContactDetails(contact: Contact) {}

    @Composable
    private fun ToggleButton(selected: Boolean, onToggled: () -> Unit) {}
}

private object MutableContactExample {
    // [START android_compose_stability_contact_mutable]
    data class Contact(var name: String, var number: String)
    // [END android_compose_stability_contact_mutable]
}
