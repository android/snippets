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

// [START android_security_protected_broadcast_send]
fun sendProtectedBroadcast(context: Context) {
    val intent = Intent("com.example.permissions.ACTION_SECRET_UPDATE").apply {
        setPackage("com.example.partner")
    }
    context.sendBroadcast(intent, "com.example.permissions.RECEIVE_SECRET_UPDATE")
}
// [END android_security_protected_broadcast_send]

fun Context.sendBroadcastWithIdentity() {
    // [START android_security_broadcast_sender_identity]
    // Sender: Enforce permission and share identity
    val intent = Intent("com.example.permissions.ACTION_SECRET_UPDATE").apply {
        setPackage("com.example.partner") // Explicit target
    }
    val isUdc = Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE
    val options = if (isUdc) {
        BroadcastOptions.makeBasic().apply {
            setShareIdentityEnabled(true)
        }.toBundle()
    } else null

    sendBroadcast(intent, "com.example.permissions.RECEIVE_SECRET_UPDATE", options)
    // [END android_security_broadcast_sender_identity]
}

// [START android_security_broadcast_receiver_verify_identity]
// Receiver: Validate sender on Android 14+
class ProtectedReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == "com.example.permissions.ACTION_SECRET_UPDATE") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                val sender = sentFromPackage
                if (sender != null && sender != "com.example.trusted_sender") {
                    return // Reject unauthorized sender
                }
            }
            processUpdate(intent)
        }
    }

    private fun processUpdate(intent: Intent) {}
}
// [END android_security_broadcast_receiver_verify_identity]

typealias MyProtectedReceiver = ProtectedReceiver
