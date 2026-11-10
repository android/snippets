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

package com.example.snippets.connectivity

import android.content.Context
import android.ranging.DataNotificationConfig
import android.ranging.DlTdoaMeasurement
import android.ranging.RangingCapabilities
import android.ranging.RangingData
import android.ranging.RangingDevice
import android.ranging.RangingManager
import android.ranging.RangingPreference
import android.ranging.RangingPreference.DEVICE_ROLE_DT_TAG
import android.ranging.RangingPreference.DEVICE_ROLE_INITIATOR
import android.ranging.RangingPreference.DEVICE_ROLE_RESPONDER
import android.ranging.RangingSession
import android.ranging.SessionConfig
import android.ranging.oob.DeviceHandle
import android.ranging.oob.OobInitiatorRangingConfig
import android.ranging.oob.OobInitiatorRangingConfig.RANGING_MODE_AUTO
import android.ranging.oob.OobInitiatorRangingConfig.SECURITY_LEVEL_BASIC
import android.ranging.oob.OobResponderRangingConfig
import android.ranging.raw.RawDtTagRangingConfig
import android.ranging.raw.RawInitiatorRangingConfig
import android.ranging.raw.RawRangingDevice
import android.ranging.uwb.DlTdoaRangingParams
import android.ranging.uwb.UwbAddress
import android.ranging.uwb.UwbComplexChannel
import android.ranging.uwb.UwbRangingParams
import androidx.annotation.RequiresApi
import java.time.Duration
import java.util.concurrent.Executor
import java.util.concurrent.Executors

// Wrapped in an object so that RangingApp stays private to this file. start() and stop() need
// the RANGING permission, which the module's manifest declares; an app must also request it at
// runtime before it calls into RangingApp.
private object RangingSessionSnippet {
    // [START android_connectivity_ranging_session]
    class RangingApp {

        // Starts a ranging session on the initiator side.
        fun startRangingInitiator(
            context: Context,
            deviceHandle: DeviceHandle,
            executor: Executor,
            callback: RangingSession.Callback
        ): RangingSession? {

            // Get the RangingManager which is the entry point for ranging module.
            val manager = context.getSystemService(RangingManager::class.java)

            // Create a new RangingSession using the provided executor and callback.
            val session = manager.createRangingSession(executor, callback) ?: return null

            // Create an OobInitiatorRangingConfig, which specifies the ranging parameters for
            // the initiator role.
            val config = OobInitiatorRangingConfig.Builder()
                .setFastestRangingInterval(Duration.ofMillis(100))
                .setSlowestRangingInterval(Duration.ofMillis(5000))
                .setRangingMode(RANGING_MODE_AUTO)
                .setSecurityLevel(SECURITY_LEVEL_BASIC)
                .addDeviceHandle(deviceHandle)
                .build()

            // Create a RangingPreference, which specifies the role (initiator) and
            // configuration for the ranging session.
            val preference =
                RangingPreference.Builder(DEVICE_ROLE_INITIATOR, config).build()

            // Start ranging session. Ranging data is delivered asynchronously through
            // callback.onResults; call stopRanging(session) when ranging is no longer needed.
            session.start(preference)
            return session
        }

        // Starts a ranging session on the responder side.
        fun startRangingResponder(
            context: Context,
            deviceHandle: DeviceHandle,
            executor: Executor,
            callback: RangingSession.Callback
        ): RangingSession? {

            // Get the RangingManager which is the entry point for ranging module.
            val manager = context.getSystemService(RangingManager::class.java)

            // Create a new RangingSession using the provided executor and callback.
            val session = manager.createRangingSession(executor, callback) ?: return null

            // Create an OobResponderRangingConfig, which specifies the ranging parameters for
            // the responder role.
            val config = OobResponderRangingConfig.Builder(deviceHandle).build()

            // Create a RangingPreference, which specifies the role (responder) and
            // configuration for the ranging session.
            val preference =
                RangingPreference.Builder(DEVICE_ROLE_RESPONDER, config).build()

            // Start the ranging session. Call stopRanging(session) when finished.
            session.start(preference)
            return session
        }

        // Stops an active ranging session.
        fun stopRanging(session: RangingSession) {
            session.stop()
        }
    }
    // [END android_connectivity_ranging_session]
}

// The values come from the app's own out-of-band exchange with the iOS device, so they're
// parameters here. Returns the preference so the caller can start a session with it.
private fun createIosInteropPreference(
    sessionId: Int,
    sourceAddress: UwbAddress,
    destinationAddress: UwbAddress,
    channelNumber: Int,
    preambleIndex: Int,
    updateRate: Int,
    sessionKeyInfo: ByteArray,
    slotDuration: Int,
): RangingPreference {
    // [START android_connectivity_ranging_ios_interop]
    // Create UwbRangingParams with iOS-specific configuration.
    val uwbRangingParams = UwbRangingParams.Builder(
        sessionId,
        UwbRangingParams.CONFIG_UNICAST_DS_TWR,
        sourceAddress,
        destinationAddress
    )
        .setComplexChannel(
            UwbComplexChannel.Builder()
                .setChannel(channelNumber)
                .setPreambleIndex(preambleIndex)
                .build()
        )
        .setRangingUpdateRate(updateRate)
        .setSessionKeyInfo(sessionKeyInfo) // Vendor ID + STS IV
        .setSlotDuration(slotDuration)
        .build()

    // Create RawRangingDevice.
    val rawRangingDevice = RawRangingDevice.Builder()
        .setRangingDevice(RangingDevice.Builder().build())
        .setUwbRangingParams(uwbRangingParams)
        .build()

    // Create SessionConfig.
    val sessionConfig = SessionConfig.Builder()
        .setAngleOfArrivalNeeded(true)
        .setDataNotificationConfig(
            DataNotificationConfig.Builder()
                .setNotificationConfigType(DataNotificationConfig.NOTIFICATION_CONFIG_ENABLE)
                .build()
        )
        .build()

    // Create RangingPreference for the initiator.
    val preference = RangingPreference.Builder(
        RangingPreference.DEVICE_ROLE_INITIATOR,
        RawInitiatorRangingConfig.Builder()
            .addRawRangingDevice(rawRangingDevice)
            .build()
    )
        .setSessionConfig(sessionConfig)
        .build()
    // [END android_connectivity_ranging_ios_interop]
    return preference
}

// DL-TDoA (isDlTdoaSupported, DEVICE_ROLE_DT_TAG, RawDtTagRangingConfig, DlTdoaRangingParams and
// onDlTdoaResults) is new in Android 17 (API level 37), above the module's minSdk. The object keeps
// RangingApp and RangingSessionCallback private to this file.
@RequiresApi(37)
private object DlTdoaSnippet {
    // [START android_connectivity_ranging_dl_tdoa]
    class RangingApp {

        fun initDlTdoa(context: Context) {
            // Initialize the Ranging Manager.
            val rangingManager = context.getSystemService(RangingManager::class.java)

            // Register for device capabilities.
            val capabilitiesCallback = object : RangingManager.RangingCapabilitiesCallback {
                override fun onRangingCapabilities(capabilities: RangingCapabilities) {
                    // Make sure DL-TDoA is supported before starting the session.
                    if (capabilities.uwbCapabilities != null && capabilities.uwbCapabilities!!.isDlTdoaSupported) {
                        startDlTDoASession(context)
                    }
                }
            }
            rangingManager.registerCapabilitiesCallback(Executors.newSingleThreadExecutor(), capabilitiesCallback)
        }

        fun startDlTDoASession(context: Context) {
            // Initialize the Ranging Manager.
            val rangingManager = context.getSystemService(RangingManager::class.java)

            // Create session and configure parameters.
            val executor = Executors.newSingleThreadExecutor()
            val rangingSession = rangingManager.createRangingSession(executor, RangingSessionCallback())
            val rangingRoundIndexes = byteArrayOf(0)
            val config: ByteArray = byteArrayOf() // OOB config data
            val params = DlTdoaRangingParams.createFromFiraConfigPacket(config, rangingRoundIndexes)

            val rangingDevice = RangingDevice.Builder().build()
            val rawTagDevice = RawRangingDevice.Builder()
                .setRangingDevice(rangingDevice)
                .setDlTdoaRangingParams(params)
                .build()

            val dtTagConfig = RawDtTagRangingConfig.Builder(rawTagDevice).build()

            val preference = RangingPreference.Builder(DEVICE_ROLE_DT_TAG, dtTagConfig)
                .setSessionConfig(SessionConfig.Builder().build())
                .build()

            // Start the ranging session.
            rangingSession?.start(preference)
        }
    }

    private class RangingSessionCallback : RangingSession.Callback {
        override fun onDlTdoaResults(peer: RangingDevice, measurement: DlTdoaMeasurement) {
            // Process measurement results here.
        }

        override fun onOpened() {
            // ...
        }

        override fun onOpenFailed(reason: Int) {
            // ...
        }

        override fun onStarted(peer: RangingDevice, technology: Int) {
            // ...
        }

        override fun onStopped(peer: RangingDevice, technology: Int) {
            // ...
        }

        override fun onResults(peer: RangingDevice, data: RangingData) {
            // ...
        }

        override fun onClosed(reason: Int) {
            // ...
        }
    }
    // [END android_connectivity_ranging_dl_tdoa]
}

// Returns both payloads so the caller can pass one of them to
// DlTdoaRangingParams.createFromFiraConfigPacket().
private fun createDlTdoaOobConfigs(): Pair<ByteArray, ByteArray> {
    // [START android_connectivity_ranging_dl_tdoa_oob_config]
    // Wi-Fi Configuration (Vendor-Specific IE, FiRa CID 0x5A18FF, Config 0)
    val wifiConfig = byteArrayOf(
        0xDD.toByte(), 0x30, 0x5A, 0x18, 0xFF.toByte(), // Wi-Fi VSE Header
        0x5F, 0x1C, // UWB Configuration Sub-Element
        0x02, 0x00, // Profile ID (Untracked Navigation), Config ID 0
        0x06, 0x02, 0x20, 0x08, // Device MAC Address
        0x14, 0x01, 0x0A, // Preamble Code Index (10)
        0x27, 0x02, 0x08, 0x07, // Vendor ID
        0x28, 0x06, 0xA1.toByte(), 0xB2.toByte(), 0xC3.toByte(), 0xD4.toByte(), 0xE5.toByte(), 0xF6.toByte(), // Static STS IV
        0x08, 0x02, 0x60, 0x09, // Slot Duration (2400 RSTU = 2 ms)
        0x1B, 0x01, 0x0A, // Slots per Ranging Round (10)
        0x09, 0x04, 0xFA.toByte(), 0x00, 0x00, 0x00, // Ranging Duration (250 ms)
        0x52, 0x01, 0x00, // Initiator DT-Anchor Round Index (0)
        0x9F.toByte(), 0x04, 0xAA.toByte(), 0xBB.toByte(), 0xCC.toByte(), 0xDD.toByte() // Session ID (0xDDCCBBAA)
    )

    // BLE Configuration (ADV_NONCONN_IND Service Data UUID 0xFFF3, Config 0)
    val bleConfig = byteArrayOf(
        0x1E, 0x16, 0xF3.toByte(), 0xFF.toByte(), // BLE Service Data Header
        0xF0.toByte(), // Fragment Indication Sub-Element (unfragmented)
        0x5F, 0x09, // UWB Configuration Sub-Element
        0x02, 0x00, // Profile ID (Untracked Navigation), Config ID 0
        0x06, 0x02, 0x20, 0x08, // Device MAC Address
        0x27, 0x02, 0x08, 0x07, // Vendor ID
        0x28, 0x06, 0xA1.toByte(), 0xB2.toByte(), 0xC3.toByte(), 0xD4.toByte(), 0xE5.toByte(), 0xF6.toByte(), // Static STS IV
        0x9F.toByte(), 0x04, 0xAA.toByte(), 0xBB.toByte(), 0xCC.toByte(), 0xDD.toByte() // Session ID (0xDDCCBBAA)
    )
    // [END android_connectivity_ranging_dl_tdoa_oob_config]
    return wifiConfig to bleConfig
}

// DlTdoaRangingParams.Builder is new in Android 17 (API level 37). The device address comes from
// the app, so it's a parameter, and the params are returned for the caller to use.
@RequiresApi(37)
private fun buildDlTdoaRangingParams(deviceAddress: UwbAddress): DlTdoaRangingParams {
    // [START android_connectivity_ranging_dl_tdoa_params_builder]
    val dlTdoaParams = DlTdoaRangingParams.Builder(1)
        .setComplexChannel(
            UwbComplexChannel.Builder()
                .setChannel(9).setPreambleIndex(10).build()
        )
        .setDeviceAddress(deviceAddress)
        .setSessionKeyInfo(byteArrayOf(0x01, 0x02, 0x03, 0x04))
        .setRangingIntervalMillis(240)
        .setSlotDuration(UwbRangingParams.DURATION_2_MS)
        .setSlotsPerRangingRound(20)
        .setRangingRoundIndexes(byteArrayOf(0x01, 0x05))
        .build()
    // [END android_connectivity_ranging_dl_tdoa_params_builder]
    return dlTdoaParams
}
