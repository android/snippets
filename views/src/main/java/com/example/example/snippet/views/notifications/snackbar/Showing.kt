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
import androidx.annotation.StringRes
import com.example.example.snippet.views.R
import com.google.android.material.snackbar.Snackbar

private class ShowingActivity : ComponentActivity() {

    fun showSnackbar(view: View, @StringRes stringId: Int, duration: Int) {
        // [START android_views_snackbar_make]
        val mySnackbar = Snackbar.make(view, stringId, duration)
        // [END android_views_snackbar_make]

        // [START android_views_snackbar_show]
        mySnackbar.show()
        // [END android_views_snackbar_show]
    }

    fun showChainedSnackbar() {
        // [START android_views_snackbar_make_show]
        Snackbar.make(
            findViewById(R.id.myCoordinatorLayout),
            R.string.email_sent,
            Snackbar.LENGTH_SHORT
        ).show()
        // [END android_views_snackbar_make_show]
    }
}
