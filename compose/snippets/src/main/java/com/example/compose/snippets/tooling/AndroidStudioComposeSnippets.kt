/*
 * Copyright 2023 The Android Open Source Project
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

package com.example.compose.snippets.tooling

import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Device
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.tooling.preview.UiMode
import androidx.compose.ui.tooling.preview.Wallpaper
import androidx.compose.ui.tooling.preview.Wallpapers
import com.example.compose.snippets.R
import com.example.compose.snippets.interop.User

// [START android_compose_tooling_simple_composable]
@Composable
fun SimpleComposable() {
    Text("Hello World")
}
// [END android_compose_tooling_simple_composable]

// [START android_compose_tooling_simple_composable_preview]
@Preview
@Composable
fun SimpleComposablePreview() {
    SimpleComposable()
}
// [END android_compose_tooling_simple_composable_preview]

// [START android_compose_tooling_local_inspection_mode]
@Composable
fun GreetingScreen(name: String) {
    if (LocalInspectionMode.current) {
        // Show this text in a preview window:
        Text("Hello preview user!")
    } else {
        // Show this text in the app:
        Text("Hello $name!")
    }
}
// [END android_compose_tooling_local_inspection_mode]

// [START android_compose_tooling_multipreview_annotations]
@Preview(
    name = "small font",
    group = "font scales",
    fontScale = 0.5f
)
@Preview(
    name = "large font",
    group = "font scales",
    fontScale = 1.5f
)
annotation class FontScalePreviews
// [END android_compose_tooling_multipreview_annotations]

// [START android_compose_tooling_multipreview_usage]
@FontScalePreviews
@Composable
fun HelloWorldPreview() {
    Text("Hello World")
}
// [END android_compose_tooling_multipreview_usage]

// [START android_compose_tooling_multipreview_combine]
@Preview(
    name = "Spanish",
    group = "locale",
    locale = "es"
)
@FontScalePreviews
annotation class CombinedPreviews

@CombinedPreviews
@Composable
fun HelloWorldPreview2() {
    MaterialTheme { Surface { Text(stringResource(R.string.hello_world)) } }
}
// [END android_compose_tooling_multipreview_combine]

// [START android_compose_tooling_preview_bg_color]
@Preview(showBackground = true, backgroundColor = 0xFF00FF00)
@Composable
fun WithGreenBackground() {
    Text("Hello World")
}
// [END android_compose_tooling_preview_bg_color]

// [START android_compose_tooling_preview_dimens]
@Preview(widthDp = 50, heightDp = 50)
@Composable
fun SquareComposablePreview() {
    Box(Modifier.background(Color.Yellow)) {
        Text("Hello World")
    }
}
// [END android_compose_tooling_preview_dimens]

// [START android_compose_tooling_preview_locale]
@Preview(locale = "fr-rFR")
@Composable
fun DifferentLocaleComposablePreview() {
    Text(text = stringResource(R.string.greeting))
}
// [END android_compose_tooling_preview_locale]

// [START android_compose_tooling_preview_system_ui]
@Preview(showSystemUi = true)
@Composable
fun DecoratedComposablePreview() {
    Text("Hello World")
}
// [END android_compose_tooling_preview_system_ui]

// [START android_compose_tooling_preview_parameter_provider_composable]
@Preview
@Composable
fun UserProfilePreview(
    @PreviewParameter(UserPreviewParameterProvider::class) user: User
) {
    UserProfile(user)
}

// [START_EXCLUDE silent]
@Composable
fun UserProfile(user: User) {
}
// [END_EXCLUDE]
// [END android_compose_tooling_preview_parameter_provider_composable]

// [START android_compose_tooling_preview_parameter_provider]
class UserPreviewParameterProvider : PreviewParameterProvider<User> {
    override val values = sequenceOf(
        User("Elise"),
        User("Frank"),
        User("Julia")
    )
}
// [END android_compose_tooling_preview_parameter_provider]

// [START android_compose_tooling_preview_parameter_provider_composable2]
@Preview
@Composable
fun UserProfilePreview2(
    @PreviewParameter(UserPreviewParameterProvider::class, limit = 2) user: User
) {
    UserProfile(user)
}
// [END android_compose_tooling_preview_parameter_provider_composable2]

// [START android_compose_tooling_preview_annotation]
annotation class Preview(
    val name: String = "",
    val group: String = "",
    @IntRange(from = 1) val apiLevel: Int = -1,
    val widthDp: Int = -1,
    val heightDp: Int = -1,
    val locale: String = "",
    @FloatRange(from = 0.01) val fontScale: Float = 1f,
    val showSystemUi: Boolean = false,
    val showBackground: Boolean = false,
    val backgroundColor: Long = 0,
    @UiMode val uiMode: Int = 0,
    @Device val device: String = Devices.DEFAULT,
    @Wallpaper val wallpaper: Int = Wallpapers.NONE,
)
// [END android_compose_tooling_preview_annotation]
