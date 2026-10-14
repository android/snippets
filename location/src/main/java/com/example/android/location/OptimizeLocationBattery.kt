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

import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.Priority

@Suppress("unused", "UNUSED_VARIABLE")
fun createBatchedLocationRequest() {
    // [START android_location_battery_optimize_batch]
    val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 10 * 60 * 1000)
        .setMaxUpdateDelayMillis(60 * 60 * 1000)
        .build()
    // [END android_location_battery_optimize_batch]
}

@Suppress("unused", "UNUSED_VARIABLE")
fun createPassiveLocationRequest() {
    // [START android_location_battery_optimize_passive]
    val request = LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY, 15 * 60 * 1000)
        .setMinUpdateIntervalMillis(2 * 60 * 1000)
        .build()
    // [END android_location_battery_optimize_passive]
}
