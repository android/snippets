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

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.LocalNavAnimatedContentScope
import androidx.navigation3.ui.NavDisplay
import com.example.compose.snippets.R
import kotlinx.serialization.Serializable

data class Snack(
    val name: String,
    val description: String,
    @DrawableRes val image: Int
)

private val listSnacks = listOf(
    Snack("Cupcake", "", R.drawable.cupcake),
    Snack("Donut", "", R.drawable.donut),
    Snack("Eclair", "", R.drawable.eclair),
    Snack("Froyo", "", R.drawable.froyo),
    Snack("Gingerbread", "", R.drawable.gingerbread),
    Snack("Honeycomb", "", R.drawable.honeycomb),
)

// [START android_compose_shared_element_navigation_screens]
@Composable
fun DetailsScreen(
    id: Int,
    snack: Snack,
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onBackPressed: () -> Unit
) {
    with(sharedTransitionScope) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onBackPressed() },
        ) {
            Image(
                painterResource(id = snack.image),
                contentDescription = snack.description,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .sharedElement(
                        sharedTransitionScope.rememberSharedContentState(key = "image-$id"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .aspectRatio(1f)
                    .fillMaxWidth()
            )
            Text(
                text = snack.name,
                fontSize = 18.sp,
                modifier = Modifier
                    .sharedElement(
                        sharedTransitionScope.rememberSharedContentState(key = "text-$id"),
                        animatedVisibilityScope = animatedVisibilityScope
                    )
                    .fillMaxWidth(),
            )
        }
    }
}

@Composable
fun HomeScreen(
    sharedTransitionScope: SharedTransitionScope,
    animatedVisibilityScope: AnimatedVisibilityScope,
    onItemClick: (Int) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        itemsIndexed(listSnacks) { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onItemClick(index) },
            ) {
                Spacer(modifier = Modifier.width(8.dp))
                with(sharedTransitionScope) {
                    Image(
                        painterResource(id = item.image),
                        contentDescription = item.description,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .sharedElement(
                                sharedTransitionScope.rememberSharedContentState(key = "image-$index"),
                                animatedVisibilityScope = animatedVisibilityScope
                            )
                            .size(100.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        item.name,
                        fontSize = 18.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .sharedElement(
                                sharedTransitionScope.rememberSharedContentState(key = "text-$index"),
                                animatedVisibilityScope = animatedVisibilityScope,
                            )
                    )
                }
            }
        }
    }
}
// [END android_compose_shared_element_navigation_screens]


@Preview
// [START android_compose_shared_element_navigation_2]
@Composable
fun SharedElement_Nav2() {
    SharedTransitionLayout {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.safeDrawingPadding()
        ) {
            composable("home") {
                HomeScreen(
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable,
                    onItemClick = { navController.navigate("details/$it") })
            }
            composable(
                "details/{item}", arguments = listOf(navArgument("item") { type = NavType.IntType })
            ) { backStackEntry ->
                val id = backStackEntry.arguments?.getInt("item") ?: 0
                val snack = listSnacks[id]
                DetailsScreen(
                    id = id,
                    snack = snack,
                    sharedTransitionScope = this@SharedTransitionLayout,
                    animatedVisibilityScope = this@composable,
                    onBackPressed = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
// [END android_compose_shared_element_navigation_2]

@Serializable
object HomeRoute : NavKey

@Serializable
data class DetailsRoute(val item: Int) : NavKey

@Preview
// [START android_compose_shared_element_navigation_3]
@Composable
fun SharedElement_Nav3() {
    SharedTransitionLayout {
        val backStack = rememberNavBackStack(HomeRoute)

        // Note: NavDisplay accepts a `sharedTransitionScope` parameter, which is used to animate
        // NavEntry instances between scenes. This parameter *isn't* required for shared element
        // or shared bounds transitioning elements between different NavEntry, as demonstrated in
        // this sample.
        // See https://developer.android.com/guide/navigation/navigation-3/animate-destinations#transition-nav-entries
        NavDisplay(
            modifier = Modifier.safeDrawingPadding(),
            backStack = backStack,
            entryProvider = entryProvider {
                entry<HomeRoute> {
                    HomeScreen(
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                        onItemClick = { backStack.add(DetailsRoute(it)) })
                }
                entry<DetailsRoute> { detailsRoute ->
                    val id = detailsRoute.item
                    val snack = listSnacks[id]

                    DetailsScreen(
                        id = id,
                        snack = snack,
                        sharedTransitionScope = this@SharedTransitionLayout,
                        animatedVisibilityScope = LocalNavAnimatedContentScope.current,
                        onBackPressed = {
                            backStack.removeLastOrNull()
                        },
                    )
                }
            })
    }
}
// [END android_compose_shared_element_navigation_3]
