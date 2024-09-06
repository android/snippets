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

package com.example.compose.snippets.graphics

import android.graphics.drawable.ShapeDrawable
import android.graphics.drawable.shapes.OvalShape
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PointMode
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.inset
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.compose.snippets.R

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
@Preview
@Composable
fun BasicCanvasUsage() {
    // [START android_compose_graphics_canvas_basic]
    Spacer(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                // this = DrawScope
            }
    )
    // [END android_compose_graphics_canvas_basic]
}

@Preview
@Composable
fun CanvasCircleExample() {
    // [START android_compose_graphics_canvas_circle]
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasQuadrantSize = size / 2F
        drawRect(
            color = Color.Magenta,
            size = canvasQuadrantSize
        )
    }
    // [END android_compose_graphics_canvas_circle]
}

@Preview
@Composable
fun CanvasDrawDiagonalLineExample() {
    // [START android_compose_graphics_canvas_diagonal_line]
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasWidth = size.width
        val canvasHeight = size.height
        drawLine(
            start = Offset(x = canvasWidth, y = 0f),
            end = Offset(x = 0f, y = canvasHeight),
            color = Color.Blue
        )
    }
    // [END android_compose_graphics_canvas_diagonal_line]
}

@Preview
@Composable
fun CanvasTransformationScale() {
    // [START android_compose_graphics_canvas_scale]
    Canvas(modifier = Modifier.fillMaxSize()) {
        scale(scaleX = 10f, scaleY = 15f) {
            drawCircle(Color.Blue, radius = 20.dp.toPx())
        }
    }
    // [END android_compose_graphics_canvas_scale]
}

@Preview
@Composable
fun CanvasTransformationTranslate() {
    // [START android_compose_graphics_canvas_translate]
    Canvas(modifier = Modifier.fillMaxSize()) {
        translate(left = 100f, top = -300f) {
            drawCircle(Color.Blue, radius = 200.dp.toPx())
        }
    }
    // [END android_compose_graphics_canvas_translate]
}

@Preview
@Composable
fun CanvasTransformationRotate() {
    // [START android_compose_graphics_canvas_rotate]
    Canvas(modifier = Modifier.fillMaxSize()) {
        rotate(degrees = 45F) {
            drawRect(
                color = Color.Gray,
                topLeft = Offset(x = size.width / 3F, y = size.height / 3F),
                size = size / 3F
            )
        }
    }
    // [END android_compose_graphics_canvas_rotate]
}

@Preview
@Composable
fun CanvasTransformationInset() {
    // [START android_compose_graphics_canvas_inset]
    Canvas(modifier = Modifier.fillMaxSize()) {
        val canvasQuadrantSize = size / 2F
        inset(horizontal = 50f, vertical = 30f) {
            drawRect(color = Color.Green, size = canvasQuadrantSize)
        }
    }
    // [END android_compose_graphics_canvas_inset]
}

@Preview
@Composable
fun CanvasMultipleTransformations() {
    // [START android_compose_graphics_canvas_multiple_transforms]
    Canvas(modifier = Modifier.fillMaxSize()) {
        withTransform({
            translate(left = size.width / 5F)
            rotate(degrees = 45F)
        }) {
            drawRect(
                color = Color.Gray,
                topLeft = Offset(x = size.width / 3F, y = size.height / 3F),
                size = size / 3F
            )
        }
    }
    // [END android_compose_graphics_canvas_multiple_transforms]
}

@Preview
@Composable
fun CanvasDrawText() {
    // [START android_compose_graphics_canvas_draw_text]
    val textMeasurer = rememberTextMeasurer()

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawText(textMeasurer, "Hello")
    }
    // [END android_compose_graphics_canvas_draw_text]
}

@Preview
@Composable
fun CanvasDrawImage() {
    // [START android_compose_graphics_canvas_draw_image]
    val dogImage = ImageBitmap.imageResource(id = R.drawable.dog)

    Canvas(modifier = Modifier.fillMaxSize(), onDraw = {
        drawImage(dogImage)
    })
    // [END android_compose_graphics_canvas_draw_image]
}

@Preview
@Composable
fun CanvasDrawPath() {
    // [START android_compose_graphics_canvas_draw_path]
    Spacer(
        modifier = Modifier
            .drawWithCache {
                val path = Path()
                path.moveTo(0f, 0f)
                path.lineTo(size.width / 2f, size.height / 2f)
                path.lineTo(size.width, 0f)
                path.close()
                onDrawBehind {
                    drawPath(path, Color.Magenta, style = Stroke(width = 10f))
                }
            }
            .fillMaxSize()
    )
    // [END android_compose_graphics_canvas_draw_path]
}

@Preview
@Composable
fun CanvasMeasureText() {
    val pinkColor = Color(0xFFF48FB1)
    val longTextSample =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
    // [START android_compose_graphics_canvas_draw_text_measure]
    val textMeasurer = rememberTextMeasurer()

    Spacer(
        modifier = Modifier
            .drawWithCache {
                val measuredText =
                    textMeasurer.measure(
                        AnnotatedString(longTextSample),
                        constraints = Constraints.fixedWidth((size.width * 2f / 3f).toInt()),
                        style = TextStyle(fontSize = 18.sp)
                    )

                onDrawBehind {
                    drawRect(pinkColor, size = measuredText.size.toSize())
                    drawText(measuredText)
                }
            }
            .fillMaxSize()
    )
    // [END android_compose_graphics_canvas_draw_text_measure]
}

@Preview
@Composable
fun CanvasMeasureTextOverflow() {
    val pinkColor = Color(0xFFF48FB1)
    val longTextSample =
        "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum."
    // [START android_compose_graphics_canvas_draw_text_measure_ellipsis]
    val textMeasurer = rememberTextMeasurer()

    Spacer(
        modifier = Modifier
            .drawWithCache {
                val measuredText =
                    textMeasurer.measure(
                        AnnotatedString(longTextSample),
                        constraints = Constraints.fixed(
                            width = (size.width / 3f).toInt(),
                            height = (size.height / 3f).toInt()
                        ),
                        overflow = TextOverflow.Ellipsis,
                        style = TextStyle(fontSize = 18.sp)
                    )

                onDrawBehind {
                    drawRect(pinkColor, size = measuredText.size.toSize())
                    drawText(measuredText)
                }
            }
            .fillMaxSize()
    )
    // [END android_compose_graphics_canvas_draw_text_measure_ellipsis]
}

@Preview
@Composable
fun CanvasDrawIntoCanvas() {
    // [START android_compose_graphics_canvas_draw_into_canvas]
    val drawable = ShapeDrawable(OvalShape())
    Spacer(
        modifier = Modifier
            .drawWithContent {
                drawIntoCanvas { canvas ->
                    drawable.setBounds(0, 0, size.width.toInt(), size.height.toInt())
                    drawable.draw(canvas.nativeCanvas)
                }
            }
            .fillMaxSize()
    )
    // [END android_compose_graphics_canvas_draw_into_canvas]
}

@Preview
@Composable
fun CanvasDrawShape() {
    // [START android_compose_graphics_draw_shape]
    val purpleColor = Color(0xFFBA68C8)
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        onDraw = {
            drawCircle(purpleColor)
        }
    )
    // [END android_compose_graphics_draw_shape]
}

@Preview
@Composable
fun CanvasDrawOtherShapes() {
    val purpleColor = Color(0xFFBA68C8)
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        onDraw = {
            drawPoints(
                listOf(
                    Offset(0f, 0f),
                    Offset(size.width / 3f, size.height / 2f),
                    Offset(size.width / 2f, size.height / 5f),
                    Offset(size.width, size.height)
                ),
                color = purpleColor,
                pointMode = PointMode.Points, strokeWidth = 10.dp.toPx()
            )
        }
    )
}

@Preview
@Composable
fun CanvasTransformationScaleAnim() {
    val animatable = remember {
        Animatable(1f)
    }
    LaunchedEffect(Unit) {
        animatable.animateTo(10f, animationSpec = tween(3000, 3000, easing = LinearEasing))
    }
    Canvas(modifier = Modifier.fillMaxSize()) {
        scale(scaleX = animatable.value, scaleY = animatable.value * 1.5f) {
            drawCircle(Color.Blue, radius = 20.dp.toPx())
        }
    }
}
