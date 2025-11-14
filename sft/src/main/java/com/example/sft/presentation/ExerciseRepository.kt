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

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.example.sft.R
import com.google.android.gms.health.tracking.exercise.data.ExerciseMetric
import com.google.android.gms.health.tracking.exercise.data.ExerciseMetrics
import com.google.android.gms.health.tracking.exercise.data.ExerciseType
import com.google.android.gms.health.tracking.exercise.data.ExerciseTypes

class ExerciseRepository() {

    class Exercise(
        @StringRes val displayName: Int,
        @DrawableRes val icon: Int,
        @ExerciseType val exerciseType: Int,
        val metricTypes: List<ExerciseMetric<*>>,
    ) {
        companion object {
            val WALK =
                Exercise(
                    displayName = R.string.walking,
                    icon = R.drawable.walk,
                    exerciseType = ExerciseTypes.WALKING,
                    metricTypes =
                    listOf(
                        ExerciseMetrics.DISTANCE,
                        ExerciseMetrics.STEPS,
                        ExerciseMetrics.ACTIVE_DURATION,
                        ExerciseMetrics.PACE,
                        ExerciseMetrics.PACE_AVERAGE,
                        ExerciseMetrics.TOTAL_CALORIES_BURNED,
                        ExerciseMetrics.ABSOLUTE_ELEVATION,
                        ExerciseMetrics.ELEVATION_GAINED,
                        ExerciseMetrics.STEPS_CADENCE,
                        ExerciseMetrics.LOCATION
                    ),
                )

            val RUN =
                Exercise(
                    displayName = R.string.running,
                    icon = R.drawable.run,
                    exerciseType = ExerciseTypes.RUNNING,
                    metricTypes =
                    listOf(
                        ExerciseMetrics.HEART_RATE,
                        ExerciseMetrics.DISTANCE,
                        ExerciseMetrics.ACTIVE_DURATION,
                        ExerciseMetrics.PACE,
                        ExerciseMetrics.PACE_AVERAGE,
                        ExerciseMetrics.TOTAL_CALORIES_BURNED,
                        ExerciseMetrics.STEPS,
                        ExerciseMetrics.ABSOLUTE_ELEVATION,
                        ExerciseMetrics.ELEVATION_GAINED,
                        ExerciseMetrics.STEPS_CADENCE,
                        ExerciseMetrics.LOCATION
                    ),
                )

            val RUN_INDOOR =
                Exercise(
                    displayName = R.string.indoor_run,
                    icon = R.drawable.run,
                    exerciseType = ExerciseTypes.RUNNING_TREADMILL,
                    metricTypes =
                    listOf(
                        ExerciseMetrics.HEART_RATE,
                        ExerciseMetrics.DISTANCE,
                        ExerciseMetrics.ACTIVE_DURATION,
                        ExerciseMetrics.STEPS,
                        ExerciseMetrics.TOTAL_CALORIES_BURNED,
                        ExerciseMetrics.STEPS_CADENCE,
                    ),
                )

            val BIKE =
                Exercise(
                    displayName = R.string.biking,
                    icon = R.drawable.bike,
                    exerciseType = ExerciseTypes.BIKING,
                    metricTypes =
                    listOf(
                        ExerciseMetrics.HEART_RATE,
                        ExerciseMetrics.DISTANCE,
                        ExerciseMetrics.ACTIVE_DURATION,
                        ExerciseMetrics.SPEED,
                        ExerciseMetrics.SPEED_AVERAGE,
                        ExerciseMetrics.ABSOLUTE_ELEVATION,
                        ExerciseMetrics.TOTAL_CALORIES_BURNED,
                        ExerciseMetrics.ELEVATION_GAINED,
                    ),
                )

            val BIKE_INDOOR =
                Exercise(
                    displayName = R.string.biking_stationary,
                    icon = R.drawable.bike,
                    exerciseType = ExerciseTypes.BIKING_STATIONARY,
                    metricTypes =
                    listOf(
                        ExerciseMetrics.HEART_RATE,
                        ExerciseMetrics.ACTIVE_DURATION,
                        ExerciseMetrics.TOTAL_CALORIES_BURNED,
                    ),
                )

            val HIKE =
                Exercise(
                    displayName = R.string.hiking,
                    icon = R.drawable.hike,
                    exerciseType = ExerciseTypes.HIKING,
                    metricTypes =
                    listOf(
                        ExerciseMetrics.HEART_RATE,
                        ExerciseMetrics.DISTANCE,
                        ExerciseMetrics.ACTIVE_DURATION,
                        ExerciseMetrics.STEPS,
                        ExerciseMetrics.ABSOLUTE_ELEVATION,
                        ExerciseMetrics.ELEVATION_GAINED,
                        ExerciseMetrics.PACE,
                        ExerciseMetrics.PACE_AVERAGE,
                        ExerciseMetrics.TOTAL_CALORIES_BURNED,
                        ExerciseMetrics.STEPS_CADENCE,
                    ),
                )

            val TENNIS =
                Exercise(
                    displayName = R.string.tennis,
                    icon = R.drawable.tennis,
                    exerciseType = ExerciseTypes.TENNIS,
                    metricTypes =
                    listOf(
                        ExerciseMetrics.ACTIVE_DURATION,
                        ExerciseMetrics.STEPS,
                        ExerciseMetrics.TOTAL_CALORIES_BURNED,
                        ExerciseMetrics.DISTANCE,
                    ),
                )

            val ELLIPTICAL =
                Exercise(
                    displayName = R.string.elliptical,
                    icon = R.drawable.cardio,
                    exerciseType = ExerciseTypes.ELLIPTICAL,
                    metricTypes =
                    listOf(
                        ExerciseMetrics.HEART_RATE,
                        ExerciseMetrics.ACTIVE_DURATION,
                        ExerciseMetrics.TOTAL_CALORIES_BURNED,
                    ),
                )

            val WORKOUT_INDOOR =
                Exercise(
                    displayName = R.string.workout_indoor,
                    icon = R.drawable.workout,
                    exerciseType =
                    ExerciseTypes.RUNNING_TREADMILL, // TODO(smskelley): update once workout type added
                    metricTypes =
                    listOf(
                        ExerciseMetrics.HEART_RATE,
                        ExerciseMetrics.ACTIVE_DURATION,
                        ExerciseMetrics.TOTAL_CALORIES_BURNED,
                    ),
                )

            val WORKOUT_OUTDOOR =
                Exercise(
                    displayName = R.string.workout_outdoor,
                    icon = R.drawable.workout,
                    exerciseType = ExerciseTypes.BIKING, // TODO(smskelley): update once workout type added
                    metricTypes =
                    listOf(
                        ExerciseMetrics.HEART_RATE,
                        ExerciseMetrics.ACTIVE_DURATION,
                        ExerciseMetrics.TOTAL_CALORIES_BURNED,
                        ExerciseMetrics.DISTANCE,
                        ExerciseMetrics.SPEED,
                    ),
                )

            val YOGA =
                Exercise(
                    displayName = R.string.yoga,
                    icon = R.drawable.yoga,
                    exerciseType = ExerciseTypes.YOGA,
                    metricTypes =
                    listOf(
                        ExerciseMetrics.HEART_RATE,
                        ExerciseMetrics.ACTIVE_DURATION,
                        ExerciseMetrics.TOTAL_CALORIES_BURNED,
                    ),
                )

            val PICKLEBALL =
                Exercise(
                    displayName = R.string.pickleball,
                    icon = R.drawable.tennis,
                    exerciseType = ExerciseTypes.TENNIS,
                    metricTypes =
                    listOf(
                        ExerciseMetrics.HEART_RATE,
                        ExerciseMetrics.ACTIVE_DURATION,
                        ExerciseMetrics.STEPS,
                        ExerciseMetrics.TOTAL_CALORIES_BURNED,
                        ExerciseMetrics.DISTANCE,
                    ),
                )
        }
    }

    val exercises =
        listOf(
            Exercise.WALK,
            Exercise.RUN,
            Exercise.RUN_INDOOR,
            Exercise.BIKE,
            Exercise.BIKE_INDOOR,
            Exercise.HIKE,
            Exercise.TENNIS,
            Exercise.ELLIPTICAL,
            Exercise.WORKOUT_INDOOR,
            Exercise.WORKOUT_OUTDOOR,
            Exercise.YOGA,
            Exercise.PICKLEBALL,
        )

    val desiredExerciseMetricTypes =
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
