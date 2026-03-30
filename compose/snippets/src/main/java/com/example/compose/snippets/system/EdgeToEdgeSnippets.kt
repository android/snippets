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

package com.example.compose.snippets.system

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fitInside
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.WindowInsetsRulers
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.view.WindowCompat
import com.example.compose.snippets.touchinput.Button

@Composable
fun ScaffoldPaddingValues(){
    // [START android_compose_e2e_scaffold_padding_values]
    Scaffold { innerPadding ->
        // innerPadding accounts for system bars and any Scaffold components
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding),
            contentPadding = innerPadding
        ) { /* Content */ }
    }
    // [END android_compose_e2e_scaffold_padding_values]
}

@Composable
fun ComposableWithoutScaffold(){
    // [START android_compose_e2e_outside_scaffold]
    Box(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
    ) {
        Button(
            onClick = {},
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text("Login")
        }
    }
    // [END android_compose_e2e_outside_scaffold]
}

@Composable
fun ImeWithScaffoldsExamples(){
    // [START android_compose_e2e_ime_right_1]
    // RIGHT
    Scaffold(contentWindowInsets = WindowInsets.safeDrawing) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .verticalScroll(rememberScrollState())
        ) { /* Content */ }
    }
    // [END android_compose_e2e_ime_right_1]

    // [START android_compose_e2e_ime_right_2]
    // RIGHT
    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .fitInside(WindowInsetsRulers.Ime.current)
                .verticalScroll(rememberScrollState())
        ) { /* Content */ }
    }
    // [END android_compose_e2e_ime_right_2]

    // [START android_compose_e2e_ime_right_3]
    // RIGHT
    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .consumeWindowInsets(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) { /* Content */ }
    }
    // [END android_compose_e2e_ime_right_3]

    // [START android_compose_e2e_ime_wrong_1]
    // WRONG
    Scaffold( contentWindowInsets = WindowInsets.safeDrawing ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .imePadding()
                .verticalScroll(rememberScrollState())
        ) { /* Content */ }
    }
    // [END android_compose_e2e_ime_wrong_1]

    // [START android_compose_e2e_ime_wrong_2]
    // WRONG
    Scaffold() { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
        ) { /* Content */ }
    }
    // [END android_compose_e2e_ime_wrong_2]
}

@Composable
fun ImeWithoutScaffoldsExamples(){

    // [START android_compose_e2e_ime_no_scaffold_right_1]
    // RIGHT
    Box(
        // Insets consumed
        modifier = Modifier.safeDrawingPadding() // or imePadding(), safeContentPadding(), safeGesturesPadding()
    ) {
        Column(
            modifier = Modifier.imePadding()
        ) { /* Content */ }
    }
    // [END android_compose_e2e_ime_no_scaffold_right_1]

    // [START android_compose_e2e_ime_no_scaffold_right_2]
    // RIGHT
    Box(
        // Insets consumed
        modifier = Modifier.windowInsetsPadding(WindowInsets.safeDrawing) // or WindowInsets.ime, WindowInsets.safeContent, WindowInsets.safeGestures
    ) {
        Column(
            modifier = Modifier.imePadding()
        ) { /* Content */ }
    }
    // [END android_compose_e2e_ime_no_scaffold_right_2]

    // [START android_compose_e2e_ime_no_scaffold_right_3]
    // RIGHT
    Box(
        // Insets not consumed, but irrelevant due to fitInside
        modifier = Modifier.padding(WindowInsets.safeDrawing.asPaddingValues()) // or WindowInsets.ime.asPaddingValues(), WindowInsets.safeContent.asPaddingValues(), WindowInsets.safeGestures.asPaddingValues()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .fitInside(WindowInsetsRulers.Ime.current)
        ) { /* Content */ }
    }
    // [END android_compose_e2e_ime_no_scaffold_right_3]

    // [START android_compose_e2e_ime_no_scaffold_wrong_1]
    // WRONG
    Box(
        // Insets not consumed
        modifier = Modifier.padding(WindowInsets.safeDrawing.asPaddingValues()) // or WindowInsets.ime.asPaddingValues(), WindowInsets.safeContent.asPaddingValues(), WindowInsets.safeGestures.asPaddingValues()
    ) {
        Column(
            modifier = Modifier.imePadding()
        ) { /* Content */ }
    }
    // [END android_compose_e2e_ime_no_scaffold_wrong_1]
}

// [START android_compose_e2e_system_bar_contrast]
// Only use if calling `enableEdgeToEdge` from `WindowCompat`.
// Apply to your theme file.
@Composable
fun MyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window ?: return@SideEffect
            val controller = WindowCompat.getInsetsController(window, view)

            // Dark icons for Light Mode (!darkTheme), Light icons for Dark Mode
            controller.isAppearanceLightStatusBars = !darkTheme
            controller.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(content = content)
}
// [END android_compose_e2e_system_bar_contrast]

@Composable
fun DialogExample(){
    // [START android_compose_e2e_dialog]
    Dialog(
        onDismissRequest = { /* Handle dismiss */ },
        properties = DialogProperties(
            // 1. Allows the dialog to span the full width of the screen
            usePlatformDefaultWidth = false,
            // 2. Allows the dialog to draw behind status and navigation bars
            decorFitsSystemWindows = false
        )
    ) { /* Content */ }
    // [END android_compose_e2e_dialog]
}
