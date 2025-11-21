/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.wear.snippets.tile

import android.content.Context
import androidx.wear.protolayout.DeviceParametersBuilders
import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.LayoutElementBuilders.Column
import androidx.wear.protolayout.ModifiersBuilders
import androidx.wear.protolayout.TypeBuilders
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.protolayout.material.layouts.PrimaryLayout
import androidx.wear.protolayout.material3.MaterialScope
import androidx.wear.protolayout.material3.materialScope
import androidx.wear.protolayout.material3.primaryLayout
import androidx.wear.protolayout.material3.text
import androidx.wear.protolayout.modifiers.LayoutModifier
import androidx.wear.protolayout.modifiers.opacity
import androidx.wear.protolayout.types.layoutString

class LayoutM2 {
    // [START android_wear_tile_version_layoutm2]
    fun myLayout(
        context: Context,
        deviceConfiguration: DeviceParametersBuilders.DeviceParameters
    ) =
        PrimaryLayout.Builder(deviceConfiguration)
            .setResponsiveContentInsetEnabled(true)
            .setContent(
                Text.Builder(context, "Hello World!")
                    .setTypography(Typography.TYPOGRAPHY_BODY1)
                    .build()
            )
            .build()
    // [END android_wear_tile_version_layoutm2]

    // [START android_wear_tile_version_modifierm2]
    // Uses Builder-style modifier to set opacity
    fun myModifier(): ModifiersBuilders.Modifiers =
        ModifiersBuilders.Modifiers.Builder()
            .setOpacity(TypeBuilders.FloatProp.Builder(0.5F).build())
            .build()
    // [END android_wear_tile_version_modifierm2]
}

class LayoutM3 {
    // [START android_wear_tile_version_layoutm3]
    fun myLayout(
        context: Context,
        deviceConfiguration: DeviceParametersBuilders.DeviceParameters,
    ) =
        materialScope(context, deviceConfiguration) {
            primaryLayout(mainSlot = { text("Hello, World!".layoutString) })
        }
    // [END android_wear_tile_version_layoutm3]

    // [START android_wear_tile_version_modifierm3]
    // Uses Compose-like modifiers to set opacity
    fun myModifier(): LayoutModifier = LayoutModifier.opacity(0.5F)
    // [END android_wear_tile_version_modifierm3]
}

private fun MaterialScope.withoutHelpers() {
    // [START android_wear_tile_version_layoutwithouthelpers]
    primaryLayout(
        mainSlot = {
            Column.Builder()
                .setWidth(expand())
                .setHeight(expand())
                .addContent(text("A".layoutString))
                .addContent(text("B".layoutString))
                .addContent(text("C".layoutString))
                .build()
        }
    )
    // [END android_wear_tile_version_layoutwithouthelpers]
}

private fun MaterialScope.withHelpers() {
    // [START android_wear_tile_version_layoutwithhelpers]
    // Function literal with receiver helper function
    fun column(builder: Column.Builder.() -> Unit) =
        Column.Builder().apply(builder).build()

    primaryLayout(
        mainSlot = {
            column {
                setWidth(expand())
                setHeight(expand())
                addContent(text("A".layoutString))
                addContent(text("B".layoutString))
                addContent(text("C".layoutString))
            }
        }
    )
    // [END android_wear_tile_version_layoutwithhelpers]
}
