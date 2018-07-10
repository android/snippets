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

    // [START on_bind_slice]
    @Override
    public Slice onBindSlice(Uri sliceUri) {
        if (getContext() == null) {
            return null;
        }
        SliceAction activityAction = createActivityAction();
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY);
        // Create parent ListBuilder.
        if ("/hello".equals(sliceUri.getPath())) {
            listBuilder.addRow(new ListBuilder.RowBuilder()
                    .setTitle("Hello World")
                    .setPrimaryAction(activityAction)
            );
        } else {
            listBuilder.addRow(new ListBuilder.RowBuilder()
                    .setTitle("URI not recognized")
                    .setPrimaryAction(activityAction)
            );
        }
        return listBuilder.build();
    }
    // [END on_bind_slice]

    // [START create_slice]
    public Slice createSlice(Uri sliceUri) {
        if (getContext() == null) {
            return null;
        }
        SliceAction activityAction = createActivityAction();
        return new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                .addRow(new ListBuilder.RowBuilder()
                        .setTitle("Perform action in app.")
                        .setPrimaryAction(activityAction)
                ).build();
    }

    public SliceAction createActivityAction() {
        if (getContext() == null) {
            return null;
        }
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
    // [END create_slice]

    // [START create_brightness_slice]
    public Slice createBrightnessSlice(Uri sliceUri) {
        if (getContext() == null) {
            return null;
        }
        SliceAction toggleAction = SliceAction.createToggle(
                createToggleIntent(),
                "Toggle adaptive brightness",
                true
        );
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY)
                .addRow(new ListBuilder.RowBuilder()
                        .setTitle("Adaptive brightness")
                        .setSubtitle("Optimizes brightness for available light.")
                        .setPrimaryAction(toggleAction)
                ).addInputRange(new ListBuilder.InputRangeBuilder()
                        .setInputAction(brightnessPendingIntent)
                        .setMax(100)
                        .setValue(45)
                );
        return listBuilder.build();
    }

    public PendingIntent createToggleIntent() {
        Intent intent = new Intent(getContext(), MyBroadcastReceiver.class);
        return PendingIntent.getBroadcast(getContext(), 0, intent, 0);
    }
    // [END create_brightness_slice]

    // [START create_dynamic_slice]
    public Slice createDynamicSlice(Uri sliceUri) {
        if (getContext() == null || sliceUri.getPath() == null) {
            return null;
        }
        ListBuilder listBuilder = new ListBuilder(getContext(), sliceUri, ListBuilder.INFINITY);
        switch (sliceUri.getPath()) {
            case "/count":
                SliceAction toastAndIncrementAction = SliceAction.create(
                        createToastAndIncrementIntent("Item clicked."),
                        actionIcon,
                        ListBuilder.ICON_IMAGE,
                        "Increment."
                );
                listBuilder.addRow(
                        new ListBuilder.RowBuilder()
                                .setPrimaryAction(toastAndIncrementAction)
                                .setTitle("Count: " + MyBroadcastReceiver.sReceivedCount)
                                .setSubtitle("Click me")
                );
                break;
            default:
                listBuilder.addRow(
                        new ListBuilder.RowBuilder()
                                .setPrimaryAction(createActivityAction())
                                .setTitle("URI not found.")
                );
                break;
        }
        return listBuilder.build();
    }

    public PendingIntent createToastAndIncrementIntent(String s) {
        Intent intent = new Intent(getContext(), MyBroadcastReceiver.class)
                .putExtra(MyBroadcastReceiver.EXTRA_MESSAGE, s);
        return PendingIntent.getBroadcast(getContext(), 0, intent, 0);
    }
    // [END create_dynamic_slice]
}
