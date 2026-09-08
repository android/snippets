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

package com.example.snippets.security.permissions

import android.Manifest
import androidx.activity.ComponentActivity
import androidx.activity.result.contract.ActivityResultContracts

// [START android_security_sequential_location_permission]
class LocationPermissionActivity : ComponentActivity() {

    // Step 1: Register launcher for ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION
    private val foregroundLocationLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val fineLocationGranted =
                permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false)
            val coarseLocationGranted =
                permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false)

            if (fineLocationGranted || coarseLocationGranted) {
                // Foreground location access granted. Now request background location in a distinct step.
                requestBackgroundLocation()
            } else {
                onForegroundLocationDenied()
            }
        }

    // Step 2: Register a separate launcher for ACCESS_BACKGROUND_LOCATION
    private val backgroundLocationLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                onBackgroundLocationGranted()
            } else {
                onBackgroundLocationDenied()
            }
        }

    // Request foreground permissions first
    fun requestForegroundLocation() {
        foregroundLocationLauncher.launch(
            arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
        )
    }

    // Request background location only after foreground permissions have been granted
    fun requestBackgroundLocation() {
        backgroundLocationLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
    }

    private fun onForegroundLocationDenied() {
        // Handle foreground location denial
    }

    private fun onBackgroundLocationGranted() {
        // Start background location updates
    }

    private fun onBackgroundLocationDenied() {
        // Handle background location denial; proceed with foreground-only features
    }
}
// [END android_security_sequential_location_permission]
