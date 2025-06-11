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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.xr.compose.spatial.ContentEdge
import androidx.xr.compose.spatial.Orbiter
import androidx.xr.compose.spatial.OrbiterOffsetType
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.SpatialRow
import androidx.xr.compose.subspace.layout.SpatialRoundedCornerShape
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.movable
import androidx.xr.compose.subspace.layout.resizable
import androidx.xr.compose.subspace.layout.width
import com.example.xr.R

@Suppress("RestrictedApi") // b/416066566
@Composable
private fun OrbiterExampleSubspace() {
    // [START androidxr_compose_OrbiterExampleSubspace]
    Subspace {
        SpatialPanel(
            SubspaceModifier
                .height(824.dp)
                .width(1400.dp)
                .movable()
                .resizable()
        ) {
            SpatialPanelContent()
            OrbiterExample()
        }
    }
    // [END androidxr_compose_OrbiterExampleSubspace]
}

// [START androidxr_compose_OrbiterExample]
@Composable
fun OrbiterExample() {
    Orbiter(
        position = ContentEdge.Bottom,
        offset = 96.dp,
        alignment = Alignment.CenterHorizontally
    ) {
        Surface(Modifier.clip(CircleShape)) {
            Row(
                Modifier
                    .background(color = Color.Black)
                    .height(100.dp)
                    .width(600.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Orbiter",
                    color = Color.White,
                    fontSize = 50.sp
                )
            }
        }
    }
}
// [END androidxr_compose_OrbiterExample]

@Composable
@Suppress("RestrictedApi") // b/416066566
fun OrbiterAnchoringExample() {
    // [START androidxr_compose_OrbiterAnchoringExample]
    Subspace {
        SpatialRow {
            Orbiter(
                position = ContentEdge.Top,
                offset = 8.dp,
                offsetType = OrbiterOffsetType.InnerEdge,
                shape = SpatialRoundedCornerShape(size = CornerSize(50))
            ) {
                Text(
                    "Hello World!",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .background(Color.White)
                        .padding(16.dp)
                )
            }
            SpatialPanel(
                SubspaceModifier
                    .height(824.dp)
                    .width(1400.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.Red)
                )
            }
            SpatialPanel(
                SubspaceModifier
                    .height(824.dp)
                    .width(1400.dp)
            ) {
                Box(
                    modifier = Modifier
                        .background(Color.Blue)
                )
            }
        }
    }
    // [END androidxr_compose_OrbiterAnchoringExample]
}

@Composable
private fun NavigationRail() {}

@Composable
private fun Ui2DToOribiter() {
    // [START androidxr_compose_orbiter_comparison]
    // Previous approach
    NavigationRail()

    // New XR differentiated approach
    Orbiter(
        position = ContentEdge.Start,
        offset = dimensionResource(R.dimen.start_orbiter_padding),
        alignment = Alignment.Top
    ) {
        NavigationRail()
    }
    // [END androidxr_compose_orbiter_comparison]
}
