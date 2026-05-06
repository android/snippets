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
import android.util.Log
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
import androidx.health.connect.client.changes.DeletionChange
import androidx.health.connect.client.changes.UpsertionChange
import androidx.health.connect.client.records.NutritionRecord
import androidx.health.connect.client.records.SleepSessionRecord
import androidx.health.connect.client.records.WeightRecord
import androidx.health.connect.client.records.metadata.DataOrigin
import androidx.health.connect.client.request.AggregateGroupByDurationRequest
import androidx.health.connect.client.request.AggregateGroupByPeriodRequest
import androidx.health.connect.client.units.Mass
import androidx.health.connect.client.units.grams
import androidx.health.connect.client.units.kilocalories
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GooglePlayServicesUtilLight.isGooglePlayServicesAvailable
import com.google.android.gms.fitness.FitnessLocal
import com.google.android.gms.fitness.LocalRecordingClient
import java.time.Duration
import com.google.android.gms.fitness.data.LocalDataSet
import com.google.android.gms.fitness.data.LocalDataType
import com.google.android.gms.fitness.request.LocalDataReadRequest
import java.time.LocalDateTime
import java.time.Period
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class HealthConnectManager(
    private val healthConnectClient: HealthConnectClient,
) {


    // [START android_healthconnect_upsert_steps_example]
    suspend fun pullStepsFromDatastore(startTime: Instant, endTime: Instant) : ArrayList<StepsRecord> {
        val appStepsRecords = arrayListOf<StepsRecord>()
        // Pull data from app datastore
        // ...
        // Make changes to data if necessary
        // ...
        // Store data in appStepsRecords
        // ...
        var sr = StepsRecord(
            metadata = Metadata(
                clientRecordId = "Your client record ID",
                device = Device(type = Device.TYPE_WATCH)
            ),
            startTime = startTime,
            startZoneOffset = startTime.atZone(ZoneId.of("PST")).offset,
            endTime = endTime,
            endZoneOffset = endTime.atZone(ZoneId.of("PST")).offset,
            count = 120
        )
        appStepsRecords.add(sr)
        // ...
        return appStepsRecords
    }

    suspend fun upsertSteps(
        healthConnectClient: HealthConnectClient,
        newStepsRecords: ArrayList<StepsRecord>
    ) {
        try {
            healthConnectClient.insertRecords(newStepsRecords)
        } catch (e: Exception) {
            // Run error handling here
        }
    }
    // [END android_healthconnect_upsert_steps_example]

    suspend fun upsertStepsCall(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ) {
        try {
            // [START android_healthconnect_upsert_steps_call_example]
            upsertSteps(healthConnectClient, pullStepsFromDatastore(
                startTime = startTime,
                endTime = endTime
            ))
            // [END android_healthconnect_upsert_steps_call_example]
        } catch (e: Exception) {
            // Run error handling here
        }
    }

    // [START android_healthconnect_process_changes]
    suspend fun processChanges(context: Context, token: String): String {
        var nextChangesToken = token
        do {
            val response = healthConnectClient.getChanges(nextChangesToken)
            response.changes.forEach { change ->
                when (change) {
                    is UpsertionChange ->
                        if (change.record.metadata.dataOrigin.packageName != context.packageName) {
                            processUpsertionChange(change)
                        }
                    is DeletionChange -> processDeletionChange(change)
                }
            }
            nextChangesToken = response.nextChangesToken
        } while (response.hasMore)
        // Return and store the changes token for use next time.
        return nextChangesToken
    }
    // [END android_healthconnect_process_changes]

    private fun processUpsertionChange(change: UpsertionChange) {
        TODO("Not yet implemented")
    }

    private fun processDeletionChange(change: DeletionChange) {
        TODO("Not yet implemented")
    }

    suspend fun upsertStepsExample(
        healthConnectClient: HealthConnectClient,
        record: StepsRecord
    ) {
        try {
            // [START android_healthconnect_upsert_call]
            healthConnectClient.insertRecords(arrayListOf(record))
            // [END android_healthconnect_upsert_call]
        } catch (e: Exception) {
            // Run error handling here
        }
    }

    suspend fun heartRateMinuteRecord(
        healthConnectClient: HealthConnectClient,
        record: StepsRecord
    ) {
        try {
            // [START android_heart_rate_minute_record_example]
            val startTime = Instant.now().truncatedTo(ChronoUnit.MINUTES)
            val endTime = startTime.plus(Duration.ofMinutes(1))

            val heartRateRecord = HeartRateRecord(
                startTime = startTime,
                startZoneOffset = ZoneOffset.UTC,
                endTime = endTime,
                endZoneOffset = ZoneOffset.UTC,
                // Create a new record every minute, containing a list of samples.
                samples = listOf(
                    HeartRateRecord.Sample(
                        time = startTime + Duration.ofSeconds(15),
                        beatsPerMinute = 80,
                    ),
                    HeartRateRecord.Sample(
                        time = startTime + Duration.ofSeconds(30),
                        beatsPerMinute = 82,
                    ),
                    HeartRateRecord.Sample(
                        time = startTime + Duration.ofSeconds(45),
                        beatsPerMinute = 85,
                    )
                ),
                metadata = Metadata(
                    device = Device(type = Device.TYPE_WATCH)
                ))
            // [END android_heart_rate_minute_record_example]
        } catch (e: Exception) {
            // Run error handling here
        }
    }

    @SuppressLint("RestrictedApi")
    suspend fun triathlonExample(
        healthConnectClient: HealthConnectClient,
    ) {
        try {
            // [START android_healthconnect_triathlon_example]
            val swimStartTime = Instant.parse("2024-08-22T08:00:00Z")
            val swimEndTime = Instant.parse("2024-08-22T08:30:00Z")
            val bikeStartTime = Instant.parse("2024-08-22T08:40:00Z")
            val bikeEndTime = Instant.parse("2024-08-22T09:40:00Z")
            val runStartTime = Instant.parse("2024-08-22T09:50:00Z")
            val runEndTime = Instant.parse("2024-08-22T10:20:00Z")

            val swimSession = ExerciseSessionRecord(
                startTime = swimStartTime,
                endTime = swimEndTime,
                exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_SWIMMING_OPEN_WATER,
                metadata = Metadata(
                    device = Device(type = Device.TYPE_WATCH)
                ),
                startZoneOffset = null,
                endZoneOffset = null,
            )

            val bikeSession = ExerciseSessionRecord(
                startTime = bikeStartTime,
                endTime = bikeEndTime,
                exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_BIKING,
                metadata = Metadata(
                    device = Device(type = Device.TYPE_WATCH)
                ),
                startZoneOffset = null,
                endZoneOffset = null,
            )

            val runSession = ExerciseSessionRecord(
                startTime = runStartTime,
                endTime = runEndTime,
                exerciseType = ExerciseSessionRecord.EXERCISE_TYPE_RUNNING,
                metadata = Metadata(
                    device = Device(type = Device.TYPE_WATCH)
                ),
                startZoneOffset = null,
                endZoneOffset = null,
            )

            healthConnectClient.insertRecords(listOf(swimSession, bikeSession, runSession))
            // [END android_healthconnect_triathlon_example]
        } catch (e: Exception) {
            // Run error handling here
        }
    }

    suspend fun  heartRateRecordExample(
        healthConnectClient: HealthConnectClient,
        record: StepsRecord
    ) {
        try {
            // [START android_healthconnect_heart_rate_record_example]
            val endTime = Instant.now()
            val startTime = endTime.minus(Duration.ofMinutes(5))

            val heartRateRecord = HeartRateRecord(
                startTime = startTime,
                startZoneOffset = ZoneOffset.UTC,
                endTime = endTime,
                endZoneOffset = ZoneOffset.UTC,
                // records 10 arbitrary data, to replace with actual data
                samples = List(10) { index ->
                    HeartRateRecord.Sample(
                        time = startTime + Duration.ofSeconds(index.toLong()),
                        beatsPerMinute = 100 + index.toLong(),
                    )
                },
                metadata = Metadata(
                    device = Device(type = Device.TYPE_WATCH)
                ))
            // [END android_healthconnect_heart_rate_record_example]
        } catch (e: Exception) {
            // Run error handling here
        }
    }

    suspend fun upsertNutritionRecord() {
        try {
            // [START android_healthconnect_nutrition_record_example]
            val endTime = Instant.now()
            val startTime = endTime.minus(Duration.ofMinutes(1))

            val banana = NutritionRecord(
                name = "banana",
                energy = 105.0.kilocalories,
                dietaryFiber = 3.1.grams,
                potassium = 0.422.grams,
                totalCarbohydrate = 27.0.grams,
                totalFat = 0.4.grams,
                saturatedFat = 0.1.grams,
                sodium = 0.001.grams,
                sugar = 14.0.grams,
                vitaminB6 = 0.0005.grams,
                vitaminC = 0.0103.grams,
                startTime = startTime,
                endTime = endTime,
                startZoneOffset = ZoneOffset.UTC,
                endZoneOffset = ZoneOffset.UTC,
                metadata = Metadata(
                    device = Device(type = Device.TYPE_PHONE)
                )
            )
            // [END android_healthconnect_nutrition_record_example]
        } catch (e: Exception) {
            // Run error handling here
        }
    }

    suspend fun insertWeightRecord(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ) {
        try {
            // [START android_healthconnect_weight_record_metadata]
            val recordVersion = 0L
            // Specify as needed
            // The clientRecordId is an ID that you choose for your record. This
            // is often the same ID you use in your app's datastore.
            val clientRecordId = "<your-record-id>"

            val record = WeightRecord(
                metadata = Metadata(
                    clientRecordId = clientRecordId,
                    clientRecordVersion = recordVersion,
                    device = Device(type = Device.TYPE_SCALE)
                ),
                weight = Mass.kilograms(62.0),
                time = Instant.now(),
                zoneOffset = ZoneOffset.UTC,
            )
            healthConnectClient.insertRecords(listOf(record))
            // [END android_healthconnect_weight_record_metadata]
        } catch (e: Exception) {
            // Run error handling here
        }
    }

    suspend fun storeIds(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ) {
        try {
            val recordVersion = 0L
            // Specify as needed
            // The clientRecordId is an ID that you choose for your record. This
            // is often the same ID you use in your app's datastore.
            val clientRecordId = "<your-record-id>"

            val record = WeightRecord(
                metadata = Metadata(
                    clientRecordId = clientRecordId,
                    clientRecordVersion = recordVersion,
                    device = Device(type = Device.TYPE_SCALE)
                ),
                weight = Mass.kilograms(62.0),
                time = Instant.now(),
                zoneOffset = ZoneOffset.UTC,
            )
            // [START android_healthconnect_store_ids]
            val response = healthConnectClient.insertRecords(listOf(record))
            for (recordId in response.recordIdsList) {
                // Store recordId to your app's datastore
            }
            // [END android_healthconnect_store_ids]
        } catch (e: Exception) {
            // Run error handling here
        }
    }

    suspend fun timeZoneHandling(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ) {
        try {
            // [START android_healthconnect_timezone_handling_example]
            val endTime = Instant.now()
            val startTime = endTime.minus(Duration.ofDays(1))
            val stepsRecords = mutableListOf<StepsRecord>()
            var sampleTime = startTime
            val minutesBetweenSamples = 15L
            while (sampleTime < endTime) {
                // Get the default ZoneId then convert it to an offset
                val zoneOffset = ZoneOffset.systemDefault().rules.getOffset(sampleTime)
                stepsRecords += StepsRecord(
                    startTime = sampleTime.minus(Duration.ofMinutes(minutesBetweenSamples)),
                    startZoneOffset = zoneOffset,
                    endTime = sampleTime,
                    endZoneOffset = zoneOffset,
                    count = Random.nextLong(1, 100),
                    metadata = Metadata(),
                )
                sampleTime = sampleTime.plus(Duration.ofMinutes(minutesBetweenSamples))
            }
            healthConnectClient.insertRecords(
                stepsRecords
            )
            // [END android_healthconnect_timezone_handling_example]
        } catch (e: Exception) {
            // Run error handling here
        }
    }

    suspend fun stepRecordVersioning(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant
    ) {
        try {
            // [START android_healthconnect_steps_record_versioning_example]
            val endTime = Instant.now()
            val startTime = endTime.minus(Duration.ofMinutes(15))

            val stepsRecord = StepsRecord(
                count = 100L,
                startTime = startTime,
                startZoneOffset = ZoneOffset.UTC,
                endTime = endTime,
                endZoneOffset = ZoneOffset.UTC,
                metadata = Metadata(
                    clientRecordId = "Your supplied record ID",
                    clientRecordVersion = 0L, // Your supplied record version
                    device = Device(type = Device.TYPE_WATCH)
                )
            )
            // [END android_healthconnect_steps_record_versioning_example]
        } catch (e: Exception) {
            // Run error handling here
        }
    }

    // [START android_healthconnect_update_steps]
    suspend fun updateSteps(
        healthConnectClient: HealthConnectClient,
        prevRecordStartTime: Instant,
        prevRecordEndTime: Instant
    ) {
        try {
            val request = healthConnectClient.readRecords(
                ReadRecordsRequest(
                    recordType = StepsRecord::class, timeRangeFilter = TimeRangeFilter.between(
                        prevRecordStartTime,
                        prevRecordEndTime
                    )
                )
            )

            val newStepsRecords = arrayListOf<StepsRecord>()
            for (record in request.records) {
                // Adjusted both offset values to reflect changes
                val sr = StepsRecord(
                    count = record.count,
                    startTime = record.startTime,
                    startZoneOffset = record.startTime.atZone(ZoneId.of("PST")).offset,
                    endTime = record.endTime,
                    endZoneOffset = record.endTime.atZone(ZoneId.of("PST")).offset,
                    metadata = record.metadata
                )
                newStepsRecords.add(sr)
            }

            healthConnectClient.updateRecords(newStepsRecords)
        } catch (e: Exception) {
            // Run error handling here
        }
    }
    // [END android_healthconnect_update_steps]

    // [START android_healthconnect_period_bucket_aggregation]
    suspend fun aggregateStepsIntoMonths(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        try {
            val response =
                healthConnectClient.aggregateGroupByPeriod(
                    AggregateGroupByPeriodRequest(
                        metrics = setOf(StepsRecord.COUNT_TOTAL),
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                        timeRangeSlicer = Period.ofMonths(1)
                    )
                )
            for (monthlyResult in response) {
                // The result may be null if no data is available in the time range
                val totalSteps = monthlyResult.result[StepsRecord.COUNT_TOTAL] ?: 0L
            }
        } catch (e: Exception) {
            // Run error handling here
        }
    }
    // [END android_healthconnect_period_bucket_aggregation]

    // [START android_healthconnect_duration_bucket_aggregation]
    suspend fun aggregateStepsIntoMinutes(
        healthConnectClient: HealthConnectClient,
        startTime: LocalDateTime,
        endTime: LocalDateTime
    ) {
        try {
            val response =
                healthConnectClient.aggregateGroupByDuration(
                    AggregateGroupByDurationRequest(
                        metrics = setOf(StepsRecord.COUNT_TOTAL),
                        timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                        timeRangeSlicer = Duration.ofMinutes(1L)
                    )
                )
            for (durationResult in response) {
                // The result may be null if no data is available in the time range
                val totalSteps = durationResult.result[StepsRecord.COUNT_TOTAL] ?: 0L
            }
        } catch (e: Exception) {
            // Run error handling here
        }
    }
    // [END android_healthconnect_duration_bucket_aggregation]

    // [START android_healthconnect_filter_by_data_origin]
    suspend fun aggregateStepsFromSpecificApp(
        healthConnectClient: HealthConnectClient,
        startTime: Instant,
        endTime: Instant,
        appPackageName: String
    ) {
        try {
            val response = healthConnectClient.aggregate(
                AggregateRequest(
                    metrics = setOf(StepsRecord.COUNT_TOTAL),
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime),
                    dataOriginFilter = setOf(DataOrigin(appPackageName))
                )
            )
            // The result may be null if no data is available in the time range
            val totalSteps = response[StepsRecord.COUNT_TOTAL] ?: 0L
        } catch (e: Exception) {
            // Run error handling here
        }
    }
    // [END android_healthconnect_filter_by_data_origin]

    suspend fun deleteStepsById(idList: List<String>) {
        // [START android_healthconnect_delete_steps_by_id]
        try {
            healthConnectClient.deleteRecords(
                recordType = StepsRecord::class,
                recordIdsList = idList,
                clientRecordIdsList = emptyList<String>()
            )
        } catch (e: Exception) {
            // Run error handling here
        }
        // [END android_healthconnect_delete_steps_by_id]
    }

    suspend fun deleteStepsByTimeRange(idList: List<String>, startTime: Instant, endTime: Instant) {
        // [START android_healthconnect_delete_steps_by_time_range]
        try {
            healthConnectClient.deleteRecords(
                StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        } catch (e: Exception) {
            // Run error handling here
        }
        // [END android_healthconnect_delete_steps_by_time_range]
    }

    suspend fun deleteSleepSession(healthConnectClient: HealthConnectClient, sleepRecord: SleepSessionRecord,) {
        // [START android_healthconnect_delete_sleep_session]
        val timeRangeFilter = TimeRangeFilter.between(sleepRecord.startTime, sleepRecord.endTime)
        healthConnectClient.deleteRecords(SleepSessionRecord::class, timeRangeFilter)
        // [END android_healthconnect_delete_sleep_session]
    }

    suspend fun readSleepSession(healthConnectClient: HealthConnectClient, startTime: Instant, endTime: Instant) {
        // [START android_healthconnect_read_sleep_session]
        val response =
            healthConnectClient.readRecords(
                ReadRecordsRequest(
                    SleepSessionRecord::class,
                    timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
                )
            )
        for (sleepRecord in response.records) {
            // Retrieve relevant sleep stages from each sleep record
            val sleepStages = sleepRecord.stages
        }
        // [END android_healthconnect_read_sleep_session]
    }

    suspend fun writeSleepSessionWithStage(healthConnectClient: HealthConnectClient, startTime: Instant, endTime: Instant) {
        // [START android_healthconnect_sleep_session_with_stages]
        val stages = listOf(
            SleepSessionRecord.Stage(
                startTime = startTime,
                endTime = endTime,
                stage = SleepSessionRecord.STAGE_TYPE_SLEEPING,
            )
        )

        SleepSessionRecord(
            title = "weekend sleep",
            startTime = startTime,
            endTime = endTime,
            startZoneOffset = ZoneOffset.UTC,
            endZoneOffset = ZoneOffset.UTC,
            stages = stages,
        )
        // [END android_healthconnect_sleep_session_with_stages]
    }

    suspend fun writeSleepSessionWithoutStage(healthConnectClient: HealthConnectClient, startTime: Instant, endTime: Instant) {
        // [START android_healthconnect_sleep_session_without_stages]
        SleepSessionRecord(
            title = "weekend sleep",
            startTime = startTime,
            endTime = endTime,
            startZoneOffset = ZoneOffset.UTC,
            endZoneOffset = ZoneOffset.UTC,
        )
        // [END android_healthconnect_sleep_session_without_stages]
    }

    // [START android_healthconnect_write_sleep_session]
    suspend fun writeSleepSession(healthConnectClient: HealthConnectClient) {
        healthConnectClient.insertRecords(
            listOf(
                SleepSessionRecord(
                    startTime = Instant.parse("2022-05-10T23:00:00.000Z"),
                    startZoneOffset = ZoneOffset.of("-08:00"),
                    endTime = Instant.parse("2022-05-11T07:00:00.000Z"),
                    endZoneOffset = ZoneOffset.of("-08:00"),
                    title = "My Sleep"
                ),
            )
        )
    }
    // [END android_healthconnect_write_sleep_session]

    // [START android_healthconnect_unsubscribe_fitness_data]
    fun unsubscribeFitnessData(context: Context) {
        val tag = "HealthConnectManager"
        val localRecordingClient = FitnessLocal.getLocalRecordingClient(context)
        localRecordingClient.unsubscribe(LocalDataType.TYPE_STEP_COUNT_DELTA)
            .addOnSuccessListener {
                Log.i(tag, "Successfully unsubscribed!")
            }
            .addOnFailureListener { e ->
                Log.w(tag, "There was a problem unsubscribing.", e)
            }
    }
    // [END android_healthconnect_unsubscribe_fitness_data]

    // [START android_healthconnect_read_process_fitness_data]
    fun readLocalSteps(context: Context, onResult: (String) -> Unit) {
        val tag = "HealthConnectManager"
        val localRecordingClient = FitnessLocal.getLocalRecordingClient(context)

        val endTime = LocalDateTime.now().atZone(ZoneId.systemDefault())
        val startTime = endTime.minusWeeks(1)

        val readRequest = LocalDataReadRequest.Builder()
            .aggregate(LocalDataType.TYPE_STEP_COUNT_DELTA)
            .bucketByTime(1, TimeUnit.DAYS)
            .setTimeRange(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
            .build()

        localRecordingClient.readData(readRequest)
            .addOnSuccessListener { response ->
                val sb = StringBuilder()
                // Flatten buckets into datasets and process
                for (dataSet in response.buckets.flatMap { it.dataSets }) {
                    dumpDataSet(dataSet, sb)
                }
                onResult(if (sb.isEmpty()) "No local data found" else sb.toString())
            }
            .addOnFailureListener { e ->
                Log.w(tag, "There was an error reading data", e)
                onResult("Error reading local data: ${e.message}")
            }
    }

    private fun dumpDataSet(dataSet: LocalDataSet, sb: StringBuilder) {
        val TAG = "HealthConnectManager"
        Log.i(TAG, "Data returned for Data type: ${dataSet.dataType.name}")

        for (dp in dataSet.dataPoints) {
            sb.append("Steps: ${dp.getValue(dp.dataType.fields[0])}\n")

            Log.i(TAG, "Data point:")
            Log.i(TAG, "\tType: ${dp.dataType.name}")
            for (field in dp.dataType.fields) {
                Log.i(TAG, "\tLocalField: ${field.name} LocalValue: ${dp.getValue(field)}")
            }
        }
    }
    // [END android_healthconnect_read_process_fitness_data]

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

    // [START android_healthconnect_delete_exercise_session_by_time_range]
    suspend fun deleteExerciseSessionByTimeRange(
        healthConnectClient: HealthConnectClient,
        exerciseRecord: ExerciseSessionRecord,
    ) {
        val timeRangeFilter = TimeRangeFilter.between(exerciseRecord.startTime, exerciseRecord.endTime)
        healthConnectClient.deleteRecords(ExerciseSessionRecord::class, timeRangeFilter)
        // delete the associated distance record
        healthConnectClient.deleteRecords(DistanceRecord::class, timeRangeFilter)
    }
    // [END android_healthconnect_delete_exercise_session_by_time_range]

    // [START android_healthconnect_delete_exercise_session_by_uid]
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
    // [END android_healthconnect_delete_exercise_session_by_uid]


    suspend fun stepsRecordMetadata(
        stepsRecord: StepsRecord,
    ) {
        // [START android_healthconnect_steps_record_metadata]
         StepsRecord(
            startTime = Instant.ofEpochMilli(1234L),
            startZoneOffset = null,
            endTime = Instant.ofEpochMilli(1236L),
            endZoneOffset = null,
            metadata = Metadata(),
            count = 10
        )
        // [END android_healthconnect_steps_record_metadata]
    }

    suspend fun playServiceCheck(context: Context) {
        // [START android_healthconnect_play_services_check]
        val hasMinPlayServices = isGooglePlayServicesAvailable(context, LocalRecordingClient.LOCAL_RECORDING_CLIENT_MIN_VERSION_CODE)

        if(hasMinPlayServices != ConnectionResult.SUCCESS) {
            // Prompt user to update their device's Google Play services app and return
        }
        // [END android_healthconnect_play_services_check]
    }

    suspend fun readHistoricalData(startTime: Instant, endTime: Instant) {
        // [START android_healthconnect_read_historical_data_example]
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
        // [END android_healthconnect_read_historical_data_example]
    }

    suspend fun pageToken() {
        // [START android_healthconnect_pagetoken_read_example]
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
        // [END android_healthconnect_pagetoken_read_example]
    }

    suspend fun deviceExamples() {
        // [START android_healthconnect_device_examples]
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
        // [END android_healthconnect_device_examples]
    }

    @OptIn(ExperimentalFeatureAvailabilityApi::class)
    @SuppressLint("RestrictedApi")
    suspend fun writeMindfulnessSession(healthConnectClient: HealthConnectClient) {
        // [START android_healthconnect_write_mindfulness_session]
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
            // [END android_healthconnect_write_mindfulness_session]
            healthConnectClient.insertRecords(listOf(record))
        }
    }

    suspend fun readStepsByTimeRange(startTime: Instant, endTime: Instant) {
        // [START android_healthconnect_read_steps_by_time_range]
        val response = healthConnectClient.readRecords(
            ReadRecordsRequest(
                StepsRecord::class,
                timeRangeFilter = TimeRangeFilter.between(startTime, endTime)
            )
        )
        response.records.forEach { record ->
            /* Process records */
        }
        // [END android_healthconnect_read_steps_by_time_range]
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
