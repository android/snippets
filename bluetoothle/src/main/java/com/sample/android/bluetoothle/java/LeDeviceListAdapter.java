package com.sample.android.bluetoothle.java;

import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.widget.ArrayAdapter;

public class LeDeviceListAdapter extends ArrayAdapter<BluetoothClass.Device> {

    public LeDeviceListAdapter(Context context, int layout) {
        super(context, layout);
    }

    public void addDevice(BluetoothDevice device) {
        // This is where you can add devices to the adapter to show a list of discovered
        // devices in the UI.
    }
}
