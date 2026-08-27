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

package com.example.media

import android.view.SurfaceControl

private fun applyLutExample() {
    // [START android_media_hdr_lut_apply]
    val sc = SurfaceControl.Builder().build()
    val luts = DisplayLuts()
    val entry = DisplayLuts.Entry(
        floatArrayOf(0.5f, 0.5f, 0.5f, 0.5f),
        LutProperties.ONE_DIMENSION,
        LutProperties.SAMPLING_KEY_MAX_RGB
    )
    luts.set(entry)
    // [START_EXCLUDE silent]
    /*
    // [END_EXCLUDE]
    SurfaceControl.Transaction().setLuts(sc, luts).apply()
    // [START_EXCLUDE silent]
    */
    // [END_EXCLUDE]
    // [END android_media_hdr_lut_apply]
}

// Platform API shims for API 36 DisplayLuts
class DisplayLuts {
    class Entry(
        val data: FloatArray,
        val dimension: Int,
        val samplingKey: Int
    )

    fun set(entry: Entry) {}
    fun set(first: Entry, second: Entry) {}
}

object LutProperties {
    const val ONE_DIMENSION = 1
    const val THREE_DIMENSIONS = 3
    const val SAMPLING_KEY_MAX_RGB = 1
}
