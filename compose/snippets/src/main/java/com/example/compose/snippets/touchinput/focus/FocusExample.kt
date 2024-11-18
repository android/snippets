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

package com.example.compose.snippets.touchinput.focus

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

enum class FocusExample(
    val route: String,
    val title: String
) {
    Home("home", "Home"),
    FocusTraversal("focusTraversal", "Focus Traversal"),
    InitialFocus("initialFocus", "Initial Focus"),
    InitialFocusWithScrollableContainer(
        "initialFocusWithScrollableContainer",
        "Initial Focus with Scrollable Container"
    ),
    InitialFocusEnablingContentReload(
        "initialFocusEnablingContentReload",
        "Initial Focus Enabling Content Reload"
    )
}

@Composable
fun FocusExample(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController, startDestination = FocusExample.Home.route) {
        composable(FocusExample.Home.route) {
            val entries = remember {
                listOf(
                    FocusExample.InitialFocus,
                    FocusExample.InitialFocusWithScrollableContainer,
                    FocusExample.InitialFocusEnablingContentReload
                )
            }
            FocusExampleScreen(entries) {
                navController.navigate(it.route)
            }
        }
        composable(FocusExample.FocusTraversal.route) {
        }
        composable(FocusExample.InitialFocus.route) {
            InitialFocusScreen()
        }
        composable(FocusExample.InitialFocusWithScrollableContainer.route) {
            InitialFocusWithScrollableContainerScreen()
        }
        composable(FocusExample.InitialFocusEnablingContentReload.route) {
            InitialFocusWithContentReloadScreen()
        }
    }
}

@Composable
private fun FocusExampleScreen(
    examples: List<FocusExample>,
    onExampleClick: (FocusExample) -> Unit = {}
) {
    Box(contentAlignment = Alignment.TopCenter) {
        LazyColumn(
            modifier = Modifier.widthIn(
                max = 600.dp
            )
        ) {
            items(examples) {
                ListItem(
                    headlineContent = { Text(it.title) },
                    modifier = Modifier.clickable { onExampleClick(it) }
                )
            }
        }
    }
}
