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

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.Action
import androidx.car.app.model.Header
import androidx.car.app.model.MessageTemplate
import androidx.car.app.model.Template

class MyNavigationScreen(carContext: CarContext) : Screen(carContext) {

    override fun onGetTemplate(): Template {
        // [START android_cars_apps_library_screen_navigation]
        val header = Header.Builder()
            .setStartHeaderAction(Action.BACK)
            .build()

        val template = MessageTemplate.Builder("Hello world!")
            .setHeader(header)
            .addAction(
                Action.Builder()
                    .setTitle("Next screen")
                    .setOnClickListener { screenManager.push(NextScreen(carContext)) }
                    .build()
            )
            .build()
        // [END android_cars_apps_library_screen_navigation]
        return template
    }
}

class NextScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        return MessageTemplate.Builder("Next Screen").build()
    }
}
