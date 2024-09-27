/*
 * Copyright 2024 The Android Open Source Project
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

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

// [START android_compose_layouts_nested_scrolling]
@Composable
fun NestedScrollingRowsList(urls: List<String>) {
    LazyColumn {
        items(10) {
            LazyRow {
                item { Text("Row: $it") }
                items(urls.size) { index ->
                    // AsyncImage provided by Coil.
                    AsyncImage(
                        model = urls[index],
                        modifier = Modifier.size(150.dp),
                        contentDescription = null
                    )
                }
            }
        }
    }
}
// [END android_compose_layouts_nested_scrolling]
