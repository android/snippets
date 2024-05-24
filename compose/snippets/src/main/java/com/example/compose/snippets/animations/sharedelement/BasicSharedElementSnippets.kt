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
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.snippets.R
import com.example.compose.snippets.ui.theme.LavenderLight
import com.example.compose.snippets.ui.theme.RoseLight

private class SharedElementBasicUsage2 {
    @Preview
    @Composable
    private fun SharedElementApp() {
        // [START android_compose_animations_shared_element_step1]
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
        // [END android_compose_animations_shared_element_step1]
    }

    @Composable
    private fun MainContent(
        onShowDetails: () -> Unit,
        sharedTransitionScope: SharedTransitionScope,
        animatedVisibilityScope: AnimatedVisibilityScope
    ) {
        Row(
            // [START_EXCLUDE]
            modifier = Modifier
                .padding(8.dp)
                .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .background(LavenderLight, RoundedCornerShape(8.dp))
                .clickable {
                    onShowDetails()
                }
                .padding(8.dp)
            // [END_EXCLUDE]
        ) {
            Image(
                painter = painterResource(id = R.drawable.cupcake),
                contentDescription = "Cupcake",
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            // [START_EXCLUDE]
            Text("Cupcake", fontSize = 21.sp)
            // [END_EXCLUDE]
        }
    }

    @Composable
    private fun DetailsContent(
        modifier: Modifier = Modifier,
        onBack: () -> Unit,
        sharedTransitionScope: SharedTransitionScope,
        animatedVisibilityScope: AnimatedVisibilityScope
    ) {
        Column(
            // [START_EXCLUDE]
            modifier = Modifier
                .padding(top = 200.dp, start = 16.dp, end = 16.dp)
                .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .background(RoseLight, RoundedCornerShape(8.dp))
                .clickable {
                    onBack()
                }
                .padding(8.dp)
            // [END_EXCLUDE]
        ) {
            Image(
                painter = painterResource(id = R.drawable.cupcake),
                contentDescription = "Cupcake",
                modifier = Modifier
                    .size(200.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            // [START_EXCLUDE]
            Text("Cupcake", fontSize = 28.sp)
            Text(
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur sit amet lobortis velit. " +
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                    " Curabitur sagittis, lectus posuere imperdiet facilisis, nibh massa " +
                    "molestie est, quis dapibus orci ligula non magna. Pellentesque rhoncus " +
                    "hendrerit massa quis ultricies. Curabitur congue ullamcorper leo, at maximus"
            )
            // [END_EXCLUDE]
        }
    }
}

private class SharedElementBasicUsage3 {

    @Preview
    @Composable
    private fun SharedElementApp() {
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

    // [START android_compose_animations_shared_element_step2]
    @Composable
    private fun MainContent(
        onShowDetails: () -> Unit,
        modifier: Modifier = Modifier,
        sharedTransitionScope: SharedTransitionScope,
        animatedVisibilityScope: AnimatedVisibilityScope
    ) {
        Row(
            // [START_EXCLUDE]
            modifier = Modifier
                .padding(8.dp)
                .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .background(LavenderLight, RoundedCornerShape(8.dp))
                .clickable {
                    onShowDetails()
                }
                .padding(8.dp)
            // [END_EXCLUDE]
        ) {
            with(sharedTransitionScope) {
                Image(
                    painter = painterResource(id = R.drawable.cupcake),
                    contentDescription = "Cupcake",
                    modifier = Modifier
                        .sharedElement(
                            rememberSharedContentState(key = "image"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        .size(100.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                // [START_EXCLUDE]
                Text(
                    "Cupcake", fontSize = 21.sp,
                    modifier = Modifier.sharedBounds(
                        rememberSharedContentState(key = "title"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                )
                // [END_EXCLUDE]
            }
        }
    }

    @Composable
    private fun DetailsContent(
        modifier: Modifier = Modifier,
        onBack: () -> Unit,
        sharedTransitionScope: SharedTransitionScope,
        animatedVisibilityScope: AnimatedVisibilityScope
    ) {
        Column(
            // [START_EXCLUDE]
            modifier = Modifier
                .padding(top = 200.dp, start = 16.dp, end = 16.dp)
                .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                .background(RoseLight, RoundedCornerShape(8.dp))
                .clickable {
                    onBack()
                }
                .padding(8.dp)
            // [END_EXCLUDE]
        ) {
            with(sharedTransitionScope) {
                Image(
                    painter = painterResource(id = R.drawable.cupcake),
                    contentDescription = "Cupcake",
                    modifier = Modifier
                        .sharedElement(
                            rememberSharedContentState(key = "image"),
                            animatedVisibilityScope = animatedVisibilityScope
                        )
                        .size(200.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
                // [START_EXCLUDE]
                Text(
                    "Cupcake", fontSize = 28.sp,
                    modifier = Modifier.sharedBounds(
                        rememberSharedContentState(key = "title"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                )
                Text(
                    "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Curabitur sit amet lobortis velit. " +
                        "Lorem ipsum dolor sit amet, consectetur adipiscing elit." +
                        " Curabitur sagittis, lectus posuere imperdiet facilisis, nibh massa " +
                        "molestie est, quis dapibus orci ligula non magna. Pellentesque rhoncus " +
                        "hendrerit massa quis ultricies. Curabitur congue ullamcorper leo, at maximus"
                )
                // [END_EXCLUDE]
            }
        }
    }
    // [END android_compose_animations_shared_element_step2]
}

@Preview
@Composable
private fun SharedElement_ManualVisibleControl() {
    // [START android_compose_shared_element_manual_control]
    var selectFirst by remember { mutableStateOf(true) }
    val key = remember { Any() }
    SharedTransitionLayout(
        Modifier
            .fillMaxSize()
            .padding(10.dp)
            .clickable {
                selectFirst = !selectFirst
            }
    ) {
        Box(
            Modifier
                .sharedElementWithCallerManagedVisibility(
                    rememberSharedContentState(key = key),
                    !selectFirst
                )
                .background(Color.Red)
                .size(100.dp)
        ) {
            Text(if (!selectFirst) "false" else "true", color = Color.White)
        }
        Box(
            Modifier
                .offset(180.dp, 180.dp)
                .sharedElementWithCallerManagedVisibility(
                    rememberSharedContentState(
                        key = key,
                    ),
                    selectFirst
                )
                .alpha(0.5f)
                .background(Color.Blue)
                .size(180.dp)
        ) {
            Text(if (selectFirst) "false" else "true", color = Color.White)
        }
    }
    // [END android_compose_shared_element_manual_control]
}

@Preview
@Composable
private fun UnmatchedBoundsExample() {
    // [START android_compose_animation_shared_element_bounds_unmatched]
    var selectFirst by remember { mutableStateOf(true) }
    val key = remember { Any() }
    SharedTransitionLayout(
        Modifier
            .fillMaxSize()
            .padding(10.dp)
            .clickable {
                selectFirst = !selectFirst
            }
    ) {
        AnimatedContent(targetState = selectFirst, label = "AnimatedContent") { targetState ->
            if (targetState) {
                Box(
                    Modifier
                        .padding(12.dp)
                        .sharedBounds(
                            rememberSharedContentState(key = key),
                            animatedVisibilityScope = this@AnimatedContent
                        )
                        .border(2.dp, Color.Red)
                ) {
                    Text(
                        "Hello",
                        fontSize = 20.sp
                    )
                }
            } else {
                Box(
                    Modifier
                        .offset(180.dp, 180.dp)
                        .sharedBounds(
                            rememberSharedContentState(
                                key = key,
                            ),
                            animatedVisibilityScope = this@AnimatedContent
                        )
                        .border(2.dp, Color.Red)
                        // This padding is placed after sharedBounds, but it doesn't match the
                        // other shared elements modifier order, resulting in visual jumps
                        .padding(12.dp)

                ) {
                    Text(
                        "Hello",
                        fontSize = 36.sp
                    )
                }
            }
        }
    }
    // [END android_compose_animation_shared_element_bounds_unmatched]
}

private object UniqueKeySnippet {
    // [START android_compose_shared_elements_unique_key]
    data class SnackSharedElementKey(
        val snackId: Long,
        val origin: String,
        val type: SnackSharedElementType
    )

    enum class SnackSharedElementType {
        Bounds,
        Image,
        Title,
        Tagline,
        Background
    }

    @Composable
    fun SharedElementUniqueKey() {
        // [START_EXCLUDE]
        SharedTransitionLayout {
            AnimatedVisibility(visible = true) {
                // [END_EXCLUDE]
                Box(
                    modifier = Modifier
                        .sharedElement(
                            rememberSharedContentState(
                                key = SnackSharedElementKey(
                                    snackId = 1,
                                    origin = "latest",
                                    type = SnackSharedElementType.Image
                                )
                            ),
                            animatedVisibilityScope = this@AnimatedVisibility
                        )
                )
                // [START_EXCLUDE]
            }
        }
        // [END_EXCLUDE]
    }
    // [END android_compose_shared_elements_unique_key]
}

// [START android_compose_shared_element_scope]
val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

@Composable
private fun SharedElementScope_CompositionLocal() {
    // An example of how to use composition locals to pass around the shared transition scope, far down your UI tree.
    // [START_EXCLUDE]
    val state = remember {
        mutableStateOf(false)
    }
    // [END_EXCLUDE]
    SharedTransitionLayout {
        CompositionLocalProvider(
            LocalSharedTransitionScope provides this
        ) {
            // This could also be your top-level NavHost as this provides an AnimatedContentScope
            AnimatedContent(state, label = "Top level AnimatedContent") { targetState ->
                CompositionLocalProvider(LocalNavAnimatedVisibilityScope provides this) {
                    // Now we can access the scopes in any nested composables as follows:
                    val sharedTransitionScope = LocalSharedTransitionScope.current
                        ?: throw IllegalStateException("No SharedElementScope found")
                    val animatedVisibilityScope = LocalNavAnimatedVisibilityScope.current
                        ?: throw IllegalStateException("No AnimatedVisibility found")
                }
                // [START_EXCLUDE]
                if (targetState.value) {
                    // do something
                }
                // [END_EXCLUDE]
            }
        }
    }
}
// [END android_compose_shared_element_scope]

private object SharedElementScope_Extensions {
    // [START android_compose_shared_element_parameters]
    @Composable
    fun MainContent(
        animatedVisibilityScope: AnimatedVisibilityScope,
        sharedTransitionScope: SharedTransitionScope
    ) {
    }

    @Composable
    fun Details(
        animatedVisibilityScope: AnimatedVisibilityScope,
        sharedTransitionScope: SharedTransitionScope
    ) {
    }
    // [END android_compose_shared_element_parameters]
}
