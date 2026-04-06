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

package com.example.tv.snippets

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.util.Log
import java.io.IOException

class HardwareActivity : Activity() {
    val TAG = "HardwareActivity"

    fun checkTvDevice() {
        // [START android_tv_hardware_check_tv]
        val isTelevision = packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
        if (isTelevision) {
            Log.d(TAG, "Running on a TV Device")
        } else {
            Log.d(TAG, "Running on a non-TV Device")
        }
        // [END android_tv_hardware_check_tv]
    }

    fun checkHardwareFeatures() {
        // [START android_tv_hardware_check_feature]
        // Check whether the telephony hardware feature is available.
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            Log.d("HardwareFeatureTest", "Device can make phone calls")
        }

        // Check whether android.hardware.touchscreen feature is available.
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)) {
            Log.d("HardwareFeatureTest", "Device has a touchscreen.")
        }
        // [END android_tv_hardware_check_feature]
    }

    fun checkCamera() {
        // [START android_tv_hardware_check_camera]
        // Check whether the camera hardware feature is available.
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.d("Camera test", "Camera available!")
        } else {
            Log.d("Camera test", "No camera available. View and edit features only.")
        }
        // [END android_tv_hardware_check_camera]
    }

    @SuppressLint("MissingPermission")
    fun gpsStaticLocation() {
        // [START android_tv_hardware_gps_static]
        // Request a static location from the location manager.
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location = locationManager.getLastKnownLocation("static")
        if (location == null) {
            Log.e(TAG, "Location is null")
            return
        }

        // Attempt to get postal code from the static location object.
        val geocoder = Geocoder(this)
        val address: Address? =
                try {
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)?.firstOrNull()
                            ?.apply {
                                Log.d(TAG, postalCode)
                            }
                } catch (e: IOException) {
                    Log.e(TAG, "Geocoder error", e)
                    null
                }
        // [END android_tv_hardware_gps_static]
    }
}
