/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.sft.presentation

import android.util.Log
import com.google.android.gms.health.tracking.exercise.data.ExerciseMetricRecord
import com.google.android.gms.health.tracking.exercise.data.ExerciseMetrics
import com.google.android.gms.health.tracking.exercise.data.ExerciseSummary
import com.google.android.gms.health.tracking.exercise.device.ExerciseDevice
import com.google.android.gms.health.tracking.exercise.event.ExerciseEventRecord
import com.google.android.gms.health.tracking.exercise.session.ExerciseSessionListenerService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.launch

class TestExerciseSessionListenerService() : ExerciseSessionListenerService() {

    private val ioScope: CoroutineScope = CoroutineScope(Dispatchers.IO)

    override fun onExerciseSessionEnded(
        exerciseSummary: ExerciseSummary,
        exerciseDevice: ExerciseDevice,
    ) {
        exerciseSummary.getMostRecentMetrics(
            ExerciseMetrics.HEART_RATE,
            ExerciseMetrics.DISTANCE, ExerciseMetrics.ABSOLUTE_ELEVATION
        )
        Log.i(TAG, "onExerciseSessionEnded for ${exerciseSummary.sessionId}")
        for (
        metric in
        exerciseSummary.getMostRecentMetrics(
            ExerciseMetrics.ACTIVE_DURATION,
            *ALL_METRICS.toTypedArray(),
        )
        ) {
            Log.i("$TAG.lastMetrics", metric.toString())
        }
        ioScope.launch {
            val allMetricsFlow = exerciseSummary.getAllMetrics()
            Log.i("$TAG.metricSummary number", allMetricsFlow.count().toString())

            allMetricsFlow.collect { metric: ExerciseMetricRecord ->
                Log.i("$TAG.metricSummary", metric.toString())
            }
            Log.i("$TAG.metricSummary", "All metrics collected")
        }
        ioScope.launch {
            val allEventsFlow = exerciseSummary.getAllEvents()
            allEventsFlow.collect { event: ExerciseEventRecord ->
                Log.i("$TAG.eventSummary", event.toString())
            }
        }
    }

    companion object {
        const val TAG = "TestExerciseSessionListenerService"
        private val ALL_METRICS =
            listOf(
                ExerciseMetrics.ACTIVE_DURATION,
                ExerciseMetrics.ABSOLUTE_ELEVATION,
                ExerciseMetrics.DISTANCE,
                ExerciseMetrics.ELEVATION_GAINED,
                ExerciseMetrics.ELEVATION_LOST,
                ExerciseMetrics.FLOORS_CLIMBED,
                ExerciseMetrics.GROUND_CONTACT_BALANCE,
                ExerciseMetrics.GROUND_CONTACT_BALANCE_AVERAGE,
                ExerciseMetrics.GROUND_CONTACT_TIME,
                ExerciseMetrics.GROUND_CONTACT_TIME_AVERAGE,
                ExerciseMetrics.HEART_RATE,
                ExerciseMetrics.HEART_RATE_AVERAGE,
                ExerciseMetrics.LOCATION,
                ExerciseMetrics.PACE,
                ExerciseMetrics.PACE_AVERAGE,
                ExerciseMetrics.SPEED,
                ExerciseMetrics.SPEED_AVERAGE,
                ExerciseMetrics.STEPS_CADENCE,
                ExerciseMetrics.STEPS_CADENCE_AVERAGE,
                ExerciseMetrics.STEPS,
                ExerciseMetrics.STRIDE_LENGTH,
                ExerciseMetrics.STRIDE_LENGTH_AVERAGE,
                ExerciseMetrics.TOTAL_CALORIES_BURNED,
                ExerciseMetrics.VERTICAL_OSCILLATION,
                ExerciseMetrics.VERTICAL_OSCILLATION_AVERAGE,
                ExerciseMetrics.VERTICAL_RATIO,
                ExerciseMetrics.VERTICAL_RATIO_AVERAGE,
            )
    }
}
