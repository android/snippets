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

package com.example.xr.compose

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.xr.compose.platform.LocalSession
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.ExperimentalSubspaceVolumeApi
import androidx.xr.compose.subspace.MovePolicy
import androidx.xr.compose.subspace.ResizePolicy
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.Volume
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.offset
import androidx.xr.compose.subspace.layout.scale
import androidx.xr.compose.subspace.layout.width
import kotlinx.coroutines.launch

@Composable
private fun VolumeExample() {
    // [START androidxr_compose_Volume]
    Subspace {
        SpatialPanel(
            SubspaceModifier.height(1500.dp).width(1500.dp),
            dragPolicy = MovePolicy(),
            resizePolicy = ResizePolicy(),
        ) {
            ObjectInAVolume(true)
            Box(
                Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Welcome",
                    fontSize = 50.sp,
                )
            }
        }
    }
    // [END androidxr_compose_Volume]
}

// [START androidxr_compose_ObjectInAVolume]
@OptIn(ExperimentalSubspaceVolumeApi::class)
@Composable
fun ObjectInAVolume(show3DObject: Boolean) {
    // [START_EXCLUDE silent]
    val volumeXOffset = 0.dp
    val volumeYOffset = 0.dp
    val volumeZOffset = 0.dp
    // [END_EXCLUDE]
    val session = checkNotNull(LocalSession.current)
    val scope = rememberCoroutineScope()
    if (show3DObject) {
        Subspace {
            Volume(
                modifier = SubspaceModifier
                    .offset(volumeXOffset, volumeYOffset, volumeZOffset) // Relative position
                    .scale(1.2f) // Scale to 120% of the size
            ) { parent ->
                scope.launch {
                    // Load your 3D model here
                }
            }
        }
    }
}
// [END androidxr_compose_ObjectInAVolume]
