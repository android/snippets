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

package com.example.compose.snippets.images

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
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
fun ImageExamplesScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        ContentScaleExample()
        ClipImageExample()
        ClipRoundedCorner()
        CustomClippingShape()
        ImageWithBorder()
        ImageRainbowBorder()
        ImageAspectRatio()
        ImageColorFilter()
        ImageBlendMode()
        ImageColorMatrix()
        ImageAdjustBrightnessContrast()
        ImageInvertColors()
        ImageBlur()
        ImageBlurEdgeTreatment()
    }
}

@Preview
@Composable
fun ContentScaleExample() {
    // [START android_compose_content_scale]
    val imageModifier = Modifier
        .size(150.dp)
        .border(BorderStroke(1.dp, Color.Black))
        .background(Color.Yellow)
    Image(
        painter = painterResource(id = R.drawable.dog),
        contentDescription = stringResource(id = R.string.dog_content_description),
        contentScale = ContentScale.Fit,
        modifier = imageModifier
    )
    // [END android_compose_content_scale]
}

@Preview
@Composable
fun ClipImageExample() {
    // [START android_compose_clip_image]
    Image(
        painter = painterResource(id = R.drawable.dog),
        contentDescription = stringResource(id = R.string.dog_content_description),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(200.dp)
            .clip(CircleShape)
    )
    // [END android_compose_clip_image]
}

@Preview
@Composable
fun ClipRoundedCorner() {
    // [START android_compose_clip_image_rounded_corner]
    Image(
        painter = painterResource(id = R.drawable.dog),
        contentDescription = stringResource(id = R.string.dog_content_description),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(200.dp)
            .clip(RoundedCornerShape(16.dp))
    )
    // [END android_compose_clip_image_rounded_corner]
}

@Preview
@Composable
fun CustomClippingShape() {
    // [START android_compose_custom_clipping_shape]
    class SquashedOval : Shape {
        override fun createOutline(
            size: Size,
            layoutDirection: LayoutDirection,
            density: Density
        ): Outline {
            val path = Path().apply {
                // We create an Oval that starts at ¼ of the width, and ends at ¾ of the width of the container.
                addOval(
                    Rect(
                        left = size.width / 4f,
                        top = 0f,
                        right = size.width * 3 / 4f,
                        bottom = size.height
                    )
                )
            }
            return Outline.Generic(path = path)
        }
    }

    Image(
        painter = painterResource(id = R.drawable.dog),
        contentDescription = stringResource(id = R.string.dog_content_description),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(200.dp)
            .clip(SquashedOval())
    )
    // [END android_compose_custom_clipping_shape]
}

@Preview
@Composable
fun ImageWithBorder() {
    // [START android_compose_image_border]
    val borderWidth = 4.dp
    Image(
        painter = painterResource(id = R.drawable.dog),
        contentDescription = stringResource(id = R.string.dog_content_description),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(150.dp)
            .border(
                BorderStroke(borderWidth, Color.Yellow),
                CircleShape
            )
            .padding(borderWidth)
            .clip(CircleShape)
    )
    // [END android_compose_image_border]
}

@Preview
@Composable
fun ImageRainbowBorder() {
    // [START android_compose_image_rainbow_border]
    val rainbowColorsBrush = remember {
        Brush.sweepGradient(
            listOf(
                Color(0xFF9575CD),
                Color(0xFFBA68C8),
                Color(0xFFE57373),
                Color(0xFFFFB74D),
                Color(0xFFFFF176),
                Color(0xFFAED581),
                Color(0xFF4DD0E1),
                Color(0xFF9575CD)
            )
        )
    }
    val borderWidth = 4.dp
    Image(
        painter = painterResource(id = R.drawable.dog),
        contentDescription = stringResource(id = R.string.dog_content_description),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(150.dp)
            .border(
                BorderStroke(borderWidth, rainbowColorsBrush),
                CircleShape
            )
            .padding(borderWidth)
            .clip(CircleShape)
    )
    // [END android_compose_image_rainbow_border]
}

@Composable
@Preview
fun ImageAspectRatio() {
    // [START android_compose_image_aspect_ratio]
    Image(
        painter = painterResource(id = R.drawable.dog),
        contentDescription = stringResource(id = R.string.dog_content_description),
        modifier = Modifier.aspectRatio(16f / 9f)
    )
    // [END android_compose_image_aspect_ratio]
}

@Composable
@Preview
fun ImageColorFilter() {
    // [START android_compose_image_color_filter]
    Image(
        painter = painterResource(id = R.drawable.baseline_directions_bus_24),
        contentDescription = stringResource(id = R.string.bus_content_description),
        colorFilter = ColorFilter.tint(Color.Yellow)
    )
    // [END android_compose_image_color_filter]
}

@Preview
@Composable
fun ImageBlendMode() {
    // [START android_compose_image_blend_mode]
    Image(
        painter = painterResource(id = R.drawable.dog),
        contentDescription = stringResource(id = R.string.dog_content_description),
        colorFilter = ColorFilter.tint(Color.Green, blendMode = BlendMode.Darken)
    )
    // [END android_compose_image_blend_mode]
}

@Preview
@Composable
fun ImageColorMatrix() {
    // [START android_compose_image_colormatrix]
    Image(
        painter = painterResource(id = R.drawable.dog),
        contentDescription = stringResource(id = R.string.dog_content_description),
        colorFilter = ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0f) })
    )
    // [END android_compose_image_colormatrix]
}

@Preview
@Composable
fun ImageAdjustBrightnessContrast() {
    // [START android_compose_image_brightness]
    val contrast = 2f // 0f..10f (1 should be default)
    val brightness = -180f // -255f..255f (0 should be default)
    val colorMatrix = floatArrayOf(
        contrast, 0f, 0f, 0f, brightness,
        0f, contrast, 0f, 0f, brightness,
        0f, 0f, contrast, 0f, brightness,
        0f, 0f, 0f, 1f, 0f
    )
    Image(
        painter = painterResource(id = R.drawable.dog),
        contentDescription = stringResource(id = R.string.dog_content_description),
        colorFilter = ColorFilter.colorMatrix(ColorMatrix(colorMatrix))
    )
    // [END android_compose_image_brightness]
}

@Preview
@Composable
fun ImageInvertColors() {
    // [START android_compose_image_invert_colors]
    val colorMatrix = floatArrayOf(
        -1f, 0f, 0f, 0f, 255f,
        0f, -1f, 0f, 0f, 255f,
        0f, 0f, -1f, 0f, 255f,
        0f, 0f, 0f, 1f, 0f
    )
    Image(
        painter = painterResource(id = R.drawable.dog),
        contentDescription = stringResource(id = R.string.dog_content_description),
        colorFilter = ColorFilter.colorMatrix(ColorMatrix(colorMatrix))
    )
    // [END android_compose_image_invert_colors]
}

@Preview
@Composable
fun ImageBlur() {
    // [START android_compose_image_blur]
    Image(
        painter = painterResource(id = R.drawable.dog),
        contentDescription = stringResource(id = R.string.dog_content_description),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(150.dp)
            .blur(
                radiusX = 10.dp,
                radiusY = 10.dp,
                edgeTreatment = BlurredEdgeTreatment(RoundedCornerShape(8.dp))
            )
    )
    // [END android_compose_image_blur]
}

@Preview
@Composable
fun ImageBlurEdgeTreatment() {
    // [START android_compose_image_blur_edge_treatment]
    Image(
        painter = painterResource(id = R.drawable.dog),
        contentDescription = stringResource(id = R.string.dog_content_description),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(150.dp)
            .blur(
                radiusX = 10.dp,
                radiusY = 10.dp,
                edgeTreatment = BlurredEdgeTreatment.Unbounded
            )
            .clip(RoundedCornerShape(8.dp))
    )
    // / [END android_compose_image_blur_edge_treatment]
}
