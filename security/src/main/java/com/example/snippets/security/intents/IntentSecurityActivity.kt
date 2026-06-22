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

import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.core.content.IntentCompat
import androidx.core.content.IntentSanitizer

// Placeholder classes for compilation
class TargetActivity : ComponentActivity()
class ReplyReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {}
}
class InternalSharingActivity : ComponentActivity()

class IntentSecurityActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    // [START android_security_intent_redirect_manual]
    fun safeIntentRedirectionManual() {
        val nestedIntent = IntentCompat.getParcelableExtra(intent, "EXTRA_NESTED_INTENT", Intent::class.java)
        if (nestedIntent != null) {
            val pm = packageManager
            val target = nestedIntent.resolveActivity(pm)
            if (target != null) {
                // 1. Verify target is within the same package
                if (target.packageName != packageName) {
                    throw SecurityException("Cross-app intent redirection is forbidden!")
                }
                try {
                    // 2. Verify target activity is exported
                    val info = pm.getActivityInfo(target, 0)
                    if (!info.exported) {
                        throw SecurityException("Target activity is private: ${target.className}")
                    }
                    // 3. Explicitly set the component to prevent intent interception
                    nestedIntent.component = target
                    // Safe to launch
                    startActivity(nestedIntent)
                } catch (e: PackageManager.NameNotFoundException) {
                    Log.e("Security", "Failed to resolve target activity", e)
                }
            }
        }
    }
    // [END android_security_intent_redirect_manual]

    // [START android_security_intent_redirect_sanitizer]
    fun safeIntentRedirectionSanitizer() {
        val untrustedIntent = IntentCompat.getParcelableExtra(intent, "EXTRA_NESTED_INTENT", Intent::class.java)
        if (untrustedIntent != null) {
            // Define the strict boundaries for allowed redirection target
            val sanitizer = IntentSanitizer.Builder()
                .allowComponent(ComponentName("com.example.app", "com.example.app.SafeTargetActivity")) // Explicitly allowed target
                .allowAction(Intent.ACTION_VIEW) // Explicitly allowed actions
                .allowDataWithAuthority("com.example.app.provider") // Allowed URI authority
                .allowType("text/plain") // Allowed mime type
                .allowExtra("user_display_name", String::class.java) // Safe type-enforced extras
                .build()

            try {
                // Option A: Throws SecurityException if the intent violates policies
                val safeIntent = sanitizer.sanitizeByThrowing(untrustedIntent)
                startActivity(safeIntent)
            } catch (e: SecurityException) {
                Log.e("SECURITY_ALERT", "Attempted launch of non-allowlisted intent blocked", e)
            }
            
            // Option B: Silently filter and launch only the authorized parts (no exception thrown)
            // val filteredIntent = sanitizer.sanitizeByFiltering(untrustedIntent)
            // startActivity(filteredIntent)
        }
    }
    // [END android_security_intent_redirect_sanitizer]

    // [START android_security_onnewintent_validate]
    override fun onNewIntent(newIntent: Intent) {
        super.onNewIntent(newIntent)
        
        // Set the intent to ensure intent returns the new one
        intent = newIntent
        
        // Validate the intent payload
        if (validateIntent(newIntent)) {
            processIntentPayload(newIntent)
        } else {
            Log.w("SECURITY_ALERT", "Received invalid or insecure intent during warm boot")
        }
    }

    private fun validateIntent(intent: Intent): Boolean {
        return intent.hasExtra("VALID_PAYLOAD_MARKER")
    }
    // [END android_security_onnewintent_validate]

    private fun processIntentPayload(intent: Intent) {
        // Placeholder implementation
    }

    // [START android_security_pendingintent_secure]
    fun createPendingIntents(context: Context) {
        // 1. Secure Immutable PendingIntent (Default)
        val intent = Intent(context, TargetActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        // 2. Secure Mutable PendingIntent (e.g., Notification Direct Reply)
        val mutableIntent = Intent().apply {
            // MUST set explicit target component to prevent redirection hijacking
            component = ComponentName(context, ReplyReceiver::class.java)
        }
        val mutablePendingIntent = PendingIntent.getBroadcast(
            context,
            0,
            mutableIntent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }
    // [END android_security_pendingintent_secure]

    // [START android_security_error_handling_secure]
    fun safeErrorHandling(callingPackage: String?) {
        try {
            val payload = intent.getStringExtra("DATA_EXTRA") ?: throw IllegalArgumentException("Payload parameter missing.")
            // Create a specific target intent using the validated payload
            val targetIntent = Intent(this, TargetActivity::class.java).apply {
                putExtra("SECURE_PAYLOAD", payload)
            }
            startActivity(targetIntent)
        } catch (e: SecurityException) {
            // MUST log security violations for audit, but NEVER expose exception details to the user.
            Log.e("SECURITY_ERROR", "Unauthorized component transition blocked. Calling Package: ${callingPackage ?: "Unknown"}", e)
            // MUST provide generic user feedback.
            showFeedbackToUser("Process request failed: Access Denied.")
        } catch (e: IllegalArgumentException) {
            Log.w("INTEGRITY_WARNING", "Missing intent parameter", e)
        }
    }
    // [END android_security_error_handling_secure]

    private fun showFeedbackToUser(message: String) {
        // Placeholder implementation
    }
}
