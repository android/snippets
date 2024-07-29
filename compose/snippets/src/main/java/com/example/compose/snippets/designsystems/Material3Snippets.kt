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

package com.example.compose.snippets.designsystems

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.PermanentNavigationDrawer
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.snippets.R
import com.example.compose.snippets.ui.theme.Typography

private object Material3Snippets {
    // [START android_compose_material3_experimental_annotation]
    // import androidx.compose.material3.ExperimentalMaterial3Api
    @Composable
    fun AppComposable() {
        // M3 composables
    }

    // [END android_compose_material3_experimental_annotation]
}

@Composable
private fun Material3ThemeStart() {
    // [START android_compose_material3_theme_definition]
    MaterialTheme(
        colorScheme = /* [START_EXCLUDE] */lightColorScheme()/* [END_EXCLUDE] */,
        typography = /* [START_EXCLUDE] */Typography/* [END_EXCLUDE] */,
        shapes = /* [START_EXCLUDE] */Shapes()/* [END_EXCLUDE] */
    ) {
        // M3 app content
    }
    // [END android_compose_material3_theme_definition]
}

private object ColorScheme {
    // [START android_compose_material3_theme_colors]
    val md_theme_light_primary = Color(0xFF476810)
    val md_theme_light_onPrimary = Color(0xFFFFFFFF)
    val md_theme_light_primaryContainer = Color(0xFFC7F089)
    // ..
    // ..

    val md_theme_dark_primary = Color(0xFFACD370)
    val md_theme_dark_onPrimary = Color(0xFF213600)
    val md_theme_dark_primaryContainer = Color(0xFF324F00)
    // ..
    // ..
    // [END android_compose_material3_theme_colors]

    // [START android_compose_material3_theme_color_setup]
    private val LightColorScheme = lightColorScheme(
        primary = md_theme_light_primary,
        onPrimary = md_theme_light_onPrimary,
        primaryContainer = md_theme_light_primaryContainer,
        // ..
    )
    private val DarkColorScheme = darkColorScheme(
        primary = md_theme_dark_primary,
        onPrimary = md_theme_dark_onPrimary,
        primaryContainer = md_theme_dark_primaryContainer,
        // ..
    )

    @Composable
    fun ReplyTheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        content: @Composable () -> Unit
    ) {
        val colorScheme =
            if (!darkTheme) {
                LightColorScheme
            } else {
                DarkColorScheme
            }
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
    // [END android_compose_material3_theme_color_setup]

    @Composable
    private fun DynamicThemeSetup(darkTheme: Boolean = isSystemInDarkTheme()) {
        // [START android_compose_material3_theme_dynamic_color]
        // Dynamic color is available on Android 12+
        val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
        val colors = when {
            dynamicColor && darkTheme -> dynamicDarkColorScheme(LocalContext.current)
            dynamicColor && !darkTheme -> dynamicLightColorScheme(LocalContext.current)
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }
        // [END android_compose_material3_theme_dynamic_color]
    }

    @Composable
    private fun UsingColorTheme() {
        // [START android_compose_material3_use_color_theme]
        Text(
            text = "Hello theming",
            color = MaterialTheme.colorScheme.primary
        )
        // [END android_compose_material3_use_color_theme]
    }

    @Composable
    private fun UseColorTheme2() {
        val isSelected by remember {
            mutableStateOf(false)
        }
        // [START android_compose_material3_use_color_theme_2]
        Card(
            colors = CardDefaults.cardColors(
                containerColor =
                if (isSelected) MaterialTheme.colorScheme.primaryContainer
                else
                    MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Text(
                text = "Dinner club",
                style = MaterialTheme.typography.bodyLarge,
                color =
                if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer
                else MaterialTheme.colorScheme.onSurface,
            )
        }

        // [END android_compose_material3_use_color_theme_2]
    }
}

private object TypographySnippets {
    // [START android_compose_material3_typography_definition]
    val replyTypography = Typography(
        titleLarge = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 22.sp,
            lineHeight = 28.sp,
            letterSpacing = 0.sp
        ),
        titleMedium = TextStyle(
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp
        ),
        // ..
    )
    // ..
    // [END android_compose_material3_typography_definition]

    val replyTypography2 = Typography(
        // [START android_compose_material3_typography_body_large]
        bodyLarge = TextStyle(
            fontWeight = FontWeight.Normal,
            fontFamily = FontFamily.SansSerif,
            fontStyle = FontStyle.Italic,
            fontSize = 16.sp,
            lineHeight = 24.sp,
            letterSpacing = 0.15.sp,
            baselineShift = BaselineShift.Subscript
        ),
        // [END android_compose_material3_typography_body_large]
    )

    @Composable
    private fun TypographyThemeSetup() {
        // [START android_compose_material3_typography_theme_setup]
        MaterialTheme(
            typography = replyTypography,
        ) {
            // M3 app Content
        }
        // [END android_compose_material3_typography_theme_setup]
    }

    @Composable
    private fun TypographyUse() {
        // [START android_compose_material3_typography_use]
        Text(
            text = "Hello M3 theming",
            style = MaterialTheme.typography.titleLarge
        )
        Text(
            text = "you are learning typography",
            style = MaterialTheme.typography.bodyMedium
        )
        // [END android_compose_material3_typography_use]
    }
}

private object Material3ShapesSetup {
    // [START android_compose_material3_shape_setup]
    val replyShapes = Shapes(
        extraSmall = RoundedCornerShape(4.dp),
        small = RoundedCornerShape(8.dp),
        medium = RoundedCornerShape(12.dp),
        large = RoundedCornerShape(16.dp),
        extraLarge = RoundedCornerShape(24.dp)
    )
    // [END android_compose_material3_shape_setup]

    @Composable
    private fun ShapeThemeSetup() {
        // [START android_compose_material3_shape_theme]
        MaterialTheme(
            shapes = replyShapes,
        ) {
            // M3 app Content
        }
        // [END android_compose_material3_shape_theme]
    }

    @Composable
    private fun ShapeUsage() {
        // [START android_compose_material3_shape_usage]
        Card(shape = MaterialTheme.shapes.medium) { /* card content */ }
        FloatingActionButton(
            shape = MaterialTheme.shapes.large,
            onClick = {
            }
        ) {
            /* fab content */
        }
        // [END android_compose_material3_shape_usage]
    }
    @Composable
    private fun ShapeUsage2() {
        // [START android_compose_material3_shape_usage_2]
        Card(shape = RectangleShape) { /* card content */ }
        Card(shape = CircleShape) { /* card content */ }
        // [END android_compose_material3_shape_usage_2]
    }
}

private object EmphasisSnippets {
    val replyTypography = Typography(
        // [START android_compose_material3_emphasis]
        bodyLarge = TextStyle(
            fontWeight = FontWeight.Bold
        ),
        bodyMedium = TextStyle(
            fontWeight = FontWeight.Normal
        )
        // [END android_compose_material3_emphasis]
    )
}
@Composable
private fun ElevationSnippets(content: @Composable ColumnScope.() -> Unit) {
    // [START android_compose_material3_elevation]
    Surface(
        modifier = Modifier,
        tonalElevation = /*[START_EXCLUDE] */ 2.dp/*[END_EXCLUDE]*/,
        shadowElevation = /*[START_EXCLUDE] */ 2.dp/*[END_EXCLUDE]*/
    ) {
        Column(content = content)
    }
    // [END android_compose_material3_elevation]
}

private object MaterialComponentsExamples {
    @Composable
    fun ButtonUsage() {
        // [START android_compose_material3_button_usage]
        Button(onClick = { /*..*/ }) {
            Text(text = "My Button")
        }
        // [END android_compose_material3_button_usage]
    }

    @Composable
    fun ExtendedFloatingActionButtonUsage() {
        // [START android_compose_material3_extended_button_usage]
        ExtendedFloatingActionButton(
            onClick = { /*..*/ },
            modifier = Modifier
        ) {
            Icon(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(id = R.string.edit),
            )
            Text(
                text = stringResource(id = R.string.add_entry),
            )
        }
        // [END android_compose_material3_extended_button_usage]
    }
    @Composable
    fun FilledButtonUsageHighEmphasis() {
        // [START android_compose_material3_filled_button_high_emphasis]
        Button(onClick = { /*..*/ }) {
            Text(text = stringResource(id = R.string.view_entry))
        }
        // [END android_compose_material3_filled_button_high_emphasis]
    }
    @Composable
    fun TextButtonUsageLowEmphasis() {
        // [START android_compose_material3_text_button_low_emphasis]
        TextButton(onClick = { /*..*/ }) {
            Text(text = stringResource(id = R.string.replated_articles))
        }
        // [END android_compose_material3_text_button_low_emphasis]
    }
    enum class Destinations {
        Home,
        Emails,
        Sent,
        Drafts
    }
    @Composable
    fun NavBarUsage() {
        val selectedDestination by remember {
            mutableStateOf(Destinations.Home)
        }
        // [START android_compose_material3_nav_bar_usage]
        NavigationBar(modifier = Modifier.fillMaxWidth()) {
            Destinations.entries.forEach { replyDestination ->
                NavigationBarItem(
                    selected = selectedDestination == replyDestination,
                    onClick = { },
                    icon = { }
                )
            }
        }
        // [END android_compose_material3_nav_bar_usage]
    }
    @Composable
    fun NavRailUsage() {
        val selectedDestination by remember {
            mutableStateOf(Destinations.Home)
        }
        // [START android_compose_material3_nav_rail_usage]
        NavigationRail(
            modifier = Modifier.fillMaxHeight(),
        ) {
            Destinations.entries.forEach { replyDestination ->
                NavigationRailItem(
                    selected = selectedDestination == replyDestination,
                    onClick = { },
                    icon = { }
                )
            }
        }
        // [END android_compose_material3_nav_rail_usage]
    }
    @Composable
    fun PermanentNavDrawerUsage() {
        val selectedDestination by remember {
            mutableStateOf(Destinations.Home)
        }
        // [START android_compose_material3_permanent_nav_drawer]
        PermanentNavigationDrawer(modifier = Modifier.fillMaxHeight(), drawerContent = {
            Destinations.entries.forEach { replyDestination ->
                NavigationRailItem(
                    selected = selectedDestination == replyDestination,
                    onClick = { },
                    icon = { },
                    label = { }
                )
            }
        }) {
        }
        // [END android_compose_material3_permanent_nav_drawer]
    }

    @Composable
    fun CardThemingExample() {
        // [START android_compose_material3_card_theming]
        val customCardColors = CardDefaults.cardColors(
            contentColor = MaterialTheme.colorScheme.primary,
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            disabledContentColor = MaterialTheme.colorScheme.surface,
            disabledContainerColor = MaterialTheme.colorScheme.onSurface,
        )
        val customCardElevation = CardDefaults.cardElevation(
            defaultElevation = 8.dp,
            pressedElevation = 2.dp,
            focusedElevation = 4.dp
        )
        Card(
            colors = customCardColors,
            elevation = customCardElevation
        ) {
            // m3 card content
        }
        // [END android_compose_material3_card_theming]
    }
}

private object MaterialAccessibilityExamples {
    @Composable
    fun ButtonContrastExample() {
        // [START android_compose_material3_button_contrast_example]
        // ✅ Button with sufficient contrast ratio
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
        }

        // ❌ Button with poor contrast ratio
        Button(
            onClick = { },
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                contentColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
        }
        // [END android_compose_material3_button_contrast_example]
    }
}
