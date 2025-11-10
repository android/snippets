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

package com.example.kotlin_samples

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.kotlin_samples.ui.theme.KotlinsamplesTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            KotlinsamplesTheme {
                // We replace the default Scaffold content with our new composable
                SampleScreen()
            }
        }
    }
}

@Composable
fun SampleScreen(modifier: Modifier = Modifier) {
    Scaffold(modifier = modifier.fillMaxSize()) { innerPadding ->
        // Get the output from our LanguageSamples.kt file
        val output = getLanguageSamplesOutput()

        // Use a scrolling Text element to display the results
        Text(
            text = output,
            modifier = Modifier
                .padding(innerPadding)
                .padding(16.dp) // Add some content padding
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Make content scrollable
        )
    }
}

@Preview(showBackground = true)
@Composable
fun SampleScreenPreview() {
    KotlinsamplesTheme {
        SampleScreen()
    }
}
