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

package com.example.compose.preview.generator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.dp
import com.example.compose.preview.wasm.registry.SnippetRegistry
import com.github.takahirom.roborazzi.captureRoboImage
import java.io.File
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

private val PreviewColorScheme = lightColorScheme(
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
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], qualifiers = "w480dp-h270dp-xxxhdpi")
class PreviewScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun getOutputDir(): File {
        var dir: File? = File(".").canonicalFile
        while (dir != null && !File(dir, "settings.gradle.kts").exists()) {
            dir = dir.parentFile
        }
        val root = dir ?: File(".")
        val screenshotsDir = File(root, "preview/wasm/src/wasmJsMain/resources/screenshots")
        screenshotsDir.mkdirs()
        return screenshotsDir
    }

    @Test
    fun captureAllRegisteredSnippets() {
        val outputDir = getOutputDir()
        var currentEntry by mutableStateOf<Pair<String, @Composable () -> Unit>?>(null)

        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            MaterialTheme(colorScheme = PreviewColorScheme) {
                Box(
                    modifier = Modifier
                        .size(480.dp, 270.dp)
                        .background(Color(0xFFFFFFFF)),
                    contentAlignment = Alignment.Center
                ) {
                    currentEntry?.let { (id, composable) ->
                        key(id) {
                            composable()
                        }
                    }
                }
            }
        }

        SnippetRegistry.snippets.forEach { (id, composable) ->
            composeTestRule.runOnIdle {
                currentEntry = id to composable
            }
            composeTestRule.mainClock.advanceTimeBy(500)
            composeTestRule.onRoot().captureRoboImage(
                filePath = File(outputDir, "$id.png").absolutePath
            )
        }
    }
}
