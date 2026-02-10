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
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.ClearCredentialStateRequest.Companion.TYPE_CLEAR_RESTORE_CREDENTIAL
import androidx.credentials.CreateRestoreCredentialRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetRestoreCredentialOption

class RestoreCredentialsFunctions(
    private val context: Context,
    private val credentialManager: CredentialManager,
) {
    suspend fun createRestoreKey(
        createRestoreRequest: CreateRestoreCredentialRequest
    ) {
        // [START android_identity_restore_cred_create]
        // createRestoreRequest contains the details sent by the server 
        val response = credentialManager.createCredential(context, createRestoreRequest)
        // [END android_identity_restore_cred_create]
    }

    suspend fun getRestoreKey(
        fetchAuthenticationJson: () -> String,
    ) {
        // [START android_identity_restore_cred_get]
        // Fetch the options required to get the restore key
        val authenticationJson = fetchAuthenticationJson()

        // Create the GetRestoreCredentialRequest object
        val options = GetRestoreCredentialOption(authenticationJson)
        val getRequest = GetCredentialRequest(listOf(options))

        val response = credentialManager.getCredential(context, getRequest)
        // [END android_identity_restore_cred_get]
    }

    suspend fun deleteRestoreKey() {
        // [START android_identity_restore_cred_delete]
        // Create a ClearCredentialStateRequest object
        val clearRequest = ClearCredentialStateRequest(TYPE_CLEAR_RESTORE_CREDENTIAL)

        // When the user logs out, delete the restore key
        val response = credentialManager.clearCredentialState(clearRequest)
        // [END android_identity_restore_cred_delete]
    }
}
