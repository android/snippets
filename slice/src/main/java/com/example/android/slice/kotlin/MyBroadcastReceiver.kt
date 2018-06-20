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

import android.app.slice.Slice
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast

//class MyBroadcastReceiver : BroadcastReceiver() {
//
//    override fun onReceive(context: Context, intent: Intent) {
//        if (intent.hasExtra(Slice.EXTRA_TOGGLE_STATE)) {
//            Toast.makeText(context, "Toggled:  " + intent.getBooleanExtra(
//                    Slice.EXTRA_TOGGLE_STATE, false),
//                    Toast.LENGTH_LONG).show()
//        }
//    }
//
//    companion object {
//        const val EXTRA_MESSAGE = "message"
//    }
//}
// [START slices_getting_started_MyBroadcastReceiver]
class MyBroadcastReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.hasExtra(Slice.EXTRA_TOGGLE_STATE)) {
            Toast.makeText(
                context, "Toggled:  " + intent.getBooleanExtra(
                    Slice.EXTRA_TOGGLE_STATE, false
                ),
                Toast.LENGTH_LONG
            ).show()
            receivedCount++;
            context.contentResolver.notifyChange(sliceUri, null)
        }
    }

    companion object {
        var receivedCount = 0
        val sliceUri = Uri.parse("content://com.android.example.slicesample/count")
        const val EXTRA_MESSAGE = "message"
    }
}
// [END slices_getting_started_MyBroadcastReceiver]