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

package com.example.snippets.security.permissions

import android.content.Context
import android.content.pm.PackageManager
import android.os.Binder
import android.os.Build
import android.util.Log

// [START android_security_caller_verifier]
// [START android_security_caller_signature_verification]
object CallerVerifier {
    private const val TRUSTED_PARTNER_SHA256 =
        "A1B2C3D4E5F60708090A0B0C0D0E0F1011121314151617181920212223242526"

    fun isCallerAuthorized(context: Context): Boolean {
        val callingUid = Binder.getCallingUid()
        if (callingUid == android.os.Process.myUid()) return true

        val pm = context.packageManager
        // Modern API 28+ check by UID:
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            val certBytes = hexStringToByteArray(TRUSTED_PARTNER_SHA256)
            if (pm.hasSigningCertificate(callingUid, certBytes, PackageManager.CERT_INPUT_SHA256)) {
                return true
            }
        }

        // Fallback for legacy APIs:
        val callingPackages = pm.getPackagesForUid(callingUid) ?: return false
        for (pkg in callingPackages) {
            if (verifyPackageSignature(pm, pkg)) {
                return true
            }
        }
        return false
    }

    @Suppress("DEPRECATION")
    private fun verifyPackageSignature(pm: PackageManager, packageName: String): Boolean {
        return try {
            val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
            val signatures = packageInfo.signatures ?: return false
            for (sig in signatures) {
                val digest = java.security.MessageDigest.getInstance("SHA-256").digest(sig.toByteArray())
                val hex = digest.joinToString("") { "%02X".format(it) }
                if (hex.equals(TRUSTED_PARTNER_SHA256, ignoreCase = true)) return true
            }
            false
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun hexStringToByteArray(s: String): ByteArray {
        val len = s.length
        val data = ByteArray(len / 2)
        for (i in 0 until len step 2) {
            data[i / 2] = ((Character.digit(s[i], 16) shl 4) + Character.digit(s[i + 1], 16)).toByte()
        }
        return data
    }

    private fun processSensitiveData(payload: String?) {
        Log.d("SecureDataService", "Processing data safely: $payload")
    }
}
// [END android_security_caller_signature_verification]
// [END android_security_caller_verifier]

// Placeholder helper for compilation of vulnerable anti-pattern snippet
object SignatureUtils {
    fun verifyPartnerPackage(context: Context, packageName: String): Boolean = true
}

fun Context.vulnerableCallerCheck(intent: android.content.Intent?) {
    // [START android_security_caller_spoofing_vulnerable]
    // VULNERABLE PATTERN: DO NOT DO THIS
    val callingPackage = intent?.getStringExtra("calling_package")
    if (callingPackage != null && SignatureUtils.verifyPartnerPackage(this, callingPackage)) {
        // A malicious app passes "com.example.partner" in the extra.
        // The signature check verifies the installed partner on disk, but the CALLER was malicious!
    }
    // [END android_security_caller_spoofing_vulnerable]
}
