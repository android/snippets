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

import android.content.ComponentName
import android.content.Intent
import androidx.core.app.TaskStackBuilder
import androidx.core.net.toUri
import androidx.wear.protolayout.ActionBuilders
import androidx.wear.protolayout.ActionBuilders.launchAction
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.expression.dynamicDataMapOf
import androidx.wear.protolayout.expression.intAppDataKey
import androidx.wear.protolayout.expression.mapTo
import androidx.wear.protolayout.expression.stringAppDataKey
import androidx.wear.protolayout.material3.MaterialScope
import androidx.wear.protolayout.material3.Typography.BODY_LARGE
import androidx.wear.protolayout.material3.materialScope
import androidx.wear.protolayout.material3.primaryLayout
import androidx.wear.protolayout.material3.text
import androidx.wear.protolayout.material3.textButton
import androidx.wear.protolayout.modifiers.clickable
import androidx.wear.protolayout.modifiers.loadAction
import androidx.wear.protolayout.types.layoutString
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.TileService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import java.util.Locale
import kotlin.random.Random

private const val RESOURCES_VERSION = "1"

abstract class BaseTileService : TileService() {

    override fun onTileRequest(
        requestParams: RequestBuilders.TileRequest
    ): ListenableFuture<Tile> =
        Futures.immediateFuture(
            Tile.Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setTileTimeline(
                    Timeline.fromLayoutElement(
                        materialScope(this, requestParams.deviceConfiguration) {
                            tileLayout(requestParams)
                        }
                    )
                )
                .build()
        )

    override fun onTileResourcesRequest(
        requestParams: ResourcesRequest
    ): ListenableFuture<Resources> =
        Futures.immediateFuture(
            Resources.Builder().setVersion(requestParams.version).build()
        )

    abstract fun MaterialScope.tileLayout(
        requestParams: RequestBuilders.TileRequest
    ): LayoutElementBuilders.LayoutElement
}

class HelloTileService : BaseTileService() {
    override fun MaterialScope.tileLayout(
        requestParams: RequestBuilders.TileRequest
    ) = primaryLayout(mainSlot = { text("Hello, World!".layoutString) })
}

class InteractionRefresh : BaseTileService() {
    override fun MaterialScope.tileLayout(
        requestParams: RequestBuilders.TileRequest
    ) =
        primaryLayout(
            // Output a debug code so we can see the layout changing
            titleSlot = {
                text(
                    String.format(
                        Locale.ENGLISH,
                        "Debug %06d",
                        Random.nextInt(0, 1_000_000),
                    )
                        .layoutString
                )
            },
            mainSlot = {
                // [START android_wear_m3_interaction_refresh]
                textButton(
                    onClick = clickable(loadAction()),
                    labelContent = { text("Refresh".layoutString) },
                )
                // [END android_wear_m3_interaction_refresh]
            },
        )
}

class InteractionDeepLink : TileService() {

    // [START android_wear_m3_interaction_deeplink_tile]
    override fun onTileRequest(
        requestParams: RequestBuilders.TileRequest
    ): ListenableFuture<Tile?> {
        val lastClickableId = requestParams.currentState.lastClickableId
        if (lastClickableId == "foo") {
            TaskStackBuilder.create(this)
                .addNextIntentWithParentStack(
                    Intent(
                        Intent.ACTION_VIEW,
                        "googleandroidsnippets://app/message_detail/1".toUri(),
                        this,
                        TileActivity::class.java,
                    )
                )
                .startActivities()
        }
        // ... User didn't tap a button (either first load or tapped somewhere else)
        // [START_EXCLUDE]
        return Futures.immediateFuture(
            Tile.Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setTileTimeline(
                    Timeline.fromLayoutElement(
                        materialScope(this, requestParams.deviceConfiguration) {
                            tileLayout(requestParams)
                        }
                    )
                )
                .build()
        )
        // [END_EXCLUDE]
    }

    // [END android_wear_m3_interaction_deeplink_tile]

    override fun onTileResourcesRequest(
        requestParams: ResourcesRequest
    ): ListenableFuture<Resources?> =
        Futures.immediateFuture(
            Resources.Builder().setVersion(requestParams.version).build()
        )

    fun MaterialScope.tileLayout(requestParams: RequestBuilders.TileRequest) =
        primaryLayout(
            mainSlot = {
                // [START android_wear_m3_interaction_deeplink_layout]
                textButton(
                    labelContent = {
                        text("Deep Link me!".layoutString, typography = BODY_LARGE)
                    },
                    onClick = clickable(id = "foo", action = loadAction()),
                )
                // [END android_wear_m3_interaction_deeplink_layout]
            }
        )
}

class InteractionLoadAction : BaseTileService() {

    // [START android_wear_m3_interaction_loadaction_request]
    override fun onTileRequest(
        requestParams: RequestBuilders.TileRequest
    ): ListenableFuture<Tile> {

        // When triggered by loadAction(), "name" will be "Javier", and "age" will
        // be 37.
        with(requestParams.currentState.stateMap) {
            val name = this[stringAppDataKey("name")]
            val age = this[intAppDataKey("age")]
        }

        // [START_EXCLUDE]
        return Futures.immediateFuture(
            Tile.Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setTileTimeline(
                    Timeline.fromLayoutElement(
                        materialScope(this, requestParams.deviceConfiguration) {
                            tileLayout(requestParams)
                        }
                    )
                )
                .build()
        )
        // [END_EXCLUDE]
    }
    // [START android_wear_m3_interaction_loadaction_request]

    override fun MaterialScope.tileLayout(
        requestParams: RequestBuilders.TileRequest
    ) =
        primaryLayout(
            // Output a debug code so we can verify that the reload happens
            titleSlot = {
                text(
                    String.format(
                        Locale.ENGLISH,
                        "Debug %06d",
                        Random.nextInt(0, 1_000_000),
                    )
                        .layoutString
                )
            },
            mainSlot = {
                // [START android_wear_m3_interaction_loadaction_layout]
                textButton(
                    labelContent = {
                        text("loadAction()".layoutString, typography = BODY_LARGE)
                    },
                    onClick =
                    clickable(
                        action =
                        loadAction(
                            dynamicDataMapOf(
                                stringAppDataKey("name") mapTo "Javier",
                                intAppDataKey("age") mapTo 37,
                            )
                        )
                    ),
                )
                // [END android_wear_m3_interaction_loadaction_layout]
            },
        )
}

class InteractionLaunchAction : BaseTileService() {

    override fun MaterialScope.tileLayout(
        requestParams: RequestBuilders.TileRequest
    ) =
        primaryLayout(
            mainSlot = {
                // [START android_wear_m3_interactions_launchaction]
                textButton(
                    labelContent = {
                        text("launchAction()".layoutString, typography = BODY_LARGE)
                    },
                    onClick =
                    clickable(
                        action =
                        launchAction(
                            ComponentName(
                                "com.example.wear",
                                "com.example.wear.snippets.m3.tile.TileActivity",
                            ),
                            mapOf(
                                "name" to ActionBuilders.stringExtra("Bartholomew"),
                                "age" to ActionBuilders.intExtra(21),
                            ),
                        )
                    ),
                )
                // [END android_wear_m3_interactions_launchaction]
            }
        )
}
