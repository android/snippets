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

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.GetCredentialException
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class CredentialManagerFunctions (
  context: Context,
) {
  // [START android_identity_initialize_credman]
  // Use your app or activity context to instantiate a client instance of
  // CredentialManager.
  private val credentialManager = CredentialManager.create(context)
  // [END android_identity_initialize_credman]

  // Placeholder for TAG log value.
  val TAG = ""
  /**
   * Retrieves a passkey from the credential manager.
   *
   * @param creationResult The result of the passkey creation operation.
   * @param context The activity context from the Composable, to be used in Credential Manager APIs
   * @return The [GetCredentialResponse] object containing the passkey, or null if an error occurred.
   */
  @RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
  fun signInFlow(
    creationResult: JSONObject,
    activityContext: Context,
  ) {
    val requestJson = creationResult.toString()
    // [START android_identity_get_password_passkey_options]
    // Retrieves the user's saved password for your app from their
    // password provider.
    val getPasswordOption = GetPasswordOption()

    // Get passkey from the user's public key credential provider.
    val getPublicKeyCredentialOption = GetPublicKeyCredentialOption(
      requestJson = requestJson
    )
    // [END android_identity_get_password_passkey_options]
    var result: GetCredentialResponse
    // [START android_identity_get_credential_request]
    val credentialRequest = GetCredentialRequest(
      listOf(getPasswordOption, getPublicKeyCredentialOption),
    )
    // [END android_identity_get_credential_request]
    runBlocking {
      // getPrepareCredential request
      // [START android_identity_prepare_get_credential]
      coroutineScope {
        val response = credentialManager.prepareGetCredential(
          GetCredentialRequest(
            listOf(
              getPublicKeyCredentialOption,
              getPasswordOption
            )
          )
        )
      }
      // [END android_identity_prepare_get_credential]
      // getCredential request without handling exception.
      // [START android_identity_launch_sign_in_flow_1]
      coroutineScope {
        try {
          result = credentialManager.getCredential(
            // Use an activity-based context to avoid undefined system UI
            // launching behavior.
            context = activityContext,
            request = credentialRequest
          )
          handleSignIn(result)
        } catch (e: GetCredentialException) {
          // Handle failure
        }
      }
      // [END android_identity_launch_sign_in_flow_1]
      // getCredential request adding some exception handling.
      // [START android_identity_handle_exceptions_no_credential]
      coroutineScope {
        try {
          result = credentialManager.getCredential(
            context = activityContext,
            request = credentialRequest
          )
        } catch (e: GetCredentialException) {
          Log.e("CredentialManager", "No credential available", e)
        }
      }
      // [END android_identity_handle_exceptions_no_credential]
    }
  }

  // [START android_identity_launch_sign_in_flow_2]
  fun handleSignIn(result: GetCredentialResponse) {
    // Handle the successfully returned credential.
    val credential = result.credential

    when (credential) {
      is PublicKeyCredential -> {
        val responseJson = credential.authenticationResponseJson
        // Share responseJson i.e. a GetCredentialResponse on your server to
        // validate and  authenticate
      }

      is PasswordCredential -> {
        val username = credential.id
        val password = credential.password
        // Use id and password to send to your server to validate
        // and authenticate
      }

      is CustomCredential -> {
        // If you are also using any external sign-in libraries, parse them
        // here with the utility functions provided.
        if (credential.type == ExampleCustomCredential.TYPE) {
          try {
            val ExampleCustomCredential =
              ExampleCustomCredential.createFrom(credential.data)
            // Extract the required credentials and complete the authentication as per
            // the federated sign in or any external sign in library flow
          } catch (e: ExampleCustomCredential.ExampleCustomCredentialParsingException) {
            // Unlikely to happen. If it does, you likely need to update the dependency
            // version of your external sign-in library.
            Log.e(TAG, "Failed to parse an ExampleCustomCredential", e)
          }
        } else {
          // Catch any unrecognized custom credential type here.
          Log.e(TAG, "Unexpected type of credential")
        }
      }
      else -> {
        // Catch any unrecognized credential type here.
        Log.e(TAG, "Unexpected type of credential")
      }
    }
  }
  // [END android_identity_launch_sign_in_flow_2]
}

sealed class ExampleCustomCredential {
  class ExampleCustomCredentialParsingException : Throwable() {}

  companion object {
    fun createFrom(data: Bundle): PublicKeyCredential {
      return PublicKeyCredential("")
    }

    const val TYPE: String = ""
  }
}