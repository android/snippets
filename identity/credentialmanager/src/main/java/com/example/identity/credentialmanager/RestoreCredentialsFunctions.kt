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

import android.app.backup.BackupAgent
import android.app.backup.BackupDataInput
import android.app.backup.BackupDataOutput
import android.content.Context
import android.os.ParcelFileDescriptor
import android.util.Base64
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.ClearCredentialStateRequest.Companion.TYPE_CLEAR_RESTORE_CREDENTIAL
import androidx.credentials.CreateRestoreCredentialRequest
import androidx.credentials.CreateRestoreCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetRestoreCredentialOption
import androidx.credentials.PublicKeyCredential
import androidx.credentials.RestoreCredential
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import androidx.credentials.exceptions.restorecredential.E2eeUnavailableException
import java.security.SecureRandom
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeoutOrNull
import org.json.JSONObject

/**
 * Demonstrates how to use the Credential Manager Restore Credentials APIs to silently restore a
 * user's account session across cloud backups and device-to-device (D2D) transfers, including
 * optional WebAuthn Level 3 `largeBlob` metadata storage.
 *
 * @property context The application or activity [Context] used for Credential Manager requests.
 * @property credentialManager The [CredentialManager] instance used to create, retrieve, and clear
 *   restore credentials.
 */
class RestoreCredentialsFunctions(
    private val context: Context,
    private val credentialManager: CredentialManager,
) {

    /**
     * Creates a restore key on the source device and registers its public key with the relying
     * party server.
     *
     * Attempts creation with end-to-end encrypted cloud backup enabled first
     * (`isCloudBackupEnabled = true`), and falls back to a local device-to-device (D2D) restore key
     * (`isCloudBackupEnabled = false`) if [E2eeUnavailableException] is thrown.
     *
     * @param requestJson The WebAuthn `PublicKeyCredentialCreationOptions` JSON string from the
     *   relying party server.
     * @return The [CreateRestoreCredentialResponse] returned by [CredentialManager], or `null` if a
     *   restore key has already been synced on this device.
     */
    suspend fun createRestoreKey(
        requestJson: String,
    ): CreateRestoreCredentialResponse? {
        if (hasSyncedRestoreCredential()) return null

        // [START android_identity_restore_cred_create]
        // Try creating an E2EE cloud-backed restore key first; fall back to local D2D if unavailable.
        val response = try {
            credentialManager.createCredential(
                context = context,
                request = CreateRestoreCredentialRequest(
                    requestJson = requestJson,
                    isCloudBackupEnabled = true,
                ),
            ) as CreateRestoreCredentialResponse
        } catch (e: E2eeUnavailableException) {
            // Device lacks a knowledge-factor screen lock or cloud backup; save for local D2D transfer.
            credentialManager.createCredential(
                context = context,
                request = CreateRestoreCredentialRequest(
                    requestJson = requestJson,
                    isCloudBackupEnabled = false,
                ),
            ) as CreateRestoreCredentialResponse
        }
        // [END android_identity_restore_cred_create]

        if (registerRestoreKeyOnServer(response.responseJson)) {
            markRestoreCredentialSynced()
        }
        return response
    }

    /**
     * Retrieves the transferred [RestoreCredential] on the target device, verifies the WebAuthn
     * assertion with the relying party server, and refreshes push notification tokens.
     *
     * Invoke this shared `suspend` function from both [AppRestoreBackupAgent.onRestoreFinished]
     * (Tier 1 background restore) and launcher `Activity.onCreate()` (Tier 2 foreground fallback).
     *
     * @return `true` if a restore key was found and verified by the server; `false` otherwise.
     */
    // [START android_identity_restore_cred_get]
    suspend fun getRestoreKey(): Boolean {
        return try {
            // Fetch unauthenticated WebAuthn PublicKeyCredentialRequestOptions JSON from your server.
            val authenticationJson = fetchAuthenticationJsonFromServer()

            // Create the GetRestoreCredentialOption and GetCredentialRequest objects.
            val options = GetRestoreCredentialOption(authenticationJson)
            val getRequest = GetCredentialRequest(listOf(options))

            val response = credentialManager.getCredential(context, getRequest)

            // Type-check and cast explicitly to RestoreCredential (not PublicKeyCredential).
            val restoreCredential = response.credential as RestoreCredential
            val signedIn =
                verifyRestoreAssertionOnServer(restoreCredential.authenticationResponseJson)
            if (signedIn) {
                // Refresh push notification token (e.g., FirebaseMessaging.getInstance().token.await()).
                refreshAndUploadPushToken()
            }
            signedIn
        } catch (e: NoCredentialException) {
            // No restore key is available on this device; proceed to standard sign-in UI.
            false
        } catch (e: GetCredentialException) {
            false
        }
    }
    // [END android_identity_restore_cred_get]

    /**
     * Extracts the WebAuthn authentication response JSON when handling credential responses that
     * may be either a [RestoreCredential] or a [PublicKeyCredential].
     *
     * Because [RestoreCredential] subclasses [androidx.credentials.Credential] directly rather than
     * [PublicKeyCredential], branch on both types explicitly.
     *
     * @param response The [GetCredentialResponse] returned by [CredentialManager.getCredential].
     * @return The WebAuthn `AuthenticatorAssertionResponse` JSON string, or `null` for other types.
     */
    fun extractAuthenticationResponseJson(response: GetCredentialResponse): String? {
        // [START android_identity_restore_cred_branching]
        val responseJson: String? = when (val credential = response.credential) {
            is RestoreCredential -> credential.authenticationResponseJson
            is PublicKeyCredential -> credential.authenticationResponseJson
            else -> null
        }
        // [END android_identity_restore_cred_branching]
        return responseJson
    }

    /**
     * Tier 1 background restoration [BackupAgent] that silently restores the user's session during
     * device setup immediately after app data restoration completes.
     *
     * Because [BackupAgent.onRestoreFinished] is synchronous while
     * [CredentialManager.getCredential] is a `suspend` function, bridge execution synchronously on
     * [Dispatchers.IO] with a bounded timeout so the system does not destroy the backup process
     * before verification finishes.
     */
    // [START android_identity_restore_cred_backup_agent]
    class AppRestoreBackupAgent : BackupAgent() {
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

        override fun onRestoreFinished() {
            super.onRestoreFinished()
            // Block synchronously with a timeout until credential retrieval and server sign-in complete.
            runBlocking(Dispatchers.IO) {
                withTimeoutOrNull(10_000L) {
                    val credentialManager = CredentialManager.create(applicationContext)
                    RestoreCredentialsFunctions(applicationContext, credentialManager)
                        .getRestoreKey()
                }
            }
        }
    }
    // [END android_identity_restore_cred_backup_agent]

    /**
     * Performs a dual credential state clear on user sign-out or server-side session invalidation
     * (`HTTP 401 Unauthorized`), and removes the local restore sync marker.
     */
    suspend fun deleteRestoreKey() {
        // [START android_identity_restore_cred_delete]
        // Clear standard active credential provider session state.
        credentialManager.clearCredentialState(ClearCredentialStateRequest())

        // Explicitly delete the stored RestoreCredential.
        val clearRequest = ClearCredentialStateRequest(TYPE_CLEAR_RESTORE_CREDENTIAL)
        credentialManager.clearCredentialState(clearRequest)

        // Delete the local has_synced_restore_credential marker from local storage.
        deleteLocalRestoreSyncMarker()
        // [END android_identity_restore_cred_delete]
    }

    /**
     * Creates a restore key while negotiating WebAuthn Level 3 `largeBlob` extension support
     * (`"support": "preferred"`), preserving the [E2eeUnavailableException] local D2D fallback.
     *
     * Requires Google Play services `26.43+` for `largeBlob` support; older versions ignore the
     * extension and still create a standard restore key.
     *
     * @param creationOptionsJson The WebAuthn `PublicKeyCredentialCreationOptions` [JSONObject]
     *   from the relying party server.
     * @return The [CreateRestoreCredentialResponse] containing `clientExtensionResults`.
     */
    // [START android_identity_restore_cred_large_blob_create]
    suspend fun createRestoreKeyWithLargeBlobSupport(
        creationOptionsJson: JSONObject,
    ): CreateRestoreCredentialResponse {
        // Negotiate WebAuthn largeBlob support during key creation (do not include read or write).
        val extensions = creationOptionsJson.optJSONObject("extensions") ?: JSONObject()
        extensions.put("largeBlob", JSONObject().put("support", "preferred"))
        creationOptionsJson.put("extensions", extensions)

        val requestJson = creationOptionsJson.toString()
        return try {
            credentialManager.createCredential(
                context = context,
                request = CreateRestoreCredentialRequest(
                    requestJson = requestJson,
                    isCloudBackupEnabled = true,
                ),
            ) as CreateRestoreCredentialResponse
        } catch (e: E2eeUnavailableException) {
            credentialManager.createCredential(
                context = context,
                request = CreateRestoreCredentialRequest(
                    requestJson = requestJson,
                    isCloudBackupEnabled = false,
                ),
            ) as CreateRestoreCredentialResponse
        }
    }

    /**
     * Checks whether Google Play services confirmed `largeBlob` support for the created restore key
     * via `clientExtensionResults.largeBlob.supported`.
     */
    fun isLargeBlobSupported(response: CreateRestoreCredentialResponse): Boolean =
        JSONObject(response.responseJson)
            .optJSONObject("clientExtensionResults")
            ?.optJSONObject("largeBlob")
            ?.optBoolean("supported", false) == true
    // [END android_identity_restore_cred_large_blob_create]

    /**
     * Writes up to 16 KiB (16,384 raw bytes) of associated bootstrap metadata to the stored
     * [RestoreCredential] using the WebAuthn `largeBlob` extension (`extensions.largeBlob.write`).
     *
     * Because this call updates the local credential entry on the source device without sending an
     * assertion to the relying party server, it generates a fresh 32-byte [SecureRandom] challenge
     * on-device.
     *
     * @param rpId The relying party domain identifier (for example, `"example.com"`).
     * @param metadataBytes The raw metadata payload to store (16,384 bytes or smaller).
     * @return `true` if `clientExtensionResults.largeBlob.written` is `true`; `false` otherwise.
     * @throws IllegalArgumentException If [metadataBytes] exceeds 16 KiB (16,384 bytes).
     */
    // [START android_identity_restore_cred_large_blob_write]
    suspend fun writeRestoreCredentialLargeBlob(
        rpId: String,
        metadataBytes: ByteArray,
    ): Boolean {
        val maxLargeBlobBytes = 16 * 1024 // 16 KiB raw byte limit
        require(metadataBytes.size <= maxLargeBlobBytes) {
            "largeBlob payload (${metadataBytes.size} bytes) exceeds 16 KiB ($maxLargeBlobBytes bytes)."
        }

        // Encode both challenge and blob as unpadded URL-safe Base64 (RFC 4648).
        val base64UrlFlags = Base64.URL_SAFE or Base64.NO_WRAP or Base64.NO_PADDING
        val challengeBytes = ByteArray(32).also { SecureRandom().nextBytes(it) }
        val encodedChallenge = Base64.encodeToString(challengeBytes, base64UrlFlags)
        val encodedBlob = Base64.encodeToString(metadataBytes, base64UrlFlags)

        val writeRequestJson = JSONObject()
            .put("challenge", encodedChallenge)
            .put("rpId", rpId)
            .put(
                "extensions",
                JSONObject().put("largeBlob", JSONObject().put("write", encodedBlob)),
            )
            .toString()

        val result = credentialManager.getCredential(
            context = context,
            request = GetCredentialRequest(
                listOf(GetRestoreCredentialOption(requestJson = writeRequestJson)),
            ),
        )
        val restoreCredential = result.credential as RestoreCredential
        return JSONObject(restoreCredential.authenticationResponseJson)
            .optJSONObject("clientExtensionResults")
            ?.optJSONObject("largeBlob")
            ?.optBoolean("written", false) == true
    }
    // [END android_identity_restore_cred_large_blob_write]

    /**
     * Holds the WebAuthn assertion JSON and optional decoded `largeBlob` metadata bytes returned
     * when retrieving a [RestoreCredential] on a new device.
     *
     * @property authenticationResponseJson The WebAuthn `AuthenticatorAssertionResponse` JSON to
     *   verify on the relying party server.
     * @property largeBlobBytes The decoded metadata payload if present, or `null` if no blob was
     *   written or Google Play services is earlier than `26.43`.
     */
    // [START android_identity_restore_cred_large_blob_read]
    class RestoredCredentialPayload(
        val authenticationResponseJson: String,
        val largeBlobBytes: ByteArray?,
    )

    /**
     * Retrieves both the WebAuthn assertion and any attached `largeBlob` metadata in a single
     * [CredentialManager.getCredential] call on the destination device.
     *
     * @param authenticationOptionsJson The WebAuthn `PublicKeyCredentialRequestOptions`
     *   [JSONObject] fetched from the relying party server.
     * @return A [RestoredCredentialPayload] containing the assertion JSON and optional blob bytes.
     */
    suspend fun getRestoreKeyAndLargeBlob(
        authenticationOptionsJson: JSONObject,
    ): RestoredCredentialPayload {
        // Request the attached largeBlob alongside the WebAuthn assertion.
        val extensions =
            authenticationOptionsJson.optJSONObject("extensions") ?: JSONObject()
        extensions.put("largeBlob", JSONObject().put("read", true))
        authenticationOptionsJson.put("extensions", extensions)

        val result = credentialManager.getCredential(
            context = context,
            request = GetCredentialRequest(
                listOf(GetRestoreCredentialOption(authenticationOptionsJson.toString())),
            ),
        )
        val restoreCredential = result.credential as RestoreCredential
        val responseJson = restoreCredential.authenticationResponseJson
        val blobBase64Url = JSONObject(responseJson)
            .optJSONObject("clientExtensionResults")
            ?.optJSONObject("largeBlob")
            ?.optString("blob", "")
            .orEmpty()

        val decodedBytes = if (blobBase64Url.isNotEmpty()) {
            Base64.decode(blobBase64Url, Base64.URL_SAFE)
        } else {
            null // No blob was written, or Google Play services < 26.43
        }
        return RestoredCredentialPayload(
            authenticationResponseJson = responseJson,
            largeBlobBytes = decodedBytes,
        )
    }
    // [END android_identity_restore_cred_large_blob_read]

    /**
     * Placeholder check for the local `has_synced_restore_credential` marker in local storage.
     */
    private suspend fun hasSyncedRestoreCredential(): Boolean = false

    /**
     * Placeholder that records `has_synced_restore_credential = true` in local storage after
     * server registration succeeds.
     */
    private suspend fun markRestoreCredentialSynced() = Unit

    /**
     * Placeholder that deletes the local `has_synced_restore_credential` marker on sign-out or
     * session invalidation.
     */
    private suspend fun deleteLocalRestoreSyncMarker() = Unit

    /**
     * Placeholder that sends `CreateRestoreCredentialResponse.responseJson` to the relying party
     * server to verify and store the public key.
     */
    private suspend fun registerRestoreKeyOnServer(responseJson: String): Boolean = true

    /**
     * Placeholder that fetches unauthenticated WebAuthn `PublicKeyCredentialRequestOptions` JSON
     * from the relying party server.
     */
    private suspend fun fetchAuthenticationJsonFromServer(): String = "{}"

    /**
     * Placeholder that sends `RestoreCredential.authenticationResponseJson` to the relying party
     * server for cryptographic signature verification and session token issuance.
     */
    private suspend fun verifyRestoreAssertionOnServer(
        authenticationResponseJson: String,
    ): Boolean = true

    /**
     * Placeholder that fetches a fresh push notification token (such as an FCM registration token)
     * and uploads it to the app server after silent restoration.
     */
    private suspend fun refreshAndUploadPushToken() = Unit
}
