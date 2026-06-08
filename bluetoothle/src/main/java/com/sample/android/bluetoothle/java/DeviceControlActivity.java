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

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import com.sample.android.bluetoothle.R;
import com.sample.android.bluetoothle.java.BluetoothLeService.LocalBinder;
import androidx.annotation.RequiresPermission;

// [START android_bluetooth_activity_all_java]
// [START android_bluetooth_bind_service_java]
public class DeviceControlActivity extends AppCompatActivity {

    private static final String TAG = "DeviceControlActivity";

    private BluetoothLeService bluetoothService;
    private String deviceAddress;
    private boolean connected = false;
    // [START_EXCLUDE silent]

    // [END_EXCLUDE]
    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
        public void onServiceConnected(ComponentName name, IBinder service) {
            bluetoothService = ((LocalBinder) service).getService();
            if (bluetoothService != null) {
                // [START android_bluetooth_initialize_activity_java]
                if (!bluetoothService.initialize()) {
                    Log.e(TAG, "Unable to initialize Bluetooth");
                    finish();
                    return;
                }
                // [END android_bluetooth_initialize_activity_java]
                // perform device connection
                // [START android_bluetooth_connect_activity_java]
                bluetoothService.connect(deviceAddress);
                // [END android_bluetooth_connect_activity_java]
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            bluetoothService = null;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);

        deviceAddress = getIntent().getStringExtra("EXTRAS_DEVICE_ADDRESS");

        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
    }
    // [START_EXCLUDE silent]

    // [START android_bluetooth_update_receiver_java]
    private final BroadcastReceiver gattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                connected = true;
                updateConnectionState(R.string.connected);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                connected = false;
                updateConnectionState(R.string.disconnected);
            }
        }
    };

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(gattUpdateReceiver, makeGattUpdateIntentFilter());
        if (bluetoothService != null) {
            final boolean result = bluetoothService.connect(deviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(gattUpdateReceiver);
    }
    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        return intentFilter;
    }
    // [END android_bluetooth_update_receiver_java]


    private void updateConnectionState(int resourceId) {
        // Placeholder implementation
    }
    // [END_EXCLUDE]
}
// [END android_bluetooth_bind_service_java]
// [END android_bluetooth_activity_all_java]
