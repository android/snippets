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
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.LayoutElementBuilders.Column
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.StateBuilders
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.expression.DynamicBuilders
import androidx.wear.protolayout.expression.dynamicDataMapOf
import androidx.wear.protolayout.expression.intAppDataKey
import androidx.wear.protolayout.expression.mapTo
import androidx.wear.protolayout.expression.stringAppDataKey
import androidx.wear.protolayout.material3.MaterialScope
import androidx.wear.protolayout.material3.Typography.BODY_LARGE
import androidx.wear.protolayout.material3.Typography.BODY_MEDIUM
import androidx.wear.protolayout.material3.button
import androidx.wear.protolayout.material3.buttonGroup
import androidx.wear.protolayout.material3.materialScope
import androidx.wear.protolayout.material3.primaryLayout
import androidx.wear.protolayout.material3.text
import androidx.wear.protolayout.material3.textButton
import androidx.wear.protolayout.modifiers.clickable
import androidx.wear.protolayout.modifiers.loadAction
import androidx.wear.protolayout.types.asLayoutConstraint
import androidx.wear.protolayout.types.asLayoutString
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
import com.google.common.util.concurrent.ListenableFuture

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

fun MaterialScope.myAdaptiveLayout() =
    primaryLayout(mainSlot = { text("Hello, World".layoutString) })

// [START android_wear_tile_preview]
@Preview(device = WearDevices.LARGE_ROUND)
fun smallPreview(context: Context) = TilePreviewData {
    TilePreviewHelper.singleTimelineEntryTileBuilder(
        materialScope(context, it.deviceConfiguration) {
            myAdaptiveLayout() // varies the layout depending on the size of the screen
        }
    )
        .build()
}
// [END android_wear_tile_preview]

class StateTile : TileService() {
    // [START android_wear_tile_dynamic_ontilerequest]
    override fun onTileRequest(
        requestParams: RequestBuilders.TileRequest
    ): ListenableFuture<Tile?> {
        // If the tile hasn't had any state set yet, use the default values
        val state =
            if (requestParams.currentState.keyToValueMapping.isNotEmpty())
                requestParams.currentState
            else
                StateBuilders.State.Builder()
                    .setStateMap(
                        dynamicDataMapOf(
                            KEY_WATER_INTAKE mapTo 200,
                            KEY_NOTE mapTo "Good"
                        )
                    )
                    .build()

        return Futures.immediateFuture(
            Tile.Builder()
                // Set resources, timeline, and other tile properties.
                // [START_EXCLUDE silent]
                .setTileTimeline(
                    Timeline.fromLayoutElement(
                        materialScope(this, requestParams.deviceConfiguration) {
                            tileLayout()
                        }
                    )
                )
                // [END_EXCLUDE]
                .setState(state)
                .build()
        )
    }
    // [END android_wear_tile_dynamic_ontilerequest]

    fun MaterialScope.tileLayout(): LayoutElementBuilders.LayoutElement {
        return primaryLayout(
            mainSlot = {
                // [START android_wear_tile_dynamic_showdata]
                val waterIntakeValue =
                    DynamicBuilders.DynamicInt32.from(KEY_WATER_INTAKE)
                        // [END android_wear_tile_dynamic_showdata]
                        .format()
                        .asLayoutString("0", "000".asLayoutConstraint())
                val noteValue =
                    DynamicBuilders.DynamicString.from(KEY_NOTE)
                        .asLayoutString(
                            "",
                            "Note about day".asLayoutConstraint()
                        )

                // [START android_wear_tile_dynamic_loadaction]
                val loadAction =
                    loadAction(
                        dynamicDataMapOf(
                            KEY_WATER_INTAKE mapTo 400,
                            KEY_NOTE mapTo "Outstanding"
                        )
                    )
                // [END android_wear_tile_dynamic_loadaction]

                Column.Builder()
                    .addContent(text(waterIntakeValue, typography = BODY_LARGE))
                    .addContent(text(noteValue, typography = BODY_MEDIUM))
                    .addContent(
                        textButton(
                            onClick = clickable(loadAction),
                            labelContent = { text("Load".layoutString) }
                        )
                    )
                    .build()
            }
        )
    }

    // [START android_wear_tile_dynamic_companion]
    companion object {
        val KEY_WATER_INTAKE = intAppDataKey("key_water_intake")
        val KEY_NOTE = stringAppDataKey("key_note")
    }
    // [END android_wear_tile_dynamic_companion]
}
