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

package com.example.cars.parked

import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import androidx.core.content.getSystemService

fun isDeviceCredentialSet(context: Context): Boolean {
    // [START android_cars_parked_browser_device_credential]
    val keyguardManager = context.getSystemService<KeyguardManager>()
    val isDeviceSecure = keyguardManager?.isDeviceSecure == true
    // [END android_cars_parked_browser_device_credential]
    return isDeviceSecure
}

fun openSecurityScreen(context: Context) {
    // [START android_cars_parked_browser_security_settings]
    context.startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
    // [END android_cars_parked_browser_security_settings]
}
