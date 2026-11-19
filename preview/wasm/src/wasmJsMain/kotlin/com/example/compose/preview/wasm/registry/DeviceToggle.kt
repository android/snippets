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

package com.example.compose.preview.wasm.registry

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.ui.platform.WindowInfo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

/**
 * Window sizes that [DeviceToggle] can show an adaptive snippet in, one per window size class
 * width: compact (Phone), medium (Foldable) and expanded (Tablet).
 */
enum class PreviewDevice(val label: String, val widthDp: Int, val heightDp: Int) {
    Phone("Phone", 360, 640),
    Foldable("Foldable", 700, 640),
    Tablet("Tablet", 1000, 640),
}

/** Frame for snippets wrapped in [DeviceToggle]: the toggle plus a scaled-down device window. */
val DeviceTogglePreviewFrame = PreviewFrame(widthDp = 800, heightDp = 560)

/**
 * Shows a Phone / Foldable / Tablet toggle above [content], and renders [content] in a window of
 * the selected device's size, starting with [initialDevice].
 *
 * The window is scaled down to fit the space below the toggle (never clipped) by lowering the
 * density, so the content still lays out at the device's full size in dp. `LocalWindowInfo` is
 * overridden with the device size, so `currentWindowAdaptiveInfo()` (and window-size-based APIs
 * such as `NavigationSuiteScaffold`) and `BoxWithConstraints` see the selected device, in the
 * live preview and in screenshots alike.
 */
@Composable
fun DeviceToggle(
    initialDevice: PreviewDevice = PreviewDevice.Phone,
    content: @Composable () -> Unit,
) {
    var device by remember { mutableStateOf(initialDevice) }
    Column(
        modifier = Modifier.fillMaxSize().padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        SingleChoiceSegmentedButtonRow {
            PreviewDevice.entries.forEachIndexed { index, option ->
                SegmentedButton(
                    selected = option == device,
                    onClick = { device = option },
                    shape = SegmentedButtonDefaults.itemShape(index, PreviewDevice.entries.size),
                ) {
                    Text(option.label)
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        BoxWithConstraints(
            modifier = Modifier.weight(1f).fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            val scale = minOf(
                maxWidth.value / device.widthDp,
                maxHeight.value / device.heightDp,
                1f,
            )
            DeviceWindow(device, scale, content)
        }
    }
}

@Composable
private fun DeviceWindow(device: PreviewDevice, scale: Float, content: @Composable () -> Unit) {
    val density = LocalDensity.current
    val windowInfo = LocalWindowInfo.current
    val deviceDensity = Density(density.density * scale, density.fontScale)
    val deviceWindowInfo = remember(windowInfo, device, deviceDensity) {
        val size = IntSize(
            (device.widthDp * deviceDensity.density).roundToInt(),
            (device.heightDp * deviceDensity.density).roundToInt(),
        )
        object : WindowInfo by windowInfo {
            override val containerSize: IntSize = size
            override val containerDpSize: DpSize = DpSize(device.widthDp.dp, device.heightDp.dp)
        }
    }
    Box(
        modifier = Modifier
            .requiredSize(device.widthDp.dp * scale, device.heightDp.dp * scale)
            .border(1.dp, MaterialTheme.colorScheme.outlineVariant),
    ) {
        CompositionLocalProvider(
            LocalDensity provides deviceDensity,
            LocalWindowInfo provides deviceWindowInfo,
        ) {
            content()
        }
    }
}
