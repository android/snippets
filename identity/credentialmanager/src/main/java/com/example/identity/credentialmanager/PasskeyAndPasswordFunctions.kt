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
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatEditText
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PasswordCredential
import androidx.credentials.PendingGetCredentialRequest
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialCustomException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.CreateCredentialInterruptedException
import androidx.credentials.exceptions.CreateCredentialProviderConfigurationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.publickeycredential.CreatePublicKeyCredentialDomException
import androidx.credentials.pendingGetCredentialRequest
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class PasskeyAndPasswordFunctions(
    context: Context,
) {
    // [START android_identity_initialize_credman]
    // Use your app or activity context to instantiate a client instance of
    // CredentialManager.
    private val credentialManager = CredentialManager.create(context)
    // [END android_identity_initialize_credman]
    private val activityContext = context

    // Placeholder for TAG log value.
    val TAG = ""
    /**
     * Retrieves a passkey from the credential manager.
     *
     * @param creationResult The result of the passkey creation operation.
     * @param context The activity context from the Composable, to be used in Credential Manager APIs
     * @return The [GetCredentialResponse] object containing the passkey, or null if an error occurred.
     */
    @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    fun signInFlow(
        creationResult: JSONObject
    ) {
        val requestJson = creationResult.toString()
        // [START android_identity_get_password_passkey_options]
        // Retrieves the user's saved password for your app from their
        // password provider.
        val getPasswordOption = GetPasswordOption()

        // Get passkey from the user's public key credential provider.
        val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
            requestJson = requestJson
        )
        // [END android_identity_get_password_passkey_options]
        var result: GetCredentialResponse
        // [START android_identity_get_credential_request]
        val credentialRequest = GetCredentialRequest(
            listOf(getPasswordOption, getPublicKeyCredentialOption),
        )
        // [END android_identity_get_credential_request]
        runBlocking {
            // getPrepareCredential request
            // [START android_identity_prepare_get_credential]
            coroutineScope {
                val response = credentialManager.prepareGetCredential(
                    GetCredentialRequest(
                        listOf(
                            getPublicKeyCredentialOption,
                            getPasswordOption
                        )
                    )
                )
            }
            // [END android_identity_prepare_get_credential]
            // getCredential request without handling exception.
            // [START android_identity_launch_sign_in_flow_1]
            coroutineScope {
                try {
                    result = credentialManager.getCredential(
                        // Use an activity-based context to avoid undefined system UI
                        // launching behavior.
                        context = activityContext,
                        request = credentialRequest
                    )
                    handleSignIn(result)
                } catch (e: GetCredentialException) {
                    // Handle failure
                }
            }
            // [END android_identity_launch_sign_in_flow_1]
            // getCredential request adding some exception handling.
            // [START android_identity_handle_exceptions_no_credential]
            coroutineScope {
                try {
                    result = credentialManager.getCredential(
                        context = activityContext,
                        request = credentialRequest
                    )
                } catch (e: GetCredentialException) {
                    Log.e("CredentialManager", "No credential available", e)
                }
            }
            // [END android_identity_handle_exceptions_no_credential]
        }
    }

    fun autofillImplementation(
        requestJson: String
    ) {
        // [START android_identity_autofill_construct_request]
        // Retrieves the user's saved password for your app.
        val getPasswordOption = GetPasswordOption()

        // Get a passkey from the user's public key credential provider.
        val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
            requestJson = requestJson
        )

        val getCredRequest = GetCredentialRequest(
            listOf(getPasswordOption, getPublicKeyCredentialOption)
        )
        // [END android_identity_autofill_construct_request]

        runBlocking {
            // [START android_identity_autofill_get_credential_api]
            coroutineScope {
                try {
                    val result = credentialManager.getCredential(
                        context = activityContext, // Use an activity-based context.
                        request = getCredRequest
                    )
                    handleSignIn(result)
                } catch (e: GetCredentialException) {
                    handleFailure(e)
                }
            }
            // [END android_identity_autofill_get_credential_api]
        }

        val usernameEditText: androidx.appcompat.widget.AppCompatEditText = AppCompatEditText(activityContext)
        val passwordEditText: androidx.appcompat.widget.AppCompatEditText = AppCompatEditText(activityContext)

        // [START android_identity_autofill_enable_edit_text]
        usernameEditText.pendingGetCredentialRequest = PendingGetCredentialRequest(
            getCredRequest
        ) { response ->
            handleSignIn(response)
        }

        passwordEditText.pendingGetCredentialRequest = PendingGetCredentialRequest(
            getCredRequest
        ) { response ->
            handleSignIn(response)
        }
        // [END android_identity_autofill_enable_edit_text]
    }

    // [START android_identity_launch_sign_in_flow_2]
    fun handleSignIn(result: GetCredentialResponse) {
        // Handle the successfully returned credential.
        val credential = result.credential

        when (credential) {
            is PublicKeyCredential -> {
                val responseJson = credential.authenticationResponseJson
                // Share responseJson i.e. a GetCredentialResponse on your server to
                // validate and  authenticate
            }

            is PasswordCredential -> {
                val username = credential.id
                val password = credential.password
                // Use id and password to send to your server to validate
                // and authenticate
            }

            is CustomCredential -> {
                // If you are also using any external sign-in libraries, parse them
                // here with the utility functions provided.
                if (credential.type == ExampleCustomCredential.TYPE) {
                    try {
                        val ExampleCustomCredential =
                            ExampleCustomCredential.createFrom(credential.data)
                        // Extract the required credentials and complete the authentication as per
                        // the federated sign in or any external sign in library flow
                    } catch (e: ExampleCustomCredential.ExampleCustomCredentialParsingException) {
                        // Unlikely to happen. If it does, you likely need to update the dependency
                        // version of your external sign-in library.
                        Log.e(TAG, "Failed to parse an ExampleCustomCredential", e)
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
    // [END android_identity_launch_sign_in_flow_2]

    // [START android_identity_create_passkey]
    suspend fun createPasskey(requestJson: String, preferImmediatelyAvailableCredentials: Boolean) {
        val createPublicKeyCredentialRequest = CreatePublicKeyCredentialRequest(
            // Contains the request in JSON format. Uses the standard WebAuthn
            // web JSON spec.
            requestJson = requestJson,
            // Defines whether you prefer to use only immediately available
            // credentials, not hybrid credentials, to fulfill this request.
            // This value is false by default.
            preferImmediatelyAvailableCredentials = preferImmediatelyAvailableCredentials,
        )

        // Execute CreateCredentialRequest asynchronously to register credentials
        // for a user account. Handle success and failure cases with the result and
        // exceptions, respectively.
        coroutineScope {
            try {
                val result = credentialManager.createCredential(
                    // Use an activity-based context to avoid undefined system
                    // UI launching behavior
                    context = activityContext,
                    request = createPublicKeyCredentialRequest,
                )
                //  Handle passkey creation result
            } catch (e: CreateCredentialException) {
                handleFailure(e)
            }
        }
    }
    // [END android_identity_create_passkey]

    // [START android_identity_handle_create_passkey_failure]
    fun handleFailure(e: CreateCredentialException) {
        when (e) {
            is CreatePublicKeyCredentialDomException -> {
                // Handle the passkey DOM errors thrown according to the
                // WebAuthn spec.
            }
            is CreateCredentialCancellationException -> {
                // The user intentionally canceled the operation and chose not
                // to register the credential.
            }
            is CreateCredentialInterruptedException -> {
                // Retry-able error. Consider retrying the call.
            }
            is CreateCredentialProviderConfigurationException -> {
                // Your app is missing the provider configuration dependency.
                // Most likely, you're missing the
                // "credentials-play-services-auth" module.
            }
            is CreateCredentialCustomException -> {
                // You have encountered an error from a 3rd-party SDK. If you
                // make the API call with a request object that's a subclass of
                // CreateCustomCredentialRequest using a 3rd-party SDK, then you
                // should check for any custom exception type constants within
                // that SDK to match with e.type. Otherwise, drop or log the
                // exception.
            }
            else -> Log.w(TAG, "Unexpected exception type ${e::class.java.name}")
        }
    }
    // [END android_identity_handle_create_passkey_failure]

    fun handleFailure(e: GetCredentialException) { }

    // [START android_identity_register_password]
    suspend fun registerPassword(username: String, password: String) {
        // Initialize a CreatePasswordRequest object.
        val createPasswordRequest =
            CreatePasswordRequest(id = username, password = password)

        // Create credential and handle result.
        coroutineScope {
            try {
                val result =
                    credentialManager.createCredential(
                        // Use an activity based context to avoid undefined
                        // system UI launching behavior.
                        activityContext,
                        createPasswordRequest
                    )
                // Handle register password result
            } catch (e: CreateCredentialException) {
                handleFailure(e)
            }
        }
    }
    // [END android_identity_register_password]
}

sealed class ExampleCustomCredential {
    class ExampleCustomCredentialParsingException : Throwable()

    companion object {
        fun createFrom(data: Bundle): PublicKeyCredential {
            return PublicKeyCredential("")
        }

        const val TYPE: String = ""
    }
}
