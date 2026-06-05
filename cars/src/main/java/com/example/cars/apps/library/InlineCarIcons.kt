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

import android.text.SpannableString
import android.text.Spanned
import androidx.car.app.CarContext
import androidx.car.app.model.CarIcon
import androidx.car.app.model.CarIconSpan
import androidx.car.app.model.Row
import androidx.core.graphics.drawable.IconCompat
import com.example.cars.R

class InlineCarIcons(private val carContext: CarContext) {

    fun addInlineIcon() {
        // [START android_cars_apps_library_inline_car_icons]
        val rating = SpannableString("Rating: 4.5 stars")
        rating.setSpan(
            CarIconSpan.create(
                // Create a CarIcon with an image of four and a half stars
                CarIcon.Builder(
                    IconCompat.createWithResource(carContext, R.drawable.ic_star)
                ).build(),
                // Align the CarIcon to the baseline of the text
                CarIconSpan.ALIGN_BASELINE
            ),
            // The start index of the span (index of the character '4')
            8,
            // The end index of the span (index of the last 's' in "stars")
            16,
            Spanned.SPAN_INCLUSIVE_INCLUSIVE
        )

        val row = Row.Builder()
            .setTitle("Rating Row")
            .addText(rating)
            .build()
        // [END android_cars_apps_library_inline_car_icons]
    }
}
