/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.compose.snippets.navigation3.scenes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope
import androidx.navigation3.ui.NavDisplay
import androidx.window.core.layout.WindowSizeClass
import androidx.window.core.layout.WindowSizeClass.Companion.WIDTH_DP_MEDIUM_LOWER_BOUND
import com.example.compose.snippets.touchinput.Button
import kotlinx.serialization.Serializable

interface SceneExample<T : Any> {

    // [START android_compose_navigation3_scenes_1]
    @Composable
    public fun calculateScene(
        entries: List<NavEntry<T>>,
        onBack: (count: Int) -> Unit,
    ): Scene<T>?
    // [END android_compose_navigation3_scenes_1]
}

// [START android_compose_navigation3_scenes_2]
data class SinglePaneScene<T : Any>(
    override val key: Any,
    val entry: NavEntry<T>,
    override val previousEntries: List<NavEntry<T>>,
) : Scene<T> {
    override val entries: List<NavEntry<T>> = listOf(entry)
    override val content: @Composable () -> Unit = { entry.Content() }
}

/**
 * A [SceneStrategy] that always creates a 1-entry [Scene] simply displaying the last entry in the
 * list.
 */
public class SinglePaneSceneStrategy<T : Any> : SceneStrategy<T> {
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? =
        SinglePaneScene(
            key = entries.last().contentKey,
            entry = entries.last(),
            previousEntries = entries.dropLast(1)
        )
}
// [END android_compose_navigation3_scenes_2]

// [START android_compose_navigation3_scenes_3]
// --- ListDetailScene ---
/**
 * A [Scene] that displays a list and a detail [NavEntry] side-by-side in a 40/60 split.
 *
 */
class ListDetailScene<T : Any>(
    override val key: Any,
    override val previousEntries: List<NavEntry<T>>,
    val listEntry: NavEntry<T>,
    val detailEntry: NavEntry<T>,
) : Scene<T> {
    override val entries: List<NavEntry<T>> = listOf(listEntry, detailEntry)
    override val content: @Composable (() -> Unit) = {
        Row(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.weight(0.4f)) {
                listEntry.Content()
            }
            Column(modifier = Modifier.weight(0.6f)) {
                detailEntry.Content()
            }
        }
    }
}

@Composable
fun <T : Any> rememberListDetailSceneStrategy(): ListDetailSceneStrategy<T> {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass

    return remember(windowSizeClass) {
        ListDetailSceneStrategy(windowSizeClass)
    }
}

// --- ListDetailSceneStrategy ---
/**
 * A [SceneStrategy] that returns a [ListDetailScene] if the window is wide enough, the last item
 * is the backstack is a detail, and before it, at any point in the backstack is a list.
 */
class ListDetailSceneStrategy<T : Any>(val windowSizeClass: WindowSizeClass) : SceneStrategy<T> {

    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {

        if (!windowSizeClass.isWidthAtLeastBreakpoint(WIDTH_DP_MEDIUM_LOWER_BOUND)) {
            return null
        }

        val detailEntry =
            entries.lastOrNull()?.takeIf { it.metadata.containsKey(DETAIL_KEY) } ?: return null
        val listEntry = entries.findLast { it.metadata.containsKey(LIST_KEY) } ?: return null

        // We use the list's contentKey to uniquely identify the scene.
        // This allows the detail panes to be displayed instantly through recomposition, rather than
        // having NavDisplay animate the whole scene out when the selected detail item changes.
        val sceneKey = listEntry.contentKey

        return ListDetailScene(
            key = sceneKey,
            previousEntries = entries.dropLast(1),
            listEntry = listEntry,
            detailEntry = detailEntry
        )
    }

    companion object {
        internal const val LIST_KEY = "ListDetailScene-List"
        internal const val DETAIL_KEY = "ListDetailScene-Detail"

        /**
         * Helper function to add metadata to a [NavEntry] indicating it can be displayed
         * as a list in the [ListDetailScene].
         */
        fun listPane() = mapOf(LIST_KEY to true)

        /**
         * Helper function to add metadata to a [NavEntry] indicating it can be displayed
         * as a list in the [ListDetailScene].
         */
        fun detailPane() = mapOf(DETAIL_KEY to true)
    }
}
// [END android_compose_navigation3_scenes_3]

// [START android_compose_navigation3_scenes_4]
// Define your navigation keys
@Serializable
data object ConversationList : NavKey

@Serializable
data class ConversationDetail(val id: String) : NavKey

@Composable
fun MyAppContent() {
    val backStack = rememberNavBackStack(ConversationList)
    val listDetailStrategy = rememberListDetailSceneStrategy<NavKey>()

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        sceneStrategy = listDetailStrategy,
        entryProvider = entryProvider {
            entry<ConversationList>(
                metadata = ListDetailSceneStrategy.listPane()
            ) {
                Column(modifier = Modifier.fillMaxSize()) {
                    Text(text = "I'm a Conversation List")
                    Button(onClick = { backStack.addDetail(ConversationDetail("123")) }) {
                        Text(text = "Open detail")
                    }
                }
            }
            entry<ConversationDetail>(
                metadata = ListDetailSceneStrategy.detailPane()
            ) {
                Text(text = "I'm a Conversation Detail")
            }
        }
    )
}

private fun NavBackStack<NavKey>.addDetail(detailRoute: ConversationDetail) {

    // Remove any existing detail routes, then add the new detail route
    removeIf { it is ConversationDetail }
    add(detailRoute)
}
// [END android_compose_navigation3_scenes_4]
