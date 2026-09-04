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
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.mutableStateOf
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.navigation3.runtime.deeplink.BackStackMatchResult
import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.invoke
import kotlinx.serialization.Serializable
import kotlinx.serialization.serializer

// Dummy screens for compilation
@Composable private fun ScreenA(title: String) {}
@Composable private fun ScreenB() {}
@Composable private fun ScreenD() {}

private object Snippet4 {
    // [START android_compose_navigation3_route_a_after]
    @Serializable data object RouteA : NavKey
    // [END android_compose_navigation3_route_a_after]
}

private object Snippet7 {
    @Serializable data object RouteA : NavKey

    @Composable
    fun SetupSnippet() {
        // [START android_compose_navigation3_remember_navigation_state]
        val navigationState = rememberNavigationState(
            // [START_EXCLUDE]
            startRoute = RouteA,
            topLevelRoutes = setOf(RouteA)
            /*
            // [END_EXCLUDE]
            startRoute = <Insert your starting route>,
            topLevelRoutes = <Insert your set of top level routes>
            // [START_EXCLUDE]
            */
            // [END_EXCLUDE]
        )

        val navigator = remember { Navigator(navigationState) }
        // [END android_compose_navigation3_remember_navigation_state]
    }
}

private object Snippet9 {
    @Serializable data object RouteA : NavKey

    fun IsSelectedAfterSnippet(navigationState: NavigationState) {
        val key = RouteA
        // [START android_compose_navigation3_is_selected_after]
        val isSelected = key == navigationState.topLevelRoute
        // [END android_compose_navigation3_is_selected_after]
    }
}

private object Snippet10 {
    fun EntryProviderSnippet() {
        // [START android_compose_navigation3_entry_provider]
        val entryProvider = entryProvider<NavKey> {

        }
        // [END android_compose_navigation3_entry_provider]
    }
}

private object Snippet12 {
    // [START android_compose_navigation3_nav_host_after]
    // [START_EXCLUDE]
    /*
    // [END_EXCLUDE]
    import androidx.navigation3.runtime.EntryProviderScope
    import androidx.navigation3.runtime.NavKey
    import androidx.navigation3.runtime.entryProvider
    import androidx.navigation3.scene.DialogSceneStrategy
    // [START_EXCLUDE]
    */
    // [END_EXCLUDE]

    @Serializable data class RouteA(val id: String) : NavKey
    @Serializable data object RouteB : NavKey
    @Serializable data object RouteD : NavKey

    val entryProvider = entryProvider {
        entry<RouteA>{ key -> ScreenA(title = "Screen has ID: ${key.id}") }
        featureBSection()
        entry<RouteD>(metadata = DialogSceneStrategy.dialog()){ ScreenD() }
    }

    fun EntryProviderScope<NavKey>.featureBSection() {
        entry<RouteB> { ScreenB() }
    }
    // [END android_compose_navigation3_nav_host_after]
}

private object Snippet13 {
    @Serializable data object RouteA : NavKey
    val entryProvider = entryProvider<NavKey> { }
    val navigationState = NavigationState(RouteA, mutableStateOf(RouteA), emptyMap())
    val navigator = Navigator(navigationState)

    @Composable
    fun NavDisplaySnippet() {
        // [START android_compose_navigation3_nav_display]
        NavDisplay(
            entries = navigationState.toEntries(entryProvider),
            onBack = { navigator.goBack() },
            sceneStrategies = remember { listOf(DialogSceneStrategy()) }
        )
        // [END android_compose_navigation3_nav_display]
    }
}

private object SnippetLifecycleAfter {
    @Composable
    fun LifecycleSnippet(flow: StateFlow<String>) {
        // [START android_compose_navigation3_lifecycle_after]
        // Inside the destination composable
        val state by flow.collectAsStateWithLifecycle()
        // [END android_compose_navigation3_lifecycle_after]
    }
}

private object SnippetDeepLinksAfter {
    @Serializable data class RouteA(val id: String) : NavKey
    @Serializable data object HomeKey : NavKey

    // [START android_compose_navigation3_deeplinks_after_matcher]
    val userMatcher = UriDeepLinkMatcher(
        DeepLinkUri("www.example.com/user/{id}"),
        serializer<RouteA>()
    )
    // [END android_compose_navigation3_deeplinks_after_matcher]

    // [START android_compose_navigation3_deeplinks_after_activity]
    val deepLinkMatchers: List<DeepLinkMatcher<*, *>> = listOf(
        userMatcher,
    )

    class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            val request = DeepLinkRequest(intent = intent)
            val matchResult = deepLinkMatchers
                .mapNotNull { it.match(request) }
                .maxOrNull()

            val backStack = when (matchResult) {
                null -> listOf(HomeKey)
                is BackStackMatchResult<*, *> -> {
                    @Suppress("UNCHECKED_CAST")
                    matchResult.backStack as List<NavKey>
                }
                else -> listOf(matchResult.key)
            }

            // Use backStack with NavDisplay
        }
    }
    // [END android_compose_navigation3_deeplinks_after_activity]
}

