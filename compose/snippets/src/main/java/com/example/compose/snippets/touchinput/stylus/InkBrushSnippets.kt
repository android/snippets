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

@file:Suppress("unused")

package com.example.compose.snippets.touchinput.stylus

import androidx.compose.ui.graphics.Color
import androidx.ink.brush.Brush
import androidx.ink.brush.StockBrushes
import androidx.ink.brush.compose.copyWithComposeColor
import androidx.ink.brush.compose.createWithComposeColor

private fun createBrushExample() {
    // [START android_compose_touchinput_stylus_ink_brush_create]
    val brush = Brush.createWithComposeColor(
        family = StockBrushes.pressurePen(),
        color = Color.Black,
        size = 5F,
        epsilon = 0.1F
    )
    // [END android_compose_touchinput_stylus_ink_brush_create]
}

private fun modifyBrushExample() {
    // [START android_compose_touchinput_stylus_ink_brush_modify]
    val redBrush = Brush.createWithComposeColor(
        family = StockBrushes.pressurePen(),
        color = Color.Red,
        size = 5F,
        epsilon = 0.1F
    )

    val blueBrush = redBrush.copyWithComposeColor(color = Color.Blue)
    // [END android_compose_touchinput_stylus_ink_brush_modify]
}
