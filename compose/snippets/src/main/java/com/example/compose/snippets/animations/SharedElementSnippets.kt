@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.compose.snippets.animations

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.snippets.R

@Preview
@Composable
private fun BasicExample_SharedElement_No_Shared_Element() {
    // [START android_compose_animations_shared_element_start]
    var showDetails by remember {
        mutableStateOf(false)
    }
    AnimatedContent(
        showDetails,
        label = "basic_transition"
    ) { targetState ->
        if (!targetState) {
            Row(modifier = Modifier
                .background(Color.Green.copy(alpha = 0.5f))
                .padding(8.dp)
                .clickable(remember { MutableInteractionSource() }, indication = null) {
                    showDetails = true
                }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.lizard),
                    contentDescription = "Lizard",
                    modifier = Modifier.size(100.dp),
                    contentScale = ContentScale.Crop
                )
                Text("Lorem ipsum dolor sit amet.", fontSize = 21.sp)
            }
        } else {
            Column(modifier = Modifier
                .background(Color.Green.copy(alpha = 0.7f))
                .padding(top = 200.dp, start = 16.dp, end = 16.dp)
                .clickable(remember { MutableInteractionSource() }, indication = null) {
                    showDetails = false
                }) {
                Image(
                    painter = painterResource(id = R.drawable.lizard),
                    contentDescription = "Lizard",
                    modifier = Modifier.size(200.dp),
                    contentScale = ContentScale.Crop
                )
                Text("Lorem ipsum dolor sit amet.", fontSize = 21.sp)
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
    // [END android_compose_animations_shared_element_start]
}

@Preview
@Composable
private fun BasicExample_SharedElement_Step1() {
    // To make it shared element, we first surround the content either with the SharedTransitionLayout composable,
    // or SharedTransitionScope.
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
                Row {
                    //..
                }
            } else {
                Column {
                    //..
                }
            }
        }
    }

    // [END android_compose_animations_shared_element_step1]
}

@Preview
@Composable
private fun BasicExample_SharedElement_Step2() {
    // To make it shared element, we first surround the content either with the SharedTransitionLayout composable,
    // or SharedTransitionScope, making
    // [START android_compose_animations_shared_element_step2]
    var showDetails by remember {
        mutableStateOf(false)
    }
    SharedTransitionLayout {
        AnimatedContent(
            showDetails,
            label = "basic_transition"
        ) { targetState ->
            if (!targetState) {
                Row(modifier = Modifier
                    .background(Color.Green.copy(alpha = 0.5f))
                    .padding(8.dp)
                    .clickable(remember { MutableInteractionSource() }, indication = null) {
                        showDetails = true
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.lizard),
                        contentDescription = "Lizard",
                        modifier = Modifier
                            .size(100.dp)
                            .sharedElement(
                                rememberSharedContentState(key = "image"),
                                animatedVisibilityScope = this@AnimatedContent
                            ),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        "Lorem ipsum dolor sit amet.", fontSize = 21.sp,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "title"),
                            animatedVisibilityScope = this@AnimatedContent
                        )
                    )
                }
            } else {
                Column(modifier = Modifier
                    .background(Color.Green.copy(alpha = 0.7f))
                    .padding(top = 200.dp, start = 16.dp, end = 16.dp)
                    .clickable(remember { MutableInteractionSource() }, indication = null) {
                        showDetails = false
                    }) {
                    Image(
                        painter = painterResource(id = R.drawable.lizard),
                        contentDescription = "Lizard",
                        modifier = Modifier
                            .size(200.dp)
                            .sharedElement(
                                rememberSharedContentState(key = "image"),
                                animatedVisibilityScope = this@AnimatedContent
                            ),
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
    // [END android_compose_animations_shared_element_step2]
}

@Preview
@Composable
private fun SharedElement_CustomizeBoundsTransform() {
    // To customize shared element transform
    // [START android_compose_animations_shared_element_bounds_transform]
    var showDetails by remember {
        mutableStateOf(false)
    }
    val imageBoundsTransform = BoundsTransform { initial, target ->
        keyframes {
            durationMillis = 500
            initial at 0
            Rect(target.left + 100, target.top, target.right + 100, target.bottom) at 300
        }
    }
    SharedTransitionLayout {
        AnimatedContent(
            showDetails,
            label = "basic_transition"
        ) { targetState ->
            if (!targetState) {
                Row(modifier = Modifier
                    .background(Color.Green.copy(alpha = 0.5f))
                    .padding(8.dp)
                    .clickable(remember { MutableInteractionSource() }, indication = null) {
                        showDetails = true
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.lizard),
                        contentDescription = "Lizard",
                        modifier = Modifier
                            .size(100.dp)
                            .sharedElement(
                                rememberSharedContentState(key = "image"),
                                animatedVisibilityScope = this@AnimatedContent,
                                boundsTransform = { _, _ ->
                                    spring(
                                        stiffness = Spring.StiffnessMediumLow,
                                        dampingRatio = Spring.DampingRatioMediumBouncy
                                    )
                                }
                            ),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        "Lorem ipsum dolor sit amet.", fontSize = 21.sp,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "title"),
                            animatedVisibilityScope = this@AnimatedContent
                        )
                    )
                }
            } else {
                Column(modifier = Modifier
                    .background(Color.Green.copy(alpha = 0.7f))
                    .padding(top = 200.dp, start = 16.dp, end = 16.dp)
                    .clickable(remember { MutableInteractionSource() }, indication = null) {
                        showDetails = false
                    }) {
                    Image(
                        painter = painterResource(id = R.drawable.lizard),
                        contentDescription = "Lizard",
                        modifier = Modifier
                            .size(200.dp)
                            .sharedElement(
                                rememberSharedContentState(key = "image"),
                                animatedVisibilityScope = this@AnimatedContent,
                                boundsTransform = imageBoundsTransform
                            ),
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
    // [END android_compose_animations_shared_element_bounds_transform]
}

@Preview
@Composable
fun SharedElement_SharedBounds() {
    // [START android_compose_animations_shared_element_shared_bounds]
    var showDetails by remember {
        mutableStateOf(false)
    }

    SharedTransitionLayout {
        AnimatedContent(
            showDetails,
            label = "basic_transition"
        ) { targetState ->
            if (!targetState) {
                Row(modifier = Modifier
                    .sharedBounds(rememberSharedContentState(key = "bounds"),
                        animatedVisibilityScope = this@AnimatedContent)
                    .background(Color.Green.copy(alpha = 0.5f))
                    .padding(8.dp)
                    .clickable(remember { MutableInteractionSource() }, indication = null) {
                        showDetails = true
                    }
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.lizard),
                        contentDescription = "Lizard",
                        modifier = Modifier
                            .size(100.dp)
                            .sharedElement(
                                rememberSharedContentState(key = "image"),
                                animatedVisibilityScope = this@AnimatedContent
                            ),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        "Lorem ipsum dolor sit amet.", fontSize = 21.sp,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "title"),
                            animatedVisibilityScope = this@AnimatedContent
                        )
                    )
                }
            } else {
                Column(modifier = Modifier
                    .sharedBounds(rememberSharedContentState(key = "bounds"),
                        animatedVisibilityScope = this@AnimatedContent)
                    .background(Color.Green.copy(alpha = 0.7f))
                    .padding(top = 200.dp, start = 16.dp, end = 16.dp)
                    .clickable(remember { MutableInteractionSource() }, indication = null) {
                        showDetails = false
                    }

                ) {
                    Image(
                        painter = painterResource(id = R.drawable.lizard),
                        contentDescription = "Lizard",
                        modifier = Modifier
                            .size(200.dp)
                            .sharedElement(
                                rememberSharedContentState(key = "image"),
                                animatedVisibilityScope = this@AnimatedContent
                            ),
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
    // [END android_compose_animations_shared_element_shared_bounds]
}