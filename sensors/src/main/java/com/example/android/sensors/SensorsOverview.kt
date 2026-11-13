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

private object SensorsOverviewDeviceSensors {
    fun listSensors(sensorManager: SensorManager) {
        // [START android_sensors_overview_device_sensors]
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        // [END android_sensors_overview_device_sensors]
    }
}

private object SensorsOverviewSensorActivity {
    // [START android_sensors_overview_sensor_activity]
    class SensorActivity : ComponentActivity(), SensorEventListener {
        private lateinit var sensorManager: SensorManager
        private var mLight: Sensor? = null

        public override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
            mLight = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
        }

        override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
            // Do something here if sensor accuracy changes.
        }

        override fun onSensorChanged(event: SensorEvent) {
            // The light sensor returns a single value.
            // Many sensors return 3 values, one for each axis.
            val lux = event.values[0]
            // Do something with this sensor value.
        }

        override fun onResume() {
            super.onResume()
            mLight?.also { light ->
                sensorManager.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL)
            }
        }

        override fun onPause() {
            super.onPause()
            sensorManager.unregisterListener(this)
        }
    }
    // [END android_sensors_overview_sensor_activity]
}
