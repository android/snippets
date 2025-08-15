/*
 * Copyright 2025 The Android Open Source Project
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

import android.os.Build.VERSION_CODES
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.annotation.RequiresApi
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.provider.BeginCreatePublicKeyCredentialRequest
import androidx.credentials.provider.BeginGetPublicKeyCredentialOption
import androidx.credentials.provider.BiometricPromptData
import androidx.credentials.provider.CallingAppInfo
import androidx.credentials.provider.PendingIntentHandler

class SingleTap : ComponentActivity() {
    private val x: Any? = null
    private val TAG: String = ""

    private fun passkeyCreation(
        request: BeginCreatePublicKeyCredentialRequest,
        passwordCount: Int,
        passkeyCount: Int
    ) {
        val option = null
        val origin = null
        val responseBuilder = null
        val autoSelectEnabled = null
        val allowedAuthenticator = 0

        val y =
            // [START android_identity_single_tap_set_biometric_prompt_data]
            PublicKeyCredentialEntry(
                // other properties...

                biometricPromptData = BiometricPromptData(
                    allowedAuthenticators = allowedAuthenticator
                )
            )
        // [END android_identity_single_tap_set_biometric_prompt_data]

        when (x) {
            // [START android_identity_single_tap_pk_creation]
            is BeginCreatePublicKeyCredentialRequest -> {
                Log.i(TAG, "Request is passkey type")
                return handleCreatePasskeyQuery(request, passwordCount, passkeyCount)
            }
            // [END android_identity_single_tap_pk_creation]
            
            // [START android_identity_single_tap_pk_flow]
            is BeginGetPublicKeyCredentialOption -> {
                // ... other logic

                populatePasskeyData(
                    origin,
                    option,
                    responseBuilder,
                    autoSelectEnabled,
                    allowedAuthenticator
                )

                // ... other logic as needed
            }
            // [END android_identity_single_tap_pk_flow]
        }
    }

    private fun handleCreatePasskeyQuery(
        request: BeginCreatePublicKeyCredentialRequest,
        passwordCount: Int,
        passkeyCount: Int
    ) {
        val allowedAuthenticator = 0

        // [START android_identity_single_tap_create_entry]
        val createEntry = CreateEntry(
            // Additional properties...
            biometricPromptData = BiometricPromptData(
                allowedAuthenticators = allowedAuthenticator
            ),
        )
        // [END android_identity_single_tap_create_entry]
    }

    @RequiresApi(VERSION_CODES.M)
    private fun handleCredentialEntrySelection(
        accountId: String = "",
        createPasskey: (String, CallingAppInfo, ByteArray?, String) -> Unit
    ) {
        // [START android_identity_single_tap_handle_credential_entry]
        val createRequest = PendingIntentHandler.retrieveProviderCreateCredentialRequest(intent)
        if (createRequest == null) {
            Log.i(TAG, "request is null")
            setUpFailureResponseAndFinish("Unable to extract request from intent")
            return
        }
        // Other logic...

        val biometricPromptResult = createRequest.biometricPromptResult

        // Add your logic based on what needs to be done
        // after getting biometrics

        if (createRequest.callingRequest is CreatePublicKeyCredentialRequest) {
            val publicKeyRequest: CreatePublicKeyCredentialRequest =
                createRequest.callingRequest as CreatePublicKeyCredentialRequest

            if (biometricPromptResult == null) {
                // Do your own authentication flow, if needed
            } else if (biometricPromptResult.isSuccessful) {
                createPasskey(
                    publicKeyRequest.requestJson,
                    createRequest.callingAppInfo,
                    publicKeyRequest.clientDataHash,
                    accountId
                )
            } else {
                val error = biometricPromptResult.authenticationError
                // Process the error
            }

            // Other logic...
        }
        // [END android_identity_single_tap_handle_credential_entry]
    }

    @RequiresApi(VERSION_CODES.M)
    private fun retrieveProviderGetCredentialRequest(
        validatePasskey: (String, String, String, String, String, String, String) -> Unit,
        publicKeyRequest: CreatePublicKeyCredentialRequest,
        origin: String,
        uid: String,
        passkey: PK,
        credId: String,
        privateKey: String,
    ) {
        // [START android_identity_single_tap_get_cred_request]
        val getRequest =
            PendingIntentHandler.retrieveProviderGetCredentialRequest(intent)

        if (getRequest == null) {
            Log.i(TAG, "request is null")
            setUpFailureResponseAndFinish("Unable to extract request from intent")
            return
        }

        // Other logic...

        val biometricPromptResult = getRequest.biometricPromptResult

        // Add your logic based on what needs to be done
        // after getting biometrics

        if (biometricPromptResult == null) {
            // Do your own authentication flow, if necessary
        } else if (biometricPromptResult.isSuccessful) {

            Log.i(TAG, "The response from the biometricPromptResult was ${biometricPromptResult.authenticationResult?.authenticationType}")

            validatePasskey(
                publicKeyRequest.requestJson,
                origin,
                packageName,
                uid,
                passkey.username,
                credId,
                privateKey
            )
        } else {
            val error = biometricPromptResult.authenticationError
            // Process the error
        }

        // Other logic...
        // [END android_identity_single_tap_get_cred_request]
    }

    private fun CreateEntry(biometricPromptData: BiometricPromptData) {}

    private fun PublicKeyCredentialEntry(biometricPromptData: BiometricPromptData) {}

    private fun populatePasskeyData(
        origin: Any?,
        option: Any?,
        responseBuilder: Any?,
        autoSelectEnabled: Any?,
        allowedAuthenticator: Any?
    ) {}

    private fun setUpFailureResponseAndFinish(str: String) {}
}

data class PK(
    val username: String,
)
