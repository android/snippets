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

// [START android_security_network_raw_lan_printer]
// Raw TCP printers have no TLS: send only the print job, never tokens or account data.
// Call this from a background thread.
fun sendToRawPrinter(host: String, payload: ByteArray, port: Int = 9100) {
    Socket().use { socket ->
        socket.connect(InetSocketAddress(host, port), 5_000)
        socket.soTimeout = 10_000
        socket.getOutputStream().apply {
            write(payload)
            flush()
        }
    }
}
// [END android_security_network_raw_lan_printer]
