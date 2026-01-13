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

package com.example.wear.snippets.watchfacepush

import android.content.Context
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.wear.watchfacepush.WatchFacePushManager
import androidx.wear.watchfacepush.WatchFacePushManagerFactory

const val TAG = "WatchFacePushSnippetManager"

class WatchFacePushSnippetManager(context: Context) {
    // [START android_wear_wfp_manager]
    val watchFacePushManager = WatchFacePushManagerFactory.createWatchFacePushManager(context)
    // [END android_wear_wfp_manager]

    suspend fun listWatchFaces() {
        // [START android_wear_wfp_list_watch_faces]
        val response = watchFacePushManager.listWatchFaces()
        val installedList = response.installedWatchFaceDetails
        val remainingSlots = response.remainingSlotCount
        // [END android_wear_wfp_list_watch_faces]
    }

    // [START android_wear_wfp_list_is_wf_installed]
    suspend fun isInstalled(packageName: String) = watchFacePushManager.listWatchFaces()
        .installedWatchFaceDetails.any { it.packageName == packageName }
    // [END android_wear_wfp_list_is_wf_installed]

    suspend fun addWatchFace(parcelFileDescriptor: ParcelFileDescriptor, token: String) {
        // [START android_wear_wfp_add_watch_face]
        try {
            // Supply the validation token along with the watch face package data itself.
            val slot = watchFacePushManager.addWatchFace(parcelFileDescriptor, token)
            Log.i(TAG, "${slot.packageName} (${slot.versionCode}) added in slot ${slot.slotId}")
        } catch (e: WatchFacePushManager.AddWatchFaceException) {
            // Something went wrong adding the watch face.
        }
        // [END android_wear_wfp_add_watch_face]
    }

    suspend fun updateWatchFace(
        redParcelFileDesc: ParcelFileDescriptor,
        redValidationToken: String
    ) {
        // [START android_wear_wfp_replace_watch_face]
        // Replacing the com.example.watchfacepush.green watch face with
        // com.example.watchfacepush.red
        val slotId =
            watchFacePushManager.listWatchFaces().installedWatchFaceDetails.firstOrNull { it.packageName == "com.example.watchfacepush.green" }?.slotId
                ?: throw Exception("No green watch face found")
        try {
            watchFacePushManager.updateWatchFace(slotId, redParcelFileDesc, redValidationToken)
        } catch (e: WatchFacePushManager.UpdateWatchFaceException) {
            // Something went wrong updating the watch face.
        }
        // [END android_wear_wfp_replace_watch_face]
    }

    suspend fun removeWatchFace() {
        // [START android_wear_wfp_remove_watch_face]
        // Remove the com.example.watchfacepush.green watch face.
        val slotId =
            watchFacePushManager.listWatchFaces().installedWatchFaceDetails.firstOrNull { it.packageName == "com.example.watchfacepush.green" }?.slotId
                ?: throw Exception("No green watch face found")

        try {
            watchFacePushManager.removeWatchFace(slotId)
        } catch (e: WatchFacePushManager.RemoveWatchFaceException) {
            // Something went wrong removing the watch face.
        }
        // [END android_wear_wfp_remove_watch_face]
    }

    // [START android_wear_wfp_has_active_watch_face]
    suspend fun hasActiveWatchFace() = watchFacePushManager.listWatchFaces()
        .installedWatchFaceDetails
        .any {
            watchFacePushManager.isWatchFaceActive(it.packageName)
        }
    // [END android_wear_wfp_has_active_watch_face]

    fun getMetaData(watchFaceDetails: WatchFacePushManager.WatchFaceDetails) {
        // [START android_wear_wfp_get_meta_data]
        watchFaceDetails
            .getMetaData("com.myapp.watchfacepush.mywatchface.default_color")
            .invoke()
        // [END android_wear_wfp_get_meta_data]
    }
}
