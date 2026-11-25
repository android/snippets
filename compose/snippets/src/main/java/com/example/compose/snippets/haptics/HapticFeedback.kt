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

package com.example.compose.snippets.haptics

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.HapticFeedbackConstants
import android.view.MotionEvent
import android.view.View
import android.widget.Button

private object HapticTouchListenerSnippet {
    // [START android_haptics_feedback_touch_listener]
    class HapticTouchListener : View.OnTouchListener {
        override fun onTouch(view: View, event: MotionEvent): Boolean {
            when (event.actionMasked) {
                MotionEvent.ACTION_DOWN ->
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY)
                MotionEvent.ACTION_UP ->
                    view.performHapticFeedback(HapticFeedbackConstants.VIRTUAL_KEY_RELEASE)
            }
            return true
        }
    }
    // [END android_haptics_feedback_touch_listener]
}

private fun submitButtonFeedback(submitButton: Button) {
    // [START android_haptics_feedback_submit_button]
    submitButton.setOnClickListener { view ->
        val successful = performSubmit()
        if (successful) {
            view.performHapticFeedback(HapticFeedbackConstants.CONFIRM)
        } else {
            view.performHapticFeedback(HapticFeedbackConstants.REJECT)
        }
    }
    // [END android_haptics_feedback_submit_button]
}

private fun loadVibrator(context: Context) {
    // [START android_haptics_feedback_load_vibrator]
    // import android.os.Vibrator

    val vibrator = context.getSystemService(Vibrator::class.java)
    // [END android_haptics_feedback_load_vibrator]
}

private fun playPredefinedClickEffect(context: Context) {
    // [START android_haptics_feedback_predefined_click]
    val vibrator = context.getSystemService(Vibrator::class.java)
    // ...
    // Requires VIBRATE permission.
    vibrator.vibrate(VibrationEffect.createPredefined(VibrationEffect.EFFECT_CLICK))
    // [END android_haptics_feedback_predefined_click]
}

private fun performSubmit(): Boolean = true
