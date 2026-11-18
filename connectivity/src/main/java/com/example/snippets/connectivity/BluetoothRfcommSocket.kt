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

import android.bluetooth.BluetoothSocket
import android.util.Log
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream

// [START android_connectivity_bluetooth_rfcomm_read_eof]
class ConnectedBluetoothThread(private val socket: BluetoothSocket) : Thread() {

    private val inputStream: InputStream? = try {
        socket.inputStream
    } catch (e: IOException) {
        Log.e(TAG, "Error securing input stream", e)
        null
    }

    private val outputStream: OutputStream? = try {
        socket.outputStream
    } catch (e: IOException) {
        Log.e(TAG, "Error securing output stream", e)
        null
    }

    private val buffer = ByteArray(1024) // 1KB Buffer for read cycles

    override fun run() {
        if (inputStream == null) {
            handleDisconnection("Cannot run reader: InputStream is null")
            return
        }

        Log.i(TAG, "Bluetooth reader thread started.")

        while (true) {
            try {
                // Read from the InputStream. This blocks until data is available.
                val numBytes = inputStream.read(buffer)

                // CASE 1: [Android 17+ / API 37+] Socket was closed or connection dropped cleanly
                if (numBytes == -1) {
                    handleDisconnection("Input stream EOF reached (-1). Connection closed.")
                    break
                }

                // Process the valid byte payload
                processIncomingData(buffer, numBytes)
            } catch (e: IOException) {
                // CASE 2: [Pre-Android 17] Legacy platform behavior on socket closure,
                // OR actual network transport failures / hardware faults.
                handleDisconnection("Input stream threw IOException: ${e.localizedMessage}")
                break
            }
        }
    }

    /**
     * Sends the read bytes to your app's handler or processing layer.
     */
    private fun processIncomingData(data: ByteArray, length: Int) {
        val payload = data.copyOf(length)
        Log.d(TAG, "Received $length bytes: ${payload.joinToString()}")
        // TODO: Forward 'payload' to your LiveData, Flow, or Handler UI state
    }

    /**
     * Standardizes cleanup actions when a disconnect event is detected.
     */
    private fun handleDisconnection(reason: String) {
        Log.w(TAG, "Disconnected: $reason")
        closeSocket()
        // TODO: Update UI or notify a manager service to attempt a reconnect
    }

    /**
     * Safely closes the socket to release system resources.
     */
    fun closeSocket() {
        try {
            socket.close()
            Log.i(TAG, "Bluetooth socket closed successfully.")
        } catch (e: IOException) {
            Log.e(TAG, "Failed to close Bluetooth socket", e)
        }
    }

    companion object {
        private const val TAG = "BT_ConnectedThread"
    }
}
// [END android_connectivity_bluetooth_rfcomm_read_eof]
