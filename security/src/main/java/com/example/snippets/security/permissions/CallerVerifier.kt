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

package com.example.snippets.security.permissions

import android.content.Context
import androidx.security.app.authenticator.AppAuthenticator
import com.example.snippets.security.R

// [START android_security_caller_verifier_app_authenticator]
class CallerVerifier(private val context: Context) {
    private val appAuthenticator: AppAuthenticator by lazy {
        AppAuthenticator.createFromResource(context, R.xml.allowed_peers)
    }

    /**
     * Verifies the caller's identity.
     *
     * @param callingPackage The package name of the caller.
     * @throws SecurityException If the caller is not authorized.
     */
    fun enforceCaller(callingPackage: String) {
        try {
            appAuthenticator.enforceAppIdentity(callingPackage)
        } catch (e: SecurityException) {
            throw SecurityException("Caller $callingPackage is not authorized", e)
        }
    }
}
// [END android_security_caller_verifier_app_authenticator]
