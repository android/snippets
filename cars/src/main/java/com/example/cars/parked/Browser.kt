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
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.getSystemService
import com.example.cars.R

fun isDeviceCredentialSet(context: Context) {
    // [START android_cars_parked_browser_device_credential]
    val keyguardManager = context.getSystemService<KeyguardManager>()
    val isDeviceSecure = keyguardManager?.isDeviceSecure == true
    // [END android_cars_parked_browser_device_credential]
}

fun openSecurityScreen(context: Context) {
    // [START android_cars_parked_browser_security_settings]
    context.startActivity(
        Intent(Settings.ACTION_SECURITY_SETTINGS)
            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    )
    // [END android_cars_parked_browser_security_settings]
}

fun fullFlow(context: Context) {
    // [START android_cars_parked_browser_protect_sensitive_data]
    val keyguardManager = context.getSystemService<KeyguardManager>()
    val isDeviceSecure = keyguardManager?.isDeviceSecure == true
    lateinit var biometricPrompt: BiometricPrompt

    if (!isDeviceSecure) {
        context.startActivity(
            Intent(Settings.ACTION_SECURITY_SETTINGS).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        )
    } else {
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.auth_title))
            .setSubtitle(context.getString(R.string.sync_data_to_car_notice))
            .setAllowedAuthenticators(BiometricManager.Authenticators.DEVICE_CREDENTIAL)
            .build()
        biometricPrompt.authenticate(promptInfo)
    }
    // [END android_cars_parked_browser_protect_sensitive_data]
}
