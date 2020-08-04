package com.sample.android.bluetoothle.kotlin

import android.bluetooth.BluetoothClass
import android.bluetooth.BluetoothDevice
import android.content.Context
import android.widget.ArrayAdapter

class LeDeviceListAdapter(context: Context?, layout: Int)
    : ArrayAdapter<BluetoothClass.Device?>(context!!, layout) {

    fun addDevice(device: BluetoothDevice?) {
        // This is where you can add devices to the adapter to
        // show a list of discovered devices in the UI.
    }
}