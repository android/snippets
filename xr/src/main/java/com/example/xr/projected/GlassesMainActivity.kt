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

package com.example.xr.projected

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import androidx.xr.glimmer.Button
import androidx.xr.glimmer.Card
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.surface
import androidx.xr.projected.ProjectedDisplayController
import androidx.xr.projected.experimental.ExperimentalProjectedApi
import kotlinx.coroutines.launch

// [START androidxr_projected_ai_glasses_activity]
@OptIn(ExperimentalProjectedApi::class)
class GlassesMainActivity : ComponentActivity() {

    private var projectedDisplayController: ProjectedDisplayController? = null
    private var areVisualsOn by mutableStateOf(true)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            // initialize the controller
            val controller = ProjectedDisplayController.create(this@GlassesMainActivity)
            projectedDisplayController = controller

            // attach the observer to manage registration based on Activity visibility
            val observer = GlassesLifecycleObserver(
                context = this@GlassesMainActivity,
                controller = controller,
                onVisualsChanged = { visualsOn ->
                    areVisualsOn = visualsOn
                }
            )
            lifecycle.addObserver(observer)
        }

        setContent {
            GlimmerTheme {
                HomeScreen(
                    visualsOn = areVisualsOn,
                    onClose = { finish() }
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        projectedDisplayController?.close()
    }
}
// [END androidxr_projected_ai_glasses_activity]

// [START androidxr_projected_ai_glasses_activity_homescreen]
@Composable
fun HomeScreen(
    visualsOn: Boolean,
    modifier: Modifier = Modifier,
    onClose: () -> Unit
) {
    Box(
        modifier = modifier
            .surface(focusable = false)
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Card(
            title = { Text("Android XR") },
            action = {
                Button(onClick = {
                    onClose()
                }) {
                    Text("Close")
                }
            }
        ) {
            // UI dynamically updates based on the observer's state
            if (visualsOn) {
                Text("Hello, AI Glasses!")
            } else {
                Text("Display is off. Audio guidance active.")
            }
        }
    }
}
// [END androidxr_projected_ai_glasses_activity_homescreen]
