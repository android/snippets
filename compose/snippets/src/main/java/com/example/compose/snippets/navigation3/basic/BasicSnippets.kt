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

package com.example.compose.snippets.navigation3.basic

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.compose.snippets.navigation3.ContentBlue
import com.example.compose.snippets.navigation3.ContentGreen
import com.example.compose.snippets.navigation3.savingstate.Home

// [START android_compose_navigation3_basic_1]
// Define keys that will identify content
data object ProductList
data class ProductDetail(val id: String)

@Composable
fun MyApp() {

    // Create a back stack, specifying the key the app should start with
    val backStack = remember { mutableStateListOf<Any>(ProductList) }

    // Supply your back stack to a NavDisplay so it can reflect changes in the UI
    // ...more on this below...

    // Push a key onto the back stack (navigate forward), the navigation library will reflect the change in state
    backStack.add(ProductDetail(id = "ABC"))

    // Pop a key off the back stack (navigate back), the navigation library will reflect the change in state
    backStack.removeLastOrNull()
}
// [END android_compose_navigation3_basic_1]

@Composable
fun EntryProvider() {
    val backStack = remember { mutableStateListOf<Any>(ProductList) }
    NavDisplay(
        backStack = backStack,
        // [START android_compose_navigation3_basic_2]
        entryProvider = { key ->
            when (key) {
                is ProductList -> NavEntry(key) { Text("Product List") }
                is ProductDetail -> NavEntry(
                    key,
                    metadata = mapOf("extraDataKey" to "extraDataValue")
                ) { Text("Product ${key.id} ") }

                else -> {
                    NavEntry(Unit) { Text(text = "Invalid Key: $it") }
                }
            }
        }
        // [END android_compose_navigation3_basic_2]
    )
}

@Composable
fun EntryProviderDsl() {
    val backStack = remember { mutableStateListOf<Any>(ProductList) }
    NavDisplay(
        backStack = backStack,
        // [START android_compose_navigation3_basic_3]
        entryProvider = entryProvider {
            entry<ProductList> { Text("Product List") }
            entry<ProductDetail>(
                metadata = mapOf("extraDataKey" to "extraDataValue")
            ) { key -> Text("Product ${key.id} ") }
        }
        // [END android_compose_navigation3_basic_3]
    )
}

// [START android_compose_navigation3_basic_4]
data object Home
data class Product(val id: String)

@Composable
fun NavExample() {

    val backStack = remember { mutableStateListOf<Any>(Home) }

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryProvider = { key ->
            when (key) {
                is Home -> NavEntry(key) {
                    ContentGreen("Welcome to Nav3") {
                        Button(onClick = {
                            backStack.add(Product("123"))
                        }) {
                            Text("Click to navigate")
                        }
                    }
                }

                is Product -> NavEntry(key) {
                    ContentBlue("Product ${key.id} ")
                }

                else -> NavEntry(Unit) { Text("Unknown route") }
            }
        }
    )
}
// [END android_compose_navigation3_basic_4]
