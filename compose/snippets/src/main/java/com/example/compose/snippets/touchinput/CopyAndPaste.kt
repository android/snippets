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

@file:OptIn(ExperimentalFoundationApi::class)

package com.example.compose.snippets.touchinput

import android.content.ClipData
import android.content.ClipDescription
import android.net.Uri
import android.os.Build
import android.os.PersistableBundle
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.content.MediaType
import androidx.compose.foundation.content.ReceiveContentListener
import androidx.compose.foundation.content.consume
import androidx.compose.foundation.content.contentReceiver
import androidx.compose.foundation.content.hasMediaType
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ClipEntry
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp

@Composable
private fun TextCopyPasteSample() {
    // [START android_compose_touchinput_copyandpaste_selection_container]
    val textFieldState = rememberTextFieldState()

    Column {
        Card {
            SelectionContainer {
                Text("You can copy this text")
            }
        }
        BasicTextField(state = textFieldState)
    }
    // [END android_compose_touchinput_copyandpaste_selection_container]
}

@Composable
private fun CopyWithClipboardManagerSample() {
    // [START android_compose_touchinput_copyandpaste_set_text]
    // Retrieve a ClipboardManager object
    val clipboardManager = LocalClipboardManager.current

    Button(
        onClick = {
            // Copy "Hello, clipboard" to the clipboard
            clipboardManager.setText(AnnotatedString("Hello, clipboard"))
        }
    ) {
        Text("Click to copy a text")
    }
    // [END android_compose_touchinput_copyandpaste_set_text]
}

@Composable
private fun CopyWithClipEntrySample() {
    // [START android_compose_touchinput_copyandpaste_clip_entry]
    // Retrieve a ClipboardManager object
    val clipboardManager = LocalClipboardManager.current

    Button(
        onClick = {
            val clipData = ClipData.newPlainText("plain text", "Hello, clipboard")
            val clipEntry = ClipEntry(clipData)
            clipboardManager.setClip(clipEntry)
        }
    ) {
        Text("Click to copy a text")
    }
    // [END android_compose_touchinput_copyandpaste_clip_entry]
}

@Composable
private fun PasteWithClipboardManagerSample() {
    // [START android_compose_touchinput_copyandpaste_paste_text]
    // [START_EXCLUDE]
    val clipboardManager = LocalClipboardManager.current
    // [END_EXCLUDE]
    val textFieldState = rememberTextFieldState()

    Column {
        TextField(state = textFieldState)

        Button(
            onClick = {
                // The getText method returns an AnnotatedString object or null
                val annotatedString = clipboardManager.getText()
                if (annotatedString != null) {
                    // The pasted text is placed on the tail of the TextField
                    textFieldState.edit {
                        append(annotatedString.text)
                    }
                }
            }
        ) {
            Text("Click to paste the text in the clipboard")
        }
    }
    // [END android_compose_touchinput_copyandpaste_paste_text]
}

@Composable
private fun CopyRichContentSample() {
    // [START android_compose_touchinput_copyandpaste_copy_rich_content]
    // [START_EXCLUDE]
    val clipboardManager = LocalClipboardManager.current
    // [END_EXCLUDE]
    // Get a reference to the context
    val context = LocalContext.current

    Button(
        onClick = {
            // URI of the copied JPEG data
            val uri = Uri.parse("content://your.app.authority/0.jpg")
            // Create a ClipData object from the URI value
            // A ContentResolver finds a proper ContentProvider so that ClipData.newUri can set appropriate MIME type to the given URI
            val clipData = ClipData.newUri(context.contentResolver, "Copied", uri)
            // Create a ClipEntry object from the clipData value
            val clipEntry = ClipEntry(clipData)
            // Copy the JPEG data to the clipboard
            clipboardManager.setClip(clipEntry)
        }
    ) {
        Text("Copy a JPEG data")
    }
    // [END android_compose_touchinput_copyandpaste_copy_rich_content]
}

@Composable
private fun PasteRichContentSample() {
    // [START android_compose_touchinput_copyandpaste_paste_rich_content]
    // A URI list of images
    val imageList = remember { mutableStateListOf<Uri>() }

    // Remember the ReceiveContentListener object as it is created inside a Composable scope
    val receiveContentListener = remember {
        ReceiveContentListener { transferableContent ->
            // Handle the pasted data if it is image data
            when {
                // Check if the pasted data is an image or not
                transferableContent.hasMediaType(MediaType.Image) -> {
                    // Handle for each ClipData.Item object
                    // The consume() method returns a new TransferableContent object containging ignored ClipData.Item objects
                    transferableContent.consume { item ->
                        val uri = item.uri
                        if (uri != null) {
                            imageList.add(uri)
                        }
                        // Mark the ClipData.Item object consumed when the retrieved URI is not null
                        uri != null
                    }
                }
                // Return the given transferableContent when the pasted data is not an image
                else -> transferableContent
            }
        }
    }

    val textFieldState = rememberTextFieldState()

    BasicTextField(
        state = textFieldState,
        modifier = Modifier
            .contentReceiver(receiveContentListener)
            .fillMaxWidth()
            .height(48.dp)
    )
    // [END android_compose_touchinput_copyandpaste_paste_rich_content]
}

private fun HasMediaTypeSample(transferableContent: androidx.compose.foundation.content.TransferableContent) {
    // [START android_compose_touchinput_copyandpaste_has_media_type]
    transferableContent.hasMediaType(MediaType.Image)
    // [END android_compose_touchinput_copyandpaste_has_media_type]
}

@RequiresApi(Build.VERSION_CODES.TIRAMISU)
private fun SensitiveContentSample(clipData: ClipData) {
    // [START android_compose_touchinput_copyandpaste_sensitive_content]
    // If your app is compiled with the API level 33 SDK or higher.
    clipData.apply {
        description.extras = PersistableBundle().apply {
            putBoolean(ClipDescription.EXTRA_IS_SENSITIVE, true)
        }
    }

    // If your app is compiled with a lower SDK.
    clipData.apply {
        description.extras = PersistableBundle().apply {
            putBoolean("android.content.extra.IS_SENSITIVE", true)
        }
    }
    // [END android_compose_touchinput_copyandpaste_sensitive_content]
}
