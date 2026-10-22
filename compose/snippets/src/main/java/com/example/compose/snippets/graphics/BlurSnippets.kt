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

package com.example.compose.snippets.graphics

import android.graphics.RuntimeShader
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.isSpecified
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.blur.BlurRadiusSpec
import androidx.compose.ui.graphics.blur.BlurStop
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.snippets.R
import kotlin.math.roundToInt
import kotlinx.coroutines.delay

/*
 * Snippets for the "Blurring in Compose" guide:
 * https://developer.android.com/develop/ui/compose/graphics/draw/blur
 *
 * Requires androidx.compose.ui:ui 1.13.0-beta01 or newer for BlurRadiusSpec, BlurStop,
 * and the Modifier.blur { } configuration block.
 */

// ---------------------------------------------------------------------------------------------
// Apply a uniform blur
// ---------------------------------------------------------------------------------------------

@Preview
// [START android_compose_graphics_blur_uniform]
@Composable
fun UniformBlurExample() {
    Image(
        painter = painterResource(id = R.drawable.dog),
        contentDescription = stringResource(id = R.string.dog_content_description),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(150.dp)
            .blur(
                radius = 8.dp,
                edgeTreatment = BlurredEdgeTreatment(RoundedCornerShape(8.dp)),
            ),
    )
}
// [END android_compose_graphics_blur_uniform]

@Preview
// [START android_compose_graphics_blur_directional]
@Composable
fun DirectionalBlurExample() {
    Image(
        painter = painterResource(id = R.drawable.dog),
        contentDescription = stringResource(id = R.string.dog_content_description),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(150.dp)
            .blur(
                radiusX = 10.dp,
                radiusY = 2.dp,
                edgeTreatment = BlurredEdgeTreatment(RoundedCornerShape(8.dp)),
            ),
    )
}
// [END android_compose_graphics_blur_directional]

// ---------------------------------------------------------------------------------------------
// Apply a progressive blur
// ---------------------------------------------------------------------------------------------

@Preview
// [START android_compose_graphics_blur_progressive]
@Composable
fun ProgressiveBlurExample() {
    Image(
        painter = painterResource(id = R.drawable.dog),
        contentDescription = stringResource(id = R.string.dog_content_description),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(150.dp)
            .blur(
                // Sharp at the top (0.dp), progressively blurred toward the bottom (6.dp)
                blurRadiusSpec = BlurRadiusSpec.verticalGradient(
                    startRadius = 0.dp,
                    endRadius = 6.dp,
                ),
                edgeTreatment = BlurredEdgeTreatment(RoundedCornerShape(8.dp)),
            ),
    )
}
// [END android_compose_graphics_blur_progressive]

@Preview
// [START android_compose_graphics_blur_multi_stop]
@Composable
fun MultiStopBlurExample() {
    Image(
        painter = painterResource(id = R.drawable.dog),
        contentDescription = stringResource(id = R.string.dog_content_description),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(150.dp)
            .blur(
                // Blurred at the top and bottom edges, sharp in the middle (0.3f..0.7f)
                blurRadiusSpec = BlurRadiusSpec.verticalGradient(
                    stops = listOf(
                        BlurStop(fraction = 0.0f, radius = 20.dp),
                        BlurStop(fraction = 0.3f, radius = 0.dp),
                        BlurStop(fraction = 0.7f, radius = 0.dp),
                        BlurStop(fraction = 1.0f, radius = 20.dp),
                    ),
                ),
                edgeTreatment = BlurredEdgeTreatment.Unbounded,
            ),
    )
}
// [END android_compose_graphics_blur_multi_stop]

// ---------------------------------------------------------------------------------------------
// Common use cases: blur the edges of scrolling or draggable content
// ---------------------------------------------------------------------------------------------

private val SampleLyrics = listOf(
    "",
    "",
    "Waiting in a car",
    "Waiting for a ride in the dark",
    "The night city grows",
    "Look at the horizon glow",
    "Waiting in a car",
    "Waiting for a ride in the dark",
    "Drinking in the lounge",
    "Following the neon signs",
    "Waiting for a word",
    "Looking at the milky skyline",
    "The city is my church",
    "It wraps me in the sparkling twilight",
    "",
    "",
)


// [START android_compose_graphics_blur_scrolling_list]
@Composable
fun SongListTopBottomBlurExample(lyrics: List<String>) {
    // [START_EXCLUDE]
    val listState = rememberLazyListState()
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val stepPx = with(LocalDensity.current) { (56.dp + 26.dp).toPx() }
    // Smoothly scroll lyrics one by one in 900ms; restart from the top if the lyrics change
    LaunchedEffect(stepPx, lyrics) {
        var currentLine = 0
        val maxScrollIndex = (lyrics.size - 5).coerceAtLeast(1)
        while (true) {
            delay(900L)
            if (currentLine < maxScrollIndex) {
                currentLine++
                listState.animateScrollBy(
                    value = stepPx,
                    animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
                )
            } else {
                currentLine = 0
                listState.scrollToItem(0)
            }
        }
    }
    // [END_EXCLUDE]
    LazyColumn(
        state = listState,
        flingBehavior = flingBehavior,
        modifier = Modifier
            .width(330.dp)
            .height(480.dp)
            .padding(horizontal = 24.dp)
            .blur(
                blurRadiusSpec = BlurRadiusSpec.verticalGradient(
                    stops = listOf(
                        BlurStop(fraction = 0.00f, radius = 18.dp), // Heavily blurred top edge
                        BlurStop(fraction = 0.24f, radius = 8.dp), // Soft upper transition
                        BlurStop(fraction = 0.42f, radius = 0.dp), // Sharp center focus starts
                        BlurStop(fraction = 0.58f, radius = 0.dp), // Sharp center focus ends
                        BlurStop(fraction = 0.76f, radius = 8.dp), // Soft lower transition
                        BlurStop(fraction = 1.00f, radius = 18.dp), // Heavily blurred bottom edge
                    ),
                ),
                edgeTreatment = BlurredEdgeTreatment.Unbounded,
            ),
        contentPadding = PaddingValues(vertical = 36.dp),
        verticalArrangement = Arrangement.spacedBy(26.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // [START_EXCLUDE]
        itemsIndexed(
            items = lyrics,
            key = { index, _ -> index },
            contentType = { _, _ -> "lyric" },
        ) { _, line ->
            Text(
                text = line,
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        // [END_EXCLUDE]
    }
}
// [END android_compose_graphics_blur_scrolling_list]

@Preview
// [START android_compose_graphics_blur_horizontal_control]
@Composable
fun HorizontalScaleBlurExample() {
    var selectedValue by remember { mutableFloatStateOf(65.0f) }
    val animatedValue by animateFloatAsState(
        targetValue = selectedValue,
        animationSpec = tween(durationMillis = 900, easing = FastOutSlowInEasing),
        label = "horizontalScaleSmoothScroll",
    )
    Column(
        modifier = Modifier
            .width(340.dp)
            .background(Color(0xFF0F172A), RoundedCornerShape(24.dp))
            .padding(vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "${(animatedValue * 10).roundToInt() / 10.0} kg",
            color = Color.White,
            fontSize = 44.sp,
            fontWeight = FontWeight.Black,
        )
        Spacer(Modifier.height(16.dp))
        Box(
            // [START_EXCLUDE]
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .pointerInput(Unit) {
                    detectHorizontalDragGestures { change, dragAmount ->
                        change.consume()
                        selectedValue = (selectedValue - dragAmount * 0.04f).coerceIn(30f, 140f)
                    }
                },
            contentAlignment = Alignment.Center,
            // [END_EXCLUDE]
        ) {
            // Ruler ticks blurred at the left and right edges, sharp at center
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(
                        blurRadiusSpec = BlurRadiusSpec.horizontalGradient(
                            stops = listOf(
                                BlurStop(fraction = 0.00f, radius = 14.dp),
                                BlurStop(fraction = 0.28f, radius = 0.dp),
                                BlurStop(fraction = 0.72f, radius = 0.dp),
                                BlurStop(fraction = 1.00f, radius = 14.dp),
                            ),
                        ),
                        edgeTreatment = BlurredEdgeTreatment.Unbounded,
                    ),
            ) {
                // Draw horizontal ruler ticks offset by `animatedValue`
                // [START_EXCLUDE]
                val centerX = size.width / 2f
                val pxPerTenthUnit = 14f // 10 tenths = 1.0 kg = 140px
                val currentTenths = (animatedValue * 10f).roundToInt()
                val fractionalOffsetPx = (animatedValue * 10f - currentTenths) * pxPerTenthUnit
                for (offsetTenths in -25..25) {
                    val tickTenths = currentTenths + offsetTenths
                    if (tickTenths < 300 || tickTenths > 1400) continue
                    val x = centerX + offsetTenths * pxPerTenthUnit - fractionalOffsetPx
                    val isMajorUnit = tickTenths % 10 == 0
                    val isHalfUnit = tickTenths % 5 == 0
                    val tickHeight = when {
                        isMajorUnit -> size.height * 0.48f
                        isHalfUnit -> size.height * 0.32f
                        else -> size.height * 0.18f
                    }
                    val tickColor = when {
                        isMajorUnit -> Color.White
                        isHalfUnit -> Color(0xFF94A3B8)
                        else -> Color(0xFF475569)
                    }
                    drawLine(
                        color = tickColor,
                        start = Offset(x, (size.height - tickHeight) / 2f),
                        end = Offset(x, (size.height + tickHeight) / 2f),
                        strokeWidth = if (isMajorUnit) 5f else 3f,
                        cap = StrokeCap.Round,
                    )
                }
                // [END_EXCLUDE]
            }
            // [START_EXCLUDE]
            // Sharp center indicator needle
            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(52.dp)
                    .background(Color(0xFF38BDF8), RoundedCornerShape(2.dp)),
            )
            // [END_EXCLUDE]
        }
    }
}
// [END android_compose_graphics_blur_horizontal_control]

// ---------------------------------------------------------------------------------------------
// Common use cases: linearGradient, radialGradient, shader
// ---------------------------------------------------------------------------------------------

@Preview
// [START android_compose_graphics_blur_diagonal_linear]
@Composable
fun DiagonalLinearBlurCardExample() {
    Box(
        modifier = Modifier
            .wrapContentSize()
            .background(Color(0xFFF8FAFC))
            .padding(10.dp),
    ) {
        Image(
            painter = painterResource(id = R.drawable.dog),
            contentDescription = stringResource(id = R.string.dog_content_description),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(width = 300.dp, height = 380.dp)
                .blur {
                    blurRadiusSpec = BlurRadiusSpec.linearGradient(
                        start = DpOffset(0.dp, 0.dp),
                        end = DpOffset(size.width, size.height),
                        stops = listOf(
                            BlurStop(fraction = 0.00f, radius = 0.dp),
                            BlurStop(fraction = 0.50f, radius = 0.dp),
                            BlurStop(fraction = 0.75f, radius = 14.dp),
                            BlurStop(fraction = 1.00f, radius = 28.dp),
                        ),
                    )
                    edgeTreatment = BlurredEdgeTreatment(RoundedCornerShape(24.dp))
                },
        )
        // [START_EXCLUDE]
        Column(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            horizontalAlignment = Alignment.End,
        ) {
            Text(
                text = "Golden Retriever",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = "Autumn portrait • 85mm f/1.4",
                color = Color.White.copy(alpha = 0.9f),
                fontSize = 14.sp,
            )
        }
        // [END_EXCLUDE]
    }
}
// [END android_compose_graphics_blur_diagonal_linear]

@Preview
// [START android_compose_graphics_blur_radial_spotlight]
@Composable
fun RadialSpotlightTextExample() {
    var touchOffset by remember { mutableStateOf(Offset.Unspecified) }
    Box(
        modifier = Modifier
            .size(width = 330.dp, height = 340.dp)
            .background(Color(0xFF0F172A), RoundedCornerShape(20.dp))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset -> touchOffset = offset },
                    onDrag = { change, _ -> touchOffset = change.position },
                )
            }
            .blur {
                val spotlightCenter = if (touchOffset.isSpecified) {
                    DpOffset(touchOffset.x.toDp(), touchOffset.y.toDp())
                } else {
                    DpOffset(x = size.width / 2, y = size.height / 2)
                }
                blurRadiusSpec = BlurRadiusSpec.radialGradient(
                    startRadius = 0.dp,
                    endRadius = 10.dp,
                    center = spotlightCenter,
                    fallOffRadius = 215.dp,
                )
                edgeTreatment = BlurredEdgeTreatment(RoundedCornerShape(20.dp))
            }
            .padding(22.dp),
    ) {
        // [START_EXCLUDE]
        Text(
            text = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod " +
                "tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, " +
                "quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo " +
                "consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse " +
                "cillum dolore eu fugiat nulla pariatur.\n\n" +
                "Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia " +
                "deserunt mollit anim id est laborum. Sed ut perspiciatis unde omnis iste " +
                "natus error sit voluptatem accusantium doloremque laudantium, totam rem " +
                "aperiam, eaque ipsa quae ab illo inventore veritatis et quasi architecto.",
            color = Color.White,
            fontSize = 15.sp,
            lineHeight = 22.sp,
        )
        // [END_EXCLUDE]
    }
}
// [END android_compose_graphics_blur_radial_spotlight]

@Preview
// [START android_compose_graphics_blur_shader_mask]
@RequiresApi(Build.VERSION_CODES.TIRAMISU)
@Composable
fun ShaderBlurExample() {
    // Creates 6 diagonal frosted-glass bands across the image.
    val maskShader = remember {
        RuntimeShader(
            """
            uniform float2 size;
            half4 main(float2 coord) {
                float2 uv = coord / size;
                float band = smoothstep(0.4, 0.6, fract((uv.x + uv.y) * 3.0));
                return half4(0.0, 0.0, 0.0, half(band));
            }
            """.trimIndent(),
        )
    }
    val maskBlurSpec = remember(maskShader) {
        BlurRadiusSpec.shader(maxRadius = 24.dp) { sizePx ->
            maskShader.setFloatUniform("size", sizePx.width, sizePx.height)
            maskShader
        }
    }
    Image(
        painter = painterResource(id = R.drawable.dog),
        contentDescription = stringResource(id = R.string.dog_content_description),
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(150.dp)
            .blur(blurRadiusSpec = maskBlurSpec),
    )
}
// [END android_compose_graphics_blur_shader_mask]
