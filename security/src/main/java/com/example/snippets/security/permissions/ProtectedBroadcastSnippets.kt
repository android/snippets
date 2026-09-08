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

import android.app.BroadcastOptions
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi

// [START android_security_protected_broadcast_send]
fun sendProtectedBroadcast(context: Context) {
    val intent = Intent("com.example.snippets.ACTION_SECRET_UPDATE")
    // Enforce permission requirements during broadcast dispatch
    context.sendBroadcast(intent, "com.example.snippets.permission.ACCESS_SECURE_API")
}
// [END android_security_protected_broadcast_send]

// [START android_security_broadcast_sender_identity]
@Suppress("ObsoleteSdkInt")
@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
fun sendBroadcastWithIdentity(context: Context) {
    val intent = Intent("com.example.snippets.ACTION_SECRET_UPDATE")

    // Opt-in to sharing sender identity
    val options = BroadcastOptions.makeBasic().apply {
        setShareIdentityEnabled(true)
    }

    context.sendBroadcast(
        intent,
        "com.example.snippets.permission.ACCESS_SECURE_API",
        options.toBundle()
    )
}
// [END android_security_broadcast_sender_identity]

// [START android_security_broadcast_receiver_verify_identity]
class MyProtectedReceiver : BroadcastReceiver() {
    @Suppress("ObsoleteSdkInt")
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.example.snippets.ACTION_SECRET_UPDATE") {
            // Retrieve the sender's package name on Android 14+
            val senderPackage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                sentFromPackage
            } else {
                null
            }

            if (senderPackage != null) {
                try {
                    // Verify the sender's identity using AppAuthenticator
                    CallerVerifier(context).enforceCaller(senderPackage)
                    processUpdate(intent)
                } catch (e: SecurityException) {
                    Log.w("SECURITY_ALERT", "Untrusted broadcast sender: $senderPackage", e)
                }
            } else {
                Log.w("SECURITY_ALERT", "Broadcast received without sender identity")
            }
        }
    }

    private fun processUpdate(intent: Intent) {}
}
// [END android_security_broadcast_receiver_verify_identity]
