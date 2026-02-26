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
import androidx.xr.runtime.Config
import androidx.xr.runtime.Session
import androidx.xr.runtime.SessionConfigureSuccess
import androidx.xr.runtime.math.Pose

private fun configureDevicePose(session: Session) {
    // [START androidxr_arcore_device_pose_configure]
    // Define the configuration object to enable tracking device pose.
    val newConfig = session.config.copy(
        deviceTrackingMode = Config.DeviceTrackingMode.LAST_KNOWN
    )
    // Apply the configuration to the session.
    try {
        when (val configResult = session.configure(newConfig)) {
            is SessionConfigureSuccess -> {
                // The session is now configured to track the device's pose.
            }
            else -> {
                // Catch-all for other configuration errors returned using the result class.
            }
        }
    } catch (e: UnsupportedOperationException) {
        // Handle configuration failure. For example, if the specific mode is not supported on the current device or API version.
    }
    // [END androidxr_arcore_device_pose_configure]
}

private suspend fun obtainDevicePose(session: Session) {
    // [START androidxr_arcore_device_pose_get]
    // Get the ArDevice instance
    val arDevice = ArDevice.getInstance(session)
    // There are two ways to get the device pose.

    // 1. Get the current device pose once.
    // This is the device's position and orientation relative to the tracking origin.
    val devicePose = arDevice.state.value.devicePose
    processDevicePose(devicePose)

    // 2. Continuously receive updates for the device pose.
    // `collect` is a suspending function that will run indefinitely and process new poses.
    arDevice.state.collect { state ->
        processDevicePose(state.devicePose)
    }
    // [END androidxr_arcore_device_pose_get]
}

// [START androidxr_arcore_device_pose_process]
fun processDevicePose(pose: Pose) {
    // Extract Translation and Rotation
    val translation = pose.translation // Vector3(x, y, z)
    val rotation = pose.rotation // Quaternion (x, y, z, w)
    TODO(/* Use the translation and rotation in your app. */)
}
// [END androidxr_arcore_device_pose_process]
