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

package com.example.compose.snippets.stylus

import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.ink.brush.Brush
import androidx.ink.brush.BrushFamily
import androidx.ink.brush.ExperimentalInkCustomBrushApi
import androidx.ink.brush.StockBrushes
import androidx.ink.brush.compose.copyWithComposeColor
import androidx.ink.brush.compose.createWithComposeColor
import androidx.ink.storage.decode
import com.example.compose.snippets.R

private fun createBrush() {
    // [START android_compose_stylus_ink_brush_create]
    val brush = Brush.createWithComposeColor(
        family = StockBrushes.pressurePen(),
        color = Color.Black,
        size = 5F,
        epsilon = 0.1F
    )
    // [END android_compose_stylus_ink_brush_create]
}

private fun modifyBrushProperties() {
    // [START android_compose_stylus_ink_brush_modify]
    val redBrush = Brush.createWithComposeColor(
        family = StockBrushes.pressurePen(),
        color = Color.Red,
        size = 5F,
        epsilon = 0.1F
    )

    val blueBrush = redBrush.copyWithComposeColor(color = Color.Blue)
    // [END android_compose_stylus_ink_brush_modify]
}

// [START android_compose_stylus_ink_brush_custom]
class CustomBrushes(val context: Context) {

    companion object {
        private const val TAG = "CustomBrushes"
    }

    val brushes by lazy { loadCustomBrushes() }

    @OptIn(ExperimentalInkCustomBrushApi::class)
    private fun loadCustomBrushes(): List<CustomBrush> {
        val brushFiles = mapOf(
            "Calligraphy" to (R.raw.calligraphy to R.drawable.draw_24px),
            "Flag Banner" to (R.raw.flag_banner to R.drawable.flag_24px),
            "Graffiti" to (R.raw.graffiti to R.drawable.format_paint_24px),
            // ...
        )

        val loadedBrushes = brushFiles.mapNotNull { (name, pair) ->
            val (resourceId, icon) = pair
            val brushFamily = context.resources.openRawResource(resourceId).use { inputStream ->
                BrushFamily.decode(inputStream)
            }
            CustomBrush(name, icon, brushFamily.copy(clientBrushFamilyId = name))
        }
        return loadedBrushes
    }
}

data class CustomBrush(
    val name: String,
    val icon: Int,
    val brushFamily: BrushFamily
)
// [END android_compose_stylus_ink_brush_custom]
