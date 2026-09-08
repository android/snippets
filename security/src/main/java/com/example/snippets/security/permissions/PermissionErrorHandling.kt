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

import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Binder
import android.os.Process
import android.util.Base64
import android.util.Log

class PermissionErrorHandling(private val context: Context) {

    // [START android_security_permission_error_handling]
    fun safeLocationAccess(locationManager: LocationManager) {
        val pm = context.packageManager
        try {
            @Suppress("DEPRECATION")
            val info = pm.getPackageInfo("com.unknown.app", PackageManager.GET_SIGNATURES)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("PERMISSION_ERROR", "Requested package information is not installed.", e)
            // Abort actions calling the target package
        }

        try {
            // Calling API requiring ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION
            val lastLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (lastLocation != null) {
                displayLocationData(lastLocation)
            }
        } catch (e: SecurityException) {
            Log.w("PERMISSION_DENIED", "Attempted to access location without GPS permission", e)
            // Fallback logic: Use a default location or guide user to enter location manually
            useDefaultLocation()
        }
    }
    // [END android_security_permission_error_handling]

    // [START android_security_caller_signature_verification]
    fun verifyCallerIdentity(trustedSha256: String) {
        val callingUid = Binder.getCallingUid()
        if (callingUid == Process.myUid()) {
            return
        }

        val pm = context.packageManager
        val packages = pm.getPackagesForUid(callingUid)
        if (packages.isNullOrEmpty()) {
            throw SecurityException("Unknown caller UID: $callingUid")
        }

        val callingPackage = packages[0]
        val trustedSha256Raw = Base64.decode(trustedSha256, Base64.DEFAULT)
        // API 28+ handles signing key lineage and avoids manual cert parsing
        val isTrusted = pm.hasSigningCertificate(
            callingPackage,
            trustedSha256Raw,
            PackageManager.CERT_INPUT_SHA256
        )

        if (!isTrusted) {
            throw SecurityException("Caller signature verification failed for $callingPackage")
        }
    }
    // [END android_security_caller_signature_verification]

    private fun displayLocationData(location: Location) {}
    private fun useDefaultLocation() {}
}
