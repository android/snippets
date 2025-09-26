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

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.xr.compose.platform.LocalSpatialCapabilities
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.MovePolicy
import androidx.xr.compose.subspace.ResizePolicy
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.width

@Composable
private fun SpatialPanelExample() {
    // [START androidxr_compose_SpatialPanel]
    Subspace {
        SpatialPanel(
            SubspaceModifier
                .height(824.dp)
                .width(1400.dp),
            dragPolicy = MovePolicy(),
            resizePolicy = ResizePolicy(),
        ) {
            SpatialPanelContent()
        }
    }
    // [END androidxr_compose_SpatialPanel]
}

// [START androidxr_compose_SpatialPanelContent]
@Composable
fun SpatialPanelContent() {
    Box(
        Modifier
            .background(color = Color.Black)
            .height(500.dp)
            .width(500.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Spatial Panel",
            color = Color.White,
            fontSize = 25.sp
        )
    }
}
// [END androidxr_compose_SpatialPanelContent]

@Composable
private fun AppContent() {}

@Composable
private fun ContentInSpatialPanel() {
    // [START androidxr_compose_SpatialPanelAppContent]
    if (LocalSpatialCapabilities.current.isSpatialUiEnabled) {
        Subspace {
            SpatialPanel(
                dragPolicy = MovePolicy(),
                resizePolicy = ResizePolicy(),
            ) {
                AppContent()
            }
        }
    } else {
        AppContent()
    }
    // [END androidxr_compose_SpatialPanelAppContent]
}
