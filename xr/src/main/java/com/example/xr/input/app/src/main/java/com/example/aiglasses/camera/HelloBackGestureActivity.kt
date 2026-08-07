package com.example.aiglasses.camera

import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.xr.glimmer.GlimmerTheme

/**
 * Demonstrates how to intercept and handle system back gestures
 * from a Projected Activity running on AI Glasses.
 */
class HelloBackGestureActivity : BaseProjectedActivity("Back Gestures") {

    // [START androidxr_projected_back_gesture_callback]
    private val backCallback =
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitConfirmation()
            }
        }
    // [END androidxr_projected_back_gesture_callback]

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // [START androidxr_projected_back_gesture_register]
        onBackPressedDispatcher.addCallback(this, backCallback)
        // [END androidxr_projected_back_gesture_register]

        setContent {
            GlimmerTheme {
                ProjectedSampleScreen(
                    activity = this@HelloBackGestureActivity,
                    subtitle = "System Back Intercept Enabled",
                    consumeSwipeBackward = false,
                    onGestureAction = {}
                )
            }
        }
    }

    private fun showExitConfirmation() {
        Log.i("HelloBackGesture", "System Back gesture received")
        triggerFeedback(
            "Back gesture detected.",
            "System Back (handleOnBackPressed)",
            "back_gesture",
            throttleMs = 1000L
        )
    }

    private fun setExitConfirmationVisible(visible: Boolean) {
        backCallback.isEnabled = visible
    }
}



