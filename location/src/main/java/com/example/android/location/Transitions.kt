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

package com.example.android.location

import android.Manifest
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.ActivityRecognition
import com.google.android.gms.location.ActivityTransition
import com.google.android.gms.location.ActivityTransitionRequest
import com.google.android.gms.location.ActivityTransitionResult
import com.google.android.gms.location.DetectedActivity

private class TransitionsSnippet(
    private val context: Context,
    private val myPendingIntent: PendingIntent
) {

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    fun registerTransitions() {
        // [START android_location_transitions_create_list]
        val transitions = mutableListOf<ActivityTransition>()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.IN_VEHICLE)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()

        transitions +=
            ActivityTransition.Builder()
                .setActivityType(DetectedActivity.WALKING)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build()
        // [END android_location_transitions_create_list]

        // [START android_location_transitions_create_request]
        val request = ActivityTransitionRequest(transitions)
        // [END android_location_transitions_create_request]

        // [START android_location_transitions_request_updates]
        // myPendingIntent is the instance of PendingIntent where the app receives callbacks.
        val task = ActivityRecognition.getClient(context)
            .requestActivityTransitionUpdates(request, myPendingIntent)

        task.addOnSuccessListener {
            // Handle success.
        }

        task.addOnFailureListener { e: Exception ->
            // Handle error.
        }
        // [END android_location_transitions_request_updates]
    }

    @RequiresPermission(Manifest.permission.ACTIVITY_RECOGNITION)
    fun deregisterTransitions() {
        // [START android_location_transitions_remove_updates]
        // myPendingIntent is the instance of PendingIntent where the app receives callbacks.
        val task = ActivityRecognition.getClient(context)
            .removeActivityTransitionUpdates(myPendingIntent)

        task.addOnSuccessListener {
            myPendingIntent.cancel()
        }

        task.addOnFailureListener { e: Exception ->
            Log.e("MYCOMPONENT", e.message.toString())
        }
        // [END android_location_transitions_remove_updates]
    }
}

private class TransitionsBroadcastReceiver : BroadcastReceiver() {
    // [START android_location_transitions_on_receive]
    override fun onReceive(context: Context, intent: Intent) {
        if (ActivityTransitionResult.hasResult(intent)) {
            val result = ActivityTransitionResult.extractResult(intent)!!
            for (event in result.transitionEvents) {
                // chronological sequence of events....
            }
        }
    }
    // [END android_location_transitions_on_receive]
}
