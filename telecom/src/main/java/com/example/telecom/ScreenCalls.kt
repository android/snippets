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

import android.net.Uri
import android.os.Build
import android.telecom.Call
import android.telecom.CallScreeningService
import android.telecom.Connection
import androidx.annotation.RequiresApi

@RequiresApi(Build.VERSION_CODES.R)
// [START android_telecom_screening_service]
class ScreeningService : CallScreeningService() {
    // This function is called when an ingoing or outgoing call
    // is from a number not in the user's contacts list
    override fun onScreenCall(callDetails: Call.Details) {
        // Can check the direction of the call
        val isIncoming = callDetails.callDirection == Call.Details.DIRECTION_INCOMING

        if (isIncoming) {
            // the handle (e.g. phone number) that the Call is currently connected to
            val handle: Uri = callDetails.handle

            // determine if you want to allow or reject the call
            when (callDetails.callerNumberVerificationStatus) {
                Connection.VERIFICATION_STATUS_FAILED -> {
                    // Network verification failed, likely an invalid/spam call.
                }
                Connection.VERIFICATION_STATUS_PASSED -> {
                    // Network verification passed, likely a valid call.
                }
                else -> {
                    // Network could not perform verification.
                    // This branch matches Connection.VERIFICATION_STATUS_NOT_VERIFIED.
                }
            }
        }
    }
}
// [END android_telecom_screening_service]

@RequiresApi(Build.VERSION_CODES.Q)
private class ScreeningResponseHelper : CallScreeningService() {
    override fun onScreenCall(callDetails: Call.Details) {
        // [START android_telecom_screening_response]
        // Tell the system how to respond to the incoming call
        // and if it should notify the user of the call.
        val response = CallResponse.Builder()
            // Sets whether the incoming call should be blocked.
            .setDisallowCall(false)
            // Sets whether the incoming call should be rejected as if the user did so manually.
            .setRejectCall(false)
            // Sets whether ringing should be silenced for the incoming call.
            .setSilenceCall(false)
            // Sets whether the incoming call should not be displayed in the call log.
            .setSkipCallLog(false)
            // Sets whether a missed call notification should not be shown for the incoming call.
            .setSkipNotification(false)
            .build()

        // Call this function to provide your screening response.
        respondToCall(callDetails, response)
        // [END android_telecom_screening_response]
    }
}
