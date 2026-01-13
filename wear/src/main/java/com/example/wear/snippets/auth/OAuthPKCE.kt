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

import android.content.Context
import android.net.Uri
import androidx.wear.phone.interactions.authentication.CodeChallenge
import androidx.wear.phone.interactions.authentication.CodeVerifier
import androidx.wear.phone.interactions.authentication.OAuthRequest
import androidx.wear.phone.interactions.authentication.OAuthResponse
import androidx.wear.phone.interactions.authentication.RemoteAuthClient
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

private const val CLIENT_ID = "my_fake_client_id"
private const val GOOGLE_OAUTH_BACKEND = "https://accounts.google.com/o/oauth2/v2/auth"
private const val GOOGLE_USER_INFO_SCOPE = "https://www.googleapis.com/auth/userinfo.profile"

class WearOAuthPKCEManager(private val context: Context) {

    /** Start the authentication flow. */
    suspend fun startAuthFlow() {
        val codeChallenge = CodeChallenge(CodeVerifier())

        // Create the authorization Uri that will be shown to the user on the phone. This will
        // be different depending on the OAuth backend your app uses, we use the google backend.
        val uri = Uri.Builder()
            .encodedPath(GOOGLE_OAUTH_BACKEND)
            .appendQueryParameter("scope", GOOGLE_USER_INFO_SCOPE)
            .build()

        // [START android_wear_auth_oauth_pkce_create_request]
        val oauthRequest = OAuthRequest.Builder(context)
            .setAuthProviderUrl(uri)
            .setCodeChallenge(codeChallenge)
            .setClientId(CLIENT_ID)
            .build()
        // [END android_wear_auth_oauth_pkce_create_request]

        // 1. Retrieve oauth code
        val code = retrieveOAuthCode(oauthRequest, context).getOrElse {}

        // 2. Retrieve auth token from your backend.
        val token = retrieveToken(code as String, oauthRequest).getOrElse {}

        // 3. Request user profile from your backend
        retrieveUserProfile(token as String).getOrElse {}
    }

    /**
     * Use the [RemoteAuthClient] class to authorize the user. The library will handle the
     * communication with the paired device, where the user can log in.
     */
    private suspend fun retrieveOAuthCode(
        oauthRequest: OAuthRequest,
        context: Context
    ): Result<String> {
        return suspendCoroutine { continuation ->
            // [START android_wear_auth_oauth_pkce_send_request]
            RemoteAuthClient.create(context).sendAuthorizationRequest(
                request = oauthRequest,
                executor = { command -> command?.run() },
                clientCallback = object : RemoteAuthClient.Callback() {
                    override fun onAuthorizationResponse(
                        request: OAuthRequest,
                        response: OAuthResponse
                    ) {
                        // Extract the token from the response, store it, and use it in requests.
                        continuation.resume(parseCodeFromResponse(response))
                    }
                    override fun onAuthorizationError(request: OAuthRequest, errorCode: Int) {
                        // Handle Errors
                        continuation.resume(Result.failure(IOException("Authorization failed")))
                    }
                }
            )
            // [END android_wear_auth_oauth_pkce_send_request]
        }
    }

    private fun parseCodeFromResponse(response: OAuthResponse): Result<String> {
        val responseUrl = response.responseUrl
        val code = responseUrl?.getQueryParameter("code")
        return if (code.isNullOrBlank()) {
            Result.failure(IOException("Authorization failed"))
        } else {
            Result.success(code)
        }
    }

    /** placeholder token retrieval function. */
    private fun retrieveToken(code: String, oauthRequest: OAuthRequest): Result<String> {
        return Result.success("placeholderToken")
    }

    /** placeholder user profile retrieval. */
    private fun retrieveUserProfile(token: String): Result<String> {
        return Result.success("placeholderProfile")
    }
}
