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

package com.example.ink.snippets.app

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Build
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.graphics.withSaveLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.util.fastForEach
import androidx.core.graphics.withSave
import androidx.ink.authoring.compose.InProgressStrokes
import androidx.ink.brush.Brush
import androidx.ink.brush.BrushFamily
import androidx.ink.brush.StockBrushes
import androidx.ink.brush.TextureBitmapStore
import androidx.ink.brush.compose.composeColor
import androidx.ink.brush.compose.copyWithComposeColor
import androidx.ink.brush.compose.createWithComposeColor
import androidx.ink.rendering.android.canvas.CanvasStrokeRenderer
import androidx.ink.strokes.Stroke
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ink.snippets.R
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

// [START android_compose_stylus_ink_app_default_pen_brush]
/**
 * Creates a default pressure-sensitive pen [Brush].
 *
 * The `epsilon` parameter sets the geometric tolerance (minimum point distance
 * in stroke units) controlling how far a stroke can be zoomed before triangle
 * artifacts appear (`0f < epsilon <= size`).
 */
fun createDefaultPenBrush(): Brush {
    return Brush.createWithComposeColor(
        family = StockBrushes.pressurePen(),
        color = Color.Black,
        size = 5f,
        epsilon = 0.1f,
    )
}
// [END android_compose_stylus_ink_app_default_pen_brush]

// [START android_compose_stylus_ink_app_drawing_surface]
@Composable
fun DrawingSurface(
    strokes: List<Stroke>,
    canvasStrokeRenderer: CanvasStrokeRenderer,
    currentBrush: Brush,
    onGetNextBrush: () -> Brush,
    onStrokesFinished: (List<Stroke>) -> Unit,
    isEraserMode: Boolean,
    modifier: Modifier = Modifier,
    textureStore: AppTextureBitmapStore = LocalTextureStore.current,
    onErase: (Float, Float) -> Unit = { _, _ -> },
    onEraseStart: () -> Unit = {},
    onEraseEnd: () -> Unit = {},
) {
    val cacheGen by textureStore.generation.collectAsStateWithLifecycle()

    // Holds newly finished strokes synchronously for the single frame before
    // asynchronous StateFlow collection delivers the updated strokes list.
    var pendingStrokes by remember { mutableStateOf<List<Stroke>>(emptyList()) }

    SideEffect {
        if (pendingStrokes.isNotEmpty()) {
            pendingStrokes = if (strokes.isEmpty()) {
                emptyList()
            } else {
                pendingStrokes.filter { pending -> strokes.none { it === pending } }
            }
        }
    }

    val strokesToDraw = remember(strokes, pendingStrokes) {
        if (pendingStrokes.isEmpty()) {
            strokes
        } else {
            strokes + pendingStrokes.filter { pending -> strokes.none { it === pending } }
        }
    }
    val identityMatrix = remember { Matrix() }

    val currentOnErase by rememberUpdatedState(onErase)
    val currentOnEraseStart by rememberUpdatedState(onEraseStart)
    val currentOnEraseEnd by rememberUpdatedState(onEraseEnd)

    Box(modifier = modifier) {
        // Layer 1: Dry ink - completed strokes + same-frame pending handoff strokes
        Canvas(modifier = Modifier.fillMaxSize()) {
            val nativeCanvas = drawContext.canvas.nativeCanvas
            strokesToDraw.fastForEach { stroke ->
                nativeCanvas.withSave {
                    canvasStrokeRenderer.draw(
                        canvas = this,
                        stroke = stroke,
                        strokeToScreenTransform = identityMatrix,
                    )
                }
            }
        }

        // Layer 2: Eraser touch handler OR Wet ink (InProgressStrokes)
        if (isEraserMode) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragStart = { currentOnEraseStart() },
                            onDragEnd = { currentOnEraseEnd() },
                            onDragCancel = { currentOnEraseEnd() },
                            onDrag = { change, _ ->
                                currentOnErase(change.position.x, change.position.y)
                                change.consume()
                            },
                        )
                    }
            )
        } else {
            key(cacheGen) {
                InProgressStrokes(
                    defaultBrush = currentBrush,
                    nextBrush = onGetNextBrush,
                    onStrokesFinished = { newStrokes ->
                        pendingStrokes = pendingStrokes + newStrokes
                        onStrokesFinished(newStrokes)
                    },
                    textureBitmapStore = textureStore,
                )
            }
        }
    }
}
// [END android_compose_stylus_ink_app_drawing_surface]

// [START android_compose_stylus_ink_app_highlighter_blend_mode]
@Composable
fun StrokeCanvas(
    strokes: List<Stroke>,
    canvasStrokeRenderer: CanvasStrokeRenderer,
    modifier: Modifier = Modifier,
) {
    val multiplyPaint = remember { Paint().apply { blendMode = BlendMode.Multiply } }
    val srcOverPaint = remember { Paint().apply { blendMode = BlendMode.SrcOver } }
    val identityMatrix = remember { Matrix() }

    Canvas(modifier = modifier.fillMaxSize()) {
        val nativeCanvas = drawContext.canvas.nativeCanvas
        val bounds = drawContext.size.toRect()

        strokes.fastForEach { stroke ->
            val paint = if (stroke.brush.family == StockBrushes.highlighter()) {
                multiplyPaint
            } else {
                srcOverPaint
            }

            drawContext.canvas.withSaveLayer(bounds, paint) {
                nativeCanvas.withSave {
                    canvasStrokeRenderer.draw(
                        canvas = this,
                        stroke = stroke,
                        strokeToScreenTransform = identityMatrix,
                    )
                }
            }
        }
    }
}
// [END android_compose_stylus_ink_app_highlighter_blend_mode]

private object EmojiHighlighterSnippet {
    // [START android_compose_stylus_ink_app_emoji_highlighter]
    val supportsMiniEmojiTrail = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE

    val heartHighlighter = StockBrushes.emojiHighlighter(
        clientTextureId = "emoji-heart",
        showMiniEmojiTrail = supportsMiniEmojiTrail,
    )
    // [END android_compose_stylus_ink_app_emoji_highlighter]
}

// [START android_compose_stylus_ink_app_switch_recolor_brush]
private const val HIGHLIGHTER_ALPHA = 0.3f

fun switchBrushFamily(currentBrush: Brush, targetFamily: BrushFamily): Brush {
    val baseColor = currentBrush.composeColor
    val adjustedColor = if (targetFamily == StockBrushes.highlighter()) {
        baseColor.copy(alpha = HIGHLIGHTER_ALPHA)
    } else {
        baseColor.copy(alpha = 1f)
    }
    return currentBrush
        .copy(family = targetFamily)
        .copyWithComposeColor(color = adjustedColor)
}

fun recolorStroke(stroke: Stroke, newColor: Color): Stroke {
    val updatedBrush = stroke.brush.copyWithComposeColor(color = newColor)
    return stroke.copy(brush = updatedBrush)
}
// [END android_compose_stylus_ink_app_switch_recolor_brush]

// [START android_compose_stylus_ink_app_texture_bitmap_store]
class AppTextureBitmapStore(context: Context) : TextureBitmapStore {
    private val resources = context.resources
    private val textureResources: Map<String, Int> = mapOf(
        "emoji-heart" to R.drawable.emoji_heart,
    )
    private val loadedBitmaps = mutableMapOf<String, Bitmap>()

    private val _generation = MutableStateFlow(0)
    val generation: StateFlow<Int> = _generation.asStateFlow()

    override operator fun get(clientTextureId: String): Bitmap? {
        return loadedBitmaps.getOrPut(clientTextureId) {
            textureResources[clientTextureId]?.let { resId ->
                BitmapFactory.decodeResource(resources, resId)
            } ?: return null
        }
    }

    fun loadTexture(textureId: String, bitmap: Bitmap) {
        loadedBitmaps[textureId] = bitmap
        _generation.update { it + 1 }
    }
}

val LocalTextureStore = staticCompositionLocalOf<AppTextureBitmapStore> {
    error("No AppTextureBitmapStore provided")
}
// [END android_compose_stylus_ink_app_texture_bitmap_store]
