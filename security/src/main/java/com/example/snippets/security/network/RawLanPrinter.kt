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
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

// [START android_security_network_raw_lan_printer]
// Raw TCP printers have no TLS: send only the print job, never tokens or account data.
// Call this from a background thread.
fun sendToRawPrinter(host: String, payload: ByteArray, port: Int = 9100) {
    val watchdog = Executors.newSingleThreadScheduledExecutor { r ->
        Thread(r).apply { isDaemon = true }
    }
    try {
        Socket().use { socket ->
            val deadline = watchdog.schedule({ runCatching { socket.close() } }, 10, TimeUnit.SECONDS)
            try {
                socket.connect(InetSocketAddress(host, port), 5_000)
                socket.getOutputStream().apply {
                    write(payload)
                    flush()
                }
            } finally {
                deadline.cancel(false)
            }
        }
    } finally {
        watchdog.shutdownNow()
    }
}
// [END android_security_network_raw_lan_printer]
