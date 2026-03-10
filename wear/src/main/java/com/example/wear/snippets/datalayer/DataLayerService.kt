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

package com.example.wear.snippets.datalayer

import android.app.Activity
import android.util.Log
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.DataItemBuffer
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import com.google.android.gms.wearable.WearableListenerService
import kotlinx.coroutines.runBlocking

private const val TAG = "DataLayerSample"
private const val START_ACTIVITY_PATH = "/start-activity"
private const val DATA_ITEM_RECEIVED_PATH = "/data-item-received"

// [START android_wear_datalayer_datalayerlistenerservice]
class DataLayerListenerService : WearableListenerService() {

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        if (Log.isLoggable(TAG, Log.DEBUG)) {
            Log.d(TAG, "onDataChanged: $dataEvents")
        }

        // Loop through the events and send a message
        // to the node that created the data item.
        dataEvents
            .map { it.dataItem.uri }
            .forEach { uri ->
                // Get the node ID from the host value of the URI.
                val nodeId: String = uri.host!!
                // Set the data of the message to be the bytes of the URI.
                val payload: ByteArray = uri.toString().toByteArray()

                // Send the RPC.
                Wearable.getMessageClient(this)
                    .sendMessage(
                        nodeId,
                        DATA_ITEM_RECEIVED_PATH,
                        payload
                    )
            }
    }
}
// [END android_wear_datalayer_datalayerlistenerservice]

// [START android_wear_datalayer_auth_token_sharing_listener]
class AuthDataListenerService : WearableListenerService() {
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_CHANGED) {
                val dataItemPath = event.dataItem.uri.path ?: ""

                if (dataItemPath.startsWith("/auth")) {
                    val token = DataMapItem.fromDataItem(event.dataItem)
                        .dataMap
                        .getString("token")
                    // Display an interstitial screen to notify the user that they're being signed
                    // in. Then, store the token and use it in network requests.
                    handleSignInSequence(token)
                }
            }
        }
    }

    /** placeholder sign in handler. */
    fun handleSignInSequence(token: String?) {}
}
// [END android_wear_datalayer_auth_token_sharing_listener]

// [START android_wear_datalayer_ondatachangedlisteneer]
class MainActivity : Activity(), DataClient.OnDataChangedListener {

    public override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(this)
    }

    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents.forEach { event ->
            if (event.type == DataEvent.TYPE_DELETED) {
                Log.d(TAG, "DataItem deleted: " + event.dataItem.uri)
            } else if (event.type == DataEvent.TYPE_CHANGED) {
                Log.d(TAG, "DataItem changed: " + event.dataItem.uri)
            }
        }
    }
}
// [END android_wear_datalayer_ondatachangedlisteneer]

// [START android_wear_datalayer_mywearablelistenerservice]
class MyWearableListenerService : WearableListenerService() {
    val dataClient: DataClient = Wearable.getDataClient(this)

    private fun shouldHandleDataItem(nodeId: String, dataItem: DataItem): Boolean {
        // Your logic here
        return dataItem.uri.path?.startsWith("/my_feature_path/") == true
    }

    private fun handleDataItem(nodeId: String, dataItem: DataItem) {
        val data = dataItem.data ?: return
        val path = dataItem.uri.path ?: return
        // Your logic here
        if (data.toString().startsWith("Please restore")) {
            dataClient.putDataItem(PutDataRequest.create(path).setData(data))
        }
    }

    override fun onNodeMigrated(nodeId: String, archive: DataItemBuffer) {
        val dataItemsToHandle = mutableListOf<DataItem>()

        for (dataItem in archive) {
            if (shouldHandleDataItem(nodeId, dataItem)) {
                dataItemsToHandle.add(dataItem.freeze())
            }
        }

        // Callback stops automatically after 20 seconds of data processing.
        // If you think you need more time, delegate to a coroutine or thread.
        runBlocking {
            for (dataItem in dataItemsToHandle) {
                handleDataItem(nodeId, dataItem)
            }
        }
    }
}
// [END android_wear_datalayer_mywearablelistenerservice]
