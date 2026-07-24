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

package com.example.tv.playback

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ListItem
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Switch
import androidx.tv.material3.Text

// [START android_compose_tv_settings]
@Composable
fun TvSettingsScreen(
    modifier: Modifier = Modifier
) {
    var autoPlayNext by remember { mutableStateOf(true) }
    var highQualityAudio by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(48.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            item {
                ListItem(
                    selected = false,
                    onClick = { autoPlayNext = !autoPlayNext },
                    headlineContent = { Text("Autoplay Next Video") },
                    supportingContent = { Text("Automatically start playing next item in queue") },
                    trailingContent = {
                        Switch(
                            checked = autoPlayNext,
                            onCheckedChange = null
                        )
                    }
                )
            }

            item {
                ListItem(
                    selected = false,
                    onClick = { highQualityAudio = !highQualityAudio },
                    headlineContent = { Text("High Quality Audio") },
                    supportingContent = { Text("Use spatial audio and multi-channel output when available") },
                    trailingContent = {
                        Switch(
                            checked = highQualityAudio,
                            onCheckedChange = null
                        )
                    }
                )
            }
        }
    }
}
// [END android_compose_tv_settings]
