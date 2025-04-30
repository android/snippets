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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.SpatialColumn
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.SpatialRow
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.width

@Composable
private fun SpatialLayoutExampleSubspace() {
    // [START androidxr_compose_SpatialLayoutExampleSubspace]
    Subspace {
        SpatialRow {
            SpatialColumn {
                SpatialPanel(SubspaceModifier.height(250.dp).width(400.dp)) {
                    SpatialPanelContent("Top Left")
                }
                SpatialPanel(SubspaceModifier.height(200.dp).width(400.dp)) {
                    SpatialPanelContent("Middle Left")
                }
                SpatialPanel(SubspaceModifier.height(250.dp).width(400.dp)) {
                    SpatialPanelContent("Bottom Left")
                }
            }
            SpatialColumn {
                SpatialPanel(SubspaceModifier.height(250.dp).width(400.dp)) {
                    SpatialPanelContent("Top Right")
                }
                SpatialPanel(SubspaceModifier.height(200.dp).width(400.dp)) {
                    SpatialPanelContent("Middle Right")
                }
                SpatialPanel(SubspaceModifier.height(250.dp).width(400.dp)) {
                    SpatialPanelContent("Bottom Right")
                }
            }
        }
    }
    // [END androidxr_compose_SpatialLayoutExampleSubspace]
}

// [START androidxr_compose_SpatialLayoutExampleSpatialPanelContent]
@Composable
fun SpatialPanelContent(text: String) {
    Column(
        Modifier
            .background(color = Color.Black)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Panel",
            color = Color.White,
            fontSize = 15.sp
        )
        Text(
            text = text,
            color = Color.White,
            fontSize = 25.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
// [END androidxr_compose_SpatialLayoutExampleSpatialPanelContent]
