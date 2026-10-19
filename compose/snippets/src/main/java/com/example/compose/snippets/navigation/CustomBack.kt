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

package com.example.compose.snippets.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

// [START android_compose_navigation_custom_back_basic]
@Composable
fun MyScreen() {
    var isExpanded by remember { mutableStateOf(false) }

    // Intercept the back button or gesture only when isExpanded is true
    BackHandler(enabled = isExpanded) {
        // Collapsing the content disables this handler, so it does not
        // receive the next back event.
        isExpanded = false
    }

    if (isExpanded) {
        Text("Expanded content. Go back to collapse it.")
    } else {
        Button(onClick = { isExpanded = true }) {
            Text("Expand")
        }
    }
}
// [END android_compose_navigation_custom_back_basic]
