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

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.ListItem
import androidx.xr.glimmer.Text

private val FavoriteIcon = Icons.Default.Favorite

// [START xr_glimmer_list_item]
@Composable
fun ListItemSample() {
    ListItem { Text("This is a list item") }
}
// [END xr_glimmer_list_item]


// [START xr_glimmer_list_item_with_icons]
@Composable
fun ListItemWithIconsSample() {
    ListItem(
        leadingIcon = { Icon(FavoriteIcon, contentDescription = "Favorite") },
        trailingIcon = { Icon(FavoriteIcon, contentDescription = "Favorite") }
    ) {
        Text("This is a list item with icons")
    }
}
// [END xr_glimmer_list_item_with_icons]
