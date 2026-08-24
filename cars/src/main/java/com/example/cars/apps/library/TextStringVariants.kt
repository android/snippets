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
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarText
import androidx.car.app.model.GridItem

class TextStringVariants(private val carContext: CarContext) {

    fun addVariants() {
        // [START android_cars_apps_library_text_string_variants_builder]
        val itemTitle = CarText.Builder("This is a very long string")
            .addVariant("Shorter string")
            .build()
        // [END android_cars_apps_library_text_string_variants_builder]

        // [START android_cars_apps_library_text_string_variants_grid_item]
        val gridItem = GridItem.Builder()
            .setTitle(itemTitle)
            .setImage(CarIcon.APP_ICON)
            .build()
        // [END android_cars_apps_library_text_string_variants_grid_item]
    }
}
