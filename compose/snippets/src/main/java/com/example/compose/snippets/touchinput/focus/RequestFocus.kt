/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.compose.snippets.touchinput.focus

import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp

@Composable
fun RequestFocusExamples(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Section("Request focus", modifier = modifier) {
        RequestFocus2()
        SubSection("Focus redirection") {
            FocusRedirection(
                onClick = onClick,
                modifier = Modifier.focusGroup(),
            )
        }
    }
}

@Composable
fun FocusRedirection(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    val thirdCard = remember { FocusRequester() }

    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // [START android_compose_touchinput_focus_redirection]
        Card(onClick = onClick) {
            Text(text = "1st card", modifier = Modifier.padding(32.dp))
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .focusProperties {
                    onEnter = {
                        thirdCard.requestFocus()
                    }
                }
                .focusGroup()
        ) {
            Card(onClick = onClick) {
                Text(text = "2nd card", modifier = Modifier.padding(32.dp))
            }
            Card(onClick = onClick, modifier = Modifier.focusRequester(thirdCard)) {
                Text(text = "3rd card", modifier = Modifier.padding(32.dp))
            }
            Card(onClick = onClick) {
                Text(text = "4th card", modifier = Modifier.padding(32.dp))
            }
        }
        // [END android_compose_touchinput_focus_redirection]
    }
}
