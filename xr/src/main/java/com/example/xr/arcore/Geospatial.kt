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

package com.example.xr.arcore

import android.content.Context
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import androidx.xr.arcore.AnchorCreateResult
import androidx.xr.arcore.AnchorCreateSuccess
import androidx.xr.arcore.ArDevice
import androidx.xr.arcore.CreateGeospatialPoseFromPoseNotTracking
import androidx.xr.arcore.CreateGeospatialPoseFromPoseSuccess
import androidx.xr.arcore.CreatePoseFromGeospatialPoseNotTracking
import androidx.xr.arcore.CreatePoseFromGeospatialPoseSuccess
import androidx.xr.arcore.Geospatial
import androidx.xr.compose.spatial.ExperimentalFollowingSubspaceApi
import androidx.xr.compose.spatial.FollowingSubspace
import androidx.xr.compose.subspace.FollowBehavior
import androidx.xr.compose.subspace.FollowTarget
import androidx.xr.compose.subspace.SpatialPanel
import androidx.xr.compose.subspace.SubspaceComposable
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.size
import androidx.xr.runtime.Config
import androidx.xr.runtime.DeviceTrackingMode
import androidx.xr.runtime.GeospatialMode
import androidx.xr.runtime.Session
import androidx.xr.runtime.SessionConfigureSuccess
import androidx.xr.runtime.VpsAvailabilityAvailable
import androidx.xr.runtime.VpsAvailabilityErrorInternal
import androidx.xr.runtime.VpsAvailabilityNetworkError
import androidx.xr.runtime.VpsAvailabilityNotAuthorized
import androidx.xr.runtime.VpsAvailabilityResourceExhausted
import androidx.xr.runtime.VpsAvailabilityUnavailable
import androidx.xr.runtime.math.FloatSize2d
import androidx.xr.runtime.math.GeospatialPose
import androidx.xr.runtime.math.IntSize2d
import androidx.xr.runtime.math.Pose
import androidx.xr.runtime.math.Quaternion
import androidx.xr.scenecore.AnchorEntity
import androidx.xr.scenecore.PanelEntity
import kotlinx.coroutines.launch

private fun configureGeospatialSession(session: Session) {
    // [START androidxr_arcore_geospatial_configure]
    // Define the configuration object to enable Geospatial features.
    val newConfig = Config(
        // Set the GeospatialMode to VPS_AND_GPS.
        geospatial = GeospatialMode.VPS_AND_GPS,
        // Set the DeviceTrackingMode to LAST_KNOWN.
        deviceTracking = DeviceTrackingMode.LAST_KNOWN
    )
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

private suspend fun checkVpsAvailability(geospatial: Geospatial) {
    // [START androidxr_arcore_geospatial_check_vps]
    val vpsAvailability = geospatial.checkVpsAvailability(37.4210545, -122.0853712)
    when (vpsAvailability) {
        is VpsAvailabilityAvailable -> {
            // VPS is available at this location.
        }

        is VpsAvailabilityUnavailable -> {
            // VPS is not available at this location.
        }

        else -> {
            // Something went wrong when querying the availability at this location.
            // See `vpsAvailability` to obtain a reason and the troubleshooting guide.
        }
    }
    // [END androidxr_arcore_geospatial_check_vps]
}

private fun convertDeviceToGeospatial(session: Session, geospatial: Geospatial, pose: Pose) {
    // [START androidxr_arcore_geospatial_device_to_geospatial]
    when (val result = geospatial.createGeospatialPoseFromPose(pose)) {
        is CreateGeospatialPoseFromPoseSuccess -> {
            val geospatialPose = result.pose
        }

        is CreateGeospatialPoseFromPoseNotTracking -> {
            // `geospatial.state.value` is not Geospatial.State.RUNNING.
            // This could be caused by a configuration failure; see the Troubleshooting guide.
        }
    }
    // [END androidxr_arcore_geospatial_device_to_geospatial]
}

private fun convertGeospatialToDeviceLocalPose(
    geospatial: Geospatial,
    geospatialPose: GeospatialPose
) {
    // [START androidxr_arcore_geospatial_pose_to_device]
    // Convert a GeospatialPose back to a device-local Pose.
    when (val result = geospatial.createPoseFromGeospatialPose(geospatialPose)) {
        is CreatePoseFromGeospatialPoseSuccess -> {
            val pose = result.pose
        }

        is CreatePoseFromGeospatialPoseNotTracking -> {
            // `geospatial.state.value` is not Geospatial.State.RUNNING.
            // This could be caused by a configuration failure; see the Troubleshooting guide.
        }
    }
    // [END androidxr_arcore_geospatial_pose_to_device]
}

private fun convertDevicePoseToGeospatialPose(session: Session, geospatial: Geospatial) {
    // [START androidxr_arcore_geospatial_device_pose_to_geospatial_pose]
    val pose = ArDevice.getInstance(session).state.value.devicePose
    when (val result = geospatial.createGeospatialPoseFromPose(pose)) {
        is CreateGeospatialPoseFromPoseSuccess -> {
            val geospatialPose = result.pose
            Log.i("Geospatial", "Device is at ${geospatialPose.latitude}, ${geospatialPose.longitude}, ${geospatialPose.altitude}")
            Log.i("Geospatial", "Device orientation is ${geospatialPose.eastUpSouthQuaternion}")
        }
        is CreateGeospatialPoseFromPoseNotTracking -> {
            // Geospatial is not currently tracking
        }
    }
    // [END androidxr_arcore_geospatial_device_pose_to_geospatial_pose]
}

@Composable
private fun CheckGeospatialStateRunning(session: Session) {
    // [START androidxr_arcore_geospatial_compose_check_geospatial_state]
    val geospatial = remember { Geospatial.getInstance(session) }
    val geospatialState by geospatial.state.collectAsStateWithLifecycle()
    if (geospatialState == Geospatial.State.RUNNING) {
        // Queries to the Geospatial API are only valid when the state is RUNNING.
    }
    // [END androidxr_arcore_geospatial_compose_check_geospatial_state]
}

private fun ComponentActivity.checkGeospatialStateRunning(session: Session) {
    // [START androidxr_arcore_geospatial_scenecore_check_geospatial_state]
    val geospatial = Geospatial.getInstance(session)
    lifecycleScope.launch {
        geospatial.state.collect { geospatialState ->
            if (geospatialState == Geospatial.State.RUNNING) {
                // Queries to the Geospatial API are only valid when the state is RUNNING.
            }
        }
    }
    // [END androidxr_arcore_geospatial_scenecore_check_geospatial_state]
}

private fun createAnchor(
    geospatial: Geospatial,
    latitude: Double,
    longitude: Double,
    altitude: Double
) {
    // [START androidxr_arcore_geospatial_createAnchor]
    val createAnchorResult = geospatial.createAnchor(
        latitude,
        longitude,
        altitude,
        Quaternion.Identity,
    )
    // [END androidxr_arcore_geospatial_createAnchor]
}

private suspend fun createAnchorOnSurface(
    geospatial: Geospatial,
    latitude: Double,
    longitude: Double,
    altitude: Double
) {
    // [START androidxr_arcore_geospatial_createAnchorOnSurface]
    val createAnchorResult = geospatial.createAnchorOnSurface(
        latitude,
        longitude,
        altitude,
        Quaternion.Identity,
        Geospatial.Surface.TERRAIN,
    )
    // [END androidxr_arcore_geospatial_createAnchorOnSurface]
}

private fun checkAnchorCreateResult(createAnchorResult: AnchorCreateResult) {
    // [START androidxr_arcore_geospatial_checkAnchorCreateResult]
    when (createAnchorResult) {
        is AnchorCreateSuccess -> {
            // Use anchorCreateResult.anchor to render content at the result.
        }

        else -> {
            // Anchor create failed.
            // See AnchorCreateResult for possible failure reasons.
        }
    }
    // [END androidxr_arcore_geospatial_checkAnchorCreateResult]
}

@OptIn(ExperimentalFollowingSubspaceApi::class)
@Composable
private fun UseGeospatialAnchorCompose(session: Session, anchorCreateResult: AnchorCreateSuccess) {
    // [START androidxr_arcore_geospatial_useAnchorCreateResult_compose]
    val anchorEntity = remember { AnchorEntity.create(session, anchorCreateResult.anchor) }
    FollowingSubspace(FollowTarget.Anchor(anchorEntity), FollowBehavior.Tight) {
        GeospatialContent()
    }
    // [END androidxr_arcore_geospatial_useAnchorCreateResult_compose]
}

private fun useGeospatialAnchorSceneCore(
    context: ComponentActivity,
    session: Session,
    anchorCreateResult: AnchorCreateSuccess
) {
    // [START androidxr_arcore_geospatial_useAnchorCreateResult_scenecore]
    val geospatialContent = createGeospatialContentEntity(context, session)

    val anchorEntity = AnchorEntity.create(session, anchorCreateResult.anchor)
    anchorEntity.addChild(geospatialContent)
    // [END androidxr_arcore_geospatial_useAnchorCreateResult_scenecore]
}

@Composable
@SubspaceComposable
private fun GeospatialContent(subspaceModifier: SubspaceModifier = SubspaceModifier.size(500.dp)) {
    SpatialPanel(subspaceModifier) {
        Box(
            Modifier
                .background(Color.White)
                .fillMaxSize()
        )
    }
}

private fun createGeospatialContentEntity(
    context: ComponentActivity,
    session: Session
): PanelEntity {
    val panelContent = ComposeView(context).apply {
        setViewTreeLifecycleOwner(context)
        setViewTreeSavedStateRegistryOwner(context)
    }
    return PanelEntity.create(
        session = session,
        view = panelContent,
        pixelDimensions = IntSize2d(500, 500),
        name = "panel entity"
    )
}
