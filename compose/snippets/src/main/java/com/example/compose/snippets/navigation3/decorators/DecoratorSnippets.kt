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

package com.example.compose.snippets.navigation3.decorators

import android.util.Log
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.example.compose.snippets.navigation3.savingstate.Home
import kotlinx.serialization.Serializable

@Serializable
data object Home : NavKey

@Composable
fun DecoratorsBasic() {
    // [START android_compose_navigation3_decorator_1]
    NavDisplay(
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        //[START_EXCLUDE]
        backStack = rememberNavBackStack(Home),
        entryProvider = entryProvider {
            entry<Home> { Text("Welcome to Nav3") }
        }
        //[END_EXCLUDE]
    )
    // [END android_compose_navigation3_decorator_1]
}

// [START android_compose_navigation3_decorator_2]
class CustomNavEntryDecorator<T : Any> : NavEntryDecorator<T>(
    decorate = { entry ->
        Log.d("CustomNavEntryDecorator", "entry with ${entry.contentKey} entered composition and was decorated")
        entry.Content()
    },
    onPop = { contentKey -> Log.d("CustomNavEntryDecorator", "entry with $contentKey was popped") }
)
// [END android_compose_navigation3_decorator_2]
