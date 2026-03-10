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

package com.example.wear.snippets.notifications

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.wear.phone.interactions.notifications.BridgingConfig
import androidx.wear.phone.interactions.notifications.BridgingManager

private const val channelId = "1"

fun setBridgeTags(context: Context) {
    // [START android_wearcompanion_notification_bridge_tags]
    val notification = NotificationCompat.Builder(context, channelId)
        // ... set other fields ...
        .extend(
            NotificationCompat.WearableExtender()
                .setBridgeTag("tagOne")
        )
        .build()
    // [END android_wearcompanion_notification_bridge_tags]
}

fun disableBridging(context: Context) {
    // [START android_wearcompanion_notification_disable_bridging]
    // In this example, bridging is only enabled for tagOne, tagTwo and tagThree.
    BridgingManager.fromContext(context).setConfig(
        BridgingConfig.Builder(context, isBridgingEnabled = false)
            .addExcludedTags(listOf("tagOne", "tagTwo", "tagThree"))
            .build()
    )
    // [END android_wearcompanion_notification_disable_bridging]
}

fun setDismissalId(context: Context) {
    // [START android_wearcompanion_notification_dismissal_id]
    val notification = NotificationCompat.Builder(context, channelId)
        // ... set other fields ...
        .extend(
            NotificationCompat.WearableExtender()
                .setDismissalId("abc123")
        )
        .build()
    // [END android_wearcompanion_notification_dismissal_id]
}