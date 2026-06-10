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

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCallback
import android.bluetooth.BluetoothProfile
import android.util.Log

private var bluetoothAdapter: BluetoothAdapter? = null
private const val TAG = "BluetoothLeService"

// [START android_bluetooth_connect_simple]
// In BluetoothLeService
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

// [START android_bluetooth_callback_simple]
// In BluetoothLeService
val bluetoothGattCallback = object : BluetoothGattCallback() {
    override fun onConnectionStateChange(gatt: BluetoothGatt?, status: Int, newState: Int) {
        if (newState == BluetoothProfile.STATE_CONNECTED) {
            // successfully connected to the GATT Server
        } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
            // disconnected from the GATT Server
        }
    }
}
// [END android_bluetooth_callback_simple]
