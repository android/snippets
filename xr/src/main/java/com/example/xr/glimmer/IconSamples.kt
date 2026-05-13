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

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Icon

private val FavoriteIcon = Icons.Default.Favorite

// [START xr_glimmer_icon]
@Composable
fun IconSample() {
    Icon(FavoriteIcon, contentDescription = "Localized description")
}
// [END xr_glimmer_icon]


// [START xr_glimmer_icon_colored]
@Composable
fun ColoredIconSample() {
    Icon(
        FavoriteIcon,
        tint = GlimmerTheme.colors.primary,
        contentDescription = "Localized description",
    )
}
// [END xr_glimmer_icon_colored]


// [START xr_glimmer_icon_sized]
@Composable
fun SizedIconSample() {
    Icon(
        FavoriteIcon,
        contentDescription = "Localized description",
        modifier = Modifier.size(GlimmerTheme.iconSizes.large),
    )
}
// [END xr_glimmer_icon_sized]
