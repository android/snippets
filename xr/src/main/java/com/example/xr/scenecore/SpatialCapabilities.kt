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

package com.example.xr.scenecore

import androidx.xr.runtime.Session
import androidx.xr.scenecore.SpatialCapabilities
import androidx.xr.scenecore.scene

@Suppress("RestrictedApi") // b/416066566
fun checkMultipleCapabilities(xrSession: Session) {
    // [START androidxr_compose_checkMultipleCapabilities]
    // Example 1: check if enabling passthrough mode is allowed
    if (xrSession.scene.spatialCapabilities.hasCapability(
            SpatialCapabilities.SPATIAL_CAPABILITY_PASSTHROUGH_CONTROL
        )
    ) {
        xrSession.scene.spatialEnvironment.preferredPassthroughOpacity = 1f
    }
    // Example 2: multiple capability flags can be checked simultaneously:
    if (xrSession.scene.spatialCapabilities.hasCapability(
            SpatialCapabilities.SPATIAL_CAPABILITY_PASSTHROUGH_CONTROL and
                SpatialCapabilities.SPATIAL_CAPABILITY_3D_CONTENT
        )
    ) {
        // ...
    }
    // [END androidxr_compose_checkMultipleCapabilities]
}
