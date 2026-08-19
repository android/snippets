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

import android.content.res.ColorStateList
import android.util.TypedValue
import android.widget.RemoteViews
import com.example.example.snippet.views.R

private fun createProgressColorStateList(): ColorStateList = ColorStateList.valueOf(0)

private fun runtimeModRemoteViewsSnippet(remoteView: RemoteViews) {
    // [START android_views_appwidgets_enhance_runtime_mod_remoteviews]
    // Set the colors of a progress bar at runtime.
    remoteView.setColorStateList(
        R.id.progress, "setProgressTintList", createProgressColorStateList()
    )

    // Specify exact sizes for margins.
    remoteView.setViewLayoutMargin(
        R.id.text, RemoteViews.MARGIN_END, 8f, TypedValue.COMPLEX_UNIT_DIP
    )
    // [END android_views_appwidgets_enhance_runtime_mod_remoteviews]
}
