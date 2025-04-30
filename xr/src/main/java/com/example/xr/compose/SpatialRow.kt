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
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.SpatialRow
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.width

@Composable
private fun SpatialRowExample() {
    // [START androidxr_compose_SpatialRowExample]
    SpatialRow(curveRadius = 825.dp) {
        SpatialPanel(
            SubspaceModifier
                .width(384.dp)
                .height(592.dp)
        ) {
            StartSupportingPanelContent()
        }
        SpatialPanel(
            SubspaceModifier
                .height(824.dp)
                .width(1400.dp)
        ) {
            App()
        }
        SpatialPanel(
            SubspaceModifier
                .width(288.dp)
                .height(480.dp)
        ) {
            EndSupportingPanelContent()
        }
    }
    // [END androidxr_compose_SpatialRowExample]
}

@Composable
private fun App() { }

@Composable
private fun EndSupportingPanelContent() { }

@Composable
private fun StartSupportingPanelContent() { }
