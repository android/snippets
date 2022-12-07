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

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.DrawModifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
@Composable
@Preview
fun ModifierDrawWithContent() {
    // [START android_compose_graphics_modifiers_drawWithContent]
    var pointerOffset by remember {
        mutableStateOf(Offset(0f, 0f))
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput("dragging") {
                detectDragGestures { change, dragAmount ->
                    pointerOffset += dragAmount
                }
            }
            .onSizeChanged {
                pointerOffset = Offset(it.width / 2f, it.height / 2f)
            }
            .drawWithContent {
                drawContent()
                // draws a fully black area with a small keyhole at pointerOffset that’ll show part of the UI.
                drawRect(
                    Brush.radialGradient(
                        listOf(Color.Transparent, Color.Black),
                        center = pointerOffset,
                        radius = 100.dp.toPx(),
                    )
                )
            }
    ) {
        // Your composables here
    }
    // [END android_compose_graphics_modifiers_drawWithContent]
}

@Composable
@Preview
fun ModifierDrawBehind() {
    // [START android_compose_graphics_modifiers_drawBehind]
    Text(
        "Hello Compose!",
        modifier = Modifier
            .drawBehind {
                drawRoundRect(
                    Color(0xFFBBAAEE),
                    cornerRadius = CornerRadius(10.dp.toPx())
                )
            }
            .padding(4.dp)
    )
    // [END android_compose_graphics_modifiers_drawBehind]
}

@Composable
@Preview
fun ModifierDrawWithCache() {
    // [START android_compose_graphics_modifiers_drawWithCache]
    Text(
        "Hello Compose!",
        modifier = Modifier
            .drawWithCache {
                val brush = Brush.linearGradient(
                    listOf(
                        Color(0xFF9E82F0),
                        Color(0xFF42A5F5)
                    )
                )
                onDrawBehind {
                    drawRoundRect(
                        brush,
                        cornerRadius = CornerRadius(10.dp.toPx())
                    )
                }
            }
    )
    // [END android_compose_graphics_modifiers_drawWithCache]
}

@Composable
@Preview
fun ModifierGraphicsLayerModifierScale() {
    // [START android_compose_graphics_modifiers_graphicsLayer_scale]
    Image(
        painter = painterResource(id = R.drawable.sunset),
        contentDescription = "Sunset",
        modifier = Modifier
            .graphicsLayer {
                this.scaleX = 1.2f
                this.scaleY = 0.8f
            }
    )
    // [END android_compose_graphics_modifiers_graphicsLayer_scale]
}

@Composable
@Preview
fun ModifierGraphicsLayerModifierTranslation() {
    // [START android_compose_graphics_modifiers_graphicsLayer_translation]
    Image(
        painter = painterResource(id = R.drawable.sunset),
        contentDescription = "Sunset",
        modifier = Modifier
            .graphicsLayer {
                this.translationX = 100.dp.toPx()
                this.translationY = 10.dp.toPx()
            }
    )
    // [END android_compose_graphics_modifiers_graphicsLayer_translation]
}

@Composable
@Preview
fun ModifierGraphicsLayerModifierRotation() {
    // [START android_compose_graphics_modifiers_graphicsLayer_rotation]
    Image(
        painter = painterResource(id = R.drawable.sunset),
        contentDescription = "Sunset",
        modifier = Modifier
            .graphicsLayer {
                this.rotationX = 90f
                this.rotationY = 275f
                this.rotationZ = 180f
            }
    )
    // [END android_compose_graphics_modifiers_graphicsLayer_rotation]
}

@Preview
@Composable
fun ModifierGraphicsLayerModifierOrigin() {
    // [START android_compose_graphics_modifiers_graphicsLayer_origin]
    Image(
        painter = painterResource(id = R.drawable.sunset),
        contentDescription = "Sunset",
        modifier = Modifier
            .graphicsLayer {
                this.transformOrigin = TransformOrigin(0f, 0f)
                this.rotationX = 90f
                this.rotationY = 275f
                this.rotationZ = 180f
            }
    )
    // [END android_compose_graphics_modifiers_graphicsLayer_origin]
}

@Preview
@Composable
fun ModifierGraphicsLayerModifierClipShape() {
    // [START android_compose_graphics_modifiers_graphicsLayer_clip_shape]
    Column(modifier = Modifier.padding(16.dp)) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer {
                    clip = true
                    shape = CircleShape
                }
                .background(Color(0xFFF06292))
        ) {
            Text(
                "Hello Compose",
                style = TextStyle(color = Color.Black, fontSize = 46.sp),
                modifier = Modifier.align(Alignment.Center)
            )
        }
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CircleShape)
                .background(Color(0xFF4DB6AC))
        )
    }
    // [END android_compose_graphics_modifiers_graphicsLayer_clip_shape]

    // [START android_compose_graphics_modifiers_graphicsLayer_clip_shape_2]
    Column(modifier = Modifier.padding(16.dp)) {
        Box(
            modifier = Modifier
                .clip(RectangleShape)
                .size(200.dp)
                .border(2.dp, Color.Black)
                .graphicsLayer {
                    clip = true
                    shape = CircleShape
                    translationY = 50.dp.toPx()
                }
                .background(Color(0xFFF06292))
        ) {
            Text(
                "Hello Compose",
                style = TextStyle(color = Color.Black, fontSize = 46.sp),
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(RoundedCornerShape(500.dp))
                .background(Color(0xFF4DB6AC))
        )
    }
    // [END android_compose_graphics_modifiers_graphicsLayer_clip_shape_2]
}

@Preview
@Composable
fun ModifierGraphicsLayerAlpha() {
    // [START android_compose_graphics_modifiers_graphicsLayer_alpha]
    Image(
        painter = painterResource(id = R.drawable.sunset),
        contentDescription = "clock",
        modifier = Modifier
            .graphicsLayer {
                this.alpha = 0.5f
            }
    )
    // [END android_compose_graphics_modifiers_graphicsLayer_alpha]
}

@Preview
@Composable
fun ModifierGraphicsLayerCompositingStrategy() {
    /* Commented out until compositing Strategy is rolled out to production
        // [START android_compose_graphics_modifiers_graphicsLayer_compositing_strategy]

        Image(painter = painterResource(id = R.drawable.dog),
           contentDescription = "Dog",
           contentScale = ContentScale.Crop,
           modifier = Modifier
               .size(120.dp)
               .aspectRatio(1f)
               .background(
                   Brush.linearGradient(
                       listOf(
                           Color(0xFFC5E1A5),
                           Color(0xFF80DEEA)
                       )
                   )
               )
               .padding(8.dp)
               .graphicsLayer {
                   compositingStrategy = CompositingStrategy.Offscreen
               }
               .drawWithCache {
                   val path = Path()
                   path.addOval(
                       Rect(
                           topLeft = Offset.Zero,
                           bottomRight = Offset(size.width, size.height)
                       )
                   )
                   onDrawWithContent {
                       clipPath(path) {
                           // this draws the actual image - if you don't call drawContent, it wont
                           // render anything
                           this@onDrawWithContent.drawContent()
                       }
                       val dotSize = size.width / 8f
                       // Clip a white border for the content
                       drawCircle(
                           Color.Black,
                           radius = dotSize,
                           center = Offset(
                               x = size.width - dotSize,
                               y = size.height - dotSize
                           ),
                           blendMode = BlendMode.Clear
                       )
                       // draw the red circle indication
                       drawCircle(
                           Color(0xFFEF5350), radius = dotSize * 0.8f,
                           center = Offset(
                               x = size.width - dotSize,
                               y = size.height - dotSize
                           )
                       )
                   }

               }
        )
        // [END android_compose_graphics_modifiers_graphicsLayer_compositing_strategy]
    */
}
/* Commented out until compositing Strategy is rolled out to production
@Preview
// [START android_compose_graphics_modifier_compositing_strategy_differences]
@Composable
fun CompositingStrategyExamples() {
   Column(
       modifier = Modifier
           .fillMaxSize()
           .wrapContentSize(Alignment.Center)
   ) {
       /** Does not clip content even with a graphics layer usage here. By default, graphicsLayer
       does not allocate + rasterize content into a separate layer but instead is used
       for isolation. That is draw invalidations made outside of this graphicsLayer will not
       re-record the drawing instructions in this composable as they have not changed **/
       Canvas(
           modifier = Modifier
               .graphicsLayer()
               .size(100.dp) // Note size of 100 dp here
               .border(2.dp, color = Color.Blue)
       ) {
           // ... and drawing a size of 200 dp here outside the bounds
           drawRect(color = Color.Magenta, size = Size(200.dp.toPx(), 200.dp.toPx()))
       }

       Spacer(modifier = Modifier.size(300.dp))

       /** Clips content as alpha usage here creates an offscreen buffer to rasterize content
       into first then draws to the original destination **/
       Canvas(
           modifier = Modifier
               // force to an offscreen buffer
               .graphicsLayer(compositingStrategy = CompositingStrategy.Offscreen)
               .size(100.dp) // Note size of 100 dp here
               .border(2.dp, color = Color.Blue)
       ) {
           /** ... and drawing a size of 200 dp. However, because of the CompositingStrategy.Offscreen usage above, the
           content gets clipped **/
           drawRect(color = Color.Red, size = Size(200.dp.toPx(), 200.dp.toPx()))
       }
   }
}
// [END android_compose_graphics_modifier_compositing_strategy_differences]
 */

/* Commented out until compositing Strategy is rolled out to production
// [START android_compose_graphics_modifier_compositing_strategy_modulate_alpha]
@Preview
@Composable
fun CompositingStratgey_ModulateAlpha() {
  Column(
      modifier = Modifier
          .fillMaxSize()
          .padding(32.dp)
  ) {
      // Base drawing, no alpha applied
      Canvas(
          modifier = Modifier.size(200.dp)
      ) {
          drawSquares()
      }

      Spacer(modifier = Modifier.size(36.dp))

      // Alpha 0.5f applied to whole composable
      Canvas(modifier = Modifier
          .size(200.dp)
          .graphicsLayer {
              alpha = 0.5f
          }) {
          drawSquares()
      }
      Spacer(modifier = Modifier.size(36.dp))

      // 0.75f alpha applied to each draw call when using ModulateAlpha
      Canvas(modifier = Modifier
          .size(200.dp)
          .graphicsLayer {
              compositingStrategy = CompositingStrategy.ModulateAlpha
              alpha = 0.75f
          }) {
          drawSquares()
      }
  }
}

private fun DrawScope.drawSquares() {

  val size = Size(100.dp.toPx(), 100.dp.toPx())
  drawRect(color = Red, size = size)
  drawRect(
      color = Purple, size = size,
      topLeft = Offset(size.width / 4f, size.height / 4f)
  )
  drawRect(
      color = Yellow, size = size,
      topLeft = Offset(size.width / 4f * 2f, size.height / 4f * 2f)
  )
}

val Purple = Color(0xFF7E57C2)
val Yellow = Color(0xFFFFCA28)
val Red = Color(0xFFEF5350)
// [END android_compose_graphics_modifier_compositing_strategy_modulate_alpha]
*/

// [START android_compose_graphics_modifier_flipped]
class FlippedModifier : DrawModifier {
    override fun ContentDrawScope.draw() {
        scale(1f, -1f) {
            this@draw.drawContent()
        }
    }
}

fun Modifier.flipped() = this.then(FlippedModifier())
// [END android_compose_graphics_modifier_flipped]

@Preview
@Composable
fun ModifierGraphicsFlippedUsage() {
    // [START android_compose_graphics_modifier_flipped_usage]
    Text(
        "Hello Compose!",
        modifier = Modifier
            .flipped()
    )
    // [END android_compose_graphics_modifier_flipped_usage]
}
