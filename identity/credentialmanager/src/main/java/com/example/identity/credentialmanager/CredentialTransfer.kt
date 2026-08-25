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
import android.graphics.Bitmap
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.credentials.providerevents.IntentHandler
import androidx.credentials.providerevents.ProviderEventsManager
import androidx.credentials.providerevents.exception.ImportCredentialsException
import androidx.credentials.providerevents.exception.ImportCredentialsNoExportOptionException
import androidx.credentials.providerevents.transfer.CredentialTypes
import androidx.credentials.providerevents.transfer.ExportEntry
import androidx.credentials.providerevents.transfer.ImportCredentialsRequest
import androidx.credentials.providerevents.transfer.ImportCredentialsResponse
import androidx.credentials.providerevents.transfer.KnownExtensions
import androidx.credentials.providerevents.transfer.ProviderImportCredentialsRequest
import androidx.credentials.providerevents.transfer.RegisterExportRequest
import org.json.JSONObject

// Shims for compilation outside of active context
private val context: Context get() = TODO("Provide context")
private val myEntries: List<ExportEntry> = emptyList()

// [START android_identity_transfer_instantiation]
val providerEventsManager = ProviderEventsManager.create(context)
// [END android_identity_transfer_instantiation]

// [START android_identity_transfer_register]
suspend fun registerMyVaultForExport(
    providerEventsManager: ProviderEventsManager,
    vaultIcon: Bitmap,
    // Randomly generated and stored in encrypted storage
    secretEntryId: String
) {
    val entry = ExportEntry(
        id = secretEntryId,
        accountDisplayName = "MyVault Personal",
        userDisplayName = "alice@example.com",
        icon = vaultIcon,
        supportedCredentialTypes = setOf(
            CredentialTypes.CREDENTIAL_TYPE_BASIC_AUTH, // Passwords
            CredentialTypes.CREDENTIAL_TYPE_PUBLIC_KEY, // Passkeys
            CredentialTypes.CREDENTIAL_TYPE_ADDRESS,
            CredentialTypes.CREDENTIAL_TYPE_CREDIT_CARD
        )
    )

    // RegisterExportRequest.create() attaches the default WASM matcher from assets
    val request = RegisterExportRequest.create(context, listOf(entry))

    try {
        val response = providerEventsManager.registerExport(request)
        // Registration successful
    } catch (e: Exception) {
        // Handle registration exceptions (e.g., RegisterExportProviderConfigurationException)
    }
}
// [END android_identity_transfer_register]

// [START android_identity_transfer_export_activity]
class CredentialExportActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Extract the transfer request from the incoming Intent
        val request: ProviderImportCredentialsRequest? =
            IntentHandler.retrieveProviderImportCredentialsRequest(intent)

        if (request == null) {
            finishWithError()
            return
        }

        // 2. Validate CallingAppInfo and secret `credId`
        val callingAppPackage = request.callingAppInfo.packageName
        val receivedCredId = request.credId
        if (!verifySecretEntryId(receivedCredId) || !isTrustedImporter(callingAppPackage)) {
            // Secret ID mismatch or untrusted caller -> abort
            sendExceptionAndFinish(ImportCredentialsNoExportOptionException("Unauthorized request"))
            return
        }

        // 3. Optional: Prompt user for Biometric / PIN authentication before exporting
        authenticateUserThenExport(request)
    }

    private fun authenticateUserThenExport(request: ProviderImportCredentialsRequest) {
        // ... Biometric prompt logic ...
        // Once authenticated, generate the FIDO CXF JSON string matching the requested types
        val cxfJsonPayload = buildFidoCxfJsonPayload(
            requestedTypes = request.request.credentialTypes,
            requestedExtensions = request.request.knownExtensions
        )

        val response = ImportCredentialsResponse(cxfJsonPayload)

        // 4. Write the JSON payload to the Content URI and set Activity result
        IntentHandler.setImportCredentialsResponse(
            context = this,
            uri = request.uri,
            intent = intent,
            response = response
        )
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun sendExceptionAndFinish(exception: androidx.credentials.providerevents.exception.ImportCredentialsException) {
        IntentHandler.setImportCredentialsException(intent, exception)
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    private fun verifySecretEntryId(credId: String): Boolean {
        // Check if credId matches what you stored when calling RegisterExportRequest
        return credId == getStoredSecretEntryId()
    }

    private fun isTrustedImporter(packageName: String): Boolean {
        // Implement any specific allowlisting / caller checks if required
        return true
    }

    private fun finishWithError() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }
    // [START_EXCLUDE silent]
    // Helper functions to make it compile
    private fun getStoredSecretEntryId(): String = "secret"
    private fun buildFidoCxfJsonPayload(requestedTypes: Set<String>, requestedExtensions: Set<String>): String = "{}"
    // [END_EXCLUDE]
}
// [END android_identity_transfer_export_activity]

// [START android_identity_transfer_import]
suspend fun startCredentialImport(
    activityContext: Context,
    providerEventsManager: ProviderEventsManager
) {
    val importRequest = ImportCredentialsRequest(
        credentialTypes = setOf(
            CredentialTypes.CREDENTIAL_TYPE_BASIC_AUTH,
            CredentialTypes.CREDENTIAL_TYPE_PUBLIC_KEY,
            CredentialTypes.CREDENTIAL_TYPE_ADDRESS,
            CredentialTypes.CREDENTIAL_TYPE_NOTE
        ),
        knownExtensions = setOf(
            KnownExtensions.KNOWN_EXTENSION_SHARED
        )
    )

    try {
        // Launches the system Selector UI; suspends until user selects a provider and completes transfer
        val response = providerEventsManager.importCredentials(activityContext, importRequest)

        // 1. Inspect the source exporter's package info
        val exporterPackageName = response.callingAppInfo.packageName

        // 2. Parse the FIDO CXF JSON string
        val cxfJsonString = response.response.responseJson
        parseAndSaveImportedCredentials(cxfJsonString)
    } catch (e: ImportCredentialsException) {
        // Handle specific import exceptions (e.g., ImportCredentialsCancellationException)
        handleImportFailure(e)
    }
}

private fun parseAndSaveImportedCredentials(cxfJsonString: String) {
    val rootJson = JSONObject(cxfJsonString)
    // Parse according to FIDO Credential Exchange Format (CXF v1.0) specification:
    // https://fidoalliance.org/specs/cx/cxf-v1.0-ps-20250814.html
}

// Helper function to make it compile
private fun handleImportFailure(e: ImportCredentialsException) {}
// [END android_identity_transfer_import]

suspend fun customWasmMatcherUsage() {
    // [START android_identity_transfer_custom_wasm]
    // Loading a custom WASM matcher
    val customMatcherBytes = context.assets.open("my_custom_matcher.wasm").use { it.readBytes() }
    val customRequest = RegisterExportRequest(
        entries = myEntries,
        exportMatcher = customMatcherBytes
    )
    providerEventsManager.registerExport(customRequest)
    // [END android_identity_transfer_custom_wasm]
}
