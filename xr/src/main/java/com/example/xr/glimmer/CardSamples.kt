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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.xr.glimmer.ActionCard
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Icon
import androidx.xr.glimmer.Text

private val FavoriteIcon = Icons.Default.Favorite

@Composable
fun CardSample() {
    // [START androidxr_glimmer_card]
    Card { Text("This is a card") }
    // [END androidxr_glimmer_card]
}

@Composable
fun ActionCardSample(myHeaderImage: Painter) {
    // [START androidxr_glimmer_action_card]
    ActionCard(
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
    // [END androidxr_glimmer_action_card]
}

@Composable
fun CardWithTitleAndLeadingIconAndHeaderAndAction(myHeaderImage: Painter) {
    // [START androidxr_glimmer_complex_card]
    ActionCard(
        action = {
            Button(onClick = {}, trailingIcon = { Icon(FavoriteIcon, "Localized description") }) {
                Text("Send")
            }
        },
        title = { Text("Title") },
        leadingIcon = { Icon(FavoriteIcon, "Localized description") },
        header = {
            Image(myHeaderImage, "Localized description", contentScale = ContentScale.FillWidth)
        },
    ) {
        Text("This is a card with a title, leading icon, header image, and action")
    }
    // [END androidxr_glimmer_complex_card]
}
