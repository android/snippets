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

package com.example.tv.snippets

import android.annotation.SuppressLint
import android.hardware.input.InputManager
import android.os.Bundle
import android.view.InputDevice
import android.view.KeyEvent
import android.view.View
import androidx.core.content.getSystemService
import androidx.fragment.app.Fragment

class GamesFragment : Fragment() {

    private var keyUp: Int = 0
    private var keyLeft: Int = 0
    private var keyDown: Int = 0
    private var keyRight: Int = 0

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        determineKeyboardLayout()
    }

    @SuppressLint("NewApi")
    private fun determineKeyboardLayout() {
        // [START android_tv_games_keyboard_layout]
        val inputManager: InputManager? = requireActivity().getSystemService()

        inputManager?.inputDeviceIds?.map { inputManager.getInputDevice(it) }
            ?.firstOrNull { it?.keyboardType == InputDevice.KEYBOARD_TYPE_ALPHABETIC }
            ?.let { inputDevice ->
                keyUp = inputDevice.getKeyCodeForKeyLocation(KeyEvent.KEYCODE_W)
                keyLeft = inputDevice.getKeyCodeForKeyLocation(KeyEvent.KEYCODE_A)
                keyDown = inputDevice.getKeyCodeForKeyLocation(KeyEvent.KEYCODE_S)
                keyRight = inputDevice.getKeyCodeForKeyLocation(KeyEvent.KEYCODE_D)
            }
        // [END android_tv_games_keyboard_layout]
    }
}
