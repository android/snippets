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

package com.example.xr.projected

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.xr.projected.ProjectedContext
import androidx.xr.projected.experimental.ExperimentalProjectedApi

/**
 * Demonstrates how to request glasses-specific hardware permissions
 * from an Activity running on the host phone.
 */
class PermissionsFromPhoneActivity : ComponentActivity() {

    private companion object {
        const val REQUEST_CODE_GLASSES_CAMERA = 1001
    }

    // [START androidxr_projected_permissions_from_phone_activity_permission_result_callback]
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        deviceId: Int
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)

        // Verify both the request code and that the result belongs to the glasses device
        if (requestCode == REQUEST_CODE_GLASSES_CAMERA) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Proceed with glasses camera features
            } else {
                // Handle glasses permission denied
            }
        }
    }
    // [END androidxr_projected_permissions_from_phone_activity_permission_result_callback]

    @OptIn(ExperimentalProjectedApi::class)
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun checkAndRequestProjectedCameraPermission() {

        try {
            val projectedContext = ProjectedContext.createProjectedDeviceContext(this)
            val projectedDeviceId = projectedContext.deviceId

            // [START androidxr_projected_permissions_from_phone_activity_has_permission]
            // Pass the projected context to check if projected permissions are granted
            val hasPermission = ContextCompat.checkSelfPermission(
                projectedContext,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
            // [END androidxr_projected_permissions_from_phone_activity_has_permission]

            if (hasPermission) {
                // Proceed with camera features
            } else {
                // [START androidxr_projected_permissions_from_phone_activity_request_permission]
                // Request the projected permission from phone activity
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CODE_GLASSES_CAMERA,
                    projectedDeviceId
                )
                // [END androidxr_projected_permissions_from_phone_activity_request_permission]
            }
        } catch (e: IllegalStateException) {
            // Handle context initialization failure safely
        }
    }
}
