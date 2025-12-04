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
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldNavigator
import androidx.compose.material3.adaptive.navigation.ThreePaneScaffoldPredictiveBackHandler
import androidx.compose.material3.adaptive.navigation.rememberSupportingPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Devices.TABLET
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SampleNavigableSupportingPaneScaffoldParts() {
    // [START android_compose_adaptivelayouts_sample_supporting_pane_scaffold_nav_and_back]
    val scaffoldNavigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()

    // [END android_compose_adaptivelayouts_sample_supporting_pane_scaffold_nav_and_back]

    // [START android_compose_adaptivelayouts_sample_supporting_pane_scaffold_params]
    NavigableSupportingPaneScaffold(
        navigator = scaffoldNavigator,
        mainPane = { /*...*/ },
        supportingPane = { /*...*/ },
    )
    // [END android_compose_adaptivelayouts_sample_supporting_pane_scaffold_params]
}

@Composable
@Preview(device = TABLET)
fun SampleNavigationSupportingPaneScaffoldFullTabletPreview() {
    SampleNavigableSupportingPaneScaffoldFull()
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
@Preview
fun SampleNavigableSupportingPaneScaffoldFull() {
    // [START android_compose_adaptivelayouts_sample_supporting_pane_scaffold_full]
    val scaffoldNavigator = rememberSupportingPaneScaffoldNavigator()
    val scope = rememberCoroutineScope()
    val backNavigationBehavior = BackNavigationBehavior.PopUntilScaffoldValueChange

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
                Column {
                    // Allow users to dismiss the supporting pane. Use back navigation to
                    // hide an expanded supporting pane.
                    if (scaffoldNavigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting] == PaneAdaptedValue.Expanded) {
                        // Material design principles promote the usage of a right-aligned
                        // close (X) button.
                        IconButton(
                            modifier =  Modifier.align(Alignment.End).padding(16.dp),
                            onClick = {
                                scope.launch {
                                    scaffoldNavigator.navigateBack(backNavigationBehavior)
                                }
                            }
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close")
                        }
                    }
                    Text("Supporting pane")
                }

            }
        }
    )
    // [END android_compose_adaptivelayouts_sample_supporting_pane_scaffold_full]
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
    scaffoldNavigator: ThreePaneScaffoldNavigator<Any>,
    modifier: Modifier = Modifier,
    backNavigationBehavior: BackNavigationBehavior = BackNavigationBehavior.PopUntilScaffoldValueChange,
) {
    val scope = rememberCoroutineScope()
    AnimatedPane(modifier = Modifier.safeContentPadding()) {
        Column {
            // Allow users to dismiss the supporting pane. Use back navigation to
            // hide an expanded supporting pane.
            if (scaffoldNavigator.scaffoldValue[SupportingPaneScaffoldRole.Supporting] == PaneAdaptedValue.Expanded) {
                // Material design principles promote the usage of a right-aligned
                // close (X) button.
                IconButton(
                    modifier =  modifier.align(Alignment.End).padding(16.dp),
                    onClick = {
                        scope.launch {
                            scaffoldNavigator.navigateBack(backNavigationBehavior)
                        }
                    }
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }
            Text("Supporting pane")
        }

    }
}
// [END android_compose_adaptivelayouts_sample_supporting_pane_scaffold_extracted_panes]

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun SampleNavigableSupportingPaneScaffoldSimplified() {
    // [START android_compose_adaptivelayouts_sample_supporting_pane_scaffold_simplified]
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
        supportingPane = { SupportingPane(scaffoldNavigator = scaffoldNavigator) },
    )
    // [END android_compose_adaptivelayouts_sample_supporting_pane_scaffold_simplified]
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
        supportingPane = { SupportingPane(scaffoldNavigator = scaffoldNavigator) },
    )
    // [END android_compose_adaptivelayouts_sample_supporting_pane_scaffold_simplified_with_predictive_back_handler]
}
