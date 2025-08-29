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
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.PasswordCredential
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialCustomException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialInterruptedException
import androidx.credentials.exceptions.GetCredentialProviderConfigurationException
import androidx.credentials.exceptions.GetCredentialUnknownException
import androidx.credentials.exceptions.GetCredentialUnsupportedException
import androidx.credentials.exceptions.NoCredentialException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

class SmartLockToCredMan(
    private val credentialManager: CredentialManager,
    private val activityContext: Context,
    private val coroutineScope: CoroutineScope,
) {
    // [START android_identity_init_password_option]
    // Retrieves the user's saved password for your app from their
    // password provider.
    val getPasswordOption = GetPasswordOption()
    // [END android_identity_init_password_option]

    // [START android_identity_get_cred_request]
    val getCredRequest = GetCredentialRequest(
        listOf(getPasswordOption)
    )
    // [END android_identity_get_cred_request]

    val TAG: String = "tag"

    // [START android_identity_launch_sign_in_flow]
    fun launchSignInFlow() {
        coroutineScope.launch {
            try {
                // Attempt to retrieve the credential from the Credential Manager.
                val result = credentialManager.getCredential(
                    // Use an activity-based context to avoid undefined system UI
                    // launching behavior.
                    context = activityContext,
                    request = getCredRequest
                )

                // Process the successfully retrieved credential.
                handleSignIn(result)
            } catch (e: GetCredentialException) {
                // Handle any errors that occur during the credential retrieval
                // process.
                handleFailure(e)
            }
        }
    }

    private fun handleSignIn(result: GetCredentialResponse) {
        // Extract the credential from the response.
        val credential = result.credential

        // Determine the type of credential and handle it accordingly.
        when (credential) {
            is PasswordCredential -> {
                val username = credential.id
                val password = credential.password

                // Use the extracted username and password to perform
                // authentication.
            }

            else -> {
                // Handle unrecognized credential types.
                Log.e(TAG, "Unexpected type of credential")
            }
        }
    }

    private fun handleFailure(e: GetCredentialException) {
        // Handle specific credential retrieval errors.
        when (e) {
            is GetCredentialCancellationException -> {
                /* This exception is thrown when the user intentionally cancels
                    the credential retrieval operation. Update the application's state
                    accordingly. */
            }

            is GetCredentialCustomException -> {
                /* This exception is thrown when a custom error occurs during the
                    credential retrieval flow. Refer to the documentation of the
                    third-party SDK used to create the GetCredentialRequest for
                    handling this exception. */
            }

            is GetCredentialInterruptedException -> {
                /* This exception is thrown when an interruption occurs during the
                    credential retrieval flow. Determine whether to retry the
                    operation or proceed with an alternative authentication method. */
            }

            is GetCredentialProviderConfigurationException -> {
                /* This exception is thrown when there is a mismatch in
                    configurations for the credential provider. Verify that the
                    provider dependency is included in the manifest and that the
                    required system services are enabled. */
            }

            is GetCredentialUnknownException -> {
                /* This exception is thrown when the credential retrieval
                    operation fails without providing any additional details. Handle
                    the error appropriately based on the application's context. */
            }

            is GetCredentialUnsupportedException -> {
                /* This exception is thrown when the device does not support the
                    Credential Manager feature. Inform the user that credential-based
                    authentication is unavailable and guide them to an alternative
                    authentication method. */
            }

            is NoCredentialException -> {
                /* This exception is thrown when there are no viable credentials
                    available for the user. Prompt the user to sign up for an account
                    or provide an alternative authentication method. Upon successful
                    authentication, store the login information using
                    androidx.credentials.CredentialManager.createCredential to
                    facilitate easier sign-in the next time. */
            }

            else -> {
                // Handle unexpected exceptions.
                Log.w(TAG, "Unexpected exception type: ${e::class.java.name}")
            }
        }
    }
    // [END android_identity_launch_sign_in_flow]
}
