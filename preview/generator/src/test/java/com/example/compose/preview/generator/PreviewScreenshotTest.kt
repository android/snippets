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

package com.example.compose.preview.generator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import com.example.compose.preview.wasm.registry.DefaultPreviewFrame
import com.example.compose.preview.wasm.registry.PreviewFontWeights
import com.example.compose.preview.wasm.registry.SnippetRegistry
import com.example.compose.preview.wasm.registry.previewFrameFor
import com.example.compose.preview.wasm.registry.previewTypography
import com.example.compose.snippets.R
import com.github.takahirom.roborazzi.ExperimentalRoborazziApi
import com.github.takahirom.roborazzi.RoboVideoOptions
import com.github.takahirom.roborazzi.captureRoboImage
import com.github.takahirom.roborazzi.recordRoboVideo
import java.io.File
import org.junit.Assume.assumeTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.RuleChain
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runner.RunWith
import org.junit.runners.model.Statement
import org.robolectric.ParameterizedRobolectricTestRunner
import org.robolectric.RuntimeEnvironment
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

internal const val PreviewRobolectricSdk = 36

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

/**
 * Roboto Flex, loaded from the same variable font file the WASM runner fetches, with one entry
 * per weight pinned to its `wght` axis value. See [previewTypography].
 */
@OptIn(ExperimentalTextApi::class)
internal val PreviewFontFamily = FontFamily(
    PreviewFontWeights.map { weight ->
        Font(
            resId = R.font.robotoflex_variable,
            weight = weight,
            variationSettings = FontVariation.Settings(FontVariation.weight(weight.weight)),
        )
    }
)

/**
 * Sets the Robolectric device size before the compose rule launches its activity, so each snippet
 * (including dialogs, which size themselves against the window) lays out in its PreviewFrame.
 */
private class DeviceQualifiersRule(private val qualifiers: String) : TestRule {
    override fun apply(base: Statement, description: Description): Statement = object : Statement() {
        override fun evaluate() {
            RuntimeEnvironment.setQualifiers(qualifiers)
            base.evaluate()
        }
    }
}

/**
 * Captures `<region_tag>.png` for every snippet in [SnippetRegistry] (and a `<region_tag>.gif`
 * recording, encoded to MP4 afterwards, for the ones in [SnippetRecordings]), one test per
 * snippet, on a
 * device sized to the snippet's PreviewFrame at xhdpi (so the default 384x216dp frame is 768x432).
 */
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@RunWith(ParameterizedRobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [PreviewRobolectricSdk])
class PreviewScreenshotTest(private val snippetId: String) {

    private val frame = previewFrameFor(snippetId)
    private val orientation = if (frame.widthDp >= frame.heightDp) "land" else "port"
    private val composeTestRule = createComposeRule()

    @get:Rule
    val rules: RuleChain = RuleChain
        .outerRule(DeviceQualifiersRule("w${frame.widthDp}dp-h${frame.heightDp}dp-$orientation-xhdpi"))
        .around(composeTestRule)

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

    private fun setSnippetContent() {
        val snippet = SnippetRegistry.snippets.getValue(snippetId)
        composeTestRule.mainClock.autoAdvance = false
        composeTestRule.setContent {
            MaterialExpressiveTheme(
                colorScheme = PreviewColorScheme,
                typography = previewTypography(PreviewFontFamily),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize().background(Color(0xFFFFFFFF)),
                    contentAlignment = Alignment.Center,
                ) {
                    snippet()
                }
            }
        }
    }

    @Test
    fun capture() {
        setSnippetContent()
        // Let entry animations, async fonts/images and lazy lists settle before capturing.
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.mainClock.advanceTimeBy(1_000)
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.onRoot().captureRoboImage(
            filePath = File(getOutputDir(), "$snippetId.png").absolutePath,
        )
    }

    /**
     * Records `<region_tag>.gif` for snippets in [SnippetRecordings] by playing their interaction
     * script. `encodePreviewVideos` (which runs after `recordRoborazziDebug`) converts it to
     * `<region_tag>.mp4`, the static preview DAC shows for animation snippets.
     */
    @OptIn(ExperimentalRoborazziApi::class)
    @Test
    fun record() {
        val recording = SnippetRecordings[snippetId]
        assumeTrue("$snippetId has no recording", recording != null)
        checkNotNull(recording)
        setSnippetContent()
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.onRoot().recordRoboVideo(
            composeTestRule,
            File(getOutputDir(), "$snippetId.gif").absolutePath,
            RoboVideoOptions(
                fps = 25,
                // Stop when the script ends instead of recording until the UI settles (3 s by
                // default), so infinite transitions loop cleanly after exactly one cycle.
                settleTimeoutMillis = 0L,
                backgroundColor = 0xFFFFFFFF.toInt(),
            ),
        ) {
            recording.steps.forEach { step ->
                when (step) {
                    is PreviewStep.Wait -> delay(step.millis)
                    is PreviewStep.Click -> clickTarget(step.text).performClick()
                    is PreviewStep.Press -> {
                        val target = clickTarget(null)
                        target.performTouchInput { down(center) }
                        delay(step.millis)
                        target.performTouchInput { up() }
                    }
                }
            }
        }
    }

    /**
     * The node showing [text], or the snippet's only clickable node when [text] is null. Fails if
     * there's more than one clickable node, rather than recording a GIF that doesn't animate.
     */
    private fun clickTarget(text: String?): SemanticsNodeInteraction =
        if (text != null) {
            composeTestRule.onNodeWithText(text)
        } else {
            composeTestRule.onNode(hasClickAction())
        }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun snippetIds(): List<Array<Any>> {
            // Recordings and frames are keyed separately from the registry, so catch typos or
            // renamed region tags here instead of silently skipping a GIF (a 404 on DAC).
            val unregistered = SnippetRecordings.keys - SnippetRegistry.snippets.keys
            require(unregistered.isEmpty()) {
                "SnippetRecordings has IDs that aren't in SnippetRegistry: $unregistered"
            }
            val withoutFrame = SnippetRecordings.keys.filter { previewFrameFor(it) == DefaultPreviewFrame }
            require(withoutFrame.isEmpty()) {
                "Recorded snippets need a frame in SnippetPreviewFrames: $withoutFrame"
            }
            return SnippetRegistry.snippets.keys.map { arrayOf(it) }
        }
    }
}
