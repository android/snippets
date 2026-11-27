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

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.DigitalCredential
import androidx.credentials.ExperimentalDigitalCredentialApi
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetDigitalCredentialOption
import androidx.credentials.exceptions.GetCredentialException

private object WebviewCredentialManagerHandler {
    // [START android_identity_webview_credential_manager_handler]
    @OptIn(ExperimentalDigitalCredentialApi::class)
    class CredentialManagerHandler(private val context: Context) {
        private val credentialManager = CredentialManager.create(context)

        suspend fun getDigitalCredential(requestJson: String): String {
            val digitalCredentialOption = GetDigitalCredentialOption(requestJson)
            val getCredRequest = GetCredentialRequest(listOf(digitalCredentialOption))

            return try {
                val result = credentialManager.getCredential(context, getCredRequest)
                val credential = result.credential
                if (credential is DigitalCredential) {
                    credential.credentialJson
                } else {
                    "{\"error\": \"Unexpected credential type\"}"
                }
            } catch (e: GetCredentialException) {
                "{\"error\": \"${e.message}\"}"
            } catch (e: Exception) {
                "{\"error\": \"Unknown exception\"}"
            }
        }

        companion object {
            private const val TAG = "CredManHandler"
        }
    }
    // [END android_identity_webview_credential_manager_handler]
}
