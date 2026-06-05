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

import android.content.Intent
import androidx.car.app.CarContext
import androidx.car.app.CarToast
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.connection.CarConnection
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template

class MyConnectionSession(carContext: CarContext) : Session() {
    override fun onCreateScreen(intent: Intent): Screen {
        // [START android_cars_apps_library_connection_api_observe]
        CarConnection(carContext).type.observe(this, ::onConnectionStateUpdated)
        // [END android_cars_apps_library_connection_api_observe]
        return MyConnectionScreen(carContext)
    }

    // [START android_cars_apps_library_connection_api_handler]
    fun onConnectionStateUpdated(connectionState: Int) {
        val message = when (connectionState) {
            CarConnection.CONNECTION_TYPE_NOT_CONNECTED -> "Not connected to a head unit"
            CarConnection.CONNECTION_TYPE_NATIVE -> "Connected to Android Automotive OS"
            CarConnection.CONNECTION_TYPE_PROJECTION -> "Connected to Android Auto"
            else -> "Unknown car connection type"
        }
        CarToast.makeText(carContext, message, CarToast.LENGTH_SHORT).show()
    }
    // [END android_cars_apps_library_connection_api_handler]
}

class MyConnectionScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        return MessageTemplate.Builder("Connection API Demo").build()
    }
}
