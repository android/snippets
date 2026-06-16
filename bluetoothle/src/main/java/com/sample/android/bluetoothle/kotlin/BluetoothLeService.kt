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
import android.annotation.SuppressLint
import android.app.Service
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.annotation.RequiresPermission

private const val TAG = "BluetoothLeService"

@SuppressLint("MissingPermission")
// [START android_bluetooth_service_all]
// [START android_bluetooth_binder]
class BluetoothLeService : Service() {

    private val binder = LocalBinder()
    // [START_EXCLUDE silent]

    private var bluetoothAdapter: BluetoothAdapter? = null
    private var bluetoothGatt: BluetoothGatt? = null
    private var connectionState = STATE_DISCONNECTED

    companion object {
        const val ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
        const val ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"

        private const val STATE_DISCONNECTED = 0
        private const val STATE_CONNECTED = 2
    }

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
    // [END_EXCLUDE]

    override fun onBind(intent: Intent): IBinder? {
        return binder
    }

    inner class LocalBinder : Binder() {
        fun getService(): BluetoothLeService {
            return this@BluetoothLeService
        }
    }
    // [START_EXCLUDE silent]

    // In BluetoothLeService
    fun initialize(): Boolean {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.")
            return false
        }
        return true
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    fun connect(address: String): Boolean {
        bluetoothAdapter?.let { adapter ->
            try {
                val device = adapter.getRemoteDevice(address)
                // connect to the GATT server on the device
                bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback)
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

    // [START android_bluetooth_broadcast]
    private fun broadcastUpdate(action: String) {
        val intent = Intent(action)
        sendBroadcast(intent)
    }
    // [END android_bluetooth_broadcast]

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onUnbind(intent: Intent?): Boolean {
        close()
        return super.onUnbind(intent)
    }

    private fun close() {
        bluetoothGatt?.let { gatt ->
            gatt.close()
            bluetoothGatt = null
        }
    }
    // [END_EXCLUDE]
}
// [END android_bluetooth_binder]

// [END android_bluetooth_service_all]

/**
 * Namespaces for simplified versions of BluetoothLeService to match documentation.
 */
private object ConnectSimpleNamespace {
    class BluetoothLeService {
        private var bluetoothAdapter: BluetoothAdapter? = null
        private val TAG = "BluetoothLeService"

        // [START android_bluetooth_connect_simple]
        fun connect(address: String): Boolean {
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
        }
        // [END android_bluetooth_connect_simple]
    }
}

private object CallbackSimpleNamespace {
    class BluetoothLeService {
        // [START android_bluetooth_callback_simple]
        private val bluetoothGattCallback = object : BluetoothGattCallback() {
            override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    // successfully connected to the GATT Server
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    // disconnected from the GATT Server
                }
            }
        }
        // [END android_bluetooth_callback_simple]
    }
}

private object InitializeNamespace {
    // [START android_bluetooth_initialize]
    private const val TAG = "BluetoothLeService"

    class BluetoothLeService : Service() {

        private var bluetoothAdapter: BluetoothAdapter? = null

        fun initialize(): Boolean {
            bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
            if (bluetoothAdapter == null) {
                Log.e(TAG, "Unable to obtain a BluetoothAdapter.")
                return false
            }
            return true
        }

        // [START_EXCLUDE]
        override fun onBind(intent: Intent): IBinder? {
            return null
        }
        // [END_EXCLUDE]
    }
    // [END android_bluetooth_initialize]
}

private object ConnectGattNamespace {
    class BluetoothLeService : Service() {
        // [START android_bluetooth_connect_gatt]
        // [START_EXCLUDE silent]
        fun dummy(device: BluetoothDevice, bluetoothGattCallback: BluetoothGattCallback) {
        // [END_EXCLUDE]
            var bluetoothGatt: BluetoothGatt? = null
            // ...
            bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback)
        // [START_EXCLUDE silent]
        }
        // [END_EXCLUDE]
        // [END android_bluetooth_connect_gatt]
        override fun onBind(intent: Intent): IBinder? = null
    }
}

private object ConnectNamespace {
    // [START android_bluetooth_connect]
    class BluetoothLeService : Service() {

        // [START_EXCLUDE silent]
        private var bluetoothAdapter: BluetoothAdapter? = null
        private val bluetoothGattCallback = object : BluetoothGattCallback() {}
        private val TAG = "BluetoothLeService"
        // [END_EXCLUDE]
        // [START_EXCLUDE]
        override fun onBind(intent: Intent): IBinder? = null
        fun dummy() {}
        // [END_EXCLUDE]
        private var bluetoothGatt: BluetoothGatt? = null

        // [START_EXCLUDE]
        fun dummy2() {}
        // [END_EXCLUDE]
        fun connect(address: String): Boolean {
            bluetoothAdapter?.let { adapter ->
                try {
                    val device = adapter.getRemoteDevice(address)
                    // connect to the GATT server on the device
                    bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback)
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
    }
    // [END android_bluetooth_connect]
}

private object CallbackNamespace {
    // [START android_bluetooth_callback]
    class BluetoothLeService : Service() {

        // [START_EXCLUDE silent]
        private val binder = LocalBinder()
        private var bluetoothAdapter: BluetoothAdapter? = null
        private var bluetoothGatt: BluetoothGatt? = null
        private fun broadcastUpdate(action: String) {}
        override fun onBind(intent: Intent): IBinder? = null
        inner class LocalBinder : Binder()
        // [END_EXCLUDE]
        private var connectionState = STATE_DISCONNECTED

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

        // [START_EXCLUDE]
        fun dummy() {}
        // [END_EXCLUDE]
        companion object {
            const val ACTION_GATT_CONNECTED =
                "com.example.bluetooth.le.ACTION_GATT_CONNECTED"
            const val ACTION_GATT_DISCONNECTED =
                "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED"

            private const val STATE_DISCONNECTED = 0
            private const val STATE_CONNECTED = 2
        }
    }
    // [END android_bluetooth_callback]
}

private object CloseNamespace {
    // [START android_bluetooth_close]
    class BluetoothLeService : Service() {

        // [START_EXCLUDE]
        private var bluetoothGatt: BluetoothGatt? = null
        override fun onBind(intent: Intent): IBinder? = null
        // [END_EXCLUDE]
        override fun onUnbind(intent: Intent?): Boolean {
            close()
            return super.onUnbind(intent)
        }

        private fun close() {
            bluetoothGatt?.let { gatt ->
                gatt.close()
                bluetoothGatt = null
            }
        }
    }
    // [END android_bluetooth_close]
}

