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
import androidx.compose.ui.Modifier

@Composable
fun SampleSupportingPaneScaffoldParts() {
    // [START android_compose_adaptivelayouts_sample_supporting_pane_scaffold_nav_and_back]
    val navigator = rememberSupportingPaneScaffoldNavigator()

    BackHandler(navigator.canNavigateBack()) {
        navigator.navigateBack()
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

@Composable
fun SampleSupportingPaneScaffoldFull() {
    // [START android_compose_adaptivelayouts_sample_supporting_pane_scaffold_full]
    val navigator = rememberSupportingPaneScaffoldNavigator()

    BackHandler(navigator.canNavigateBack()) {
        navigator.navigateBack()
    }

    SupportingPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        mainPane = {
            AnimatedPane(modifier = Modifier.safeContentPadding()) {
                // Main pane content
                if (navigator.scaffoldValue.secondary == PaneAdaptedValue.Hidden) {
                    Button(
                        modifier = Modifier.wrapContentSize(),
                        onClick = {
                            navigator.navigateTo(SupportingPaneScaffoldRole.Supporting)
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
) {
    AnimatedPane(modifier = Modifier.safeContentPadding()) {
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

@Composable
fun ThreePaneScaffoldScope.SupportingPane() {
    AnimatedPane(modifier = Modifier.safeContentPadding()) {
        // Supporting pane content
        Text("This is the supporting pane")
    }
}
// [END android_compose_adaptivelayouts_sample_supporting_pane_scaffold_extracted_panes]

@Composable
fun SampleSupportingPaneScaffoldSimplified() {
// [START android_compose_adaptivelayouts_sample_supporting_pane_scaffold_simplified]
    val navigator = rememberSupportingPaneScaffoldNavigator()

    BackHandler(navigator.canNavigateBack()) {
        navigator.navigateBack()
    }

    SupportingPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        mainPane = {
            MainPane(
                shouldShowSupportingPaneButton = navigator.scaffoldValue.secondary == PaneAdaptedValue.Hidden,
                onNavigateToSupportingPane = { navigator.navigateTo(ThreePaneScaffoldRole.Secondary) }
            )
        },
        supportingPane = { SupportingPane() },
    )
// [END android_compose_adaptivelayouts_sample_supporting_pane_scaffold_simplified]
}