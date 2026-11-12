/*
 * Copyright 2026 The Android Open Source Project
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

import androidx.xr.arcore.ArDevice
import androidx.xr.arcore.CreateGeospatialPoseFromPoseNotTracking
import androidx.xr.arcore.CreateGeospatialPoseFromPoseSuccess
import androidx.xr.arcore.CreatePoseFromGeospatialPoseInternalError
import androidx.xr.arcore.CreatePoseFromGeospatialPoseNotTracking
import androidx.xr.arcore.CreatePoseFromGeospatialPoseSuccess
import androidx.xr.arcore.Geospatial
import androidx.xr.runtime.Session
import androidx.xr.runtime.math.GeospatialPose
import androidx.xr.runtime.math.Pose

private fun convertDeviceToGeospatial(session: Session, geospatial: Geospatial) {
    // [START androidxr_arcore_geospatial_device_to_geospatial]
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

        else -> {
            // A newer exception was added, but your app is using an old version of the library
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
        is CreatePoseFromGeospatialPoseInternalError -> {
            // An internal error occurred.
        }
        else -> {
            // A newer exception was added, but your app is using an old version of the library
        }
    }
    // [END androidxr_arcore_geospatial_pose_to_device]
}
