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
import androidx.slice.builders.ListBuilder;
import androidx.slice.builders.SliceAction;

public class GettingStartedSliceProvider extends SliceProvider {

    private PendingIntent brightnessPendingIntent;
    private IconCompat actionIcon;

    @Override
    public boolean onCreateSliceProvider() {
        return false;
    }

    // [START slices_getting_started_onBindSlice]
    @Override
    public Slice onBindSlice(Uri sliceUri) {
        // Create parent ListBuilder.
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY);

        // Create RowBuilder.
        ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder(listBuilder);

        if (sliceUri.getPath().equals("/hello")) {
            rowBuilder.setTitle("URI found.");
        } else {
            rowBuilder.setTitle("URI not found.");
        }

        // Add Row to List.
        listBuilder.addRow(rowBuilder);

        // Build List.
        return listBuilder.build();
    }
    // [END slices_getting_started_onBindSlice]

    // [START slices_getting_started_createSlice]
    public Slice createSlice(Uri sliceUri) {
        SliceAction activityAction = createActivityAction();
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY);
        ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder(listBuilder)
                .setTitle("Perform action in app.")
                .setPrimaryAction(activityAction);
        listBuilder.addRow(rowBuilder);
        return listBuilder.build();
    }

    public SliceAction createActivityAction() {
        Intent intent = new Intent(getContext(), MainActivity.class);
        return new SliceAction(PendingIntent.getActivity(getContext(), 0, intent, 0),
                IconCompat.createWithResource(getContext(), R.drawable.ic_home),
                "Open MainActivity");
    }
    // [END slices_getting_started_createSlice]

    // [START slices_getting_started_createBrightnessSlice]
    public Slice createBrightnessSlice(Uri sliceUri) {
        SliceAction toggleAction = new SliceAction(createToggleIntent(),
                "Toggle adaptive brightness", true);
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY);
        ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder(listBuilder)
                .setTitle("Adaptive brightness")
                .setSubtitle("Optimizes brightness for available light.")
                .setPrimaryAction(toggleAction);

        listBuilder.addRow(rowBuilder);

        ListBuilder.InputRangeBuilder inputRangeBuilder = new ListBuilder.InputRangeBuilder(
                listBuilder)
                .setInputAction(brightnessPendingIntent)
                .setMax(100)
                .setValue(45);

        listBuilder.addInputRange(inputRangeBuilder);

        return listBuilder.build();
    }

    public PendingIntent createToggleIntent() {
        Intent intent = new Intent(getContext(), MyBroadcastReceiver.class);
        return PendingIntent.getBroadcast(getContext(), 0, intent, 0);
    }
    // [END slices_getting_started_createBrightnessSlice]

    // [START slices_getting_started_createDynamicSlice]
    public Slice createDynamicSlice(Uri sliceUri) {
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY);
        ListBuilder.RowBuilder rowBuilder = new ListBuilder.RowBuilder(listBuilder);
        switch (sliceUri.getPath()) {
            case "/count":
                SliceAction toastAndIncrementAction = new SliceAction(createToastAndIncrementIntent(
                        "Item clicked."), actionIcon, "Increment.");
                rowBuilder.setPrimaryAction(toastAndIncrementAction)
                        .setTitle("Count: " + MyBroadcastReceiver.sReceivedCount)
                        .setSubtitle("Click me");
                listBuilder.addRow(rowBuilder);
                break;
            default:
                rowBuilder.setTitle("URI not found.");
                listBuilder.addRow(rowBuilder);
                break;
        }
        return listBuilder.build();
    }

    public PendingIntent createToastAndIncrementIntent(String s) {
        Intent intent = new Intent(getContext(), MyBroadcastReceiver.class)
                .putExtra(MyBroadcastReceiver.EXTRA_MESSAGE, s);
        return PendingIntent.getBroadcast(getContext(), 0, intent, 0);
    }
    // [END slices_getting_started_createDynamicSlice]
}
