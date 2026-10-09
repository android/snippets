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

package com.example.compose.snippets.resources

import android.content.res.Resources
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import com.example.compose.snippets.R

private fun readBooleanResource(resources: Resources) {
    // [START android_resources_more_boolean]
    val screenIsSmall: Boolean = resources.getBoolean(R.bool.screen_small)
    // [END android_resources_more_boolean]
}

private fun readColorResource(resources: Resources) {
    // [START android_resources_more_color]
    val color: Int = resources.getColor(R.color.opaque_red, null)
    // [END android_resources_more_color]
}

private fun readDimensionResource(resources: Resources) {
    // [START android_resources_more_dimension]
    val fontSize: Float = resources.getDimension(R.dimen.font_size)
    // [END android_resources_more_dimension]
}

private fun readIntegerResource(resources: Resources) {
    // [START android_resources_more_integer]
    val maxSpeed: Int = resources.getInteger(R.integer.max_speed)
    // [END android_resources_more_integer]
}

private fun readIntArrayResource(resources: Resources) {
    // [START android_resources_more_int_array]
    val bits: IntArray = resources.getIntArray(R.array.bits)
    // [END android_resources_more_int_array]
}

private fun readTypedArrayResources(resources: Resources) {
    // [START android_resources_more_typed_array]
    val icons: TypedArray = resources.obtainTypedArray(R.array.icons)
    val drawable: Drawable? = icons.getDrawable(0)

    val colors: TypedArray = resources.obtainTypedArray(R.array.colors)
    val color: Int = colors.getColor(0, 0)
    // [END android_resources_more_typed_array]
}
