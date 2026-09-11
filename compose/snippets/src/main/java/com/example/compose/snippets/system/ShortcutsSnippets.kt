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

package com.example.compose.snippets.system

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.ShortcutInfo
import android.content.pm.ShortcutManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.core.content.getSystemService
import androidx.core.content.pm.ShortcutInfoCompat
import androidx.core.content.pm.ShortcutManagerCompat
import androidx.core.graphics.drawable.IconCompat
import com.example.compose.snippets.R

fun createDynamicShortcut(context: Context) {
    // [START android_shortcuts_create_dynamic]
    val shortcut = ShortcutInfoCompat.Builder(context, "id1")
        .setShortLabel("Website")
        .setLongLabel("Open the website")
        .setIcon(IconCompat.createWithResource(context, R.drawable.icon_website))
        .setIntent(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://www.mysite.example.com/")
            )
        )
        .build()

    ShortcutManagerCompat.pushDynamicShortcut(context, shortcut)
    // [END android_shortcuts_create_dynamic]
}

fun pinShortcut(context: Context) {
    // [START android_shortcuts_pin_shortcut]
    val shortcutManager = context.getSystemService<ShortcutManager>()

    if (shortcutManager!!.isRequestPinShortcutSupported) {
        // Enable the existing shortcut with the ID "my-shortcut".
        val pinShortcutInfo = ShortcutInfo.Builder(context, "my-shortcut").build()

        // Create the PendingIntent object only if your app needs to be notified
        // that the user let the shortcut be pinned. If the pinning operation fails,
        // your app isn't notified. Assume here that the app implements a method
        // called createShortcutResultIntent() that returns a broadcast intent.
        val pinnedShortcutCallbackIntent = shortcutManager.createShortcutResultIntent(pinShortcutInfo)

        // Configure the intent so that your app's broadcast receiver gets the
        // callback successfully. For details, see PendingIntent.getBroadcast().
        val successCallback = PendingIntent.getBroadcast(
            context, /* request code */ 0,
            pinnedShortcutCallbackIntent, /* flags */ PendingIntent.FLAG_IMMUTABLE
        )

        shortcutManager.requestPinShortcut(
            pinShortcutInfo,
            successCallback.intentSender
        )
    }
    // [END android_shortcuts_pin_shortcut]
}

// [START android_shortcuts_restore_dynamic]
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (ShortcutManagerCompat.getDynamicShortcuts(this).isEmpty()) {
            // Application restored. Re-publish dynamic shortcuts.
            if (ShortcutManagerCompat.getShortcuts(this, ShortcutManagerCompat.FLAG_MATCH_PINNED).isNotEmpty()) {
                // Pinned shortcuts are restored. Use updateShortcuts() to make
                // sure they contain up-to-date information.
            }
        }
    }
    // ...
}
// [END android_shortcuts_restore_dynamic]
