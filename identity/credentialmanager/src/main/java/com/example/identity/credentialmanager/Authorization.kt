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

import android.accounts.Account
import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.google.android.gms.auth.api.identity.AuthorizationRequest
import com.google.android.gms.auth.api.identity.AuthorizationResult
import com.google.android.gms.auth.api.identity.ClearTokenRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.RevokeAccessRequest
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.Scope
import com.google.api.services.drive.DriveScopes

private object AuthorizationExamples {
    private const val TAG = "Authorization"

    fun requestPermissions(
        activity: Activity,
        startAuthorizationIntent: ActivityResultLauncher<IntentSenderRequest>,
    ) {
        // [START android_identity_authorization_request_permissions]
        val requestedScopes: List<Scope> = listOf(Scope(DriveScopes.DRIVE_FILE))
        val authorizationRequest = AuthorizationRequest.builder()
            .setRequestedScopes(requestedScopes)
            .build()

        Identity.getAuthorizationClient(activity)
            .authorize(authorizationRequest)
            .addOnSuccessListener { authorizationResult ->
                if (authorizationResult.hasResolution()) {
                    val pendingIntent = authorizationResult.pendingIntent
                    // Access needs to be granted by the user.
                    startAuthorizationIntent.launch(IntentSenderRequest.Builder(pendingIntent!!.intentSender).build())
                } else {
                    // Access was previously granted, continue with user action.
                    saveToDriveAppFolder(authorizationResult)
                }
            }
            .addOnFailureListener { e -> Log.e(TAG, "Failed to authorize", e) }
        // [END android_identity_authorization_request_permissions]
    }

    fun revokePermissions(activity: Activity, account: Account) {
        // [START android_identity_authorization_revoke_access]
        val requestedScopes: MutableList<Scope> = mutableListOf(Scope(DriveScopes.DRIVE_FILE))
        val revokeAccessRequest = RevokeAccessRequest.builder()
            .setAccount(account)
            .setScopes(requestedScopes)
            .build()

        Identity.getAuthorizationClient(activity)
            .revokeAccess(revokeAccessRequest)
            .addOnSuccessListener { Log.i(TAG, "Successfully revoked access") }
            .addOnFailureListener { e -> Log.e(TAG, "Failed to revoke access", e) }
        // [END android_identity_authorization_revoke_access]
    }

    fun clearTokenCache(activity: Activity, invalidAccessToken: String) {
        // [START android_identity_authorization_clear_token]
        Identity.getAuthorizationClient(activity)
            .clearToken(ClearTokenRequest.builder().setToken(invalidAccessToken).build())
            .addOnSuccessListener { Log.i(TAG, "Successfully removed the token from the cache") }
            .addOnFailureListener { e -> Log.e(TAG, "Failed to clear token", e) }
        // [END android_identity_authorization_clear_token]
    }

    fun requestOfflineAccess(
        activity: Activity,
        requestedScopes: List<Scope>,
        serverClientId: String,
        startAuthorizationIntent: ActivityResultLauncher<IntentSenderRequest>,
    ) {
        // [START android_identity_authorization_request_offline_access]
        // Ask for offline access during the first authorization request.
        val authorizationRequest = AuthorizationRequest.builder()
            .setRequestedScopes(requestedScopes)
            .requestOfflineAccess(serverClientId)
            .build()

        Identity.getAuthorizationClient(activity)
            .authorize(authorizationRequest)
            .addOnSuccessListener { authorizationResult ->
                startAuthorizationIntent.launch(
                    IntentSenderRequest.Builder(
                        authorizationResult.pendingIntent!!.intentSender
                    ).build()
                )
            }
            .addOnFailureListener { e -> Log.e(TAG, "Failed to authorize", e) }
        // [END android_identity_authorization_request_offline_access]
    }
}

private class AuthorizationResultFragment : Fragment() {
    // [START android_identity_authorization_handle_result]
    private lateinit var startAuthorizationIntent: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // ...
        startAuthorizationIntent =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
                try {
                    // extract the result.
                    val authorizationResult = Identity.getAuthorizationClient(requireContext())
                        .getAuthorizationResultFromIntent(activityResult.data)
                    // continue with user action.
                    saveToDriveAppFolder(authorizationResult)
                } catch (e: ApiException) {
                    // log exception.
                }
            }
        // [START_EXCLUDE silent]
        return super.onCreateView(inflater, container, savedInstanceState)
        // [END_EXCLUDE]
    }
    // [END android_identity_authorization_handle_result]
}

private class OfflineAccessResultFragment : Fragment() {
    private var accessToken: String? = null

    // [START android_identity_authorization_handle_offline_result]
    private lateinit var startAuthorizationIntent: ActivityResultLauncher<IntentSenderRequest>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        // ...
        startAuthorizationIntent =
            registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { activityResult ->
                try {
                    val authorizationResult = Identity.getAuthorizationClient(requireContext())
                        .getAuthorizationResultFromIntent(activityResult.data)
                    // short-lived access token.
                    accessToken = authorizationResult.accessToken
                    // store the authorization code used for getting a refresh token safely to your app's backend server.
                    val authCode: String? = authorizationResult.serverAuthCode
                    storeAuthCodeSafely(authCode)
                } catch (e: ApiException) {
                    // log exception.
                }
            }
        // [START_EXCLUDE silent]
        return super.onCreateView(inflater, container, savedInstanceState)
        // [END_EXCLUDE]
    }
    // [END android_identity_authorization_handle_offline_result]
}

private fun saveToDriveAppFolder(authorizationResult: AuthorizationResult) {}

private fun storeAuthCodeSafely(authCode: String?) {}
