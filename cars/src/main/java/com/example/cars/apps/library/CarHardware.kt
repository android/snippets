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

package com.example.cars.apps.library

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.car.app.CarContext
import androidx.car.app.hardware.CarHardwareManager
import androidx.car.app.hardware.common.CarValue
import androidx.car.app.hardware.common.OnCarDataAvailableListener
import androidx.car.app.hardware.info.CarSensors
import androidx.car.app.hardware.info.Compass
import androidx.car.app.hardware.info.EnergyLevel

class CarHardware(private val carContext: CarContext) {

    @RequiresApi(Build.VERSION_CODES.P)
    fun demoCarInfo() {
        // [START android_cars_apps_library_car_hardware_info]
        val carInfo = carContext.getCarService(CarHardwareManager::class.java).carInfo

        val listener = OnCarDataAvailableListener<EnergyLevel> { data ->
            if (data.rangeRemainingMeters.status == CarValue.STATUS_SUCCESS) {
                val rangeRemaining = data.rangeRemainingMeters.value
            } else {
                // Handle error
            }
        }

        carInfo.addEnergyLevelListener(carContext.mainExecutor, listener)
        // ...
        // Unregister the listener when you no longer need updates
        carInfo.removeEnergyLevelListener(listener)
        // [END android_cars_apps_library_car_hardware_info]
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun demoCarSensors() {
        // [START android_cars_apps_library_car_hardware_sensors]
        val carSensors = carContext.getCarService(CarHardwareManager::class.java).carSensors

        val listener = OnCarDataAvailableListener<Compass> { data ->
            if (data.orientations.status == CarValue.STATUS_SUCCESS) {
                val orientation = data.orientations.value
            } else {
                // Data not available, handle error
            }
        }

        carSensors.addCompassListener(CarSensors.UPDATE_RATE_NORMAL, carContext.mainExecutor, listener)
        // ...
        // Unregister the listener when you no longer need updates
        carSensors.removeCompassListener(listener)
        // [END android_cars_apps_library_car_hardware_sensors]
    }
}
