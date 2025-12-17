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
import android.content.Intent
import android.net.Uri
import androidx.wear.remote.interactions.RemoteActivityHelper

class DeviceGrantManager(private val context: Context) {

    /** Executes the full Device Grant flow. */
    suspend fun startAuthFlow(): Result<String> {
        // 1: Get device info from server
        val deviceVerificationInfo = getFakeVerificationInfo()

        // 2: Show user code UI and open URL on phone
        verifyDeviceAuthGrant(deviceVerificationInfo.verificationUri)

        // 3: Fetch for the DAG token
        val token = fetchToken(deviceVerificationInfo.deviceCode)

        // Step 3: Use token to get profile
        return retrieveUserProfile(token)
    }

    // The response data when retrieving the verification
    data class VerificationInfo(
        val verificationUri: String,
        val userCode: String,
        val deviceCode: String,
    )

    /* A fake server call to retrieve */
    private fun getFakeVerificationInfo(): VerificationInfo {
        // An example of a verificationURI w
        return VerificationInfo(
            "your client backend",
            userCode = "dummyUser",
            deviceCode = "myDeviceCode",
        )
    }

    // [START android_wear_auth_oauth_dag_authorize_device]
    // Request access from the authorization server and receive Device Authorization Response.
    private fun verifyDeviceAuthGrant(verificationUri: String) {
        RemoteActivityHelper(context).startRemoteActivity(
            Intent(Intent.ACTION_VIEW).apply {
                addCategory(Intent.CATEGORY_BROWSABLE)
                data = Uri.parse(verificationUri)
            },
            null
        )
    }
    // [END android_wear_auth_oauth_dag_authorize_device]

    private fun fetchToken(deviceCode: String): Result<String> {
        return Result.success("dummyToken")
    }

    private fun retrieveUserProfile(token: Result<String>): Result<String> {
        return Result.success("dummyProfile")
    }
}
