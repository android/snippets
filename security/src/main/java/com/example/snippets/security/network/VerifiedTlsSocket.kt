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

import java.net.InetSocketAddress
import java.net.Socket
import javax.net.ssl.HttpsURLConnection
import javax.net.ssl.SSLPeerUnverifiedException
import javax.net.ssl.SSLSocket
import javax.net.ssl.SSLSocketFactory

// [START android_security_network_verified_tls_socket]
fun openVerifiedTlsSocket(host: String, port: Int, timeoutMillis: Int = 10_000): SSLSocket {
    // Connect with a timeout first, then layer TLS on top. The layered
    // createSocket(socket, host, ...) call passes the hostname for SNI.
    val plain = Socket()
    val socket = try {
        plain.connect(InetSocketAddress(host, port), timeoutMillis)
        plain.soTimeout = timeoutMillis
        val factory = SSLSocketFactory.getDefault() as SSLSocketFactory
        factory.createSocket(plain, host, port, true) as SSLSocket
    } catch (e: Exception) {
        plain.close()
        throw e
    }
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
