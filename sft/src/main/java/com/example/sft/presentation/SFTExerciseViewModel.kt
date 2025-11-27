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

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sft.R
import com.google.android.gms.health.tracking.HealthTracking
import com.google.android.gms.health.tracking.exercise.data.ExerciseMetric
import com.google.android.gms.health.tracking.exercise.data.ExerciseMetricRecord
import com.google.android.gms.health.tracking.exercise.data.ExerciseMetrics
import com.google.android.gms.health.tracking.exercise.event.ExerciseEvents
import com.google.android.gms.health.tracking.exercise.event.ExerciseLocationAvailabilityRecord
import com.google.android.gms.health.tracking.exercise.event.ExerciseStateRecord
import com.google.android.gms.health.tracking.exercise.session.ControllableExerciseSession
import com.google.android.gms.health.tracking.exercise.session.ExerciseSessionControllerExtensions.getOngoingSystemRenderedSession
import com.google.android.gms.health.tracking.exercise.session.ExerciseSessionListenerServiceConfig
import com.google.android.gms.health.tracking.exercise.session.ExerciseSessionRequest
import com.google.android.gms.health.tracking.exercise.session.SystemRenderedExerciseSession
import com.google.android.gms.health.tracking.exercise.session.SystemRenderedSessionConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

class SFTExerciseViewModel(context: Context) : ViewModel() {
    private val exerciseClient = HealthTracking.getExerciseClient(context.applicationContext)
    private lateinit var session: ControllableExerciseSession
    private val _systemRenderedSession = MutableStateFlow<SystemRenderedExerciseSession?>(null)
    private var exerciseRepository = ExerciseRepository()

    fun newSystemRenderedSession(
        exerciseType: Int,
        exerciseName: Int,
        exerciseIcon: Int,
        topMetricRecord: List<ExerciseMetric<ExerciseMetricRecord>>,
        activity: Activity?
    ) {
        if (activity == null) {
            Log.d("ExerciseSelectionViewModel", "Activity is null, not starting new session")
            return
        }
        viewModelScope.launch {
                exerciseClient.exerciseSessionController.newSystemRenderedSession(
                    ExerciseSessionRequest(exerciseType),
                    SystemRenderedSessionConfig(
                        exerciseName = exerciseName,
                        exerciseIcon = exerciseIcon,
                        primaryColor = R.color.primary,
                        topMetrics = topMetricRecord,
                        distanceUnits = SystemRenderedSessionConfig.DistanceUnits.METRIC
                    )
                ).show(activity = activity)
            Log.i(
                TAG,
                "Got a system rendered session with id ${_systemRenderedSession.value?.sessionId}"
            )
        }
    }

    init {
        viewModelScope.launch {
            val config = ExerciseSessionListenerServiceConfig.createForOnExerciseSessionEnded(
                ComponentName(context, TestExerciseSessionListenerService::class.java),
                desiredExerciseMetricTypes = exerciseRepository.desiredExerciseMetricTypes
            )
            exerciseClient.exerciseSessionController.registerExerciseSessionListenerService(config)
        }
    }

    fun showActiveSessionIfExists(activity: Activity) {
        viewModelScope.launch {
            exerciseClient.exerciseSessionController.getOngoingSystemRenderedSession()?.show(activity =  activity)
        }
    }

    fun metrics() {
        viewModelScope.launch {
            val flow = session.metrics(ExerciseMetrics.HEART_RATE)
            flow
                .takeWhile { true }
                .collect { value: ExerciseMetricRecord ->
                    Log.i(
                        TAG + ".metrics",
                        value.toString()
                    )
                }
        }
        viewModelScope.launch {
            val flow = session.metrics(ExerciseMetrics.SPEED_AVERAGE)
            flow
                .takeWhile { true }
                .collect { value: ExerciseMetricRecord ->
                    Log.i(
                        TAG + ".metrics2",
                        value.toString()
                    )
                }
        }
        viewModelScope.launch {
            val flow =
                session.metrics(
                    ExerciseMetrics.DISTANCE,
                    ExerciseMetrics.STEPS,
                    ExerciseMetrics.HEART_RATE
                )
            flow
                .takeWhile { true }
                .collect { value: ExerciseMetricRecord ->
                    Log.i(
                        TAG + ".metrics3",
                        value.toString()
                    )
                }
        }
    }

    companion object {
        const val TAG = "HealthTracking.FusedExercise"
    }

    fun events() {
        viewModelScope.launch {
            val flow = session.events(ExerciseEvents.EXERCISE_STATE)
            flow
                .takeWhile { true }
                .collect { value: ExerciseStateRecord -> Log.i(TAG + ".state", value.toString()) }
        }
        viewModelScope.launch {
            val flow = session.events(ExerciseEvents.LOCATION_AVAILABILITY)
            flow
                .takeWhile { true }
                .collect { value: ExerciseLocationAvailabilityRecord ->
                    Log.i(TAG + ".location", value.toString())
                }
        }
    }
}
