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

package com.example.snippets.security.network

import com.example.snippets.security.BuildConfig
import okhttp3.CertificatePinner
import okhttp3.ConnectionSpec
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.logging.HttpLoggingInterceptor

// [START android_security_network_okhttp_pinning]
fun createSecureOkHttpClient(onHostname: String, onPrimaryPin: String, onBackupPin: String): OkHttpClient {
    // 1. Build CertificatePinner with fallback pins
    val certificatePinner = CertificatePinner.Builder()
        .add(onHostname, "sha256/$onPrimaryPin")
        .add(onHostname, "sha256/$onBackupPin")
        .build()

    // 2. Attach the pinner to the OkHttp client. Do not call
    //    sslSocketFactory(...) or hostnameVerifier(...): the defaults validate
    //    the chain against system CAs and verify the hostname.
    return OkHttpClient.Builder()
        .connectionSpecs(listOf(ConnectionSpec.MODERN_TLS))
        .certificatePinner(certificatePinner)
        .build()
}
// [END android_security_network_okhttp_pinning]

fun buildAuthorizedRequest(orderId: String, accessToken: String): Pair<HttpLoggingInterceptor, Request> {
    // [START android_security_network_okhttp_logging_auth]
    val logging = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC
        else HttpLoggingInterceptor.Level.NONE
        redactHeader("Authorization")
    }

    val request = Request.Builder()
        .url("https://api.example.com/v1/orders/$orderId")
        .header("Authorization", "Bearer $accessToken")
        .build()
    // [END android_security_network_okhttp_logging_auth]
    return logging to request
}
