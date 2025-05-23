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

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.xr.compose.platform.LocalSpatialCapabilities
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.fillMaxHeight
import androidx.xr.compose.subspace.layout.width

@Composable
private fun SupportingInfoPanel() {}

@Composable
private fun ButtonToPresentInfoModal() {}

@Composable
private fun SpatialCapabilitiesCheck() {
    // [START androidxr_compose_checkSpatialCapabilities]
    if (LocalSpatialCapabilities.current.isSpatialUiEnabled) {
        SupportingInfoPanel()
    } else {
        ButtonToPresentInfoModal()
    }

    // Similar check for audio
    val spatialAudioEnabled = LocalSpatialCapabilities.current.isSpatialAudioEnabled
    // [END androidxr_compose_checkSpatialCapabilities]
}

@Composable
private fun checkSpatialUiEnabled() {
    // [START androidxr_compose_checkSpatialUiEnabled]
    if (LocalSpatialCapabilities.current.isSpatialUiEnabled) {
        Subspace {
            SpatialPanel(
                modifier = SubspaceModifier
                    .width(1488.dp)
                    .fillMaxHeight()
            ) {
                AppContent()
            }
        }
    } else {
        AppContent()
    }
    // [END androidxr_compose_checkSpatialUiEnabled]
}

@Composable
private fun AppContent() {}
