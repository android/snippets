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

package com.example.xr.arcore.geospatial

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.xr.arcore.ArDevice
import androidx.xr.arcore.CreateGeospatialPoseFromPoseNotTracking
import androidx.xr.arcore.CreateGeospatialPoseFromPoseSuccess
import androidx.xr.arcore.CreatePoseFromGeospatialPoseInternalError
import androidx.xr.arcore.CreatePoseFromGeospatialPoseNotTracking
import androidx.xr.arcore.CreatePoseFromGeospatialPoseSuccess
import androidx.xr.arcore.Geospatial
import androidx.xr.arcore.VpsAvailabilityAvailable
import androidx.xr.arcore.VpsAvailabilityErrorInternal
import androidx.xr.arcore.VpsAvailabilityNetworkError
import androidx.xr.arcore.VpsAvailabilityNotAuthorized
import androidx.xr.arcore.VpsAvailabilityResourceExhausted
import androidx.xr.arcore.VpsAvailabilityUnavailable
import androidx.xr.runtime.Config
import androidx.xr.runtime.GeospatialMode
import androidx.xr.runtime.Session
import androidx.xr.runtime.SessionConfigureSuccess
import androidx.xr.runtime.math.GeospatialPose
import androidx.xr.runtime.math.Pose
import kotlinx.coroutines.launch

private fun configureGeospatialSession(session: Session) {
    // [START androidxr_arcore_geospatial_configure]
    // Define the configuration object to enable Geospatial features.
    val newConfig = Config.Builder(session.config)
        .setGeospatial(GeospatialMode.SPATIAL)
        .build()
    // Apply the configuration to the session.
    try {
        when (val configResult = session.configure(newConfig)) {
            is SessionConfigureSuccess -> {
                // The session is now configured to use the Geospatial API.
            }

            else -> {
                // Handle other configuration errors (e.g., missing library dependencies).
            }
        }
    } catch (e: UnsupportedOperationException) {
        // Handle configuration failure if the mode is not supported.
    }
    // [END androidxr_arcore_geospatial_configure]
}

@Suppress("ControlFlowWithEmptyBody")
@Composable
private fun CheckGeospatialStateRunning(session: Session) {
    // [START androidxr_arcore_geospatial_compose_check_geospatial_state]
    val geospatial = remember { Geospatial.getInstance(session) }
    val geospatialState by geospatial.state.collectAsStateWithLifecycle()
    if (geospatialState.geospatialTrackingState == Geospatial.GeospatialTrackingState.RUNNING) {
        // Queries to the Geospatial API are only valid when the state is RUNNING.
    }
    // [END androidxr_arcore_geospatial_compose_check_geospatial_state]
}

@Suppress("ControlFlowWithEmptyBody")
private fun ComponentActivity.checkGeospatialStateRunning(session: Session) {
    // [START androidxr_arcore_geospatial_scenecore_check_geospatial_state]
    val geospatial = Geospatial.getInstance(session)
    lifecycleScope.launch {
        geospatial.state.collect { geospatialState ->
            if (geospatialState == Geospatial.GeospatialTrackingState.RUNNING) {
                // Queries to the Geospatial API are only valid when the state is RUNNING.
            }
        }
    }
    // [END androidxr_arcore_geospatial_scenecore_check_geospatial_state]
}

private suspend fun checkVpsAvailability(geospatial: Geospatial) {
    // [START androidxr_arcore_geospatial_check_vps]
    // You can query the GPS to get the current device's location.
    val latitude = 37.422
    val longitude = -122.084

    // Use the geospatial instance to check VPS availability for a specific location.
    val result = geospatial.checkVpsAvailability(latitude, longitude)
    when (result) {
        is VpsAvailabilityAvailable -> {
            // VPS is available at this location.
        }
        is VpsAvailabilityErrorInternal -> {
            // VPS availability check failed with an internal error.
        }
        is VpsAvailabilityNetworkError -> {
            // VPS availability check failed due to a network error.
        }
        is VpsAvailabilityNotAuthorized -> {
            // VPS availability check failed due to an authorization error.
        }
        is VpsAvailabilityResourceExhausted -> {
            // VPS availability check failed due to resource exhaustion.
        }
        is VpsAvailabilityUnavailable -> {
            // VPS is not available at this location.
        }
        else -> {
            // A newer exception was added, but your app is using an old version of the library
        }
    }
    // [END androidxr_arcore_geospatial_check_vps]
}
