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

import androidx.credentials.CredentialManager
import androidx.credentials.SignalAllAcceptedCredentialIdsRequest
import androidx.credentials.SignalCurrentUserDetailsRequest
import androidx.credentials.SignalUnknownCredentialRequest
import org.json.JSONArray
import org.json.JSONObject

/**
 * This class demonstrates how to use the Credential Manager Signal APIs. These APIs allows to send
 * signals to the credential provider about the user's credential state. For example, when a user
 * has deleted a credential or updated it's metadata.
 *
 * @param credentialManager An instance of [CredentialManager] used to interact with the Signal APIs.
 */
class SignalApiFunctions(
    private val credentialManager: CredentialManager,
) {

    /**
     * Signals that a credential is unknown to the user. For example when a credential is deleted
     * by the user
     *
     * @param rpId The relying party ID.
     * @param credentialId The ID of the credential that is unknown.
     */
    suspend fun signalUnknownCredential(
        rpId: String,
        credentialId: String,
    ) {
        // [START android_identity_signal_api_unknown]
        credentialManager.signalCredentialState(
            SignalUnknownCredentialRequest(
                requestJson = JSONObject().apply {
                    put("rpId", rpId /* [String] RP ID of the relying party */)
                    put("credentialId", credentialId /* [String] Credential ID of the credential to be hidden or deleted */)
                }.toString()
            )
        )
        // [END android_identity_signal_api_unknown]
    }

    /**
     * Signals all accepted credentials for a given user. Used to inform the credential provider of
     * all the accepted credentials.
     *
     * @param rpId The relying party ID.
     * @param userId The user ID of the current user.
     * @param credentialIdsList A list of all accepted credential IDs for the user.
     */
    suspend fun signalAllAcceptedCredentials(
        rpId: String,
        userId: String,
        credentialIdsList: List<String>,
    ) {
        // [START android_identity_signal_api_all_accepted]
        credentialManager.signalCredentialState(
            SignalAllAcceptedCredentialIdsRequest(
                requestJson = JSONObject().apply {
                    put("rpId", rpId /* [String] RP ID of the relying party */)
                    put("userId", userId /* [String] User ID of the current user */)
                    put(
                        "allAcceptedCredentialIds",
                        JSONArray(credentialIdsList /* [List<String>] List of accepted Credential IDs */)
                    )
                }.toString()
            )
        )
        // [END android_identity_signal_api_all_accepted]
    }

    /**
     * Signals the current user's updated details. Used to inform the credential provider about the
     * current user's updated  details, such as their name and display name.
     *
     * @param rpId The relying party ID.
     * @param userId The user ID of the current user.
     * @param name The new name to be updated for the current user.
     * @param displayName The new display name to be updated for the current user.
     */
    suspend fun signalCurrentUserDetails(
        rpId: String,
        userId: String,
        name: String,
        displayName: String,
    ) {
        // [START android_identity_signal_api_current_user_details]
        credentialManager.signalCredentialState(
            SignalCurrentUserDetailsRequest(
                requestJson = JSONObject().apply {
                    put("rpId", rpId /* [String] RP ID of the relying party */)
                    put("userId", userId /* [String] User ID of the current user */)
                    put("name", name /* [String] New Name to be updated for the current user */)
                    put("displayName", displayName /* [String] New display name to be updated for the current user */)
                }.toString()
            )
        )
        // [END android_identity_signal_api_current_user_details]
    }
}
