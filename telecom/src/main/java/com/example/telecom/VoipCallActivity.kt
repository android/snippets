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

package com.example.telecom

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.core.telecom.CallAttributesCompat

class VoipCallActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Telecom VoIP snippets"
                    )
                }
            }
        }
    }

    private fun getCallAttributes(displayName: String, address: Uri, excludeCallLogging: Boolean, isIncoming: Boolean) =
        // [START android_telecom_call_attributes_call_log_exclusion]
        CallAttributesCompat(
            displayName = displayName,
            address = address,
            isLogExcluded = excludeCallLogging, // to exclude call from logging
            direction = if (isIncoming) {
                CallAttributesCompat.DIRECTION_INCOMING
            } else {
                CallAttributesCompat.DIRECTION_OUTGOING
            },
            callType = CallAttributesCompat.CALL_TYPE_AUDIO_CALL,
            callCapabilities = (
                CallAttributesCompat.SUPPORTS_SET_INACTIVE
                    or CallAttributesCompat.SUPPORTS_STREAM
                    or CallAttributesCompat.SUPPORTS_TRANSFER
                ),
        )
    // [END android_telecom_call_attributes_call_log_exclusion]

    @RequiresApi(37)
    private fun handleCallBack() {
        // [START android_telecom_call_back_intent_handling]
        // check the intent action for CALL_BACK
        if (intent.action == TelecomManager.ACTION_CALL_BACK) {
            launchCall(
                // fetching stored call details for the UUID to initiate callback
                callDetails = getCallDetails(
                    uuid = intent.getStringExtra(TelecomManager.EXTRA_UUID)
                )
            )
        }
        // [END android_telecom_call_back_intent_handling]
    }

    private fun getCallDetails(uuid: String?) { }

    private fun launchCall(callDetails: Any) { }

    @RequiresApi(Build.VERSION_CODES.CINNAMON_BUN)
    private fun launchIntegratedCallLogSettings() {
        // [START android_telecom_launch_integrated_call_log_settings]
        val intent = Intent(TelecomManager.ACTION_CONFIGURE_CALL_LOG_INTEGRATION)
        startActivity(intent)
        // [END android_telecom_launch_integrated_call_log_settings]
    }

    @RequiresApi(Build.VERSION_CODES.CINNAMON_BUN)
    // [START android_telecom_voip_call_log_preference_receiver]
    val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == TelecomManager.ACTION_VOIP_CALL_LOG_PREFERENCE) {
                // Status of the Unified Call History of the app
                val isEnabled = intent.getBooleanExtra(
                    TelecomManager.EXTRA_VOIP_CALL_LOG_PREFERENCE_STATUS,
                    true
                )
            }
        }
    }
    // [END android_telecom_voip_call_log_preference_receiver]
}
