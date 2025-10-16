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

package com.example.datastore.snippets.multiprocess

import android.content.Intent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.datastore.snippets.TimestampUpdateService

@Preview(showBackground = true)
@Composable
internal fun MultiProcessDataStoreScreen() {
    Column(Modifier.fillMaxSize()) {
        Text(
            text = "Multi-process DataStore",
            fontSize = 30.sp
        )

        // [START android_datastore_multiprocess_app]
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val multiProcessDataStore = remember(context) { MultiProcessDataStore(context) }

        // Display time written by other process.
        val lastUpdateTime by multiProcessDataStore.timeFlow()
            .collectAsState(initial = 0, coroutineScope.coroutineContext)
        Text(
            text = "Last updated: $lastUpdateTime",
            fontSize = 25.sp
        )

        DisposableEffect(context) {
            val serviceIntent = Intent(context, TimestampUpdateService::class.java)
            context.startService(serviceIntent)
            onDispose {
                context.stopService(serviceIntent)
            }
        }
        // [END android_datastore_multiprocess_app]
    }
}