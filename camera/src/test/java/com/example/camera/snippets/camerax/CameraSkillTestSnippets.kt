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

package com.example.camera.snippets.camerax

import androidx.camera.core.CameraInfo
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageProxy
import com.google.common.truth.Truth.assertThat
import java.nio.ByteBuffer
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

// Replaces android_camerax_skill_testing_2 (FakeImageProxy is not published).
// [START android_camerax_skill_testing_2]
// Production code: keep frame math independent of ImageProxy.
class LumaAnalyzer(private val onLuma: (Double) -> Unit) : ImageAnalysis.Analyzer {
    override fun analyze(image: ImageProxy) {
        image.use { onLuma(averageLuma(it.planes[0].buffer)) }
    }
}

fun averageLuma(yPlane: ByteBuffer): Double {
    yPlane.rewind()
    val count = yPlane.remaining()
    if (count == 0) return 0.0
    var sum = 0L
    repeat(count) { sum += yPlane.get().toInt() and 0xFF }
    return sum.toDouble() / count
}

// JVM test: a hand-written fake frame. No camera, emulator, or Mockito.
@RunWith(JUnit4::class)
class LumaAnalyzerTest {
    @Test
    fun averageLuma_uniformFrame_returnsPixelValue() {
        val fakeFrame = ByteBuffer.wrap(ByteArray(640 * 480) { 100 })
        assertThat(averageLuma(fakeFrame)).isWithin(0.001).of(100.0)
    }
}
// [END android_camerax_skill_testing_2]

// [START android_camerax_skill_testing_3]
// Wrap the capability query in an app interface so tests can fake it.
interface FlashCapability {
    fun hasFlashUnit(): Boolean
}

class CameraFlashCapability(private val cameraInfo: CameraInfo) : FlashCapability {
    override fun hasFlashUnit(): Boolean = cameraInfo.hasFlashUnit()
}

fun chooseFlashMode(capability: FlashCapability): Int =
    if (capability.hasFlashUnit()) ImageCapture.FLASH_MODE_AUTO else ImageCapture.FLASH_MODE_OFF

// Hand-written fake for a device without a flash unit.
class FakeFlashCapability(private val hasFlash: Boolean) : FlashCapability {
    override fun hasFlashUnit(): Boolean = hasFlash
}

@RunWith(JUnit4::class)
class FlashModeTest {
    @Test
    fun deviceWithoutFlash_usesFlashModeOff() {
        assertThat(chooseFlashMode(FakeFlashCapability(hasFlash = false)))
            .isEqualTo(ImageCapture.FLASH_MODE_OFF)
    }
}
// [END android_camerax_skill_testing_3]
