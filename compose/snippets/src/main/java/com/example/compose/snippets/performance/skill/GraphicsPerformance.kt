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

package com.example.compose.snippets.performance.skill

import android.graphics.RuntimeShader
import androidx.collection.MutableLongSet
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.packFloats
import androidx.compose.ui.util.packInts
import androidx.compose.ui.util.unpackFloat1
import androidx.compose.ui.util.unpackFloat2
import coil.compose.AsyncImage
import com.example.compose.snippets.R
import com.example.compose.snippets.performance.skill.PointAllocationGood.Holder.Point

@Composable
private fun DrawPathBad() {
    // [START android_compose_performance_graphics_path_bad]
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, size.height)
                }
                drawPath(path, Color.Red)
            }
    )
    // [END android_compose_performance_graphics_path_bad]
}

@Composable
private fun DrawPathGood() {
    // [START android_compose_performance_graphics_path_good]
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawWithCache {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, size.height)
                }
                onDrawBehind {
                    drawPath(path, Color.Red)
                }
            }
    )
    // [END android_compose_performance_graphics_path_good]
}

@Composable
private fun DrawShaderUniforms(timeState: State<Float>, modifier: Modifier = Modifier) {
    // [START android_compose_performance_graphics_shader_uniforms]
    // RuntimeShader parsed once and cached in Composable scope
    val shader = remember { RuntimeShader(SHADER_SRC) }
    val brush = remember(shader) { ShaderBrush(shader) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .drawWithCache {
                // Layout/size-dependent uniforms updated in cache block (on resize)
                shader.setFloatUniform("u_resolution", size.width, size.height)

                onDrawBehind {
                    // Per-frame uniforms updated in draw block without reallocating
                    shader.setFloatUniform("u_time", timeState.value)
                    drawRect(brush)
                }
            }
    )
    // [END android_compose_performance_graphics_shader_uniforms]
}

private fun GridPackingBad(items: List<GridCell>) {
    // [START android_compose_performance_graphics_packing_bad]
    val occupied = HashSet<Pair<Int, Int>>()
    for (item in items) {
        occupied.add(Pair(item.row, item.col))
    }
    // [END android_compose_performance_graphics_packing_bad]
}

private fun GridPackingGood(items: List<GridCell>) {
    // [START android_compose_performance_graphics_packing_good]
    // import androidx.collection.MutableLongSet
    // import androidx.compose.ui.util.fastForEach
    // import androidx.compose.ui.util.packInts

    val occupied = MutableLongSet()
    items.fastForEach { item ->
        occupied.add(packInts(item.row, item.col))
    }
    // [END android_compose_performance_graphics_packing_good]
}

private object PointAllocationBad {
    fun accumulatePoints() {
        // [START android_compose_performance_graphics_point_bad]
        class Point(val x: Float, val y: Float)
        var acc = Point(0f, 0f)
        for (i in 0 until 1000) {
            acc = Point(acc.x + i, acc.y - i)
        }
        // [END android_compose_performance_graphics_point_bad]
    }
}

private object PointAllocationGood {
    object Holder {
        // [START android_compose_performance_graphics_point_value_class]
        // import androidx.compose.ui.util.packFloats
        // import androidx.compose.ui.util.unpackFloat1
        // import androidx.compose.ui.util.unpackFloat2

        @JvmInline
        value class Point private constructor(val packedValue: Long) {
            constructor(x: Float, y: Float) : this(packFloats(x, y))

            val x: Float get() = unpackFloat1(packedValue)
            val y: Float get() = unpackFloat2(packedValue)
        }
        // [START_EXCLUDE silent]
    }

    fun accumulatePackedPoints() {
        // [END_EXCLUDE]

        var acc = Point(0f, 0f)
        for (i in 0 until 1000) {
            acc = Point(acc.x + i, acc.y - i)
        }
        // Alternatively, use Compose's built-in Offset class, which implements
        // this exact @JvmInline Long packing pattern under the hood.
        // [END android_compose_performance_graphics_point_value_class]
    }
}

@Composable
private fun BitmapImageBad() {
    // [START android_compose_performance_graphics_image_painter_bad]
    Image(
        painter = painterResource(id = R.drawable.donut_photo),
        contentScale = ContentScale.Fit,
        modifier = Modifier.size(160.dp),
        contentDescription = stringResource(id = R.string.attached_image),
    )
    // [END android_compose_performance_graphics_image_painter_bad]
}

@Composable
private fun BitmapImageGood() {
    // [START android_compose_performance_graphics_image_async_good]
    // import coil3.compose.AsyncImage

    AsyncImage(
        model = R.drawable.donut_photo,
        contentScale = ContentScale.Fit,
        modifier = Modifier.size(160.dp),
        contentDescription = stringResource(id = R.string.attached_image),
    )
    // [END android_compose_performance_graphics_image_async_good]
}

private const val SHADER_SRC = """
    uniform float2 u_resolution;
    uniform float u_time;
    half4 main(float2 fragCoord) {
        return half4(1.0, 0.0, 0.0, 1.0);
    }
"""

private data class GridCell(val row: Int, val col: Int)
