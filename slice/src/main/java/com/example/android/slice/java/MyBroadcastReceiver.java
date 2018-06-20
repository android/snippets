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

import static android.app.slice.Slice.EXTRA_TOGGLE_STATE;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.Toast;

// [START slices_getting_started_MyBroadcastReceiver]
public class MyBroadcastReceiver extends BroadcastReceiver {

    public static int sReceivedCount = 0;
    public static String EXTRA_MESSAGE = "message";

    private static Uri sliceUri = Uri.parse("content://com.android.example.slicesample/count");

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.hasExtra(EXTRA_TOGGLE_STATE)) {
            Toast.makeText(context, "Toggled:  " + intent.getBooleanExtra(
                    EXTRA_TOGGLE_STATE, false),
                    Toast.LENGTH_LONG).show();
            sReceivedCount++;
            context.getContentResolver().notifyChange(sliceUri, null);
        }
    }
}
// [END slices_getting_started_MyBroadcastReceiver]
