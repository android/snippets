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

package com.example.compose.snippets.layouts.grid

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
private fun TextCard(text: String, modifier: Modifier = Modifier, onClick: () -> Unit = {}) {
    Card(onClick = onClick, modifier = modifier) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text(text)
        }
    }
}

@Composable
internal fun Card1(modifier: Modifier = Modifier, onClick: () -> Unit  = {}) {
    TextCard(text = "#1", modifier = modifier, onClick = onClick)
}

@Composable
internal fun Card2(modifier: Modifier = Modifier, onClick: () -> Unit  = {}) {
    TextCard(text = "#2", modifier = modifier, onClick = onClick)
}

@Composable
internal fun Card3(modifier: Modifier = Modifier, onClick: () -> Unit  = {}) {
    TextCard(text = "#3", modifier = modifier, onClick = onClick)
}

@Composable
internal fun Card4(modifier: Modifier = Modifier, onClick: () -> Unit  = {}) {
    TextCard(text = "#4", modifier = modifier, onClick = onClick)
}

@Composable
internal fun Card5(modifier: Modifier = Modifier, onClick: () -> Unit  = {}) {
    TextCard(text = "#5", modifier = modifier, onClick = onClick)
}

@Composable
internal fun Card6(modifier: Modifier = Modifier, onClick: () -> Unit  = {}) {
    TextCard(text = "#6", modifier = modifier, onClick = onClick)
}

@Preview
@Composable
fun TextCardPreview() {
    TextCard("Preview")
}