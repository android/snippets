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

private object SensorsEnvironmentPressureActivity {
    // [START android_sensors_environment_pressure_activity]
    class SensorActivity : ComponentActivity(), SensorEventListener {

        private lateinit var sensorManager: SensorManager
        private var pressure: Sensor? = null

        public override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // Get an instance of the sensor service, and use that to get an instance of
            // a particular sensor.
            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            pressure = sensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE)
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // Do something here if sensor accuracy changes.
        }

        override fun onSensorChanged(event: SensorEvent) {
            val millibarsOfPressure = event.values[0]
            // Do something with this sensor data.
        }

        override fun onResume() {
            // Register a listener for the sensor.
            super.onResume()
            sensorManager.registerListener(this, pressure, SensorManager.SENSOR_DELAY_NORMAL)
        }

        override fun onPause() {
            // Be sure to unregister the sensor when the activity pauses.
            super.onPause()
            sensorManager.unregisterListener(this)
        }
    }
    // [END android_sensors_environment_pressure_activity]
}
