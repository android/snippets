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

package com.sample.android.bluetoothle.java;

import android.Manifest;
import android.app.Service;
import androidx.annotation.RequiresPermission;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothProfile;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;

// [START android_bluetooth_service_all_java]
// [START android_bluetooth_service_binder_java]
public class BluetoothLeService extends Service {
    // [START_EXCLUDE silent]
    public static final String TAG = "BluetoothLeService";
    // [END_EXCLUDE]

    private final Binder binder = new LocalBinder();

    // [START_EXCLUDE silent]
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothGatt bluetoothGatt;
    private int connectionState;

    // [START android_bluetooth_constants_java]
    public static final String ACTION_GATT_CONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_CONNECTED";
    public static final String ACTION_GATT_DISCONNECTED =
            "com.example.bluetooth.le.ACTION_GATT_DISCONNECTED";

    private static final int STATE_DISCONNECTED = 0;
    private static final int STATE_CONNECTED = 2;
    // [END android_bluetooth_constants_java]
    // [END_EXCLUDE]


    // [START android_bluetooth_binder_java]
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    public class LocalBinder extends Binder {
        public BluetoothLeService getService() {
            return BluetoothLeService.this;
        }
    }
    // [END android_bluetooth_binder_java]
    // [START_EXCLUDE silent]

    // [START android_bluetooth_initialize_java]
    public boolean initialize() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (bluetoothAdapter == null) {
            Log.e(TAG, "Unable to obtain a BluetoothAdapter.");
            return false;
        }
        return true;
    }
    // [END android_bluetooth_initialize_java]

    // [START android_bluetooth_connect_simple_java]
    // [START_EXCLUDE silent]
    private class SimplifiedConnect {
        public boolean connect(final String address) {
    // [END_EXCLUDE]
            if (bluetoothAdapter == null || address == null) {
                Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
                return false;
            }
            try {
                final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
            } catch (IllegalArgumentException exception) {
                Log.w(TAG, "Device not found with provided address.");
                return false;
            }
            // connect to the GATT server on the device
            return true;
    // [START_EXCLUDE silent]
        }
    }
    // [END_EXCLUDE]
    // [END android_bluetooth_connect_simple_java]

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    // [START android_bluetooth_connect_java]
    public boolean connect(final String address) {
        if (bluetoothAdapter == null || address == null) {
            Log.w(TAG, "BluetoothAdapter not initialized or unspecified address.");
            return false;
        }
        try {
            final BluetoothDevice device = bluetoothAdapter.getRemoteDevice(address);
            // connect to the GATT server on the device
            bluetoothGatt = device.connectGatt(this, false, bluetoothGattCallback);
            return true;
        } catch (IllegalArgumentException exception) {
            Log.w(TAG, "Device not found with provided address.  Unable to connect.");
            return false;
        }
    }
    // [END android_bluetooth_connect_java]

    // [START android_bluetooth_callback_simple_java]
    // [START_EXCLUDE silent]
    private class SimplifiedCallback {
        private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
    // [END_EXCLUDE]
            @Override
            public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
                if (newState == BluetoothProfile.STATE_CONNECTED) {
                    // successfully connected to the GATT Server
                } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                    // disconnected from the GATT Server
                }
            }
    // [START_EXCLUDE silent]
        };
    }
    // [END_EXCLUDE]
    // [END android_bluetooth_callback_simple_java]

    // [START android_bluetooth_callback_java]
    private final BluetoothGattCallback bluetoothGattCallback = new BluetoothGattCallback() {
        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState == BluetoothProfile.STATE_CONNECTED) {
                // successfully connected to the GATT Server
                connectionState = STATE_CONNECTED;
                broadcastUpdate(ACTION_GATT_CONNECTED);
            } else if (newState == BluetoothProfile.STATE_DISCONNECTED) {
                // disconnected from the GATT Server
                connectionState = STATE_DISCONNECTED;
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
            }
        }
    };
    // [END android_bluetooth_callback_java]

    // [START android_bluetooth_broadcast_java]
    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }
    // [END android_bluetooth_broadcast_java]

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    // [START android_bluetooth_close_java]
    @Override
    public boolean onUnbind(Intent intent) {
        close();
        return super.onUnbind(intent);
    }

    // [START_EXCLUDE silent]
    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    // [END_EXCLUDE]
    private void close() {
        if (bluetoothGatt == null) {
            return;
        }
        bluetoothGatt.close();
        bluetoothGatt = null;
    }
    // [END android_bluetooth_close_java]
    // [END_EXCLUDE]
}
// [END android_bluetooth_service_binder_java]

// [END android_bluetooth_service_all_java]
