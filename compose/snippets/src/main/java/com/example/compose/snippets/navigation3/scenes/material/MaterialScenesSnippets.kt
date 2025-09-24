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

package com.example.compose.snippets.navigation3.scenes.material

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.compose.snippets.navigation3.ContentBlue
import com.example.compose.snippets.navigation3.ContentGreen
import com.example.compose.snippets.navigation3.ContentRed
import com.example.compose.snippets.navigation3.ContentYellow
import com.example.compose.snippets.ui.theme.PastelBlue
import kotlinx.serialization.Serializable

// [START android_compose_navigation3_scenes_material_1]
@Serializable
object ProductList : NavKey

@Serializable
data class ProductDetail(val id: String) : NavKey

@Serializable
data object Profile : NavKey

class MaterialListDetailActivity : ComponentActivity() {

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            Scaffold { paddingValues ->
                val backStack = rememberNavBackStack(ProductList)
                val listDetailStrategy = rememberListDetailSceneStrategy<Any>()

                NavDisplay(
                    backStack = backStack,
                    modifier = Modifier.padding(paddingValues),
                    onBack = { keysToRemove -> repeat(keysToRemove) { backStack.removeLastOrNull() } },
                    sceneStrategy = listDetailStrategy,
                    entryProvider = entryProvider {
                        entry<ProductList>(
                            metadata = ListDetailSceneStrategy.listPane(
                                detailPlaceholder = {
                                    ContentYellow("Choose a product from the list")
                                }
                            )
                        ) {
                            ContentRed("Welcome to Nav3") {
                                Button(onClick = {
                                    backStack.add(ProductDetail("ABC"))
                                }) {
                                    Text("View product")
                                }
                            }
                        }
                        entry<ProductDetail>(
                            metadata = ListDetailSceneStrategy.detailPane()
                        ) { product ->
                            ContentBlue("Product ${product.id} ", Modifier.background(PastelBlue)) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Button(onClick = {
                                        backStack.add(Profile)
                                    }) {
                                        Text("View profile")
                                    }
                                }
                            }
                        }
                        entry<Profile>(
                            metadata = ListDetailSceneStrategy.extraPane()
                        ) {
                            ContentGreen("Profile")
                        }
                    }
                )
            }
        }
    }
}
// [END android_compose_navigation3_scenes_material_1]
