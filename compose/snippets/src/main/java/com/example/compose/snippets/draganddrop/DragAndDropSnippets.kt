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

package com.example.compose.snippets.draganddrop

import android.content.ClipData
import android.content.ClipDescription
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.draganddrop.dragAndDropSource
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draganddrop.DragAndDropEvent
import androidx.compose.ui.draganddrop.DragAndDropTarget
import androidx.compose.ui.draganddrop.DragAndDropTransferData
import androidx.compose.ui.draganddrop.mimeTypes

@RequiresApi(Build.VERSION_CODES.N)
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun DragAndDropSnippet() {

    val url = ""

    // [START android_compose_drag_and_drop_1]
    Modifier.dragAndDropSource {
        detectTapGestures(onLongPress = {
            // Transfer data here.
        })
    }
    // [END android_compose_drag_and_drop_1]

    // [START android_compose_drag_and_drop_2]
    Modifier.dragAndDropSource {
        detectTapGestures(onLongPress = {
            startTransfer(
                DragAndDropTransferData(
                    ClipData.newPlainText(
                        "image Url", url
                    )
                )
            )
        })
    }
    // [END android_compose_drag_and_drop_2]

    // [START android_compose_drag_and_drop_3]
    Modifier.dragAndDropSource {
        detectTapGestures(onLongPress = {
            startTransfer(
                DragAndDropTransferData(
                    ClipData.newPlainText(
                        "image Url", url
                    ),
                    flags = View.DRAG_FLAG_GLOBAL
                )
            )
        })
    }
    // [END android_compose_drag_and_drop_3]

    // [START android_compose_drag_and_drop_4]
    val callback = remember {
        object : DragAndDropTarget {
            override fun onDrop(event: DragAndDropEvent): Boolean {
                // Parse received data
                return true
            }
        }
    }
    // [END android_compose_drag_and_drop_4]

    // [START android_compose_drag_and_drop_5]
    Modifier.dragAndDropTarget(
        shouldStartDragAndDrop = { event ->
            event.mimeTypes().contains(ClipDescription.MIMETYPE_TEXT_PLAIN)
        }, target = callback
    )
    // [END android_compose_drag_and_drop_5]

    // [START android_compose_drag_and_drop_6]
    object : DragAndDropTarget {
        override fun onStarted(event: DragAndDropEvent) {
            // When the drag event starts
        }

        override fun onEntered(event: DragAndDropEvent) {
            // When the dragged object enters the target surface
        }

        override fun onEnded(event: DragAndDropEvent) {
            // When the drag event stops
        }

        override fun onExited(event: DragAndDropEvent) {
            // When the dragged object exits the target surface
        }

        override fun onDrop(event: DragAndDropEvent): Boolean = true
    }
    // [END android_compose_drag_and_drop_6]
}
