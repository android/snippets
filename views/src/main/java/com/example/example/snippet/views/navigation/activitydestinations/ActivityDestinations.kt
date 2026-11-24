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

package com.example.example.snippet.views.navigation.activitydestinations

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.navigation.NavController
import com.example.example.snippet.views.R

private class SourceActivity : ComponentActivity() {

    fun navigateToDestinationActivity(context: Context) {
        // [START android_views_navigation_activity_destinations_start_activity]
        startActivity(Intent(context, DestinationActivity::class.java))
        // [END android_views_navigation_activity_destinations_start_activity]
    }
}

private fun navigateWithBundle(navController: NavController) {
    // [START android_views_navigation_activity_destinations_navigate_bundle]
    navController.navigate(
        R.id.localDestinationActivity,
        Bundle().apply { putString("userId", "someUser") }
    )
    // [END android_views_navigation_activity_destinations_navigate_bundle]
}

class DestinationActivity : ComponentActivity()
