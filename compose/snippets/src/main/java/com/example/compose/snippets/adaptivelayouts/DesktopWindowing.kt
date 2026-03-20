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

import android.content.ClipData
import android.view.View
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

// [START android_compose_desktop_window_insets_title]
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CaptionBar() {
    if (WindowInsets.isCaptionBarVisible) {
        Row(
            modifier = Modifier
                .windowInsetsTopHeight(WindowInsets.captionBar)
                .fillMaxWidth()
                .background(
                    if (isSystemInDarkTheme())
                        Color.White
                    else Color.Black

                ),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Caption Bar Title",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(4.dp)
            )
        }
    }
}
// [END android_compose_desktop_window_insets_title]

// [START android_compose_desktop_drag_drop_source]
fun Modifier.dragAndDropSourceModifier(): Modifier {
    return this.dragAndDropSource { _ ->
        DragAndDropTransferData(
            clipData = ClipData.newPlainText("COPIED_DATA_KEY", "Sample Copied Data"),
            flags = View.DRAG_FLAG_GLOBAL_SAME_APPLICATION or View.DRAG_FLAG_START_INTENT_SENDER_ON_UNHANDLED_DRAG,
        )
    }
}
// [END android_compose_desktop_drag_drop_source]

// [START android_compose_desktop_drag_drop_target]
fun Modifier.dragAndDropTargetModifier(): Modifier {
    return this.dragAndDropTarget(
        shouldStartDragAndDrop = { event ->
            event.toAndroidDragEvent().clipDescription.hasMimeType("text/plain")
        },
        target = object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                val clipData = event.toAndroidDragEvent().clipData
                return clipData != null
            }
        }
    )
}
// [END android_compose_desktop_drag_drop_target]