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

package com.example.voipapp

import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.TelecomManager
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.telecom.CallAttributesCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.voipapp.ui.main.MainFragment

class VoipCallbackActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main, MainFragment.newInstance())
                .commitNow()
        }
    }

    private fun getCallAttributes(displayName: String, address: Uri, excludeCallLogging: Boolean, isIncoming: Boolean) =
        // START android_telecom_call_attributes_call_log_exclusion
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
    // END android_telecom_call_attributes_call_log_exclusion

    @RequiresApi(Build.VERSION_CODES_FULL.BAKLAVA_1)
    private fun handleCallBack() {
        // START android_telecom_call_back_intent_handling
        // check the intent action for CALL_BACK
        if (intent.action == TelecomManager.ACTION_CALL_BACK) {
            launchCall(
                // fetching stored call details for the UUID to initiate callback
                callDetails = getCallDetails(
                    intent.getStringExtra(TelecomManager.EXTRA_UUID)
                )
            )
        }
        // END android_telecom_call_back_intent_handling
    }

    private fun getCallDetails(uuid: String?) { }

    private fun launchCall(callDetails: Any) { }
}
