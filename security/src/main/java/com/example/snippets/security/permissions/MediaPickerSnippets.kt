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

package com.example.snippets.security.permissions

import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts

// [START android_security_photo_picker_request]
class MediaPickerActivity : ComponentActivity() {

    // Registers a photo picker activity launcher in single-select mode.
    // The photo picker provides safe, direct access to media items without requiring
    // READ_EXTERNAL_STORAGE or READ_MEDIA_IMAGES permissions.
    private val launcher =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri: Uri? ->
            if (uri != null) {
                // Access the media directly using the returned URI without storage permissions.
                handleSelectedImage(uri)
            } else {
                handleNoImageSelected()
            }
        }

    fun selectPhoto() {
        launcher.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
    }

    private fun handleSelectedImage(uri: Uri) {
        // Direct URI access without requesting storage permissions
    }

    private fun handleNoImageSelected() {
        // Picker was cancelled or no photo was selected
    }
}
// [END android_security_photo_picker_request]
