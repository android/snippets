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

package com.example.compose.snippets.connectivity

import android.net.ConnectivityManager
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.activity.ComponentActivity

private const val TAG = "ReadingNetworkState"

private class ReadingNetworkState : ComponentActivity() {
    private val myNetworkCallback = object : ConnectivityManager.NetworkCallback() {}

    private fun getConnectivityManager() {
        // [START android_connectivity_reading_network_state_get_manager]
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        // [END android_connectivity_reading_network_state_get_manager]
    }

    private fun getInstantaneousState(connectivityManager: ConnectivityManager) {
        // [START android_connectivity_reading_network_state_active_network]
        val currentNetwork = connectivityManager.getActiveNetwork()
        // [END android_connectivity_reading_network_state_active_network]

        // [START android_connectivity_reading_network_state_network_info]
        val caps = connectivityManager.getNetworkCapabilities(currentNetwork)
        val linkProperties = connectivityManager.getLinkProperties(currentNetwork)
        // [END android_connectivity_reading_network_state_network_info]
    }

    private fun registerDefaultCallback(connectivityManager: ConnectivityManager) {
        // [START android_connectivity_reading_network_state_default_callback]
        connectivityManager.registerDefaultNetworkCallback(object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.e(TAG, "The default network is now: " + network)
            }

            override fun onLost(network: Network) {
                Log.e(TAG, "The application no longer has a default network. The last default network was " + network)
            }

            override fun onCapabilitiesChanged(network: Network, networkCapabilities: NetworkCapabilities) {
                Log.e(TAG, "The default network changed capabilities: " + networkCapabilities)
            }

            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                Log.e(TAG, "The default network changed link properties: " + linkProperties)
            }
        })
        // [END android_connectivity_reading_network_state_default_callback]
    }

    private fun registerUnmeteredCallback(connectivityManager: ConnectivityManager) {
        // [START android_connectivity_reading_network_state_unmetered_request]
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, myNetworkCallback)
        // [END android_connectivity_reading_network_state_unmetered_request]
    }
}
