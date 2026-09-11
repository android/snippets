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

import android.app.Activity
import android.app.PendingIntent
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_SEND
import android.graphics.drawable.Icon
import android.net.Uri
import android.service.chooser.ChooserAction
import android.service.chooser.ChooserTarget
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.IntentCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.example.compose.snippets.R

@Composable
fun SharesheetNavHost() {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "home") {
        composable(route = "home") {
            // Home screen
        }
        // [START android_receive_data_nav_deeplink]
        composable(
            route = "incoming_share",
            deepLinks = listOf(
                navDeepLink {
                    action = Intent.ACTION_SEND
                    mimeType = "text/plain"
                },
                navDeepLink {
                    action = Intent.ACTION_SEND
                    mimeType = "image/*"
                },
                navDeepLink {
                    action = Intent.ACTION_SEND_MULTIPLE
                    mimeType = "image/*"
                }
            )
        ) {
            SharesheetHandler()
        }
        // [END android_receive_data_nav_deeplink]
    }
}

// [START android_handle_intent_action_data_sent]
@Composable
fun SharesheetHandler() {
    val context = LocalContext.current
    val intent = (context as? Activity)?.intent

    when (intent?.action) {
        Intent.ACTION_SEND -> {
            if (intent.type == "text/plain") {
                handleSendText(intent) // Handle text being sent
            } else if (intent.type?.startsWith("image/") == true) {
                handleSendImage(intent) // Handle single image being sent
            }
        }

        Intent.ACTION_SEND_MULTIPLE -> {
            if (intent.type?.startsWith("image/") == true) {
                handleSendMultipleImages(intent) // Handle multiple images being sent
            }
        }

        else -> {
            // Handle other intents, such as being started from the home screen
        }
    }
}

private fun handleSendText(intent: Intent) {
    intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
        // Update UI state with the shared text
    }
}

private fun handleSendImage(intent: Intent) {
    val imageUri = IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri::class.java)
    imageUri?.let {
        // Update UI state with the shared image
    }
}

private fun handleSendMultipleImages(intent: Intent) {
    val imageUris =
        IntentCompat.getParcelableArrayListExtra(intent, Intent.EXTRA_STREAM, Uri::class.java)
    imageUris?.let {
        // Update UI state with the shared images
    }
}
// [END android_handle_intent_action_data_sent]

fun handleSendAndExtraText(intent: Intent) {
    // [START android_handle_intent_handle_extra_text]
    IntentCompat.getParcelableExtra(intent, Intent.EXTRA_STREAM, Uri::class.java).let {
        // Handle the EXTRA_TEXT as well
        val extraText = intent.getCharSequenceExtra(Intent.EXTRA_TEXT)
        // Update UI state with the shared image and the EXTRA_TEXT if available
    }
    // [END android_handle_intent_handle_extra_text]
}

// [START android_handle_share_text]
fun shareText(context: Context) {
    val sendIntent: Intent = Intent().apply {
        action = ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}
// [END android_handle_share_text]

// [START android_handle_share_image]
fun shareBinaryContent(context: Context) {
    val shareIntent: Intent = Intent().apply {
        action = ACTION_SEND
        // Example: content://com.google.android.apps.photos.contentprovider/...
        val imageUri: Uri =
            Uri.parse("content://com.google.android.apps.photos.contentprovider/0/1/mediakey/1")
        putExtra(Intent.EXTRA_STREAM, imageUri)
        type = "image/jpeg"
    }
    context.startActivity(Intent.createChooser(shareIntent, null))
}
// [END android_handle_share_image]

// [START android_handle_share_multiple]
fun shareMultiple(context: Context) {
    val imageUris: ArrayList<Uri> = arrayListOf(
        Uri.parse("content://com.google.android.apps.photos.contentprovider/0/1/mediakey/1"),
        Uri.parse("content://com.google.android.apps.photos.contentprovider/0/1/mediakey/2")
    )

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND_MULTIPLE
        putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
        type = "image/*"
    }
    context.startActivity(Intent.createChooser(shareIntent, null))
}
// [END android_handle_share_multiple]

// [START android_provide_rich_content_text_previews]
fun richContentToTextPreviewShares(context: Context) {
    val share = Intent.createChooser(
        Intent().apply {
            action = ACTION_SEND
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
    context.startActivity(share)
}
// [END android_provide_rich_content_text_previews]

// [START android_provide_custom_actions]
fun sharesheetCustomActions(context: Context, previewText: String) {
    val sendIntent = Intent(ACTION_SEND)
        .setType("text/plain")
        .putExtra(Intent.EXTRA_TEXT, previewText)
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
}
// [END android_provide_custom_actions]

fun customTargets(context: Context, previewText: String) {
    val chooserTargetJessica = ChooserTarget(
        "ChooserTargetJessica",
        Icon.createWithResource(context, R.drawable.ic_logo),
        0f,
        ComponentName(context.packageName, context.packageName + ".SharesheetActivity"),
        null
    )
    val chooserTargetSpyros = ChooserTarget(
        "ChooserTargetSpyros",
        Icon.createWithResource(context, R.drawable.ic_logo),
        0f,
        ComponentName(context.packageName, context.packageName + ".SharesheetActivity"),
        null
    )
    val intentTargetNearbyShare = Intent().apply {
        component = ComponentName(context.packageName, "${context.packageName}.AnActivity")
    }
    val intentTargetMaps = Intent(Intent.ACTION_VIEW).apply {
        setPackage("com.google.android.apps.maps")
    }
    val sendIntent = Intent(ACTION_SEND)
        .setType("text/plain")
        .putExtra(Intent.EXTRA_TEXT, previewText)
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
    }
    // [END android_provide_custom_targets]
    context.startActivity(share)
}

// [START android_exclude_specific_targets]
fun excludeSpecificTargets(context: Context) {
    val share = Intent.createChooser(Intent(ACTION_SEND), null).apply {
        // Only use for components you have control over
        val excludedComponentNames =
            arrayOf(ComponentName("com.example.android", "ExampleClass"))
        putExtra(Intent.EXTRA_EXCLUDE_COMPONENTS, excludedComponentNames)
    }
    context.startActivity(share)
}
// [END android_exclude_specific_targets]

// [START android_info_on_sharing]
fun infoAboutSharing(context: Context, requestCode: Int) {
    var share = Intent(ACTION_SEND)
    // ...
    val pi = PendingIntent.getBroadcast(
        context, requestCode,
        Intent(context, ShareBroadcastReceiver::class.java),
        PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )
    share = Intent.createChooser(share, null, pi.intentSender)
    context.startActivity(share)
}
// [END android_info_on_sharing]

// [START android_custom_action]
fun customActions(context: Context, text: String) {
    val sendIntent = Intent(ACTION_SEND)
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
}
// [END android_custom_action]

// [START android_intent_resolver]
fun intentResolver(context: Context) {
    val sendIntent: Intent = Intent().apply {
        action = ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "This is my text to send.")
        type = "text/plain"
    }
    context.startActivity(sendIntent)
}
// [END android_intent_resolver]
