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

package com.example.example.snippet.views.appwidget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.os.Bundle
import android.util.SizeF
import android.widget.RemoteViews
import com.example.example.snippet.views.R

private object LayoutsResponsive {
    class ResponsiveAppWidgetProvider : AppWidgetProvider() {
        private lateinit var appWidgetManager: AppWidgetManager
        private val id: Int = 0

        // [START android_views_appwidgets_layouts_responsive]
        override fun onUpdate(
            // [START_EXCLUDE silent]
            context: Context,
            appWidgetManager: AppWidgetManager,
            appWidgetIds: IntArray
            // [END_EXCLUDE]
            /*
            ...
             */
        ) {
            val smallView = RemoteViews(
                // [START_EXCLUDE silent]
                context.packageName,
                R.layout.widget_weather_forecast_small
                // [END_EXCLUDE]
                /*
                ...
                 */
            )
            val tallView = RemoteViews(
                // [START_EXCLUDE silent]
                context.packageName,
                R.layout.widget_weather_forecast_medium
                // [END_EXCLUDE]
                /*
                ...
                 */
            )
            val wideView = RemoteViews(
                // [START_EXCLUDE silent]
                context.packageName,
                R.layout.widget_weather_forecast_large
                // [END_EXCLUDE]
                /*
                ...
                 */
            )

            val viewMapping: Map<SizeF, RemoteViews> = mapOf(
                SizeF(150f, 100f) to smallView,
                SizeF(150f, 200f) to tallView,
                SizeF(215f, 100f) to wideView
            )
            val remoteViews = RemoteViews(viewMapping)

            appWidgetManager.updateAppWidget(id, remoteViews)
        }
        // [END android_views_appwidgets_layouts_responsive]
    }
}

private object LayoutsExact {
    class ExactAppWidgetProvider : AppWidgetProvider() {
        // [START android_views_appwidgets_layouts_exact]
        override fun onAppWidgetOptionsChanged(
            context: Context,
            appWidgetManager: AppWidgetManager,
            id: Int,
            newOptions: Bundle?
        ) {
            super.onAppWidgetOptionsChanged(context, appWidgetManager, id, newOptions)
            // Get the new sizes.
            val sizes = newOptions?.getParcelableArrayList<SizeF>(
                AppWidgetManager.OPTION_APPWIDGET_SIZES
            )
            // Check that the list of sizes is provided by the launcher.
            if (sizes.isNullOrEmpty()) {
                return
            }
            // Map the sizes to the RemoteViews that you want.
            val remoteViews = RemoteViews(sizes.associateWith(::createRemoteViews))
            appWidgetManager.updateAppWidget(id, remoteViews)
        }

        // Create the RemoteViews for the given size.
        private fun createRemoteViews(size: SizeF): RemoteViews {
            // [START_EXCLUDE silent]
            return RemoteViews("", 0)
            // [END_EXCLUDE]
        }
        // [END android_views_appwidgets_layouts_exact]
    }
}

private fun weatherResponsiveSnippet(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    // [START android_views_appwidgets_layouts_weather_responsive]
    val smallView = RemoteViews(context.packageName, R.layout.widget_weather_forecast_small)
    val mediumView = RemoteViews(context.packageName, R.layout.widget_weather_forecast_medium)
    val largeView = RemoteViews(context.packageName, R.layout.widget_weather_forecast_large)

    val viewMapping: Map<SizeF, RemoteViews> = mapOf(
        SizeF(180f, 110f) to smallView,
        SizeF(270f, 110f) to mediumView,
        SizeF(270f, 280f) to largeView
    )

    appWidgetManager.updateAppWidget(appWidgetId, RemoteViews(viewMapping))
    // [END android_views_appwidgets_layouts_weather_responsive]
}
