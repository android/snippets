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

package com.example.wear.snippets.datalayer

import android.content.Context
import android.net.Uri
import com.google.android.gms.wearable.ChannelClient
import com.google.android.gms.wearable.Wearable
import java.io.File
import java.io.InputStream
import java.io.OutputStream
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

// [START android_wear_datalayer_send_file]
suspend fun sendFileToNode(context: Context, nodeId: String, fileUri: Uri) {
    val channelClient = Wearable.getChannelClient(context)
    val path = "/my_file_transfer_path"

    // Moving to an IO dispatcher is best practice for file/network operations
    withContext(Dispatchers.IO) {
        try {
            // Suspend until the channel is successfully opened
            val channel = channelClient.openChannel(nodeId, path).await()

            // Suspend until the file has finished sending
            channelClient.sendFile(channel, fileUri).await()

            // File transfer completed successfully!
        } catch (e: Exception) {
            // Handle failures (e.g., node disconnected, file not found)
            e.printStackTrace()
        }
    }
}
// [END android_wear_datalayer_send_file]

// [START android_wear_datalayer_stream]
suspend fun handleDataStream(channelClient: ChannelClient, channel: ChannelClient.Channel) {
    withContext(Dispatchers.IO) {
        try {
            // Await the creation of the Output/Input streams
            val outputStream: OutputStream = channelClient.getOutputStream(channel).await()
            val inputStream: InputStream = channelClient.getInputStream(channel).await()

            // You can now read from inputStream and write to outputStream
            // Make sure to manage your stream loops and close them when finished
        } catch (e: Exception) {
            // Handle failures
        }
    }
}
// [END android_wear_datalayer_stream]

class FileReceiver(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) : ChannelClient.ChannelCallback() {
    private val channelClient = Wearable.getChannelClient(context)

    fun register() {
        // [START android_wear_datalayer_channelclient_register]
        channelClient.registerChannelCallback(this)
        // [END android_wear_datalayer_channelclient_register]
    }

    fun unregister() {
        channelClient.unregisterChannelCallback(this)
    }

    // [START android_wear_datalayer_channelclient_receivefile]
    override fun onChannelOpened(channel: ChannelClient.Channel) {
        super.onChannelOpened(channel)

        if (channel.path == "/my_file_transfer_path") {
            val destinationFile = File(context.filesDir, "received_file.ext")
            val destinationUri = Uri.fromFile(destinationFile)

            coroutineScope.launch(Dispatchers.IO) {
                try {
                    // await() suspends until the entire file has been transferred
                    channelClient.receiveFile(channel, destinationUri, false).await()

                    // The file is now fully saved to destinationFile
                } catch (e: Exception) {
                    // Handle transfer failure (e.g., node disconnected midway)
                    e.printStackTrace()
                }
            }
        }
    }
    // [END android_wear_datalayer_channelclient_receivefile]
}
