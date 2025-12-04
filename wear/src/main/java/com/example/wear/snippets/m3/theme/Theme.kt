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
import androidx.wear.compose.material3.ColorScheme
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.Shapes
import androidx.wear.compose.material3.Typography
import androidx.wear.compose.material3.MotionScheme

@Composable
fun materialTheme() {
    val appColorScheme = ColorScheme()
    val appTypography = Typography()
    val appShapes = Shapes()
    val appMotionScheme = MotionScheme.standard()
    // [START android_wear_material_theme]
    MaterialTheme(
        colorScheme = appColorScheme,
        typography = appTypography,
        shapes = appShapes,
        motionScheme = appMotionScheme,
        content = { /*content here*/ }
    )
    // [END android_wear_material_theme]
}