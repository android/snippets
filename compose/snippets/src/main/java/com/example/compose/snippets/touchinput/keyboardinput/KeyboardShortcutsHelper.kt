/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.compose.snippets.touchinput.keyboardinput

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.KeyEvent
import android.view.KeyboardShortcutGroup
import android.view.KeyboardShortcutInfo
import android.view.Menu
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext

class MainActivity : ComponentActivity() {
    // Activity codes such as overridden onStart method.

    @RequiresApi(Build.VERSION_CODES.N)
    // [START android_compose_keyboard_shortcuts_helper]
    override fun onProvideKeyboardShortcuts(
        data: MutableList<KeyboardShortcutGroup>?,
        menu: Menu?,
        deviceId: Int
    ) {
        val shortcutGroup =
            KeyboardShortcutGroup(
                "Cursor movement",
                listOf(
                    KeyboardShortcutInfo("Up", KeyEvent.KEYCODE_P, KeyEvent.META_CTRL_ON),
                    KeyboardShortcutInfo("Down", KeyEvent.KEYCODE_N, KeyEvent.META_CTRL_ON),
                    KeyboardShortcutInfo("Forward", KeyEvent.KEYCODE_F, KeyEvent.META_CTRL_ON),
                    KeyboardShortcutInfo("Backward", KeyEvent.KEYCODE_B, KeyEvent.META_CTRL_ON),
                )
            )
        data?.add(shortcutGroup)
    }
    // [END android_compose_keyboard_shortcuts_helper]
}

class AnotherActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                // [START android_compose_keyboard_shortcuts_helper_request]
                val activity = LocalContext.current as? Activity

                Button(
                    onClick = {
                        activity?.requestShowKeyboardShortcuts()
                    }
                ) {
                    Text(text = "Show keyboard shortcuts")
                }
                // [END android_compose_keyboard_shortcuts_helper_request]
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    // [START android_compose_keyboard_shortcuts_helper_with_groups]
    override fun onProvideKeyboardShortcuts(
        data: MutableList<KeyboardShortcutGroup>?,
        menu: Menu?,
        deviceId: Int
    ) {
        val cursorMovement = KeyboardShortcutGroup(
            "Cursor movement",
            listOf(
                KeyboardShortcutInfo("Up", KeyEvent.KEYCODE_P, KeyEvent.META_CTRL_ON),
                KeyboardShortcutInfo("Down", KeyEvent.KEYCODE_N, KeyEvent.META_CTRL_ON),
                KeyboardShortcutInfo("Forward", KeyEvent.KEYCODE_F, KeyEvent.META_CTRL_ON),
                KeyboardShortcutInfo("Backward", KeyEvent.KEYCODE_B, KeyEvent.META_CTRL_ON),
            )
        )

        val messageEdit = KeyboardShortcutGroup(
            "Message editing",
            listOf(
                KeyboardShortcutInfo("Select All", KeyEvent.KEYCODE_A, KeyEvent.META_CTRL_ON),
                KeyboardShortcutInfo(
                    "Send a message",
                    KeyEvent.KEYCODE_ENTER,
                    KeyEvent.META_SHIFT_ON
                )
            )
        )

        data?.add(cursorMovement)
        data?.add(messageEdit)
    }
    // [END android_compose_keyboard_shortcuts_helper_with_groups]
}
