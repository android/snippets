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

package com.example.wear.snippets.complication

import androidx.wear.watchface.complications.data.ComplicationData
import androidx.wear.watchface.complications.data.ComplicationType
import androidx.wear.watchface.complications.data.NoDataComplicationData
import androidx.wear.watchface.complications.data.PlainComplicationText
import androidx.wear.watchface.complications.data.ShortTextComplicationData
import androidx.wear.watchface.complications.datasource.ComplicationDataTimeline
import androidx.wear.watchface.complications.datasource.ComplicationRequest
import androidx.wear.watchface.complications.datasource.SuspendingTimelineComplicationDataSourceService
import androidx.wear.watchface.complications.datasource.TimeInterval
import androidx.wear.watchface.complications.datasource.TimelineEntry
import java.time.Instant
import java.time.temporal.ChronoUnit

data class CalendarEntry(
    val start: Instant,
    val end: Instant,
    val name: String
)

// [START android_wear_timeline_complication]
class MyTimelineComplicationDataSourceService : SuspendingTimelineComplicationDataSourceService() {
    override suspend fun onComplicationRequest(request: ComplicationRequest): ComplicationDataTimeline? {
        if (request.complicationType != ComplicationType.SHORT_TEXT) {
            return ComplicationDataTimeline(
                defaultComplicationData = NoDataComplicationData(),
                timelineEntries = emptyList()
            )
        }
        // Retrieve list of events from your own datasource / database.
        val events = getCalendarEvents()
        return ComplicationDataTimeline(
            defaultComplicationData = shortTextComplicationData("No event"),
            timelineEntries = events.map {
                TimelineEntry(
                    validity = TimeInterval(it.start, it.end),
                    complicationData = shortTextComplicationData(it.name)
                )
            }
        )
    }

    override fun getPreviewData(type: ComplicationType): ComplicationData? {
        return shortTextComplicationData("Event 1")
    }

    private fun shortTextComplicationData(text: String) =
        ShortTextComplicationData.Builder(
            text = PlainComplicationText.Builder(text).build(),
            contentDescription = PlainComplicationText.Builder(text).build()
        )
            // Add further optional details here such as icon, tap action, title etc
            .build()

    // [START_EXCLUDE]
    private fun getCalendarEvents(): List<CalendarEntry> {
        val now = Instant.now()
        return listOf(
            CalendarEntry(now, now.plus(1, ChronoUnit.HOURS), "Event 1"),
            CalendarEntry(now.plus(2, ChronoUnit.HOURS), now.plus(3, ChronoUnit.HOURS), "Event 2"),
            CalendarEntry(now.plus(4, ChronoUnit.HOURS), now.plus(5, ChronoUnit.HOURS), "Event 3"),
        )
    }
    // [END_EXCLUDE]
}
// [END android_wear_timeline_complication]
