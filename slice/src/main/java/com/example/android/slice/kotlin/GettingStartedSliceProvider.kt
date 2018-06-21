/*
 *  Copyright 2018 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.example.android.slice.kotlin

import android.app.PendingIntent
import android.content.Intent
import android.net.Uri
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.SliceProvider
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.ListBuilder.INFINITY
import androidx.slice.builders.SliceAction
import androidx.slice.builders.inputRange
import androidx.slice.builders.list
import androidx.slice.builders.row
import com.example.android.snippets.R

class GettingStartedSliceProvider : SliceProvider() {
    lateinit var brightnessPendingIntent: PendingIntent

    // [START on_bind_slice]
    override fun onBindSlice(sliceUri: Uri): Slice? {
        return if (sliceUri.path == "/hello") {
            ListBuilder(context, sliceUri, ListBuilder.INFINITY)
                .addRow { it.setTitle("URI found.") }
                .build()
        } else {
            ListBuilder(context, sliceUri, ListBuilder.INFINITY)
                .addRow { it.setTitle("URI not found.") }
                .build()
        }
    }
    // [END on_bind_slice]

    override fun onCreateSliceProvider(): Boolean {
        return true
    }

    // [START create_slice]
    fun createSlice(sliceUri: Uri): Slice {
        val activityAction = createActivityAction()
        return list(context, sliceUri, INFINITY) {
            row {
                setTitle("Perform action in app")
                setPrimaryAction(activityAction)
            }
        }
    }

    fun createActivityAction(): SliceAction {
        val intent = Intent(context, MainActivity::class.java)
        return SliceAction(
            PendingIntent.getActivity(context, 0, intent, 0),
            IconCompat.createWithResource(context, R.drawable.ic_home),
            "Open MainActivity."
        )
    }
    // [END create_slice]

    // [START create_brightness_slice]
    fun createBrightnessSlice(sliceUri: Uri): Slice {
        val toggleAction =
            SliceAction(createToggleIntent(), "Toggle adaptive brightness", true)
        return list(context, sliceUri, ListBuilder.INFINITY) {
            row {
                setTitle("Adaptive brightness")
                setSubtitle("Optimizes brightness for available light")
                setPrimaryAction(toggleAction)
            }
            inputRange {
                setInputAction(brightnessPendingIntent)
                setMax(100)
                setValue(45)
            }
        }
    }

    fun createToggleIntent(): PendingIntent {
        val intent = Intent(context, MyBroadcastReceiver::class.java)
        return PendingIntent.getBroadcast(context, 0, intent, 0)
    }
    // [END create_brightness_slice]

    lateinit var actionIcon: IconCompat

    // [START create_dynamic_slice]
    fun createDynamicSlice(sliceUri: Uri): Slice {
        return when (sliceUri.path) {
            "/count" -> {
                val toastAndIncrementAction = SliceAction(
                    createToastAndIncrementIntent("Item clicked"),
                    actionIcon, "Increment."
                )
                list(context, sliceUri, ListBuilder.INFINITY) {
                    row {
                        setPrimaryAction(toastAndIncrementAction)
                        setTitle("Count: ${MyBroadcastReceiver.receivedCount}")
                        setSubtitle("Click me")
                    }
                }
            }

            else -> {
                list(context, sliceUri, ListBuilder.INFINITY) {
                    row { setTitle("URI not found.") }
                }
            }
        }
    }

    fun createToastAndIncrementIntent(s: String): PendingIntent {
        return PendingIntent.getBroadcast(
            context, 0,
            Intent(context, MyBroadcastReceiver::class.java)
                .putExtra(MyBroadcastReceiver.EXTRA_MESSAGE, s), 0
        )
    }
    // [END create_dynamic_slice]
}