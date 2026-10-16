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
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.provider.CalendarContract
import android.provider.CalendarContract.Events
import java.util.Calendar

private class SendingIntentsActivity : Activity() {
    fun buildDialIntent() {
        // [START android_intents_sending_dial]
        val callIntent: Intent = Uri.parse("tel:5551234").let { number ->
            Intent(Intent.ACTION_DIAL, number)
        }
        // [END android_intents_sending_dial]
    }

    fun buildMapIntent() {
        // [START android_intents_sending_view_map]
        // Map point based on address.
        val mapIntent: Intent = Uri.parse(
            "geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California"
        ).let { location ->
            // Or map point based on latitude/longitude.
            // val location: Uri = Uri.parse("geo:37.422219,-122.08364?z=14") // z param is zoom level
            Intent(Intent.ACTION_VIEW, location)
        }
        // [END android_intents_sending_view_map]
    }

    fun buildWebIntent() {
        // [START android_intents_sending_view_web]
        val webIntent: Intent = Uri.parse("https://www.android.com").let { webpage ->
            Intent(Intent.ACTION_VIEW, webpage)
        }
        // [END android_intents_sending_view_web]
    }

    fun buildEmailAttachmentIntent() {
        // [START android_intents_sending_email_attachment]
        Intent(Intent.ACTION_SEND).apply {
            // The intent does not have a URI, so declare the "text/plain" MIME type.
            type = "text/plain"
            putExtra(Intent.EXTRA_EMAIL, arrayOf("jan@example.com")) // recipients
            putExtra(Intent.EXTRA_SUBJECT, "Email subject")
            putExtra(Intent.EXTRA_TEXT, "Email message text")
            putExtra(Intent.EXTRA_STREAM, Uri.parse("content://path/to/email/attachment"))
            // You can also attach multiple items by passing an ArrayList of Uris.
        }
        // [END android_intents_sending_email_attachment]
    }

    fun buildCalendarEventIntent() {
        // [START android_intents_sending_calendar_event]
        // Event is on January 23, 2021 -- from 7:30 AM to 10:30 AM.
        Intent(Intent.ACTION_INSERT, Events.CONTENT_URI).apply {
            val beginTime: Calendar = Calendar.getInstance().apply {
                set(2021, 0, 23, 7, 30)
            }
            val endTime = Calendar.getInstance().apply {
                set(2021, 0, 23, 10, 30)
            }
            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.timeInMillis)
            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.timeInMillis)
            putExtra(Events.TITLE, "Ninja class")
            putExtra(Events.EVENT_LOCATION, "Secret dojo")
        }
        // [END android_intents_sending_calendar_event]
    }

    fun invokeStartActivity() {
        // [START android_intents_sending_start_activity]
        startActivity(intent)
        // [END android_intents_sending_start_activity]
    }

    fun invokeStartActivitySafely() {
        // [START android_intents_sending_catch_not_found]
        try {
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            // Define what your app should do if no activity can handle the intent.
        }
        // [END android_intents_sending_catch_not_found]
    }

    fun completeMapExample() {
        // [START android_intents_sending_complete_example]
        // Build the intent.
        val location = Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California")
        val mapIntent = Intent(Intent.ACTION_VIEW, location)

        // Try to invoke the intent.
        try {
            startActivity(mapIntent)
        } catch (e: ActivityNotFoundException) {
            // Define what your app should do if no activity can handle the intent.
        }
        // [END android_intents_sending_complete_example]
    }

    fun showAppChooser() {
        // [START android_intents_sending_app_chooser]
        val intent = Intent(Intent.ACTION_SEND)

        // Create intent to show chooser.
        val chooser = Intent.createChooser(intent, /* title */ null)

        // Try to invoke the intent.
        try {
            startActivity(chooser)
        } catch (e: ActivityNotFoundException) {
            // Define what your app should do if no activity can handle the intent.
        }
        // [END android_intents_sending_app_chooser]
    }
}
