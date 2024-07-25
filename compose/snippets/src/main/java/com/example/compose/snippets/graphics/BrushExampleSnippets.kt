/*
 * Copyright 2022 The Android Open Source Project
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

package com.example.compose.snippets.graphics

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.geometry.center
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ImageShader
import androidx.compose.ui.graphics.LinearGradientShader
import androidx.compose.ui.graphics.RadialGradientShader
import androidx.compose.ui.graphics.Shader
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.snippets.R
import org.intellij.lang.annotations.Language

/*
* Copyright 2022 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
/**
 * The snippets in this file relate to the documentation at
 * https://developr.android.com/jetpack/compose/graphics/draw/brush
 */
@Composable
fun BrushExamplesScreen() {
    Column(Modifier.verticalScroll(rememberScrollState())) {
        GraphicsBrushCanvasUsage()
        GraphicsBrushColorStopUsage()
        GraphicsBrushTileMode()
        GraphicsBrushSize()
        GraphicsBrushSizeRecreationExample()
        GraphicsImageBrush()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ShaderBrushExample()
        }
    }
}

@Preview
@Composable
fun GraphicsBrushCanvasUsage() {
    // [START android_compose_brush_basic_canvas]
    val brush = Brush.horizontalGradient(listOf(Color.Red, Color.Blue))
    Canvas(
        modifier = Modifier.size(200.dp),
        onDraw = {
            drawCircle(brush)
        }
    )
    // [END android_compose_brush_basic_canvas]
}

@Preview
@Composable
fun GraphicsBrushColorStopUsage() {
    // [START android_compose_brush_color_stop]
    val colorStops = arrayOf(
        0.0f to Color.Yellow,
        0.2f to Color.Red,
        1f to Color.Blue
    )
    Box(
        modifier = Modifier
            .requiredSize(200.dp)
            .background(Brush.horizontalGradient(colorStops = colorStops))
    )
    // [END android_compose_brush_color_stop]
}

@Preview
@Composable
fun GraphicsBrushTileMode() {
    // [START android_compose_brush_tile_mode]
    val listColors = listOf(Color.Yellow, Color.Red, Color.Blue)
    val tileSize = with(LocalDensity.current) {
        50.dp.toPx()
    }
    Box(
        modifier = Modifier
            .requiredSize(200.dp)
            .background(
                Brush.horizontalGradient(
                    listColors,
                    endX = tileSize,
                    tileMode = TileMode.Repeated
                )
            )
    )
    // [END android_compose_brush_tile_mode]
}

@Composable
@Preview
fun GraphicsBrushSize() {
    // [START android_compose_graphics_brush_size]
    val listColors = listOf(Color.Yellow, Color.Red, Color.Blue)
    val customBrush = remember {
        object : ShaderBrush() {
            override fun createShader(size: Size): Shader {
                return LinearGradientShader(
                    colors = listColors,
                    from = Offset.Zero,
                    to = Offset(size.width / 4f, 0f),
                    tileMode = TileMode.Mirror
                )
            }
        }
    }
    Box(
        modifier = Modifier
            .requiredSize(200.dp)
            .background(customBrush)
    )
    // [END android_compose_graphics_brush_size]
}

@Composable
@Preview
fun GraphicsBrushSizeRadialGradientBefore() {
    // [START android_compose_graphics_brush_size_radial_before]
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    listOf(Color(0xFF2be4dc), Color(0xFF243484))
                )
            )
    )
    // [END android_compose_graphics_brush_size_radial_before]
}

@Preview
@Composable
fun GraphicsBrushSizeRadialGradientAfter() {
    // [START android_compose_graphics_brush_size_radial_after]
    val largeRadialGradient = object : ShaderBrush() {
        override fun createShader(size: Size): Shader {
            val biggerDimension = maxOf(size.height, size.width)
            return RadialGradientShader(
                colors = listOf(Color(0xFF2be4dc), Color(0xFF243484)),
                center = size.center,
                radius = biggerDimension / 2f,
                colorStops = listOf(0f, 0.95f)
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(largeRadialGradient)
    )
    // [END android_compose_graphics_brush_size_radial_after]
}

@Preview
@Composable
fun GraphicsBrushSizeRecreationExample() {
    // [START android_compose_graphics_brush_recreation]
    val colorStops = arrayOf(
        0.0f to Color.Yellow,
        0.2f to Color.Red,
        1f to Color.Blue
    )
    val brush = Brush.horizontalGradient(colorStops = colorStops)
    Box(
        modifier = Modifier
            .requiredSize(200.dp)
            .drawBehind {
                drawRect(brush = brush) // will allocate a shader to occupy the 200 x 200 dp drawing area
                inset(10f) {
          /* Will allocate a shader to occupy the 180 x 180 dp drawing area as the
           inset scope reduces the drawing  area by 10 pixels on the left, top, right,
          bottom sides */
                    drawRect(brush = brush)
                    inset(5f) {
            /* will allocate a shader to occupy the 170 x 170 dp drawing area as the
             inset scope reduces the  drawing area by 5 pixels on the left, top,
             right, bottom sides */
                        drawRect(brush = brush)
                    }
                }
            }
    )
    // [END android_compose_graphics_brush_recreation]
}

@Preview
@Composable
fun GraphicsImageBrush() {
    // [START android_compose_graphics_brush_image]
    val imageBrush =
        ShaderBrush(ImageShader(ImageBitmap.imageResource(id = R.drawable.dog)))

    // Use ImageShader Brush with background
    Box(
        modifier = Modifier
            .requiredSize(200.dp)
            .background(imageBrush)
    )

    // Use ImageShader Brush with TextStyle
    Text(
        text = "Hello Android!",
        style = TextStyle(
            brush = imageBrush,
            fontWeight = FontWeight.ExtraBold,
            fontSize = 36.sp
        )
    )

    // Use ImageShader Brush with DrawScope#drawCircle()
    Canvas(onDraw = {
        drawCircle(imageBrush)
    }, modifier = Modifier.size(200.dp))
    // [END android_compose_graphics_brush_image]
}

// [START android_compose_brush_custom_shader]
@Language("AGSL")
val CUSTOM_SHADER = """
    uniform float2 resolution;
    layout(color) uniform half4 color;
    layout(color) uniform half4 color2;

    half4 main(in float2 fragCoord) {
        float2 uv = fragCoord/resolution.xy;

        float mixValue = distance(uv, vec2(0, 1));
        return mix(color, color2, mixValue);
    }
""".trimIndent()
// [END android_compose_brush_custom_shader]

// [START android_compose_brush_custom_shader_usage]
val Coral = Color(0xFFF3A397)
val LightYellow = Color(0xFFF8EE94)

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
@Preview
fun ShaderBrushExample() {
    Box(
        modifier = Modifier
            .drawWithCache {
                val shader = RuntimeShader(CUSTOM_SHADER)
                val shaderBrush = ShaderBrush(shader)
                shader.setFloatUniform("resolution", size.width, size.height)
                onDrawBehind {
                    shader.setColorUniform(
                        "color",
                        android.graphics.Color.valueOf(
                            LightYellow.red, LightYellow.green,
                            LightYellow
                                .blue,
                            LightYellow.alpha
                        )
                    )
                    shader.setColorUniform(
                        "color2",
                        android.graphics.Color.valueOf(
                            Coral.red,
                            Coral.green,
                            Coral.blue,
                            Coral.alpha
                        )
                    )
                    drawRect(shaderBrush)
                }
            }
            .fillMaxWidth()
            .height(200.dp)
    )
}
// [END android_compose_brush_custom_shader_usage]
