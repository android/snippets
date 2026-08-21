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

package com.example.compose.snippets.navigation3.metadata

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.NavMetadataKey
import androidx.navigation3.runtime.contains
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.get
import androidx.navigation3.runtime.metadata
import androidx.navigation3.scene.Scene
import androidx.navigation3.scene.SceneStrategy
import androidx.navigation3.scene.SceneStrategyScope

data object Home : NavKey
data class Conversation(val id: String) : NavKey

val condition = true

fun manualEntryProvider(key: Any) {
    // [START android_compose_navigation3_metadata_1]
    when (key) {
        is Home -> NavEntry(key, metadata = mapOf("key" to "value")) {}
    }
    // [END android_compose_navigation3_metadata_1]
}

fun dslEntryProvider() {
    entryProvider {
        // [START android_compose_navigation3_metadata_2]
        entry<Home>(metadata = mapOf("key" to "value")) { /* ... */ }
        entry<Conversation>(metadata = { key: Conversation ->
            mapOf("key" to "value: ${key.id})")
        }) { /* ... */ }
        // [END android_compose_navigation3_metadata_2]
    }
}

// [START android_compose_navigation3_metadata_3]
// For classes such as scene strategies or nav entry decorators, you can define the keys
// as nested object.
class MySceneStrategy<T : Any> : SceneStrategy<T> {

    // [START_EXCLUDE]
    override fun SceneStrategyScope<T>.calculateScene(entries: List<NavEntry<T>>): Scene<T>? {
        TODO("Not yet implemented")
    }
    // [END_EXCLUDE]

    object MyStringMetadataKey : NavMetadataKey<String>
}

// An example from NavDisplay.
// Because NavDisplay is a function, the metadata keys are defined in an object with the same name.
public object NavDisplay {

    public object TransitionKey :
        NavMetadataKey<AnimatedContentTransitionScope<Scene<*>>.() -> ContentTransform>
}
// [END android_compose_navigation3_metadata_3]

val entryProvider = entryProvider<NavKey> {
    // [START android_compose_navigation3_metadata_4]
    entry<Home>(
        metadata = metadata {
            put(NavDisplay.TransitionKey) { fadeIn() togetherWith fadeOut() }
            // An additional benefit of the metadata DSL is the ability to use conditional logic
            if (condition) {
                put(MySceneStrategy.MyStringMetadataKey, "Hello, world!")
            }
        }
    ) {
        // ...
    }
    // [END android_compose_navigation3_metadata_4]
}

val metadata = emptyMap<String, Any>()

// [START android_compose_navigation3_metadata_5]
// import androidx.navigation3.runtime.contains
// import androidx.navigation3.runtime.get

val hasMyString: Boolean = metadata.contains(MySceneStrategy.MyStringMetadataKey)
val myString: String? = metadata[MySceneStrategy.MyStringMetadataKey]
// [END android_compose_navigation3_metadata_5]
