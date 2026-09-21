/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.compose.preview.wasm.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import kotlin.math.min

/**
 * Material Theme Builder presets matching official Material 3 color generation.
 * Reference: https://material-foundation.github.io/material-theme-builder/
 */
enum class ThemePreset(
    val id: String,
    val displayName: String,
    val seedColor: Color,
    val lightColorScheme: ColorScheme,
    val darkColorScheme: ColorScheme
) {
    ANDROID_GREEN(
        id = "android_green",
        displayName = "Android Green",
        seedColor = Color(0xFF3DDC84),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF006D3B),
            onPrimary = Color.White,
            primaryContainer = Color(0xFF94F7B3),
            onPrimaryContainer = Color(0xFF00210E),
            secondary = Color(0xFF4F6353),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFD2E8D4),
            onSecondaryContainer = Color(0xFF0D1F13),
            tertiary = Color(0xFF3A646E),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFBEEAF5),
            onTertiaryContainer = Color(0xFF001F25),
            background = Color(0xFFF7FBF4),
            onBackground = Color(0xFF191C19),
            surface = Color(0xFFF7FBF4),
            onSurface = Color(0xFF191C19),
            surfaceVariant = Color(0xFFDCE5DB),
            onSurfaceVariant = Color(0xFF414942),
            surfaceTint = Color(0xFF006D3B),
            surfaceDim = Color(0xFFD8DBD4),
            surfaceBright = Color(0xFFF7FBF4),
            surfaceContainerLowest = Color(0xFFFFFFFF),
            surfaceContainerLow = Color(0xFFF1F5EE),
            surfaceContainer = Color(0xFFEBF0E8),
            surfaceContainerHigh = Color(0xFFE6EAE3),
            surfaceContainerHighest = Color(0xFFE0E5DD),
            outline = Color(0xFF717971),
            outlineVariant = Color(0xFFC0C9BF)
        ),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF78DA99),
            onPrimary = Color(0xFF00391C),
            primaryContainer = Color(0xFF00522B),
            onPrimaryContainer = Color(0xFF94F7B3),
            secondary = Color(0xFFB6CCB8),
            onSecondary = Color(0xFF223527),
            secondaryContainer = Color(0xFF384B3D),
            onSecondaryContainer = Color(0xFFD2E8D4),
            tertiary = Color(0xFFA2CED9),
            onTertiary = Color(0xFF02363F),
            tertiaryContainer = Color(0xFF204D56),
            onTertiaryContainer = Color(0xFFBEEAF5),
            background = Color(0xFF101411),
            onBackground = Color(0xFFE1E3DE),
            surface = Color(0xFF101411),
            onSurface = Color(0xFFE1E3DE),
            surfaceVariant = Color(0xFF414942),
            onSurfaceVariant = Color(0xFFC0C9BF),
            surfaceTint = Color(0xFF78DA99),
            surfaceDim = Color(0xFF101411),
            surfaceBright = Color(0xFF363A36),
            surfaceContainerLowest = Color(0xFF0B0F0C),
            surfaceContainerLow = Color(0xFF191C19),
            surfaceContainer = Color(0xFF1D201D),
            surfaceContainerHigh = Color(0xFF272B27),
            surfaceContainerHighest = Color(0xFF323632),
            outline = Color(0xFF8B938A),
            outlineVariant = Color(0xFF414942)
        )
    ),
    BASELINE_PURPLE(
        id = "baseline_purple",
        displayName = "Material 3 Purple",
        seedColor = Color(0xFF6750A4),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF6750A4),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFEADDFF),
            onPrimaryContainer = Color(0xFF21005D),
            secondary = Color(0xFF625B71),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFE8DEF8),
            onSecondaryContainer = Color(0xFF1D192B),
            tertiary = Color(0xFF7D5260),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFFFD8E4),
            onTertiaryContainer = Color(0xFF31111D),
            background = Color(0xFFFEF7FF),
            onBackground = Color(0xFF1D1B20),
            surface = Color(0xFFFEF7FF),
            onSurface = Color(0xFF1D1B20),
            surfaceVariant = Color(0xFFE7E0EC),
            onSurfaceVariant = Color(0xFF49454F),
            surfaceTint = Color(0xFF6750A4),
            surfaceDim = Color(0xFFDED8E1),
            surfaceBright = Color(0xFFFEF7FF),
            surfaceContainerLowest = Color(0xFFFFFFFF),
            surfaceContainerLow = Color(0xFFF7F2FA),
            surfaceContainer = Color(0xFFF3EDF7),
            surfaceContainerHigh = Color(0xFFECE6F0),
            surfaceContainerHighest = Color(0xFFE6E0E9),
            outline = Color(0xFF79747E),
            outlineVariant = Color(0xFFCAC4D0)
        ),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFD0BCFF),
            onPrimary = Color(0xFF381E72),
            primaryContainer = Color(0xFF4F378B),
            onPrimaryContainer = Color(0xFFEADDFF),
            secondary = Color(0xFFCCC2DC),
            onSecondary = Color(0xFF332D41),
            secondaryContainer = Color(0xFF4A4458),
            onSecondaryContainer = Color(0xFFE8DEF8),
            tertiary = Color(0xFFEFB8C8),
            onTertiary = Color(0xFF492532),
            tertiaryContainer = Color(0xFF633B48),
            onTertiaryContainer = Color(0xFFFFD8E4),
            background = Color(0xFF141218),
            onBackground = Color(0xFFE6E0E9),
            surface = Color(0xFF141218),
            onSurface = Color(0xFFE6E0E9),
            surfaceVariant = Color(0xFF49454F),
            onSurfaceVariant = Color(0xFFCAC4D0),
            surfaceTint = Color(0xFFD0BCFF),
            surfaceDim = Color(0xFF141218),
            surfaceBright = Color(0xFF3B383E),
            surfaceContainerLowest = Color(0xFF0F0D13),
            surfaceContainerLow = Color(0xFF1D1B20),
            surfaceContainer = Color(0xFF211F26),
            surfaceContainerHigh = Color(0xFF2B2930),
            surfaceContainerHighest = Color(0xFF36343B),
            outline = Color(0xFF938F99),
            outlineVariant = Color(0xFF49454F)
        )
    ),
    GOOGLE_BLUE(
        id = "google_blue",
        displayName = "Google Blue",
        seedColor = Color(0xFF4285F4),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF005AC1),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFD8E2FF),
            onPrimaryContainer = Color(0xFF001A41),
            secondary = Color(0xFF575E71),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFDBE2F9),
            onSecondaryContainer = Color(0xFF141B2C),
            tertiary = Color(0xFF715573),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFFBD7FC),
            onTertiaryContainer = Color(0xFF29132D),
            background = Color(0xFFFDFBFF),
            onBackground = Color(0xFF1A1B1F),
            surface = Color(0xFFFDFBFF),
            onSurface = Color(0xFF1A1B1F),
            surfaceVariant = Color(0xFFE1E2EC),
            onSurfaceVariant = Color(0xFF44474F),
            surfaceTint = Color(0xFF005AC1),
            surfaceDim = Color(0xFFDAD9DE),
            surfaceBright = Color(0xFFFDFBFF),
            surfaceContainerLowest = Color(0xFFFFFFFF),
            surfaceContainerLow = Color(0xFFF4F3F8),
            surfaceContainer = Color(0xFFEEEDF2),
            surfaceContainerHigh = Color(0xFFE8E7EC),
            surfaceContainerHighest = Color(0xFFE2E2E7),
            outline = Color(0xFF74777F),
            outlineVariant = Color(0xFFC4C6D0)
        ),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFADC6FF),
            onPrimary = Color(0xFF002E69),
            primaryContainer = Color(0xFF004494),
            onPrimaryContainer = Color(0xFFD8E2FF),
            secondary = Color(0xFFBFC6DC),
            onSecondary = Color(0xFF293041),
            secondaryContainer = Color(0xFF3F4759),
            onSecondaryContainer = Color(0xFFDBE2F9),
            tertiary = Color(0xFFDEBCDF),
            onTertiary = Color(0xFF402843),
            tertiaryContainer = Color(0xFF583E5B),
            onTertiaryContainer = Color(0xFFFBD7FC),
            background = Color(0xFF1A1B1F),
            onBackground = Color(0xFFE3E2E6),
            surface = Color(0xFF1A1B1F),
            onSurface = Color(0xFFE3E2E6),
            surfaceVariant = Color(0xFF44474F),
            onSurfaceVariant = Color(0xFFC4C6D0),
            surfaceTint = Color(0xFFADC6FF),
            surfaceDim = Color(0xFF1A1B1F),
            surfaceBright = Color(0xFF3A393E),
            surfaceContainerLowest = Color(0xFF0E0E12),
            surfaceContainerLow = Color(0xFF1A1B1F),
            surfaceContainer = Color(0xFF1E1F24),
            surfaceContainerHigh = Color(0xFF292A2E),
            surfaceContainerHighest = Color(0xFF343439),
            outline = Color(0xFF8E9099),
            outlineVariant = Color(0xFF44474F)
        )
    ),
    OCEAN_TEAL(
        id = "ocean_teal",
        displayName = "Ocean Teal",
        seedColor = Color(0xFF006A6A),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF006A6A),
            onPrimary = Color.White,
            primaryContainer = Color(0xFF6FF7F7),
            onPrimaryContainer = Color(0xFF002020),
            secondary = Color(0xFF4A6363),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFCCE8E8),
            onSecondaryContainer = Color(0xFF051F1F),
            tertiary = Color(0xFF4B607C),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFD3E4FF),
            onTertiaryContainer = Color(0xFF041C35),
            background = Color(0xFFFAFDFB),
            onBackground = Color(0xFF191C1C),
            surface = Color(0xFFFAFDFB),
            onSurface = Color(0xFF191C1C),
            surfaceVariant = Color(0xFFDAE4E4),
            onSurfaceVariant = Color(0xFF3F4948),
            surfaceTint = Color(0xFF006A6A),
            surfaceDim = Color(0xFFD7DAD8),
            surfaceBright = Color(0xFFFAFDFB),
            surfaceContainerLowest = Color(0xFFFFFFFF),
            surfaceContainerLow = Color(0xFFF1F5F3),
            surfaceContainer = Color(0xFFEBEFED),
            surfaceContainerHigh = Color(0xFFE5EAE7),
            surfaceContainerHighest = Color(0xFFE0E4E1),
            outline = Color(0xFF6F7979),
            outlineVariant = Color(0xFFBEC8C8)
        ),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF4CDADA),
            onPrimary = Color(0xFF003737),
            primaryContainer = Color(0xFF004F4F),
            onPrimaryContainer = Color(0xFF6FF7F7),
            secondary = Color(0xFFB0CCCC),
            onSecondary = Color(0xFF1B3435),
            secondaryContainer = Color(0xFF324B4B),
            onSecondaryContainer = Color(0xFFCCE8E8),
            tertiary = Color(0xFFB3C8E8),
            onTertiary = Color(0xFF1C314B),
            tertiaryContainer = Color(0xFF334863),
            onTertiaryContainer = Color(0xFFD3E4FF),
            background = Color(0xFF191C1C),
            onBackground = Color(0xFFE0E3E3),
            surface = Color(0xFF191C1C),
            onSurface = Color(0xFFE0E3E3),
            surfaceVariant = Color(0xFF3F4948),
            onSurfaceVariant = Color(0xFFBEC8C8),
            surfaceTint = Color(0xFF4CDADA),
            surfaceDim = Color(0xFF191C1C),
            surfaceBright = Color(0xFF393C3B),
            surfaceContainerLowest = Color(0xFF0D1010),
            surfaceContainerLow = Color(0xFF191C1C),
            surfaceContainer = Color(0xFF1D2020),
            surfaceContainerHigh = Color(0xFF272B2A),
            surfaceContainerHighest = Color(0xFF323535),
            outline = Color(0xFF899392),
            outlineVariant = Color(0xFF3F4948)
        )
    ),
    TERRACOTTA_ROSE(
        id = "terracotta_rose",
        displayName = "Terracotta Rose",
        seedColor = Color(0xFF9C4146),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF904A4E),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFDAD9),
            onPrimaryContainer = Color(0xFF3B080F),
            secondary = Color(0xFF775656),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFFFDAD9),
            onSecondaryContainer = Color(0xFF2C1516),
            tertiary = Color(0xFF755A2F),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFFFDDAE),
            onTertiaryContainer = Color(0xFF281800),
            background = Color(0xFFFFF8F7),
            onBackground = Color(0xFF201A1A),
            surface = Color(0xFFFFF8F7),
            onSurface = Color(0xFF201A1A),
            surfaceVariant = Color(0xFFF5DDDC),
            onSurfaceVariant = Color(0xFF534343),
            surfaceTint = Color(0xFF904A4E),
            surfaceDim = Color(0xFFE2D7D6),
            surfaceBright = Color(0xFFFFF8F7),
            surfaceContainerLowest = Color(0xFFFFFFFF),
            surfaceContainerLow = Color(0xFFFCF1F0),
            surfaceContainer = Color(0xFFF6EBEA),
            surfaceContainerHigh = Color(0xFFF0E5E5),
            surfaceContainerHighest = Color(0xFFEAE0DF),
            outline = Color(0xFF857372),
            outlineVariant = Color(0xFFD8C2C1)
        ),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFFB3B6),
            onPrimary = Color(0xFF561D22),
            primaryContainer = Color(0xFF733337),
            onPrimaryContainer = Color(0xFFFFDAD9),
            secondary = Color(0xFFE7BDBE),
            onSecondary = Color(0xFF44292A),
            secondaryContainer = Color(0xFF5D3F3F),
            onSecondaryContainer = Color(0xFFFFDAD9),
            tertiary = Color(0xFFE5C18D),
            onTertiary = Color(0xFF422C05),
            tertiaryContainer = Color(0xFF5B421A),
            onTertiaryContainer = Color(0xFFFFDDAE),
            background = Color(0xFF1A1112),
            onBackground = Color(0xFFEDE0DF),
            surface = Color(0xFF1A1112),
            onSurface = Color(0xFFEDE0DF),
            surfaceVariant = Color(0xFF534343),
            onSurfaceVariant = Color(0xFFD8C2C1),
            surfaceTint = Color(0xFFFFB3B6),
            surfaceDim = Color(0xFF1A1112),
            surfaceBright = Color(0xFF3C3132),
            surfaceContainerLowest = Color(0xFF0F0708),
            surfaceContainerLow = Color(0xFF1A1112),
            surfaceContainer = Color(0xFF1F1516),
            surfaceContainerHigh = Color(0xFF2A1F20),
            surfaceContainerHighest = Color(0xFF35292B),
            outline = Color(0xFFA08C8C),
            outlineVariant = Color(0xFF534343)
        )
    ),
    AMBER_GOLD(
        id = "amber_gold",
        displayName = "Amber Gold",
        seedColor = Color(0xFF825500),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF825500),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFDDAE),
            onPrimaryContainer = Color(0xFF2A1800),
            secondary = Color(0xFF6F5B40),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFFADEBC),
            onSecondaryContainer = Color(0xFF271904),
            tertiary = Color(0xFF516440),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFD4EABB),
            onTertiaryContainer = Color(0xFF102004),
            background = Color(0xFFFFFBFF),
            onBackground = Color(0xFF1F1B16),
            surface = Color(0xFFFFFBFF),
            onSurface = Color(0xFF1F1B16),
            surfaceVariant = Color(0xFFF0E0CF),
            onSurfaceVariant = Color(0xFF4F4539),
            surfaceTint = Color(0xFF825500),
            surfaceDim = Color(0xFFDFD9D1),
            surfaceBright = Color(0xFFFFFBFF),
            surfaceContainerLowest = Color(0xFFFFFFFF),
            surfaceContainerLow = Color(0xFFF9F3EB),
            surfaceContainer = Color(0xFFF3EDE5),
            surfaceContainerHigh = Color(0xFFEDE7DF),
            surfaceContainerHighest = Color(0xFFE7E1D9),
            outline = Color(0xFF817567),
            outlineVariant = Color(0xFFD3C4B4)
        ),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFFB951),
            onPrimary = Color(0xFF452B00),
            primaryContainer = Color(0xFF633F00),
            onPrimaryContainer = Color(0xFFFFDDAE),
            secondary = Color(0xFFDDC2A1),
            onSecondary = Color(0xFF3D2E15),
            secondaryContainer = Color(0xFF56442A),
            onSecondaryContainer = Color(0xFFFADEBC),
            tertiary = Color(0xFFB8CEA1),
            onTertiary = Color(0xFF243516),
            tertiaryContainer = Color(0xFF3A4C2A),
            onTertiaryContainer = Color(0xFFD4EABB),
            background = Color(0xFF1F1B16),
            onBackground = Color(0xFFEAE1D9),
            surface = Color(0xFF1F1B16),
            onSurface = Color(0xFFEAE1D9),
            surfaceVariant = Color(0xFF4F4539),
            onSurfaceVariant = Color(0xFFD3C4B4),
            surfaceTint = Color(0xFFFFB951),
            surfaceDim = Color(0xFF1F1B16),
            surfaceBright = Color(0xFF413C36),
            surfaceContainerLowest = Color(0xFF14100C),
            surfaceContainerLow = Color(0xFF1F1B16),
            surfaceContainer = Color(0xFF24201A),
            surfaceContainerHigh = Color(0xFF2F2A24),
            surfaceContainerHighest = Color(0xFF3A352F),
            outline = Color(0xFF9C8F80),
            outlineVariant = Color(0xFF4F4539)
        )
    ),
    MONOCHROME(
        id = "monochrome",
        displayName = "Monochrome",
        seedColor = Color(0xFF5F6368),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF000000),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE0E0E0),
            onPrimaryContainer = Color(0xFF1F1F1F),
            secondary = Color(0xFF5F6368),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFE8EAED),
            onSecondaryContainer = Color(0xFF1F1F1F),
            tertiary = Color(0xFF3C4043),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFFDADCE0),
            onTertiaryContainer = Color(0xFF1F1F1F),
            background = Color(0xFFFFFFFF),
            onBackground = Color(0xFF1F1F1F),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF1F1F1F),
            surfaceVariant = Color(0xFFF1F3F4),
            onSurfaceVariant = Color(0xFF5F6368),
            surfaceTint = Color(0xFF5F6368),
            surfaceDim = Color(0xFFDCDCDC),
            surfaceBright = Color(0xFFF8F9FA),
            surfaceContainerLowest = Color(0xFFFFFFFF),
            surfaceContainerLow = Color(0xFFF6F6F6),
            surfaceContainer = Color(0xFFF0F1F2),
            surfaceContainerHigh = Color(0xFFEAEBED),
            surfaceContainerHighest = Color(0xFFE2E3E5),
            inverseSurface = Color(0xFF303030),
            inverseOnSurface = Color(0xFFF1F1F1),
            inversePrimary = Color(0xFFC6C6C6),
            outline = Color(0xFF80868B),
            outlineVariant = Color(0xFFDADCE0)
        ),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFFFFFF),
            onPrimary = Color(0xFF000000),
            primaryContainer = Color(0xFF3C4043),
            onPrimaryContainer = Color(0xFFE8EAED),
            secondary = Color(0xFFBDC1C6),
            onSecondary = Color(0xFF202124),
            secondaryContainer = Color(0xFF3C4043),
            onSecondaryContainer = Color(0xFFE8EAED),
            tertiary = Color(0xFFE8EAED),
            onTertiary = Color(0xFF202124),
            tertiaryContainer = Color(0xFF3C4043),
            onTertiaryContainer = Color(0xFFE8EAED),
            background = Color(0xFF121212),
            onBackground = Color(0xFFE8EAED),
            surface = Color(0xFF121212),
            onSurface = Color(0xFFE8EAED),
            surfaceVariant = Color(0xFF28292A),
            onSurfaceVariant = Color(0xFFBDC1C6),
            surfaceTint = Color(0xFFBDC1C6),
            surfaceDim = Color(0xFF121212),
            surfaceBright = Color(0xFF38393A),
            surfaceContainerLowest = Color(0xFF0D0D0E),
            surfaceContainerLow = Color(0xFF191A1B),
            surfaceContainer = Color(0xFF1E1F20),
            surfaceContainerHigh = Color(0xFF28292A),
            surfaceContainerHighest = Color(0xFF333435),
            inverseSurface = Color(0xFFE8EAED),
            inverseOnSurface = Color(0xFF1E1F20),
            inversePrimary = Color(0xFF5E5E5E),
            outline = Color(0xFF9AA0A6),
            outlineVariant = Color(0xFF3C4043)
        )
    );

    companion object {
        fun fromKey(key: String?): ThemePreset? {
            if (key == null) return null
            val clean = key.trim().lowercase().replace("-", "_").replace(" ", "_")
            return entries.firstOrNull { it.id == clean || it.name.lowercase() == clean || it.displayName.lowercase() == clean }
        }
    }
}

/**
 * Generates an algorithmic Material 3 ColorScheme from any custom seed color.
 */
fun generateDynamicColorScheme(seed: Color, darkTheme: Boolean): ColorScheme {
    val r = seed.red
    val g = seed.green
    val b = seed.blue

    return if (darkTheme) {
        val primary = Color(
            red = min(1f, r * 1.3f + 0.2f),
            green = min(1f, g * 1.3f + 0.2f),
            blue = min(1f, b * 1.3f + 0.2f)
        )
        val container = Color(red = r * 0.45f, green = g * 0.45f, blue = b * 0.45f)
        val secondary = Color(red = min(1f, b * 1.1f + 0.2f), green = min(1f, r * 1.1f + 0.2f), blue = min(1f, g * 1.1f + 0.2f))
        val secContainer = Color(red = b * 0.35f + 0.1f, green = r * 0.35f + 0.1f, blue = g * 0.35f + 0.1f)
        val tertiary = Color(red = min(1f, g * 1.1f + 0.2f), green = min(1f, b * 1.1f + 0.2f), blue = min(1f, r * 1.1f + 0.2f))
        val tertContainer = Color(red = g * 0.35f + 0.1f, green = b * 0.35f + 0.1f, blue = r * 0.35f + 0.1f)
        darkColorScheme(
            primary = primary,
            onPrimary = Color(0xFF001F0F),
            primaryContainer = container,
            onPrimaryContainer = Color(0xFFE0FFE0),
            secondary = secondary,
            onSecondary = Color(0xFF1A1A1A),
            secondaryContainer = secContainer,
            onSecondaryContainer = Color(0xFFD2E8D4),
            tertiary = tertiary,
            onTertiary = Color(0xFF02363F),
            tertiaryContainer = tertContainer,
            onTertiaryContainer = Color(0xFFBEEAF5),
            background = Color(0xFF121413),
            onBackground = Color(0xFFE1E3DF),
            surface = Color(0xFF121413),
            onSurface = Color(0xFFE1E3DF),
            surfaceVariant = Color(0xFF3B443E),
            onSurfaceVariant = Color(0xFFBEC8C1),
            surfaceTint = primary,
            surfaceDim = Color(0xFF121413),
            surfaceBright = Color(0xFF383A38),
            surfaceContainerLowest = Color(0xFF0D0F0E),
            surfaceContainerLow = Color(0xFF191C1A),
            surfaceContainer = Color(0xFF1E211F),
            surfaceContainerHigh = Color(0xFF282B29),
            surfaceContainerHighest = Color(0xFF333634),
            outline = Color(0xFF88938C),
            outlineVariant = Color(0xFF3B443E)
        )
    } else {
        val primary = Color(red = r * 0.75f, green = g * 0.75f, blue = b * 0.75f)
        val container = Color(red = min(1f, r * 0.25f + 0.75f), green = min(1f, g * 0.25f + 0.75f), blue = min(1f, b * 0.25f + 0.75f))
        val secondary = Color(red = b * 0.6f, green = r * 0.6f, blue = g * 0.6f)
        val secContainer = Color(red = min(1f, b * 0.2f + 0.8f), green = min(1f, r * 0.2f + 0.8f), blue = min(1f, g * 0.2f + 0.8f))
        val tertiary = Color(red = g * 0.6f, green = b * 0.6f, blue = r * 0.6f)
        val tertContainer = Color(red = min(1f, g * 0.2f + 0.8f), green = min(1f, b * 0.2f + 0.8f), blue = min(1f, r * 0.2f + 0.8f))
        lightColorScheme(
            primary = primary,
            onPrimary = Color.White,
            primaryContainer = container,
            onPrimaryContainer = Color(0xFF00210E),
            secondary = secondary,
            onSecondary = Color.White,
            secondaryContainer = secContainer,
            onSecondaryContainer = Color(0xFF141B15),
            tertiary = tertiary,
            onTertiary = Color.White,
            tertiaryContainer = tertContainer,
            onTertiaryContainer = Color(0xFF101C1A),
            background = Color(0xFFFCFDFB),
            onBackground = Color(0xFF191C1A),
            surface = Color(0xFFFCFDFB),
            onSurface = Color(0xFF191C1A),
            surfaceVariant = Color(0xFFDCE5DC),
            onSurfaceVariant = Color(0xFF414942),
            surfaceTint = primary,
            surfaceDim = Color(0xFFDADCDA),
            surfaceBright = Color(0xFFF9FAF8),
            surfaceContainerLowest = Color(0xFFFFFFFF),
            surfaceContainerLow = Color(0xFFF3F5F3),
            surfaceContainer = Color(0xFFEDEFED),
            surfaceContainerHigh = Color(0xFFE7E9E7),
            surfaceContainerHighest = Color(0xFFE1E3E1),
            outline = Color(0xFF717972),
            outlineVariant = Color(0xFFC0C9BF)
        )
    }
}

/**
 * Utility to parse hex colors like "#RRGGBB" or "RRGGBB"
 */
fun parseHexColor(hex: String?): Color? {
    if (hex == null) return null
    val clean = hex.trim().removePrefix("#")
    if (clean.length != 6 && clean.length != 8) return null
    return try {
        val argb = if (clean.length == 6) {
            (0xFFL shl 24) or clean.toLong(16)
        } else {
            clean.toLong(16)
        }
        Color(argb)
    } catch (_: Exception) {
        null
    }
}

@Composable
fun AndroidSnippetsTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    preset: ThemePreset = ThemePreset.MONOCHROME,
    customSeed: Color? = null,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        customSeed != null -> generateDynamicColorScheme(customSeed, darkTheme)
        darkTheme -> preset.darkColorScheme
        else -> preset.lightColorScheme
    }

    val typography = rememberGoogleSansTypography()

    MaterialTheme(
        colorScheme = colorScheme,
        typography = typography,
        content = content
    )
}
