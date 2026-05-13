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

import androidx.compose.foundation.Image
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text

private val FavoriteIcon = Icons.Default.Favorite

// [START xr_glimmer_card]
@Composable
fun CardSample() {
    val myHeaderImage = painterResource(id = android.R.drawable.ic_menu_gallery)
    Card(
        title = { Text("Card Title", style = GlimmerTheme.typography.titleMedium) },
        subtitle = { Text("Sub-heading text", style = GlimmerTheme.typography.titleSmall) },
        leadingIcon = { Icon(FavoriteIcon, contentDescription = "Favorite") },
        header = {
            Image(
                painter = myHeaderImage,
                contentDescription = "Header image",
                contentScale = ContentScale.FillWidth
            )
        },
        action = {
            Button(onClick = { /* Handle action */ }) {
                Text("Action")
            }
        }
    ) {
        Text(
            "This is the main body content of the card, utilizing theme-tokens for consistent styling.",
            style = GlimmerTheme.typography.bodyMedium
        )
    }
}
// [END xr_glimmer_card]
