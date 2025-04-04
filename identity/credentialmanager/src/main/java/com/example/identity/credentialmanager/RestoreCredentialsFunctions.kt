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
        val credentialManager = CredentialManager.create(context)

        // On a successful authentication create a Restore Key
        // Pass in the context and CreateRestoreCredentialRequest object
        val response = credentialManager.createCredential(context, createRestoreRequest)
        // [END android_identity_restore_cred_create]
    }

    suspend fun getRestoreKey(
        fetchAuthenticationJson: () -> String,
    ) {
        // [START android_identity_restore_cred_get]
        // Fetch the Authentication JSON from server
        val authenticationJson = fetchAuthenticationJson()

        // Create the GetRestoreCredentialRequest object
        val options = GetRestoreCredentialOption(authenticationJson)
        val getRequest = GetCredentialRequest(listOf(options))

        // The restore key can be fetched in two scenarios to
        // 1. On the first launch of app on the device, fetch the Restore Key
        // 2. In the onRestore callback (if the app implements the Backup Agent)
        val response = credentialManager.getCredential(context, getRequest)
        // [END android_identity_restore_cred_get]
    }

    suspend fun deleteRestoreKey() {
        // [START android_identity_restore_cred_delete]
        // Create a ClearCredentialStateRequest object
        val clearRequest = ClearCredentialStateRequest(TYPE_CLEAR_RESTORE_CREDENTIAL)

        // On user log-out, clear the restore key
        val response = credentialManager.clearCredentialState(clearRequest)
        // [END android_identity_restore_cred_delete]
    }
}
