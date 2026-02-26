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

import androidx.xr.arcore.ArDevice
import androidx.xr.arcore.CreateGeospatialPoseFromPoseNotTracking
import androidx.xr.arcore.CreateGeospatialPoseFromPoseSuccess
import androidx.xr.arcore.CreatePoseFromGeospatialPoseNotTracking
import androidx.xr.arcore.CreatePoseFromGeospatialPoseSuccess
import androidx.xr.arcore.Geospatial
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
import androidx.xr.runtime.math.GeospatialPose
import androidx.xr.runtime.math.Pose

private fun configureGeospatialSession(session: Session) {
    // [START androidxr_arcore_geospatial_configure]
    // Define the configuration object to enable Geospatial features.
    val newConfig = session.config.copy(
        // Set the GeospatialMode to VPS_AND_GPS.
        geospatialMode = GeospatialMode.VPS_AND_GPS,
        // Set the DeviceTrackingMode to LAST_KNOWN.
        deviceTrackingMode = DeviceTrackingMode.LAST_KNOWN
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

private fun obtainGeospatial(session: Session) {
    // [START androidxr_arcore_geospatial_get_instance]
    // Get the Geospatial instance for the current session.
    val geospatial = Geospatial.getInstance(session)
    // [END androidxr_arcore_geospatial_get_instance]
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
    }
    // [END androidxr_arcore_geospatial_check_vps]
}

private fun convertDeviceToGeospatial(session: Session, geospatial: Geospatial) {
    // [START androidxr_arcore_geospatial_device_to_geospatial]
    // Get the current device Pose from the AR Session's state.
    val devicePose = ArDevice.getInstance(session).state.value.devicePose

    // Convert the device Pose into a GeospatialPose.
    when (val result = geospatial.createGeospatialPoseFromPose(devicePose)) {
        is CreateGeospatialPoseFromPoseSuccess -> {
            val geoPose = result.pose
            val lat = geoPose.latitude
            val lon = geoPose.longitude
            val alt = geoPose.altitude
            // Orientation is in the EUS (East-Up-South) coordinate system.
            val orientation = geoPose.eastUpSouthQuaternion
        }
        is CreateGeospatialPoseFromPoseNotTracking -> {
            // Geospatial is not currently tracking.
        }
    }
    // [END androidxr_arcore_geospatial_device_to_geospatial]
}

private fun convertGeospatialToDevice(geospatial: Geospatial, geoPose: GeospatialPose) {
    // [START androidxr_arcore_geospatial_pose_to_device]
    // Convert a GeospatialPose (lat/long/alt) back to a device-space Pose.
    when (val result = geospatial.createPoseFromGeospatialPose(geoPose)) {
        is CreatePoseFromGeospatialPoseSuccess -> {
            val devicePose: Pose = result.pose
            // devicePose is now ready to be used relative to the tracking origin.
        }
        is CreatePoseFromGeospatialPoseNotTracking -> {
            // Geospatial is not currently tracking.
        }
    }
    // [END androidxr_arcore_geospatial_pose_to_device]
}
