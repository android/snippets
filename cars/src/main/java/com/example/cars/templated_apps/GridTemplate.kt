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
import androidx.car.app.model.Action
import androidx.car.app.model.CarIcon
import androidx.car.app.model.GridItem
import androidx.car.app.model.GridTemplate
import androidx.car.app.model.Header
import androidx.car.app.model.ItemList
import androidx.car.app.model.Template
import androidx.core.graphics.drawable.IconCompat
import com.example.cars.R

class IOTScreen(carContext: CarContext) : Screen(carContext) {
    override fun onGetTemplate(): Template {
        // [START android_cars_templated_apps_gridtemplate_iot]
        val listBuilder = ItemList.Builder()
        val headerBuilder = Header.Builder()
        val garageIcon = IconCompat.createWithResource(
            carContext,
            R.drawable.ic_garage
        )

        listBuilder.addItem(
            GridItem.Builder()
                .setTitle("Garage door")
                .setImage(
                    CarIcon.Builder(garageIcon).build(),
                    GridItem.IMAGE_TYPE_ICON
                )
                .setOnClickListener {
                    // Handle user interactions
                }
                .build()
        )

        listBuilder.addItem(
            GridItem.Builder()
                .setTitle("Garage lights")
                // Show a loading indicator until the status of the device is known
                // (call invalidate() when the status is known to refresh the screen)
                .setLoading(true)
                .build()
        )

        return GridTemplate.Builder()
            .setHeader(
                headerBuilder.setTitle("Devices")
                    .setStartHeaderAction(Action.APP_ICON).build()
            )
            .setSingleList(listBuilder.build())
            .build()
        // [END android_cars_templated_apps_gridtemplate_iot]
    }
}
