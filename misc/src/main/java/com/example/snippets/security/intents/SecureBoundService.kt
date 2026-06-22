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
import android.content.pm.Signature
import android.os.Binder
import android.os.IBinder
import android.util.Base64
import android.util.Log
import java.security.MessageDigest

// [START android_security_service_caller_verify]
class SecureBoundService : Service() {
    companion object {
        // Expected SHA-256 hash of the trusted app's signing certificate (Base64 encoded)
        private const val TRUSTED_PARTNER_SHA256 = "A1B2C3D4E5F6G7H8I9J0K1L2M3N4O5P6Q7R8S9T0U1V="
    }

    override fun onBind(intent: Intent): IBinder {
        val callingUid = Binder.getCallingUid()
        val pm = packageManager
        val packages = pm.getPackagesForUid(callingUid)
        
        if (!packages.isNullOrEmpty()) {
            val callingPackage = packages[0]
            if (verifySignature(pm, callingPackage)) {
                return LocalBinder()
            }
        }
        throw SecurityException("Binding denied: Caller identity unverified.")
    }

    private fun verifySignature(pm: PackageManager, packageName: String): Boolean {
        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNING_CERTIFICATES)
                val signingInfo = packageInfo.signingInfo
                if (signingInfo != null) {
                    val signatures = signingInfo.apkContentsSigners
                    signatures?.forEach { signature ->
                        if (verifyHash(signature)) return true
                    }
                }
            } else {
                @Suppress("DEPRECATION")
                val packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_SIGNATURES)
                val signatures = packageInfo.signatures
                signatures?.forEach { signature ->
                    if (verifyHash(signature)) return true
                }
            }
        } catch (e: Exception) {
            Log.e("SECURITY_ERROR", "Verification failed for package: $packageName", e)
        }
        return false
    }

    private fun verifyHash(signature: Signature): Boolean {
        val md = MessageDigest.getInstance("SHA-256")
        md.update(signature.toByteArray())
        val currentSignature = Base64.encodeToString(md.digest(), Base64.NO_WRAP).trim()
        return TRUSTED_PARTNER_SHA256 == currentSignature
    }

    private inner class LocalBinder : Binder()
}
// [END android_security_service_caller_verify]
