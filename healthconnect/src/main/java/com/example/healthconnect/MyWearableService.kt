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

package com.example.healthconnect

import android.annotation.SuppressLint
import android.bluetooth.BluetoothGatt
import android.companion.CompanionDeviceService
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.ExerciseRouteResult
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.metadata.Device
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import androidx.health.connect.client.units.Length
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import java.time.ZoneOffset

@RequiresApi(Build.VERSION_CODES.S)
class MyWearableService : CompanionDeviceService() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var healthConnectClient: HealthConnectClient? = null
    private var bluetoothGatt: BluetoothGatt? = null

    override fun onDeviceAppeared(address: String) {
        super.onDeviceAppeared(address)
        healthConnectClient = HealthConnectClient.getOrCreate(this)

        serviceScope.launch {
            val granted = healthConnectClient?.permissionController?.getGrantedPermissions()

            // New logic: Read session and route
            readExerciseSessionAndRoute()

            // ... set up GATT and subscribe ...
        }

    }

    // [START android_healthconnect_read_exercise_route]
    private suspend fun readExerciseSessionAndRoute() {
        val client = healthConnectClient ?: return

        val endTime = Instant.now()
        val startTime = endTime.minus(Duration.ofHours(1))

        val grantedPermissions = client.permissionController.getGrantedPermissions()

        // 1. Verify basic Exercise Session permissions
        if (!grantedPermissions.contains(
                HealthPermission.getReadPermission(ExerciseSessionRecord::class))) {
            return
        }

        // 2. Read the sessions
        val readResponse = client.readRecords(
            ReadRecordsRequest(
                ExerciseSessionRecord::class,
                TimeRangeFilter.between(startTime, endTime)
            )
        )

        val exerciseRecord = readResponse.records.firstOrNull() ?: return
        val recordId = exerciseRecord.metadata.id

        // 3. Read the specific record to check for the route
        val sessionResponse = client.readRecord(ExerciseSessionRecord::class, recordId)
        val exerciseSessionRecord = sessionResponse.record

        // 4. Handle the Route Result
        when (val routeResult = exerciseSessionRecord.exerciseRouteResult) {
            is ExerciseRouteResult.Data -> {
                displayExerciseRoute(routeResult.exerciseRoute)
            }
            is ExerciseRouteResult.ConsentRequired -> {
                // NOTE: Since you are in a Service, you cannot launch the ActivityResultLauncher here.
                // You would typically send a notification to the user to open the app
                // and grant route-specific consent.
                handleConsentRequired(recordId)
            }
            is ExerciseRouteResult.NoData -> Unit
            else -> Unit
        }
    }

    private fun displayExerciseRoute(route: ExerciseRoute) {
        val locations = route.route.orEmpty()
        for (location in locations) {
           println(location)
        }
    }
    // [END android_healthconnect_read_exercise_route]

    private fun handleConsentRequired(recordId: String) {
        // Implementation for services: Trigger a Notification that launches an Activity
        // to call ExerciseRouteRequestContract()
    }

    @SuppressLint("RestrictedApi")
    // [START android_healthconnect_insert_exercise_route]
    private suspend fun insertExerciseRoute() {
        val client = healthConnectClient ?: return

        val grantedPermissions = client.permissionController.getGrantedPermissions()

        // 1. Verify Session Write Permission
        val hasWriteSession = grantedPermissions.contains(
            HealthPermission.getWritePermission(ExerciseSessionRecord::class)
        )
        if (!hasWriteSession) return

        val sessionStartTime = Instant.now()
        val sessionDuration = Duration.ofMinutes(20)
        val sessionEndTime = sessionStartTime.plus(sessionDuration)

        // 2. Build the route if route-specific write permission is granted
        val hasWriteRoute = grantedPermissions.contains(HealthPermission.PERMISSION_WRITE_EXERCISE_ROUTE)

        val exerciseRoute = if (hasWriteRoute) {
            ExerciseRoute(
                listOf(
                    ExerciseRoute.Location(
                        time = sessionStartTime,
                        latitude = 6.5483,
                        longitude = 0.5488,
                        horizontalAccuracy = Length.meters(2.0),
                        verticalAccuracy = Length.meters(2.0),
                        altitude = Length.meters(9.0),
                    ),
                    ExerciseRoute.Location(
                        time = sessionEndTime.minusSeconds(1),
                        latitude = 6.4578,
                        longitude = 0.6577,
                        horizontalAccuracy = Length.meters(2.0),
                        verticalAccuracy = Length.meters(2.0),
                        altitude = Length.meters(9.2),
                    )
                )
            )
        } else {
            null
        }

        // 3. Create the session record
        val exerciseSessionRecord = ExerciseSessionRecord(
            startTime = sessionStartTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = sessionEndTime,
            endZoneOffset = ZoneOffset.UTC,
            exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_BIKING,
            title = "Morning Bike Ride",
            exerciseRoute = exerciseRoute,
            metadata = Metadata(
                device = Device(type = Device.TYPE_PHONE)
            )
        )

        // 4. Insert into Health Connect
        client.insertRecords(listOf(exerciseSessionRecord))
    }
    // [END android_healthconnect_insert_exercise_route]
}
