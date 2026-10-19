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

package com.example.compose.snippets.intents

import android.app.Activity
import android.app.PendingIntent
import android.app.Service
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.StrictMode
import android.os.StrictMode.VmPolicy
import com.example.compose.snippets.R

private const val REQUEST_CODE = 0

private object ActionTimeTravelScope {
    // [START android_intents_filters_action_timetravel]
    const val ACTION_TIMETRAVEL = "com.example.action.TIMETRAVEL"
    // [END android_intents_filters_action_timetravel]
}

private object ExtraGigawattsScope {
    // [START android_intents_filters_extra_gigawatts]
    const val EXTRA_GIGAWATTS = "com.example.EXTRA_GIGAWATTS"
    // [END android_intents_filters_extra_gigawatts]
}

private class IntentsFiltersExamplesActivity : Activity() {
    private val fileUrl = "http://www.example.com/image.png"
    private val textMessage = "Hello, world!"

    fun startDownloadService() {
        // [START android_intents_filters_explicit_intent]
        // Executed in an Activity, so 'this' is the Context.
        // The fileUrl is a string URL, such as "http://www.example.com/image.png".
        val downloadIntent = Intent(this, DownloadService::class.java).apply {
            data = Uri.parse(fileUrl)
        }
        startService(downloadIntent)
        // [END android_intents_filters_explicit_intent]
    }

    fun sendImplicitIntent() {
        // [START android_intents_filters_implicit_intent]
        // Create the text message with a string.
        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, textMessage)
            type = "text/plain"
        }

        // Try to invoke the intent.
        try {
            startActivity(sendIntent)
        } catch (e: ActivityNotFoundException) {
            // Define what your app should do if no activity can handle the intent.
        }
        // [END android_intents_filters_implicit_intent]
    }

    fun forceAppChooser() {
        // [START android_intents_filters_force_chooser]
        val sendIntent = Intent(Intent.ACTION_SEND)
        // ...

        // Always use string resources for UI text.
        // This says something like "Share this photo with".
        val title: String = resources.getString(R.string.chooser_title)
        // Create intent to show the chooser dialog.
        val chooser: Intent = Intent.createChooser(sendIntent, title)

        // Verify the original intent will resolve to at least one activity.
        if (sendIntent.resolveActivity(packageManager) != null) {
            startActivity(chooser)
        }
        // [END android_intents_filters_force_chooser]
    }

    // [START android_intents_filters_detect_unsafe_launch]
    fun onCreate() {
        StrictMode.setVmPolicy(
            VmPolicy.Builder()
                // Other StrictMode checks that you've previously added.
                // ...
                .detectUnsafeIntentLaunch()
                .penaltyLog()
                // Consider also adding penaltyDeath().
                .build()
        )
    }
    // [END android_intents_filters_detect_unsafe_launch]

    fun createImmutablePendingIntent() {
        // [START android_intents_filters_immutable_pending_intent]
        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            REQUEST_CODE,
            intent,
            /* flags */ PendingIntent.FLAG_IMMUTABLE
        )
        // [END android_intents_filters_immutable_pending_intent]
    }

    fun checkPendingIntentSdk() {
        // [START android_intents_filters_pending_intent_sdk_check]
        if (Build.VERSION.SDK_INT >= 23) {
            // Create a PendingIntent using FLAG_IMMUTABLE.
        } else {
            // Existing code that creates a PendingIntent.
        }
        // [END android_intents_filters_pending_intent_sdk_check]
    }
}

private class DownloadService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
}
