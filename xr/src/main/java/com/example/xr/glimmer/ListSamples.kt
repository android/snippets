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

package com.example.xr.glimmer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.ListItem
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.TitleChip
import androidx.xr.glimmer.list.VerticalList
import androidx.xr.glimmer.list.items

// [START xr_glimmer_list]
@Composable
fun VerticalListSample() {
    VerticalList(
        title = { TitleChip { Text("List Section Header") } },
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            ListItem {
                Text("Static Header Item")
            }
        }

        items(count = 5) { index ->
            ListItem {
                Text("Dynamic Item #$index")
            }
        }

        item {
            ListItem {
                Text("Static Footer Item")
            }
        }
    }
}
// [END xr_glimmer_list]
