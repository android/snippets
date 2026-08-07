package com.example.aiglasses.camera

import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import androidx.activity.compose.setContent
import androidx.xr.glimmer.GlimmerTheme

/**
 * Demonstrates how to inspect generic motion events (touchpad gestures)
 * delivered to a Projected Activity running on AI Glasses.
 */
class HelloMotionGestureActivity : BaseProjectedActivity("Motion Gestures") {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            GlimmerTheme {
                ProjectedSampleScreen(
                    activity = this@HelloMotionGestureActivity,
                    onGestureAction = { source ->
                        triggerFeedback("Motion gesture detected.", source, "motion_gesture", throttleMs = 1000L)
                    }
                )
            }
        }
    }

    // [START androidxr_projected_motion_gesture_dispatch]
    override fun dispatchGenericMotionEvent(ev: MotionEvent): Boolean {
        val actionName = when (ev.actionMasked) {
            // Touch down: first contact with the glasses touchpad.
            MotionEvent.ACTION_DOWN -> "ACTION_DOWN"
            // Touch move: contact moving across the glasses touchpad.
            MotionEvent.ACTION_MOVE -> "ACTION_MOVE"
            // Touch up: contact lifted from the glasses touchpad.
            MotionEvent.ACTION_UP -> "ACTION_UP"
            else -> "ACTION_${ev.actionMasked}"
        }

        Log.d(
            "ProjectedInput",
            "MotionEvent: action=$actionName " +
                "source=${ev.source} deviceId=${ev.deviceId} " +
                "x=${ev.x} y=${ev.y} rawX=${ev.rawX} rawY=${ev.rawY}",
        )

        if (
            ev.actionMasked == MotionEvent.ACTION_DOWN ||
            ev.actionMasked == MotionEvent.ACTION_MOVE ||
            ev.actionMasked == MotionEvent.ACTION_UP
        ) {
            triggerFeedback(
                "Motion event detected.",
                "Generic Motion ($actionName: x=${ev.x.toInt()}, y=${ev.y.toInt()})",
                "motion_gesture",
                throttleMs = if (ev.actionMasked == MotionEvent.ACTION_MOVE) 500L else 0L,
                speakAudio = (ev.actionMasked == MotionEvent.ACTION_DOWN)
            )
        }

        // Keep normal dispatch unless this activity intentionally consumes the event.
        return super.dispatchGenericMotionEvent(ev)
    }
    // [END androidxr_projected_motion_gesture_dispatch]
}



