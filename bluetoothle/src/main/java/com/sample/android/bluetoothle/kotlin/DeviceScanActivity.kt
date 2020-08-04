package com.sample.android.bluetoothle.kotlin

import android.app.ListActivity
import android.bluetooth.BluetoothAdapter
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Handler
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