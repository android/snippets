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

import android.app.backup.BackupAgent
import android.app.backup.BackupDataInput
import android.app.backup.BackupDataOutput
import android.content.Context
import android.os.ParcelFileDescriptor
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.ClearCredentialStateRequest.Companion.TYPE_CLEAR_RESTORE_CREDENTIAL
import androidx.credentials.CreateRestoreCredentialRequest
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetRestoreCredentialOption
import androidx.credentials.RestoreCredential
import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.exceptions.restorecredential.E2eeUnavailableException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class RestoreCredentialsFunctions(
    private val context: Context,
    private val credentialManager: CredentialManager,
) {
    suspend fun createRestoreKey(
        requestJson: String,
    ) {
        // [START android_identity_restore_cred_create]
        // requestJson contains the credential creation options sent by the server
        val createRestoreRequest = CreateRestoreCredentialRequest(requestJson)
        val response = try {
            credentialManager.createCredential(context, createRestoreRequest)
        } catch (e: E2eeUnavailableException) {
            // Fall back to local device-to-device (D2D) restore if cloud E2EE is unavailable
            credentialManager.createCredential(
                context,
                CreateRestoreCredentialRequest(requestJson, isCloudBackupEnabled = false),
            )
        }
        // [END android_identity_restore_cred_create]
    }

    suspend fun deleteRestoreKey() {
        // [START android_identity_restore_cred_delete]
        // Clear active credential provider session state
        credentialManager.clearCredentialState(ClearCredentialStateRequest())

        // When the user logs out, delete the restore key
        val clearRequest = ClearCredentialStateRequest(TYPE_CLEAR_RESTORE_CREDENTIAL)
        val response = credentialManager.clearCredentialState(clearRequest)
        // [END android_identity_restore_cred_delete]
    }

    // [START android_identity_restore_cred_get]
    suspend fun getRestoreKey(
        fetchAuthenticationJson: () -> String,
    ) {
        // Fetch the options required to get the restore key
        val authenticationJson = fetchAuthenticationJson()

        // Create the GetRestoreCredentialOption object
        val options = GetRestoreCredentialOption(authenticationJson)
        val getRequest = GetCredentialRequest(listOf(options))

        try {
            val response = credentialManager.getCredential(context, getRequest)

            // Type-check and extract the restore credential
            val credential = response.credential
            if (credential is RestoreCredential) {
                val responseJson = credential.authenticationResponseJson
            }
        } catch (e: NoCredentialException) {
            // No restore key is present on this device
        }
    }
    // [START_EXCLUDE silent]
}
// [END_EXCLUDE]

class MyBackupAgent : BackupAgent() {
    // [START_EXCLUDE silent]
    override fun onBackup(
        oldState: ParcelFileDescriptor?,
        data: BackupDataOutput?,
        newState: ParcelFileDescriptor?,
    ) = Unit

    override fun onRestore(
        data: BackupDataInput?,
        appVersionCode: Int,
        newState: ParcelFileDescriptor?,
    ) = Unit
    // [END_EXCLUDE]

    override fun onRestoreFinished() {
        super.onRestoreFinished()
        val credentialManager = CredentialManager.create(this)
        val restoreFunctions = RestoreCredentialsFunctions(this, credentialManager)

        runBlocking(Dispatchers.IO) {
            restoreFunctions.getRestoreKey(
                fetchAuthenticationJson = {
                    /* Fetch challenge JSON from server */ ""
                }
            )
        }
    }
}
// [END android_identity_restore_cred_get]
