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

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent

private class ExampleAppWidgetProvider : AppWidgetProvider()

private fun discoverabilitySnippet(context: Context) {
    // [START android_views_appwidgets_discoverability_request_pin]
    val appWidgetManager = AppWidgetManager.getInstance(context)
    val myProvider = ComponentName(context, ExampleAppWidgetProvider::class.java)

    if (appWidgetManager.isRequestPinAppWidgetSupported) {
        // Create the PendingIntent object only if your app needs to be notified
        // when the user chooses to pin the widget. Note that if the pinning
        // operation fails, your app isn't notified. This callback receives the ID
        // of the newly pinned widget (EXTRA_APPWIDGET_ID).
        val successCallback = PendingIntent.getBroadcast(
            /* context = */ context,
            /* requestCode = */ 0,
            /* intent = */ Intent(
                // [START_EXCLUDE]
                context, ExampleAppWidgetProvider::class.java
                // [END_EXCLUDE]
            ),
            /* flags = */ PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        appWidgetManager.requestPinAppWidget(myProvider, null, successCallback)
    }
    // [END android_views_appwidgets_discoverability_request_pin]
}
