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

import android.content.IntentSender
import androidx.activity.ComponentActivity
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.Priority
import com.google.android.gms.location.SettingsClient
import com.google.android.gms.tasks.Task
import kotlin.time.Duration.Companion.seconds

private const val REQUEST_CHECK_SETTINGS = 1

private class MainActivity : ComponentActivity() {

    // [START android_location_change_location_settings_create_request]
    private fun createLocationRequest() {
        val locationRequest = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10.seconds.inWholeMilliseconds)
            .setMinUpdateIntervalMillis(5.seconds.inWholeMilliseconds)
            .build()
    }
    // [END android_location_change_location_settings_create_request]

    private fun addLocationRequest(locationRequest: LocationRequest) {
        // [START android_location_change_location_settings_add_request]
        val builder = LocationSettingsRequest.Builder()
            .addLocationRequest(locationRequest)
        // [END android_location_change_location_settings_add_request]
    }

    private fun checkLocationSettings() {
        // [START android_location_change_location_settings_check]
        val builder = LocationSettingsRequest.Builder()

        // ...

        val client: SettingsClient = LocationServices.getSettingsClient(this)
        val task: Task<LocationSettingsResponse> = client.checkLocationSettings(builder.build())
        // [END android_location_change_location_settings_check]

        // [START android_location_change_location_settings_prompt]
        task.addOnSuccessListener { locationSettingsResponse ->
            // All location settings are satisfied. The client can initialize
            // location requests here.
            // ...
        }

        task.addOnFailureListener { exception ->
            if (exception is ResolvableApiException) {
                // Location settings are not satisfied, but this can be fixed
                // by showing the user a dialog.
                try {
                    // Show the dialog by calling startResolutionForResult(),
                    // and check the result in onActivityResult().
                    exception.startResolutionForResult(
                        this@MainActivity,
                        REQUEST_CHECK_SETTINGS
                    )
                } catch (sendEx: IntentSender.SendIntentException) {
                    // Ignore the error.
                }
            }
        }
        // [END android_location_change_location_settings_prompt]
    }
}
