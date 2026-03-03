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

package com.example.compose.snippets.designsystems

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.toUpperCase
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// [START android_compose_styles_basic_button]
@Composable
fun BasicButtonStyle() {
    Button(
        onClick = { },
        style = { }
    ) {
        Text("Click me")
    }
}
// [END android_compose_styles_basic_button]

// [START android_compose_styles_button_background]
@Composable
fun ButtonBackgroundStyle() {
    Button(
        onClick = { },
        style = { background(Color.Blue) }
    ) {
        Text("Click me")
    }
}
// [END android_compose_styles_button_background]

// [START android_compose_styles_row_styleable]
@Composable
fun RowStyleable() {
    Row(
        modifier = Modifier.styleable { }
    ) {
        Text("Content")
    }
}
// [END android_compose_styles_row_styleable]

// [START android_compose_styles_row_styleable_background]
@Composable
fun RowStyleableBackground() {
    Row(
        modifier = Modifier.styleable {
            background(Color.Blue)
        }
    ) {
        Text("Content")
    }
}
// [END android_compose_styles_row_styleable_background]

// [START android_compose_styles_standalone_style]
val style = Style { background(Color.Blue) }
// [END android_compose_styles_standalone_style]

// [START android_compose_styles_standalone_usage]
@Composable
fun StandaloneStyleUsage() {
    val style = Style { background(Color.Blue) }

    // built in parameter
    Button(onClick = { }, style = style) {
        Text("Button")
    }

    // modifier styleable
    val styleState = remember { MutableStyleState(null) }
    Column(
        Modifier.styleable(styleState, style)
    ) {
        Text("Column content")
    }
}
// [END android_compose_styles_standalone_usage]

// [START android_compose_styles_multiple_components]
@Composable
fun MultipleComponentsStyle() {
    val style = Style { background(Color.Blue) }

    // built in parameter
    Button(onClick = { }, style = style) {
        Text("Button")
    }
    Checkbox(checked = true, onCheckedChange = { }, style = style)

    // modifier styleable
    val columnStyleState = remember { MutableStyleState(null) }
    Column(
        Modifier.styleable(columnStyleState, style)
    ) {
        Text("Column")
    }
    val rowStyleState = remember { MutableStyleState(null) }
    Row(
        Modifier.styleable(rowStyleState, style)
    ) {
        Text("Row")
    }
}
// [END android_compose_styles_multiple_components]

// [START android_compose_styles_multiple_properties]
@Composable
fun MultiplePropertiesStyle() {
    Button(
        onClick = { },
        style = {
            background(Color.Blue)
            contentPaddingStart(16.dp)
        }
    ) {
        Text("Button")
    }
}
// [END android_compose_styles_multiple_properties]

val TealColor = Color(0xFF008080)

// [START android_compose_styles_overwrite_properties]
@Composable
fun OverwritePropertiesStyle() {
    Button(
        style = {
            background(Color.Red)
            // Background of Red is now overridden with TealColor instead
            background(TealColor)
            // All directions of padding are set to 64.dp (top, start, end, bottom)
            contentPadding(64.dp)
            // Top padding is now set to 16.dp, all other paddings remain at 64.dp
            contentPaddingTop(16.dp)
        },
        onClick = {
            //
        }
    ) {
        Text("Click me!")
    }
}
// [END android_compose_styles_overwrite_properties]

// [START android_compose_styles_merge_styles]
@Composable
fun MergeStyles() {
    val style1 = Style { background(TealColor) }
    val style2 = Style { contentPaddingTop(16.dp) }

    Button(
        style = style1 then style2,
        onClick = {

        },
    ) {
        Text("Click me!")
    }
}
// [END android_compose_styles_merge_styles]

// [START android_compose_styles_merge_overwrite]
@Composable
fun MergeOverwriteStyles() {
    val style1 = Style {
        background(Color.Red)
        contentPadding(32.dp)
    }

    val style2 = Style {
        contentPaddingHorizontal(8.dp)
        background(Color.LightGray)
    }

    Button(
        style = style1 then style2,
        onClick = {

        },
    ) {
        Text("Click me!")
    }
}
// [END android_compose_styles_merge_overwrite]

// [START android_compose_styles_parent_styling]
@Composable
fun ParentStyling() {
    val styleState = remember { MutableStyleState(null) }
    Column(
        modifier = Modifier.styleable(styleState) {
            background(Color.LightGray)
            val blue = Color(0xFF4285F4)
            val purple = Color(0xFFA250EA)
            val colors = listOf(blue, purple)
            contentBrush(Brush.linearGradient(colors))
        },
    ) {
        Text("Children inherit", style = { width(60.dp) })
        Text("certain properties")
        Text("from their parents")
    }
}
// [END android_compose_styles_parent_styling]

// [START android_compose_styles_child_override]
@Composable
fun ChildOverrideStyling() {
    val styleState = remember { MutableStyleState(null) }
    Column(
        modifier = Modifier.styleable(styleState) {
            background(Color.LightGray)
            val blue = Color(0xFF4285F4)
            val purple = Color(0xFFA250EA)
            val colors = listOf(blue, purple)
            contentBrush(Brush.linearGradient(colors))
        },
    ) {
        Text("Children can ", style = {
            contentBrush(Brush.linearGradient(listOf(Color.Red, Color.Blue)))
        })
        Text("override properties")
        Text("set by their parents")
    }
}
// [END android_compose_styles_child_override]

// [START android_compose_styles_custom_extension]
fun StyleScope.outlinedBackground(color: Color) {
    border(1.dp, color)
    background(color)
}
// [END android_compose_styles_custom_extension]

// [START android_compose_styles_custom_extension_usage]
val customExtensionStyle = Style {
    outlinedBackground(Color.Blue)
}
// [END android_compose_styles_custom_extension_usage]

// [START android_compose_styles_composition_local]
@Composable
fun CompositionLocalStyle() {
    val buttonStyle = Style {
        contentPadding(12.dp)
        shape(RoundedCornerShape(50))
        background(Color.Blue) // Simplified for snippet
    }
}
// [END android_compose_styles_composition_local]

// [START android_compose_styles_design_system_component]
@Composable
fun LoginButtonSnippet(modifier: Modifier = Modifier, style: Style = Style) {
    // Your custom component applying the style via the styleable modifier
    // e.g., Box(modifier = modifier.styleable(styleState, style)) 
}
// [END android_compose_styles_design_system_component]

// [START android_compose_styles_theme_integration]
@Immutable
data class AppStyles(
    val baseButtonStyle: Style = Style,
    val baseTextStyle: Style = Style,
    val baseCardStyle: Style = Style
)

val LocalAppStyles = compositionLocalOf { AppStyles() }

@Composable
fun CustomTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val styles = AppStyles()
    CompositionLocalProvider(
        LocalAppStyles provides styles,
        content = content
    )
}

@Composable
fun CustomButton(
    modifier: Modifier = Modifier,
    style: Style = Style,
    text: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember(interactionSource) { MutableStyleState(interactionSource) }

    Box(
        modifier = modifier
            .clickable(interactionSource = interactionSource, onClick = { })
            .styleable(styleState, LocalAppStyles.current.baseButtonStyle, style)
    ) {
        Text(text)
    }
}
// [END android_compose_styles_theme_integration]

// [START android_compose_styles_dos_expose_style]
@Composable
fun GradientButton(
    modifier: Modifier = Modifier,
    // ✅ DO: for design system components, expose a style modifier to consumers to be able to customize the components
    style: Style = Style
) {
    // Consume the style 
}
// [END android_compose_styles_dos_expose_style]

// [START android_compose_styles_dos_replace_params]
// Before
@Composable
fun OldButton(background: Color, fontColor: Color) {
}

// After
// ✅ DO: Replace visual-based parameters with a style that includes same properties
@Composable
fun NewButton(style: Style = Style) {
}
// [END android_compose_styles_dos_replace_params]

// [START android_compose_styles_dos_wrapper]
@Composable
fun BaseButton(
    modifier: Modifier = Modifier,
    style: Style = Style
) {
    // Uses LocalTheme.appStyles.button + incoming style
}

// ✅ Do create wrapper composables that expose common implementations of the same component
@Composable
fun SpecialGradientButton(
    modifier: Modifier = Modifier,
    style: Style = Style
) {
    // Uses LocalTheme.appStyles.button + LocalTheme.appStyles.gradientButton + incoming style - merge these styles 
}
// [END android_compose_styles_dos_wrapper]

// [START android_compose_styles_donts_default_style]
@Composable
fun BadButton(
    modifier: Modifier = Modifier,
    // ❌ DONT set a default style here as a parameter 
    style: Style = Style { background(Color.Red) }
) {
}
// [END android_compose_styles_donts_default_style]

// [START android_compose_styles_base_button]
@Composable
fun MyBaseButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: Style = Style,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit
) {
    val effectiveInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val styleState = remember(effectiveInteractionSource) {
        MutableStyleState(effectiveInteractionSource)
    }
    styleState.isEnabled = enabled
    Row(
        modifier = modifier
            .clickable(
                enabled = enabled,
                onClick = onClick,
                interactionSource = effectiveInteractionSource,
                indication = null,
            )
            .styleable(styleState, Style, style), // Assuming some base style
        content = content,
        verticalAlignment = Alignment.CenterVertically
    )
}
// [END android_compose_styles_base_button]

// [START android_compose_styles_hover_button]
@Preview
@Composable
fun Button52() {
    Box(
        modifier = Modifier.padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        MyBaseButton(
            onClick = {},
            style = Style {
                background(Color.Transparent)
                shape(RoundedCornerShape(0.dp))
                border(1.dp, Color.Black)
                contentColor(Color.Black)
                fontSize(16.sp)
                fontWeight(FontWeight.Light)
                letterSpacing(1.sp)
                contentPadding(vertical = 13.dp, horizontal = 20.dp)
                dropShadow(
                    Shadow(
                        spread = 0.dp, color = Color(0xFFFFE54C),
                        radius = 0.dp,
                        offset = DpOffset(7.dp, 7.dp)
                    )
                )
                hovered {
                    animate(tween(200)) {
                        dropShadow(
                            Shadow(
                                spread = 0.dp, color = Color(0xFFFFE54C),
                                radius = 0.dp,
                                offset = DpOffset(0.dp, 0.dp)
                            )
                        )
                    }
                }
            }
        ) {
            Text("Button 52")
        }
    }
}
// [END android_compose_styles_hover_button]

// [START android_compose_styles_rounded_depth_button]
@Preview
@Composable
fun Button74() {
    val density = LocalDensity.current
    val buttonStyle = Style {
        background(Color(0xFFFBEED0))
        border(2.dp, Color(0xFF422800))
        shape(RoundedCornerShape(30.dp))
        dropShadow(
            Shadow(
                color = Color(0xFF422800), offset = DpOffset(4.dp, 4.dp),
                radius = 0.dp, spread = 0.dp
            )
        )
        contentColor(Color(0xFF422800))
        fontWeight(FontWeight.SemiBold)
        fontSize(18.sp)
        contentPaddingHorizontal(25.dp)
        externalPadding(8.dp)
        height(50.dp)
        textAlign(TextAlign.Center)
        hovered {
            animate {
                background(Color.White)
            }
        }
        pressed {
            animate {
                dropShadow(
                    Shadow(
                        color = Color(0xFF422800),
                        offset = DpOffset(2.dp, 2.dp),
                        radius = 0.dp,
                        spread = 0.dp
                    )
                )
                translation(with(density) { 2.dp.toPx() }, with(density) { 2.dp.toPx() })
            }
        }
    }
    MyBaseButton(
        onClick = {},
        style = buttonStyle
    ) {
        Text("Button 74")
    }
}
// [END android_compose_styles_rounded_depth_button]

// [START android_compose_styles_depth_pressed_button]
@Preview
@Composable
fun Button19() {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember(interactionSource) { MutableStyleState(interactionSource) }
    val density = LocalDensity.current

    Box(
        modifier = Modifier
            .padding(100.dp)
            .windowInsetsPadding(WindowInsets.systemBars)
            .size(200.dp, 50.dp)
            .clickable(interactionSource, indication = null) {},
        contentAlignment = Alignment.Center
    ) {
        val edgeStyle = Style {
            fillSize()
            shape(RoundedCornerShape(16.dp))
            background(Color(0xFF1CB0F6))
        }

        val frontStyle = Style {
            fillSize()
            background(Color(0xFF1899D6))
            shape(RoundedCornerShape(16.dp))
            contentPadding(vertical = 13.dp, horizontal = 16.dp)
            translationY(with(density) { (-4).dp.toPx() })
            pressed {
                animate {
                    translationY(with(density) { (0).dp.toPx() })
                }
            }
        }
        Box(modifier = Modifier.styleable(styleState, edgeStyle)) {
            Box(
                modifier = Modifier
                    .styleable(styleState, frontStyle),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "Button 19".toUpperCase(Locale.current),
                    style = Style {
                        contentColor(Color.White)
                        fontSize(15.sp)
                        fontWeight(FontWeight.Bold)
                        letterSpacing(0.8.sp)
                    }
                )
            }
        }
    }
}
// [END android_compose_styles_depth_pressed_button]

// [START android_compose_styles_gradient_glow_button]
@Preview
@Composable
fun Button85() {
    val infiniteTransition = rememberInfiniteTransition(label = "glowing_button_85_animation")
    val animatedProgress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 20000, easing = LinearEasing),
        ), label = "progress"
    )

    val gradientColors = listOf(
        Color(0xffff0000), Color(0xffff7300), Color(0xfffffb00), Color(0xff48ff00),
        Color(0xff00ffd5), Color(0xff002bff), Color(0xff7a00ff), Color(0xffff00c8),
        Color(0xffff0000)
    )

    val glowingBrush = remember(animatedProgress) {
        object : ShaderBrush() {
            override fun createShader(size: androidx.compose.ui.geometry.Size): androidx.compose.ui.graphics.Shader {
                val width = size.width * 4
                val brushSize = width * animatedProgress
                return androidx.compose.ui.graphics.LinearGradientShader(
                    colors = gradientColors,
                    from = androidx.compose.ui.geometry.Offset(brushSize, 0f),
                    to = androidx.compose.ui.geometry.Offset(brushSize + width, 0f),
                    tileMode = TileMode.Repeated
                )
            }
        }
    }


    Box(
        modifier = Modifier
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        MyBaseButton(
            onClick = { },
            style = Style {
                dropShadow(
                    Shadow(
                        brush = glowingBrush,
                        radius = 5.dp
                    )
                )
                transformOrigin(TransformOrigin.Center)
                pressed {
                    animate {
                        dropShadow(
                            Shadow(
                                brush = glowingBrush,
                                radius = 10.dp
                            )
                        )
                        scale(0.95f)
                    }

                }
                size(width = 200.dp, height = 50.dp)
                background(Color(0xFF111111))
                shape(RoundedCornerShape(10.dp))
                contentColor(Color.White)
                contentPadding(vertical = (0.6f * 14).dp, horizontal = (2f * 14).dp)
                border(width = 0.dp, color = Color.Transparent)
            }
        ) {
            Text(text = "Button 85")
        }
    }
}
// [END android_compose_styles_gradient_glow_button]

// Dummy classes to satisfy the compiler/snippets if they were ever built
class Style
fun Style(block: StyleScope.() -> Unit): Style = Style()
interface StyleScope {
    fun background(color: Color)
    fun background(brush: Brush)
    fun shape(shape: RoundedCornerShape)
    fun border(width: androidx.compose.ui.unit.Dp, color: Color)
    fun contentColor(color: Color)
    fun fontSize(size: androidx.compose.ui.unit.TextUnit)
    fun fontWeight(weight: FontWeight)
    fun letterSpacing(spacing: androidx.compose.ui.unit.TextUnit)
    fun contentPadding(all: androidx.compose.ui.unit.Dp)
    fun contentPadding(vertical: androidx.compose.ui.unit.Dp, horizontal: androidx.compose.ui.unit.Dp)
    fun contentPaddingStart(padding: androidx.compose.ui.unit.Dp)
    fun contentPaddingTop(padding: androidx.compose.ui.unit.Dp)
    fun contentPaddingHorizontal(padding: androidx.compose.ui.unit.Dp)
    fun externalPadding(padding: androidx.compose.ui.unit.Dp)
    fun dropShadow(shadow: Shadow)
    fun hovered(block: StyleScope.() -> Unit)
    fun pressed(block: StyleScope.() -> Unit)
    fun animate(spec: androidx.compose.animation.core.AnimationSpec<Float> = tween(), block: StyleScope.() -> Unit)
    fun animate(block: StyleScope.() -> Unit)
    fun width(width: androidx.compose.ui.unit.Dp)
    fun height(height: androidx.compose.ui.unit.Dp)
    fun size(size: androidx.compose.ui.unit.Dp)
    fun size(width: androidx.compose.ui.unit.Dp, height: androidx.compose.ui.unit.Dp)
    fun fillSize()
    fun translation(x: Float, y: Float)
    fun translationY(y: Float)
    fun scale(scale: Float)
    fun transformOrigin(origin: TransformOrigin)
    fun textAlign(align: TextAlign)
    fun contentBrush(brush: Brush)
}
infix fun Style.then(other: Style): Style = Style()
fun Modifier.styleable(state: MutableStyleState, style: Style = Style, overrideStyle: Style = Style): Modifier = this
fun Modifier.styleable(block: StyleScope.() -> Unit): Modifier = this
class MutableStyleState(interactionSource: androidx.compose.foundation.interaction.MutableInteractionSource?) {
    var isEnabled: Boolean = true
}
class Shadow(val color: Color = Color.Unspecified, val brush: Brush? = null, val offset: DpOffset = DpOffset.Zero, val radius: androidx.compose.ui.unit.Dp = 0.dp, val spread: androidx.compose.ui.unit.Dp = 0.dp)
