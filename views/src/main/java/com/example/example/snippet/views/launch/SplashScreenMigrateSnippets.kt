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

package com.example.example.snippet.views.launch

import android.app.Activity
import android.os.Bundle
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.example.snippet.views.R

private object MigrateMainActivitySnippet {
    // [START android_views_splash_screen_migrate_main_activity]
    class MainActivity : Activity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            // Handle the splash screen transition.
            val splashScreen = installSplashScreen()

            super.onCreate(savedInstanceState)
            setContentView(R.layout.main_activity)
            // ...
            // [END android_views_splash_screen_migrate_main_activity]
        }
    }
}

private object MigrateRoutingActivitySnippet {
    // [START android_views_splash_screen_migrate_routing_activity]
    class RoutingActivity : Activity() {

        override fun onCreate(savedInstanceState: Bundle?) {
            val splashScreen = installSplashScreen()
            super.onCreate(savedInstanceState)

            // Keep the splash screen visible for this Activity.
            splashScreen.setKeepOnScreenCondition { true }
            startSomeNextActivity()
            finish()
        }
        // ...
        // [END android_views_splash_screen_migrate_routing_activity]
        private fun startSomeNextActivity() {}
    }
}
