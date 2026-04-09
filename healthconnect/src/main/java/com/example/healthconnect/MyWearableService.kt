package com.example.healthconnect

import android.Manifest
import android.companion.CompanionDeviceService
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.health.connect.client.HealthConnectClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import android.bluetooth.BluetoothGatt
import androidx.annotation.RequiresPermission
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
// [START health_connect_companion_service]
class MyWearableService : CompanionDeviceService() {
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var healthConnectClient: HealthConnectClient? = null
    private var bluetoothGatt: BluetoothGatt? = null

    override fun onDeviceAppeared(address: String) {
        super.onDeviceAppeared(address)
        healthConnectClient = HealthConnectClient.getOrCreate(this)

        serviceScope.launch {
            val granted = healthConnectClient?.permissionController?.getGrantedPermissions()
            // ... set up GATT and subscribe ...
        }
    }

    @RequiresPermission(Manifest.permission.BLUETOOTH_CONNECT)
    override fun onDeviceDisappeared(address: String) {
        super.onDeviceDisappeared(address)
        bluetoothGatt?.close()
    }
}
// [END health_connect_companion_service]