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

package com.example.wear.snippets.network

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest

class NetworkSnippets(val context: Context) {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    fun requestWifiNetwork() {
        // [START android_wear_request_network]
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                // The Wi-Fi network has been acquired. Bind it to use this network by default.
                connectivityManager.bindProcessToNetwork(network)
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                // Called when a network disconnects or otherwise no longer satisfies this request
                // or callback.
            }
        }
        connectivityManager.requestNetwork(
            NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI).build(),
            callback
        )
        // [END android_wear_request_network]
    }

    fun unregisterNetwork(callback: ConnectivityManager.NetworkCallback) {
        // [START android_wear_unregister_network]
        connectivityManager.bindProcessToNetwork(null)
        connectivityManager.unregisterNetworkCallback(callback)
        // [END android_wear_unregister_network]
    }

    fun launchSettings() {
        // [START android_wear_launch_settings]
        context.startActivity(Intent("com.google.android.clockwork.settings.connectivity.wifi.ADD_NETWORK_SETTINGS"))
        // [END android_wear_launch_settings]
    }
}
