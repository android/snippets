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

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.ArcMode
import androidx.compose.animation.core.ExperimentalAnimationSpecApi
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.compose.snippets.R
import com.example.compose.snippets.ui.theme.LavenderLight
import com.example.compose.snippets.ui.theme.RoseLight

@Preview
@Composable
fun SharedElementApp_BoundsTransformExample() {
    var showDetails by remember {
        mutableStateOf(false)
    }
    SharedTransitionLayout {
        AnimatedContent(
            showDetails,
            label = "basic_transition"
        ) { targetState ->
            if (!targetState) {
                MainContent(
                    onShowDetails = {
                        showDetails = true
                    },
                    animatedVisibilityScope = this@AnimatedContent,
                    sharedTransitionScope = this@SharedTransitionLayout
                )
            } else {
                DetailsContent(
                    onBack = {
                        showDetails = false
                    },
                    animatedVisibilityScope = this@AnimatedContent,
                    sharedTransitionScope = this@SharedTransitionLayout
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationSpecApi::class)
@Composable
private fun MainContent(
    onShowDetails: () -> Unit,
    modifier: Modifier = Modifier,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .fillMaxWidth()
                    .sharedBounds(
                        rememberSharedContentState(key = "bounds"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        enter = fadeIn(
                            tween(
                                boundsAnimationDurationMillis,
                                easing = FastOutSlowInEasing
                            )
                        ),
                        exit = fadeOut(
                            tween(
                                boundsAnimationDurationMillis,
                                easing = FastOutSlowInEasing
                            )
                        ),
                        boundsTransform = boundsTransform
                    )
                    .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .background(LavenderLight, RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onShowDetails()
                    }
                    .padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cupcake),
                    contentDescription = "Cupcake",
                    modifier = Modifier
                        .sharedElement(
                            rememberSharedContentState(key = "image"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = boundsTransform
                        )
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                val textBoundsTransform = BoundsTransform { initialBounds, targetBounds ->
                    keyframes {
                        durationMillis = boundsAnimationDurationMillis
                        initialBounds at 0 using ArcMode.ArcBelow using FastOutSlowInEasing
                        targetBounds at boundsAnimationDurationMillis
                    }
                }
                Text(
                    "Cupcake", fontSize = 21.sp,
                    modifier = Modifier.sharedBounds(
                        rememberSharedContentState(key = "title"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = textBoundsTransform
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalAnimationSpecApi::class)
@Composable
private fun DetailsContent(
    modifier: Modifier = Modifier,
    onBack: () -> Unit,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope
) {
    with(sharedTransitionScope) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .padding(top = 200.dp, start = 16.dp, end = 16.dp)
                    .sharedBounds(
                        rememberSharedContentState(key = "bounds"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        enter = fadeIn(
                            tween(
                                durationMillis = boundsAnimationDurationMillis,
                                easing = FastOutSlowInEasing
                            )
                        ),
                        exit = fadeOut(
                            tween(
                                durationMillis = boundsAnimationDurationMillis,
                                easing = FastOutSlowInEasing
                            )
                        ),
                        boundsTransform = boundsTransform
                    )
                    .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .background(RoseLight, RoundedCornerShape(8.dp))
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) {
                        onBack()
                    }
                    .padding(8.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.cupcake),
                    contentDescription = "Cupcake",
                    modifier = Modifier
                        .sharedElement(
                            rememberSharedContentState(key = "image"),
                            animatedVisibilityScope = animatedVisibilityScope,
                            boundsTransform = boundsTransform
                        )
                        .size(200.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                // [START android_compose_shared_element_text_bounds_transform]
                val textBoundsTransform = BoundsTransform { initialBounds, targetBounds ->
                    keyframes {
                        durationMillis = boundsAnimationDurationMillis
                        initialBounds at 0 using ArcMode.ArcBelow using FastOutSlowInEasing
                        targetBounds at boundsAnimationDurationMillis
                    }
                }
                Text(
                    "Cupcake", fontSize = 28.sp,
                    modifier = Modifier.sharedBounds(
                        rememberSharedContentState(key = "title"),
                        animatedVisibilityScope = animatedVisibilityScope,
                        boundsTransform = textBoundsTransform
                    )
                )
                // [END android_compose_shared_element_text_bounds_transform]
                Text(
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur sit amet lobortis velit. " +
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                        " Curabitur sagittis, lectus posuere imperdiet facilisis, nibh massa " +
                        "molestie est, quis dapibus orci ligula non magna. Pellentesque rhoncus " +
                        "hendrerit massa quis ultricies. Curabitur congue ullamcorper leo, at maximus",
                    modifier = Modifier.skipToLookaheadSize()
                )
            }
        }
    }
}

private val boundsTransform = BoundsTransform { _: Rect, _: Rect ->
    tween(durationMillis = boundsAnimationDurationMillis, easing = FastOutSlowInEasing)
}
private const val boundsAnimationDurationMillis = 500

@Preview
@Composable
private fun SharedElement_Clipping() {
    var showDetails by remember {
        mutableStateOf(false)
    }
    SharedTransitionLayout {
        AnimatedContent(
            showDetails,
            label = "basic_transition"
        ) { targetState ->
            if (!targetState) {
                Row(
                    modifier = Modifier
                        .sharedBounds(
                            rememberSharedContentState(key = "bounds"),
                            animatedVisibilityScope = this@AnimatedContent
                        )
                        .background(Color.Green.copy(alpha = 0.5f))
                        .padding(8.dp)
                        .clickable {
                            showDetails = true
                        }
                ) {
                    // [START android_compose_animations_shared_element_clipping]
                    Image(
                        painter = painterResource(id = R.drawable.cupcake),
                        contentDescription = "Cupcake",
                        modifier = Modifier
                            .size(100.dp)
                            .sharedElement(
                                rememberSharedContentState(key = "image"),
                                animatedVisibilityScope = this@AnimatedContent
                            )
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    // [END android_compose_animations_shared_element_clipping]
                    Text(
                        "Lorem ipsum dolor sit amet.", fontSize = 21.sp,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "title"),
                            animatedVisibilityScope = this@AnimatedContent,

                        )
                    )
                }
            } else {
                Column(
                    modifier = Modifier
                        .sharedBounds(
                            rememberSharedContentState(key = "bounds"),
                            animatedVisibilityScope = this@AnimatedContent
                        )
                        .background(Color.Green.copy(alpha = 0.7f))
                        .padding(top = 200.dp, start = 16.dp, end = 16.dp)
                        .clickable {
                            showDetails = false
                        }

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cupcake),
                        contentDescription = "Cupcake",
                        modifier = Modifier
                            .size(200.dp)
                            .sharedElement(
                                rememberSharedContentState(key = "image"),
                                animatedVisibilityScope = this@AnimatedContent
                            )
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        "Lorem ipsum dolor sit amet.", fontSize = 21.sp,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "title"),
                            animatedVisibilityScope = this@AnimatedContent
                        )
                    )
                    Text(
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur sit amet lobortis velit. " +
                            "Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                            " Curabitur sagittis, lectus posuere imperdiet facilisis, nibh massa " +
                            "molestie est, quis dapibus orci ligula non magna. Pellentesque rhoncus " +
                            "hendrerit massa quis ultricies. Curabitur congue ullamcorper leo, at maximus"
                    )
                }
            }
        }
    }
}

@Composable
private fun JetsnackBottomBar(modifier: Modifier) {
}

@Composable
private fun EnterExitJetsnack() {
    SharedTransitionLayout {
        AnimatedVisibility(visible = true) {
            // [START android_compose_shared_element_enter_exit]
            JetsnackBottomBar(
                modifier = Modifier
                    .renderInSharedTransitionScopeOverlay(
                        zIndexInOverlay = 1f,
                    )
                    .animateEnterExit(
                        enter = fadeIn() + slideInVertically {
                            it
                        },
                        exit = fadeOut() + slideOutVertically {
                            it
                        }
                    )
            )
            // [END android_compose_shared_element_enter_exit]
        }
    }
}

@Preview
@Composable
private fun SharedElement_SkipLookaheadSize() {
    // Nested shared bounds sample.
    val selectionColor = Color(0xff3367ba)
    var expanded by remember { mutableStateOf(true) }
    SharedTransitionLayout(
        Modifier
            .fillMaxSize()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                expanded = !expanded
            }
            .background(Color(0x88000000))
    ) {
        AnimatedVisibility(
            visible = expanded,
            enter = EnterTransition.None,
            exit = ExitTransition.None
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Surface(
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(20.dp)
                        .sharedBounds(
                            rememberSharedContentState(key = "container"),
                            this@AnimatedVisibility
                        )
                        .requiredHeightIn(max = 60.dp),
                    shape = RoundedCornerShape(50),
                ) {
                    Row(
                        Modifier
                            .padding(10.dp)
                            // By using Modifier.skipToLookaheadSize(), we are telling the layout
                            // system to layout the children of this node as if the animations had
                            // all finished. This avoid re-laying out the Row with animated width,
                            // which is _sometimes_ desirable. Try removing this modifier and
                            // observe the effect.
                            .skipToLookaheadSize()
                    ) {
                        Icon(
                            Icons.Outlined.Share,
                            contentDescription = "Share",
                            modifier = Modifier.padding(
                                top = 10.dp,
                                bottom = 10.dp,
                                start = 10.dp,
                                end = 20.dp
                            )
                        )
                        Icon(
                            Icons.Outlined.Favorite,
                            contentDescription = "Favorite",
                            modifier = Modifier.padding(
                                top = 10.dp,
                                bottom = 10.dp,
                                start = 10.dp,
                                end = 20.dp
                            )
                        )
                        Icon(
                            Icons.Outlined.Create,
                            contentDescription = "Create",
                            tint = Color.White,
                            modifier = Modifier
                                .sharedBounds(
                                    rememberSharedContentState(key = "icon_background"),
                                    this@AnimatedVisibility
                                )
                                .background(selectionColor, RoundedCornerShape(50))
                                .padding(
                                    top = 10.dp,
                                    bottom = 10.dp,
                                    start = 20.dp,
                                    end = 20.dp
                                )
                                .sharedElement(
                                    rememberSharedContentState(key = "icon"),
                                    this@AnimatedVisibility
                                )
                        )
                    }
                }
            }
        }
        AnimatedVisibility(
            visible = !expanded,
            enter = EnterTransition.None,
            exit = ExitTransition.None
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                Surface(
                    Modifier
                        .align(Alignment.BottomEnd)
                        .padding(30.dp)
                        .sharedBounds(
                            rememberSharedContentState(key = "container"),
                            this@AnimatedVisibility,
                            enter = EnterTransition.None,
                        )
                        .sharedBounds(
                            rememberSharedContentState(key = "icon_background"),
                            this@AnimatedVisibility,
                            enter = EnterTransition.None,
                            exit = ExitTransition.None
                        ),
                    shape = RoundedCornerShape(30.dp),
                    color = selectionColor
                ) {
                    Icon(
                        Icons.Outlined.Create,
                        contentDescription = "Create",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(30.dp)
                            .size(40.dp)
                            .sharedElement(
                                rememberSharedContentState(key = "icon"),
                                this@AnimatedVisibility
                            )
                    )
                }
            }
        }
    }
}

private val listSnacks = listOf(
    Snack("Cupcake", "", R.drawable.cupcake),
    Snack("Donut", "", R.drawable.donut),
    Snack("Eclair", "", R.drawable.eclair),
    Snack("Froyo", "", R.drawable.froyo),
    Snack("Gingerbread", "", R.drawable.gingerbread),
    Snack("Honeycomb", "", R.drawable.honeycomb),
)

@Preview
@Composable
fun PlaceholderSizeAnimated_Demo() {
    // This demo shows how other items in a layout can respond to shared elements changing in size.
    // [START android_compose_shared_element_placeholder_size]
    SharedTransitionLayout {

        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {
            composable("home", enterTransition = { fadeIn() }, exitTransition = { fadeOut() }) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        (listSnacks).forEachIndexed { index, snack ->
                            Image(
                                painterResource(id = snack.image),
                                contentDescription = snack.description,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .padding(8.dp)
                                    .sharedBounds(
                                        rememberSharedContentState(key = "image-${snack.name}"),
                                        animatedVisibilityScope = this@composable,
                                        placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                                    )
                                    .clickable {
                                        navController.navigate("details/$index")
                                    }
                                    .height(180.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .aspectRatio(9f / 16f)

                            )
                        }
                    }
                    Text("Nearby snacks")
                    Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        (listSnacks).forEach { snack ->
                            Image(
                                painterResource(id = snack.image),
                                contentDescription = snack.description,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .height(200.dp)
                                    .aspectRatio(16f / 9f)
                                    .padding(8.dp)
                            )
                        }
                    }
                }
            }
            composable(
                "details/{id}",
                arguments = listOf(navArgument("id") { type = NavType.IntType }),
                enterTransition = { fadeIn() }, exitTransition = { fadeOut() }
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("id")
                val snack = listSnacks[id!!]
                Column(
                    Modifier
                        .fillMaxSize()
                        .clickable {
                            navController.navigateUp()
                        }
                ) {
                    Image(
                        painterResource(id = snack.image),
                        contentDescription = snack.description,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .sharedBounds(
                                rememberSharedContentState(key = "image-${snack.name}"),
                                animatedVisibilityScope = this@composable,
                                placeHolderSize = SharedTransitionScope.PlaceHolderSize.animatedSize
                            )
                            .clip(RoundedCornerShape(8.dp))
                            .fillMaxWidth()
                            .aspectRatio(9f / 16f)
                    )
                }
            }
        }
    }
// [END android_compose_shared_element_placeholder_size]
}
