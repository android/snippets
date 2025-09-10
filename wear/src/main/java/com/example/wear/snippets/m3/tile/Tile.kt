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

package com.example.wear.snippets.m3.tile

import android.content.Context
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.material3.Typography.BODY_LARGE
import androidx.wear.protolayout.material3.button
import androidx.wear.protolayout.material3.buttonGroup
import androidx.wear.protolayout.material3.materialScope
import androidx.wear.protolayout.material3.primaryLayout
import androidx.wear.protolayout.material3.text
import androidx.wear.protolayout.modifiers.clickable
import androidx.wear.protolayout.types.layoutString
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.TileService
import androidx.wear.tiles.tooling.preview.Preview
import androidx.wear.tiles.tooling.preview.TilePreviewData
import androidx.wear.tiles.tooling.preview.TilePreviewHelper
import androidx.wear.tooling.preview.devices.WearDevices
import com.google.common.util.concurrent.Futures

private const val RESOURCES_VERSION = "1"

// [START android_wear_m3_tile_mytileservice]
class MyTileService : TileService() {

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest) =
        Futures.immediateFuture(
            Tile.Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setTileTimeline(
                    Timeline.fromLayoutElement(
                        materialScope(this, requestParams.deviceConfiguration) {
                            primaryLayout(
                                mainSlot = {
                                    text("Hello, World!".layoutString, typography = BODY_LARGE)
                                }
                            )
                        }
                    )
                )
                .build()
        )

    override fun onTileResourcesRequest(requestParams: ResourcesRequest) =
        Futures.immediateFuture(Resources.Builder().setVersion(RESOURCES_VERSION).build())
}

// [END android_wear_m3_tile_mytileservice]

class TileBreakpoints : TileService() {

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest) =
        Futures.immediateFuture(
            Tile.Builder()
                .setTileTimeline(
                    Timeline.fromLayoutElement(
                        // [START android_wear_tile_breakpoints]
                        materialScope(this, requestParams.deviceConfiguration) {
                            // [START_EXCLUDE]
                            val button1 =
                                button(
                                    onClick = clickable(),
                                    labelContent = { text("button1".layoutString) },
                                )
                            val button2 =
                                button(
                                    onClick = clickable(),
                                    labelContent = { text("button2".layoutString) },
                                )
                            val button3 =
                                button(
                                    onClick = clickable(),
                                    labelContent = { text("button3".layoutString) },
                                )
                            val button4 =
                                button(
                                    onClick = clickable(),
                                    labelContent = { text("button4".layoutString) },
                                )
                            val button5 =
                                button(
                                    onClick = clickable(),
                                    labelContent = { text("button5".layoutString) },
                                )
                            // [END_EXCLUDE]
                            val isLargeScreen = deviceConfiguration.screenWidthDp >= 225
                            primaryLayout(
                                mainSlot = {
                                    buttonGroup {
                                        buttonGroupItem { button1 }
                                        buttonGroupItem { button2 }
                                        buttonGroupItem { button3 }
                                        if (isLargeScreen) {
                                            buttonGroupItem { button4 }
                                            buttonGroupItem { button5 }
                                        }
                                    }
                                }
                            )
                        }
                        // [END android_wear_tile_breakpoints]
                    )
                )
                .build()
        )
}

// [START android_wear_tile_preview]
@Preview(device = WearDevices.LARGE_ROUND)
fun smallPreview(context: Context) = TilePreviewData {
    TilePreviewHelper.singleTimelineEntryTileBuilder(
        materialScope(context, it.deviceConfiguration) {
            primaryLayout(mainSlot = { text("Hello, World".layoutString) })
        }
    )
        .build()
}
// [END android_wear_tile_preview]
