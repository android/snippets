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

package com.sample.android.bluetoothle.kotlin

import android.Manifest
import android.content.ComponentName
import android.content.ServiceConnection
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresPermission
import com.sample.android.bluetoothle.kotlin.BluetoothLeService.LocalBinder

// Placeholders for compilation
private var bluetoothService: BluetoothLeService? = null
private var deviceAddress: String? = null
private const val TAG = "DeviceControlActivity"
private fun finish() {}

class DeviceControlActivitySimple {

    // [START android_bluetooth_service_connection_simple]
    // [START_EXCLUDE silent]
    private inner class SimplifiedServiceConnection {
        val serviceConnection: ServiceConnection = object : ServiceConnection {
            // [END_EXCLUDE]
            override fun onServiceConnected(
                componentName: ComponentName,
                service: IBinder
            ) {
                bluetoothService = (service as LocalBinder).getService()
                bluetoothService?.let { bluetooth ->
                    // call functions on service to check connection and connect to devices
                }
            }

            override fun onServiceDisconnected(componentName: ComponentName) {
                bluetoothService = null
            }
            // [START_EXCLUDE silent]
        }
    }
    // [END_EXCLUDE]
    // [END android_bluetooth_service_connection_simple]

    // [START android_bluetooth_service_connection_initialize]
    // [START_EXCLUDE silent]
    private inner class InitializeServiceConnection {
        // [END_EXCLUDE]
        // In DeviceControlActivity
        val serviceConnection: ServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(
                componentName: ComponentName,
                service: IBinder
            ) {
                bluetoothService = (service as LocalBinder).getService()
                bluetoothService?.let { bluetooth ->
                    if (!bluetooth.initialize()) {
                        Log.e(TAG, "Unable to initialize Bluetooth")
                        finish()
                    }
                    // perform device connection
                }
            }

            override fun onServiceDisconnected(componentName: ComponentName) {
                bluetoothService = null
            }
        }
        // [START_EXCLUDE silent]
    }
    // [END_EXCLUDE]
    // [END android_bluetooth_service_connection_initialize]

    // [START android_bluetooth_service_connection_connect]
    // [START_EXCLUDE silent]
    private inner class ConnectServiceConnection {
        // [END_EXCLUDE]
        // In DeviceControlActivity
        val serviceConnection: ServiceConnection = object : ServiceConnection {
            // [START_EXCLUDE silent]
            @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
            // [END_EXCLUDE]
            override fun onServiceConnected(
                componentName: ComponentName,
                service: IBinder
            ) {
                bluetoothService = (service as LocalBinder).getService()
                bluetoothService?.let { bluetooth ->
                    if (!bluetooth.initialize()) {
                        Log.e(TAG, "Unable to initialize Bluetooth")
                        finish()
                    }
                    // perform device connection
                    deviceAddress?.let { bluetooth.connect(it) }
                }
            }

            override fun onServiceDisconnected(componentName: ComponentName) {
                bluetoothService = null
            }
        }
        // [START_EXCLUDE silent]
    }
    // [END_EXCLUDE]
    // [END android_bluetooth_service_connection_connect]
}
