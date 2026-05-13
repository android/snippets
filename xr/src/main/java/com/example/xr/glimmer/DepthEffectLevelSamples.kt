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

package com.example.xr.glimmer

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.xr.glimmer.DepthEffect
import androidx.xr.glimmer.GlimmerTheme
import androidx.xr.glimmer.SurfaceDepthEffect
import androidx.xr.glimmer.Text
import androidx.xr.glimmer.surface

// [START xr_glimmer_depth_effect_levels]
@Composable
fun DepthEffectLevelsSample() {
    val levels = GlimmerTheme.depthEffectLevels
    Box(contentAlignment = Alignment.Center) {
        DepthBox("Level 1", levels.level1)
        DepthBox("Level 2", levels.level2)
        DepthBox("Level 3", levels.level3)
        DepthBox("Level 4", levels.level4)
        DepthBox("Level 5", levels.level5)
    }
}

@Composable
private fun DepthBox(name: String, depthEffect: DepthEffect) {
    val interactionSource = remember { MutableInteractionSource() }
    Box(
        modifier = Modifier
            .size(100.dp)
            .surface(
                interactionSource = interactionSource,
                depthEffect = SurfaceDepthEffect(
                    depthEffect = depthEffect,
                    focusedDepthEffect = depthEffect
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(name)
    }
}
// [END xr_glimmer_depth_effect_levels]
