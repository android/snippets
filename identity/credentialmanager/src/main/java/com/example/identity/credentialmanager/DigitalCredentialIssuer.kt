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

import android.app.Activity
import android.content.Context
import androidx.credentials.CreateDigitalCredentialRequest
import androidx.credentials.CreateDigitalCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.ExperimentalDigitalCredentialApi
import androidx.credentials.exceptions.CreateCredentialCancellationException
import androidx.credentials.exceptions.CreateCredentialCustomException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.CreateCredentialInterruptedException
import androidx.credentials.exceptions.CreateCredentialNoCreateOptionException
import androidx.credentials.exceptions.CreateCredentialProviderConfigurationException
import androidx.credentials.exceptions.CreateCredentialUnknownException
import androidx.credentials.exceptions.CreateCredentialUnsupportedException

class DigitalCredentialIssuer : Activity() {
    @OptIn(ExperimentalDigitalCredentialApi::class)
    suspend fun issueToWallet(context: Context) {
        // [START android_identity_issuance_create_credentialmanager]
        val credentialManager = CredentialManager.create(context)
        // [END android_identity_issuance_create_credentialmanager]

        // [START android_identity_issuance_create_digitalcredentialrequest]
        val issuanceRequestJson = "{ ... }" // Your issuance JSON
        val createRequest = CreateDigitalCredentialRequest(
            requestJson = issuanceRequestJson,
            origin = null
        )
        // [END android_identity_issuance_create_digitalcredentialrequest]

        // [START android_identity_issuance_execute_createcredential]
        try {
            val response = credentialManager.createCredential(
                context = context,
                request = createRequest
            )
            handleSuccess(response as CreateDigitalCredentialResponse)
        } catch (e: CreateCredentialException) {
            handleCreateException(e)
        }
        // [END android_identity_issuance_execute_createcredential]
    }

    @OptIn(ExperimentalDigitalCredentialApi::class)
    // [START android_identity_issuance_handle_success]
    fun handleSuccess(response: CreateDigitalCredentialResponse) {
        val responseJson = response.responseJson
        // Parse responseJson according to your protocol (e.g. OpenID4VCI)
    }
    // [END android_identity_issuance_handle_success]

    // [START android_identity_issuance_handle_exception]
    fun handleCreateException(e: CreateCredentialException) {
        when (e) {
            is CreateCredentialCancellationException -> {
                // The user canceled the flow
            }
            is CreateCredentialInterruptedException -> {
                // The flow was interrupted (e.g. by another UI element)
            }
            is CreateCredentialNoCreateOptionException -> {
                // No wallet application is available to handle the request
            }
            is CreateCredentialUnsupportedException -> {
                // The device or the system doesn't support this request
            }
            is CreateCredentialProviderConfigurationException -> {
                // There is a configuration issue with the wallet provider
            }
            is CreateCredentialCustomException -> {
                // A protocol-specific error occurred
                val errorType = e.type
                val errorMessage = e.message
            }
            is CreateCredentialUnknownException -> {
                // An unknown error occurred
            }
            else -> {
                // Generic error handling
            }
        }
    }
    // [END android_identity_issuance_handle_exception]
}
