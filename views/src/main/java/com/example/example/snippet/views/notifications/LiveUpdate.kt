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

package com.example.example.snippet.views.notifications

import android.app.Notification
import android.app.Notification.SEMANTIC_STYLE_CAUTION
import android.app.Notification.SEMANTIC_STYLE_DANGER
import android.app.Notification.SEMANTIC_STYLE_INFO
import android.app.Notification.SEMANTIC_STYLE_SAFE
import android.app.Notification.SEMANTIC_STYLE_UNSPECIFIED
import android.content.Context
import android.os.Build
import android.text.SpannableStringBuilder
import androidx.annotation.RequiresApi
import com.example.example.snippet.views.R

@RequiresApi(Build.VERSION_CODES.CINNAMON_BUN)
fun createLiveUpdateNotification(context: Context, channelId: String) {
    // [START android_views_notifications_live_update_semantic_coloring]
    val ssb = SpannableStringBuilder()
        .append("Colors: ")
        .append("NONE", Notification.createSemanticStyleAnnotation(SEMANTIC_STYLE_UNSPECIFIED), 0)
        .append(", ")
        .append("INFO", Notification.createSemanticStyleAnnotation(SEMANTIC_STYLE_INFO), 0)
        .append(", ")
        .append("SAFE", Notification.createSemanticStyleAnnotation(SEMANTIC_STYLE_SAFE), 0)
        .append(", ")
        .append("CAUTION", Notification.createSemanticStyleAnnotation(SEMANTIC_STYLE_CAUTION), 0)
        .append(", ")
        .append("DANGER", Notification.createSemanticStyleAnnotation(SEMANTIC_STYLE_DANGER), 0)

    Notification.Builder(context, channelId)
        .setSmallIcon(R.drawable.ic_icon)
        .setContentTitle("Hello World!")
        .setContentText(ssb)
        .setOngoing(true)
        .setRequestPromotedOngoing(true)
    // [END android_views_notifications_live_update_semantic_coloring]
}
