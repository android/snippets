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

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.activity.ComponentActivity
import com.example.wear.R
import com.google.android.gms.tasks.Task
import com.google.android.gms.tasks.Tasks
import com.google.android.gms.wearable.Asset
import com.google.android.gms.wearable.DataClient
import com.google.android.gms.wearable.DataEvent
import com.google.android.gms.wearable.DataEventBuffer
import com.google.android.gms.wearable.DataItem
import com.google.android.gms.wearable.DataMapItem
import com.google.android.gms.wearable.PutDataMapRequest
import com.google.android.gms.wearable.PutDataRequest
import com.google.android.gms.wearable.Wearable
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.concurrent.ExecutionException

class DataLayerActivity : ComponentActivity(), DataClient.OnDataChangedListener {
    private val dataClient by lazy { Wearable.getDataClient(this) }
    private val messageClient by lazy { Wearable.getMessageClient(this) }
    private val capabilityClient by lazy { Wearable.getCapabilityClient(this) }

    private var count = 0

    override fun onResume() {
        super.onResume()
        Wearable.getDataClient(this).addListener(this)
    }

    override fun onPause() {
        super.onPause()
        Wearable.getDataClient(this).removeListener(this)
    }

    // [START android_wear_datalayer_increasecounter]
    private fun increaseCounter() {
        val putDataReq: PutDataRequest = PutDataMapRequest.create("/count").run {
            dataMap.putInt(COUNT_KEY, count++)
            asPutDataRequest()
        }
        val putDataTask: Task<DataItem> = dataClient.putDataItem(putDataReq)
    }
    // [END android_wear_datalayer_increasecounter]

    // [START android_wear_datalayer_ondatachangedlistener]
    override fun onDataChanged(dataEvents: DataEventBuffer) {

        dataEvents.forEach { event ->
            // DataItem changed
            if (event.type == DataEvent.TYPE_CHANGED) {
                event.dataItem.also { item ->
                    if (item.uri.path?.compareTo("/count") == 0) {
                        DataMapItem.fromDataItem(item).dataMap.apply {
                            updateCount(getInt(COUNT_KEY))
                        }
                    }
                }
            } else if (event.type == DataEvent.TYPE_DELETED) {
                // DataItem deleted
            }
        }
    }
    // [END android_wear_datalayer_ondatachangedlistener]

    private fun updateCount(int: Int) {
    }
    companion object {
        private const val COUNT_KEY = "com.example.key.count"
    }
}

// [START android_wear_sync_createasset]
private fun createAssetFromBitmap(bitmap: Bitmap): Asset =
    ByteArrayOutputStream().let { byteStream ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteStream)
        Asset.createFromBytes(byteStream.toByteArray())
    }
// [END android_wear_sync_createasset]

// [START android_wear_datalayer_imageputdata]
private fun Context.sendImagePutDataRequest(): Task<DataItem> {

    val asset: Asset = createAssetFromBitmap(BitmapFactory.decodeResource(resources, R.drawable.ic_walk))
    val request: PutDataRequest = PutDataRequest.create("/image").apply {
        putAsset("profileImage", asset)
    }
    val putTask: Task<DataItem> = Wearable.getDataClient(this).putDataItem(request)

    return putTask
}
// [END android_wear_datalayer_imageputdata]

// [START android_wear_datalayer_imageputdatamap]
private fun Context.sendImagePutDataMapRequest(): Task<DataItem> {

    val asset: Asset = createAssetFromBitmap(BitmapFactory.decodeResource(resources, R.drawable.ic_walk))
    val request: PutDataRequest = PutDataMapRequest.create("/image").run {
        dataMap.putAsset("profileImage", asset)
        asPutDataRequest()
    }
    val putTask: Task<DataItem> = Wearable.getDataClient(this).putDataItem(request)

    return putTask
}
// [END android_wear_datalayer_imageputdatamap]

private fun Context.sendAuthTokenPutDataMapRequest(): Task<DataItem> {
    // [START android_wear_datalayer_auth_token_sharing]

    val token = "..." // Auth token to transmit to the Wear OS device.
    val putDataReq: PutDataRequest = PutDataMapRequest.create("/auth").run {
        dataMap.putString("token", token)
        asPutDataRequest()
    }
    val putDataTask: Task<DataItem> = Wearable.getDataClient(this).putDataItem(putDataReq)
    // [END android_wear_datalayer_auth_token_sharing]

    return putDataTask
}

class DataLayerActivity2 : ComponentActivity(), DataClient.OnDataChangedListener {
    // [START android_wear_datalayer_ondatachanged_assetextract]
    override fun onDataChanged(dataEvents: DataEventBuffer) {
        dataEvents
            .filter { it.type == DataEvent.TYPE_CHANGED && it.dataItem.uri.path == "/image" }
            .forEach { event ->
                val bitmap: Bitmap? = DataMapItem.fromDataItem(event.dataItem)
                    .dataMap.getAsset("profileImage")
                    ?.let { asset -> loadBitmapFromAsset(asset) }
                // Do something with the bitmap
            }
    }

    fun loadBitmapFromAsset(asset: Asset): Bitmap? {
        // Convert asset into a file descriptor and block until it's ready
        val assetInputStream: InputStream? =
            Tasks.await(Wearable.getDataClient(this).getFdForAsset(asset))
                ?.inputStream

        return assetInputStream?.let { inputStream ->
            // Decode the stream into a bitmap
            BitmapFactory.decodeStream(inputStream)
        } ?: run {
            // Requested an unknown asset
            null
        }
    }
    // [END android_wear_datalayer_ondatachanged_assetextract]
}

private fun Context.syncDataAsync(count: Int) {
    val putDataReq: PutDataRequest = PutDataMapRequest.create("/count").run {
        dataMap.putInt("count_key", count)
        asPutDataRequest()
    }

    val task: Task<DataItem> = Wearable.getDataClient(this).putDataItem(putDataReq)
    // [START android_wear_datalayer_async_call]
    // Using Kotlin function references
    task.addOnSuccessListener(::handleDataItem)
    task.addOnFailureListener(::handleDataItemError)
    task.addOnCompleteListener(::handleTaskComplete)
    // [START_EXCLUDE]
}
// [END_EXCLUDE]
private fun handleDataItem(dataItem: DataItem) { }
private fun handleDataItemError(exception: Exception) { }
private fun handleTaskComplete(task: Task<DataItem>) { }
// [END android_wear_datalayer_async_call]

private fun Context.syncDataSync(count: Int) {
    val putDataReq: PutDataRequest = PutDataMapRequest.create("/count").run {
        dataMap.putInt("count_key", count)
        asPutDataRequest()
    }
    val task: Task<DataItem> = Wearable.getDataClient(this).putDataItem(putDataReq)
    // [START android_wear_datalayer_sync_call]
    try {
        Tasks.await(task).apply {
            // Add your logic here
        }
    } catch (e: ExecutionException) {
        // TODO: Handle exception
    } catch (e: InterruptedException) {
        // TODO: Handle exception
        Thread.currentThread().interrupt()
    }
    // [END android_wear_datalayer_sync_call]
}
