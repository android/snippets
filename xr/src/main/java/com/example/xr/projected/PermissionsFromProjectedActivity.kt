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
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.xr.projected.ProjectedActivityCompat
import androidx.xr.projected.experimental.ExperimentalProjectedApi
import kotlinx.coroutines.launch

/**
 * Demonstrates how to request glasses-specific hardware permissions, such as
 * the glasses camera, from a Projected Activity.
 */
@OptIn(ExperimentalProjectedApi::class)
class PermissionsFromProjectedActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            checkAndRequestCameraPermission()
        }
    }

    // [START androidxr_projected_permissions_permission_result_callback]
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray,
        deviceId: Int,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults, deviceId)
        if (requestCode != CAMERA_PERMISSION_REQUEST_CODE) return

        val isCameraGranted =
            grantResults.getOrNull(permissions.indexOf(Manifest.permission.CAMERA)) ==
                PackageManager.PERMISSION_GRANTED

        if (isCameraGranted) {
            initializeCameraFeatures()
        } else {
            onCameraPermissionDenied()
        }
    }
    // [END androidxr_projected_permissions_permission_result_callback]

    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun checkAndRequestCameraPermission() {
        // [START androidxr_projected_permissions_from_projected_activity_has_permission]
        val hasCameraPermission = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.CAMERA,
        ) == PackageManager.PERMISSION_GRANTED
        // [END androidxr_projected_permissions_from_projected_activity_has_permission]

        if (hasCameraPermission) {
            initializeCameraFeatures()
        } else {
            requestCameraPermission()
        }
    }

    // [START androidxr_projected_permissions_from_projected_activity_request_permission]
    @RequiresApi(Build.VERSION_CODES.VANILLA_ICE_CREAM)
    private fun requestCameraPermission() {
        lifecycleScope.launch {
            try {
                ProjectedActivityCompat.requestPermissions(
                    this@PermissionsFromProjectedActivity,
                    arrayOf(Manifest.permission.CAMERA),
                    // CAMERA_PERMISSION_REQUEST_CODE is a developer defined constant
                    CAMERA_PERMISSION_REQUEST_CODE,
                )
            } catch (e: IllegalStateException) {
                // Thrown when the projected system service can't be bound.
                Log.e(TAG, "Camera permission request failed: projected service unavailable.", e)
            }
        }
    }
    // [END androidxr_projected_permissions_from_projected_activity_request_permission]

    private fun initializeCameraFeatures() {
        // Start glasses camera features here.
    }

    private fun onCameraPermissionDenied() {
        // Turn off camera features and tell the user why on the glasses display.
    }

    // [START androidxr_projected_permissions_from_projected_activity_constants]
    private companion object {
        const val CAMERA_PERMISSION_REQUEST_CODE = 1002
        const val TAG = "ProjectedPermissions"
    }
    // [END androidxr_projected_permissions_from_projected_activity_constants]
}
