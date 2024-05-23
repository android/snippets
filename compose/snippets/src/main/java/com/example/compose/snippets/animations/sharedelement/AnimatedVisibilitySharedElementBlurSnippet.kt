/*
 * Copyright 2024 The Android Open Source Project
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

@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.compose.snippets.animations.sharedelement

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlurEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.layer.GraphicsLayer
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.R

private val listSnacks = listOf(
    Snack("Cupcake", "", R.drawable.cupcake),
    Snack("Donut", "", R.drawable.donut),
    Snack("Eclair", "", R.drawable.eclair),
    Snack("Froyo", "", R.drawable.froyo),
    Snack("Gingerbread", "", R.drawable.gingerbread),
    Snack("Honeycomb", "", R.drawable.honeycomb),
)

private fun <T> animationSpec() = tween<T>(durationMillis = 500)
private val boundsTransition = BoundsTransform { _, _ -> animationSpec() }
private val shapeForSharedElement = RoundedCornerShape(16.dp)

@OptIn(ExperimentalSharedTransitionApi::class)
@Preview
@Composable
private fun AnimatedVisibilitySharedElementBlurLayer() {
    var selectedSnack by remember { mutableStateOf<Snack?>(null) }
    val graphicsLayer = rememberGraphicsLayer()
    val animateBlurRadius = animateFloatAsState(
        targetValue = if (selectedSnack != null) 20f else 0f,
        label = "blur radius",
        animationSpec = animationSpec()
    )

    SharedTransitionLayout(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.LightGray.copy(alpha = 0.5f))
                .blurLayer(graphicsLayer, animateBlurRadius.value)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            itemsIndexed(listSnacks, key = { index, snack -> snack.name }) { index, snack ->
                SnackItem(
                    snack = snack,
                    onClick = {
                        selectedSnack = snack
                    },
                    visible = selectedSnack != snack,
                    modifier = Modifier.animateItem(
                        placementSpec = animationSpec(),
                        fadeOutSpec = animationSpec(),
                        fadeInSpec = animationSpec()
                    )
                )
            }
        }

        SnackEditDetails(
            snack = selectedSnack,
            onConfirmClick = {
                selectedSnack = null
            }
        )
    }
}

fun Modifier.blurLayer(layer: GraphicsLayer, radius: Float): Modifier {
    return if (radius == 0f) this else this.drawWithContent {
        layer.apply {
            record {
                this@drawWithContent.drawContent()
            }
            // will apply a blur on API 31+
            this.renderEffect = BlurEffect(radius, radius, TileMode.Decal)
        }
        drawLayer(layer)
    }
}
@Composable
fun SharedTransitionScope.SnackItem(
    snack: Snack,
    visible: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(animationSpec = animationSpec()) + scaleIn(
            animationSpec()
        ),
        exit = fadeOut(animationSpec = animationSpec()) + scaleOut(
            animationSpec()
        )
    ) {
        Box(
            modifier = Modifier
                .sharedBounds(
                    sharedContentState = rememberSharedContentState(key = "${snack.name}-bounds"),
                    animatedVisibilityScope = this,
                    boundsTransform = boundsTransition,
                    clipInOverlayDuringTransition = OverlayClip(shapeForSharedElement)
                )
                .background(Color.White, shapeForSharedElement)
                .clip(shapeForSharedElement)
        ) {
            SnackContents(
                snack = snack,
                modifier = Modifier.sharedElement(
                    state = rememberSharedContentState(key = snack.name),
                    animatedVisibilityScope = this@AnimatedVisibility,
                    boundsTransform = boundsTransition,
                ),
                onClick = onClick
            )
        }
    }
}
