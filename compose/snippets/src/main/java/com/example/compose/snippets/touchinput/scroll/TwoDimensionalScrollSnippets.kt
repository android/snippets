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

package com.example.compose.snippets.touchinput.scroll

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.draggable2D
import androidx.compose.foundation.gestures.rememberDraggable2DState
import androidx.compose.foundation.gestures.rememberScrollable2DState
import androidx.compose.foundation.gestures.scrollable2D
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.snippets.R
import kotlin.math.roundToInt

@Preview
// [START android_compose_touchinput_scroll_scrollable2D_basic]
@Composable
private fun Scrollable2DSample() {
    // 1. Manually track the total distance the user has moved in both X and Y directions
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            // [START_EXCLUDE]
            .background(Color(0xFFF5F5F5)),
            // [END_EXCLUDE]
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                // 2. Attach the 2D scroll logic to capture XY movement deltas
                .scrollable2D(
                    state = rememberScrollable2DState { delta ->
                        // 3. Update the cumulative offset state with the new movement delta
                        offset += delta

                        // Return the delta to indicate the entire movement was handled by this box
                        delta
                    }
                )
                // [START_EXCLUDE]
                .background(Color(0xFF6200EE), shape = RoundedCornerShape(16.dp)),
                // [END_EXCLUDE]
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 4. Display the current X and Y values from the offset state in real-time
                Text(
                    text = "X: ${offset.x.roundToInt()}",
                    color = Color.White,
                    fontSize = 20.sp,
                    // [START_EXCLUDE]
                    fontWeight = FontWeight.Bold
                    // [END_EXCLUDE]
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Y: ${offset.y.roundToInt()}",
                    color = Color.White,
                    fontSize = 20.sp,
                    // [START_EXCLUDE]
                    fontWeight = FontWeight.Bold
                    // [END_EXCLUDE]
                )
            }
        }
    }
}
// [END android_compose_touchinput_scroll_scrollable2D_basic]


@Preview
// [START android_compose_touchinput_scroll_scrollable2D_pan_large_viewport]
@Composable
private fun Panning2DImage(modifier: Modifier = Modifier) {

    // Manually track the total distance the user has moved in both X and Y directions
    val offset = remember { mutableStateOf(Offset.Zero) }

    // Define how gestures are captured. The lambda is called for every finger movement
    val scrollState = rememberScrollable2DState { delta ->
        offset.value += delta
        delta
    }

    // The Viewport (Container): A fixed-size box that acts as a window into the larger content
    Box(
        modifier = modifier
            .size(600.dp, 400.dp) // The visible area dimensions
            // [START_EXCLUDE]
            .border(width = 2.dp, color = Color.Black, shape = RoundedCornerShape(0.dp))
            .background(color = Color.LightGray)
            // [END_EXCLUDE]
            // Hide any parts of the large content that sit outside this container's boundaries
            .clipToBounds()
            // Apply the 2D scroll modifier to intercept touch and fling gestures in all directions
            .scrollable2D(state = scrollState),
        contentAlignment = Alignment.Center,
    ) {
        // The Content: An image given a much larger size than the container viewport
        Image(
            painter = painterResource(R.drawable.cheese_5),
            contentDescription = null,
            modifier = Modifier
                .requiredSize(1200.dp, 800.dp)
                // Manual Scroll Effect: Since scrollable2D doesn't move content automatically,
                // we use graphicsLayer to shift the drawing position based on the tracked offset.
                .graphicsLayer {
                    translationX = offset.value.x
                    translationY = offset.value.y
                },
            contentScale = ContentScale.FillBounds
        )
    }
}
// [END android_compose_touchinput_scroll_scrollable2D_pan_large_viewport]


@Preview
// [START android_compose_touchinput_scroll_scrollable2D_nested_scrolling]
@Composable
private fun NestedScrollable2DSample() {
    var offset by remember { mutableStateOf(Offset.Zero) }
    val maxScroll = 300f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(Color(0xFFF5F5F5)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Scroll down to find the 2D Box",
            modifier = Modifier.padding(top = 100.dp, bottom = 500.dp),
            style = TextStyle(fontSize = 18.sp, color = Color.Gray)
        )

        // The Child: A 2D scrollable box with nested scroll coordination
        Box(
            modifier = Modifier
                .size(250.dp)
                .scrollable2D(
                    state = rememberScrollable2DState { delta ->
                        val oldOffset = offset

                        // Calculate new potential offset and clamp it to our boundaries
                        val newX = (oldOffset.x + delta.x).coerceIn(-maxScroll, maxScroll)
                        val newY = (oldOffset.y + delta.y).coerceIn(-maxScroll, maxScroll)

                        val newOffset = Offset(newX, newY)

                        // Calculate exactly how much was consumed by the child
                        val consumed = newOffset - oldOffset

                        offset = newOffset

                        // IMPORTANT: Return ONLY the consumed delta.
                        // The remaining (unconsumed) delta propagates to the parent Column.
                        consumed
                    }
                )
                // [START_EXCLUDE]
                .background(Color(0xFF6200EE), shape = RoundedCornerShape(16.dp)),
                // [END_EXCLUDE]
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("2D Panning Zone", color = Color.White.copy(alpha = 0.7f), fontSize = 12.sp)
                Spacer(Modifier.height(8.dp))
                Text("X: ${offset.x.roundToInt()}", color = Color.White, fontWeight = FontWeight.Bold)
                Text("Y: ${offset.y.roundToInt()}", color = Color.White, fontWeight = FontWeight.Bold)
            }
        }

        Text(
            "Once the Purple Box hits Y: 300 or -300,\nthis parent list will take over the vertical scroll.",
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 40.dp, bottom = 800.dp),
            style = TextStyle(fontSize = 14.sp, color = Color.Gray)
        )
    }
}
// [END android_compose_touchinput_scroll_scrollable2D_nested_scrolling]


@Preview
// [START android_compose_touchinput_scroll_draggable2D_basic]
@Composable
private fun DraggableComposableElement() {
    // 1. Track the position of the floating window
    var offset by remember { mutableStateOf(Offset.Zero) }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFFF5F5F5))) {
        Box(
            modifier = Modifier
                // 2. Apply the offset to the box's position
                .offset { IntOffset(offset.x.roundToInt(), offset.y.roundToInt()) }
                // [START_EXCLUDE]
                .size(160.dp, 90.dp)
                .background(Color(0xFF6200EE), RoundedCornerShape(8.dp))
                .border(1.dp, Color.White, RoundedCornerShape(8.dp))
                // [END_EXCLUDE]
                // 3. Attach the 2D drag logic
                .draggable2D(
                    state = rememberDraggable2DState { delta ->
                        // 4. Update the position based on the movement delta
                        offset += delta
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            Text("Video Preview", color = Color.White, fontSize = 12.sp)
        }
    }
}
// [END android_compose_touchinput_scroll_draggable2D_basic]


@Preview
// [START android_compose_touchinput_scroll_draggable2D_color_picker]
@Composable
private fun ExampleColorSelector(
    // [START_EXCLUDE]
    hue: Float = 0f,
    // [END_EXCLUDE]
)  {
    // 1. Maintain the 2D position of the selector in state.
    var selectorOffset by remember { mutableStateOf(Offset.Zero) }

    // 2. Track the size of the background container.
    var containerSize by remember { mutableStateOf(IntSize.Zero) }

    Box(
        modifier = Modifier
            .size(300.dp, 200.dp)
            // Capture the actual pixel dimensions of the container when it's laid out.
            .onSizeChanged { containerSize = it }
            .clip(RoundedCornerShape(12.dp))
            .background(
                brush = remember(hue) {
                    // Create a simple gradient representing Saturation and Value for the given Hue.
                    Brush.linearGradient(listOf(Color.White, Color.hsv(hue, 1f, 1f)))
                }
            )
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .graphicsLayer {
                    // Center the selector on the finger by subtracting half its size.
                    translationX = selectorOffset.x - (24.dp.toPx() / 2)
                    translationY = selectorOffset.y - (24.dp.toPx() / 2)
                }
                // [START_EXCLUDE]
                // Configure the shadow
                .dropShadow(shape = CircleShape) {
                    color = Color.Black.copy(alpha = 0.2f)
                    radius = 4.dp.toPx()
                    offset = Offset(0f, 2.dp.toPx())
                }
                .background(Color.Transparent, CircleShape)
                .border(2.dp, Color.White, CircleShape)
                // [END_EXCLUDE]
                // 3. Configure 2D touch dragging.
                .draggable2D(
                    state = rememberDraggable2DState { delta ->
                        // 4. Calculate the new position and clamp it to the container bounds
                        val newX = (selectorOffset.x + delta.x)
                            .coerceIn(0f, containerSize.width.toFloat())
                        val newY = (selectorOffset.y + delta.y)
                            .coerceIn(0f, containerSize.height.toFloat())

                        selectorOffset = Offset(newX, newY)
                    }
                )
        )
    }
}
// [END android_compose_touchinput_scroll_draggable2D_color_picker]




