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

package com.example.wear.snippets.complication

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.Text
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService.Companion.EXTRA_CONFIG_COMPLICATION_ID
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService.Companion.EXTRA_CONFIG_COMPLICATION_TYPE
import androidx.wear.watchface.complications.datasource.ComplicationDataSourceService.Companion.EXTRA_CONFIG_DATA_SOURCE_COMPONENT

class ConfigurationActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // [START android_wear_complication_configuration_intent]
        // Keys defined on ComplicationDataSourceService
        val id = intent.getIntExtra(EXTRA_CONFIG_COMPLICATION_ID, -1)
        val type = intent.getIntExtra(EXTRA_CONFIG_COMPLICATION_TYPE, -1)
        val source = intent.getStringExtra(EXTRA_CONFIG_DATA_SOURCE_COMPONENT)
        // [END android_wear_complication_configuration_intent]
        setContent {
            ComplicationConfig(
                id = id,
                type = type,
                source = source
            )
        }
    }

    @Composable
    fun ComplicationConfig(
        modifier: Modifier = Modifier,
        id: Int,
        type: Int,
        source: String?
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceEvenly,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("ID: $id")
            Text("Type: $type")
            Text("Source: $source")
            Spacer(modifier = Modifier.height(4.dp))
            Button(onClick = {
                // [START android_wear_complication_configuration_finish]
                setResult(RESULT_OK) // Or RESULT_CANCELED to cancel configuration
                finish()
                // [END android_wear_complication_configuration_finish]
            }) {
                Text("Done!")
            }
        }
    }
}
