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
import android.content.Intent
import android.util.Base64
import android.util.Log
import androidx.credentials.DigitalCredential
import androidx.credentials.ExperimentalDigitalCredentialApi
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetDigitalCredentialOption
import androidx.credentials.exceptions.GetCredentialUnknownException
import androidx.credentials.provider.PendingIntentHandler
import androidx.credentials.provider.ProviderGetCredentialRequest
import androidx.credentials.registry.digitalcredentials.mdoc.MdocEntry
import androidx.credentials.registry.digitalcredentials.mdoc.MdocField
import androidx.credentials.registry.digitalcredentials.openid4vp.OpenId4VpRegistry
import androidx.credentials.registry.digitalcredentials.sdjwt.SdJwtClaim
import androidx.credentials.registry.digitalcredentials.sdjwt.SdJwtEntry
import androidx.credentials.registry.provider.RegistryManager
import androidx.credentials.registry.provider.digitalcredentials.DigitalCredentialEntry
import androidx.credentials.registry.provider.digitalcredentials.EntryDisplayProperties
import java.security.MessageDigest

class DigitalCredentialHolderActivity : Activity() {
    suspend fun createRegistryAndOutlineRegisterCredentials(context: Context) {
        val credentialEntries = listOf<DigitalCredentialEntry>()
        val id = "123"

        // [START android_identity_create_registry]
        // Create the registry manager
        val registryManager = RegistryManager.create(context)

        // The guide covers how to build this out later
        val registryRequest = OpenId4VpRegistry(credentialEntries, id)

        try {
            registryManager.registerCredentials(registryRequest)
        } catch (e: Exception) {
            // Handle exceptions
        }
        // [END android_identity_create_registry]
    }

    // [START android_identity_mapto_sdjwt_entries]
    fun mapToSdJwtEntries(sdJwtsFromStorage: List<StoredSdJwtEntry>): List<SdJwtEntry> {
        val list = mutableListOf<SdJwtEntry>()

        for (sdJwt in sdJwtsFromStorage) {
            list.add(
                SdJwtEntry(
                    verifiableCredentialType = sdJwt.getVCT(),
                    claims = sdJwt.getClaimsList(),
                    entryDisplayPropertySet = sdJwt.toDisplayProperties(),
                    id = sdJwt.getId() // Make sure this cannot be readily guessed
                )
            )
        }
        return list
    }
    // [END android_identity_mapto_sdjwt_entries]

    // [START android_identity_mapto_mdoc_entries]
    fun mapToMdocEntries(mdocsFromStorage: List<StoredMdocEntry>): List<MdocEntry> {
        val list = mutableListOf<MdocEntry>()

        for (mdoc in mdocsFromStorage) {
            list.add(
                MdocEntry(
                    docType = mdoc.retrieveDocType(),
                    fields = mdoc.getFields(),
                    entryDisplayPropertySet = mdoc.toDisplayProperties(),
                    id = mdoc.getId() // Make sure this cannot be readily guessed
                )
            )
        }
        return list
    }
    // [END android_identity_mapto_mdoc_entries]

    suspend fun registerCredentials(sdJwtsFromStorage: List<StoredSdJwtEntry>, mdocsFromStorage: List<StoredMdocEntry>, registryManager: RegistryManager) {
        // [START android_identity_register_credential_entries]
        val credentialEntries = mapToSdJwtEntries(sdJwtsFromStorage) + mapToMdocEntries(mdocsFromStorage)

        val openidRegistryRequest = OpenId4VpRegistry(
            credentialEntries = credentialEntries,
            id = "my-wallet-openid-registry-v1" // A stable, unique ID to identify your registry record.
        )
        // [END android_identity_register_credential_entries]

        // [START android_identity_register_credentials_registrymanager]
        try {
            val response = registryManager.registerCredentials(openidRegistryRequest)
        } catch (e: Exception) {
            // Handle failure
        }
        // [END android_identity_register_credentials_registrymanager]
    }

    @OptIn(ExperimentalDigitalCredentialApi::class)
    suspend fun handleSelectedCredential(request: ProviderGetCredentialRequest) {
        // [START android_identity_process_selected_credential]
        request.credentialOptions.forEach { option ->
            if (option is GetDigitalCredentialOption) {
                Log.i(TAG, "Got DC request: ${option.requestJson}")
                processRequest(option.requestJson)
            }
        }
        // [END android_identity_process_selected_credential]
    }

    fun processRequest(requestJson: String) {}

    fun checkVerifierIdentity(intent: Intent): String {
        // [START android_identity_retrieve_getcredential_intent]
        val request = PendingIntentHandler.retrieveProviderGetCredentialRequest(intent)
        // [END android_identity_retrieve_getcredential_intent]

        val privilegedAppsJson = ""
        // [START android_identity_get_credential_origin]
        val origin = request?.callingAppInfo?.getOrigin(
            privilegedAppsJson // Your allow list JSON
        )
        // [END android_identity_get_credential_origin]

        // [START android_identity_get_signing_cert_hash]
        val appSigningInfo = request?.callingAppInfo?.signingInfoCompat?.signingCertificateHistory[0]?.toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val certHash = Base64.encodeToString(md.digest(appSigningInfo), Base64.NO_WRAP or Base64.NO_PADDING)
        return "android:apk-key-hash:$certHash"
        // [END android_identity_get_signing_cert_hash]
    }

    @OptIn(ExperimentalDigitalCredentialApi::class)
    fun returnCredentialResponse(resultData: Intent, response: HolderCredentialResponse) {
        try {
            // [START android_identity_get_credential_response]
            PendingIntentHandler.setGetCredentialResponse(
                resultData,
                GetCredentialResponse(DigitalCredential(response.responseJson))
            )
            setResult(RESULT_OK, resultData)
            finish()
            // [END android_identity_get_credential_response]
        } catch (e: Exception) {
            // [START android_identity_get_credential_response_exception]
            PendingIntentHandler.setGetCredentialException(
                resultData,
                GetCredentialUnknownException() // Configure the proper exception
            )
            setResult(RESULT_OK, resultData)
            finish()
            // [END android_identity_get_credential_response_exception]
        }
    }
}

sealed class StoredSdJwtEntry {
    fun getVCT(): String {
        return ""
    }
    fun getClaimsList(): List<SdJwtClaim> {
        return emptyList()
    }
    fun toDisplayProperties(): Set<EntryDisplayProperties> {
        return emptySet()
    }
    fun getId(): String {
        return ""
    }
}

sealed class StoredMdocEntry {
    fun retrieveDocType(): String {
        return ""
    }
    fun getFields(): List<MdocField> {
        return emptyList()
    }
    fun toDisplayProperties(): Set<EntryDisplayProperties> {
        return emptySet()
    }
    fun getId(): String {
        return ""
    }
}

sealed class HolderCredentialResponse {
    val responseJson = ""
}
