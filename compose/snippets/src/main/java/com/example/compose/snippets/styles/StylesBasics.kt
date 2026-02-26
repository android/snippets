@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.example.compose.snippets.styles

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.styleable
import androidx.compose.foundation.style.then
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.designsystems.FullyCustomDesignSystem.LocalCustomColors


// [START android_compose_styles_basics_direct_components]
@Composable
fun DirectComponents() {
    BaseButton(
        style = { },
        onClick = {
            // your click logic
        }
    ) {
        Text("Click me!")
    }
}
// [END android_compose_styles_basics_direct_components]

// [START android_compose_styles_basics_direct_components_props]
@Composable
fun DirectComponentsProps() {
    BaseButton(
        style = { background(Color.Blue) },
        onClick = {
            // your click logic
        }
    ) {
        Text("Click me!")
    }
}
// [END android_compose_styles_basics_direct_components_props]

// [START android_compose_styles_basics_modifiers]
@Composable
fun Modifiers() {
    Row(
        modifier = Modifier.styleable {

        }
    ) {

    }
}
// [END android_compose_styles_basics_modifiers]

// [START android_compose_styles_basics_modifiers_props]
@Composable
fun ModifiersProps() {
    Row(
        modifier = Modifier.styleable {
            background(Color.Blue)
        }
    ) {

    }
}
// [END android_compose_styles_basics_modifiers_props]

// [START android_compose_styles_basics_standalone]
@Composable
fun StandaloneStyle() {
    val style = Style { background(Color.Blue) }

    // built in parameter
    BaseButton(style = style, onClick = {

    }) {
        Text("Click me!")
    }

    // modifier styleable
    val styleState = remember { MutableStyleState(null) }
    Column(
        Modifier.styleable(styleState, style)
    ) {

    }
}
// [END android_compose_styles_basics_standalone]

// [START android_compose_styles_basics_standalone_multiple]
@Composable
fun StandaloneStyleMultiple() {
    val style = Style { background(Color.Blue) }

    // built in parameter
    BaseButton(style = style, onClick = {

    }) {
        Text("Click me!")
    }
    BaseCheckbox(style = style)

    // modifier styleable
    val columnStyleState = remember { MutableStyleState(null) }
    Column(
        Modifier.styleable(columnStyleState, style)
    ) {

    }
    val rowStyleState = remember { MutableStyleState(null) }
    Row(
        Modifier.styleable(rowStyleState, style)
    ) {

    }
}
// [END android_compose_styles_basics_standalone_multiple]

// [START android_compose_styles_basics_multiple_props]
@Composable
fun MultipleProps() {
    BaseButton(
        style = {
            background(Color.Blue)
            contentPaddingStart(16.dp)
        },
        onClick = {
            // your click logic
        }
    ) {
        Text("Click Me!")
    }
}

// [END android_compose_styles_basics_multiple_props]
val tealColor = Color(0xFF009688)

// [START android_compose_styles_basics_overwrite]
@Composable
fun OverwriteProps() {
    BaseButton(
        style = {
            background(Color.Red)
            // Background of Red is now overridden with TealColor instead

            background(tealColor)
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
// [END android_compose_styles_basics_overwrite]

// [START android_compose_styles_basics_merge]
@Composable
fun MergeStyles() {
    val style1 = Style { background(tealColor) }
    val style2 = Style { contentPaddingTop(16.dp) }

    BaseButton(
        style = style1 then style2,
        onClick = {

        },
    ) {
        Text("Click me!")
    }
}
// [END android_compose_styles_basics_merge]

// [START android_compose_styles_basics_merge_overwrite]
@Composable
fun MergeStylesOverwrite() {
    val style1 = Style {
        background(Color.Red)
        contentPadding(32.dp)
    }

    val style2 = Style {
        contentPaddingHorizontal(8.dp)
        background(Color.LightGray)
    }

    BaseButton(
        style = style1 then style2,
        onClick = {

        },
    ) {
        Text("Click me!")
    }
}
// [END android_compose_styles_basics_merge_overwrite]

// [START android_compose_styles_basics_parent]
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
        BaseText("Children inherit", style = { width(60.dp) })
        BaseText("certain properties")
        BaseText("from their parents")
    }
}
// [END android_compose_styles_basics_parent]

// [START android_compose_styles_basics_child]
@Composable
fun ChildOverride() {
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
        BaseText("Children can ", style = {
            contentBrush(Brush.linearGradient(listOf(Color.Red, Color.Blue)))
        })
        BaseText("override properties")
        BaseText("set by their parents")
    }
}
// [END android_compose_styles_basics_child]

// [START android_compose_styles_basics_custom_prop]
fun StyleScope.outlinedBackground(color: Color) {
    border(1.dp, color)
    background(color)
}
// [END android_compose_styles_basics_custom_prop]

// [START android_compose_styles_basics_custom_prop_apply]
val style = Style {
    outlinedBackground(Color.Blue)
}
// [END android_compose_styles_basics_custom_prop_apply]

// [START android_compose_styles_basics_composition_local]
val buttonStyle = Style {
    contentPadding(12.dp)
    shape(RoundedCornerShape(50))
    background(LocalCustomColors.currentValue.content)
}
// [END android_compose_styles_basics_composition_local]

// [START android_compose_styles_basics_design_system]
@Composable
fun LoginButton(modifier: Modifier = Modifier, style: Style = Style) {
    // Your custom component applying the style via the styleable modifier
    // e.g., Box(modifier = modifier.styleable(styleState, style))
}
// [END android_compose_styles_basics_design_system]


