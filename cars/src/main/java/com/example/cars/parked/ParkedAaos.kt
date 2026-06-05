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

package com.example.cars.parked

import android.car.Car
import android.car.drivingstate.CarUxRestrictions
import android.car.drivingstate.CarUxRestrictionsManager
import android.content.Context
import android.content.pm.PackageManager

class ParkedAaos(private val context: Context) {

    fun checkUxRestrictions() {
        // [START android_cars_parked_automotive_os_ux_restrictions]
        val car = Car.createCar(context)
        val carUxRestrictionsManager =
            car.getCarManager(Car.CAR_UX_RESTRICTION_SERVICE) as CarUxRestrictionsManager

        // You can either read the state directly ...
        val currentUxRestrictions = carUxRestrictionsManager.currentCarUxRestrictions

        // or listen to state changes
        carUxRestrictionsManager.registerListener { carUxRestrictions: CarUxRestrictions ->
            // Handle UX restrictions
        }

        // Don't forget to teardown and release resources when they're no longer needed
        carUxRestrictionsManager.unregisterListener()
        car.disconnect()
        // [END android_cars_parked_automotive_os_ux_restrictions]
    }

    fun detectAaos() {
        val packageManager: PackageManager = context.packageManager
        // [START android_cars_parked_automotive_os_detect]
        val isCar = packageManager.hasSystemFeature(PackageManager.FEATURE_AUTOMOTIVE)
        if (isCar) {
            // Enable or disable a given feature
        }
        // [END android_cars_parked_automotive_os_detect]
    }
}
