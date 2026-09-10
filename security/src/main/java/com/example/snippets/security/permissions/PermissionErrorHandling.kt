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
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.content.ContextCompat

class PermissionErrorHandling : ComponentActivity() {

    private val locationManager: LocationManager by lazy {
        getSystemService(Context.LOCATION_SERVICE) as LocationManager
    }

    // [START android_security_permission_error_handling]
    fun performLocationAccess() {
        val fine = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
        if (fine != PackageManager.PERMISSION_GRANTED && coarse != PackageManager.PERMISSION_GRANTED) {
            requestForegroundLocation()
            return
        }

        try {
            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            processLocation(location)
        } catch (e: SecurityException) {
            Log.e("LocationAccess", "Permission revoked at runtime", e)
        }
    }
    // [END android_security_permission_error_handling]

    fun safeLocationAccess(locationManager: LocationManager) {
        performLocationAccess()
    }

    fun verifyCallerIdentity(trustedSha256: String) {
        if (!CallerVerifier.isCallerAuthorized(this)) {
            throw SecurityException("Caller signature verification failed")
        }
    }

    private fun requestForegroundLocation() {}
    private fun processLocation(location: Location?) {}
}
