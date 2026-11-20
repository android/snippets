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

import android.content.MutableContextWrapper
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.DigitalCredential
import androidx.credentials.ExperimentalDigitalCredentialApi
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetDigitalCredentialOption
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.GetCredentialUnknownException
import androidx.credentials.exceptions.NoCredentialException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalDigitalCredentialApi::class)
private fun constructDigitalCredentialRequest(): GetCredentialRequest {
    // [START android_identity_verifier_create_request]
    // The request in the JSON format to conform with
    // the JSON-ified Credential Manager - Verifier API request definition.
    val requestJson = generateRequestFromServer()
    val digitalCredentialOption =
        GetDigitalCredentialOption(requestJson = requestJson)

    // Use the option from the previous step to build the `GetCredentialRequest`.
    val getCredRequest = GetCredentialRequest(
        listOf(digitalCredentialOption)
    )
    // [END android_identity_verifier_create_request]
    return getCredRequest
}

@OptIn(ExperimentalDigitalCredentialApi::class)
private object GetCredentialFlow {
    private const val TAG = "RequestCredential"

    private fun Handlers.launchGetCredential(
        coroutineScope: CoroutineScope,
        credentialManager: CredentialManager,
        activityContext: MutableContextWrapper,
        getCredRequest: GetCredentialRequest,
    ) {
        // [START android_identity_verifier_get_credential]
        coroutineScope.launch {
            try {
                val result = credentialManager.getCredential(
                    context = activityContext,
                    request = getCredRequest
                )
                verifyResult(result)
            } catch (e: GetCredentialException) {
                handleFailure(e)
            }
        }

        // [START_EXCLUDE silent]
    }

    private object Handlers {
        // [END_EXCLUDE]
        // Handle the successfully returned credential.
        fun verifyResult(result: GetCredentialResponse) {
            val credential = result.credential
            when (credential) {
                is DigitalCredential -> {
                    val responseJson = credential.credentialJson
                    validateResponseOnServer(responseJson)
                }
                else -> {
                    // Catch any unrecognized credential type here.
                    Log.e(TAG, "Unexpected type of credential ${credential.type}")
                }
            }
        }

        // Handle failure.
        fun handleFailure(e: GetCredentialException) {
            when (e) {
                is GetCredentialCancellationException -> {
                    // The user intentionally canceled the operation and chose not
                    // to share the credential.
                }
                is GetCredentialInterruptedException -> {
                    // Retry-able error. Consider retrying the call.
                }
                is NoCredentialException -> {
                    // No credential was available.
                }
                else -> Log.w(TAG, "Unexpected exception type ${e::class.java}")
            }
        }
        // [END android_identity_verifier_get_credential]
    }
}

private object RequestCredentialHandlers {
    private const val TAG = "RequestCredential"

    @OptIn(ExperimentalDigitalCredentialApi::class)
    // [START android_identity_verifier_verify_result]
    // Handle the successfully returned credential.
    fun verifyResult(result: GetCredentialResponse) {
        val credential = result.credential
        when (credential) {
            is DigitalCredential -> {
                val responseJson = credential.credentialJson
                validateResponseOnServer(responseJson)
            }
            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential ${credential.type}")
            }
        }
    }
    // [END android_identity_verifier_verify_result]

    // [START android_identity_verifier_handle_failure]
    // Handle failure.
    fun handleFailure(e: GetCredentialException) {
        when (e) {
            is GetCredentialCancellationException -> {
                // The user intentionally canceled the operation and chose not
                // to share the credential.
            }
            is GetCredentialInterruptedException -> {
                // Retry-able error. Consider retrying the call.
            }
            is NoCredentialException -> {
                // No credential was available.
            }
            is GetCredentialUnknownException -> {
                // An unknown, usually unexpected, error has occurred. Check the
                // message error for any additional debugging information.
            }
            is GetCredentialCustomException -> {
                // You have encountered a custom error thrown by the wallet.
                // If you made the API call with a request object that's a
                // subclass of GetCustomCredentialOption using a 3rd-party SDK,
                // then you should check for any custom exception type constants
                // within that SDK to match with e.type. Otherwise, drop or log the
                // exception.
            }
            else -> Log.w(TAG, "Unexpected exception type ${e::class.java}")
        }
    }
    // [END android_identity_verifier_handle_failure]
}

private fun generateRequestFromServer(): String = "{}"

private fun validateResponseOnServer(responseJson: String) {
    Log.d("RequestCredential", "Validating response: $responseJson")
}
