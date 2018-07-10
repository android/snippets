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

package com.example.android.slice.java;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;

import com.example.android.snippets.R;

import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import androidx.slice.builders.GridRowBuilder;
import androidx.slice.builders.GridRowBuilder.CellBuilder;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.ListBuilder.HeaderBuilder;
import androidx.slice.builders.ListBuilder.InputRangeBuilder;
import androidx.slice.builders.ListBuilder.RowBuilder;
import androidx.slice.builders.SliceAction;
import androidx.slice.core.SliceHints;

public class MySliceProvider extends SliceProvider {

    boolean isConnected;
    PendingIntent wifiTogglePendingIntent;
    Uri wifiUri;
    PendingIntent seeAllNetworksPendingIntent;
    PendingIntent takeNoteIntent;
    PendingIntent voiceNoteIntent;
    PendingIntent cameraNoteIntent;
    PendingIntent pendingIntent;
    PendingIntent intent1;
    PendingIntent intent2;
    PendingIntent intent3;
    PendingIntent intent4;
    IconCompat icon;
    IconCompat image1;
    IconCompat image2;
    IconCompat image3;
    IconCompat image4;
    PendingIntent volumeChangedPendingIntent;
    PendingIntent wifiSettingsPendingIntent;

    @Override
    public boolean onCreateSliceProvider() {
        return false;
    }

    @Override
    public Slice onBindSlice(Uri sliceUri) {
        return null;
    }

    // [START create_slice_with_header]
    public Slice createSliceWithHeader(Uri sliceUri) {
        if (getContext() == null) {
            return null;
        }

        // Construct the parent.
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                .setAccentColor(0xff0F9D58) // Specify color for tinting icons.
                .setHeader( // Create the header and add to slice.
                        new HeaderBuilder()
                                .setTitle("Get a ride")
                                .setSubtitle("Ride in 4 min.")
                                .setSummary("Work in 1 hour 45 min | Home in 12 min.")
                ).addRow(new RowBuilder() // Add a row.
                        .setPrimaryAction(
                                createActivityAction()) // A slice always needs a SliceAction.
                        .setTitle("Home")
                        .setSubtitle("12 miles | 12 min | $9.00")
                        .addEndItem(IconCompat.createWithResource(getContext(), R.drawable.ic_home),
                                SliceHints.ICON_IMAGE)
                ); // Add more rows if needed...
        return listBuilder.build();
    }
    // [END create_slice_with_header]

    // [START create_slice_with_action_in_header]
    public Slice createSliceWithActionInHeader(Uri sliceUri) {
        if (getContext() == null) {
            return null;
        }
        // Construct our slice actions.
        SliceAction noteAction = SliceAction.create(takeNoteIntent,
                IconCompat.createWithResource(getContext(), R.drawable.ic_pencil),
                ListBuilder.ICON_IMAGE, "Take note");

        SliceAction voiceNoteAction = SliceAction.create(voiceNoteIntent,
                IconCompat.createWithResource(getContext(), R.drawable.ic_mic),
                ListBuilder.ICON_IMAGE,
                "Take voice note");

        SliceAction cameraNoteAction = SliceAction.create(cameraNoteIntent,
                IconCompat.createWithResource(getContext(), R.drawable.ic_camera),
                ListBuilder.ICON_IMAGE,
                "Create photo note");


        // Construct the list.
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                .setAccentColor(0xfff4b400) // Specify color for tinting icons
                .setHeader(new HeaderBuilder() // Construct the header.
                        .setTitle("Create new note")
                        .setSubtitle("Easily done with this note taking app")
                )
                .addRow(new RowBuilder()
                        .setTitle("Enter app")
                        .setPrimaryAction(createActivityAction())
                )
                // Add the actions to the ListBuilder.
                .addAction(noteAction)
                .addAction(voiceNoteAction)
                .addAction(cameraNoteAction);
        return listBuilder.build();
    }
    // [END create_slice_with_action_in_header]

    // [START create_action_with_action_in_row]
    public Slice createActionWithActionInRow(Uri sliceUri) {
        if (getContext() == null) {
            return null;
        }
        // Primary action - open wifi settings.
        SliceAction primaryAction = SliceAction.create(wifiSettingsPendingIntent,
                IconCompat.createWithResource(getContext(), R.drawable.ic_wifi),
                ListBuilder.ICON_IMAGE,
                "Wi-Fi Settings"
        );

        // Toggle action - toggle wifi.
        SliceAction toggleAction = SliceAction.createToggle(wifiTogglePendingIntent,
                "Toggle Wi-Fi", isConnected /* isChecked */);

        // Create the parent builder.
        ListBuilder listBuilder = new ListBuilder(getContext(), wifiUri, ListBuilder.INFINITY)
                // Specify color for tinting icons / controls.
                .setAccentColor(0xff4285f4)
                // Create and add a row.
                .addRow(new RowBuilder()
                        .setTitle("Wi-Fi")
                        .setPrimaryAction(primaryAction)
                        .addEndItem(toggleAction));
        // Build the slice.
        return listBuilder.build();
    }
    // [END create_action_with_action_in_row]

    // [START create_slice_with_gridrow]
    public Slice createSliceWithGridRow(Uri sliceUri) {
        if (getContext() == null) {
            return null;
        }
        // Create the parent builder.
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                .setHeader(
                        // Create the header.
                        new HeaderBuilder()
                                .setTitle("Famous restaurants")
                                .setPrimaryAction(SliceAction
                                        .create(pendingIntent, icon, ListBuilder.ICON_IMAGE,
                                                "Famous restaurants"))
                )
                // Add a grid row to the list.
                .addGridRow(new GridRowBuilder()
                        // Add cells to the grid row.
                        .addCell(new CellBuilder()
                                .addImage(image1, ListBuilder.LARGE_IMAGE)
                                .addTitleText("Top Restaurant")
                                .addText("0.3 mil")
                                .setContentIntent(intent1)
                        ).addCell(new CellBuilder()
                                .addImage(image2, ListBuilder.LARGE_IMAGE)
                                .addTitleText("Fast and Casual")
                                .addText("0.5 mil")
                                .setContentIntent(intent2)
                        )
                        .addCell(new CellBuilder()
                                .addImage(image3, ListBuilder.LARGE_IMAGE)
                                .addTitleText("Casual Diner")
                                .addText("0.9 mi")
                                .setContentIntent(intent3))
                        .addCell(new CellBuilder()
                                .addImage(image4, ListBuilder.LARGE_IMAGE)
                                .addTitleText("Ramen Spot")
                                .addText("1.2 mi")
                                .setContentIntent(intent4))
                        // Every slice needs a primary action.
                        .setPrimaryAction(createActivityAction())
                );
        return listBuilder.build();
    }
    // [END create_slice_with_gridrow]

    // [START create_slice_with_range]
    public Slice createSliceWithRange(Uri sliceUri) {
        if (getContext() == null) {
            return null;
        }
        // Construct the parent.
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                .addRow(new RowBuilder() // Every slice needs a row.
                        .setTitle("Enter app")
                         // Every slice needs a primary action.
                        .setPrimaryAction(createActivityAction())
                )
                .addInputRange(new InputRangeBuilder() // Create the input row.
                        .setTitle("Ring Volume")
                        .setInputAction(volumeChangedPendingIntent)
                        .setMax(100)
                        .setValue(30)
                );
        return listBuilder.build();
    }
    // [END create_slice_with_range]

    // [START create_slice_showing_loading]
    public Slice createSliceShowingLoading(Uri sliceUri) {
        if (getContext() == null) {
            return null;
        }
        // Construct the parent.
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                // Construct the row.
                .addRow(new RowBuilder()
                        .setPrimaryAction(createActivityAction())
                        .setTitle("Ride to work")
                        // We’re waiting to load the time to work so indicate that on the slice by
                        // setting the subtitle with the overloaded method and indicate true.
                        .setSubtitle(null, true)
                        .addEndItem(IconCompat.createWithResource(getContext(), R.drawable.ic_work),
                                ListBuilder.ICON_IMAGE)
                );
        return listBuilder.build();
    }

    private SliceAction createActivityAction() {
        return SliceAction.create(
                PendingIntent.getActivity(
                        getContext(),
                        0,
                        new Intent(getContext(), MainActivity.class),
                        0
                ),
                IconCompat.createWithResource(getContext(), R.drawable.ic_home),
                ListBuilder.ICON_IMAGE,
                "Enter app"
        );
    }
    // [END create_slice_showing_loading]

    // [START see_more_action]
    public Slice seeMoreActionSlice(Uri sliceUri) {
        if (getContext() == null) {
            return null;
        }
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY);
        // [START_EXCLUDE]
        listBuilder.addRow(new RowBuilder()
                .setTitle("Hello")
                .setPrimaryAction(createActivityAction())
        );
        // [END_EXCLUDE]
        listBuilder.setSeeMoreAction(seeAllNetworksPendingIntent);
        // [START_EXCLUDE]
        // [END_EXCLUDE]
        return listBuilder.build();
    }
    // [END see_more_action]

    // [START see_more_row]
    public Slice seeMoreRowSlice(Uri sliceUri) {
        if (getContext() == null) {
            return null;
        }
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                // [START_EXCLUDE]
                .addRow(new RowBuilder()
                        .setTitle("Hello")
                        .setPrimaryAction(createActivityAction())
                )
                // [END_EXCLUDE]
                .setSeeMoreRow(new RowBuilder()
                        .setTitle("See all available networks")
                        .addEndItem(IconCompat
                                        .createWithResource(getContext(), R.drawable
                                                .ic_right_caret),
                                ListBuilder.ICON_IMAGE)
                        .setPrimaryAction(SliceAction.create(seeAllNetworksPendingIntent,
                                IconCompat.createWithResource(getContext(), R.drawable.ic_wifi),
                                ListBuilder.ICON_IMAGE,
                                "Wi-Fi Networks"))
                );
        // [START_EXCLUDE]
        // [END_EXCLUDE]
        return listBuilder.build();
    }
    // [END see_more_row]
}