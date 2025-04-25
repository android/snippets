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
import androidx.xr.compose.spatial.SpatialElevation
import androidx.xr.compose.spatial.SpatialElevationLevel

@Composable
private fun ComposableThatShouldElevateInXr() {}

@Composable
private fun SpatialElevationExample() {
    // [START androidxr_compose_spatialelevation]
    // Elevate an otherwise 2D Composable (signified here by ComposableThatShouldElevateInXr).
    SpatialElevation(spatialElevationLevel = SpatialElevationLevel.Level4) {
        ComposableThatShouldElevateInXr()
    }
    // [END androidxr_compose_spatialelevation]
}
