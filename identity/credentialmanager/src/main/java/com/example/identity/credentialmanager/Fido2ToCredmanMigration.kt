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

import android.app.Activity
import android.content.Context
import android.util.JsonWriter
import android.util.Log
import android.widget.Toast
import androidx.credentials.CreateCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialException
import com.example.identity.credentialmanager.ApiResult.Success
import java.io.StringWriter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request.Builder
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import org.json.JSONObject
import ru.gildor.coroutines.okhttp.await

class Fido2ToCredmanMigration(
    private val context: Context,
    private val client: OkHttpClient,
) {
    private val BASE_URL = ""
    private val JSON = "".toMediaTypeOrNull()
    private val PUBLIC_KEY = ""

    // [START android_identity_fido2_credman_init]
    val credMan = CredentialManager.create(context)
    // [END android_identity_fido2_credman_init]

    // [START android_identity_fido2_migration_post_request_body]
    suspend fun registerRequest() {
        // ...
        val call = client.newCall(
            Builder()
                .method(
                    "POST",
                    jsonRequestBody {
                        name("attestation").value("none")
                        name("authenticatorSelection").objectValue {
                            name("residentKey").value("required")
                        }
                    }
                ).build()
        )
        // ...
    }
    // [END android_identity_fido2_migration_post_request_body]

    // [START android_identity_fido2_migration_register_request]
    suspend fun registerRequest(sessionId: String): ApiResult<JSONObject> {
        val call = client.newCall(
            Builder()
                .url("$BASE_URL/<your api url>")
                .addHeader("Cookie", formatCookie(sessionId))
                .method(
                    "POST",
                    jsonRequestBody {
                        name("attestation").value("none")
                        name("authenticatorSelection").objectValue {
                            name("authenticatorAttachment").value("platform")
                            name("userVerification").value("required")
                            name("residentKey").value("required")
                        }
                    }
                ).build()
        )
        val response = call.await()
        return response.result("Error calling the api") {
            parsePublicKeyCredentialCreationOptions(
                body ?: throw ApiException("Empty response from the api call")
            )
        }
    }
    // [END android_identity_fido2_migration_register_request]

    // [START android_identity_fido2_migration_create_passkey]
    suspend fun createPasskey(
        activity: Activity,
        requestResult: JSONObject
    ): CreatePublicKeyCredentialResponse? {
        val request = CreatePublicKeyCredentialRequest(requestResult.toString())
        var response: CreatePublicKeyCredentialResponse? = null
        try {
            response = credMan.createCredential(
                request = request as CreateCredentialRequest,
                context = activity
            ) as CreatePublicKeyCredentialResponse
        } catch (e: CreateCredentialException) {

            showErrorAlert(activity, e)

            return null
        }
        return response
    }
    // [END android_identity_fido2_migration_create_passkey]

    // [START android_identity_fido2_migration_auth_with_passkeys]
    /**
     * @param sessionId The session ID to be used for the sign-in.
     * @param credentialId The credential ID of this device.
     * @return a JSON object.
     */
    suspend fun signinRequest(): ApiResult<JSONObject> {
        val call = client.newCall(
            Builder().url(
                buildString {
                    append("$BASE_URL/signinRequest")
                }
            ).method("POST", jsonRequestBody {})
                .build()
        )
        val response = call.await()
        return response.result("Error calling /signinRequest") {
            parsePublicKeyCredentialRequestOptions(
                body ?: throw ApiException("Empty response from /signinRequest")
            )
        }
    }

    /**
     * @param sessionId The session ID to be used for the sign-in.
     * @param response The JSONObject for signInResponse.
     * @param credentialId id/rawId.
     * @return A list of all the credentials registered on the server,
     * including the newly-registered one.
     */
    suspend fun signinResponse(
        sessionId: String,
        response: JSONObject,
        credentialId: String
    ): ApiResult<Unit> {

        val call = client.newCall(
            Builder().url("$BASE_URL/signinResponse")
                .addHeader("Cookie", formatCookie(sessionId))
                .method(
                    "POST",
                    jsonRequestBody {
                        name("id").value(credentialId)
                        name("type").value(PUBLIC_KEY.toString())
                        name("rawId").value(credentialId)
                        name("response").objectValue {
                            name("clientDataJSON").value(
                                response.getString("clientDataJSON")
                            )
                            name("authenticatorData").value(
                                response.getString("authenticatorData")
                            )
                            name("signature").value(
                                response.getString("signature")
                            )
                            name("userHandle").value(
                                response.getString("userHandle")
                            )
                        }
                    }
                ).build()
        )
        val apiResponse = call.await()
        return apiResponse.result("Error calling /signingResponse") {
        }
    }
    // [END android_identity_fido2_migration_auth_with_passkeys]

    // [START android_identity_fido2_migration_get_passkeys]
    suspend fun getPasskey(
        activity: Activity,
        creationResult: JSONObject
    ): GetCredentialResponse? {
        Toast.makeText(
            activity,
            "Fetching previously stored credentials",
            Toast.LENGTH_SHORT
        )
            .show()
        var result: GetCredentialResponse? = null
        try {
            val request = GetCredentialRequest(
                listOf(
                    GetPublicKeyCredentialOption(
                        creationResult.toString(),
                        null
                    ),
                    GetPasswordOption()
                )
            )
            result = credMan.getCredential(activity, request)
            if (result.credential is PublicKeyCredential) {
                val publicKeycredential = result.credential as PublicKeyCredential
                Log.i("TAG", "Passkey ${publicKeycredential.authenticationResponseJson}")
                return result
            }
        } catch (e: Exception) {
            showErrorAlert(activity, e)
        }
        return result
    }
    // [END android_identity_fido2_migration_get_passkeys]

    private fun showErrorAlert(
        activity: Activity,
        e: Exception
    ) {}

    private fun jsonRequestBody(body: JsonWriter.() -> Unit): RequestBody {
        val output = StringWriter()
        JsonWriter(output).use { writer ->
            writer.beginObject()
            writer.body()
            writer.endObject()
        }
        return output.toString().toRequestBody(JSON)
    }

    private fun JsonWriter.objectValue(body: JsonWriter.() -> Unit) {
        beginObject()
        body()
        endObject()
    }

    private fun formatCookie(sessionId: String): String {
        return ""
    }

    private fun parsePublicKeyCredentialCreationOptions(body: ResponseBody): JSONObject {
        return JSONObject()
    }

    private fun parsePublicKeyCredentialRequestOptions(body: ResponseBody): JSONObject {
        return JSONObject()
    }

    private fun <T> Response.result(errorMessage: String, data: Response.() -> T): ApiResult<T> {
        return Success()
    }
}

sealed class ApiResult<out R> {
    class Success<T> : ApiResult<T>()
}

class ApiException(message: String) : RuntimeException(message)
