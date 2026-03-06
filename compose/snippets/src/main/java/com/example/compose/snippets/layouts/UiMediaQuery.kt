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

@file:OptIn(ExperimentalComposeUiApi::class, ExperimentalMediaQueryApi::class)

package com.example.compose.snippets.layouts

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ComposeUiFlags
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.ExperimentalMediaQueryApi
import androidx.compose.ui.LocalUiMediaScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.UiMediaScope
import androidx.compose.ui.derivedMediaQuery
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.mediaQuery
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.window.core.layout.WindowSizeClass

// [START ENABLE_MEDIA_QUERY_INTEGRATION]
class MyApplication : Application() {
    override fun onCreate() {
        ComposeUiFlags.isMediaQueryIntegrationEnabled = true
        super.onCreate()
    }
}
// [END ENABLE_MEDIA_QUERY_INTEGRATION]

@Preview(showBackground = true)
@Composable
fun MediaQueryUsage(
    @PreviewParameter(PostureProvider::class) posture: Posture
) {
    EnableMediaQueryIntegration {
        OverrideUiMediaScope(windowPosture = posture.value) {
            // [START USE_MEDIA_QUERY]
            if (mediaQuery { windowPosture == UiMediaScope.Posture.Tabletop }) {
                TabletopLayout()
            } else {
                FlatLayout()
            }
            // [END USE_MEDIA_QUERY]
        }
    }
}

@Preview(showBackground = true, widthDp = 480, heightDp = 320, name = "Compact")
@Preview(showBackground = true, widthDp = 600, heightDp = 320, name = "Medium")
@Preview(showBackground = true, widthDp = 840, heightDp = 320, name = "Expanded")
@Composable
fun WindowSize() {
    EnableMediaQueryIntegration {
        // [START WINDOW_SIZE]
        val narrowerThanMedium by derivedMediaQuery {
            windowWidth < WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND.dp
        }
        val narrowerThanExpanded by derivedMediaQuery {
            windowWidth < WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND.dp
        }
        when {
            narrowerThanMedium -> SinglePaneLayout()
            narrowerThanExpanded -> TwoPaneLayout()
            else -> ThreePaneLayout()
        }
        // [END WINDOW_SIZE]
    }
}

@Preview(showBackground = true)
@Composable
fun WindowPosture(
    @PreviewParameter(PostureProvider::class) posture: Posture
) {
    EnableMediaQueryIntegration {
        OverrideUiMediaScope(windowPosture = posture.value) {
            // [START WINDOW_POSTURE]
            when {
                mediaQuery { windowPosture == UiMediaScope.Posture.Tabletop } -> TabletopLayout()
                mediaQuery { windowPosture == UiMediaScope.Posture.Book } -> BookLayout()
                mediaQuery { windowPosture == UiMediaScope.Posture.Flat } -> FlatLayout()
            }
            // [END WINDOW_POSTURE]
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PointerPrecision(
    @PreviewParameter(PointerPrecisionProvider::class) precision: Precision
) {
    OverrideUiMediaScope(pointerPrecision = precision.value) {
        // [START POINTER_PRECISION]
        if (mediaQuery { pointerPrecision == UiMediaScope.PointerPrecision.Blunt }) {
            LargeSizeButton()
        } else {
            NormalSizeButton()
        }
        // [END POINTER_PRECISION]
    }
}

@Preview(showBackground = true)
@Composable
fun KeyboardKind(
    @PreviewParameter(KeyboardKindProvider::class) keyboardKindHolder: KeyboardKindHolder
) {
    OverrideUiMediaScope(keyboardKind = keyboardKindHolder.value) {
        // [START KEYBOARD_KIND]
        if (mediaQuery { keyboardKind == UiMediaScope.KeyboardKind.None }) {
            SuggestKeyboardConnect()
        }
        // [END KEYBOARD_KIND]
    }
}

@Preview(showBackground = true)
@Composable
fun CameraAndMicrophoneSupport(
    @PreviewParameter(MediaDeviceSupportProvider::class) support: MediaDeviceSupport
) {
    EnableMediaQueryIntegration {
        OverrideUiMediaScope(hasCamera = support.camera, hasMicrophone = support.microphone) {
            // [START CAMERA_AND_MICROPHONE_SUPPORT]
            Row {
                OutlinedTextField(state = rememberTextFieldState())
                // Show the MicButton when the device supports a microphone.
                if (mediaQuery { hasMicrophone }) {
                    MicButton()
                }
                // Show the CameraButton when the device supports a camera.
                if (mediaQuery { hasCamera }) {
                    CameraButton()
                }
            }
            // [END CAMERA_AND_MICROPHONE_SUPPORT]
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ViewingDistance(
    @PreviewParameter(ViewingDistanceProvider::class) viewingDistanceHolder: ViewingDistanceHolder
) {
    EnableMediaQueryIntegration {
        OverrideUiMediaScope(viewingDistance = viewingDistanceHolder.value) {
            // [START VIEWING_DISTANCE]
            val fontSize = when {
                mediaQuery { viewingDistance == UiMediaScope.ViewingDistance.Far } -> 20.sp
                mediaQuery { viewingDistance == UiMediaScope.ViewingDistance.Medium } -> 18.sp
                else -> 16.sp
            }
            // [END VIEWING_DISTANCE]
            Text("A text with a font size of $fontSize", style = TextStyle(fontSize = fontSize))
        }
    }
}

// [START LAYOUT_PREVIEW]
@Preview
@Composable
fun PreviewLayout() {
    // [START EXCLUDE]
    ComposeUiFlags.isMediaQueryIntegrationEnabled = true
    // [END EXCLUDE]
    when {
        mediaQuery { windowPosture == UiMediaScope.Posture.Tabletop } -> TabletopLayout()
        else -> FlatLayout()
    }
}
// [END LAYOUT_PREVIEW]

// [START LAYOUT_FOR_TABLETOP_PREVIEW]
@Preview
@Composable
fun PreviewLayoutForTabletop() {
    // Step 1: Enable the mediaQuery function
    ComposeUiFlags.isMediaQueryIntegrationEnabled = true

    val currentUiMediaScope = LocalUiMediaScope.current
    // Step 2: Define a custom object implementing the UiMediaScope interface.
    // The object overrides the windowPosture parameter.
    // The resolution of the remaining parameters is deferred to the currentUiMediaScope object.
    val uiMediaScope = object : UiMediaScope by currentUiMediaScope {
        override val windowPosture: UiMediaScope.Posture
            get() = UiMediaScope.Posture.Tabletop
    }

    // Step 3: Set the object to the LocalUiMediaScope.
    CompositionLocalProvider(LocalUiMediaScope provides uiMediaScope) {
        // Step 4: Call the composable to preview.
        when {
            mediaQuery { windowPosture == UiMediaScope.Posture.Tabletop } -> TabletopLayout()
            mediaQuery { windowPosture == UiMediaScope.Posture.Book } -> BookLayout()
            mediaQuery { windowPosture == UiMediaScope.Posture.Flat } -> FlatLayout()
        }
    }
}
// [END LAYOUT_FOR_TABLETOP_PREVIEW]

@Composable
private fun FlatLayout() {
    CenteredText(text = "Flat Layout")
}

@Composable
private fun TabletopLayout() {
    CenteredText(text = "Tabletop Layout")
}

@Composable
private fun BookLayout() {
    CenteredText(text = "Book Layout")
}

@Composable
private fun SinglePaneLayout() {
    FirstPane()
}

@Composable
private fun TwoPaneLayout() {
    Row(modifier = Modifier.fillMaxSize()) {
        FirstPane(modifier = Modifier.weight(1f))
        SecondPane(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun ThreePaneLayout() {
    Row(modifier = Modifier.fillMaxSize()) {
        FirstPane(modifier = Modifier.weight(1f))
        SecondPane(modifier = Modifier.weight(1f))
        ThirdPane(modifier = Modifier.weight(1f))
    }
}

@Composable
private fun FirstPane(modifier: Modifier = Modifier) {
    CenteredText(text = "First Pane", modifier = modifier, containerColor = Color.White)
}

@Composable
private fun SecondPane(modifier: Modifier = Modifier) {
    CenteredText(text = "Second Pane", modifier = modifier)
}

@Composable
private fun ThirdPane(modifier: Modifier = Modifier) {
    CenteredText(text = "Third Pane", modifier = modifier, containerColor = Color.White)
}

@Composable
private fun CenteredText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
    containerColor: Color = Color.LightGray
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .fillMaxSize()
            .background(containerColor)
    ) {
        Text(text = text, style = style)
    }
}

@Composable
private fun NormalSizeButton() {
    Button(onClick = {}) {
        Text("Normal Size Button")
    }
}

@Composable
private fun LargeSizeButton() {
    Button(onClick = {}, contentPadding = ButtonDefaults.ContentPadding * 1.24f) {
        Text("Large Size Button")
    }
}

/**
 * Multiplies each dimension of [PaddingValues] by a given [scale].
 */
@Composable
operator fun PaddingValues.times(scale: Float): PaddingValues {
    val layoutDirection = LocalLayoutDirection.current
    return PaddingValues(
        start = this.calculateStartPadding(layoutDirection) * scale,
        top = this.calculateTopPadding() * scale,
        end = this.calculateEndPadding(layoutDirection) * scale,
        bottom = this.calculateBottomPadding() * scale
    )
}

@Composable
private fun SuggestKeyboardConnect() {
    Text("Please connect a keyboard.")
}

@Composable
private fun MicButton() {
    IconButton(onClick = {}) {
        Icon(Icons.Default.Mic, contentDescription = "Start speaking")
    }
}

@Composable
private fun CameraButton() {
    IconButton(onClick = {}) {
        Icon(Icons.Default.Camera, contentDescription = "Start camera")
    }
}

@Composable
private fun OverrideUiMediaScope(
    windowWidth: Dp = LocalUiMediaScope.current.windowWidth,
    windowHeight: Dp = LocalUiMediaScope.current.windowHeight,
    windowPosture: UiMediaScope.Posture = LocalUiMediaScope.current.windowPosture,
    viewingDistance: UiMediaScope.ViewingDistance = LocalUiMediaScope.current.viewingDistance,
    hasMicrophone: Boolean = LocalUiMediaScope.current.hasMicrophone,
    hasCamera: Boolean = LocalUiMediaScope.current.hasCamera,
    pointerPrecision: UiMediaScope.PointerPrecision = LocalUiMediaScope.current.pointerPrecision,
    keyboardKind: UiMediaScope.KeyboardKind = LocalUiMediaScope.current.keyboardKind,
    content: @Composable () -> Unit,
) {
    OverrideUiMediaScope(
        uiMediaScope = createUiMediaScope(
            windowWidth = windowWidth,
            windowHeight = windowHeight,
            windowPosture = windowPosture,
            viewingDistance = viewingDistance,
            hasMicrophone = hasMicrophone,
            hasCamera = hasCamera,
            pointerPrecision = pointerPrecision,
            keyboardKind = keyboardKind
        ),
        content = content
    )
}

@Composable
private fun OverrideUiMediaScope(
    uiMediaScope: UiMediaScope = LocalUiMediaScope.current,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalUiMediaScope provides uiMediaScope, content = content)
    }

@Composable
private fun EnableMediaQueryIntegration(content: @Composable () -> Unit) {
    ComposeUiFlags.isMediaQueryIntegrationEnabled = true
    content()
}

private fun createUiMediaScope(
    windowWidth: Dp,
    windowHeight: Dp,
    windowPosture: UiMediaScope.Posture,
    viewingDistance: UiMediaScope.ViewingDistance,
    hasMicrophone: Boolean,
    hasCamera: Boolean,
    pointerPrecision: UiMediaScope.PointerPrecision,
    keyboardKind: UiMediaScope.KeyboardKind,
): UiMediaScope {
    return object : UiMediaScope {
        override val windowPosture: UiMediaScope.Posture
            get() = windowPosture
        override val windowWidth: Dp
            get() = windowWidth
        override val windowHeight: Dp
            get() = windowHeight
        override val pointerPrecision: UiMediaScope.PointerPrecision
            get() = pointerPrecision
        override val keyboardKind: UiMediaScope.KeyboardKind
            get() = keyboardKind
        override val hasMicrophone: Boolean
            get() = hasMicrophone
        override val hasCamera: Boolean
            get() = hasCamera
        override val viewingDistance: UiMediaScope.ViewingDistance
            get() = viewingDistance
    }
}

// Workaround for the issue where PreviewParameterProvider doesn't work with UiMediaScope.Posture.
sealed class Posture(val value: UiMediaScope.Posture) {
    object Tabletop : Posture(UiMediaScope.Posture.Tabletop)
    object Book : Posture(UiMediaScope.Posture.Book)
    object Flat : Posture(UiMediaScope.Posture.Flat)
}

class PostureProvider : PreviewParameterProvider<Posture> {
    private val postures = listOf(
        Posture.Tabletop,
        Posture.Book,
        Posture.Flat,
    )

    override val values: Sequence<Posture> = postures.asSequence()

    override fun getDisplayName(index: Int): String? {
        return if (index < 0 || index >= postures.size) {
            null
        } else {
            postures[index].value.toString()
        }
    }
}

sealed class Precision(val value: UiMediaScope.PointerPrecision) {
    object Fine : Precision(UiMediaScope.PointerPrecision.Fine)
    object Coarse : Precision(UiMediaScope.PointerPrecision.Coarse)
    object Blunt : Precision(UiMediaScope.PointerPrecision.Blunt)
    object None : Precision(UiMediaScope.PointerPrecision.None)
}

class PointerPrecisionProvider : PreviewParameterProvider<Precision> {
    private val all = listOf(
        Precision.Fine,
        Precision.Coarse,
        Precision.Blunt,
        Precision.None,
    )

    override val values: Sequence<Precision> = all.asSequence()

    override fun getDisplayName(index: Int): String? {
        return if (index < 0 || index >= all.size) {
            null
        } else {
            all[index].value.toString()
        }
    }
}

sealed class KeyboardKindHolder(val value: UiMediaScope.KeyboardKind) {
    object None : KeyboardKindHolder(UiMediaScope.KeyboardKind.None)
    object Virtual : KeyboardKindHolder(UiMediaScope.KeyboardKind.Virtual)
    object Physical : KeyboardKindHolder(UiMediaScope.KeyboardKind.Physical)
}

class KeyboardKindProvider : PreviewParameterProvider<KeyboardKindHolder> {
    private val all = listOf(
        KeyboardKindHolder.None,
        KeyboardKindHolder.Virtual,
        KeyboardKindHolder.Physical,
    )

    override val values: Sequence<KeyboardKindHolder> = all.asSequence()

    override fun getDisplayName(index: Int): String? {
        return if (index < 0 || index >= all.size) {
            null
        } else {
            all[index].value.toString()
        }
    }
}

sealed class ViewingDistanceHolder(val value: UiMediaScope.ViewingDistance) {
    object Near : ViewingDistanceHolder(UiMediaScope.ViewingDistance.Near)
    object Medium : ViewingDistanceHolder(UiMediaScope.ViewingDistance.Medium)
    object Far : ViewingDistanceHolder(UiMediaScope.ViewingDistance.Far)
}

class ViewingDistanceProvider : PreviewParameterProvider<ViewingDistanceHolder> {
    private val all = listOf(
        ViewingDistanceHolder.Near,
        ViewingDistanceHolder.Medium,
        ViewingDistanceHolder.Far,
    )

    override val values: Sequence<ViewingDistanceHolder> = all.asSequence()

    override fun getDisplayName(index: Int): String? {
        return if (index < 0 || index >= all.size) {
            null
        } else {
            all[index].value.toString()
        }
    }
}

sealed class MediaDeviceSupport(val camera: Boolean, val microphone: Boolean) {
    object Supported : MediaDeviceSupport(true, true) {
        override fun toString(): String = "Camera and microphone supported"
    }

    object CameraOnly : MediaDeviceSupport(true, false) {
        override fun toString(): String = "Camera only"
    }

    object MicrophoneOnly : MediaDeviceSupport(false, true) {
        override fun toString(): String = "Microphone only"
    }

    object Unsupported : MediaDeviceSupport(false, false) {
        override fun toString(): String = "Camera and microphone not supported"
    }
}

class MediaDeviceSupportProvider : PreviewParameterProvider<MediaDeviceSupport> {
    private val all = listOf(
        MediaDeviceSupport.Supported,
        MediaDeviceSupport.CameraOnly,
        MediaDeviceSupport.MicrophoneOnly,
        MediaDeviceSupport.Unsupported,
    )

    override val values: Sequence<MediaDeviceSupport> = all.asSequence()

    override fun getDisplayName(index: Int): String? {
        return if (index < 0 || index >= all.size) {
            null
        } else {
            all[index].toString()
        }
    }
}