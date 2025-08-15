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

import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GetSignInWithGoogleOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import kotlinx.coroutines.coroutineScope

const val WEB_CLIENT_ID = ""
class SignInWithGoogleFunctions(
    context: Context,
) {
    private val credentialManager = CredentialManager.create(context)
    private val activityContext = context
    // Placeholder for TAG log value.
    val TAG = ""

    fun createGoogleIdOption(nonce: String): GetGoogleIdOption {
        // [START android_identity_siwg_instantiate_request]
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(true)
            .setServerClientId(WEB_CLIENT_ID)
            .setAutoSelectEnabled(true)
            // nonce string to use when generating a Google ID token
            .setNonce(nonce)
            .build()
        // [END android_identity_siwg_instantiate_request]

        return googleIdOption
    }

    private val googleIdOption = createGoogleIdOption("")

    suspend fun signInUser() {
        // [START android_identity_siwg_signin_flow_create_request]
        val request: GetCredentialRequest = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        coroutineScope {
            try {
                val result = credentialManager.getCredential(
                    request = request,
                    context = activityContext,
                )
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                // Handle failure
            }
        }
        // [END android_identity_siwg_signin_flow_create_request]
    }

    // [START android_identity_siwg_signin_flow_handle_signin]
    fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        val credential = result.credential
        val responseJson: String

        when (credential) {

            // Passkey credential
            is PublicKeyCredential -> {
                // Share responseJson such as a GetCredentialResponse to your server to validate and
                // authenticate
                responseJson = credential.authenticationResponseJson
            }

            // Password credential
            is PasswordCredential -> {
                // Send ID and password to your server to validate and authenticate.
                val username = credential.id
                val password = credential.password
            }

            // GoogleIdToken credential
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract the ID to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                        // You can use the members of googleIdTokenCredential directly for UX
                        // purposes, but don't use them to store or control access to user
                        // data. For that you first need to validate the token:
                        // pass googleIdTokenCredential.getIdToken() to the backend server.
                        // see [validation instructions](https://developers.google.com/identity/gsi/web/guides/verify-google-id-token)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized custom credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }
    // [END android_identity_siwg_signin_flow_handle_signin]

    fun createGoogleSignInWithGoogleOption(nonce: String): GetSignInWithGoogleOption {
        // [START android_identity_siwg_get_siwg_option]
        val signInWithGoogleOption: GetSignInWithGoogleOption = GetSignInWithGoogleOption.Builder(
            serverClientId = WEB_CLIENT_ID
        ).setNonce(nonce)
            .build()
        // [END android_identity_siwg_get_siwg_option]

        return signInWithGoogleOption
    }

    // [START android_identity_handle_siwg_option]
    fun handleSignInWithGoogleOption(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        val credential = result.credential

        when (credential) {
            is CustomCredential -> {
                if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                    try {
                        // Use googleIdTokenCredential and extract id to validate and
                        // authenticate on your server.
                        val googleIdTokenCredential = GoogleIdTokenCredential
                            .createFrom(credential.data)
                    } catch (e: GoogleIdTokenParsingException) {
                        Log.e(TAG, "Received an invalid google id token response", e)
                    }
                } else {
                    // Catch any unrecognized credential type here.
                    Log.e(TAG, "Unexpected type of credential")
                }
            }

            else -> {
                // Catch any unrecognized credential type here.
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }
    // [END android_identity_handle_siwg_option]

    fun googleIdOptionFalseFilter() {
        // [START android_identity_siwg_instantiate_request_2]
        val googleIdOption: GetGoogleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false)
            .setServerClientId(WEB_CLIENT_ID)
            .build()
        // [END android_identity_siwg_instantiate_request_2]
    }
}
