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

import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLPeerUnverifiedException
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

// [START android_security_network_verified_tls_socket]
fun openVerifiedTlsSocket(host: String, port: Int): SSLSocket {
    val factory = SSLSocketFactory.getDefault() as SSLSocketFactory
    val socket = factory.createSocket(host, port) as SSLSocket
    try {
        socket.enabledProtocols = socket.supportedProtocols
            .filter { it == "TLSv1.3" || it == "TLSv1.2" }
            .toTypedArray()
        socket.startHandshake()
        val verifier = HttpsURLConnection.getDefaultHostnameVerifier()
        if (!verifier.verify(host, socket.session)) {
            throw SSLPeerUnverifiedException("Expected $host, got ${socket.session.peerPrincipal}")
        }
        return socket
    } catch (e: Exception) {
        socket.close()
        throw e
    }
}
// [END android_security_network_verified_tls_socket]
