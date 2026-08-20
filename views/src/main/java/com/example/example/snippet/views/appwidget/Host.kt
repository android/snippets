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

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProviderInfo
import android.content.Intent
import android.os.Bundle

private class HostActivity : Activity() {
    fun displayBindingDialog(
        appWidgetId: Int,
        info: AppWidgetProviderInfo,
        options: Bundle,
        REQUEST_BIND_APPWIDGET: Int
    ) {
        // [START android_views_appwidgets_host_bind_dialog]
        val intent = Intent(AppWidgetManager.ACTION_APPWIDGET_BIND).apply {
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_PROVIDER, info.provider)
            // This is the options bundle described in the preceding section.
            putExtra(AppWidgetManager.EXTRA_APPWIDGET_OPTIONS, options)
        }
        startActivityForResult(intent, REQUEST_BIND_APPWIDGET)
        // [END android_views_appwidgets_host_bind_dialog]
    }
}
