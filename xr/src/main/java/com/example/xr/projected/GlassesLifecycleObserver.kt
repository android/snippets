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

import android.content.Context
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.xr.projected.ProjectedDisplayController
import androidx.xr.projected.ProjectedDisplayController.PresentationMode
import androidx.xr.projected.experimental.ExperimentalProjectedApi
import java.util.function.Consumer

@OptIn(ExperimentalProjectedApi::class)
class GlassesLifecycleObserver(
    context: Context,
    private val controller: ProjectedDisplayController,
    private val onVisualsChanged: (Boolean) -> Unit
) : DefaultLifecycleObserver {

    private val executor = ContextCompat.getMainExecutor(context)

    private val visualStateListener = Consumer<ProjectedDisplayController.PresentationModeFlags> { flags ->
        val visualsOn = flags.hasPresentationMode(PresentationMode.VISUALS_ON)
        onVisualsChanged(visualsOn)
    }

    override fun onStart(owner: LifecycleOwner) {
        // Register when the Activity is visible
        controller.addPresentationModeChangedListener(executor, visualStateListener)
    }

    override fun onStop(owner: LifecycleOwner) {
        // Unregister when the Activity is hidden to save battery and prevent leaks
        controller.removePresentationModeChangedListener(visualStateListener)
    }
}
