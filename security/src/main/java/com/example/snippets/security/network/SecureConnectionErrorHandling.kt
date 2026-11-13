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

import android.util.Log
import java.io.IOException
import java.net.URL
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLHandshakeException
import javax.net.ssl.SSLPeerUnverifiedException
import javax.net.ssl.SSLProtocolException

private fun showErrorToUser(message: String) {
    Log.i("NETWORK_UI", message)
}

// [START android_security_network_tls_error_handling]
fun connectToSecureServer(onUrlString: String) {
    try {
        val url = URL(onUrlString)
        val conn = url.openConnection() as HttpsURLConnection

        // Timeout configurations to prevent hanging connections
        conn.connectTimeout = 5000
        conn.readTimeout = 5000

        conn.connect()
    } catch (e: SSLPeerUnverifiedException) {
        // Triggered by a hostname mismatch or an OkHttp CertificatePinner mismatch
        Log.w("SECURITY_HANDSHAKE_FAIL", "Peer verification failed. Host identity unconfirmed.", e)
        // Terminate request immediately; do not retry with fallback HTTP or without pins
        showErrorToUser("Connection insecure. Please try again later.")
    } catch (e: SSLHandshakeException) {
        // Triggered by untrusted CAs, expired certificates, protocol mismatch, or a
        // pin-set mismatch in the network security config
        Log.e("SECURITY_HANDSHAKE_FAIL", "SSL Handshake failed", e)
        showErrorToUser("Secure connection failed. Please check your network and device time.")
    } catch (e: SSLProtocolException) {
        Log.e("SECURITY_HANDSHAKE_FAIL", "Secure protocol failure", e)
        showErrorToUser("A connection error occurred. Please try again.")
    } catch (e: IOException) {
        Log.e("NETWORK_ERROR", "General communication failure", e)
        showErrorToUser("Network error. Please try again.")
    }
}
// [END android_security_network_tls_error_handling]
