/*
 * Copyright 2022 The Android Open Source Project
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

import android.Manifest
import android.content.Context
import androidx.annotation.RequiresPermission
import androidx.wear.protolayout.ColorBuilders.argb
import androidx.wear.protolayout.DimensionBuilders
import androidx.wear.protolayout.LayoutElementBuilders
import androidx.wear.protolayout.ResourceBuilders.Resources
import androidx.wear.protolayout.TimelineBuilders
import androidx.wear.protolayout.TimelineBuilders.Timeline
import androidx.wear.protolayout.TypeBuilders
import androidx.wear.protolayout.expression.DynamicBuilders
import androidx.wear.protolayout.expression.PlatformHealthSources
import androidx.wear.protolayout.material.Text
import androidx.wear.protolayout.material.Typography
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.RequestBuilders.ResourcesRequest
import androidx.wear.tiles.TileBuilders.Tile
import androidx.wear.tiles.TileService
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture

private const val RESOURCES_VERSION = "1"

// [START android_wear_tile_mytileservice]
class MyTileService : TileService() {

    override fun onTileRequest(requestParams: RequestBuilders.TileRequest) =
        Futures.immediateFuture(
            Tile.Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setTileTimeline(
                    Timeline.fromLayoutElement(
                        Text.Builder(this, "Hello World!")
                            .setTypography(Typography.TYPOGRAPHY_BODY1)
                            .setColor(argb(0xFFFFFFFF.toInt()))
                            .build()
                    )
                )
                .build()
        )

    override fun onTileResourcesRequest(requestParams: ResourcesRequest) =
        Futures.immediateFuture(Resources.Builder().setVersion(RESOURCES_VERSION).build())
}

// [END android_wear_tile_mytileservice]

fun simpleLayout(context: Context) =
    Text.Builder(context, "Hello World!")
        .setTypography(Typography.TYPOGRAPHY_BODY1)
        .setColor(argb(0xFFFFFFFF.toInt()))
        .build()

class PeriodicUpdatesSingleEntry : TileService() {
    // [START android_wear_tile_periodic_single_entry]
    override fun onTileRequest(
        requestParams: RequestBuilders.TileRequest
    ): ListenableFuture<Tile?> {
        val tile =
            Tile.Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                // We add a single timeline entry when our layout is fixed, and
                // we don't know in advance when its contents might change.
                .setTileTimeline(Timeline.fromLayoutElement(simpleLayout(this)))
                .build()
        return Futures.immediateFuture(tile)
    }
    // [END android_wear_tile_periodic_single_entry]
}

fun emptySpacer(): LayoutElementBuilders.LayoutElement {
    return LayoutElementBuilders.Spacer.Builder()
        .setWidth(DimensionBuilders.dp(0f))
        .setHeight(DimensionBuilders.dp(0f))
        .build()
}

fun getNoMeetingsLayout(): LayoutElementBuilders.Layout {
    return LayoutElementBuilders.Layout.Builder().setRoot(emptySpacer()).build()
}

fun getMeetingLayout(meeting: Meeting): LayoutElementBuilders.Layout {
    return LayoutElementBuilders.Layout.Builder().setRoot(emptySpacer()).build()
}

data class Meeting(val name: String, val dateTimeMillis: Long)

object MeetingsRepo {
    fun getMeetings(): List<Meeting> {
        // TODO: Replace with actual meeting data
        val now = System.currentTimeMillis()
        return listOf(
            Meeting("Meeting 1", now + 1 * 60 * 60 * 1000), // 1 hour from now
            Meeting("Meeting 2", now + 3 * 60 * 60 * 1000), // 3 hours from now
        )
    }
}

class PeriodicUpdatesTimebound : TileService() {
    // [START android_wear_tile_periodic_timebound]
    override fun onTileRequest(
        requestParams: RequestBuilders.TileRequest
    ): ListenableFuture<Tile?> {
        val timeline = Timeline.Builder()

        // Add fallback "no meetings" entry
        // Use the version of TimelineEntry that's in androidx.wear.protolayout.
        timeline.addTimelineEntry(
            TimelineBuilders.TimelineEntry.Builder().setLayout(getNoMeetingsLayout()).build()
        )

        // Retrieve a list of scheduled meetings
        val meetings = MeetingsRepo.getMeetings()
        // Add a timeline entry for each meeting
        meetings.forEach { meeting ->
            timeline.addTimelineEntry(
                TimelineBuilders.TimelineEntry.Builder()
                    .setLayout(getMeetingLayout(meeting))
                    .setValidity(
                        // The tile should disappear when the meeting begins
                        // Use the version of TimeInterval that's in
                        // androidx.wear.protolayout.
                        TimelineBuilders.TimeInterval.Builder()
                            .setEndMillis(meeting.dateTimeMillis)
                            .build()
                    )
                    .build()
            )
        }

        val tile =
            Tile.Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setTileTimeline(timeline.build())
                .build()
        return Futures.immediateFuture(tile)
    }
    // [END android_wear_tile_periodic_timebound]
}

fun getWeatherLayout() = emptySpacer()

class PeriodicUpdatesRefresh : TileService() {
    // [START android_wear_tile_periodic_refresh]
    override fun onTileRequest(
        requestParams: RequestBuilders.TileRequest
    ): ListenableFuture<Tile?> =
        Futures.immediateFuture(
            Tile.Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setFreshnessIntervalMillis(60 * 60 * 1000) // 60 minutes
                .setTileTimeline(Timeline.fromLayoutElement(getWeatherLayout()))
                .build()
        )
    // [END android_wear_tile_periodic_refresh]
}

class DynamicHeartRate : TileService() {
    @RequiresPermission(Manifest.permission.BODY_SENSORS)
    // [START android_wear_tile_dynamic_heart_rate]
    override fun onTileRequest(requestParams: RequestBuilders.TileRequest) =
        Futures.immediateFuture(
            Tile.Builder()
                .setResourcesVersion(RESOURCES_VERSION)
                .setFreshnessIntervalMillis(60 * 60 * 1000) // 60 minutes
                .setTileTimeline(
                    Timeline.fromLayoutElement(
                        Text.Builder(
                            this,
                            TypeBuilders.StringProp.Builder("--")
                                .setDynamicValue(
                                    PlatformHealthSources.heartRateBpm()
                                        .format()
                                        .concat(DynamicBuilders.DynamicString.constant(" bpm"))
                                )
                                .build(),
                            TypeBuilders.StringLayoutConstraint.Builder("000").build(),
                        )
                            .build()
                    )
                )
                .build()
        )
    // [END android_wear_tile_dynamic_heart_rate]
}
