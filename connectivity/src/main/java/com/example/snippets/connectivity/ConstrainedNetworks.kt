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
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.NET_CAPABILITY_INTERNET
import android.net.NetworkCapabilities.NET_CAPABILITY_NOT_BANDWIDTH_CONSTRAINED
import android.net.NetworkRequest
import android.os.Handler
import android.os.HandlerThread

// Takes the ConnectivityManager as cm, the name the page uses, and returns the callback and
// thread so the caller can unregister the callback and quit the thread when it's done.
private fun monitorConstrainedNetworks(
    cm: ConnectivityManager,
): Pair<NetworkCallback, HandlerThread> {
    // [START android_connectivity_constrained_networks_monitor]
    val handlerThread = HandlerThread("SatelliteNetworkMonitor")
    handlerThread.start()
    val handler = Handler(handlerThread.getLooper())

    // Make the network request.
    val request = NetworkRequest.Builder()
        .addCapability(NET_CAPABILITY_INTERNET)
        .removeCapability(NET_CAPABILITY_NOT_BANDWIDTH_CONSTRAINED)
        .build()

    // Register for the callback.
    val callback = object : NetworkCallback() {
        override fun onCapabilitiesChanged(net: Network, nc: NetworkCapabilities) {
            updateAppUseCases(net, nc)
        }

        fun updateAppUseCases(net: Network, nc: NetworkCapabilities) {
            // Check if the network is bandwidth-constrained or if the transport is satellite.
            // Checking for the satellite transport is to make sure that application
            // continue to have optimized behavior on satellite networks irrespective of whether
            // the NET_CAPABILITY_NOT_BANDWIDTH_CONSTRAINED capability is set or not. This is
            // because the capability can be removed on an unconstraned satellite Network which only
            // means applications does not need to modify to get data access but should continue
            // to optimize for data usage.
            if (!nc.hasCapability(NET_CAPABILITY_NOT_BANDWIDTH_CONSTRAINED) ||
                nc.hasTransport(NetworkCapabilities.TRANSPORT_SATELLITE)
            ) {
                // Adapt to constrained network or disable heavy data usage features.
                // ...
            } else {
                // Revert to non optimized behavior for data usage.
                // ...
            }
        }
    }
    // Where cm is your ConnectivityManager object:
    cm.registerBestMatchingNetworkCallback(request, callback, handler)
    // [END android_connectivity_constrained_networks_monitor]
    return callback to handlerThread
}
