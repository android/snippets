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

package com.example.wear.snippets.widget.skill

// [START android_wear_widget_skill_grid_actions]
import android.graphics.Bitmap
import androidx.compose.remote.creation.compose.action.Action
import androidx.compose.remote.creation.compose.layout.RemoteAlignment
import androidx.compose.remote.creation.compose.layout.RemoteArrangement
import androidx.compose.remote.creation.compose.layout.RemoteBox
import androidx.compose.remote.creation.compose.layout.RemoteColumn
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.layout.RemoteImage
import androidx.compose.remote.creation.compose.layout.RemoteRow
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.background
import androidx.compose.remote.creation.compose.modifier.clickable
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.modifier.padding
import androidx.compose.remote.creation.compose.modifier.size
import androidx.compose.remote.creation.compose.state.RemoteImageBitmap
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.remote.creation.compose.state.rdp
import androidx.compose.remote.creation.compose.state.rs
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.wear.compose.remote.material3.RemoteButton
import androidx.wear.compose.remote.material3.RemoteText

@RemoteComposable
@Composable
fun GridWithBottomActionLayout(
    items: List<Pair<String, Bitmap?>>,
    isLarge: Boolean,
    onItemClick: Action,
    onMoreClick: Action,
) {
    val visibleItems = if (isLarge) items.take(6) else items.take(3)
    val rows = visibleItems.chunked(3)

    RemoteColumn(
        modifier = RemoteModifier.fillMaxSize().padding(8.rdp),
        verticalArrangement = RemoteArrangement.Center,
        horizontalAlignment = RemoteAlignment.CenterHorizontally,
    ) {
        for (rowItems in rows) {
            RemoteRow(
                horizontalArrangement = RemoteArrangement.spacedBy(8.rdp),
                verticalAlignment = RemoteAlignment.CenterVertically,
            ) {
                for ((label, avatar) in rowItems) {
                    RemoteBox(
                        modifier = RemoteModifier.size(36.rdp)
                            .background(Color(0xFF4F378B).rc)
                            .clickable(onItemClick),
                        contentAlignment = RemoteAlignment.Center,
                    ) {
                        if (avatar != null) {
                            RemoteImage(
                                remoteBitmap =
                                    RemoteImageBitmap(avatar.asImageBitmap()),
                                contentDescription = label.rs,
                                modifier = RemoteModifier.fillMaxSize(),
                            )
                        } else {
                            RemoteText(text = label.rs, color = Color.White.rc)
                        }
                    }
                }
            }
        }
        RemoteButton(
            onClick = onMoreClick,
            modifier = RemoteModifier.padding(top = 6.rdp),
        ) {
            RemoteText(text = "More".rs)
        }
    }
}
// [END android_wear_widget_skill_grid_actions]
