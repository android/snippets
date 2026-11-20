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

package com.example.snippets.security.privacy

import android.util.Log

private const val TAG = "LogInfoDisclosure"

private object SanitizeCredentialSnippet {
    // [START android_security_privacy_log_sanitize_credential]
    data class Credential<T>(val data: String) {
        /** Returns a redacted value to avoid accidental inclusion in logs. */
        override fun toString() = "Credential XX"
    }

    fun checkNoMatches(list: List<Any>) {
        if (!list.isEmpty()) {
            Log.e(TAG, String.format("Expected empty list, but was %s", list))
        }
    }
    // [END android_security_privacy_log_sanitize_credential]
}

private object RedactLogsSnippet {
    // [START android_security_privacy_log_redact_mask]
    data class ToMask<T>(private val data: T) {
        // Prevents accidental logging when an error is encountered.
        override fun toString() = "XX"

        // Makes it more difficult for developers to invoke sensitive data
        // and facilitates sensitive data usage tracking.
        fun getDataToMask(): T = data
    }

    data class Person(
        val email: ToMask<String>,
        val username: String
    )

    fun main() {
        val person = Person(
            ToMask("name@gmail.com"),
            "myname"
        )
        println(person)
        println(person.email.getDataToMask())
    }
    // [END android_security_privacy_log_redact_mask]
}
