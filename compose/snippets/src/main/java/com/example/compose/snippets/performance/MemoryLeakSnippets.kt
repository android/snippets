/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.compose.snippets.performance

import android.annotation.SuppressLint
import android.location.LocationListener
import android.location.LocationManager
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@SuppressLint("MissingPermission")
// [START android_compose_performance_memory_leak_location_with_leak]
@Composable
fun LocationScreenWithLeak(locationManager: LocationManager) {
    var locationText by remember { mutableStateOf("Locating...") }

    // This registers the listener when entering composition, but leaves it attached to the OS when leaving
    LaunchedEffect(locationManager) {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1f) { location ->
            locationText = "Lat: ${location.latitude}, Lng: ${location.longitude}"
        }
    }

    Text(text = locationText)
}
// [END android_compose_performance_memory_leak_location_with_leak]

@SuppressLint("MissingPermission")
// [START android_compose_performance_memory_leak_location_recommended]
@Composable
fun LocationScreenRecommended(locationManager: LocationManager) {
    var locationText by remember { mutableStateOf("Locating...") }

    // DisposableEffect provides an onDispose block for mandatory cleanup
    DisposableEffect(locationManager) {
        val listener = LocationListener { location ->
            locationText = "Lat: ${location.latitude}, Lng: ${location.longitude}"
        }

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1f, listener)

        // Automatically executed when this Composable leaves the screen
        onDispose {
            locationManager.removeUpdates(listener)
        }
    }

    Text(text = locationText)
}
// [END android_compose_performance_memory_leak_location_recommended]
