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

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusEvent
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.isAltPressed
import androidx.compose.ui.input.key.isCtrlPressed
import androidx.compose.ui.input.key.isMetaPressed
import androidx.compose.ui.input.key.isShiftPressed
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Suppress("unused")
@Composable
fun CommandsScreen() {
    val context = LocalContext.current
    var playerState by rememberSaveable { mutableStateOf(false) }

    val doSomething = {
        showToast(context, "Doing something")
    }

    val doAnotherThing = {
        showToast(context, "Doing another thing")
    }

    val togglePlayPause = {
        playerState = !playerState
        val message = if (playerState) {
            "Playing"
        } else {
            "Paused"
        }
        showToast(context, message)
    }

    val actionC = {
        showToast(context, "Action C")
    }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier
            .verticalScroll(rememberScrollState())
            .padding(8.dp)
    ) {
        KeyEvents(doSomething)
        ModifierKeys(doSomething)
        SpacebarAndEnterKeyTriggersClickEvents(togglePlayPause)
        UnconsumedKeyEvents(doSomething, doAnotherThing, actionC)
        PreviewKeyEvents()
        InterceptKeyEvents(
            doSomething,
            { keyEvent ->
                showToast(context, "onPreviewKeyEvent: ${keyEvent.key.keyCode}")
            },
            { keyEvent ->
                showToast(context, "onKeyEvent: ${keyEvent.key.keyCode}")
            }
        )
    }
}

fun showToast(context: Context, message: String) {
    val toast = Toast.makeText(context, message, Toast.LENGTH_SHORT)
    toast.show()
}

@Composable
private fun BoxWithFocusIndication(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    var isFocused by remember { mutableStateOf(false) }
    val backgroundColor = if (isFocused) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        MaterialTheme.colorScheme.surface
    }
    Box(
        modifier = modifier
            .onFocusEvent {
                isFocused = it.isFocused
            }
            .background(backgroundColor),
        content = content
    )
}

@Composable
private fun KeyEvents(
    doSomething: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithFocusIndication(modifier) {
        // [START android_compose_touchinput_keyboardinput_keyevents]
        Box(
            modifier = Modifier
                .onKeyEvent {
                    if (
                        it.type == KeyEventType.KeyUp &&
                        it.key == Key.S
                    ) {
                        doSomething()
                        true
                    } else {
                        false
                    }
                }
                .focusable()
        ) {
            Text("Press S key")
        }
        // [END android_compose_touchinput_keyboardinput_keyevents]
    }
}

@Composable
private fun ModifierKeys(
    doSomething: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithFocusIndication(modifier = modifier) {
        // [START android_compose_touchinput_keyboardinput_modifierkeys]
        Box(
            modifier = Modifier
                .focusable()
                .onKeyEvent {
                    if (
                        it.type == KeyEventType.KeyUp &&
                        it.key == Key.S &&
                        !it.isAltPressed &&
                        !it.isCtrlPressed &&
                        !it.isMetaPressed &&
                        !it.isShiftPressed
                    ) {
                        doSomething()
                        true
                    } else {
                        false
                    }
                }
        ) {
            Text("Press S key with a modifier key")
        }
        // [END android_compose_touchinput_keyboardinput_modifierkeys]
    }
}

@Composable
private fun SpacebarAndEnterKeyTriggersClickEvents(
    togglePausePlay: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithFocusIndication(modifier = modifier) {
        // [START android_compose_touchinput_keyboardinput_spacebar]
        MediaPlayer(modifier = Modifier.clickable { togglePausePlay() })
        // [END android_compose_touchinput_keyboardinput_spacebar]
    }
}

@Composable
private fun MediaPlayer(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(200.dp)
            .background(MaterialTheme.colorScheme.primaryContainer)
    )
}

@Composable
private fun UnconsumedKeyEvents(
    actionA: () -> Unit,
    actionB: () -> Unit,
    actionC: () -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithFocusIndication(modifier = modifier) {
        // [START android_compose_touchinput_keyboardinput_unconsumedkeyevents]
        OuterComponent(
            modifier = Modifier.onKeyEvent {
                when {
                    it.type == KeyEventType.KeyUp && it.key == Key.S -> {
                        actionB() // This function is never called.
                        true
                    }

                    it.type == KeyEventType.KeyUp && it.key == Key.D -> {
                        actionC()
                        true
                    }

                    else -> false
                }
            }
        ) {
            InnerComponent(
                modifier = Modifier.onKeyEvent {
                    if (it.type == KeyEventType.KeyUp && it.key == Key.S) {
                        actionA()
                        true
                    } else {
                        false
                    }
                }
            )
        }
        // [END android_compose_touchinput_keyboardinput_unconsumedkeyevents]
    }
}

@Composable
private fun OuterComponent(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) =
    Box(content = content, modifier = modifier.focusable())

@Composable
private fun InnerComponent(
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.focusable()) {
        Text("Press S key or D key", modifier = Modifier.padding(16.dp))
    }
}

@Composable
private fun PreviewKeyEvents() {
    // [START android_compose_touchinput_keyboardinput_previewkeyevents]
    val focusManager = LocalFocusManager.current
    var textFieldValue by remember { mutableStateOf(TextFieldValue()) }

    TextField(
        textFieldValue,
        onValueChange = {
            textFieldValue = it
        },
        modifier = Modifier.onPreviewKeyEvent {
            if (it.type == KeyEventType.KeyUp && it.key == Key.Tab) {
                focusManager.moveFocus(FocusDirection.Next)
                true
            } else {
                false
            }
        }
    )
    // [END android_compose_touchinput_keyboardinput_previewkeyevents]
}

@Composable
private fun InterceptKeyEvents(
    previewSKey: () -> Unit,
    actionForPreview: (KeyEvent) -> Unit,
    actionForKeyEvent: (KeyEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    BoxWithFocusIndication(modifier = modifier) {
        // [START android_compose_touchinput_keyboardinput_interceptevents]
        Column(
            modifier = Modifier.onPreviewKeyEvent {
                if (it.key == Key.S) {
                    previewSKey()
                    true
                } else {
                    false
                }
            }
        ) {
            Box(
                modifier = Modifier
                    .focusable()
                    .onPreviewKeyEvent {
                        actionForPreview(it)
                        false
                    }
                    .onKeyEvent {
                        actionForKeyEvent(it)
                        true
                    }
            ) {
                Text("Press any key")
            }
        }
        // [END android_compose_touchinput_keyboardinput_interceptevents]
    }
}
