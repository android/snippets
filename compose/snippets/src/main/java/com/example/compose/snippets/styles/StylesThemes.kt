@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.example.compose.snippets.styles

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.pressed
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object StylesThemes {

    @Immutable
    data class AppStyles(
        val baseButtonStyle: Style = buttonStyle,
        val baseTextStyle: Style = textStyle,
        val baseCardStyle: Style = cardStyle
        // etc
    )

    // These would live in a Styles.kt file

    private val buttonStyle = Style {
        // Styles can access CompositionLocals from other subsystems you may define in your theme.
        background(Brush.linearGradient(LocalCustomColors.currentValue.background))
        externalPadding(12.dp)
        shape(RoundedCornerShape(4.dp))
        pressed {
            background(Color.DarkGray)
            alpha(0.8f)
        }
    }

    private val textStyle = Style {
        fontSize(12.sp)
        contentColor(Color.Black)
    }
    private val cardStyle = Style {
        shape(RoundedCornerShape(16.dp))
    }

    @Composable
    fun CustomTheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        content: @Composable () -> Unit
    ) {
        val customColors = when {
            darkTheme -> CustomColors(
                content = Color.White,
                component = Color.White,
                background = listOf(Color.Black, Color.DarkGray)
            )

            else -> CustomColors(
                content = Color.Black,
                component = Color.Black,
                background = listOf(Color.White, Color.LightGray)
            )
        }
        val customTypography = CustomTypography(
            body = TextStyle(fontSize = 16.sp),
            title = TextStyle(fontSize = 32.sp)
        )

        val styles = AppStyles()
        CompositionLocalProvider(
            LocalCustomColors provides customColors,
            LocalCustomTypography provides customTypography,
            // Provide the list of styles as part of your theme, via a CompositionLocal
            LocalAppStyles provides styles,
            content = content
        )
    }

    // Use with eg. CustomTheme.styles.baseButtonStyle
    object CustomTheme {
        val colors: CustomColors
            @Composable
            get() = LocalCustomColors.current
        val typography: CustomTypography
            @Composable
            get() = LocalCustomTypography.current

        val styles: AppStyles
            @Composable
            get() = LocalAppStyles.current
        // other properties of your theme...
    }

    @Composable
    fun CustomButton(
        modifier: Modifier,
        style: Style = Style,
        text: String
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        val styleState = remember(interactionSource) { MutableStyleState(interactionSource) }

        // Apply style to top level container in combination with incoming style from parameter.
        Box(
            modifier = modifier
                .clickable(
                    interactionSource = interactionSource,
                    indication = null,
                    onClick = {

                    })
                .styleable(styleState, CustomTheme.styles.baseButtonStyle, style)
        ) {
            BaseText(text)
        }
    }

    @Immutable
    data class CustomColors(
        val content: Color,
        val component: Color,
        val background: List<Color>
    )

    @Immutable
    data class CustomTypography(
        val body: TextStyle,
        val title: TextStyle
    )

    val LocalCustomColors = staticCompositionLocalOf {
        CustomColors(
            content = Color.Unspecified,
            component = Color.Unspecified,
            background = emptyList()
        )
    }
    val LocalCustomTypography = staticCompositionLocalOf {
        CustomTypography(
            body = TextStyle.Default,
            title = TextStyle.Default
        )
    }
    val LocalAppStyles = staticCompositionLocalOf {
        AppStyles(

        )
    }
}