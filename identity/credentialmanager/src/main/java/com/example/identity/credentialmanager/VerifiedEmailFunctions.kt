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
import android.util.Base64
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.DigitalCredential
import androidx.credentials.ExperimentalDigitalCredentialApi
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetDigitalCredentialOption
import java.security.SecureRandom
import org.json.JSONObject

/**
 * A utility class for handling interactions with the Credential Manager for verified email
 * credentials. This class provides functions to request verified user information and create
 * accounts using these credentials.
 *
 * @param credentialManager The [CredentialManager] instance to use for credential operations.
 */
class VerifiedEmailFunctions(
    private val credentialManager: CredentialManager,
) {

    /**
     * Constructs an OpenID4VP request for a UserInfoCredential, sends it to the
     * CredentialManager, and extracts the verified email and user profile claims
     * from the returned SD-JWT.
     *
     * @param activity The current Activity context.
     */
    @OptIn(ExperimentalDigitalCredentialApi::class)
    suspend fun getVerifiedUserInfo(activity: Activity) {
        // [START android_identity_get_verified_user_info_request]
val nonce = generateSecureRandomNonce()

// This request follows the OpenID4VP spec
 val openId4vpRequest = """
    {
      "requests": [
        {
          "protocol": "openid4vp-v1-unsigned",
          "data": {
            "response_type": "vp_token",
            "response_mode": "dc_api",
            "nonce": "$nonce",
            "dcql_query": {
              "credentials": [
                {
                  "id": "user_info_query",
                  "format": "dc+sd-jwt",
                   "meta": { 
                      "vct_values": ["UserInfoCredential"] 
                   },
                  "claims": [ 
                    {"path": ["email"]}, 
                    {"path": ["name"]},  
                    {"path": ["given_name"]},
                    {"path": ["family_name"]},
                    {"path": ["picture"]},
                    {"path": ["hd"]},
                    {"path": ["email_verified"]}
                  ]
                }
              ]
            }
          }
        }
      ]
    }
    """

val getDigitalCredentialOption = GetDigitalCredentialOption(requestJson = openId4vpRequest)
val request = GetCredentialRequest(listOf(getDigitalCredentialOption))
        // [END android_identity_get_verified_user_info_request]

        // [START android_identity_get_verified_user_info_response]
        try {
            // Requesting Digital Credential from user...
            val result = credentialManager.getCredential(activity, request)

            when (val credential = result.credential) {
                is DigitalCredential -> {
                    val responseJsonString = credential.credentialJson

                    // Successfully received digital credential response.

                    // Next, parse this response and send it to your server.
                    // [START_EXCLUDE]
                    // 1. Parse the outer JSON wrapper to get the `vp_token`
                    val responseData = JSONObject(responseJsonString)
                    val vpToken = responseData.getJSONObject("vp_token")

                    // 2. Extract the raw SD-JWT string
                    val credentialId = vpToken.keys().next()
                    val rawSdJwt = vpToken.getJSONArray(credentialId).getString(0)

                    // 3. Use your parser to get the verified claims.
                    // Note: You would have to first split the SD-JWT and "kb" portions
                    // and then parse the SD-JWT portion using either a local implementation // or a library.
                    // Server-side validation/parsing is highly recommended.
                    val claims = SdJwtParser.parse(rawSdJwt)
                    Log.d("TAG", "Parsed Claims: ${claims.toString(2)}")

                    // 4. Create your VerifiedUserInfo object with REAL data
                    val userInfo = VerifiedUserInfo(
                        email = claims.getString("email"),
                        displayName = claims.optString("name", claims.getString("email"))
                    )
                    // handle response - Up to the developer
                    // [END_EXCLUDE]
                }

                else -> {
                    // handle Unexpected State() - Up to the developer
                }
            }
        } catch (e: Exception) {
            // handle exceptions - Up to the developer
        }
        // [END android_identity_get_verified_user_info_response]
    }

    /**
     * Parses the digital credential response to extract verified user information.
     *
     * @param responseJsonString The raw digital credential response JSON.
     */
    fun parseVerifiedUserInfoResponse(responseJsonString: String) {
        // [START android_identity_parse_response]
        // 1. Parse the outer JSON wrapper to get the `vp_token`
        val responseData = JSONObject(responseJsonString)
        val vpToken = responseData.getJSONObject("vp_token")

        // 2. Extract the raw SD-JWT string
        val credentialId = vpToken.keys().next()
        val rawSdJwt = vpToken.getJSONArray(credentialId).getString(0)

        // 3. Use your parser to get the verified claims
        // Server-side validation/parsing is highly recommended.

        // Assumes a local parser like the one in our SdJwtParser.kt sample
        val claims = SdJwtParser.parse(rawSdJwt)
        Log.d("TAG", "Parsed Claims: ${claims.toString(2)}")

        // 4. Create your VerifiedUserInfo object with REAL data
        val userInfo = VerifiedUserInfo(
            email = claims.getString("email"),
            displayName = claims.optString("name", claims.getString("email"))
        )
        // [END android_identity_parse_response]
    }

    /**
     * Sends the raw digital credential response to the Relying Party server
     * for final cryptographic validation and subsequent account creation.
     *
     * @param responseJsonString The raw digital credential response JSON.
     * @param nonce The original nonce used in the OpenID4VP request.
     */
    fun createAccountWithVerifiedEmail(responseJsonString: String, nonce: String) {
        // [START android_identity_create_account_verified_email_client]
        try {
            // Send the raw credential response and the original nonce to your server.
            // Your server must validate the response. createAccountWithVerifiedCredentials
            // is a custom implementation per each RP for server side verification and account creation.
            val serverResponse = createAccountWithVerifiedCredentials(responseJsonString, nonce)

            // Server returns the new account info (e.g., email, name)
            val claims = JSONObject(serverResponse.json)

            val userInfo = VerifiedUserInfo(
                email = claims.getString("email"),
                displayName = claims.optString("name", claims.getString("email"))
            )

            // handle response - Up to the developer
        } catch (e: Exception) {
            // handle exceptions - Up to the developer
        }
        // [END android_identity_create_account_verified_email_client]
    }

    // [START android_identity_response_and_claims_example]
    /*
    // Example of the raw JSON response from credential.credentialJson:
    {
      "vp_token": {
        // This key matches the 'id' you set in your dcql_query
        "user_info_query": [
          // The SD-JWT string (Issuer JWT ~ Disclosures ~ Key Binding JWT)
          "eyJhbGciOiJ...~WyI...IiwgImVtYWlsIiwgInVzZXJAZXhhbXBsZS5jb20iXQ~...~eyJhbGciOiJ..."
        ]
      }
    }

    // Example of the parsed and verified claims from the SD-JWT on your server:
    {
      "cnf": {
        "jwk": {..}
      },
      "exp": 1775688222,
      "iat": 1775083422,
      "iss": "https://verifiablecredentials-pa.googleapis.com",
      "vct": "UserInfoCredential",
      "email": "jane.doe.246745@gmail.com",
      "email_verified": true,
      "given_name": "Jane",
      "family_name": "Doe",
      "name": "Jane Doe",
      "picture": "http://example.com/janedoe/me.jpg",
      "hd": ""
    }
     */
// [END android_identity_response_and_claims_example]

    /**
     * A placeholder function for server-side validation of the digital credential response.
     *
     * @param responseJsonString The raw digital credential response JSON.
     * @param nonce The original nonce used in the OpenID4VP request.
     * @return A placeholder server response.
     */
    private fun createAccountWithVerifiedCredentials(responseJsonString: String, nonce: String): ServerResponse {
        // Send the raw credential response and the original nonce to your server.
        // Your server must validate the response. This is a placeholder for the server-side logic.
        // In a real implementation, this would involve cryptographic verification of the SD-JWT
        // and checking the nonce to prevent replay attacks.
        val serverResponse = ServerResponse("""{"email": "...", "name": "..."}""")
        return serverResponse
    }

    // Placeholder for generating nonce
    fun generateSecureRandomNonce(byteLength: Int = 32): String {
        val randomBytes = ByteArray(byteLength)
        SecureRandom().nextBytes(randomBytes)
        return Base64.encodeToString(
            randomBytes,
            Base64.NO_WRAP or Base64.URL_SAFE or Base64.NO_PADDING
        )
    }
}

// Placeholder for a data class that would typically be defined elsewhere
data class VerifiedUserInfo(val email: String, val displayName: String)

// Placeholder for a server response class
data class ServerResponse(val json: String)

// Placeholder for a class that would parse the SD-JWT
object SdJwtParser {
    fun parse(sdJwt: String): JSONObject {
        // In a real implementation, this would parse the SD-JWT and return the claims.
        // For this example, we'll return a dummy JSON object.
        return JSONObject().apply {
            put("email", "example@example.com")
            put("name", "Example User")
        }
    }
}
