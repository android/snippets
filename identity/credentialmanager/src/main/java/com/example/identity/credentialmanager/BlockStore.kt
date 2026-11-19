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

package com.example.identity.credentialmanager

import android.content.ContentValues
import android.util.Log
import androidx.activity.ComponentActivity
import com.google.android.gms.auth.blockstore.Blockstore
import com.google.android.gms.auth.blockstore.BlockstoreClient
import com.google.android.gms.auth.blockstore.DeleteBytesRequest
import com.google.android.gms.auth.blockstore.RetrieveBytesRequest
import com.google.android.gms.auth.blockstore.RetrieveBytesResponse
import com.google.android.gms.auth.blockstore.StoreBytesData
import java.util.Arrays

private class BlockStoreActivity : ComponentActivity() {

    private fun saveToken() {
        // [START android_identity_block_store_save_token]
        val client = Blockstore.getClient(this)

        val bytes1 = byteArrayOf(1, 2, 3, 4) // Store one data block.
        val key1 = "com.example.app.key1"
        val storeRequest1 = StoreBytesData.Builder()
            .setBytes(bytes1) // Call this method to set the key value with which the data should be associated with.
            .setKey(key1)
            .build()
        client.storeBytes(storeRequest1)
            .addOnSuccessListener { result: Int ->
                Log.d(
                    TAG,
                    "Stored $result bytes"
                )
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to store bytes", e)
            }
        // [END android_identity_block_store_save_token]
    }

    private fun saveDefaultToken() {
        // [START android_identity_block_store_save_default_token]
        val client = Blockstore.getClient(this)
        // the default key BlockstoreClient.DEFAULT_BYTES_DATA_KEY.
        val bytes = byteArrayOf(1, 2, 3, 4)
        val storeRequest = StoreBytesData.Builder()
            .setBytes(bytes)
            .build()
        client.storeBytes(storeRequest)
            .addOnSuccessListener { result: Int ->
                Log.d(
                    TAG,
                    "stored $result bytes"
                )
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to store bytes", e)
            }
        // [END android_identity_block_store_save_default_token]
    }

    private fun retrieveTokens() {
        // [START android_identity_block_store_retrieve_tokens]
        val client = Blockstore.getClient(this)

        // Retrieve data associated with certain keys.
        val key1 = "com.example.app.key1"
        val key2 = "com.example.app.key2"
        val key3 = BlockstoreClient.DEFAULT_BYTES_DATA_KEY // Used to retrieve data stored without a key.

        val requestedKeys = Arrays.asList(key1, key2, key3) // Add keys to array.

        val retrieveRequest = RetrieveBytesRequest.Builder()
            .setKeys(requestedKeys)
            .build()

        client.retrieveBytes(retrieveRequest)
            .addOnSuccessListener { result: RetrieveBytesResponse ->
                val blockstoreDataMap =
                    result.blockstoreDataMap
                for ((key, value) in blockstoreDataMap) {
                    Log.d(
                        ContentValues.TAG,
                        String.format(
                            "Retrieved bytes %s associated with key %s.",
                            String(value.bytes),
                            key
                        )
                    )
                }
            }
            .addOnFailureListener { e: Exception? ->
                Log.e(
                    ContentValues.TAG,
                    "Failed to store bytes",
                    e
                )
            }
        // [END android_identity_block_store_retrieve_tokens]
    }

    private fun retrieveAllTokens() {
        // [START android_identity_block_store_retrieve_all_tokens]
        val client = Blockstore.getClient(this)

        val retrieveRequest = RetrieveBytesRequest.Builder()
            .setRetrieveAll(true)
            .build()

        client.retrieveBytes(retrieveRequest)
            .addOnSuccessListener { result: RetrieveBytesResponse ->
                val blockstoreDataMap =
                    result.blockstoreDataMap
                for ((key, value) in blockstoreDataMap) {
                    Log.d(
                        ContentValues.TAG,
                        String.format(
                            "Retrieved bytes %s associated with key %s.",
                            String(value.bytes),
                            key
                        )
                    )
                }
            }
            .addOnFailureListener { e: Exception? ->
                Log.e(
                    ContentValues.TAG,
                    "Failed to store bytes",
                    e
                )
            }
        // [END android_identity_block_store_retrieve_all_tokens]
    }

    private fun retrieveDefaultToken() {
        // [START android_identity_block_store_retrieve_default_token]
        val client = Blockstore.getClient(this)

        val retrieveRequest = RetrieveBytesRequest.Builder()
            .setKeys(Arrays.asList(BlockstoreClient.DEFAULT_BYTES_DATA_KEY))
            .build()
        client.retrieveBytes(retrieveRequest)
        // [END android_identity_block_store_retrieve_default_token]
    }

    private fun deleteTokens() {
        // [START android_identity_block_store_delete_tokens]
        val client = Blockstore.getClient(this)

        // Retrieve data associated with certain keys.
        val key1 = "com.example.app.key1"
        val key2 = "com.example.app.key2"
        val key3 = BlockstoreClient.DEFAULT_BYTES_DATA_KEY // Used to retrieve data stored without a key.

        val requestedKeys = Arrays.asList(key1, key2, key3) // Add keys to array.

        val retrieveRequest = DeleteBytesRequest.Builder()
            .setKeys(requestedKeys)
            .build()

        client.deleteBytes(retrieveRequest)
        // [END android_identity_block_store_delete_tokens]
    }

    private fun deleteAllTokens(client: BlockstoreClient) {
        // [START android_identity_block_store_delete_all_tokens]
        // Delete all data.
        val deleteAllRequest = DeleteBytesRequest.Builder()
            .setDeleteAll(true)
            .build()
        client.deleteBytes(deleteAllRequest)
            .addOnSuccessListener { result -> Log.d(TAG, "Any data found and deleted? $result") }
        // [END android_identity_block_store_delete_all_tokens]
    }

    private fun checkEndToEndEncryptionAvailability(client: BlockstoreClient) {
        // [START android_identity_block_store_check_e2ee]
        client.isEndToEndEncryptionAvailable()
            .addOnSuccessListener { result ->
                Log.d(TAG, "Will Block Store cloud backup be end-to-end encrypted? $result")
            }
        // [END android_identity_block_store_check_e2ee]
    }

    private fun enableCloudBackup() {
        val bytes = byteArrayOf(1, 2, 3, 4)
        // [START android_identity_block_store_enable_cloud_backup]
        val client = Blockstore.getClient(this)
        val storeBytesDataBuilder = StoreBytesData.Builder()
            .setBytes(bytes)

        client.isEndToEndEncryptionAvailable()
            .addOnSuccessListener { isE2EEAvailable ->
                if (isE2EEAvailable) {
                    storeBytesDataBuilder.setShouldBackupToCloud(true)
                    Log.d(TAG, "E2EE is available, enable backing up bytes to the cloud.")

                    client.storeBytes(storeBytesDataBuilder.build())
                        .addOnSuccessListener { result ->
                            Log.d(TAG, "stored: $result")
                        }.addOnFailureListener { e ->
                            Log.e(TAG, "Failed to store bytes", e)
                        }
                } else {
                    Log.d(TAG, "E2EE is not available, only store bytes for D2D restore.")
                }
            }
        // [END android_identity_block_store_enable_cloud_backup]
    }

    private companion object {
        private const val TAG = "BlockStore"
    }
}
