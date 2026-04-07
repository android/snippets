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

package com.example.compose.snippets.adaptivelayouts

import android.app.Activity
import android.app.PendingIntent
import android.content.ClipData
import android.content.ClipDescription
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.compose.foundation.background
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.captionBar
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.isCaptionBarVisible
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsTopHeight
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.toAndroidDragEvent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat.requestDragAndDropPermissions

/**
 * A custom Title Bar that respects the system caption bar insets.
 */
// [START android_compose_desktop_window_insets_title]
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CaptionBar() {
    if (WindowInsets.isCaptionBarVisible) {
        Row(
            modifier = Modifier
                .windowInsetsTopHeight(WindowInsets.captionBar)
                .fillMaxWidth()
                .background(if (isSystemInDarkTheme()) Color.White else Color.Black),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Caption Bar Title",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}
// [END android_compose_desktop_window_insets_title]

/**
 * Transparent System Caption Bar
 */
class TransparentActionBarActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // [START android_compose_desktop_window_transparent_caption]
        window.insetsController?.setSystemBarsAppearance(
            WindowInsetsController.APPEARANCE_TRANSPARENT_CAPTION_BAR_BACKGROUND,
            WindowInsetsController.APPEARANCE_TRANSPARENT_CAPTION_BAR_BACKGROUND
        )
        // [END android_compose_desktop_window_transparent_caption]
    }
}


/**
 * Simple drag source for plain text data.
 */
fun dragAndDropSourceModifier() {
    // [START android_compose_desktop_drag_drop_source]
    Modifier.dragAndDropSource { _ ->
        DragAndDropTransferData(
            clipData = ClipData.newPlainText("label", "Your data"),
            flags = View.DRAG_FLAG_GLOBAL_SAME_APPLICATION
        )
    }
    // [END android_compose_desktop_drag_drop_source]
}

/**
 * Custom drag source that launches a new Activity instance when dropped.
 */
fun customDragAndDropSource(activity: Activity, itemId: String) {
    // [START android_compose_desktop_drag_drop_source_unhandled_flag]
    Modifier.dragAndDropSource { _ ->
        val intent = Intent.makeMainActivity(activity.componentName).apply {
            putExtra("EXTRA_ITEM_ID", itemId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK or
                    Intent.FLAG_ACTIVITY_LAUNCH_ADJACENT
        }

        val pendingIntent = PendingIntent.getActivity(
            activity, 0, intent, PendingIntent.FLAG_IMMUTABLE
        )

        val data = ClipData(
            "Item $itemId",
            arrayOf(ClipDescription.MIMETYPE_TEXT_INTENT),
            ClipData.Item.Builder().setIntentSender(pendingIntent.intentSender).build()
        )

        DragAndDropTransferData(
            clipData = data,
            flags = View.DRAG_FLAG_GLOBAL_SAME_APPLICATION or
                    View.DRAG_FLAG_START_INTENT_SENDER_ON_UNHANDLED_DRAG,
        )
    }
    // [END android_compose_desktop_drag_drop_source_unhandled_flag]
}

/**
 * A target modifier configured to receive plain text drag events.
 */
fun dragAndDropTargetModifier(activity: Activity) {
    // [START android_compose_desktop_drag_drop_target]
    Modifier.dragAndDropTarget(
        shouldStartDragAndDrop = { event ->
            event.toAndroidDragEvent().clipDescription.hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN)
        },
        target = object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                requestDragAndDropPermissions(activity, event.toAndroidDragEvent())
                val clipData = event.toAndroidDragEvent().clipData
                val item = clipData?.getItemAt(0)?.text
                if (item != null) {
                    // Process the dropped text item here
                }
                return item != null
            }
        }
    )
    // [END android_compose_desktop_drag_drop_target]
}
