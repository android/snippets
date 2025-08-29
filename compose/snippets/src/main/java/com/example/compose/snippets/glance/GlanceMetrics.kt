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

package com.example.compose.snippets.glance

import android.appwidget.AppWidgetManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

private const val TAG = "WidgetMetrics"

class GlanceMetrics {

    // [START android_compose_glance_metrics]
    @RequiresApi(Build.VERSION_CODES_FULL.BAKLAVA_1)
    fun getWidgetEngagementMetrics(context: Context) {
        val manager = AppWidgetManager.getInstance(context)

        val endTime = System.currentTimeMillis()
        val startTime = endTime - (24 * 60 * 60 * 1000) // a day ago

        val events = manager.queryAppWidgetEvents(startTime, endTime)

        if (events.isEmpty()) {
            Log.d(TAG, "No events found for the given time range.")
        }

        val metrics = hashMapOf(
            "clicks" to 0L,
            "scrolls" to 0L,
            "totalImpressionLength" to 0L
        )

        for (event in events) {

            Log.d(TAG, "Event Start: ${event.start}")
            Log.d(TAG, "Event End: ${event.end}")

            val widgetId = event.appWidgetId

            // Tap actions
            val clickedIds = event.clickedIds
            if (clickedIds?.isNotEmpty() == true) {
                metrics["clicks"] = metrics.getValue("clicks") + clickedIds.size
                // Log or analyze which components were clicked.
                for (id in clickedIds) {
                    Log.d(TAG, "Widget $widgetId: Tap event on component with ID $id")
                }
            }

            // Scroll events
            val scrolledIds = event.scrolledIds
            if (scrolledIds?.isNotEmpty() == true) {
                metrics["scrolls"] = metrics.getValue("scrolls") + scrolledIds.size
                // Log or analyze which lists were scrolled.
                for (id in scrolledIds) {
                    Log.d(TAG, "Widget $widgetId: Scroll event in list with ID/tag $id")
                }
            }

            // Impressions
            metrics["totalImpressionLength"] =
                metrics.getValue("totalImpressionLength") + event.visibleDuration.toMillis()
            Log.d(
                TAG,
                "Widget $widgetId: Impression event with duration " +
                    event.visibleDuration.toMillis() +
                    "ms"
            )

            // Position
            val position = event.position
            if (position != null) {
                Log.d(
                    TAG,
                    "Widget $widgetId: left=${position.left}, right=${position.right}, top=${position.top}, bottom=${position.bottom}"
                )
            }
        }
        Log.d("WidgetMetrics", "Metrics: $metrics")
    }
    // [END android_compose_glance_metrics]
}
