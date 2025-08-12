package com.sample.android.bluetoothle.java;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.RequiresPermission;
import androidx.appcompat.app.AppCompatActivity;

import com.sample.android.bluetoothle.R;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_ENABLE_BT = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    private void setUpBLE() {
        // [START get_bluetooth_adapter]
        // Initializes Bluetooth adapter.
        final BluetoothManager bluetoothManager =
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();
        // [END get_bluetooth_adapter]

        // [START enable_bluetooth]
        // Ensures Bluetooth is available on the device and it is enabled. If not,
        // displays a dialog requesting user permission to enable Bluetooth.
        if (bluetoothAdapter == null || !bluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
        // [END enable_bluetooth]
    }
}
