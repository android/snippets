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
import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.HealthConnectFeatures
import androidx.health.connect.client.feature.ExperimentalFeatureAvailabilityApi
import androidx.health.connect.client.records.DistanceRecord
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.MindfulnessSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.metadata.Device
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.ZoneOffset
import androidx.health.connect.client.HealthConnectFeatures.Companion.FEATURE_MINDFULNESS_SESSION
import androidx.health.connect.client.records.metadata.DataOrigin
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtilLight.isGooglePlayServicesAvailable
import com.google.android.gms.fitness.LocalRecordingClient
import java.time.Duration

class HealthConnectManager(
    private val healthConnectClient: HealthConnectClient,
) {

    suspend fun insertSteps(startTime: Instant, endTime: Instant) {
        // [START android_healthconnect_insert_steps]
        val zoneOffset = ZoneOffset.systemDefault().rules.getOffset(startTime)
        val stepsRecord = StepsRecord(
            count = 120,
            startTime = startTime,
            endTime = endTime,
            startZoneOffset = zoneOffset,
            endZoneOffset = zoneOffset,
            metadata = Metadata(
                device = Device(type = Device.TYPE_WATCH),
                recordingMethod = Metadata.RECORDING_METHOD_AUTOMATICALLY_RECORDED
            )
        )
        healthConnectClient.insertRecords(listOf(stepsRecord))
        // [END android_healthconnect_insert_steps]
    }

    // [START android_healthconnect_read_steps_aggregate]
    suspend fun readStepsAggregate(startTime: Instant, endTime: Instant): Long {
        val response = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        return response[StepsRecord.COUNT_TOTAL] ?: 0L
    }
    // [END android_healthconnect_read_steps_aggregate]

    // [START android_healthconnect_read_distance_aggregate]
    suspend fun readDistanceAggregate(startTime: Instant, endTime: Instant): Number {
        val response = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(DistanceRecord.DISTANCE_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        return response[DistanceRecord.DISTANCE_TOTAL]?.inMeters ?: 0L
    }
    // [END android_healthconnect_read_distance_aggregate]

    // [START android_healthconnect_read_heart_rate_aggregate]
    suspend fun readHeartRateAggregate(startTime: Instant, endTime: Instant): Pair<Long, Long> {
        val response = healthConnectClient.aggregate(
            AggregateRequest(
                metrics = setOf(HeartRateRecord.BPM_MAX, HeartRateRecord.BPM_MIN),
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        val minimumHeartRate = response[HeartRateRecord.BPM_MIN] ?: 0L
        val maximumHeartRate = response[HeartRateRecord.BPM_MAX] ?: 0L

        return maximumHeartRate to minimumHeartRate
    }
    // [END android_healthconnect_read_heart_rate_aggregate]

    // [START delete_exercise_session_by_time_range]
    suspend fun deleteExerciseSessionByTimeRange(
        healthConnectClient: HealthConnectClient,
        exerciseRecord: ExerciseSessionRecord,
    ) {
        val timeRangeFilter = TimeRangeFilter.between(exerciseRecord.startTime, exerciseRecord.endTime)
        healthConnectClient.deleteRecords(ExerciseSessionRecord::class, timeRangeFilter)
        // delete the associated distance record
        healthConnectClient.deleteRecords(DistanceRecord::class, timeRangeFilter)
    }
    // [END delete_exercise_session_by_time_range]

    // [START delete_exercise_session_by_uid]
    suspend fun deleteExerciseSessionByUid(
        healthConnectClient: HealthConnectClient,
        exerciseRecord: ExerciseSessionRecord,
    ) {
        healthConnectClient.deleteRecords(
            ExerciseSessionRecord::class,
            recordIdsList = listOf(exerciseRecord.metadata.id),
            clientRecordIdsList = emptyList()
        )
    }
    // [END delete_exercise_session_by_uid]


    suspend fun stepsRecordMetadata(
        stepsRecord: StepsRecord,
    ) {
        // [START steps_record_metadata]
         StepsRecord(
            startTime = Instant.ofEpochMilli(1234L),
            startZoneOffset = null,
            endTime = Instant.ofEpochMilli(1236L),
            endZoneOffset = null,
            metadata = Metadata(),
            count = 10
        )
        // [END steps_record_metadata]
    }

    suspend fun playServiceCheck(context: Context) {
        // [START play_services_check]
        val hasMinPlayServices = isGooglePlayServicesAvailable(context, LocalRecordingClient.LOCAL_RECORDING_CLIENT_MIN_VERSION_CODE)

        if(hasMinPlayServices != ConnectionResult.SUCCESS) {
            // Prompt user to update their device's Google Play services app and return
        }
        // [END play_services_check]
    }

    suspend fun readHistoricalData(startTime: Instant, endTime: Instant) {
        // [START read_historical_data_example]
        try {
            val response =  healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = HeartRateRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    dataOriginFilter = setOf(DataOrigin("com.my.package.name"))
                )
            )
            for (record in response.records) {
                // Process each record
            }
        } catch (e: Exception) {
            // Run error handling here
        }
        // [END read_historical_data_example]
    }

    suspend fun pageToken() {
        // [START pagetoken_read_example]
        val type = HeartRateRecord::class
        val endTime = Instant.now()
        val startTime = endTime.minus(Duration.ofDays(7))

        try {
            var pageToken: String? = null
            do {
                val readResponse =
                    healthConnectClient.readRecords(
                        ReadRecordsRequest(
                            recordType = type,
                            timeRangeFilter = TimeRangeFilter.between(
                                startTime,
                                endTime
                            ),
                            pageToken = pageToken
                        )
                    )
                val records = readResponse.records
                // Do something with records
                pageToken = readResponse.pageToken
            } while (pageToken != null)
        } catch (quotaError: IllegalStateException) {
            // Backoff
        }
        // [END pagetoken_read_example]
    }

    suspend fun deviceExamples() {
        // [START device_examples]
         val WATCH_DEVICE = Device(
            manufacturer = "Google",
            model = "Pixel Watch",
            type = Device.TYPE_WATCH
        )

        // Phone
         val PHONE_DEVICE = Device(
            manufacturer = "Google",
            model = "Pixel 8",
            type = Device.TYPE_PHONE
        )

        // Ring
         val RING_DEVICE = Device(
            manufacturer = "Oura",
            model = "Ring Gen3",
            type = Device.TYPE_RING
        )

        // Scale
         val SCALE_DEVICE = Device(
            manufacturer = "Withings",
            model = "Body Comp",
            type = Device.TYPE_SCALE
        )
        // [END device_examples]
    }

    @OptIn(ExperimentalFeatureAvailabilityApi::class)
    @SuppressLint("RestrictedApi")
    suspend fun writeMindfulnessSession(healthConnectClient: HealthConnectClient) {
        // [START write_mindfulness_session]
        val isAvailable = healthConnectClient.features.getFeatureStatus(FEATURE_MINDFULNESS_SESSION)

        if (isAvailable == HealthConnectFeatures.FEATURE_STATUS_AVAILABLE) {

            val record = MindfulnessSessionRecord(
                startTime = Instant.now().minus(Duration.ofHours(1)),
                startZoneOffset = ZoneOffset.UTC,
                endTime = Instant.now(),
                endZoneOffset = ZoneOffset.UTC,
                mindfulnessSessionType = MindfulnessSessionRecord.MINDFULNESS_SESSION_TYPE_MEDITATION,
                title = "Lake meditation",
                notes = "Meditation by the lake",
                metadata = Metadata(
                    clientRecordId = "myid",
                    clientRecordVersion = 1L,
                    device = Device(type = Device.TYPE_PHONE)
                )
            )
            // [END write_mindfulness_session]
            healthConnectClient.insertRecords(listOf(record))
        }
    }


    suspend fun readHeartRateByTimeRange(startTime: Instant, endTime: Instant) {
        // [START android_healthconnect_read_heart_rate]
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                HeartRateRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        response.records.forEach { record ->
            /* Process records */
        }
        // [END android_healthconnect_read_heart_rate]
    }

    @SuppressLint("RestrictedApi")
    suspend fun insertExerciseRoute(startTime: Instant, endTime: Instant) {
        // [START android_healthconnect_write_exercise_route]
        val exerciseRoute = ExerciseRoute(
            listOf(
                ExerciseRoute.Location(time = startTime, latitude = 6.5483, longitude = 0.5488),
                ExerciseRoute.Location(time = endTime.minusSeconds(1), latitude = 6.4578, longitude = 0.6577)
            )
        )
        val zoneOffset = ZoneOffset.systemDefault().rules.getOffset(startTime)
        val session = ExerciseSessionRecord(
            startTime = startTime,
            startZoneOffset = zoneOffset,
            endTime = endTime,
            endZoneOffset = zoneOffset,
            exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_BIKING,
            title = "Morning Bike Ride",
            exerciseRoute = exerciseRoute,
            metadata = Metadata(
                device = Device(type = Device.TYPE_PHONE),
                recordingMethod = Metadata.RECORDING_METHOD_MANUAL_ENTRY
            )
        )
        healthConnectClient.insertRecords(listOf(session))
        // [END android_healthconnect_write_exercise_route]
    }
}
