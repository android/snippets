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

package com.example.compose.snippets.adaptivelayouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.PaneAdaptedValue
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffold
import androidx.compose.material3.adaptive.layout.SupportingPaneScaffoldRole
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldPaneScope
import androidx.compose.material3.adaptive.layout.ThreePaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.BackNavigationBehavior
import androidx.compose.material3.adaptive.navigation.NavigableSupportingPaneScaffold
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldPredictiveBackHandler
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SampleNavigableSupportingPaneScaffoldParts() {
    // [START android_compose_adaptivelayouts_sample_navigable_supporting_pane_scaffold_nav_and_back]
    val scaffoldNavigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    // [END android_compose_adaptivelayouts_sample_navigable_supporting_pane_scaffold_nav_and_back]

    // [START android_compose_adaptivelayouts_sample_navigable_supporting_pane_scaffold_params]
    NavigableSupportingPaneScaffold(
        navigator = scaffoldNavigator,
        mainPane = { /*...*/ },
        supportingPane = { /*...*/ },
    )
    // [END android_compose_adaptivelayouts_sample_navigable_supporting_pane_scaffold_params]
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
@Preview
fun SampleNavigableSupportingPaneScaffoldFull() {
    // [START android_compose_adaptivelayouts_sample_navigable_supporting_pane_scaffold_full]
    val scaffoldNavigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    NavigableSupportingPaneScaffold(
        navigator = scaffoldNavigator,
        mainPane = {
            AnimatedPane(
                modifier = Modifier
                    .safeContentPadding()
                    .background(Color.Red)
            ) {
                if (scaffoldNavigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting] == PaneAdaptedValue.Hidden) {
                    Button(
                        modifier = Modifier
                            .wrapContentSize(),
                        onClick = {
                            scope.launch {
                                scaffoldNavigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
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
                Text("Supporting pane")
            }
        }
    )
    // [END android_compose_adaptivelayouts_sample_navigable_supporting_pane_scaffold_full]
}

// [START android_compose_adaptivelayouts_sample_supporting_pane_scaffold_extracted_panes]
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ThreePaneScaffoldPaneScope.MainPane(
    shouldShowSupportingPaneButton: Boolean,
    onNavigateToSupportingPane: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedPane(
        modifier = modifier.safeContentPadding()
    ) {
        // Main pane content
        if (shouldShowSupportingPaneButton) {
            Button(onClick = onNavigateToSupportingPane) {
                Text("Show supporting pane")
            }
        } else {
            Text("Supporting pane is shown")
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun ThreePaneScaffoldPaneScope.SupportingPane(
    modifier: Modifier = Modifier,
) {
    AnimatedPane(modifier = modifier.safeContentPadding()) {
        // Supporting pane content
        Text("This is the supporting pane")
    }
}
// [END android_compose_adaptivelayouts_sample_supporting_pane_scaffold_extracted_panes]

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SampleNavigableSupportingPaneScaffoldSimplified() {
    // [START android_compose_adaptivelayouts_sample_navigable_supporting_pane_scaffold_simplified]
    val scaffoldNavigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    NavigableSupportingPaneScaffold(
        navigator = scaffoldNavigator,
        mainPane = {
            MainPane(
                shouldShowSupportingPaneButton = scaffoldNavigator.scaffoldValue.secondary == PaneAdaptedValue.Hidden,
                onNavigateToSupportingPane = {
                    scope.launch {
                        scaffoldNavigator.navigateTo(ThreePaneScaffoldRole.Secondary)
                    }
                }
            )
        },
        supportingPane = { SupportingPane() },
    )
    // [END android_compose_adaptivelayouts_sample_navigable_supporting_pane_scaffold_simplified]
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SampleSupportingPaneScaffoldSimplifiedWithPredictiveBackHandler() {
    // [START android_compose_adaptivelayouts_sample_supporting_pane_scaffold_simplified_with_predictive_back_handler]
    val scaffoldNavigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    ThreePaneScaffoldPredictiveBackHandler(
        navigator = scaffoldNavigator,
        backBehavior = BackNavigationBehavior.PopUntilScaffoldValueChange
    )

    SupportingPaneScaffold(
        directive = scaffoldNavigator.scaffoldDirective,
        scaffoldState = scaffoldNavigator.scaffoldState,
        mainPane = {
            MainPane(
                shouldShowSupportingPaneButton = scaffoldNavigator.scaffoldValue.secondary == PaneAdaptedValue.Hidden,
                onNavigateToSupportingPane = {
                    scope.launch {
                        scaffoldNavigator.navigateTo(ThreePaneScaffoldRole.Secondary)
                    }
                }
            )
        },
        supportingPane = { SupportingPane() },
    )
    // [END android_compose_adaptivelayouts_sample_supporting_pane_scaffold_simplified_with_predictive_back_handler]
}
