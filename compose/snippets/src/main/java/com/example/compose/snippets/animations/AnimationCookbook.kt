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

package com.example.compose.snippets.animations

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import com.example.compose.snippets.util.randomSampleImageUrl
import java.net.URLDecoder
import java.net.URLEncoder
import kotlin.math.roundToInt

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
fun AnimationExamplesScreen() {
    Column {
        AnimatedVisibilityCookbook()
    }
}

@Preview
@Composable
fun AnimatedVisibilityCookbook() {
    Box(modifier = Modifier.fillMaxSize()) {
        // [START android_compose_animation_cookbook_visibility]
        var visible by remember {
            mutableStateOf(true)
        }
        // Animated visibility will eventually remove the item from the composition once the animation has finished.
        AnimatedVisibility(visible) {
            // your composable here
            // [START_EXCLUDE]
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(colorGreen)
            ) {
            }
            // [END_EXCLUDE]
        }
        // [END android_compose_animation_cookbook_visibility]
        Button(modifier = Modifier.align(Alignment.BottomCenter), onClick = {
            visible = !visible
        }) {
            Text("Show/Hide")
        }
    }
}

@Preview
@Composable
fun AnimatedVisibilityCookbook_ModifierAlpha() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // [START android_compose_animation_cookbook_visibility_alpha]
        var visible by remember {
            mutableStateOf(true)
        }
        val animatedAlpha by animateFloatAsState(
            targetValue = if (visible) 1.0f else 0f,
            label = "alpha"
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .graphicsLayer {
                    alpha = animatedAlpha
                }
                .clip(RoundedCornerShape(8.dp))
                .background(colorGreen)
                .align(Alignment.TopCenter)
        ) {
        }
        // [END android_compose_animation_cookbook_visibility_alpha]
        Button(modifier = Modifier.align(Alignment.BottomCenter), onClick = {
            visible = !visible
        }) {
            Text("Show/Hide")
        }
    }
}

@Composable
fun AnimateBackgroundColor() {
    var animateBackgroundColor by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(Unit) {
        animateBackgroundColor = true
    }
    // [START android_compose_animate_background_color]
    val animatedColor by animateColorAsState(
        if (animateBackgroundColor) colorGreen else colorBlue,
        label = "color"
    )
    Column(
        modifier = Modifier.drawBehind {
            drawRect(animatedColor)
        }
    ) {
        // your composable here
    }
    // [END android_compose_animate_background_color]
}

@Preview
@Composable
fun AnimatePadding() {
    Box {
        // [START android_compose_animation_padding]
        var toggled by remember {
            mutableStateOf(false)
        }
        val animatedPadding by animateDpAsState(
            if (toggled) {
                0.dp
            } else {
                20.dp
            },
            label = "padding"
        )
        Box(
            modifier = Modifier
                .aspectRatio(1f)
                .fillMaxSize()
                .padding(animatedPadding)
                .background(Color(0xff53D9A1))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    toggled = !toggled
                }
        )
        // [END android_compose_animation_padding]
    }
}

@Preview
@Composable
fun AnimateSizeChange() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // [START android_compose_animation_size_change]
        var expanded by remember { mutableStateOf(false) }
        Box(
            modifier = Modifier
                .background(colorBlue)
                .animateContentSize()
                .height(if (expanded) 400.dp else 200.dp)
                .fillMaxWidth()
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    expanded = !expanded
                }

        ) {
        }
        // [END android_compose_animation_size_change]
    }
}

@Preview
@Composable
fun AnimateOffset() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // [START android_compose_animation_size_change]
        var moved by remember { mutableStateOf(false) }
        val pxToMove = with(LocalDensity.current) {
            100.dp.toPx().roundToInt()
        }
        val offset by animateIntOffsetAsState(
            targetValue = if (moved) {
                IntOffset(pxToMove, pxToMove)
            } else {
                IntOffset.Zero
            },
            label = "offset"
        )

        Box(
            modifier = Modifier
                .offset {
                    offset
                }
                .background(colorBlue)
                .size(100.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    moved = !moved
                }
        )
        // [END android_compose_animation_size_change]
    }
}

@Preview
@Composable
fun AnimateBetweenComposableDestinations() {
    // [START android_compose_animate_destinations]
    val navController = rememberNavController()
    val customBezierCurve = CubicBezierEasing(1.0f, 0f, 0.6f, 1f)
    NavHost(
        navController = navController, startDestination = "landing",
        enterTransition = {
            fadeIn(animationSpec = tween(60, delayMillis = 60, easing = LinearEasing
            )) + scaleIn(animationSpec = tween(300, easing = customBezierCurve), initialScale = 0.8f)
        },
        exitTransition = {
           scaleOut(animationSpec = tween(300, easing = customBezierCurve), targetScale = 1.1f)
        },
        popExitTransition = {
            scaleOut(animationSpec = tween(300, easing = customBezierCurve), targetScale = 0.8f)
        }
    ) {
        composable("landing") {
            ScreenLanding(onItemClicked = {
                navController.navigate("detail/${URLEncoder.encode(it)}")
            })
        }
        composable("detail/{photoUrl}",
            arguments = listOf(navArgument("photoUrl") { type = NavType.StringType })
        ) { backStackEntry ->
            ScreenDetails(
                photo = URLDecoder.decode(backStackEntry.arguments!!.getString("photoUrl")!!),
                onBackClicked = {
                    navController.popBackStack()
                })
        }
    }
    // [END android_compose_animate_destinations]
}

@Preview
@Composable
fun AnimateSizeChange_Specs() {
    Row(modifier = Modifier.fillMaxSize()) {
        var expanded by remember { mutableStateOf(false) }
        Column(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
        ) {
            Text("No spec set")
            Box(
                modifier = Modifier
                    .background(colorBlue)
                    .animateContentSize()
                    .height(if (expanded) 300.dp else 200.dp)
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        expanded = !expanded
                    }

            ) {
            }
        }
        Column(
            modifier = Modifier
                .padding(8.dp)
                .weight(1f)
        ) {
            Text("Custom spec")
            // [START android_compose_animation_size_change_spec]
            Box(
                modifier = Modifier
                    .background(colorBlue)
                    .animateContentSize(
                        spring(
                            stiffness = Spring.StiffnessLow,
                            dampingRatio = Spring.DampingRatioHighBouncy
                        )
                    )
                    .height(if (expanded) 300.dp else 200.dp)
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        expanded = !expanded
                    }

            ) {
            }
            // [END android_compose_animation_size_change_spec]
        }
    }
}

val colorGreen = Color(0xFF53D9A1)
val colorBlue = Color(0xFF4FC3F7)

@Preview
@Composable
fun AnimationLayoutIndividualItem() {
    var toggled by remember {
        mutableStateOf(false)
    }
    val interactionSource = remember {
        MutableInteractionSource()
    }
    Column(modifier = Modifier
        .fillMaxSize()
        .clickable(indication = null, interactionSource = interactionSource) {
            toggled = !toggled
        }) {
        val offset = animateIntOffsetAsState(
            targetValue = if (toggled) {
                IntOffset(150, 150)
            } else {
                IntOffset.Zero
            }, label = "offset"
        )
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(colorBlue)
        )
        Box(modifier = Modifier
            .layout { measurable, constraints ->
                val placeable = measurable.measure(constraints)
                layout(placeable.width + offset.value.x, placeable.height + offset.value.y) {
                    placeable.placeRelative(offset.value)
                }
            }
            .size(100.dp)
            .background(colorGreen)
        )
        Box(
            modifier = Modifier
                .size(100.dp)
                .background(colorBlue)
        )
    }

}

@Composable
private fun ScreenLanding(onItemClicked: (String) -> Unit) {
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(200.dp),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        content = {
            items(randomSizedPhotos) { photo ->
                AsyncImage(
                    model = photo,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .clickable {
                            onItemClicked(photo)
                        }
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ScreenDetails(photo: String, onBackClicked: () -> Unit) {
    Scaffold(modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(title = {
                Text("Photo Details")
            },
                navigationIcon = {
                    IconButton(onClick = { onBackClicked() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            AsyncImage(
                model = photo,
                contentScale = ContentScale.Crop,
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()

            )
            Text("Photo details")
        }
    }
}

private val randomSizedPhotos = listOf(
    randomSampleImageUrl(width = 1600, height = 900),
    randomSampleImageUrl(width = 900, height = 1600),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 300, height = 400),
    randomSampleImageUrl(width = 1600, height = 900),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 1600, height = 900),
    randomSampleImageUrl(width = 900, height = 1600),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 300, height = 400),
    randomSampleImageUrl(width = 1600, height = 900),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 900, height = 1600),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 300, height = 400),
    randomSampleImageUrl(width = 1600, height = 900),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 300, height = 400),
    randomSampleImageUrl(width = 1600, height = 900),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 900, height = 1600),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 300, height = 400),
    randomSampleImageUrl(width = 1600, height = 900),
    randomSampleImageUrl(width = 500, height = 500),
)
