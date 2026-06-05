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
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresPermission

private const val TAG = "BluetoothLeService"

// [START android_bluetooth_service_all]
class BluetoothLeService : Service() {

    private val binder = LocalBinder()

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var connectionState = STATE_DISCONNECTED

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothLeService {
            return this@BluetoothLeService
        }
    }

    // [START android_bluetooth_initialize]
    fun initialize(): Boolean {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.")
            return false
        }
        return true
    }
    // [END android_bluetooth_initialize]

    // [START android_bluetooth_connect_simple]
    // [START_EXCLUDE silent]
    private inner class SimplifiedConnect {
        fun connect(address: String): Boolean {
            // [END_EXCLUDE]
            bluetoothAdapter?.let { adapter ->
                try {
                    val device = adapter.getRemoteDevice(address)
                } catch (exception: IllegalArgumentException) {
                    Log.w(TAG, "Device not found with provided address.")
                    return false
                }
                // connect to the GATT server on the device
                return true
            } ?: run {
                Log.w(TAG, "BluetoothAdapter not initialized")
                return false
            }
            // [START_EXCLUDE silent]
        }
    }
    // [END_EXCLUDE]
    // [END android_bluetooth_connect_simple]

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    // [START android_bluetooth_connect]
    fun connect(address: String): Boolean {
        bluetoothAdapter?.let { adapter ->
            try {
                val device = adapter.getRemoteDevice(address)
                // connect to the GATT server on the device
                // [START android_bluetooth_connect_gatt]
                bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback)
                // [END android_bluetooth_connect_gatt]
                return true
            } catch (exception: IllegalArgumentException) {
                Log.w(TAG, "Device not found with provided address.  Unable to connect.")
                return false
            }
        } ?: run {
            Log.w(TAG, "BluetoothAdapter not initialized")
            return false
        }
    }
    // [END android_bluetooth_connect]

    // [START android_bluetooth_callback_simple]
    // [START_EXCLUDE silent]
    private inner class SimplifiedCallback {
        val bluetoothGattCallback = object : BluetoothGattCallback() {
            // [END_EXCLUDE]
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    // successfully connected to the GATT Server
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    // disconnected from the GATT Server
                }
            }
            // [START_EXCLUDE silent]
        }
    }
    // [END_EXCLUDE]
    // [END android_bluetooth_callback_simple]

    // [START android_bluetooth_callback]
    private val bluetoothGattCallback = object : BluetoothGattCallback() {
        override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // successfully connected to the GATT Server
                connectionState = STATE_CONNECTED
                broadcastUpdate(ACTION_GATT_CONNECTED)
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
                connectionState = STATE_DISCONNECTED
                broadcastUpdate(ACTION_GATT_DISCONNECTED)
            }
        }
    }
    // [END android_bluetooth_callback]

    // [START android_bluetooth_broadcast]
    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }
    // [END android_bluetooth_broadcast]

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    // [START android_bluetooth_close]
    override fun onUnbind(intent: Intent?): Boolean {
        close()
        return super.onUnbind(intent)
    }

    // [START_EXCLUDE silent]
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    // [END_EXCLUDE]
    private fun close() {
        bluetoothGatt?.let { gatt ->
            gatt.close()
            bluetoothGatt = null
        }
    }
    // [END android_bluetooth_close]

    companion object {
        // [START android_bluetooth_constants]
        const val ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"

        private const val STATE_DISCONNECTED = 0
        private const val STATE_CONNECTED = 2
        // [END android_bluetooth_constants]
    }
}

// [END android_bluetooth_service_all]
