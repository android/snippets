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

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.animateBounds
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.LookaheadScope
import androidx.compose.ui.layout.layout
import androidx.compose.ui.layout.onVisibilityChanged
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
private fun AnimateAlphaBad(targetAlpha: Float) {
    // [START android_compose_performance_anim_alpha_bad]
    val alpha by animateFloatAsState(targetValue = targetAlpha, label = "Alpha")
    // Reading alpha here registers a composition dependency on every frame
    Box(modifier = Modifier.alpha(alpha))
    // [END android_compose_performance_anim_alpha_bad]
}

@Composable
private fun AnimateAlphaGood(targetAlpha: Float) {
    // [START android_compose_performance_anim_alpha_good]
    val alpha by animateFloatAsState(targetValue = targetAlpha, label = "Alpha")
    // graphicsLayer with lambda skips composition and runs in the draw phase
    Box(
        modifier = Modifier.graphicsLayer {
            this.alpha = alpha
        }
    )
    // [END android_compose_performance_anim_alpha_good]
}

@Composable
private fun AnimateOffsetBad(targetOffset: Dp) {
    // [START android_compose_performance_anim_offset_bad]
    val animatedOffset by animateDpAsState(
        targetValue = targetOffset,
        label = "Offset",
    )
    Box(modifier = Modifier.offset(x = animatedOffset, y = 0.dp))
    // [END android_compose_performance_anim_offset_bad]
}

@Composable
private fun AnimateOffsetGood(targetOffsetPx: Int) {
    // [START android_compose_performance_anim_offset_good]
    val animatedOffset by animateIntAsState(
        targetValue = targetOffsetPx,
        label = "Offset",
    )
    Box(
        modifier = Modifier.offset {
            IntOffset(x = animatedOffset, y = 0)
        }
    )
    // [END android_compose_performance_anim_offset_good]
}

@Composable
private fun AnimateSizeBad(targetSize: Dp) {
    // [START android_compose_performance_anim_size_bad]
    val animatedSize by animateDpAsState(
        targetValue = targetSize,
        label = "Size",
    )
    Box(modifier = Modifier.size(animatedSize))
    // [END android_compose_performance_anim_size_bad]
}

@Composable
private fun AnimateSizeLayoutGood(targetWidthPx: Int) {
    // [START android_compose_performance_anim_size_layout_good]
    val animatedWidth by animateIntAsState(
        targetValue = targetWidthPx,
        label = "Width",
    )
    Box(
        modifier = Modifier.layout { measurable, constraints ->
            val placeable = measurable.measure(
                constraints.copy(
                    minWidth = animatedWidth,
                    maxWidth = animatedWidth
                )
            )
            layout(placeable.width, placeable.height) {
                placeable.placeRelative(0, 0)
            }
        }
    )
    // [END android_compose_performance_anim_size_layout_good]
}

@Composable
private fun AnimateSizeLookaheadGood(lookaheadScope: LookaheadScope, finalSize: Dp) {
    // [START android_compose_performance_anim_size_lookahead_good]
    with(lookaheadScope) {
        Box(modifier = Modifier.animateBounds(this, Modifier.size(finalSize)))
    }
    // [END android_compose_performance_anim_size_lookahead_good]
}

private object AnimatedCardBad {
    // [START android_compose_performance_anim_card_bad]
    @Composable
    fun AnimatedCard(alpha: Float) {
        Box(modifier = Modifier.graphicsLayer { this.alpha = alpha })
    }
    // [END android_compose_performance_anim_card_bad]
}

private object AnimatedCardGood {
    // [START android_compose_performance_anim_card_good]
    @Composable
    fun AnimatedCard(alphaProvider: () -> Float) {
        Box(modifier = Modifier.graphicsLayer { this.alpha = alphaProvider() })
    }
    // [END android_compose_performance_anim_card_good]
}

@OptIn(ExperimentalSharedTransitionApi::class)
private object SharedBoundsBad {
    // [START android_compose_performance_anim_shared_bounds_bad]
    @Composable
    fun FeedItemRow(
        item: FeedItem,
        sharedTransitionScope: SharedTransitionScope,
        animatedVisibilityScope: AnimatedVisibilityScope,
    ) {
        with(sharedTransitionScope) {
            ItemContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .sharedBounds(
                        rememberSharedContentState(key = item.id),
                        animatedVisibilityScope = animatedVisibilityScope,
                    )
            )
        }
    }
    // [END android_compose_performance_anim_shared_bounds_bad]
}

@OptIn(ExperimentalSharedTransitionApi::class)
private object SharedBoundsGood {
    // [START android_compose_performance_anim_shared_bounds_good]
    @Composable
    fun FeedItemRow(
        item: FeedItem,
        sharedTransitionScope: SharedTransitionScope,
        animatedVisibilityScope: AnimatedVisibilityScope,
    ) {
        var isVisibleInWindow by remember { mutableStateOf(false) }

        with(sharedTransitionScope) {
            val sharedBoundsModifier = if (isVisibleInWindow) {
                Modifier.sharedBounds(
                    rememberSharedContentState(key = item.id),
                    animatedVisibilityScope = animatedVisibilityScope,
                )
            } else {
                Modifier
            }

            ItemContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .onVisibilityChanged(
                        minFractionVisible = 0.001f,
                    ) { visible ->
                        isVisibleInWindow = visible
                    }
                    .then(sharedBoundsModifier)
            )
        }
    }
    // [END android_compose_performance_anim_shared_bounds_good]
}

// [START android_compose_performance_anim_node_animatable]
private class FadeRevealNode(
    var targetAlpha: Float,
    var durationMillis: Int
) : Modifier.Node(), DrawModifierNode {
    // Persistent node property (avoids remember overhead)
    private val alpha = Animatable(0f)

    override fun onAttach() {
        // Launches animation tied directly to node lifecycle
        coroutineScope.launch {
            alpha.animateTo(targetAlpha, tween(durationMillis))
        }
    }

    override fun ContentDrawScope.draw() {
        drawContext.canvas.saveLayer(
            bounds = size.toRect(),
            paint = Paint().apply {
                this.alpha = this@FadeRevealNode.alpha.value
            }
        )
        drawContent()
        drawContext.canvas.restore()
    }
}
// [END android_compose_performance_anim_node_animatable]

internal data class FeedItem(val id: String)

@Composable
private fun ItemContent(modifier: Modifier = Modifier) {
    Box(modifier = modifier)
}
