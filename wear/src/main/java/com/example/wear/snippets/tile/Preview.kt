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
import androidx.annotation.DrawableRes
import androidx.wear.protolayout.DeviceParametersBuilders.DeviceParameters
import androidx.wear.protolayout.ResourceBuilders
import androidx.wear.protolayout.material3.materialScope
import androidx.wear.protolayout.material3.primaryLayout
import androidx.wear.protolayout.material3.text
import androidx.wear.protolayout.types.stringLayoutConstraint
import androidx.wear.tiles.tooling.preview.Preview
import androidx.wear.tiles.tooling.preview.TilePreviewData
import androidx.wear.tiles.tooling.preview.TilePreviewHelper
import androidx.wear.tooling.preview.devices.WearDevices
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.expression.DynamicDataBuilders
import androidx.wear.protolayout.expression.PlatformDataValues
import androidx.wear.protolayout.expression.PlatformHealthSources
import com.example.wear.R
import androidx.wear.protolayout.types.layoutString
import androidx.wear.protolayout.types.asLayoutString

// [START android_wear_tile_preview_simple]
@Preview(device = WearDevices.SMALL_ROUND)
@Preview(device = WearDevices.LARGE_ROUND)
fun tilePreview(context: Context) = TilePreviewData { request ->
    TilePreviewHelper.singleTimelineEntryTileBuilder(
        buildMyTileLayout(context, request.deviceConfiguration)
    ).build()
}
// [END android_wear_tile_preview_simple]

fun buildMyTileLayout(
    context: Context,
    deviceParameters: DeviceParameters,
) = materialScope(context = context, deviceConfiguration = deviceParameters) {
        primaryLayout(
            mainSlot = {
                text("Hello world!".layoutString)
            }
        )
    }

private const val RESOURCES_VERSION = "1"
private const val myImageId = "myImageId"

// [START android_wear_tile_preview_resources]
@Preview(device = WearDevices.SMALL_ROUND)
fun previewWithResources(context: Context) = TilePreviewData(
    onTileResourceRequest = { request ->
        Resources.Builder()
            .setVersion(RESOURCES_VERSION)
            .addIdToImageMapping(
                myImageId,
                getImageById(R.drawable.animated_walk)
            )
            .build()
    },
    onTileRequest = { request ->
        TilePreviewHelper.singleTimelineEntryTileBuilder(
            buildMyTileLayout(context, request.deviceConfiguration)
        ).build()
    }
)
// [END android_wear_tile_preview_resources]

fun getImageById(
    @DrawableRes id: Int,
): ResourceBuilders.ImageResource =
    ResourceBuilders.ImageResource.Builder()
        .setAndroidResourceByResId(
            ResourceBuilders.AndroidImageResourceByResId.Builder()
                .setResourceId(id)
                .build(),
        )
        .build()

fun buildMyTileLayoutDynamic(
    context: Context,
    deviceParameters: DeviceParameters,
) = materialScope(context = context, deviceConfiguration = deviceParameters) {
    primaryLayout(
        mainSlot = {
            text(
                text =
                PlatformHealthSources.heartRateBpm()
                    .format()
                    .asLayoutString("--", stringLayoutConstraint("999"))
            )
        }
    )
}

// [START android_wear_tile_preview_platform]
@Preview(device = WearDevices.SMALL_ROUND)
fun previewWithPlatformOverride(context: Context) = TilePreviewData(
    platformDataValues = PlatformDataValues.of(
        PlatformHealthSources.Keys.HEART_RATE_BPM,
        DynamicDataBuilders.DynamicDataValue.fromFloat(160f)
    ),
    onTileRequest = { request ->
        TilePreviewHelper.singleTimelineEntryTileBuilder(
            buildMyTileLayoutDynamic(context, request.deviceConfiguration)
        ).build()
    }
)
// [END android_wear_tile_preview_platform]
