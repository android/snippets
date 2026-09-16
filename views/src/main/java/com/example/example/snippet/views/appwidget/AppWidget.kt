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
import android.widget.RemoteViews
import com.example.example.snippet.views.R

private fun statefulBehaviorSnippet(
    remoteView: RemoteViews,
    onCheckedChangePendingIntent: PendingIntent
) {
    // [START android_views_appwidgets_stateful_behavior]
    // Check the view.
    remoteView.setCompoundButtonChecked(R.id.my_checkbox, true)

    // Check a radio group.
    remoteView.setRadioGroupChecked(R.id.my_radio_group, R.id.radio_button_2)

    // Listen for check changes. The intent has an extra with the key
    // EXTRA_CHECKED that specifies the current checked state of the view.
    remoteView.setOnCheckedChangeResponse(
        R.id.my_checkbox,
        RemoteViews.RemoteResponse.fromPendingIntent(onCheckedChangePendingIntent)
    )
    // [END android_views_appwidgets_stateful_behavior]
}
