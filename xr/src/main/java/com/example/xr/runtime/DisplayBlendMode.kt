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

package com.example.xr.runtime

import androidx.xr.runtime.DisplayBlendMode.Companion.ADDITIVE
import androidx.xr.runtime.DisplayBlendMode.Companion.ALPHA_BLEND
import androidx.xr.runtime.DisplayBlendMode.Companion.NO_DISPLAY
import androidx.xr.runtime.Session
import androidx.xr.runtime.XrDevice

fun getPreferredBlendMode(session: Session) {
    // [START androidxr_runtime_getPreferredBlendMode]
    val preferredDisplayBlendMode =
        XrDevice.getCurrentDevice(session).getPreferredDisplayBlendMode()
    when (preferredDisplayBlendMode) {
        ADDITIVE -> {
            // Virtual content is added to the real world by adding the pixel values for each of
            // Red, Green, and Blue components. Alpha is ignored. Black pixels will appear transparent.
        }

        ALPHA_BLEND -> {
            // Virtual content is added to the real world by alpha blending the pixel values based
            // on the Alpha component.
        }

        NO_DISPLAY -> {
            // Device doesn't have a display.
        }
    }
    // [END androidxr_runtime_getPreferredBlendMode]
}
