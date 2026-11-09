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
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.onRoot
import org.robolectric.Shadows.shadowOf
import android.os.Looper
import com.github.takahirom.roborazzi.captureScreenRoboImage
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isToggleable
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.text.ExperimentalTextApi
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontVariation
import com.example.compose.preview.wasm.registry.PreviewFontWeights
import com.example.compose.preview.wasm.registry.SnippetRegistry
import com.example.compose.preview.wasm.registry.previewFrameFor
import com.example.compose.preview.wasm.registry.previewTypography
import com.example.compose.snippets.R
import com.github.takahirom.roborazzi.captureRoboImage
import java.io.File
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
 * Captures `<region_tag>.png` for every snippet in [SnippetRegistry], one test per snippet, on a
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

    @Test
    fun capture() {
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
        // Let entry animations, async fonts/images and lazy lists settle before capturing.
        composeTestRule.mainClock.advanceTimeByFrame()
        composeTestRule.mainClock.advanceTimeBy(1_000)
        composeTestRule.mainClock.advanceTimeByFrame()
        val filePath = File(getOutputDir(), "$snippetId.png").absolutePath
        val interaction = PreviewInteractions[snippetId]
            ?: PreviewTapTargets[snippetId]?.let { t -> { clickOn(t) } }
        if (interaction == null) {
            composeTestRule.onRoot().captureRoboImage(filePath = filePath)
        } else {
            composeTestRule.interaction()
            // Let the popup window attach (Robolectric runs the main looper in paused mode).
            repeat(3) {
                composeTestRule.mainClock.advanceTimeByFrame()
                shadowOf(Looper.getMainLooper()).idle()
            }
            composeTestRule.mainClock.advanceTimeBy(1_000)
            composeTestRule.mainClock.advanceTimeByFrame()
            // Capture every window, so popups such as an open menu are included.
            captureScreenRoboImage(filePath = filePath)
        }
    }

    companion object {
        @JvmStatic
        @ParameterizedRobolectricTestRunner.Parameters(name = "{0}")
        fun snippetIds(): List<Array<Any>> {
            // Tap targets are keyed separately from the registry, so catch typos or renamed region
            // tags here instead of silently capturing the untapped state.
            val unregistered =
                PreviewTapTargets.keys + PreviewInteractions.keys - SnippetRegistry.snippets.keys
            require(unregistered.isEmpty()) {
                "PreviewTapTargets has IDs that aren't in SnippetRegistry: $unregistered"
            }
            return SnippetRegistry.snippets.keys.map { arrayOf(it) }
        }
    }
}

/**
 * Snippets whose static preview shows the result of tapping a control, keyed by region tag. The
 * value is the control's text or content description. Use it when the snippet's starting state
 * doesn't show the component, for example a menu that opens from an icon button. The live preview
 * still starts in the snippet's starting state.
 */
private val PreviewTapTargets = mapOf(
    "android_compose_components_minimaldropdownmenu" to "More options",
    "android_compose_components_longbasicdropdownmenu" to "More options",
    "android_compose_components_dropdownmenuwithdetails" to "More options",
    "android_compose_components_indeterminateindicator" to "Start loading",
)

/**
 * Snippets whose static preview needs more than one tap (or another gesture) before capture, keyed
 * by region tag. Runs instead of [PreviewTapTargets], before the shared settle and capture.
 */
private val PreviewInteractions: Map<String, ComposeContentTestRule.() -> Unit> = mapOf(
    // Show the selected (filled) state of the toggle.
    "android_compose_components_togglebuttonexample" to { clickOn("Unselected icon button.") },
    // Show a non-zero count instead of "advanced by 0 frames".
    "android_compose_components_momentaryiconbuttons" to {
        repeat(5) { clickOn("Increase count button") }
    },
    // A multi-select button with two options picked, as in M3 and the DAC figure.
    "android_compose_components_multichoicesegmentedbutton" to {
        clickOn("Directions Walk")
        clickOn("Directions Bus")
    },
    // Show the selected state with its leading check icon.
    "android_compose_components_filterchip" to { clickOn("Filter chip") },
    // Check "Option 1" (the second checkbox, after "Select all") so the parent shows the
    // indeterminate state. The child checkboxes have no text of their own to tap.
    "android_compose_components_checkbox_parent" to {
        onAllNodes(isToggleable())[1].performSemanticsAction(SemanticsActions.OnClick)
    },
)

private fun ComposeContentTestRule.clickOn(label: String) {
    onNode(hasText(label) or hasContentDescription(label))
        .performSemanticsAction(SemanticsActions.OnClick)
    mainClock.advanceTimeByFrame()
}
