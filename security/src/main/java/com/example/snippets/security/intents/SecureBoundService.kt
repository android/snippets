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

    override fun onBind(intent: Intent): IBinder {
        // Return the binder. Do NOT perform signature verification in onBind() because
        // the binder connection is cached by Android, which can bypass checks on subsequent binds.
        return LocalBinder()
    }

    private fun enforceTrustedCaller() {
        val callingUid = Binder.getCallingUid()
        // Allow calls from the same application
        if (callingUid == Process.myUid()) {
            return
        }
        val pm = packageManager
        val packages = pm.getPackagesForUid(callingUid)
        
        if (packages.isNullOrEmpty() || !verifySignature(pm, packages[0])) {
            throw SecurityException("Access Denied: Caller signature is untrusted.")
        }
    }

    private fun verifySignature(pm: PackageManager, packageName: String): Boolean {
        try {
            val trustedSha256Raw = Base64.decode(TRUSTED_PARTNER_SHA256, Base64.DEFAULT)
            // API 28+ handles rotated certificates and avoids manual hashing.
            // Since minSdk is 36, this is always available.
            return pm.hasSigningCertificate(packageName, trustedSha256Raw, PackageManager.CERT_INPUT_SHA256)
        } catch (e: Exception) {
            Log.e("SECURITY_ERROR", "Verification failed for package: $packageName", e)
        }
        return false
    }

    inner class LocalBinder : Binder() {
        fun doSecureWork() {
            // Verify caller identity on every transaction method call
            enforceTrustedCaller()
            // Safe to proceed with sensitive operations
        }
    }
}
// [END android_security_service_caller_verify]
