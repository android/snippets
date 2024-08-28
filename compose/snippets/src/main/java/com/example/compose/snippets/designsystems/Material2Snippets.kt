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

@file:Suppress("unused", "DEPRECATION_ERROR", "UsingMaterialAndMaterial3Libraries")

package com.example.compose.snippets.designsystems

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.ContentAlpha
import androidx.compose.material.Icon
import androidx.compose.material.LocalContentAlpha
import androidx.compose.material.LocalElevationOverlay
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Shapes
import androidx.compose.material.SnackbarDefaults.backgroundColor
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.Typography
import androidx.compose.material.contentColorFor
import androidx.compose.material.darkColors
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.lightColors
import androidx.compose.material.primarySurface
import androidx.compose.material.ripple.LocalRippleTheme
import androidx.compose.material.ripple.RippleTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.snippets.R

@Composable
fun Material2() {
    // [START android_compose_material2_theme]
    MaterialTheme(
        colors = // [START_EXCLUDE]
        MaterialTheme.colors,
        // [END_EXCLUDE]
        typography = // [START_EXCLUDE] */
        MaterialTheme.typography,
        // [END_EXCLUDE]
        shapes = // [START_EXCLUDE]
        MaterialTheme.shapes
        // [END_EXCLUDE]
    ) {
        // app content
    }
    // [END android_compose_material2_theme]
}

@Composable
fun Colors() {
    // [START android_compose_material2_colors]
    val Red = Color(0xffff0000)
    val Blue = Color(red = 0f, green = 0f, blue = 1f)
    // [END android_compose_material2_colors]
}

private val Yellow400 = Color(0xffffeb46)
private val Yellow500 = Color(0xffffeb46)
private val Blue700 = Color(0xff91a4fc)

// [START android_compose_material2_colors_builder]
private val Yellow200 = Color(0xffffeb46)
private val Blue200 = Color(0xff91a4fc)
// ...

private val DarkColors = darkColors(
    primary = Yellow200,
    secondary = Blue200,
    // ...
)
private val LightColors = lightColors(
    primary = Yellow500,
    primaryVariant = Yellow400,
    secondary = Blue700,
    // ...
)
// [END android_compose_material2_colors_builder]

private val darkTheme = true

@Composable
fun ThemeBuilder() {
    // [START android_compose_material2_theme_builder]
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors
    ) {
        // app content
    }
    // [END android_compose_material2_theme_builder]
}

@Composable
fun ColorUsage() {
    // [START android_compose_material2_color_usage]
    Text(
        text = "Hello theming",
        color = MaterialTheme.colors.primary
    )
    // [END android_compose_material2_color_usage]

    val color = Color(0xffffeb46)

    // [START android_compose_material2_color_surface]
    Surface(
        color = MaterialTheme.colors.surface,
        contentColor = contentColorFor(color),
        // ...
    ) { /* ... */ }

    TopAppBar(
        backgroundColor = MaterialTheme.colors.primarySurface,
        contentColor = contentColorFor(backgroundColor),
        // ...
    ) { /* ... */ }
    // [END android_compose_material2_color_surface]

    // [START android_compose_material2_content_alpha]
    // By default, both Icon & Text use the combination of LocalContentColor &
    // LocalContentAlpha. De-emphasize content by setting content alpha
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
        Text(
            // [START_EXCLUDE]
            ""
            // [END_EXCLUDE]
        )
    }
    CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.disabled) {
        Icon(
            // [START_EXCLUDE]
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = null
            // [END_EXCLUDE]
        )
        Text(
            // [START_EXCLUDE]
            ""
            // [END_EXCLUDE]
        )
    }
    // [END android_compose_material2_content_alpha]
}

// [START android_compose_material2_dark_theme]
@Composable
fun MyTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkColors else LightColors,
        /*...*/
        content = content
    )
}
// [END android_compose_material2_dark_theme]

@Composable
fun DarkTheme() {
    // [START android_compose_material2_dark_theme_check]
    val isLightTheme = MaterialTheme.colors.isLight
    Icon(
        painterResource(
            id = if (isLightTheme) {
                R.drawable.ic_sun_24
            } else {
                R.drawable.ic_moon_24
            }
        ),
        contentDescription = "Theme"
    )
    // [END android_compose_material2_dark_theme_check]
}

@Composable
fun Elevation() {
    // [START android_compose_material2_elevation]
    Surface(
        elevation = 2.dp,
        color = MaterialTheme.colors.surface, // color will be adjusted for elevation
        /*...*/
    ) { /*...*/ }
    // [END android_compose_material2_elevation]

    // [START android_compose_material2_elevation_overlays]
    // Elevation overlays
    // Implemented in Surface (and any components that use it)
    val color = MaterialTheme.colors.surface
    val elevation = 4.dp
    val overlaidColor = LocalElevationOverlay.current?.apply(
        color, elevation
    )
    // [END android_compose_material2_elevation_overlays]

    // [START android_compose_material2_elevation_overlays_disable]
    MyTheme {
        CompositionLocalProvider(LocalElevationOverlay provides null) {
            // Content without elevation overlays
        }
    }
    // [END android_compose_material2_elevation_overlays_disable]
}

@Composable
fun ColorAccents() {
    // [START android_compose_material2_color_accents]
    Surface(
        // Switches between primary in light theme and surface in dark theme
        color = MaterialTheme.colors.primarySurface,
        /*...*/
    ) { /*...*/ }
    // [END android_compose_material2_color_accents]
}

@Composable
fun TypeSystem() {
    // [START android_compose_material2_typography]
    val raleway = FontFamily(
        Font(R.font.raleway_regular),
        Font(R.font.raleway_medium, FontWeight.W500),
        Font(R.font.raleway_semibold, FontWeight.SemiBold)
    )

    val myTypography = Typography(
        h1 = TextStyle(
            fontFamily = raleway,
            fontWeight = FontWeight.W300,
            fontSize = 96.sp
        ),
        body1 = TextStyle(
            fontFamily = raleway,
            fontWeight = FontWeight.W600,
            fontSize = 16.sp
        )
        /*...*/
    )
    MaterialTheme(typography = myTypography, /*...*/) {
        /*...*/
    }
    // [END android_compose_material2_typography]

    // [START android_compose_material2_typography_default]
    val typography = Typography(defaultFontFamily = raleway)
    MaterialTheme(typography = typography, /*...*/) {
        /*...*/
    }
    // [END android_compose_material2_typography_default]

    // [START android_compose_material2_typography_using]
    Text(
        text = "Subtitle2 styled",
        style = MaterialTheme.typography.subtitle2
    )
    // [END android_compose_material2_typography_using]
}

@Composable
fun ShapeSystem() {
    // [START android_compose_material2_shapes]
    val shapes = Shapes(
        small = RoundedCornerShape(percent = 50),
        medium = RoundedCornerShape(0f),
        large = CutCornerShape(
            topStart = 16.dp,
            topEnd = 0.dp,
            bottomEnd = 0.dp,
            bottomStart = 16.dp
        )
    )

    MaterialTheme(shapes = shapes, /*...*/) {
        /*...*/
    }
    // [END android_compose_material2_shapes]

    // [START android_compose_material2_shapes_usage]
    Surface(
        shape = MaterialTheme.shapes.medium, /*...*/
    ) {
        /*...*/
    }
    // [END android_compose_material2_shapes_usage]
}

// [START android_compose_material2_styles]
@Composable
fun MyButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Button(
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.secondary
        ),
        onClick = onClick,
        modifier = modifier,
        content = content
    )
}
// [END android_compose_material2_styles]

@Composable
fun PinkTheme(
    content: @Composable () -> Unit
) {
}
@Composable
fun BlueTheme(
    content: @Composable () -> Unit
) {
}

// [START android_compose_material2_theme_overlays]
@Composable
fun DetailsScreen(/* ... */) {
    PinkTheme {
        // other content
        RelatedSection()
    }
}

@Composable
fun RelatedSection(/* ... */) {
    BlueTheme {
        // content
    }
}
// [END android_compose_material2_theme_overlays]

@Composable
fun ComponentStates() {
    // [START android_compose_material2_component_states]
    Button(
        onClick = { /* ... */ },
        enabled = true,
        // Custom colors for different states
        colors = ButtonDefaults.buttonColors(
            backgroundColor = MaterialTheme.colors.secondary,
            disabledBackgroundColor = MaterialTheme.colors.onBackground
                .copy(alpha = 0.2f)
                .compositeOver(MaterialTheme.colors.background)
            // Also contentColor and disabledContentColor
        ),
        // Custom elevation for different states
        elevation = ButtonDefaults.elevation(
            defaultElevation = 8.dp,
            disabledElevation = 2.dp,
            // Also pressedElevation
        )
    ) { /* ... */ }
    // [END android_compose_material2_component_states]
}

// [START android_compose_material2_ripples]
@Composable
fun MyApp() {
    MaterialTheme {
        CompositionLocalProvider(
            LocalRippleTheme provides SecondaryRippleTheme
        ) {
            // App content
        }
    }
}

@Immutable
private object SecondaryRippleTheme : RippleTheme {
    @Composable
    override fun defaultColor() = RippleTheme.defaultRippleColor(
        contentColor = MaterialTheme.colors.secondary,
        lightTheme = MaterialTheme.colors.isLight
    )

    @Composable
    override fun rippleAlpha() = RippleTheme.defaultRippleAlpha(
        contentColor = MaterialTheme.colors.secondary,
        lightTheme = MaterialTheme.colors.isLight
    )
}
// [END android_compose_material2_ripples]
