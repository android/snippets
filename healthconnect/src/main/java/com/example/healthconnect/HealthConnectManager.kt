/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 */

package com.example.healthconnect

import android.annotation.SuppressLint
import android.content.Context
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.ExerciseRoute
import androidx.health.connect.client.records.metadata.Device
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.Instant
import java.time.ZoneOffset

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
        // [END android_healthconnect_insert_steps]
        healthConnectClient.insertRecords(listOf(stepsRecord))
    }

    suspend fun readStepsAggregate(startTime: Instant, endTime: Instant): Long {
        // [START android_healthconnect_read_steps_aggregate]
        val response = healthConnectClient.aggregate(
             AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        return response[StepsRecord.COUNT_TOTAL] ?: 0L
        // [END android_healthconnect_read_steps_aggregate]
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