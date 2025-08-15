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

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.content.Intent
import android.graphics.drawable.Icon
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.CancellationSignal
import android.os.OutcomeReceiver
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.CreateCredentialUnknownException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.GetCredentialUnknownException
import androidx.credentials.provider.AuthenticationAction
import androidx.credentials.provider.BeginCreateCredentialRequest
import androidx.credentials.provider.BeginCreateCredentialResponse
import androidx.credentials.provider.BeginCreatePublicKeyCredentialRequest
import androidx.credentials.provider.BeginGetCredentialRequest
import androidx.credentials.provider.BeginGetCredentialResponse
import androidx.credentials.provider.BeginGetPasswordOption
import androidx.credentials.provider.BeginGetPublicKeyCredentialOption
import androidx.credentials.provider.CallingAppInfo
import androidx.credentials.provider.CreateEntry
import androidx.credentials.provider.CredentialEntry
import androidx.credentials.provider.CredentialProviderService
import androidx.credentials.provider.PasswordCredentialEntry
import androidx.credentials.provider.ProviderClearCredentialStateRequest
import androidx.credentials.provider.PublicKeyCredentialEntry
import androidx.credentials.webauthn.PublicKeyCredentialRequestOptions

@RequiresApi(VERSION_CODES.UPSIDE_DOWN_CAKE)
class MyCredentialProviderService : CredentialProviderService() {
    private val PERSONAL_ACCOUNT_ID: String = ""
    private val FAMILY_ACCOUNT_ID: String = ""
    private val CREATE_PASSKEY_INTENT: String = ""
    private val PACKAGE_NAME: String = ""
    private val EXTRA_KEY_ACCOUNT_ID: String = ""
    private val UNIQUE_REQ_CODE: Int = 1
    private val UNLOCK_INTENT: String = ""
    private val UNIQUE_REQUEST_CODE: Int = 0
    private val TAG: String = ""
    private val GET_PASSWORD_INTENT: String = ""

    // [START android_identity_credential_provider_passkey_creation]
    override fun onBeginCreateCredentialRequest(
        request: BeginCreateCredentialRequest,
        cancellationSignal: CancellationSignal,
        callback: OutcomeReceiver<BeginCreateCredentialResponse, CreateCredentialException>,
    ) {
        val response: BeginCreateCredentialResponse? = processCreateCredentialRequest(request)
        if (response != null) {
            callback.onResult(response)
        } else {
            callback.onError(CreateCredentialUnknownException())
        }
    }

    fun processCreateCredentialRequest(request: BeginCreateCredentialRequest): BeginCreateCredentialResponse? {
        when (request) {
            is BeginCreatePublicKeyCredentialRequest -> {
                // Request is passkey type
                return handleCreatePasskeyQuery(request)
            }
        }
        // Request not supported
        return null
    }

    private fun handleCreatePasskeyQuery(
        request: BeginCreatePublicKeyCredentialRequest
    ): BeginCreateCredentialResponse {

        // Adding two create entries - one for storing credentials to the 'Personal'
        // account, and one for storing them to the 'Family' account. These
        // accounts are local to this sample app only.
        val createEntries: MutableList<CreateEntry> = mutableListOf()
        createEntries.add(
            CreateEntry(
                PERSONAL_ACCOUNT_ID,
                createNewPendingIntent(PERSONAL_ACCOUNT_ID, CREATE_PASSKEY_INTENT)
            )
        )

        createEntries.add(
            CreateEntry(
                FAMILY_ACCOUNT_ID,
                createNewPendingIntent(FAMILY_ACCOUNT_ID, CREATE_PASSKEY_INTENT)
            )
        )

        return BeginCreateCredentialResponse(createEntries)
    }

    private fun createNewPendingIntent(accountId: String, action: String): PendingIntent {
        val intent = Intent(action).setPackage(PACKAGE_NAME)

        // Add your local account ID as an extra to the intent, so that when
        // user selects this entry, the credential can be saved to this
        // account
        intent.putExtra(EXTRA_KEY_ACCOUNT_ID, accountId)

        return PendingIntent.getActivity(
            applicationContext, UNIQUE_REQ_CODE,
            intent,
            (
                PendingIntent.FLAG_MUTABLE
                    or PendingIntent.FLAG_UPDATE_CURRENT
                )
        )
    }
    // [END android_identity_credential_provider_passkey_creation]

    private lateinit var response: BeginGetCredentialResponse

    // [START android_identity_credential_provider_sign_in]
    private val unlockEntryTitle = "Authenticate to continue"

    override fun onBeginGetCredentialRequest(
        request: BeginGetCredentialRequest,
        cancellationSignal: CancellationSignal,
        callback: OutcomeReceiver<BeginGetCredentialResponse, GetCredentialException>,
    ) {
        if (isAppLocked()) {
            callback.onResult(
                BeginGetCredentialResponse(
                    authenticationActions = mutableListOf(
                        AuthenticationAction(
                            unlockEntryTitle, createUnlockPendingIntent()
                        )
                    )
                )
            )
            return
        }
        try {
            response = processGetCredentialRequest(request)
            callback.onResult(response)
        } catch (e: GetCredentialException) {
            callback.onError(GetCredentialUnknownException())
        }
    }
    // [END android_identity_credential_provider_sign_in]

    // [START android_identity_credential_provider_unlock_pending_intent]
    private fun createUnlockPendingIntent(): PendingIntent {
        val intent = Intent(UNLOCK_INTENT).setPackage(PACKAGE_NAME)
        return PendingIntent.getActivity(
            applicationContext, UNIQUE_REQUEST_CODE, intent,
            (
                PendingIntent.FLAG_MUTABLE
                    or PendingIntent.FLAG_UPDATE_CURRENT
                )
        )
    }
    // [END android_identity_credential_provider_unlock_pending_intent]

    // [START android_identity_credential_provider_process_get_credential_request]
    companion object {
        // These intent actions are specified for corresponding activities
        // that are to be invoked through the PendingIntent(s)
        private const val GET_PASSKEY_INTENT_ACTION = "PACKAGE_NAME.GET_PASSKEY"
        private const val GET_PASSWORD_INTENT_ACTION = "PACKAGE_NAME.GET_PASSWORD"
    }

    fun processGetCredentialRequest(
        request: BeginGetCredentialRequest
    ): BeginGetCredentialResponse {
        val callingPackageInfo = request.callingAppInfo
        val callingPackageName = callingPackageInfo?.packageName.orEmpty()
        val credentialEntries: MutableList<CredentialEntry> = mutableListOf()

        for (option in request.beginGetCredentialOptions) {
            when (option) {
                is BeginGetPasswordOption -> {
                    credentialEntries.addAll(
                        populatePasswordData(
                            callingPackageName,
                            option
                        )
                    )
                }
                is BeginGetPublicKeyCredentialOption -> {
                    credentialEntries.addAll(
                        populatePasskeyData(
                            callingPackageInfo,
                            option
                        )
                    )
                } else -> {
                    Log.i(TAG, "Request not supported")
                }
            }
        }
        return BeginGetCredentialResponse(credentialEntries)
    }
    // [END android_identity_credential_provider_process_get_credential_request]

    @SuppressLint("RestrictedApi")
    // [START android_identity_credential_provider_populate_pkpw_data]
    private fun populatePasskeyData(
        callingAppInfo: CallingAppInfo?,
        option: BeginGetPublicKeyCredentialOption
    ): List<CredentialEntry> {
        val passkeyEntries: MutableList<CredentialEntry> = mutableListOf()
        val request = PublicKeyCredentialRequestOptions(option.requestJson)
        // Get your credentials from database where you saved during creation flow
        val creds = getCredentialsFromInternalDb(request.rpId)
        val passkeys = creds.passkeys
        for (passkey in passkeys) {
            val data = Bundle()
            data.putString("credId", passkey.credId)
            passkeyEntries.add(
                PublicKeyCredentialEntry(
                    context = applicationContext,
                    username = passkey.username,
                    pendingIntent = createNewPendingIntent(
                        GET_PASSKEY_INTENT_ACTION,
                        data
                    ),
                    beginGetPublicKeyCredentialOption = option,
                    displayName = passkey.displayName,
                    icon = passkey.icon
                )
            )
        }
        return passkeyEntries
    }

    // Fetch password credentials and create password entries to populate to the user
    private fun populatePasswordData(
        callingPackage: String,
        option: BeginGetPasswordOption
    ): List<CredentialEntry> {
        val passwordEntries: MutableList<CredentialEntry> = mutableListOf()

        // Get your password credentials from database where you saved during
        // creation flow
        val creds = getCredentialsFromInternalDb(callingPackage)
        val passwords = creds.passwords
        for (password in passwords) {
            passwordEntries.add(
                PasswordCredentialEntry(
                    context = applicationContext,
                    username = password.username,
                    pendingIntent = createNewPendingIntent(
                        GET_PASSWORD_INTENT
                    ),
                    beginGetPasswordOption = option,
                    displayName = password.username,
                    icon = password.icon
                )
            )
        }
        return passwordEntries
    }

    private fun createNewPendingIntent(
        action: String,
        extra: Bundle? = null
    ): PendingIntent {
        val intent = Intent(action).setPackage(PACKAGE_NAME)
        if (extra != null) {
            intent.putExtra("CREDENTIAL_DATA", extra)
        }

        return PendingIntent.getActivity(
            applicationContext, UNIQUE_REQUEST_CODE, intent,
            (PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT)
        )
    }
    // [END android_identity_credential_provider_populate_pkpw_data]

    // [START android_identity_credential_provider_clear_credential]
    override fun onClearCredentialStateRequest(
        request: ProviderClearCredentialStateRequest,
        cancellationSignal: CancellationSignal,
        callback: OutcomeReceiver<Void?, ClearCredentialException>
    ) {
        // Delete any maintained state as appropriate.
    }
    // [END android_identity_credential_provider_clear_credential]

    private fun isAppLocked(): Boolean {
        return true
    }

    private fun getCredentialsFromInternalDb(rpId: String): Creds {
        return Creds()
    }
}

data class Creds(
    val passkeys: List<Passkey> = listOf(),
    val passwords: List<Password> = listOf()
)

data class Passkey(
    val credId: String = "",
    val username: String = "",
    val displayName: String = "",
    val icon: Icon
)

data class Password(
    val username: String = "",
    val icon: Icon
)
