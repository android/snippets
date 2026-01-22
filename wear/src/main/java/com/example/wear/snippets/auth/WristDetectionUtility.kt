/*
 * Copyright 2025 The Android Open Source Project
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

import android.app.KeyguardManager
import android.content.Context
import androidx.core.content.getSystemService

// This setting differs across OEMs, so you may want to check several in your detection function
// This one works for the pixel watch.
private const val PIXEL_WRIST_AUTOLOCK_SETTING_STATE = "wrist_detection_auto_locking_enabled"

// [START android_wear_auth_wrist_detection]
fun isWristDetectionAutoLockingEnabled(context: Context): Boolean {
    // [END android_wear_auth_wrist_detection]
    // Use the keyguard manager to check for the presence of a lock mechanism
    val keyguardManager = context.getSystemService<KeyguardManager>()
    val isSecured = keyguardManager?.isDeviceSecure == true

    // Use OEM-specific system settings to verify that on-body autolock is enabled.
    val isWristDetectionOn = android.provider.Settings.Global.getInt(
        context.contentResolver, PIXEL_WRIST_AUTOLOCK_SETTING_STATE,
        0
    ) == 1

    return isSecured && isWristDetectionOn
}
