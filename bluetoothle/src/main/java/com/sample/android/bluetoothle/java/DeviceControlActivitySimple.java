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

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import com.sample.android.bluetoothle.java.BluetoothLeService.LocalBinder;

public class DeviceControlActivitySimple {
    private static final String TAG = "DeviceControlActivity";
    private static BluetoothLeService bluetoothService;
    private static void finish() {}

    // [START android_bluetooth_service_connection_simple_java]
    // [START_EXCLUDE silent]
    private class SimplifiedServiceConnection {
        private final ServiceConnection serviceConnection = new ServiceConnection() {
            // [END_EXCLUDE]
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                bluetoothService = ((LocalBinder) service).getService();
                if (bluetoothService != null) {
                    // call functions on service to check connection and connect to devices
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                bluetoothService = null;
            }
            // [START_EXCLUDE silent]
        };
    }
    // [END_EXCLUDE]
    // [END android_bluetooth_service_connection_simple_java]

    // [START android_bluetooth_service_connection_initialize_java]
    // [START_EXCLUDE silent]
    private class InitializeServiceConnection {
        private final ServiceConnection serviceConnection = new ServiceConnection() {
            // [END_EXCLUDE]
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                bluetoothService = ((LocalBinder) service).getService();
                if (bluetoothService != null) {
                    if (!bluetoothService.initialize()) {
                        Log.e(TAG, "Unable to initialize Bluetooth");
                        finish();
                    }
                    // perform device connection
                }
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                bluetoothService = null;
            }
            // [START_EXCLUDE silent]
        };
    }
    // [END_EXCLUDE]
    // [END android_bluetooth_service_connection_initialize_java]
}
