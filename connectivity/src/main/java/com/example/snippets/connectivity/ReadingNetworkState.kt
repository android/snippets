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
import android.net.LinkProperties
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.util.Log
import androidx.activity.ComponentActivity

private const val TAG = "ReadingNetworkState"

// An Activity, not a plain Context, because the page registers callbacks in onResume() and
// unregisters them in onPause().
private class ReadingNetworkStateActivity : ComponentActivity() {
    // Looked up once so the lifecycle methods can register and unregister callbacks.
    private val connectivityManager by lazy {
        getSystemService(ConnectivityManager::class.java)
    }

    // Stands in for the app's own callback in the additional-networks example. It overrides
    // nothing, so a plain NetworkCallback instance is enough.
    private val myNetworkCallback = ConnectivityManager.NetworkCallback()

    // Kept so onPause() can unregister the callback that onResume() registered.
    private var defaultNetworkCallback: ConnectivityManager.NetworkCallback? = null

    // The three regions share one function, so each uses the connectivityManager declared in the
    // first one.
    private fun getInstantaneousState() {
        // [START android_connectivity_reading_network_state_get_manager]
        val connectivityManager = getSystemService(ConnectivityManager::class.java)
        // [END android_connectivity_reading_network_state_get_manager]

        // [START android_connectivity_reading_network_state_active_network]
        val currentNetwork = connectivityManager.activeNetwork
        // [END android_connectivity_reading_network_state_active_network]

        // [START android_connectivity_reading_network_state_network_info]
        val caps = connectivityManager.getNetworkCapabilities(currentNetwork)
        val linkProperties = connectivityManager.getLinkProperties(currentNetwork)
        // [END android_connectivity_reading_network_state_network_info]
    }

    // Returns the callback so the caller can unregister it later.
    private fun registerDefaultCallback(): ConnectivityManager.NetworkCallback {
        // [START android_connectivity_reading_network_state_default_callback]
        val defaultNetworkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                Log.d(TAG, "The default network is now: $network")
            }

            override fun onLost(network: Network) {
                Log.d(
                    TAG,
                    "The application no longer has a default network. " +
                        "The last default network was $network"
                )
            }

            override fun onCapabilitiesChanged(
                network: Network,
                networkCapabilities: NetworkCapabilities
            ) {
                Log.d(TAG, "The default network changed capabilities: $networkCapabilities")
            }

            override fun onLinkPropertiesChanged(network: Network, linkProperties: LinkProperties) {
                Log.d(TAG, "The default network changed link properties: $linkProperties")
            }
        }
        connectivityManager.registerDefaultNetworkCallback(defaultNetworkCallback)
        // [END android_connectivity_reading_network_state_default_callback]
        return defaultNetworkCallback
    }

    private fun registerUnmeteredCallback() {
        // [START android_connectivity_reading_network_state_unmetered_request]
        val request = NetworkRequest.Builder()
            .addCapability(NetworkCapabilities.NET_CAPABILITY_NOT_METERED)
            .addCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            .build()

        connectivityManager.registerNetworkCallback(request, myNetworkCallback)
        // [END android_connectivity_reading_network_state_unmetered_request]
    }

    // Registers in onResume() and unregisters in onPause(), as the page recommends.
    override fun onResume() {
        super.onResume()
        defaultNetworkCallback = registerDefaultCallback()
        registerUnmeteredCallback()
    }

    override fun onPause() {
        super.onPause()
        defaultNetworkCallback?.let { connectivityManager.unregisterNetworkCallback(it) }
        defaultNetworkCallback = null
        connectivityManager.unregisterNetworkCallback(myNetworkCallback)
    }
}
