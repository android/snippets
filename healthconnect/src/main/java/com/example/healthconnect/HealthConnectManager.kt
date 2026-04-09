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
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.request.AggregateRequest
import androidx.health.connect.client.records.ExerciseSessionRecord
import androidx.health.connect.client.records.*
import androidx.health.connect.client.records.metadata.Device
import androidx.health.connect.client.records.metadata.Metadata
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import java.time.*

class HealthConnectManager(
    private val healthConnectClient: HealthConnectClient,
    private val context: Context
) {

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertSteps(startTime: Instant, endTime: Instant) {
        // [START health_connect_insert_steps]
        val stepsRecord = StepsRecord(
            count = 120,
            startTime = startTime,
            endTime = endTime,
            startZoneOffset = ZoneOffset.UTC,
            endZoneOffset = ZoneOffset.UTC,
            metadata = Metadata(
                device = Device(type = Device.TYPE_WATCH),
                recordingMethod = Metadata.RECORDING_METHOD_AUTOMATICALLY_RECORDED
            )
        )
        // [END health_connect_insert_steps]
        healthConnectClient.insertRecords(listOf(stepsRecord))
    }

    suspend fun readStepsAggregate(startTime: Instant, endTime: Instant): Long {
        // [START health_connect_read_steps_aggregate]
        val response = healthConnectClient.aggregate(
             AggregateRequest(
                metrics = setOf(StepsRecord.COUNT_TOTAL),
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        return response[StepsRecord.COUNT_TOTAL] ?: 0L
        // [END health_connect_read_steps_aggregate]
    }

    suspend fun readHeartRateByTimeRange(startTime: Instant, endTime: Instant) {
        // [START health_connect_read_heart_rate]
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                HeartRateRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        response.records.forEach { record ->
            /* Process records */
        }
        // [END health_connect_read_heart_rate]
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun insertExerciseRoute(startTime: Instant, endTime: Instant) {
        // [START health_connect_write_exercise_route]
        val exerciseRoute = ExerciseRoute(
            listOf(
                ExerciseRoute.Location(time = startTime, latitude = 6.5483, longitude = 0.5488),
                ExerciseRoute.Location(time = endTime.minusSeconds(1), latitude = 6.4578, longitude = 0.6577)
            )
        )
        val session = ExerciseSessionRecord(
            startTime = startTime,
            startZoneOffset = ZoneOffset.UTC,
            endTime = endTime,
            endZoneOffset = ZoneOffset.UTC,
            exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_BIKING,
            title = "Morning Bike Ride",
            exerciseRoute = exerciseRoute,
            metadata = Metadata(
                device = Device(type = Device.TYPE_PHONE),
                recordingMethod = Metadata.RECORDING_METHOD_MANUAL_ENTRY
            )
        )
        healthConnectClient.insertRecords(listOf(session))
        // [END health_connect_write_exercise_route]
    }
}