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

package com.example.cars.templated_apps

import androidx.car.app.CarContext
import androidx.car.app.Screen
import androidx.car.app.model.PlaceListMapTemplate
import androidx.car.app.model.Template

class MainScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        // [START android_cars_templated_apps_placelistmaptemplate_setOnContentRefreshListener]
        return PlaceListMapTemplate.Builder()
            // ...
            .setOnContentRefreshListener {
                // Execute any desired logic
                // ...
                // Then call invalidate() so onGetTemplate() is called again
                invalidate()
            }
            .build()
        // [END android_cars_templated_apps_placelistmaptemplate_setOnContentRefreshListener]
    }
}
