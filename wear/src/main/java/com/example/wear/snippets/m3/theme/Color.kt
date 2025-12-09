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

package com.example.wear.snippets.m3.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
// [START android_wear_color]
import androidx.wear.compose.material3.ColorScheme
// [START_EXCLUDE]
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.dynamicColorScheme


fun color() {
// [END_EXCLUDE]
    val appColorScheme: ColorScheme = ColorScheme(
        // M3 ColorScheme parameters
    )
    // [END android_wear_color]
}

// [START android_wear_dynamic_theme]
@Composable
fun myApp() {
    val dynamicColorScheme = dynamicColorScheme(LocalContext.current)
    MaterialTheme(colorScheme = dynamicColorScheme ?: myBrandColors) {}
}

internal val myBrandColors: ColorScheme = ColorScheme( /* Specify colors here */)
// [END android_wear_dynamic_theme]