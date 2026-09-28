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

package com.example.example.snippet.views.notifications.snackbar

import android.view.View
import androidx.activity.ComponentActivity
import com.example.example.snippet.views.R
import com.google.android.material.snackbar.Snackbar

// [START android_views_snackbar_action_listener]
class MyUndoListener : View.OnClickListener {

    override fun onClick(v: View) {
        // Code to undo the user's last action.
    }
}
// [END android_views_snackbar_action_listener]

private class ActionActivity : ComponentActivity() {

    fun setupActionSnackbar() {
        // [START android_views_snackbar_set_action]
        val mySnackbar = Snackbar.make(
            findViewById(R.id.myCoordinatorLayout),
            R.string.email_archived,
            Snackbar.LENGTH_SHORT
        )
        mySnackbar.setAction(R.string.undo_string, MyUndoListener())
        mySnackbar.show()
        // [END android_views_snackbar_set_action]
    }
}
