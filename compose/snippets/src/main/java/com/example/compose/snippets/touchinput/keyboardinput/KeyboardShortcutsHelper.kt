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
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.MutableStateFlow

// [START android_compose_keyboard_shortcuts_helper]
class KeyboardShortcutsHelperActivity : ComponentActivity() {

    @RequiresApi(Build.VERSION_CODES.N)
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
}
// [END android_compose_keyboard_shortcuts_helper]


class KeyboardShortcutsHelperRequestActivity : ComponentActivity() {

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

private sealed class CursorMovement(val label: String) {

    data object Up : CursorMovement("Up")
    data object Down : CursorMovement("Down")
    data object Forward : CursorMovement("Forward")
    data object Backward : CursorMovement("Backward")
}

private class CursorMovementKeyboardShortcut(
    val cursorMovement: CursorMovement,
    val key: Int,
    val modifiers: Int = 0,
) {
    @RequiresApi(Build.VERSION_CODES.N)
    fun intoKeyboardShortcutInfo(): KeyboardShortcutInfo {
        return KeyboardShortcutInfo(
            cursorMovement.label,
            key,
            modifiers
        )
    }
}

private enum class CursorMovementStyle(
    val label: String,
    val shortcuts: List<CursorMovementKeyboardShortcut>
) {

    Emacs(
        "Emacs", listOf(
            CursorMovementKeyboardShortcut(
                CursorMovement.Up,
                KeyEvent.KEYCODE_P,
                KeyEvent.META_CTRL_ON
            ),
            CursorMovementKeyboardShortcut(
                CursorMovement.Down,
                KeyEvent.KEYCODE_N,
                KeyEvent.META_CTRL_ON
            ),
            CursorMovementKeyboardShortcut(
                CursorMovement.Forward,
                KeyEvent.KEYCODE_F,
                KeyEvent.META_CTRL_ON
            ),
            CursorMovementKeyboardShortcut(
                CursorMovement.Backward,
                KeyEvent.KEYCODE_B,
                KeyEvent.META_CTRL_ON
            ),

            CursorMovementKeyboardShortcut(CursorMovement.Up, KeyEvent.KEYCODE_DPAD_UP),
            CursorMovementKeyboardShortcut(CursorMovement.Down, KeyEvent.KEYCODE_DPAD_DOWN),
            CursorMovementKeyboardShortcut(CursorMovement.Forward, KeyEvent.KEYCODE_DPAD_RIGHT),
            CursorMovementKeyboardShortcut(CursorMovement.Backward, KeyEvent.KEYCODE_DPAD_LEFT),
        )
    ),
    Vim(
        "Vim", listOf(
            CursorMovementKeyboardShortcut(CursorMovement.Up, KeyEvent.KEYCODE_K),
            CursorMovementKeyboardShortcut(CursorMovement.Down, KeyEvent.KEYCODE_J),
            CursorMovementKeyboardShortcut(CursorMovement.Forward, KeyEvent.KEYCODE_L),
            CursorMovementKeyboardShortcut(CursorMovement.Backward, KeyEvent.KEYCODE_H),

            CursorMovementKeyboardShortcut(CursorMovement.Up, KeyEvent.KEYCODE_DPAD_UP),
            CursorMovementKeyboardShortcut(CursorMovement.Down, KeyEvent.KEYCODE_DPAD_DOWN),
            CursorMovementKeyboardShortcut(CursorMovement.Forward, KeyEvent.KEYCODE_DPAD_RIGHT),
            CursorMovementKeyboardShortcut(CursorMovement.Backward, KeyEvent.KEYCODE_DPAD_LEFT),
        )
    );

    @RequiresApi(Build.VERSION_CODES.N)
    fun intoKeyboardShortcutGroup(): KeyboardShortcutGroup {
        return KeyboardShortcutGroup(
            "Cursor movement($label)",
            shortcuts.map { it.intoKeyboardShortcutInfo() }
        )
    }
}

class ShortcutCustomizableKeyboardShortcutsHelperActivity : ComponentActivity() {
    private var cursorMovement = MutableStateFlow<CursorMovementStyle>(CursorMovementStyle.Emacs)

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onProvideKeyboardShortcuts(
        data: MutableList<KeyboardShortcutGroup>?,
        menu: Menu?,
        deviceId: Int
    ) {
        val cursorMovementSection = cursorMovement.value.intoKeyboardShortcutGroup()
        data?.add(cursorMovementSection)
    }

    private fun updateCursorMovementStyle(style: CursorMovementStyle) {
        cursorMovement.value = style
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                val cursorMovementKeyboardShortcuts by cursorMovement.collectAsStateWithLifecycle()
                val activity = LocalContext.current as? Activity

                Box(
                    modifier = Modifier
                        .safeDrawingPadding()
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Column {
                        SingleChoiceSegmentedButtonRow {
                            CursorMovementStyle.entries.forEachIndexed { index, cursorMovementStyle ->
                                SegmentedButton(
                                    selected = cursorMovementStyle == cursorMovementKeyboardShortcuts,
                                    onClick = { updateCursorMovementStyle(cursorMovementStyle) },
                                    label = { Text(cursorMovementStyle.label) },
                                    shape = SegmentedButtonDefaults.itemShape(
                                        index = index,
                                        count = CursorMovementStyle.entries.size
                                    )
                                )
                            }
                        }
                        Button(
                            onClick = {
                                activity?.requestShowKeyboardShortcuts()
                            }
                        ) {
                            Text(text = "Show keyboard shortcuts")
                        }
                    }
                }
            }
        }
    }

}