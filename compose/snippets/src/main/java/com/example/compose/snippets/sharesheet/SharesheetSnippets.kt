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

package com.example.compose.snippets.sharesheet

import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.graphics.drawable.Icon
import android.net.Uri
import android.os.Bundle
import android.service.chooser.ChooserAction
import android.service.chooser.ChooserTarget
import androidx.activity.ComponentActivity
import androidx.core.content.IntentCompat
import com.example.compose.snippets.R

class SharesheetSnippets : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // [START android_handle_intent_action_data_sent]
        when (intent?.action) {
            Intent.ACTION_SEND -> {
                if ("text/plain" == intent.type) {
                    handleSendText(intent) // Handle text being sent.
                } else if (intent.type?.startsWith("image/") == true) {
                    handleSendImage(intent) // Handle single image being sent
                }
            }

            Intent.ACTION_SEND_MULTIPLE
            if intent.type?.startsWith("image/") == true -> {
                handleSendMultipleImages(intent) // Handle multiple images being sent
            }

            else -> {
                // Handle other intents, such as being started from the home screen
            }
        }
    }

    private fun handleSendText(intent: Intent) {
        intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            // Update UI to reflect text being shared
        }
    }

    private fun handleSendImage(intent: Intent) {
        IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri::class.java).let {
            // Update UI to reflect image being shared
        }
    }

    private fun handleSendMultipleImages(intent: Intent) {
        IntentCompat.getParcelableArrayListExtra(intent, Intent.EXTRA_STREAM, Uri::class.java).let {
            // Update UI to reflect multiple images being shared
        }
    }
    // [END android_handle_intent_action_data_sent]

    private fun handleSendAndExtraText(intent: Intent) {
        // [START android_handle_intent_handle_extra_text]
        IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri::class.java).let {
            // Handle the EXTRA_TEXT as well
            val extraText = intent.getCharSequenceExtra(Intent.EXTRA_TEXT)
            // Update UI to reflect image being shared and the EXTRA_TEXT
            // if available
        }
        // [END android_handle_intent_handle_extra_text]
    }

    private fun shareText() {
        // [START android_handle_share_text]
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
            type = "text/plain"
        }

        val shareIntent = Intent.createChooser(sendIntent, null)
        startActivity(shareIntent)
        // [END android_handle_share_text]
    }

    private fun shareBinaryContent() {
        // [START android_handle_share_image]
        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            // Example: content://com.google.android.apps.photos.contentprovider/...
            val imageUri: Uri =
                Uri.parse("content://com.google.android.apps.photos.contentprovider/0/1/mediakey/1")
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = "image/jpeg"
        }
        startActivity(Intent.createChooser(shareIntent, null))
        // [END android_handle_share_image]
    }

    private fun shareMultiple() {
        // [START android_handle_share_multiple]
        val imageUris: ArrayList<Uri> = arrayListOf(
            Uri.parse("content://com.google.android.apps.photos.contentprovider/0/1/mediakey/1"),
            Uri.parse("content://com.google.android.apps.photos.contentprovider/0/1/mediakey/2")
        )

        val shareIntent = Intent().apply {
            action = Intent.ACTION_SEND_MULTIPLE
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
            type = "image/*"
        }
        startActivity(Intent.createChooser(shareIntent, null))
        // [END android_handle_share_multiple]
    }

    private fun richContentToTextPreviewShares() {
        // [START android_provide_rich_content_text_previews]
        val share = Intent.createChooser(
            Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, "https://developer.android.com/training/sharing/")

                // (Optional) Here you're setting the title of the content
                putExtra(Intent.EXTRA_TITLE, "Introducing content previews")

                // (Optional) Here you're passing a content URI to an image to be displayed
                data =
                    Uri.parse("content://com.google.android.apps.photos.contentprovider/0/1/mediakey/A123456789")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            },
            null
        )
        startActivity(share)
        // [END android_provide_rich_content_text_previews]
    }

    private fun sharesheetCustomActions(context: Context, preview_text: String) {
        // [START android_provide_custom_actions]
        val sendIntent = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, preview_text)
        val shareIntent = Intent.createChooser(sendIntent, null)
        val customActions = arrayOf(
            ChooserAction.Builder(
                Icon.createWithResource(context, R.drawable.ic_logo),
                "Custom",
                PendingIntent.getBroadcast(
                    context,
                    1,
                    Intent(Intent.ACTION_VIEW),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
                )
            ).build()
        )
        shareIntent.putExtra(Intent.EXTRA_CHOOSER_CUSTOM_ACTIONS, customActions)
        context.startActivity(shareIntent)
        // [END android_provide_custom_actions]
    }

    private fun customTargets(context: Context, preview_text: String) {
        val chooserTargetJessica = ChooserTarget(
            "ChooserTargetJessica",
            Icon.createWithResource(context, R.drawable.ic_logo),
            0f,
            ComponentName(packageName, packageName + ".SharesheetActivity"),
            null
        )
        val chooserTargetSpyros = ChooserTarget(
            "ChooserTargetSpyros",
            Icon.createWithResource(context, R.drawable.ic_logo),
            0f,
            ComponentName(packageName, packageName + ".SharesheetActivity"),
            null
        )
        val intentTargetNearbyShare = Intent().apply {
            component = ComponentName(packageName, "$packageName.AnActivity")
        }
        val intentTargetMaps = Intent(Intent.ACTION_VIEW).apply {
            setPackage("com.google.android.apps.maps")
        }
        val sendIntent = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, preview_text)
        val shareIntent = Intent.createChooser(sendIntent, null)
        // [START android_provide_custom_targets]
        val share = Intent.createChooser(shareIntent, null).apply {
            putExtra(
                Intent.EXTRA_CHOOSER_TARGETS,
                arrayOf(chooserTargetJessica, chooserTargetSpyros)
            )
            putExtra(
                Intent.EXTRA_INITIAL_INTENTS,
                arrayOf(intentTargetNearbyShare, intentTargetMaps)
            )
            // [END android_provide_custom_targets]
        }
        startActivity(share)
    }

    fun excludeSpecificTargets() {
        // [START android_exclude_specific_targets]
        val share = Intent.createChooser(Intent(ACTION_SEND), null).apply {
            // Only use for components you have control over
            val excludedComponentNames =
                arrayOf(ComponentName("com.example.android", "ExampleClass"))
            putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, excludedComponentNames)
        }
        // [END android_exclude_specific_targets]
    }

    fun infoAboutSharing(context: Context, requestCode: Int) {
        // [START android_info_on_sharing]
        var share = Intent(Intent.ACTION_SEND)
        // ...
        val pi = PendingIntent.getBroadcast(
            context, requestCode,
            Intent(context, ShareBroadcastReceiver::class.java),
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        share = Intent.createChooser(share, null, pi.intentSender)
        // [END android_info_on_sharing]
    }

    fun customActions(context: Context, text: String) {
        // [START android_custom_action]
        val sendIntent = Intent(Intent.ACTION_SEND)
            .setType("text/plain")
            .putExtra(Intent.EXTRA_TEXT, text)
        val shareIntent = Intent.createChooser(sendIntent, null)
        val customActions = arrayOf(
            ChooserAction.Builder(
                Icon.createWithResource(context, R.drawable.ic_logo),
                "Custom",
                PendingIntent.getBroadcast(
                    context,
                    1,
                    Intent(Intent.ACTION_VIEW),
                    PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_CANCEL_CURRENT
                )
            ).build()
        )
        shareIntent.putExtra(Intent.EXTRA_CHOOSER_CUSTOM_ACTIONS, customActions)
        context.startActivity(shareIntent)
        // [END android_custom_action]
    }

    fun intentResolver() {
        // [START android_intent_resolver]
        val sendIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
            type = "text/plain"
        }
        startActivity(sendIntent)
        // [END android_intent_resolver]
    }
}
