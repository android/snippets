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

@file:Suppress("unused")

package com.example.compose.snippets.resources

import androidx.compose.animation.graphics.ExperimentalAnimationGraphicsApi
import androidx.compose.animation.graphics.res.animatedVectorResource
import androidx.compose.animation.graphics.res.rememberAnimatedVectorPainter
import androidx.compose.animation.graphics.vector.AnimatedImageVector
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.example.compose.snippets.R

@Composable
fun Strings() {
    // [START android_compose_resources_strings]
    // In the res/values/strings.xml file
    // <string name="compose">Jetpack Compose</string>

    // In your Compose code
    Text(
        text = stringResource(R.string.compose)
    )
    // [END android_compose_resources_strings]

    // [START android_compose_resources_strings_formatting]
    // In the res/values/strings.xml file
    // <string name="congratulate">Happy %1$s %2$d</string>

    // In your Compose code
    Text(
        text = stringResource(R.string.congratulate, "New Year", 2021)
    )
    // [END android_compose_resources_strings_formatting]

    val quantity = 1
    // [START android_compose_resources_strings_plural]
    // In the res/strings.xml file
    // <plurals name="runtime_format">
    //    <item quantity="one">%1$d minute</item>
    //    <item quantity="other">%1$d minutes</item>
    // </plurals>

    // In your Compose code
    Text(
        text = pluralStringResource(
            R.plurals.runtime_format,
            quantity,
            quantity
        )
    )
    // [END android_compose_resources_strings_plural]
}

@Composable
fun Dimensions() {
    // [START android_compose_resources_dimensions]
    // In the res/values/dimens.xml file
    // <dimen name="padding_small">8dp</dimen>

    // In your Compose code
    val smallPadding = dimensionResource(R.dimen.padding_small)
    Text(
        text = "...",
        modifier = Modifier.padding(smallPadding)
    )
    // [END android_compose_resources_dimensions]
}

@Composable
fun Colors() {
    // [START android_compose_resources_colors]
    // In the res/colors.xml file
    // <color name="purple_200">#FFBB86FC</color>

    // In your Compose code
    Divider(color = colorResource(R.color.purple_200))
    // [END android_compose_resources_colors]
}

@Composable
fun VectorAssets() {
    // [START android_compose_resources_vector_assets]
    // Files in res/drawable folders. For example:
    // - res/drawable-nodpi/ic_logo.xml
    // - res/drawable-xxhdpi/ic_logo.png

    // In your Compose code
    Icon(
        painter = painterResource(id = R.drawable.ic_logo),
        contentDescription = null // decorative element
    )
    // [END android_compose_resources_vector_assets]
}

@OptIn(ExperimentalAnimationGraphicsApi::class)
@Composable
fun AnimatedVectorDrawables() {
    // [START android_compose_resources_avd]
    // Files in res/drawable folders. For example:
    // - res/drawable/ic_hourglass_animated.xml

    // In your Compose code
    val image =
        AnimatedImageVector.animatedVectorResource(R.drawable.ic_hourglass_animated)
    val atEnd by remember { mutableStateOf(false) }
    Icon(
        painter = rememberAnimatedVectorPainter(image, atEnd),
        contentDescription = null // decorative element
    )
    // [END android_compose_resources_avd]
}

@Composable
fun Icons() {
    // [START android_compose_resources_icons]
    Icon(Icons.Rounded.Menu, contentDescription = "Localized description")
    // [END android_compose_resources_icons]
}

// [START android_compose_resources_fonts]
// Define and load the fonts of the app
private val light = Font(R.font.raleway_light, FontWeight.W300)
private val regular = Font(R.font.raleway_regular, FontWeight.W400)
private val medium = Font(R.font.raleway_medium, FontWeight.W500)
private val semibold = Font(R.font.raleway_semibold, FontWeight.W600)

// Create a font family to use in TextStyles
private val craneFontFamily = FontFamily(light, regular, medium, semibold)

// Use the font family to define a custom typography
val craneTypography = Typography(
    titleLarge = TextStyle(
        fontFamily = craneFontFamily
    ) /* ... */
)

// Pass the typography to a MaterialTheme that will create a theme using
// that typography in the part of the UI hierarchy where this theme is used
@Composable
fun CraneTheme(content: @Composable () -> Unit) {
    MaterialTheme(typography = craneTypography) {
        content()
    }
}
// [END android_compose_resources_fonts]
