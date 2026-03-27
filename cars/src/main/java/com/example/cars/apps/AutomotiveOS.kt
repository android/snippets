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

package com.example.cars.apps

import android.content.Intent
import androidx.car.app.Screen
import androidx.car.app.Session
import com.example.cars.apps.library.MyStartScreen

// [START android_cars_apps_automotive_os_handle_intents]
class MySession : Session() {
    // ...
    override fun onCreateScreen(intent: Intent): Screen {
        // Handle the intent when the app is being started for the first time
        return MyStartScreen(carContext)
    }

    override fun onNewIntent(intent: Intent) {
        // Handle the intent when the app is already running
    }
}
// [END android_cars_apps_automotive_os_handle_intents]
