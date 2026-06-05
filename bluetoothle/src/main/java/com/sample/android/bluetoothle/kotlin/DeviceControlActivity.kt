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
import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import com.sample.android.bluetoothle.R
import com.sample.android.bluetoothle.kotlin.BluetoothLeService.LocalBinder

private const val TAG = "DeviceControlActivity"

// [START android_bluetooth_activity_all]
class DeviceControlActivity : AppCompatActivity() {

    private var bluetoothService: BluetoothLeService? = null
    private var deviceAddress: String? = null
    private var connected = false

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
        val serviceConnection: ServiceConnection = object : ServiceConnection {
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
                }
            }

            override fun onServiceDisconnected(componentName: ComponentName) {
                bluetoothService = null
            }
            // [START_EXCLUDE silent]
        }
    }
    // [END_EXCLUDE]
    // [END android_bluetooth_service_connection_initialize]

    // [START android_bluetooth_service_connection]

    // Code to manage Service lifecycle.
    private val serviceConnection: ServiceConnection = object : ServiceConnection {
        // [START_EXCLUDE silent]
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        // [END_EXCLUDE]
        override fun onServiceConnected(
            componentName: ComponentName,
            service: IBinder
        ) {
            bluetoothService = (service as LocalBinder).getService()
            bluetoothService?.let { bluetooth ->
                // [START android_bluetooth_initialize_activity]
                if (!bluetooth.initialize()) {
                    Log.e(TAG, "Unable to initialize Bluetooth")
                    finish()
                    return@let
                }
                // [END android_bluetooth_initialize_activity]
                // perform device connection
                // [START android_bluetooth_connect_activity]
                bluetooth.connect(deviceAddress!!)
                // [END android_bluetooth_connect_activity]
            }
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            bluetoothService = null
        }
    }
    // [END android_bluetooth_service_connection]

    // [START android_bluetooth_update_receiver]
    private val gattUpdateReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            when (intent.action) {
                BluetoothLeService.ACTION_GATT_CONNECTED -> {
                    connected = true
                    updateConnectionState(R.string.connected)
                }
                BluetoothLeService.ACTION_GATT_DISCONNECTED -> {
                    connected = false
                    updateConnectionState(R.string.disconnected)
                }
            }
        }
    }

    // [START_EXCLUDE silent]
    // [START android_bluetooth_bind_service]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gatt_services_characteristics)

        deviceAddress = intent.getStringExtra("EXTRAS_DEVICE_ADDRESS")

        val gattServiceIntent = Intent(this, BluetoothLeService::class.java)
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE)
    }
    // [END android_bluetooth_bind_service]

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    // [END_EXCLUDE]
    override fun onResume() {
        super.onResume()
        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter())
        if (bluetoothService != null) {
            val result = bluetoothService!!.connect(deviceAddress!!)
            Log.d(TAG, "Connect request result=$result")
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(gattUpdateReceiver)
    }
    private fun makeGattUpdateIntentFilter(): IntentFilter {
        return IntentFilter().apply {
            addAction(BluetoothLeService.ACTION_GATT_CONNECTED)
            addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED)
        }
    }
    // [END android_bluetooth_update_receiver]

    private fun updateConnectionState(resourceId: Int) {
        // Placeholder implementation
    }
}
// [END android_bluetooth_activity_all]
