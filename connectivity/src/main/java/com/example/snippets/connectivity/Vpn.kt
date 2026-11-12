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

package com.example.snippets.connectivity

import android.content.pm.PackageManager
import android.net.VpnService
import android.os.ParcelFileDescriptor

// A VpnService subclass, because VpnService.Builder is an inner class that needs the service
// instance.
private class ConnectivityVpnService : VpnService() {
    // Returns the interface so the caller owns the file descriptor and closes it.
    private fun setUpConnection(): ParcelFileDescriptor? {
        // [START android_connectivity_vpn_interface_config]
        // Configure a new interface from our VpnService instance. This must be done
        // from inside a VpnService.
        val builder = Builder()

        // Create a local TUN interface using predetermined addresses. In your app,
        // you typically use values returned from the VPN gateway during handshaking.
        val localTunnel = builder
            .addAddress("192.168.2.2", 24)
            .addRoute("0.0.0.0", 0)
            .addDnsServer("192.168.1.1")
            .establish()
        // [END android_connectivity_vpn_interface_config]
        return localTunnel
    }

    // Returns the interface so the caller owns the file descriptor and closes it.
    private fun addAllowedApps(): ParcelFileDescriptor? {
        // [START android_connectivity_vpn_add_allowed_apps]
        // The apps that will have access to the VPN.
        val appPackages = arrayOf(
            "com.android.chrome",
            "com.google.android.youtube",
            "com.example.a.missing.app"
        )

        // Loop through the app packages in the array and confirm that the app is
        // installed before adding the app to the allowed list.
        val builder = Builder()
        for (appPackage in appPackages) {
            try {
                packageManager.getPackageInfo(appPackage, 0)
                builder.addAllowedApplication(appPackage)
            } catch (e: PackageManager.NameNotFoundException) {
                // The app isn't installed.
            }
        }

        // Complete the VPN interface config.
        val localTunnel = builder
            .addAddress("2001:db8::1", 64)
            .addRoute("::", 0)
            .establish()
        // [END android_connectivity_vpn_add_allowed_apps]
        return localTunnel
    }
}
