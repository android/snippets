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

package com.example.healthconnect

import android.Manifest
import android.bluetooth.BluetoothGatt
import android.companion.CompanionDeviceService
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.annotation.RequiresPermission
import androidx.health.connect.client.HealthConnectClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.S)
// [START android_healthconnect_companion_service]
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
// [END android_healthconnect_companion_service]
