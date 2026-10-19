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

package com.example.android.location

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

private const val REQUESTING_LOCATION_UPDATES_KEY = "requesting_location_updates"

@SuppressLint("MissingPermission")
private class RequestLocationUpdatesActivity : ComponentActivity() {
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationRequest: LocationRequest
    private var requestingLocationUpdates = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10000)
            .setMinUpdateIntervalMillis(5000)
            .build()
    }

    // [START android_location_request_updates_start]
    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()
    }

    private fun startLocationUpdates() {
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )
    }
    // [END android_location_request_updates_start]

    // [START android_location_request_updates_callback]
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult ?: return
            for (location in locationResult.locations) {
                // Update UI with location data.
                // ...
            }
        }
    }
    // [END android_location_request_updates_callback]

    // [START android_location_request_updates_stop]
    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
    }
    // [END android_location_request_updates_stop]

    // [START android_location_request_updates_save_state]
    override fun onSaveInstanceState(outState: Bundle) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, requestingLocationUpdates)
        super.onSaveInstanceState(outState)
    }
    // [END android_location_request_updates_save_state]
}

private class RestoreLocationUpdatesStateActivity : ComponentActivity() {
    private var requestingLocationUpdates = false

    private fun startLocationUpdates() {}
    private fun updateUI() {}

    // [START android_location_request_updates_resume]
    override fun onResume() {
        super.onResume()
        if (requestingLocationUpdates) startLocationUpdates()
    }
    // [END android_location_request_updates_resume]

    // [START android_location_request_updates_restore_state]
    override fun onCreate(savedInstanceState: Bundle?) {
        // [START_EXCLUDE]
        super.onCreate(savedInstanceState)
        // [END_EXCLUDE]
        updateValuesFromBundle(savedInstanceState)
    }

    private fun updateValuesFromBundle(savedInstanceState: Bundle?) {
        savedInstanceState ?: return

        // Update the value of requestingLocationUpdates from the Bundle.
        if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
            requestingLocationUpdates = savedInstanceState.getBoolean(
                REQUESTING_LOCATION_UPDATES_KEY
            )
        }

        // ...

        // Update UI to match restored state.
        updateUI()
    }
    // [END android_location_request_updates_restore_state]
}
