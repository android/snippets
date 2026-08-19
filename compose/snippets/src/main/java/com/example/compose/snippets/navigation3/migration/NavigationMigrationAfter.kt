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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.result.LocalResultEventBus
import androidx.navigation3.runtime.result.ResultEffect
import androidx.navigation3.scene.DialogSceneStrategy
import androidx.navigation3.ui.NavDisplay
import kotlinx.coroutines.flow.StateFlow
import kotlinx.serialization.Serializable

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

private object SnippetResultAfter {
    @Serializable data object ContactPickerRoute : NavKey
    data class Contact(val name: String = "")
    class ComposeMessageViewModel : ViewModel() {
        val recipient: Contact? = null
        fun onRecipientSelected(contact: Contact) {}
    }

    @Composable private fun ContactPickerScreen(onContactSelected: (Contact) -> Unit) {}
    @Composable private fun ComposeMessageContent(recipient: Contact?) {}

    // [START android_compose_navigation3_result_after]
    // [START_EXCLUDE]
    fun EntryProviderScope<NavKey>.entryProviderResult(navigator: Navigator) {
    // [END_EXCLUDE]
        // Sender destination (in entryProvider):
        entry<ContactPickerRoute> {
            val resultBus = LocalResultEventBus.current

            ContactPickerScreen(
                onContactSelected = { contact ->
                    resultBus.sendResult<Contact>(result = contact)
                    navigator.goBack()
                }
            )
        }
    // [START_EXCLUDE]
    }
    // [END_EXCLUDE]

    // Receiver destination:
    @Composable
    fun ComposeMessageScreen(viewModel: ComposeMessageViewModel = viewModel()) {
        ResultEffect<Contact> { contact ->
            viewModel.onRecipientSelected(contact)
        }

        ComposeMessageContent(recipient = viewModel.recipient)
    }
    // [END android_compose_navigation3_result_after]
}
