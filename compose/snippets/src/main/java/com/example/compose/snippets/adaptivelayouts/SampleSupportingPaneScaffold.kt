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

@file:OptIn(ExperimentalMaterial3AdaptiveApi::class)

package com.example.compose.snippets.adaptivelayouts

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldScope
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch

@Preview
@Composable
fun SampleSupportingPaneScaffoldParts() {
    // [START android_compose_adaptivelayouts_sample_supporting_pane_scaffold_nav_and_back]
    val navigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    BackHandler(navigator.canNavigateBack()) {
        scope.launch { navigator.navigateBack() }
    }
    // [END android_compose_adaptivelayouts_sample_supporting_pane_scaffold_nav_and_back]

    // [START android_compose_adaptivelayouts_sample_supporting_pane_scaffold_params]
    SupportingPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        mainPane = { /*...*/ },
        supportingPane = { /*...*/ },
    )
    // [END android_compose_adaptivelayouts_sample_supporting_pane_scaffold_params]
}

@Preview
@Composable
fun SampleSupportingPaneScaffoldFull() {
    // [START android_compose_adaptivelayouts_sample_supporting_pane_scaffold_full]
    val navigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    BackHandler(navigator.canNavigateBack()) {
        scope.launch { navigator.navigateBack() }
    }

    SupportingPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        mainPane = {
            AnimatedPane(modifier = Modifier.safeContentPadding()) {
                // Main pane content
                if (navigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting] == PaneAdaptedValue.Hidden) {
                    Button(
                        modifier = Modifier.wrapContentSize(),
                        onClick = {
                            scope.launch {
                                navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
                            }
                        }
                    ) {
                        Text("Show supporting pane")
                    }
                } else {
                    Text("Supporting pane is shown")
                }
            }
        },
        supportingPane = {
            AnimatedPane(modifier = Modifier.safeContentPadding()) {
                // Supporting pane content
                Text("Supporting pane")
            }
        },
    )
    // [END android_compose_adaptivelayouts_sample_supporting_pane_scaffold_full]
}

// [START android_compose_adaptivelayouts_sample_supporting_pane_scaffold_extracted_panes]
@Composable
fun ThreePaneScaffoldScope.MainPane(
    shouldShowSupportingPaneButton: Boolean,
    onNavigateToSupportingPane: () -> Unit,
    modifier: Modifier = Modifier,
) {
    MainPane(
        modifier = modifier.safeContentPadding(),
        shouldShowSupportingPaneButton = shouldShowSupportingPaneButton,
        onNavigateToSupportingPane = onNavigateToSupportingPane
    )
}

@Composable
fun ThreePaneScaffoldScope.SupportingPane(
    modifier: Modifier = Modifier,
) {
    SupportingPane(modifier = modifier.safeContentPadding())
}
// [END android_compose_adaptivelayouts_sample_supporting_pane_scaffold_extracted_panes]

@Preview
@Composable
fun SampleSupportingPaneScaffoldSimplified() {
// [START android_compose_adaptivelayouts_sample_supporting_pane_scaffold_simplified]
    val navigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    BackHandler(navigator.canNavigateBack()) {
        scope.launch { navigator.navigateBack() }
    }

    SupportingPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        mainPane = {
            MainPane(
                shouldShowSupportingPaneButton = navigator.scaffoldValue.secondary == PaneAdaptedValue.Hidden,
                onNavigateToSupportingPane = {
                    scope.launch { navigator.navigateTo(ThreePaneScaffoldRole.Secondary) }
                }
            )
        },
        supportingPane = { SupportingPane() },
    )
// [END android_compose_adaptivelayouts_sample_supporting_pane_scaffold_simplified]
}
