/*
 * Copyright 2025 The Android Open Source Project
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

import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.widget.ArrayAdapter

class LeDeviceListAdapter(context: Context?, layout: Int) :
    ArrayAdapter<BluetoothClass.Device?>(context!!, layout) {

    fun addDevice(device: BluetoothDevice?) {
        // This is where you can add devices to the adapter to
        // show a list of discovered devices in the UI.
    }
}
