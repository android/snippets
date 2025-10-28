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

package com.example.datastore.snippets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun Home(backStack: MutableList<MainActivity.Snippets>) {
    Column {
        Item("Preferences Data Store") {
            backStack.add(MainActivity.Snippets.PreferencesDataStore)
        }
        Item("Proto Data Store") {
            backStack.add(MainActivity.Snippets.ProtoDataStore)
        }
        Item("Json Data Store") {
            backStack.add(MainActivity.Snippets.JsonDataStore)
        }
        Item("Multi process Data Store") {
            backStack.add(MainActivity.Snippets.MultiProcessDataStore)
        }
    }
}

@Composable
private fun Item(text: String, onClick: () -> Unit) {
    Box(
        Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        Text(fontSize = 30.sp, text = text)
    }
}