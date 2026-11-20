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

package com.example.identity.credentialmanager

import android.app.Activity
import android.app.PendingIntent
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import com.google.android.gms.auth.api.identity.GetPhoneNumberHintIntentRequest
import com.google.android.gms.auth.api.identity.Identity

private object PhoneNumberHintExamples {
    private const val TAG = "PhoneNumberHint"

    fun createPhoneNumberHintRequest() {
        // [START android_identity_phone_number_hint_create_request]
        val request: GetPhoneNumberHintIntentRequest = GetPhoneNumberHintIntentRequest.builder().build()
        // [END android_identity_phone_number_hint_create_request]
    }

    fun requestPhoneNumberHint(
        activity: Activity,
        request: GetPhoneNumberHintIntentRequest,
        launcher: ActivityResultLauncher<IntentSenderRequest>,
    ) {
        // [START android_identity_phone_number_hint_request_intent]
        // [START_EXCLUDE silent]
        /*
        // [END_EXCLUDE]
        val phoneNumberHintIntentResultLauncher = ...
        // [START_EXCLUDE silent]
         */
        val phoneNumberHintIntentResultLauncher = launcher
        // [END_EXCLUDE]

        Identity.getSignInClient(activity)
            .getPhoneNumberHintIntent(request)
            .addOnSuccessListener { result: PendingIntent ->
                try {
                    phoneNumberHintIntentResultLauncher.launch(
                        IntentSenderRequest.Builder(result).build()
                    )
                } catch (e: Exception) {
                    Log.e(TAG, "Launching the PendingIntent failed")
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "Phone Number Hint failed")
            }
        // [END android_identity_phone_number_hint_request_intent]
    }
}

private class PhoneNumberHintActivity : ComponentActivity() {
    private val activity: Activity = this

    // [START android_identity_phone_number_hint_retrieve_number]
    val phoneNumberHintIntentResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            try {
                val phoneNumber = Identity.getSignInClient(activity).getPhoneNumberFromIntent(result.data)
            } catch (e: Exception) {
                Log.e(TAG, "Phone Number Hint failed")
            }
        }
    // [END android_identity_phone_number_hint_retrieve_number]

    companion object {
        private const val TAG = "PhoneNumberHint"
    }
}
