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

import java.io.DataInputStream
import java.net.InetAddress
import java.net.ServerSocket
import java.security.MessageDigest
import java.security.SecureRandom
import java.util.concurrent.Executors
import java.util.concurrent.Semaphore
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

// [START android_security_network_loopback_server]
// respond runs concurrently on worker threads and must be thread-safe.
class LoopbackServer(private val respond: (String) -> String) {
    // Share this secret with the paired client out of band (for example in a
    // signature-protected bound service); never send it over the socket.
    val pairingSecret: ByteArray = ByteArray(32).also { SecureRandom().nextBytes(it) }

    fun start(port: Int): ServerSocket {
        // Bind to loopback only so that other devices on the network can't connect.
        val server = ServerSocket(port, 0, InetAddress.getLoopbackAddress())
        val slots = Semaphore(4)
        val deadlines = Executors.newSingleThreadScheduledExecutor { r ->
            Thread(r).apply { isDaemon = true }
        }
        thread(isDaemon = true) {
            try {
                while (!server.isClosed) {
                    val client = runCatching { server.accept() }.getOrNull() ?: break
                    if (!slots.tryAcquire()) {
                        runCatching { client.close() }
                        continue
                    }
                    // Handle each client on its own bounded worker thread with an overall deadline.
                    thread(isDaemon = true) {
                        try {
                            val timeout = deadlines.schedule(
                                { runCatching { client.close() } },
                                5,
                                TimeUnit.SECONDS,
                            )
                            try {
                                client.use { socket ->
                                    socket.soTimeout = 5_000
                                    val input = DataInputStream(socket.getInputStream())
                                    val presented = ByteArray(pairingSecret.size)
                                    input.readFully(presented)
                                    // Constant-time comparison avoids leaking the secret through timing.
                                    if (!MessageDigest.isEqual(presented, pairingSecret)) return@use
                                    val request = input.bufferedReader().readLine() ?: return@use
                                    socket.getOutputStream().write((respond(request) + "\n").toByteArray())
                                }
                            } finally {
                                timeout.cancel(false)
                            }
                        } catch (e: Exception) {
                            // A timeout, short read, disconnect, or respond failure ends this
                            // client only. An uncaught exception on this thread would crash the app.
                            runCatching { client.close() }
                        } finally {
                            slots.release()
                        }
                    }
                }
            } finally {
                deadlines.shutdownNow()
            }
        }
        return server
    }
}
// [END android_security_network_loopback_server]
