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

import android.app.Notification
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.telecom.DisconnectCause
import androidx.annotation.RequiresApi
import androidx.core.telecom.CallAttributesCompat
import androidx.core.telecom.CallControlResult
import androidx.core.telecom.CallControlScope
import androidx.core.telecom.CallEndpointCompat
import androidx.core.telecom.CallsManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.O)
class TelecomSnippets(private val context: Context, private val scope: CoroutineScope) {

    fun registerApp() {
        // [START android_telecom_register_app]
        val callsManager = CallsManager(context)

        val capabilities: @CallsManager.Companion.Capability Int =
            (CallsManager.CAPABILITY_BASELINE or
                  CallsManager.CAPABILITY_SUPPORTS_VIDEO_CALLING)

        callsManager.registerAppWithTelecom(capabilities)
        // [END android_telecom_register_app]
    }

    // [START android_telecom_create_incoming_call_attributes]
    fun createIncomingCallAttributes(
        callerName: String,
        callerNumber: String,
        isVideoCall: Boolean): CallAttributesCompat {
        val addressUri = Uri.parse("YourAppScheme:$callerNumber")

        return CallAttributesCompat(
            displayName = callerName,
            address = addressUri,
            direction = CallAttributesCompat.DIRECTION_INCOMING,
            callType = if (isVideoCall) {
                CallAttributesCompat.CALL_TYPE_VIDEO_CALL
            } else {
                CallAttributesCompat.CALL_TYPE_AUDIO_CALL
            },
            callCapabilities = CallAttributesCompat.SUPPORTS_SET_INACTIVE
        )
    }
    // [END android_telecom_create_incoming_call_attributes]

    suspend fun addCallSnippet(callsManager: CallsManager) {
        val INCOMING_CALL_ATTRIBUTES = createIncomingCallAttributes("Caller", "123456", false)
        val onAnswerCall: suspend (Int) -> Unit = {}
        val onSetCallDisconnected: suspend (DisconnectCause) -> Unit = {}
        val onSetCallActive: suspend () -> Unit = {}
        val onSetCallInactive: suspend () -> Unit = {}
        var callControlScope: CallControlScope? = null

        // [START android_telecom_add_call]
        try {
            callsManager.addCall(
                INCOMING_CALL_ATTRIBUTES,
                onAnswerCall, // Watch needs to know if it can answer the call.
                onSetCallDisconnected,
                onSetCallActive,
                onSetCallInactive
            ) {
                // The call was successfully added once this scope runs.
                callControlScope = this
            }
        }
        catch(addCallException: Exception){
           // Handle the addCall failure.
        }
        // [END android_telecom_add_call]
    }

    suspend fun CallControlScope.callControlActions() {
        // [START android_telecom_answer_call]
        when (val result = answer(CallAttributesCompat.CALL_TYPE_AUDIO_CALL)) {
            is CallControlResult.Success -> { /* Call answered */ }
            is CallControlResult.Error -> { /* Handle error */ }
        }
        // [END android_telecom_answer_call]

        // [START android_telecom_reject_call]
        disconnect(DisconnectCause(DisconnectCause.REJECTED))
        // [END android_telecom_reject_call]

        // [START android_telecom_set_active]
        when (val result = setActive()) {
            is CallControlResult.Success -> { /* Call active */ }
            is CallControlResult.Error -> { /* Handle error */ }
        }
        // [END android_telecom_set_active]

        // [START android_telecom_set_inactive]
        when (val result = setInactive()) {
            is CallControlResult.Success -> { /* Call on hold */ }
            is CallControlResult.Error -> { /* Handle error */ }
        }
        // [END android_telecom_set_inactive]

        // [START android_telecom_disconnect_call]
        disconnect(DisconnectCause(DisconnectCause.LOCAL))
        // [END android_telecom_disconnect_call]
    }

    // [START android_telecom_observe_audio_state_changes]
    fun observeAudioStateChanges(callControlScope: CallControlScope) {
        with(callControlScope) {
            launch { currentCallEndpoint.collect { /* Update UI */ } }
            launch { availableEndpoints.collect { /* Update UI */ } }
            launch { isMuted.collect { /* Handle mute state */ } }
        }
    }
    // [END android_telecom_observe_audio_state_changes]

    fun requestEndpointChangeSnippet(
        coroutineScope: CoroutineScope,
        callControlScope: CallControlScope,
        callEndpoint: CallEndpointCompat
    ) {
        // [START android_telecom_request_endpoint_change]
        coroutineScope.launch {
             callControlScope.requestEndpointChange(callEndpoint)
        }
        // [END android_telecom_request_endpoint_change]
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun promoteToForeground(service: TelecomVoipService, notificationId: Int, notification: Notification) {
        with(service) {
            // [START android_telecom_start_foreground]
            startForeground(
                notificationId,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_PHONE_CALL
            )
            // [END android_telecom_start_foreground]
        }
    }

    suspend fun addCallWithExtensionsSnippet(
        mCallsManager: CallsManager,
        attributes: CallAttributesCompat,
        onAnswer: suspend (Int) -> Unit,
        onDisconnect: suspend (DisconnectCause) -> Unit,
        onSetActive: suspend () -> Unit,
        onSetInactive: suspend () -> Unit,
        callStateFlow: Flow<Int>
    ) {
        // [START android_telecom_add_call_with_extensions]
        scope.launch {
            mCallsManager.addCallWithExtensions(
                attributes,
                onAnswer,
                onDisconnect,
                onSetActive,
                onSetInactive
            ) {
                // Initialize extension-specific code...

                // After the call has been initialized, perform in-call actions
                onCall {
                    // Example: process call state updates
                    callStateFlow.onEach { newState ->
                        // handle call state updates and notify telecom
                    }.launchIn(this)

                    // Use initialized extensions...
                }
            }
        }
        // [END android_telecom_add_call_with_extensions]
    }
}

class TelecomVoipService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null
}
