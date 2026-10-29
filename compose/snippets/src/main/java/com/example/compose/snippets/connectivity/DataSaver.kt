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

package com.example.compose.snippets.connectivity

import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_DISABLED
import android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_ENABLED
import android.net.ConnectivityManager.RESTRICT_BACKGROUND_STATUS_WHITELISTED
import androidx.activity.ComponentActivity

private class DataSaver : ComponentActivity() {
    private fun checkDataSaverPreferences() {
        // [START android_connectivity_data_saver_check_preferences]
        (getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager).apply {
            // Checks if the device is on a metered network.
            if (isActiveNetworkMetered) {
                // Checks user’s Data Saver settings.
                when (restrictBackgroundStatus) {
                    RESTRICT_BACKGROUND_STATUS_ENABLED -> {
                        // Background data usage is blocked for this app. Wherever possible,
                        // the app should also use less data in the foreground.
                    }
                    RESTRICT_BACKGROUND_STATUS_WHITELISTED -> {
                        // The app is allowed to bypass Data Saver. Nevertheless, wherever possible,
                        // the app should use less data in the foreground and background.
                    }
                    RESTRICT_BACKGROUND_STATUS_DISABLED -> {
                        // Data Saver is disabled. Since the device is connected to a
                        // metered network, the app should use less data wherever possible.
                    }
                }
            } else {
                // The device is not on a metered network.
                // Use data as required to perform syncs, downloads, and updates.
            }
        }
        // [END android_connectivity_data_saver_check_preferences]
    }
}
