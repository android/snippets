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

package com.example.datastore.snippets.json

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Preview(showBackground = true)
@Composable
internal fun JsonDataStoreScreen() {
    Column(Modifier.fillMaxSize()) {
        Text(
            text = "Json DataStore",
            fontSize = 30.sp
        )
        // [START android_datastore_json_compose_sample]
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val jsonDataStore = remember(context) { JsonDataStore(context) }

        // Display counter value.
        val exampleCounter by jsonDataStore.counterFlow()
            .collectAsState(initial = 0, coroutineScope.coroutineContext)
        Text(
            text = "Counter $exampleCounter",
            fontSize = 25.sp
        )

        // Update the counter.
        Button(onClick = { coroutineScope.launch { jsonDataStore.incrementCounter() } }) {
            Text("increment")
        }
        // [END android_datastore_json_compose_sample]
    }
}