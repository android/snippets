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
import android.net.Uri
import androidx.core.graphics.drawable.IconCompat
import androidx.slice.Slice
import androidx.slice.SliceProvider
import androidx.slice.builders.ListBuilder
import androidx.slice.builders.ListBuilder.LARGE_IMAGE
import androidx.slice.builders.SliceAction
import androidx.slice.builders.cell
import androidx.slice.builders.gridRow
import androidx.slice.builders.header
import androidx.slice.builders.inputRange
import androidx.slice.builders.list
import androidx.slice.builders.row
import androidx.slice.builders.seeMoreRow
import androidx.slice.core.SliceHints
import androidx.slice.core.SliceHints.ICON_IMAGE
import com.example.android.snippets.R

class MySliceProvider : SliceProvider() {
    var isConnected: Boolean = false
    lateinit var wifiTogglePendingIntent: PendingIntent
    lateinit var wifiUri: Uri
    lateinit var seeAllNetworksPendingIntent: PendingIntent
    lateinit var takeNoteIntent: PendingIntent
    lateinit var voiceNoteIntent: PendingIntent
    lateinit var cameraNoteIntent: PendingIntent
    lateinit var pendingIntent: PendingIntent
    lateinit var intent1: PendingIntent
    lateinit var intent2: PendingIntent
    lateinit var intent3: PendingIntent
    lateinit var intent4: PendingIntent
    lateinit var icon: IconCompat
    lateinit var image1: IconCompat
    lateinit var image2: IconCompat
    lateinit var image3: IconCompat
    lateinit var image4: IconCompat
    lateinit var volumeChangedPendingIntent: PendingIntent
    lateinit var wifiSettingsPendingIntent: PendingIntent

    override fun onBindSlice(sliceUri: Uri): Slice? {
        return null
    }

    override fun onCreateSliceProvider(): Boolean {
        return false
    }

    // [START create_slice_with_header]
    fun createSliceWithHeader(sliceUri: Uri) =
        list(context, sliceUri, ListBuilder.INFINITY) {
            setAccentColor(0xff0F9D) // Specify color for tinting icons
            header {
                setTitle("Get a ride")
                setSubtitle("Ride in 4 min")
                setSummary("Work in 1 hour 45 min | Home in 12 min")
            }
            row {
                setTitle("Home")
                setSubtitle("12 miles | 12 min | $9.00")
                addEndItem(
                    IconCompat.createWithResource(context, R.drawable.ic_home),
                    SliceHints.ICON_IMAGE
                )
            }
        }
    // [END create_slice_with_header]

    // [START create_slice_with_action_in_header]
    fun createSliceWithActionInHeader(sliceUri: Uri): Slice {
        // Construct our slice actions.
        val noteAction = SliceAction(
            takeNoteIntent,
            IconCompat.createWithResource(context, R.drawable.ic_pencil),
            ICON_IMAGE,
            "Take note"
        )

        val voiceNoteAction = SliceAction(
            voiceNoteIntent,
            IconCompat.createWithResource(context, R.drawable.ic_mic),
            ICON_IMAGE,
            "Take voice note"
        )

        val cameraNoteAction = SliceAction(
            cameraNoteIntent,
            IconCompat.createWithResource(context, R.drawable.ic_camera),
            ICON_IMAGE,
            "Create photo note"
        )

        // Construct the list.
        return list(context, sliceUri, ListBuilder.INFINITY) {
            setAccentColor(0xfff4b4) // Specify color for tinting icons
            header {
                setTitle("Create new note")
                setSubtitle("Easily done with this note taking app")
            }
            addAction(noteAction)
            addAction(voiceNoteAction)
            addAction(cameraNoteAction)
        }
    }
    // [END create_slice_with_action_in_header]

    // [START create_action_with_action_in_row]
    fun createActionWithActionInRow(sliceUri: Uri): Slice {
        // Primary action - open wifi settings.
        val primaryAction = SliceAction(
            wifiSettingsPendingIntent,
            IconCompat.createWithResource(context, R.drawable.ic_wifi),
            "Wi-Fi Settings"
        )

        // Toggle action - toggle wifi.
        val toggleAction = SliceAction(
            wifiTogglePendingIntent,
            "Toggle Wi-Fi", isConnected /* isChecked */
        )

        // Create the parent builder.
        return list(context, wifiUri, ListBuilder.INFINITY) {
            setAccentColor(0xff4285) // Specify color for tinting icons / controls.
            row {
                setTitle("Wi-Fi")
                setPrimaryAction(primaryAction)
                addEndItem(toggleAction)
            }
        }
    }
    // [END create_action_with_action_in_row]

    // [START create_slice_with_gridrow]
    fun createSliceWithGridRow(sliceUri: Uri): Slice {
        // Create the parent builder.
        return list(context, sliceUri, ListBuilder.INFINITY) {
            header {
                setTitle("Famous restaurants")
                setPrimaryAction(SliceAction(pendingIntent, icon, "Famous restaurants"))
            }
            gridRow {
                cell {
                    addImage(image1, LARGE_IMAGE)
                    addTitleText("Top Restaurant")
                    addText("0.3 mil")
                    setContentIntent(intent1)
                }
                cell {
                    addImage(image2, LARGE_IMAGE)
                    addTitleText("Fast and Casual")
                    addText("0.5 mil")
                    setContentIntent(intent2)
                }
                cell {
                    addImage(image3, LARGE_IMAGE)
                    addTitleText("Casual Diner")
                    addText("0.9 mi")
                    setContentIntent(intent3)
                }
                cell {
                    addImage(image4, LARGE_IMAGE)
                    addTitleText("Ramen Spot")
                    addText("1.2 mi")
                    setContentIntent(intent4)
                }
            }
        }
    }
    // [END create_slice_with_gridrow]

    // [START create_slice_with_range]
    fun createSliceWithRange(sliceUri: Uri): Slice {
        return list(context, sliceUri, ListBuilder.INFINITY) {
            inputRange {
                setTitle("Ring Volume")
                setInputAction(volumeChangedPendingIntent)
                setMax(100)
                setValue(30)
            }
        }
    }
    // [END create_slice_with_range]

    // [START create_slice_showing_loading]
    fun createSliceShowingLoading(sliceUri: Uri): Slice {
        // We’re waiting to load the time to work so indicate that on the slice by
        // setting the subtitle with the overloaded method and indicate true.
        return list(context, sliceUri, ListBuilder.INFINITY) {
            row {
                setTitle("Ride to work")
                setSubtitle(null, true)
                addEndItem(
                    IconCompat.createWithResource(context, R.drawable.ic_work), ICON_IMAGE
                )
            }
        }
    }
    // [END create_slice_showing_loading]

    // [START see_more_action]
    fun seeMoreActionSlice(sliceUri: Uri) =
        list(context, sliceUri, ListBuilder.INFINITY) {
            // [START_EXCLUDE]
            // [END_EXCLUDE]
            setSeeMoreAction(seeAllNetworksPendingIntent)
            // [START_EXCLUDE]
            // [END_EXCLUDE]
        }
    // [END see_more_action

    // [START see_more_row]
    fun seeMoreRowSlice(sliceUri: Uri) =
        list(context, sliceUri, ListBuilder.INFINITY) {
            // [START_EXCLUDE]
            // [END_EXCLUDE]
            seeMoreRow {
                setTitle("See all available networks")
                addEndItem(
                    IconCompat.createWithResource(context, R.drawable.ic_right_caret), ICON_IMAGE
                )
                setPrimaryAction(
                    SliceAction(
                        seeAllNetworksPendingIntent,
                        IconCompat.createWithResource(context, R.drawable.ic_wifi),
                        "Wi-Fi Networks"
                    )
                )
            }
        }
    // [END see_more_row]
}