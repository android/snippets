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
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.os.BadParcelableException
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
            // 1. Check for URI permission grants to prevent URI permission bypass
            val hasUriPermissionGrants = (
                nestedIntent.flags and (
                    Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                        Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or
                        Intent.FLAG_GRANT_PREFIX_URI_PERMISSION
                    )
                ) != 0
            if (hasUriPermissionGrants) {
                throw SecurityException("Nested intent contains forbidden URI permission grant flags!")
            }

            // 2. Clear any selector intent that could override resolution
            nestedIntent.selector = null

            // 3. Resolve the target activity and verify both packageName and className
            //    match the expected allowlisted destination
            val target = nestedIntent.resolveActivity(packageManager)
            if (target != null &&
                target.packageName == packageName &&
                target.className == TargetActivity::class.java.name
            ) {
                // 4. Explicitly pin the resolved component to prevent intent interception
                nestedIntent.component = target
                startActivity(nestedIntent)
            } else {
                throw SecurityException("Invalid or disallowed redirect target: $target")
            }
        }
    }
    // [END android_security_intent_redirect_manual]

    // [START android_security_intent_redirect_sanitizer]
    fun safeIntentRedirectionSanitizer() {
        // Allowlist: anything NOT listed is rejected (only component is required; other fields are optional).
        // No allowFlags(): every flag, including FLAG_GRANT_*_URI_PERMISSION, is rejected.
        val sanitizer = IntentSanitizer.Builder()
            .allowComponent(ComponentName(this, TargetActivity::class.java))
            .allowAction(Intent.ACTION_VIEW)
            .allowData { it.scheme == ContentResolver.SCHEME_CONTENT && it.authority == "$packageName.provider" }
            .allowType("text/plain")
            .allowExtra("user_display_name", String::class.java) { it.length <= 100 } // type and content
            .build()

        val safeIntent = try {
            val untrusted = IntentCompat.getParcelableExtra(intent, "EXTRA_NESTED_INTENT", Intent::class.java)
                ?: return
            sanitizer.sanitizeByThrowing(untrusted)
        } catch (_: SecurityException) {
            Log.w("SECURITY_ALERT", "Blocked non-allowlisted nested intent")
            return
        } catch (_: BadParcelableException) { // e.g. an extra naming an unknown Parcelable/Serializable
            Log.w("SECURITY_ALERT", "Malformed nested intent")
            return
        }

        // The sanitizer doesn't enforce presence; check what the target actually requires.
        if (safeIntent.getStringExtra("user_display_name") == null) {
            Log.w("SECURITY_ALERT", "Nested intent missing user_display_name")
            return
        }
        startActivity(safeIntent)
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
