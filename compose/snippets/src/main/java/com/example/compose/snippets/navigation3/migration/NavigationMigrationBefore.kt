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

package com.example.compose.snippets.navigation3.migration

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import kotlinx.coroutines.flow.StateFlow
import androidx.navigation.NavHostController
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.dialog
import androidx.navigation.compose.navigation
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import kotlinx.serialization.Serializable
import kotlin.reflect.KClass

// Dummy screens for compilation
@Composable private fun ScreenA(title: String) {}
@Composable private fun ScreenB() {}
@Composable private fun ScreenD() {}

private object Snippet3 {
    // [START android_compose_navigation3_route_a_before]
    @Serializable data object RouteA
    // [END android_compose_navigation3_route_a_before]
}

private object Snippet8 {
    @Serializable data object RouteA

    // [START android_compose_navigation3_is_selected_before]
    // [START_EXCLUDE]
    @Composable
    fun Dummy(navController: NavController) {
        val key = RouteA
    // [END_EXCLUDE]
    val isSelected = navController.currentBackStackEntryAsState().value?.destination.isRouteInHierarchy(key::class)
    // [START_EXCLUDE]
    }
    // [END_EXCLUDE]

    fun NavDestination?.isRouteInHierarchy(route: KClass<*>) =
        this?.hierarchy?.any {
            it.hasRoute(route)
        } ?: false
    // [END android_compose_navigation3_is_selected_before]
}

private object Snippet11 {
    // [START android_compose_navigation3_nav_host_before]
    // [START_EXCLUDE]
    /*
    // [END_EXCLUDE]
    import androidx.navigation.NavDestination
    import androidx.navigation.NavDestination.Companion.hasRoute
    import androidx.navigation.NavDestination.Companion.hierarchy
    import androidx.navigation.NavGraphBuilder
    import androidx.navigation.compose.NavHost
    import androidx.navigation.compose.composable
    import androidx.navigation.compose.currentBackStackEntryAsState
    import androidx.navigation.compose.dialog
    import androidx.navigation.compose.navigation
    import androidx.navigation.compose.rememberNavController
    import androidx.navigation.navOptions
    import androidx.navigation.toRoute
    // [START_EXCLUDE]
    */
    // [END_EXCLUDE]

    @Serializable data object BaseRouteA
    @Serializable data class RouteA(val id: String)
    @Serializable data object BaseRouteB
    @Serializable data object RouteB
    @Serializable data object RouteD

    @Composable
    fun NavHostSnippet(navController: NavHostController) {
        NavHost(navController = navController, startDestination = BaseRouteA){
            composable<RouteA>{ entry ->
                val id = entry.toRoute<RouteA>().id
                ScreenA(title = "Screen has ID: $id")
            }
            featureBSection()
            dialog<RouteD>{ ScreenD() }
        }
    }

    fun NavGraphBuilder.featureBSection() {
        navigation<BaseRouteB>(startDestination = RouteB) {
            composable<RouteB> { ScreenB() }
        }
    }
    // [END android_compose_navigation3_nav_host_before]
}

private object SnippetLifecycleBefore {
    @Composable
    fun LifecycleSnippet(navController: NavController, flow: StateFlow<String>) {
        // [START android_compose_navigation3_lifecycle_before]
        // In your destination screen or host
        val lifecycleOwner = navController.currentBackStackEntry!!
        val state by flow.collectAsStateWithLifecycle(lifecycleOwner = lifecycleOwner)
        // [END android_compose_navigation3_lifecycle_before]
    }
}

private object SnippetDeepLinksBefore {
    @Serializable data class RouteA(val id: String)

    fun NavGraphBuilder.deepLinksBefore() {
        // [START android_compose_navigation3_deeplinks_before]
        composable<RouteA>(
            deepLinks = listOf(
                navDeepLink { uriPattern = "example.com/user/{id}" }
            )
        ) {
            // ...
        }
        // [END android_compose_navigation3_deeplinks_before]
    }
}

