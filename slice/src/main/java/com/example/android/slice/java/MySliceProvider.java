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
import android.net.Uri;

import com.example.android.snippets.R;

import androidx.core.graphics.drawable.IconCompat;
import androidx.slice.Slice;
import androidx.slice.SliceProvider;
import androidx.slice.builders.GridRowBuilder;
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.ListBuilder.HeaderBuilder;
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
        // Construct the parent.
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                .setAccentColor(0xff0F9D58); // Specify color for tinting icons.

        // Create the header.
        HeaderBuilder headerBuilder = new HeaderBuilder(listBuilder)
                .setTitle("Get a ride")
                .setSubtitle("Ride in 4 min.")
                .setSummary("Work in 1 hour 45 min | Home in 12 min.");

        // Add it to the list.
        listBuilder.setHeader(headerBuilder);

        // Construct the rows.
        RowBuilder rowBuilder = new RowBuilder(listBuilder)
                .setTitle("Home")
                .setSubtitle("12 miles | 12 min | $9.00")
                .addEndItem(IconCompat.createWithResource(getContext(), R.drawable.ic_home),
                        SliceHints.ICON_IMAGE);

        listBuilder.addRow(rowBuilder);

        // Add more rows if needed...

        return listBuilder.build();
    }
    // [END create_slice_with_header]

    // [START create_slice_with_action_in_header]
    public Slice createSliceWithActionInHeader(Uri sliceUri) {

        // Construct the list.
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                .setAccentColor(0xfff4b400); // Specify color for tinting icons

        // Construct the header.
        HeaderBuilder headerBuilder = new HeaderBuilder(listBuilder)
                .setTitle("Create new note")
                .setSubtitle("Easily done with this note taking app");

        // Construct our slice actions.
        SliceAction noteAction = new SliceAction(takeNoteIntent,
                IconCompat.createWithResource(getContext(), R.drawable.ic_pencil),
                ListBuilder.ICON_IMAGE, "Take note");

        SliceAction voiceNoteAction = new SliceAction(voiceNoteIntent,
                IconCompat.createWithResource(getContext(), R.drawable.ic_mic),
                ListBuilder.ICON_IMAGE,
                "Take voice note");

        SliceAction cameraNoteAction = new SliceAction(cameraNoteIntent,
                IconCompat.createWithResource(getContext(), R.drawable.ic_camera),
                ListBuilder.ICON_IMAGE,
                "Create photo note");

        // Set the header.
        listBuilder.setHeader(headerBuilder);

        // Add the actions to the list builder.
        listBuilder.addAction(noteAction);
        listBuilder.addAction(voiceNoteAction);
        listBuilder.addAction(cameraNoteAction);

        return listBuilder.build();
    }
    // [END create_slice_with_action_in_header]

    // [START create_action_with_action_in_row]
    public Slice createActionWithActionInRow(Uri sliceUri) {
        // Primary action - open wifi settings.
        SliceAction primaryAction = new SliceAction(wifiSettingsPendingIntent,
                IconCompat.createWithResource(getContext(), R.drawable.ic_wifi),
                "Wi-Fi Settings");

        // Toggle action - toggle wifi.
        SliceAction toggleAction = new SliceAction(wifiTogglePendingIntent,
                "Toggle Wi-Fi", isConnected /* isChecked */);

        // Create the parent builder.
        ListBuilder listBuilder = new ListBuilder(getContext(), wifiUri, ListBuilder.INFINITY);

        // Specify color for tinting icons / controls.
        listBuilder.setAccentColor(0xff4285f4);

        // Create the row.
        RowBuilder rowBuilder = new RowBuilder(listBuilder)
                .setTitle("Wi-Fi")
                .setPrimaryAction(primaryAction)
                .addEndItem(toggleAction);

        // Add the row.
        listBuilder.addRow(rowBuilder);

        // Build the slice.
        return listBuilder.build();
    }
    // [END create_action_with_action_in_row]

    // [START create_slice_with_gridrow]
    public Slice createSliceWithGridRow(Uri sliceUri) {
        // Create the parent builder.
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY);

        // Create the header.
        HeaderBuilder hb = new HeaderBuilder(listBuilder);
        hb.setTitle("Famous restaurants");
        hb.setPrimaryAction(new SliceAction(pendingIntent, icon, "Famous restaurants"));
        listBuilder.setHeader(hb);

        // Create the grid.
        GridRowBuilder gb = new GridRowBuilder(listBuilder);

        // Create the cells.
        GridRowBuilder.CellBuilder cell1 = new GridRowBuilder.CellBuilder(gb)
                .addImage(image1, ListBuilder.LARGE_IMAGE)
                .addTitleText("Top Restaurant")
                .addText("0.3 mil")
                .setContentIntent(intent1);
        GridRowBuilder.CellBuilder cell2 = new GridRowBuilder.CellBuilder(gb)
                .addImage(image2, ListBuilder.LARGE_IMAGE)
                .addTitleText("Fast and Casual")
                .addText("0.5 mil")
                .setContentIntent(intent2);
        GridRowBuilder.CellBuilder cell3 = new GridRowBuilder.CellBuilder(gb)
                .addImage(image3, ListBuilder.LARGE_IMAGE)
                .addTitleText("Casual Diner")
                .addText("0.9 mi")
                .setContentIntent(intent3);
        GridRowBuilder.CellBuilder cell4 = new GridRowBuilder.CellBuilder(gb)
                .addImage(image4, ListBuilder.LARGE_IMAGE)
                .addTitleText("Ramen Spot")
                .addText("1.2 mi")
                .setContentIntent(intent4);

        // Add cells to the grid.
        gb.addCell(cell1).addCell(cell2).addCell(cell3).addCell(cell4);

        // Add the grid to the list.
        listBuilder.addGridRow(gb);

        return listBuilder.build();
    }
    // [END create_slice_with_gridrow]

    // [START create_slice_with_range]
    public Slice createSliceWithRange(Uri sliceUri) {
        // Construct the parent.
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY);

        // Create the input row.
        ListBuilder.InputRangeBuilder volumeSliderRow = new ListBuilder.InputRangeBuilder(
                listBuilder)
                .setTitle("Ring Volume")
                .setInputAction(volumeChangedPendingIntent)
                .setMax(100)
                .setValue(30);

        // Add the row to the list.
        listBuilder.addInputRange(volumeSliderRow);

        return listBuilder.build();
    }
    // [END create_slice_with_range]

    // [START create_slice_showing_loading]
    public Slice createSliceShowingLoading(Uri sliceUri) {
        // Construct the parent.
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY);

        // Construct the row.
        RowBuilder rowBuilder1 = new RowBuilder(listBuilder)
                .setTitle("Ride to work");

        // We’re waiting to load the time to work so indicate that on the slice by
        // setting the subtitle with the overloaded method and indicate true.
        rowBuilder1.setSubtitle(null, true);
        rowBuilder1.addEndItem(IconCompat.createWithResource(getContext(), R.drawable.ic_work),
                ListBuilder.ICON_IMAGE);

        // Add the rows to the list
        listBuilder.addRow(rowBuilder1);

        return listBuilder.build();
    }
    // [END create_slice_showing_loading]

    // [START see_more_action]
    public Slice seeMoreActionSlice(Uri sliceUri) {
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY);
        // [START_EXCLUDE]
        // [END_EXCLUDE]
        listBuilder.setSeeMoreAction(seeAllNetworksPendingIntent);
        // [START_EXCLUDE]
        // [END_EXCLUDE]
        return listBuilder.build();
    }
    // [END see_more_action]

    // [START see_more_row]
    public Slice seeMoreRowSlice(Uri sliceUri) {
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY);
        // [START_EXCLUDE]
        // [END_EXCLUDE]
        RowBuilder rowBuilder = new RowBuilder(listBuilder)
                .setTitle("See all available networks")
                .addEndItem(IconCompat.createWithResource(getContext(), R.drawable.ic_right_caret),
                        ListBuilder.ICON_IMAGE)
                .setPrimaryAction(new SliceAction(seeAllNetworksPendingIntent,
                        IconCompat.createWithResource(getContext(), R.drawable.ic_wifi),
                        "Wi-Fi Networks"));
        listBuilder.setSeeMoreRow(rowBuilder);
        // [START_EXCLUDE]
        // [END_EXCLUDE]
        return listBuilder.build();
    }
    // [END see_more_row]
}