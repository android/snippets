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

import android.app.role.RoleManager
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.telecom.CallRedirectionService
import android.telecom.PhoneAccountHandle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity

@RequiresApi(Build.VERSION_CODES.Q)
// [START android_telecom_redirection_service]
class RedirectionService : CallRedirectionService() {
    override fun onPlaceCall(
        handle: Uri,
        initialPhoneAccount: PhoneAccountHandle,
        allowInteractiveResponse: Boolean
    ) {
        // Determine if the call should proceed, be redirected, or cancelled.
        val callShouldProceed = true
        val callShouldRedirect = false
        when {
            callShouldProceed -> {
                placeCallUnmodified()
            }
            callShouldRedirect -> {
                // Update the URI to point to a different phone number or modify the
                // PhoneAccountHandle and redirect.
                redirectCall(handle, initialPhoneAccount, true)
            }
            else -> {
                cancelCall()
            }
        }
    }
}
// [END android_telecom_redirection_service]

// [START android_telecom_redirection_role_request]
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Tell the system that you want your app to handle call redirects. This
        // is done by using the RoleManager to register your app to handle redirects.
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q) {
            val roleManager = getSystemService(Context.ROLE_SERVICE) as RoleManager
            // Check if the app needs to register call redirection role.
            val shouldRequestRole = roleManager.isRoleAvailable(RoleManager.ROLE_CALL_REDIRECTION) &&
                !roleManager.isRoleHeld(RoleManager.ROLE_CALL_REDIRECTION)
            if (shouldRequestRole) {
                val intent = roleManager.createRequestRoleIntent(RoleManager.ROLE_CALL_REDIRECTION)
                startActivityForResult(intent, REDIRECT_ROLE_REQUEST_CODE)
            }
        }
    }

    companion object {
        private const val REDIRECT_ROLE_REQUEST_CODE = 1
    }
}
// [END android_telecom_redirection_role_request]
