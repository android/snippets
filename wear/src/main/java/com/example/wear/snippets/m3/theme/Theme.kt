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
// [START android_wear_material_theme]
import androidx.wear.compose.material3.MaterialTheme
// [START_EXCLUDE]
import androidx.wear.compose.material3.MotionScheme
import androidx.wear.compose.material3.Shapes
import androidx.wear.compose.material3.Typography

@Composable
fun materialTheme() {
    // [END_EXCLUDE]
    MaterialTheme(
        colorScheme = ColorScheme(),
        typography = Typography(),
        shapes = Shapes(),
        motionScheme = MotionScheme.standard(),
        content = { /*content here*/ }
    )
    // [END android_wear_material_theme]
}
