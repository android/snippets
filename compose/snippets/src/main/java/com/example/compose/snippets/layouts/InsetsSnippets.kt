/*
 * Copyright 2023 The Android Open Source Project
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

@file:Suppress("unused")

package com.example.compose.snippets.layouts

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsBottomHeight
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

class InsetSnippetActivity : ComponentActivity() {

    // [START android_compose_insets_app_wide_safe_drawing]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            Box(Modifier.safeDrawingPadding()) {
                // the rest of the app
            }
        }
    }
    // [END android_compose_insets_app_wide_safe_drawing]
}

@Preview
@Composable
fun SpacerHeightSnippet() {
    // [START android_compose_insets_spacer_height]
    LazyColumn(
        Modifier.imePadding()
    ) {
        // Other content
        item {
            Spacer(
                Modifier.windowInsetsBottomHeight(
                    WindowInsets.systemBars
                )
            )
        }
    }
    // [END android_compose_insets_spacer_height]
}

@Preview
@Composable
fun ConsumedFromSiblingsSnippet() {
    // [START android_compose_insets_consumed_from_siblings]
    Column(Modifier.verticalScroll(rememberScrollState())) {
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.systemBars))

        Column(
            Modifier.consumeWindowInsets(
                WindowInsets.systemBars.only(WindowInsetsSides.Vertical)
            )
        ) {
            // content
            Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.ime))
        }

        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.systemBars))
    }
    // [END android_compose_insets_consumed_from_siblings]
}

@Preview
@Composable
fun ConsumedFromPaddingSnippet() {
    // [START android_compose_insets_consumed_from_padding]
    Column(Modifier.padding(16.dp).consumeWindowInsets(PaddingValues(16.dp))) {
        // content
        Spacer(Modifier.windowInsetsBottomHeight(WindowInsets.ime))
    }
    // [END android_compose_insets_consumed_from_padding]
}

@Preview
@Composable
fun M3SupportScaffoldSnippet() {
    val colors = listOf(Color.Red, Color.Blue, Color.Yellow)
    // [START android_compose_insets_m3_scaffold]
    Scaffold { innerPadding ->
        // innerPadding contains inset information for you to use and apply
        LazyColumn(
            // consume insets as scaffold doesn't do it by default
            modifier = Modifier.consumeWindowInsets(innerPadding),
            contentPadding = innerPadding
        ) {
            items(count = 100) {
                Box(
                    Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .background(colors[it % colors.size])
                )
            }
        }
    }
    // [END android_compose_insets_m3_scaffold]
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun OverrideDefaultInsetsSnippet() {
    // [START android_compose_insets_override_defaults]
    LargeTopAppBar(
        windowInsets = WindowInsets(0, 0, 0, 0),
        title = {
            Text("Hi")
        }
    )
    // [END android_compose_insets_override_defaults]
}
