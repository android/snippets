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

package com.example.tv

import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import java.io.IOException

private fun stopPlayback() {}

class HardwareSnippetsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun checkDeviceType() {
        // [START android_tv_check_device_type]
        val TAG = "DeviceTypeRuntimeCheck"
        
        val isTelevision = packageManager.hasSystemFeature(PackageManager.FEATURE_LEANBACK)
        if (isTelevision) {
            Log.d(TAG, "Running on a TV Device")
        } else {
            Log.d(TAG, "Running on a non-TV Device")
        }
        // [END android_tv_check_device_type]
    }

    fun checkFeatures() {
        // [START android_tv_check_features]
        // Check whether the telephony hardware feature is available.
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_TELEPHONY)) {
            Log.d("HardwareFeatureTest", "Device can make phone calls")
        }
        
        // Check whether android.hardware.touchscreen feature is available.
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_TOUCHSCREEN)) {
            Log.d("HardwareFeatureTest", "Device has a touchscreen.")
        }
        // [END android_tv_check_features]
    }

    fun cameraTest() {
        // [START android_tv_camera_test]
        // Check whether the camera hardware feature is available.
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            Log.d("Camera test", "Camera available!")
        } else {
            Log.d("Camera test", "No camera available. View and edit features only.")
        }
        // [END android_tv_camera_test]
    }

    fun requestStaticLocation() {
        // [START android_tv_gps_static_location]
        // Request a static location from the location manager.
        val locationManager = this.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val location: Location = locationManager.getLastKnownLocation("static")
        
        // Attempt to get postal code from the static location object.
        val geocoder = Geocoder(this)
        val address: Address? =
                try {
                    geocoder.getFromLocation(location.latitude, location.longitude, 1)[0]
                            .apply {
                                Log.d(TAG, postalCode)
                            }
                } catch (e: IOException) {
                    Log.e(TAG, "Geocoder error", e)
                    null
                }
        // [END android_tv_gps_static_location]
    }

    // [START android_tv_pause_playback]
    override fun onStop() {
        // App-specific method to stop playback.
        stopPlayback()
        super.onStop()
    }
    // [END android_tv_pause_playback]
}
