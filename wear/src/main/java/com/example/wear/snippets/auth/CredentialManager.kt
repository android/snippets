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

package com.example.wear.snippets.auth

import android.app.Activity
import android.content.Context
import android.os.Bundle
import androidx.credentials.Credential
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import com.google.android.libraries.identity.googleid.GetGoogleIdOption

/**
 * Handles authentication operations using the Android Credential Manager API.
 *
 * This class interacts with an [AuthenticationServer] to facilitate sign-in processes
 * using Passkeys, Passwords, and Sign-In With Google credentials.
 *
 * @param context The Android [Context] used to create the [CredentialManager].
 * @param authenticationServer The [AuthenticationServer] responsible for handling authentication requests.
 */
class CredentialManagerAuthenticator(
    applicationContext: Context,
    private val authenticationServer: AuthenticationServer,
) {
    private val credentialManager: CredentialManager = CredentialManager.create(applicationContext)

    internal suspend fun signInWithCredentialManager(activity: Activity): Boolean {
        // [START android_wear_credential_manager_secondary_fallback]
        try {
            val getCredentialResponse: GetCredentialResponse =
                credentialManager.getCredential(activity, createGetCredentialRequest())
            return authenticate(getCredentialResponse.credential)
        } catch (_: GetCredentialCancellationException) {
            navigateToSecondaryAuthentication()
        }
        // [END android_wear_credential_manager_secondary_fallback]
        return false
    }


/**signInRequest
 * Creates a [GetCredentialRequest] with standard Wear Credential types.
 *
 * @return A configured [GetCredentialRequest] ready to be used with [CredentialManager.getCredential].
 */
private fun createGetCredentialRequest(): GetCredentialRequest {
    return GetCredentialRequest(
        credentialOptions = listOf(
            GetPublicKeyCredentialOption(authenticationServer.getPublicKeyRequestOptions()),
            GetPasswordOption(),
            GetGoogleIdOption.Builder()
                .setServerClientId("<Your Google Sign in Server Client ID here.").build(),
        ),
    )
}

/**
 * Routes the credential received from `getCredential` to the appropriate authentication
 * type handler on the [AuthenticationServer].
 *
 * @param credential The selected cre
 * @return `true` if the credential was successfully processed and authenticated, else 'false'.
 */
private fun authenticate(credential: Credential): Boolean {
    when (credential) {
        is PublicKeyCredential -> {
            return authenticationServer.loginWithPasskey(credential.authenticationResponseJson)
        }

        is PasswordCredential -> {
            return authenticationServer.loginWithPassword(
                credential.id,
                credential.password,
            )
        }

        is CustomCredential -> {
            return authenticationServer.loginWithCustomCredential(
                credential.type,
                credential.data,
            )
        }

        else -> {
            return false
        }
    }
}
}


/** Dummy authentication server would make network calls to your authentication server.*/
class AuthenticationServer {

    /** Retrieves the public key credential request options from the authentication server.*/
    internal fun getPublicKeyRequestOptions(): String { return "result of network call" }

    fun loginWithPasskey(passkeyResponseJSON: String): Boolean { return true }

    fun loginWithPassword(username: String, password: String): Boolean { return true }

    fun loginWithCustomCredential(type: String, data: Bundle) : Boolean { return true }

}

/** Dummy navigation function. */
fun navigateToSecondaryAuthentication() {
}
