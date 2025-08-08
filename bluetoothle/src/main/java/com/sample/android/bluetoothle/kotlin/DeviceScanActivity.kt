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

import android.Manifest
import android.app.ListActivity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Handler
import androidx.annotation.RequiresPermission
import com.sample.android.bluetoothle.java.LeDeviceListAdapter

/**
 * Activity for scanning and displaying available BLE devices.
 */
class DeviceScanActivity : ListActivity() {
    // [START process_scan_result]
    private val leDeviceListAdapter: LeDeviceListAdapter? = null
    // Device scan callback.
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            leDeviceListAdapter!!.addDevice(result.device)
            leDeviceListAdapter.notifyDataSetChanged()
        }
    }
    // [END process_scan_result]

    // [START start_and_stop_scan]
    private val bluetoothLeScanner = BluetoothAdapter.getDefaultAdapter().bluetoothLeScanner
    private var mScanning = false
    private val handler = Handler()

    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000

    @RequiresPermission(Manifest.permission.BLUETOOTH_SCAN)
    private fun scanLeDevice() {
        if (!mScanning) { // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                mScanning = false
                bluetoothLeScanner.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            mScanning = true
            bluetoothLeScanner.startScan(leScanCallback)
        } else {
            mScanning = false
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }
    // [END start_and_stop_scan]
}
