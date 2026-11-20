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

package com.example.snippets.security.intents

import android.content.Intent
import android.os.Build
import android.os.StrictMode
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.CINNAMON_BUN)
fun enableImplicitUriPermissionGrantDetection() {
    // [START android_security_intents_detect_implicit_uri_permission_grant]
    val policy = StrictMode.VmPolicy.Builder()
        .detectImplicitUriPermissionGrant()
        .penaltyLog()
        .build()
    StrictMode.setVmPolicy(policy)
    // [END android_security_intents_detect_implicit_uri_permission_grant]
}

fun grantReadUriPermission(intent: Intent) {
    // [START android_security_intents_grant_read_uri_permission]
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    // [END android_security_intents_grant_read_uri_permission]
}

fun grantReadWriteUriPermission(intent: Intent) {
    // [START android_security_intents_grant_read_write_uri_permission]
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
    // [END android_security_intents_grant_read_write_uri_permission]
}
