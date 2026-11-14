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

import android.content.Context
import android.content.pm.PackageManager
import android.net.LocalSocket
import java.util.Locale

// [START android_security_network_local_peer_checks]
// Header names are lowercased by the caller.
fun isAllowedLoopbackRequest(headers: Map<String, String>, port: Int, allowedOrigins: Set<String>): Boolean {
    val origin = headers["origin"] ?: return false
    val host = headers["host"]?.lowercase(Locale.ROOT) ?: return false
    // InetAddress.getLoopbackAddress() is ::1 on Android, so clients connect
    // through localhost or [::1].
    val loopbackHosts = setOf("localhost:$port", "[::1]:$port", "127.0.0.1:$port")
    return origin.startsWith("https://") && origin in allowedOrigins && host in loopbackHosts
}

// On targetSdk 30+, declare <queries><package android:name="..." /></queries>
// for expectedPackage in the manifest, or getPackagesForUid returns null.
fun isTrustedPeer(context: Context, socket: LocalSocket, expectedPackage: String): Boolean {
    val packages = context.packageManager.getPackagesForUid(socket.peerCredentials.uid) ?: return false
    return expectedPackage in packages &&
        context.packageManager.checkSignatures(context.packageName, expectedPackage) ==
        PackageManager.SIGNATURE_MATCH
}
// [END android_security_network_local_peer_checks]
