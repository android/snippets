/*
 * Copyright 2024 The Android Open Source Project
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
import android.appwidget.AppWidgetProviderInfo
import android.content.ComponentName
import android.content.Context
import android.widget.RemoteViews
import androidx.glance.GlanceId
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.compose
import com.example.android.views.R

class ExampleAppWidget : GlanceAppWidget() {
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        TODO("Not yet implemented")
    }
}

private object GeneratedPreviewWithoutGlance {

    lateinit var appContext: Context

    fun MyWidgetPreview() {
        // [START android_view_appwidget_generatedpreview_with_remoteview]
        AppWidgetManager.getInstance(appContext).setWidgetPreview(
            ComponentName(
                appContext,
                ExampleAppWidgetReceiver::class.java
            ),
            AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN,
            RemoteViews("com.example", R.layout.widget_preview)
        )
        // [END android_view_appwidget_generatedpreview_with_remoteview]
    }

    suspend fun MyGlanceWidgetPreview() {
        // [START android_view_appwidget_generatedpreview_with_glance]
        AppWidgetManager.getInstance(appContext).setWidgetPreview(
            ComponentName(
                appContext,
                ExampleAppWidgetReceiver::class.java
            ),
            AppWidgetProviderInfo.WIDGET_CATEGORY_HOME_SCREEN,
            ExampleAppWidget().compose(
                context = appContext
            ),
        )

        // [END android_view_appwidget_generatedpreview_with_glance]
    }
}

class ExampleAppWidgetReceiver : AppWidgetProvider()
