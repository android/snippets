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

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import androidx.activity.ComponentActivity

private object SensorsPositionOrientationActivity {
    // [START android_sensors_position_orientation_activity]
    class SensorActivity : ComponentActivity(), SensorEventListener {

        private lateinit var sensorManager: SensorManager
        private val accelerometerReading = FloatArray(3)
        private val magnetometerReading = FloatArray(3)

        private val rotationMatrix = FloatArray(9)
        private val orientationAngles = FloatArray(3)

        public override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // Do something here if sensor accuracy changes.
            // You must implement this callback in your code.
        }

        override fun onResume() {
            super.onResume()

            // Get updates from the accelerometer and magnetometer at a constant rate.
            // To make batch operations more efficient and reduce power consumption,
            // provide support for delaying updates to the application.
            //
            // In this example, the sensor reporting delay is small enough such that
            // the application receives an update before the system checks the sensor
            // readings again.
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { accelerometer ->
                sensorManager.registerListener(
                    this,
                    accelerometer,
                    SensorManager.SENSOR_DELAY_NORMAL,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
            sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { magneticField ->
                sensorManager.registerListener(
                    this,
                    magneticField,
                    SensorManager.SENSOR_DELAY_NORMAL,
                    SensorManager.SENSOR_DELAY_UI
                )
            }
        }

        override fun onPause() {
            super.onPause()

            // Don't receive any more updates from either sensor.
            sensorManager.unregisterListener(this)
        }

        // Get readings from accelerometer and magnetometer. To simplify calculations,
        // consider storing these readings as unit vectors.
        override fun onSensorChanged(event: SensorEvent) {
            if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
                System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
            } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
                System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
            }
        }

        // Compute the three orientation angles based on the most recent readings from
        // the device's accelerometer and magnetometer.
        fun updateOrientationAngles() {
            // Update rotation matrix, which is needed to update orientation angles.
            SensorManager.getRotationMatrix(
                rotationMatrix,
                null,
                accelerometerReading,
                magnetometerReading
            )

            // "rotationMatrix" now has up-to-date information.

            SensorManager.getOrientation(rotationMatrix, orientationAngles)

            // "orientationAngles" now has up-to-date information.
        }
    }
    // [END android_sensors_position_orientation_activity]
}

private object SensorsPositionProximityActivity {
    // [START android_sensors_position_proximity_activity]
    class SensorActivity : ComponentActivity(), SensorEventListener {

        private lateinit var sensorManager: SensorManager
        private var proximity: Sensor? = null

        public override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Get an instance of the sensor service, and use that to get an instance of
            // a particular sensor.
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY)
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // Do something here if sensor accuracy changes.
        }

        override fun onSensorChanged(event: SensorEvent) {
            val distance = event.values[0]
            // Do something with this sensor data.
        }

        override fun onResume() {
            // Register a listener for the sensor.
            super.onResume()

            proximity?.also { proximity ->
                sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }

        override fun onPause() {
            // Be sure to unregister the sensor when the activity pauses.
            super.onPause()
            sensorManager.unregisterListener(this)
        }
    }
    // [END android_sensors_position_proximity_activity]
}
