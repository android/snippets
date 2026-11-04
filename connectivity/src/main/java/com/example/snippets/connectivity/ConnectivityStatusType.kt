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

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.activity.ComponentActivity

// An Activity, not a plain Context, because the callback is registered in onStart() and
// unregistered in onStop().
private class ConnectivityStatusTypeActivity : ComponentActivity() {
    // [START android_connectivity_status_type_configure_request]
    private val networkRequest = NetworkRequest.Builder()
        .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
        .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
        .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
        .build()
    // [END android_connectivity_status_type_configure_request]

    // [START android_connectivity_status_type_configure_callback]
    private val networkCallback = object : ConnectivityManager.NetworkCallback() {
        override fun onAvailable(network: Network) {
            // Network is available for use.
        }

        // Network capabilities have changed for the network.
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities
        ) {
            val unmetered =
                networkCapabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
            if (!unmetered) {
                // Reduce data usage, or delay it until the network is unmetered.
            }
        }

        override fun onLost(network: Network) {
            // Lost network connection.
        }
    }
    // [END android_connectivity_status_type_configure_callback]

    override fun onStart() {
        super.onStart()
        // [START android_connectivity_status_type_register_updates]
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
        // [END android_connectivity_status_type_register_updates]
    }

    // Unregisters the callback so it doesn't outlive the Activity.
    override fun onStop() {
        super.onStop()
        getSystemService(ConnectivityManager::class.java).unregisterNetworkCallback(networkCallback)
    }
}
