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

import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Binder
import android.os.IBinder
import android.os.Process
import android.util.Base64
import android.util.Log

// [START android_security_service_caller_verify]
class SecureBoundService : Service() {
    companion object {
        // Expected SHA-256 hash of the trusted app's signing certificate (Base64 encoded)
        private const val TRUSTED_PARTNER_SHA256 = "A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V="
    }

    // Use an AIDL Stub for cross-process IPC
    private val binder = object : IMyService.Stub() {
        override fun doSecureWork() {
            // Verify caller identity inside the active Binder IPC transaction
            enforceTrustedCaller()
            // Safe to proceed with sensitive operations
        }
    }

    override fun onBind(intent: Intent): IBinder {
        // Return the AIDL Stub. Do NOT perform Binder.getCallingUid() checks in onBind()
        // because onBind() is invoked asynchronously by system_server and the IBinder is cached.
        return binder
    }

    private fun enforceTrustedCaller() {
        val callingUid = Binder.getCallingUid()
        if (callingUid == Process.myUid() || !verifyUidSignature(callingUid)) {
            throw SecurityException("Access Denied: Unauthorized or local/cleared-identity caller.")
        }
    }

    private fun verifyUidSignature(callingUid: Int): Boolean {
        return try {
            val trustedSha256Raw = Base64.decode(TRUSTED_PARTNER_SHA256, Base64.DEFAULT)
            // PackageManager.hasSigningCertificate(uid, ...) (API 28+) checks the UID directly
            // and handles certificate rotation.
            packageManager.hasSigningCertificate(
                callingUid,
                trustedSha256Raw,
                PackageManager.CERT_INPUT_SHA256
            )
        } catch (e: IllegalArgumentException) {
            Log.e("SECURITY_ERROR", "Invalid certificate hash format", e)
            false
        }
    }
}
// [END android_security_service_caller_verify]

interface IMyService {
    fun doSecureWork()
    abstract class Stub : Binder(), IMyService
}
