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
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.ToggleButton

private val FavoriteIcon = Icons.Default.Favorite
private val OutlinedFavoriteIcon = Icons.Outlined.FavoriteBorder

// [START xr_glimmer_toggle_button]
@Composable
fun ToggleButtonSample() {
    var checked by remember { mutableStateOf(false) }
    val text = if (checked) "Toggle on" else "Toggle off"

    ToggleButton(
        checked = checked,
        onCheckedChange = { checked = it }
    ) {
        Text(text)
    }
}
// [END xr_glimmer_toggle_button]


// [START xr_glimmer_toggle_button_with_icon]
@Composable
fun ToggleButtonWithLeadingIconSample() {
    var checked by remember { mutableStateOf(false) }

    ToggleButton(
        checked = checked,
        leadingIcon = {
            Icon(
                if (checked) FavoriteIcon else OutlinedFavoriteIcon,
                contentDescription = "Toggle favorite"
            )
        },
        onCheckedChange = { checked = it }
    ) {
        Text(if (checked) "On" else "Off")
    }
}
// [END xr_glimmer_toggle_button_with_icon]
