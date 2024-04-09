@file:OptIn(ExperimentalSharedTransitionApi::class)

package com.example.compose.snippets.animations

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.BoundsTransform
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.compose.snippets.R
import kotlinx.coroutines.delay

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
                    .sharedBounds(
                        rememberSharedContentState(key = "bounds"),
                        animatedVisibilityScope = this@AnimatedContent
                    )
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
                    .sharedBounds(
                        rememberSharedContentState(key = "bounds"),
                        animatedVisibilityScope = this@AnimatedContent
                    )
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


@Preview
@Composable
private fun SharedElement_Clipping() {
    // [START android_compose_animations_shared_element_clipping]
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
                    .sharedBounds(
                        rememberSharedContentState(key = "bounds"),
                        animatedVisibilityScope = this@AnimatedContent
                    )
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
                            )
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    Text(
                        "Lorem ipsum dolor sit amet.", fontSize = 21.sp,
                        modifier = Modifier.sharedElement(
                            rememberSharedContentState(key = "title"),
                            animatedVisibilityScope = this@AnimatedContent,

                            )
                    )
                }
            } else {
                Column(modifier = Modifier
                    .sharedBounds(
                        rememberSharedContentState(key = "bounds"),
                        animatedVisibilityScope = this@AnimatedContent
                    )
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
    // [END android_compose_animations_shared_element_clipping]
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
                    !selectFirst,
                    boundsTransform = boundsTransform
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
                    selectFirst,
                    boundsTransform = boundsTransform
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

private val boundsTransform = BoundsTransform { initial, target ->
    // Move vertically first then horizontally
    keyframes {
        durationMillis = 500
        initial at 0
        Rect(initial.left, target.top, initial.left + target.width, target.bottom) at 300
    }
}


@Preview
@Composable
fun SharedElementWithFABInOverlaySample() {
    // [START android_compose_shared_element_render_in_shared_scope_overlay]
    // Create an Image that will be shared between the two shared elements.
    @Composable
    fun Lizard(modifier: Modifier = Modifier) {
        Image(
            painterResource(id = R.drawable.lizard),
            contentDescription = "cute lizard",
            contentScale = ContentScale.FillHeight,
            modifier = modifier
                .clip(shape = RoundedCornerShape(10))
        )
    }

    var showThumbnail by remember {
        mutableStateOf(true)
    }
    SharedTransitionLayout(
        Modifier
            .clickable { showThumbnail = !showThumbnail }
            .fillMaxSize()
            .padding(10.dp)) {
        Column(Modifier.padding(10.dp)) {
            // Create an AnimatedVisibility for the shared element, so that the layout siblings
            // (i.e. the two boxes below) will move in to fill the space during the exit transition.
            AnimatedVisibility(visible = showThumbnail) {
                Lizard(
                    Modifier
                        .size(100.dp)
                        // Create a shared element, using string as the key
                        .sharedElement(
                            rememberSharedContentState(key = "lizard"),
                            this@AnimatedVisibility,
                        )
                )
            }
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xffffcc5c), RoundedCornerShape(5.dp))
            )
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(Color(0xff2a9d84), RoundedCornerShape(5.dp))
            )
        }
        Box(modifier = Modifier.fillMaxSize()) {
            AnimatedVisibility(!showThumbnail) {
                Lizard(
                    Modifier
                        .fillMaxSize()
                        // Create another shared element, and make sure the string key matches
                        // the other shared element.
                        .sharedElement(
                            rememberSharedContentState(key = "lizard"),
                            this@AnimatedVisibility,
                        )
                )
            }
            FloatingActionButton(
                modifier = Modifier
                    .padding(20.dp)
                    .align(Alignment.BottomEnd)
                    // During shared element transition, shared elements will be rendered in
                    // overlay to escape any clipping or layer transform from parents. It also
                    // means they will render over on top of UI elements such as Floating Action
                    // Button. Once the transition is finished, they will be dropped from the
                    // overlay to their own DrawScopes. To help support keeping specific UI
                    // elements always on top, Modifier.renderInSharedTransitionScopeOverlay
                    // will temporarily elevate them into the overlay as well. By default,
                    // this modifier keeps content in overlay during the time when the
                    // shared transition is active (i.e. SharedTransitionScope#isTransitionActive).
                    // The duration can be customize via `renderInOverlay` parameter.
                    .renderInSharedTransitionScopeOverlay(
                        // zIndexInOverlay by default is 0f for this modifier and for shared
                        // elements. By overwriting zIndexInOverlay to 1f, we can ensure this
                        // FAB is rendered on top of the shared elements.
                        zIndexInOverlay = 1f
                    ),
                onClick = {}
            ) {
                Icon(Icons.Default.Favorite, contentDescription = "favorite")
            }
        }
    }
    // [END android_compose_shared_element_render_in_shared_scope_overlay]
}


@Preview
@Composable
fun SharedElement_PredictiveBack() {
    // [START android_compose_shared_element_predictive_back]
    SharedTransitionLayout {
        val listAnimals = remember {
            listOf(
                Animal("Lion", "", R.drawable.lion),
                Animal("Lizard", "", R.drawable.lizard),
                Animal("Elephant", "", R.drawable.elephant),
                Animal("Penguin", "", R.drawable.penguin)
            )
        }
        val boundsTransform = { _: Rect, _: Rect -> tween<Rect>(1400) }

        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "home"
        ) {

            composable("home") {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    itemsIndexed(listAnimals) { index, item ->
                        Row(Modifier.clickable {
                            navController.navigate("details/$index")
                        }) {
                            Spacer(modifier = Modifier.width(8.dp))
                            Image(
                                painterResource(id = item.image),
                                contentDescription = item.description,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .size(100.dp)
                                    .sharedElement(
                                        rememberSharedContentState(key = "image-$index"),
                                        animatedVisibilityScope = this@composable,
                                        boundsTransform = boundsTransform
                                    )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                item.name, fontSize = 18.sp,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                                    .sharedElement(
                                        rememberSharedContentState(key = "text-$index"),
                                        animatedVisibilityScope = this@composable,
                                        boundsTransform = boundsTransform
                                    )
                            )
                        }
                    }

                }
            }
            composable(
                "details/{animal}",
                arguments = listOf(navArgument("animal") { type = NavType.IntType })
            )
            { backStackEntry ->
                val animalId = backStackEntry.arguments?.getInt("animal")
                val animal = listAnimals[animalId!!]
                Column(
                    Modifier
                        .fillMaxSize()
                        .clickable {
                            navController.navigate("home")
                        }) {
                    Image(
                        painterResource(id = animal.image),
                        contentDescription = animal.description,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .aspectRatio(1f)
                            .fillMaxWidth()
                            .sharedElement(
                                rememberSharedContentState(key = "image-$animalId"),
                                animatedVisibilityScope = this@composable,
                                boundsTransform = boundsTransform
                            )
                    )
                    Text(
                        animal.name, fontSize = 18.sp, modifier =
                        Modifier
                            .fillMaxWidth()
                            .sharedElement(
                                rememberSharedContentState(key = "text-$animalId"),
                                animatedVisibilityScope = this@composable,
                                boundsTransform = boundsTransform
                            )
                    )
                }
            }
        }
    }
// [END android_compose_shared_element_predictive_back]
}

data class Animal(
    val name: String,
    val description: String,
    @DrawableRes val image: Int
)


@Preview
@Composable
private fun SkipToLookahead() {
// Nested shared bounds sample.
    val selectionColor = Color(0xff3367ba)
    var expanded by remember { mutableStateOf(true) }
    SharedTransitionLayout(
        Modifier
            .fillMaxSize()
            .clickable {
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

@Preview
@Composable
private fun SharedAsyncImage() {
    SharedTransitionLayout {
        AnimatedVisibility(visible = true) {
            // [START android_compose_shared_element_async_image_tip]
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data("your-image-url")
                    .crossfade(true)
                    .build(),
                placeholder = null,
                contentDescription = null,
                modifier = Modifier
                    .size(120.dp)
                    .sharedBounds(
                        rememberSharedContentState(
                            key = "image-key"
                        ),
                        animatedVisibilityScope = this,
                        exit = ExitTransition.None
                    )
            )
            // [END android_compose_shared_element_async_image_tip]
        }
    }
}

@Composable
fun debugPlaceholder(@DrawableRes debugPreview: Int) =
    if (LocalInspectionMode.current) {
        painterResource(id = debugPreview)
    } else {
        null
    }

@Preview
@Composable
private fun SharedElementTypicalUseText() {
    SharedTransitionLayout {
        AnimatedVisibility(visible = true) {
            // [START android_compose_shared_element_text_tip]
            Text(
                text = "This is an example of how to share text",
                modifier = Modifier
                    .wrapContentWidth()
                    .sharedBounds(
                        rememberSharedContentState(
                            key = "shared Text"
                        ),
                        animatedVisibilityScope = this,
                        enter = fadeIn() + scaleInSharedContentToBounds(),
                        exit = fadeOut() + scaleOutSharedContentToBounds()
                    )
            )
            // [END android_compose_shared_element_text_tip]
        }
    }

}