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

import okhttp3.Cookie
import okhttp3.CookieJar
import okhttp3.HttpUrl
import okhttp3.OkHttpClient

// [START android_security_network_scoped_credentials]
class ScopedCookieJar : CookieJar {
    private val cookies = mutableListOf<Cookie>()

    @Synchronized
    override fun saveFromResponse(url: HttpUrl, cookies: List<Cookie>) {
        for (cookie in cookies) {
            this.cookies.removeAll { it.name == cookie.name && it.domain == cookie.domain && it.path == cookie.path }
            this.cookies += cookie
        }
    }

    @Synchronized
    override fun loadForRequest(url: HttpUrl): List<Cookie> {
        cookies.removeAll { it.expiresAt < System.currentTimeMillis() }
        return cookies.filter { it.matches(url) }
    }
}

fun createPartnerClient(partnerHost: String, apiKey: String, basicCredentials: () -> String): OkHttpClient =
    OkHttpClient.Builder()
        .cookieJar(ScopedCookieJar())
        .followSslRedirects(false)
        // Network interceptors also run for every redirect hop.
        .addNetworkInterceptor { chain ->
            val request = chain.request()
            if (request.url.isHttps && request.url.host == partnerHost) {
                chain.proceed(request.newBuilder().header("X-Api-Key", apiKey).build())
            } else {
                chain.proceed(request)
            }
        }
        .authenticator { _, response ->
            val url = response.request.url
            if (!url.isHttps || url.host != partnerHost || response.request.header("Authorization") != null) {
                null
            } else {
                response.request.newBuilder().header("Authorization", basicCredentials()).build()
            }
        }
        .build()
// [END android_security_network_scoped_credentials]
