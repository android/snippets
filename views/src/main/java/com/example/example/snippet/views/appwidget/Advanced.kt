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
import android.content.Context
import android.widget.RemoteViews
import com.example.example.snippet.views.R

private fun fullUpdateSnippet(
    context: Context,
    appWidgetId: Int
) {
    // [START android_views_appwidgets_advanced_full_update]
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val remoteViews = RemoteViews(context.packageName, R.layout.widgetlayout).apply {
        setTextViewText(R.id.textview_widget_layout1, "Updated text1")
        setTextViewText(R.id.textview_widget_layout2, "Updated text2")
    }
    appWidgetManager.updateAppWidget(appWidgetId, remoteViews)
    // [END android_views_appwidgets_advanced_full_update]
}

private fun partialUpdateSnippet(
    context: Context,
    appWidgetId: Int
) {
    // [START android_views_appwidgets_advanced_partial_update]
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val remoteViews = RemoteViews(context.packageName, R.layout.widgetlayout).apply {
        setTextViewText(R.id.textview_widget_layout, "Updated text")
    }
    appWidgetManager.partiallyUpdateAppWidget(appWidgetId, remoteViews)
    // [END android_views_appwidgets_advanced_partial_update]
}

private fun collectionDataRefreshSnippet(
    context: Context,
    appWidgetId: Int
) {
    // [START android_views_appwidgets_advanced_notify_data_changed]
    val appWidgetManager = AppWidgetManager.getInstance(context)
    appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.widget_listview)
    // [END android_views_appwidgets_advanced_notify_data_changed]
}
