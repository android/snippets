/*
 * Copyright 2023 The Android Open Source Project
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

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.CircularWavyProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Preview
@Composable
fun ProgressIndicatorExamples() {
    Column(
        modifier = Modifier
            .padding(48.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text("Determinate linear indicator:")
        LinearDeterminateIndicator()
        Text("Indeterminate linear indicator:")
        IndeterminateLinearIndicator()
        Text("Determinate circular indicator:")
        CircularDeterminateIndicator()
        Text("Indeterminate circular indicator:")
        IndeterminateCircularIndicator()
        Text("Determinate linear wavy indicator:")
        LinearWavyProgressIndicatorSample()
        Text("Indeterminate linear wavy indicator:")
        IndeterminateLinearWavyProgressIndicatorSample()
        Text("Determinate circular wavy indicator:")
        CircularWavyProgressIndicatorSample()
        Text("Indeterminate circular wavy indicator:")
        IndeterminateCircularWavyProgressIndicatorSample()
        Text("Determinate linear indicator Expressive:")
        LinearProgressIndicatorSample()
        Text("Indeterminate linear indicator Expressive:")
        IndeterminateLinearProgressIndicatorSample()
        Text("Determinate circular indicator Expressive:")
        CircularProgressIndicatorSample()
        Text("Indeterminate circular indicator Expressive:")
        IndeterminateCircularProgressIndicatorSample()
    }
}

@Preview
// [START android_compose_components_determinateindicator]
@Composable
fun LinearDeterminateIndicator() {
    var currentProgress by remember { mutableFloatStateOf(0f) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope() // Create a coroutine scope

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(onClick = {
            loading = true
            scope.launch {
                loadProgress { progress ->
                    currentProgress = progress
                }
                loading = false // Reset loading when the coroutine finishes
            }
        }, enabled = !loading) {
            Text("Start loading")
        }

        if (loading) {
            LinearProgressIndicator(
                progress = { currentProgress },
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

/** Iterate the progress value */
suspend fun loadProgress(updateProgress: (Float) -> Unit) {
    for (i in 1..100) {
        updateProgress(i.toFloat() / 100)
        delay(100)
    }
}
// [END android_compose_components_determinateindicator]

@Preview
@Composable
fun CircularDeterminateIndicator() {
    var currentProgress by remember { mutableFloatStateOf(0f) }
    var loading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope() // Create a coroutine scope

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(onClick = {
            loading = true
            scope.launch {
                loadProgress { progress ->
                    currentProgress = progress
                }
                loading = false // Reset loading when the coroutine finishes
            }
        }, enabled = !loading) {
            Text("Start loading")
        }

        if (loading) {
            CircularProgressIndicator(
                progress = { currentProgress },
                modifier = Modifier.width(64.dp),
            )
        }
    }
}

@Preview
@Composable
fun IndeterminateLinearIndicator() {
    var loading by remember { mutableStateOf(false) }

    Button(onClick = { loading = true }, enabled = !loading) {
        Text("Start loading")
    }

    if (!loading) return

    LinearProgressIndicator(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}

@Preview
// [START android_compose_components_indeterminateindicator]
@Composable
fun IndeterminateCircularIndicator() {
    var loading by remember { mutableStateOf(false) }

    Button(onClick = { loading = true }, enabled = !loading) {
        Text("Start loading")
    }

    if (!loading) return

    CircularProgressIndicator(
        modifier = Modifier.width(64.dp),
        color = MaterialTheme.colorScheme.secondary,
        trackColor = MaterialTheme.colorScheme.surfaceVariant,
    )
}
// [END android_compose_components_indeterminateindicator]

// [START android_compose_expressive_components_determinatelinearwavyindicator]
@Preview
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LinearWavyProgressIndicatorSample() {
    var progress by remember { mutableFloatStateOf(0.1f) }
    val animatedProgress by
    animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LinearWavyProgressIndicator(progress = { animatedProgress })
        Spacer(Modifier.requiredHeight(30.dp))
        Text("Set progress:")
        Slider(
            modifier = Modifier.width(300.dp),
            value = progress,
            valueRange = 0f..1f,
            onValueChange = { progress = it },
        )
    }
}
// [END android_compose_expressive_components_determinatelinearwavyindicator]

// [START android_compose_expressive_components_indeterminatelinearwavyindicator]
@Preview
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun IndeterminateLinearWavyProgressIndicatorSample() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) { LinearWavyProgressIndicator() }
}
// [END android_compose_expressive_components_indeterminatelinearwavyindicator]

// [START android_compose_expressive_components_determinatecircularwavyindicator]
@Preview
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun CircularWavyProgressIndicatorSample() {
    var progress by remember { mutableFloatStateOf(0.1f) }
    val animatedProgress by
    animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularWavyProgressIndicator(progress = { animatedProgress })
        Spacer(Modifier.requiredHeight(30.dp))
        Text("Set progress:")
        Slider(
            modifier = Modifier.width(300.dp),
            value = progress,
            valueRange = 0f..1f,
            onValueChange = { progress = it },
        )
    }
}
// [END android_compose_expressive_components_determinatecircularwavyindicator]

// [START android_compose_expressive_components_indeterminatecircularwavyindicator]
@Preview
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun IndeterminateCircularWavyProgressIndicatorSample() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) { CircularWavyProgressIndicator() }
}
// [END android_compose_expressive_components_indeterminatecircularwavyindicator]

// [START android_compose_expressive_components_determinatelinearindicator]
@Preview
@Composable
fun LinearProgressIndicatorSample() {
    var progress by remember { mutableFloatStateOf(0.1f) }
    val animatedProgress by
    animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        LinearProgressIndicator(progress = { animatedProgress })
        Spacer(Modifier.requiredHeight(30.dp))
        Text("Set progress:")
        Slider(
            modifier = Modifier.width(300.dp),
            value = progress,
            valueRange = 0f..1f,
            onValueChange = { progress = it },
        )
    }
}
// [END android_compose_expressive_components_determinatelinearindicator]

// [START android_compose_expressive_components_indeterminatelinearindicator]
@Preview
@Composable
fun IndeterminateLinearProgressIndicatorSample() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) { LinearProgressIndicator() }
}
// [END android_compose_expressive_components_indeterminatelinearindicator]

// [START android_compose_expressive_components_determinatecircularindicator]
@Preview
@Composable
fun CircularProgressIndicatorSample() {
    var progress by remember { mutableFloatStateOf(0.1f) }
    val animatedProgress by
    animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        CircularProgressIndicator(progress = { animatedProgress })
        Spacer(Modifier.requiredHeight(30.dp))
        Text("Set progress:")
        Slider(
            modifier = Modifier.width(300.dp),
            value = progress,
            valueRange = 0f..1f,
            onValueChange = { progress = it },
        )
    }
}
// [END android_compose_expressive_components_determinatecircularindicator]

// [START android_compose_expressive_components_indeterminatecircularindicator]
@Preview
@Composable
fun IndeterminateCircularProgressIndicatorSample() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) { CircularProgressIndicator() }
}
// [END android_compose_expressive_components_indeterminatecircularindicator]
