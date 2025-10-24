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

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.datastore.snippets.json.JsonDataStoreScreen
import com.example.datastore.snippets.multiprocess.MultiProcessDataStoreScreen
import com.example.datastore.snippets.preferences.PreferencesDataStoreScreen
import com.example.datastore.snippets.proto.ProtoDataStoreScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class DataStoreSnippetsTest {
    @get:Rule
    val rule = createComposeRule()

    @Test
    fun launchPreferencesDataStore() {
        rule.setContent { PreferencesDataStoreScreen() }
        rule.onNodeWithText("Preferences DataStore").assertExists()
    }

    @Test
    fun launchProtoDataStore() {
        rule.setContent { ProtoDataStoreScreen() }
        rule.onNodeWithText("Proto DataStore").assertExists()
    }

    @Test
    fun launchJsonDataStore() {
        rule.setContent { JsonDataStoreScreen() }
        rule.onNodeWithText("Json DataStore").assertExists()
    }

    @Test
    fun launchMultiProcessDataStore() {
        rule.setContent { MultiProcessDataStoreScreen() }
        rule.onNodeWithText("Multi-process DataStore").assertExists()
    }
}
