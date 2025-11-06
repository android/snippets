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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.example.datastore.snippets.MainActivity.Snippets.JsonDataStore
import com.example.datastore.snippets.MainActivity.Snippets.LandingPage
import com.example.datastore.snippets.MainActivity.Snippets.MultiProcessDataStore
import com.example.datastore.snippets.MainActivity.Snippets.PreferencesDataStore
import com.example.datastore.snippets.MainActivity.Snippets.ProtoDataStore
import com.example.datastore.snippets.json.JsonDataStoreScreen
import com.example.datastore.snippets.multiprocess.MultiProcessDataStoreScreen
import com.example.datastore.snippets.preferences.PreferencesDataStoreScreen
import com.example.datastore.snippets.proto.ProtoDataStoreScreen
import com.example.datastore.snippets.ui.theme.SnippetsTheme

class MainActivity : ComponentActivity() {

    internal enum class Snippets {
        LandingPage,
        PreferencesDataStore,
        ProtoDataStore,
        JsonDataStore,
        MultiProcessDataStore,
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SnippetsTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val backStack = remember { mutableStateListOf(LandingPage) }
                    NavDisplay(
                        modifier = Modifier.padding(innerPadding),
                        backStack = backStack
                    ) {
                        when (it) {
                            LandingPage -> NavEntry(LandingPage) {
                                Home(backStack)
                            }

                            PreferencesDataStore -> NavEntry(PreferencesDataStore) {
                                PreferencesDataStoreScreen()
                            }

                            ProtoDataStore -> NavEntry(ProtoDataStore) {
                                ProtoDataStoreScreen()
                            }

                            JsonDataStore -> NavEntry(JsonDataStore) {
                                JsonDataStoreScreen()
                            }

                            MultiProcessDataStore -> NavEntry(MultiProcessDataStore) {
                                MultiProcessDataStoreScreen()
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    SnippetsTheme {
        Greeting("Android")
    }
}