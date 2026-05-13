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

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.TitleChip
import androidx.xr.glimmer.TitleChipDefaults

private val FavoriteIcon = Icons.Default.Favorite

// [START xr_glimmer_title_chip]
@Composable
fun BasicTitleChipSample() {
    TitleChip { Text("Messages") }
}
// [END xr_glimmer_title_chip]


// [START xr_glimmer_title_chip_with_icon]
@Composable
fun TitleChipWithIconSample() {
    TitleChip {
        Icon(FavoriteIcon, contentDescription = "Favorite")
        Text("Favorites")
    }
}
// [END xr_glimmer_title_chip_with_icon]


// [START xr_glimmer_title_chip_with_card]
@Composable
fun TitleChipWithCardSample() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        TitleChip { Text("Title Chip") }

        Spacer(Modifier.height(TitleChipDefaults.associatedContentSpacing))

        Card(
            title = { Text("Title") },
            subtitle = { Text("Subtitle") },
            leadingIcon = { Icon(FavoriteIcon, "Localized description") },
        ) {
            Text("Card Content")
        }
    }
}
// [END xr_glimmer_title_chip_with_card]
