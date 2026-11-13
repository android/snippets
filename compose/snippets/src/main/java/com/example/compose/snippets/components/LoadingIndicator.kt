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

package com.example.compose.snippets.components

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSliderState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
// [START android_compose_expressive_components_loadingindicator]
@Composable
fun LoadingIndicatorSample() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) { LoadingIndicator() }
}
// [END android_compose_expressive_components_loadingindicator]

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
// [START android_compose_expressive_components_containedloadingindicator]
@Composable
fun ContainedLoadingIndicatorSample() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) { ContainedLoadingIndicator() }
}
// [END android_compose_expressive_components_containedloadingindicator]

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Preview
// [START android_compose_expressive_components_determinateloadingindicator]
@Composable
fun DeterminateLoadingIndicatorSample() {
    val sliderState = rememberSliderState()
    val animatedProgress by
        animateFloatAsState(
            targetValue = sliderState.value,
            animationSpec =
                spring(
                    dampingRatio = Spring.DampingRatioNoBouncy,
                    stiffness = Spring.StiffnessVeryLow,
                    visibilityThreshold = 1 / 1000f,
                ),
        )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LoadingIndicator(progress = { animatedProgress })
        Spacer(Modifier.requiredHeight(30.dp))
        Text("Set loading progress:")
        Slider(
            state = sliderState,
            onValueChange = { sliderState.value = it },
            modifier = Modifier.width(300.dp),
        )
    }
}
// [END android_compose_expressive_components_determinateloadingindicator]
