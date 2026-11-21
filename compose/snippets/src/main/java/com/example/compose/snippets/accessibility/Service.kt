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

package com.example.compose.snippets.accessibility

import android.accessibilityservice.AccessibilityButtonController
import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.accessibilityservice.FingerprintGestureController
import android.accessibilityservice.GestureDescription
import android.content.Context
import android.graphics.Path
import android.media.AudioManager
import android.os.Build
import android.text.Spannable
import android.text.SpannableStringBuilder
import android.text.style.LocaleSpan
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo
import java.util.Locale

private object CreateAccessibilityServiceSnippet {
    // [START android_compose_accessibility_service_create]
    // package com.example.android.apis.accessibility

    // import android.accessibilityservice.AccessibilityService
    // import android.accessibilityservice.AccessibilityServiceInfo
    // import android.accessibilityservice.FingerprintGestureController
    // import android.accessibilityservice.AccessibilityButtonController
    // import android.accessibilityservice.GestureDescription
    // import android.view.accessibility.AccessibilityEvent
    // import android.view.accessibility.AccessibilityNodeInfo
    // import android.graphics.Path
    // import android.os.Build
    // import android.media.AudioManager
    // import android.content.Context

    class MyAccessibilityService : AccessibilityService() {

        override fun onAccessibilityEvent(event: AccessibilityEvent) {
            // Interpret the event and provide feedback to the user.
        }

        override fun onInterrupt() {
            // Interrupt any ongoing feedback.
        }

        override fun onServiceConnected() {
            // Perform initialization here.
        }
    }
    // [END android_compose_accessibility_service_create]
}

private abstract class RuntimeConfigAccessibilityService : AccessibilityService() {
    // [START android_compose_accessibility_service_runtime_config]
    override fun onServiceConnected() {
        val info = AccessibilityServiceInfo()

        // Set the type of events that this service wants to listen to.
        info.eventTypes = AccessibilityEvent.TYPE_VIEW_CLICKED or AccessibilityEvent.TYPE_VIEW_FOCUSED

        // Set the type of feedback your service provides.
        info.feedbackType = AccessibilityServiceInfo.FEEDBACK_SPOKEN

        // Set flags at runtime.
        info.flags = AccessibilityServiceInfo.DEFAULT or
            AccessibilityServiceInfo.FLAG_REQUEST_FINGERPRINT_GESTURES

        this.setServiceInfo(info)
    }
    // [END android_compose_accessibility_service_runtime_config]
}

private abstract class InterpretUiAccessibilityService : AccessibilityService() {
    // [START android_compose_accessibility_service_interpret_ui]
    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // Get the source node of the event.
        val sourceNode: AccessibilityNodeInfo? = event.source

        if (sourceNode == null) return

        // Inspect properties.
        if (sourceNode.isCheckable) {
            val state = if (sourceNode.isChecked) "Checked" else "Unchecked"
            val label = sourceNode.text ?: sourceNode.contentDescription

            // Provide feedback (for example, speak to the user).
            speakToUser("$label is $state")
        }

        // Always recycle nodes to prevent memory leaks.
        sourceNode.recycle()
    }

    private fun speakToUser(text: String) {
        // Your text-to-speech implementation goes here.
    }
    // [END android_compose_accessibility_service_interpret_ui]
}

private object PerformClickSnippet {
    // [START android_compose_accessibility_service_perform_click]
    fun performClick(node: AccessibilityNodeInfo) {
        if (node.isClickable) {
            node.performAction(AccessibilityNodeInfo.ACTION_CLICK)
        }
    }
    // [END android_compose_accessibility_service_perform_click]
}

private abstract class NavigateBackAccessibilityService : AccessibilityService() {
    // [START android_compose_accessibility_service_navigate_back]
    // Navigate back.
    fun navigateBack() {
        performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
    }
    // [END android_compose_accessibility_service_navigate_back]
}

private abstract class FindFocusAccessibilityService : AccessibilityService() {
    fun findFocusedNode() {
        // [START android_compose_accessibility_service_find_focus]
        // Find the node that currently has accessibility focus.
        // Note: rootInActiveWindow can be null if the window is not available.
        val root = rootInActiveWindow
        if (root != null) {
            val focusedNode = root.findFocus(AccessibilityNodeInfo.FOCUS_ACCESSIBILITY)

            // Do something with focusedNode.

            // Always recycle nodes.
            focusedNode?.recycle()
            // rootInActiveWindow doesn't need to be recycled, but obtained nodes do.
        }
        // [END android_compose_accessibility_service_find_focus]
    }
}

private object FocusNodeSnippet {
    // [START android_compose_accessibility_service_focus_node]
    // Request that the system give focus to a given node.
    fun focusNode(node: AccessibilityNodeInfo) {
        node.performAction(AccessibilityNodeInfo.ACTION_ACCESSIBILITY_FOCUS)
    }
    // [END android_compose_accessibility_service_focus_node]
}

private abstract class SwipeRightAccessibilityService : AccessibilityService() {
    // [START android_compose_accessibility_service_swipe_right]
    fun swipeRight() {
        // Create a path for the swipe (from x=100 to x=500).
        val swipePath = Path()
        swipePath.moveTo(100f, 500f)
        swipePath.lineTo(500f, 500f)

        // Build the stroke description (0ms delay, 500ms duration).
        val stroke = GestureDescription.StrokeDescription(swipePath, 0, 500)

        // Build the gesture description.
        val gestureBuilder = GestureDescription.Builder()
        gestureBuilder.addStroke(stroke)

        // Dispatch the gesture.
        dispatchGesture(
            gestureBuilder.build(),
            object : AccessibilityService.GestureResultCallback() {
                override fun onCompleted(gestureDescription: GestureDescription?) {
                    super.onCompleted(gestureDescription)
                    // Gesture finished successfully.
                }
            },
            null
        )
    }
    // [END android_compose_accessibility_service_swipe_right]
}

private abstract class ContinuedGestureAccessibilityService : AccessibilityService() {
    // [START android_compose_accessibility_service_continued_gesture]
    fun performLShapedGesture() {
        val path1 = Path().apply {
            moveTo(200f, 200f)
            lineTo(400f, 200f)
        }

        val path2 = Path().apply {
            moveTo(400f, 200f)
            lineTo(400f, 400f)
        }

        // First stroke: willContinue = true.
        val stroke1 = GestureDescription.StrokeDescription(path1, 0, 500, true)

        // Second stroke: continues immediately after stroke1.
        val stroke2 = stroke1.continueStroke(path2, 0, 500, false)

        val builder = GestureDescription.Builder()
        builder.addStroke(stroke1)
        builder.addStroke(stroke2)

        dispatchGesture(builder.build(), null, null)
    }
    // [END android_compose_accessibility_service_continued_gesture]
}

private abstract class IncreaseVolumeAccessibilityService : AccessibilityService() {
    // [START android_compose_accessibility_service_increase_volume]
    fun increaseAccessibilityVolume() {
        val audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.adjustStreamVolume(
            AudioManager.STREAM_ACCESSIBILITY,
            AudioManager.ADJUST_RAISE,
            0
        )
    }
    // [END android_compose_accessibility_service_increase_volume]
}

private abstract class FingerprintGestureAccessibilityService : AccessibilityService() {
    // [START android_compose_accessibility_service_fingerprint_gesture]
    // Import: android.os.Build.
    // Import: android.accessibilityservice.FingerprintGestureController.

    private var gestureController: FingerprintGestureController? = null

    override fun onServiceConnected() {
        // Check if the device is running Android 10 (Q) or higher.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            gestureController = fingerprintGestureController

            val callback = object : FingerprintGestureController.FingerprintGestureCallback() {
                override fun onGestureDetected(gesture: Int) {
                    when (gesture) {
                        FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_DOWN -> {
                            // Handle swipe down.
                        }
                        FingerprintGestureController.FINGERPRINT_GESTURE_SWIPE_UP -> {
                            // Handle swipe up.
                        }
                    }
                }
            }

            gestureController?.registerFingerprintGestureCallback(callback, null)
        }
    }
    // [END android_compose_accessibility_service_fingerprint_gesture]
}

private abstract class AccessibilityButtonAccessibilityService : AccessibilityService() {
    // [START android_compose_accessibility_service_accessibility_button]
    // Import: android.accessibilityservice.AccessibilityButtonController.

    override fun onServiceConnected() {
        // ... existing initialization code ...

        val controller = accessibilityButtonController

        controller.registerAccessibilityButtonCallback(
            object : AccessibilityButtonController.AccessibilityButtonCallback() {
                override fun onClicked(controller: AccessibilityButtonController) {
                    // Respond to button tap.
                }
            }
        )
    }
    // [END android_compose_accessibility_service_accessibility_button]
}

private fun multilingualTts() {
    // [START android_compose_accessibility_service_multilingual_tts]
    // import android.text.Spannable
    // import android.text.SpannableStringBuilder
    // import android.text.style.LocaleSpan
    // import java.util.Locale

    // Wrap text in LocaleSpan to indicate language.
    val spannable = SpannableStringBuilder("Bonjour")
    spannable.setSpan(
        LocaleSpan(Locale.FRANCE),
        0,
        spannable.length,
        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
    )
    // [END android_compose_accessibility_service_multilingual_tts]
}
