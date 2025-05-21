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
import androidx.wear.protolayout.ActionBuilders.launchAction
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TimelineBuilders.Timeline
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
import kotlin.jvm.java

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

class InteractionDeepLink : TileService() {

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
            TileActivity::class.java
          )
        )
        .startActivities()
    }
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
  }

  override fun onTileResourcesRequest(
    requestParams: ResourcesRequest
  ): ListenableFuture<Resources?> =
    Futures.immediateFuture(
      Resources.Builder().setVersion(requestParams.version).build()
    )

  fun MaterialScope.tileLayout(requestParams: RequestBuilders.TileRequest) =
    primaryLayout(
      mainSlot = {
        textButton(
          labelContent = {
            text("Message 1".layoutString, typography = BODY_LARGE)
          },
          onClick = clickable(id = "foo", action = loadAction()),
        )
      }
    )
}

class InteractionLaunchAction : BaseTileService() {

  override fun MaterialScope.tileLayout(
    requestParams: RequestBuilders.TileRequest
  ) =
    // [START android_wear_m3_interactions_launchaction_tile]
    primaryLayout(
      mainSlot = {
        textButton(
          labelContent = {
            text("Tap me!".layoutString, typography = BODY_LARGE)
          },
          onClick =
            clickable(
              id = "foo",
              action =
                launchAction(
                  ComponentName(
                    "com.example.wear",
                    "com.example.wear.snippets.m3.tile.TileActivity",
                  )
                ),
            ),
        )
      }
    )
  // [END android_wear_m3_interactions_launchaction_tile]
}
