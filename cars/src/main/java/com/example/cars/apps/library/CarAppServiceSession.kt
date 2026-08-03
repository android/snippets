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
import androidx.car.app.CarAppService
import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.Session
import androidx.car.app.SessionInfo
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template
import androidx.car.app.validation.HostValidator

// [START android_cars_apps_library_carappservice_session_service]
class HelloWorldService : CarAppService() {
    override fun onCreateSession(sessionInfo: SessionInfo): Session {
        return HelloWorldSession()
    }
    // [START_EXCLUDE]
    override fun createHostValidator(): HostValidator {
        return HostValidator.ALLOW_ALL_HOSTS_VALIDATOR
    }
    // [END_EXCLUDE]
}
// [END android_cars_apps_library_carappservice_session_service]

// [START android_cars_apps_library_carappservice_session_instance]
class HelloWorldSession : Session() {
    override fun onCreateScreen(intent: Intent): Screen {
        return HelloWorldScreen(carContext)
    }
}
// [END android_cars_apps_library_carappservice_session_instance]

class HelloWorldScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        return MessageTemplate.Builder("Hello World").build()
    }
}
