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
import androidx.car.app.constraints.ConstraintManager

class Constraints(private val carContext: CarContext) {

    fun setupAndQueryLimit() {
        // [START android_cars_apps_library_constraints_api_get_contstraint_manager]
        val manager = carContext.getCarService(ConstraintManager::class.java)
        // [END android_cars_apps_library_constraints_api_get_contstraint_manager]

        // [START android_cars_apps_library_constraints_api_limit]
        val gridItemLimit = manager.getContentLimit(ConstraintManager.CONTENT_LIMIT_TYPE_GRID)
        // [END android_cars_apps_library_constraints_api_limit]
    }
}
