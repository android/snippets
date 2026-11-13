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

package com.example.android.sensors

import android.app.Activity
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.hardware.TriggerEvent
import android.hardware.TriggerEventListener
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

private object SensorsMotionGravity {
    fun init(context: Context) {
        context.apply {
            // [START android_sensors_motion_default_gravity]
            val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY)
            // [END android_sensors_motion_default_gravity]
        }
    }
}

private object SensorsMotionLinearAcceleration {
    fun init(context: Context) {
        context.apply {
            // [START android_sensors_motion_default_linear_acceleration]
            val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
            // [END android_sensors_motion_default_linear_acceleration]
        }
    }
}

private object SensorsMotionRotationVector {
    fun init(context: Context) {
        context.apply {
            // [START android_sensors_motion_default_rotation_vector]
            val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
            // [END android_sensors_motion_default_rotation_vector]
        }
    }
}

private object SensorsMotionSignificantMotion {
    fun init(context: Context) {
        context.apply {
            // [START android_sensors_motion_significant_motion]
            val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val mSensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_SIGNIFICANT_MOTION)
            val triggerEventListener = object : TriggerEventListener() {
                override fun onTrigger(event: TriggerEvent?) {
                    // Do work
                }
            }
            mSensor?.also { sensor ->
                sensorManager.requestTriggerSensor(triggerEventListener, sensor)
            }
            // [END android_sensors_motion_significant_motion]
        }
    }
}

private object SensorsMotionStepCounter {
    fun init(context: Context) {
        context.apply {
            // [START android_sensors_motion_default_step_counter]
            val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
            // [END android_sensors_motion_default_step_counter]
        }
    }
}

private object SensorsMotionStepDetector {
    fun init(context: Context) {
        context.apply {
            // [START android_sensors_motion_default_step_detector]
            val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
            // [END android_sensors_motion_default_step_detector]
        }
    }
}

private object SensorsMotionAccelerometer {
    fun init(context: Context) {
        context.apply {
            // [START android_sensors_motion_default_accelerometer]
            val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            // [END android_sensors_motion_default_accelerometer]
        }
    }
}

private object SensorsMotionFilterAccelerometer {
    class SensorActivity : Activity(), SensorEventListener {
        private val gravity = FloatArray(3)
        private val linear_acceleration = FloatArray(3)

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {}

        // [START android_sensors_motion_filter_accelerometer]
        override fun onSensorChanged(event: SensorEvent) {
            // In this example, alpha is calculated as t / (t + dT),
            // where t is the low-pass filter's time-constant and
            // dT is the event delivery rate.

            val alpha: Float = 0.8f

            // Isolate the force of gravity with the low-pass filter.
            gravity[0] = alpha * gravity[0] + (1 - alpha) * event.values[0]
            gravity[1] = alpha * gravity[1] + (1 - alpha) * event.values[1]
            gravity[2] = alpha * gravity[2] + (1 - alpha) * event.values[2]

            // Remove the gravity contribution with the high-pass filter.
            linear_acceleration[0] = event.values[0] - gravity[0]
            linear_acceleration[1] = event.values[1] - gravity[1]
            linear_acceleration[2] = event.values[2] - gravity[2]
        }
        // [END android_sensors_motion_filter_accelerometer]
    }
}

private object SensorsMotionGyroscope {
    fun init(context: Context) {
        context.apply {
            // [START android_sensors_motion_default_gyroscope]
            val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
            // [END android_sensors_motion_default_gyroscope]
        }
    }
}

private object SensorsMotionGyroscopeDeltaRotation {
    private const val EPSILON: Float = 0.000000001f

    class SensorActivity : Activity(), SensorEventListener {
        override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

        // [START android_sensors_motion_gyroscope_delta_rotation]
        // Create a constant to convert nanoseconds to seconds.
        private val NS2S = 1.0f / 1000000000.0f
        private val deltaRotationVector = FloatArray(4) { 0f }
        private var timestamp: Float = 0f

        override fun onSensorChanged(event: SensorEvent?) {
            // This timestep's delta rotation to be multiplied by the current rotation
            // after computing it from the gyro sample data.
            if (timestamp != 0f && event != null) {
                val dT = (event.timestamp - timestamp) * NS2S
                // Axis of the rotation sample, not normalized yet.
                var axisX: Float = event.values[0]
                var axisY: Float = event.values[1]
                var axisZ: Float = event.values[2]

                // Calculate the angular speed of the sample
                val omegaMagnitude: Float = sqrt(axisX * axisX + axisY * axisY + axisZ * axisZ)

                // Normalize the rotation vector if it's big enough to get the axis
                // (that is, EPSILON should represent your maximum allowable margin of error)
                if (omegaMagnitude > EPSILON) {
                    axisX /= omegaMagnitude
                    axisY /= omegaMagnitude
                    axisZ /= omegaMagnitude
                }

                // Integrate around this axis with the angular speed by the timestep
                // in order to get a delta rotation from this sample over the timestep
                // We will convert this axis-angle representation of the delta rotation
                // into a quaternion before turning it into the rotation matrix.
                val thetaOverTwo: Float = omegaMagnitude * dT / 2.0f
                val sinThetaOverTwo: Float = sin(thetaOverTwo)
                val cosThetaOverTwo: Float = cos(thetaOverTwo)
                deltaRotationVector[0] = sinThetaOverTwo * axisX
                deltaRotationVector[1] = sinThetaOverTwo * axisY
                deltaRotationVector[2] = sinThetaOverTwo * axisZ
                deltaRotationVector[3] = cosThetaOverTwo
            }
            timestamp = event?.timestamp?.toFloat() ?: 0f
            val deltaRotationMatrix = FloatArray(9) { 0f }
            SensorManager.getRotationMatrixFromVector(deltaRotationMatrix, deltaRotationVector);
            // User code should concatenate the delta rotation we computed with the current rotation
            // in order to get the updated rotation.
            // rotationCurrent = rotationCurrent * deltaRotationMatrix;
        }
        // [END android_sensors_motion_gyroscope_delta_rotation]
    }
}

private object SensorsMotionGyroscopeUncalibrated {
    fun init(context: Context) {
        context.apply {
            // [START android_sensors_motion_default_gyroscope_uncalibrated]
            val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            val sensor: Sensor? = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE_UNCALIBRATED)
            // [END android_sensors_motion_default_gyroscope_uncalibrated]
        }
    }
}
