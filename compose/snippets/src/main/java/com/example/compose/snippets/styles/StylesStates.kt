@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.example.compose.snippets.styles

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.StyleStateKey
import androidx.compose.foundation.style.focused
import androidx.compose.foundation.style.hovered
import androidx.compose.foundation.style.pressed
import androidx.compose.foundation.style.styleable
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// [START android_compose_styles_states_interaction]
@Composable
private fun OpenButton() {
    BaseButton(
        style = {
            shape(RoundedCornerShape(0.dp))
            background(Color.White)
            hovered {
                val lightPurple = Color(0xFFD1C4E9)
                background(lightPurple)
            }
            focused {
                val lightBlue = Color(0xFF81D4FA)
                background(lightBlue)
            }
        },
        onClick = { },
        content = {
            BaseText("Open in Studio", style = {
                contentColor(Color.Black)
                fontSize(26.sp)
                textAlign(TextAlign.Center)
            })
        }
    )
}
// [END android_compose_styles_states_interaction]

// [START android_compose_styles_states_nested]
@Composable
private fun OpenButtonNested() {
    BaseButton(
        style = {
            shape(RoundedCornerShape(0.dp))
            background(Color.White)
            hovered {
                // light purple
                val lightPurple = Color(0xFFD1C4E9)
                background(lightPurple)
                pressed {
                    // When running on a device that can hover, whilst hovering and then pressing the button this would be invoked
                    val lightOrange = Color(0xFFFFE0B2)
                    background(lightOrange)
                }
            }
            pressed {
                // when running on a device without a mouse attached, this would be invoked as you wouldn't be in a hovered state only
                val lightRed = Color(0xFF66E7DC)
                background(lightRed)
            }
            focused {
                val lightBlue = Color(0xFF81D4FA)
                background(lightBlue)
            }
        },
        onClick = { },
        content = {
            BaseText("Open in Studio", style = {
                contentColor(Color.Black)
                fontSize(26.sp)
                textAlign(TextAlign.Center)
            })
        }
    )
}
// [END android_compose_styles_states_nested]

val baseGradientButtonStyle = Style {
    background(
        Brush.linearGradient(
            listOf(
                Color(0xFFD1C4E9),
                Color(0xFFFFE0B2),
                Color(0xFF81D4FA)
            )
        )
    )
    shape(RoundedCornerShape(8.dp))
}

// [START android_compose_styles_states_custom_composable]
@Composable
private fun GradientButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: Style = Style,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource? = null,
    content: @Composable RowScope.() -> Unit,
) {
    val interactionSource = interactionSource ?: remember { MutableInteractionSource() }
    val styleState = remember(interactionSource) { MutableStyleState(interactionSource) }
    styleState.isEnabled = enabled
    Row(
        modifier =
            modifier
                .clickable(
                    onClick = onClick,
                    enabled = enabled,
                    interactionSource = interactionSource,
                    indication = null,
                )
                .styleable(styleState, baseGradientButtonStyle, style),
        content = content,
    )
}

@Composable
fun LoginButton() {
    val loginButtonStyle = Style {
        externalPadding(8.dp)
        pressed {
            background(
                Brush.linearGradient(
                    listOf(Color.Magenta, Color.Red)
                )
            )
        }
    }
    GradientButton(onClick = {
        // Login logic
    }, style = loginButtonStyle) {
        Text("Login")
    }
}
// [END android_compose_styles_states_custom_composable]

// [START android_compose_styles_states_animating]
val animatingStyle = Style {
    border(3.dp, Color.Black)
    background(Color.White)
    size(100.dp)

    pressed {
        animate {
            borderColor(Color.Magenta)
            background(Color(0xFFB39DDB))
        }
    }
}

@Preview
@Composable
fun AnimatingStyleChanges() {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember(interactionSource) { MutableStyleState(interactionSource) }
    Box(
        modifier = Modifier
            .clickable(
                interactionSource,
                enabled = true,
                indication = null,
                onClick = {

                }
            )
            .focusable(true, interactionSource)
            .styleable(styleState, animatingStyle))
}
// [END android_compose_styles_states_animating]

// [START android_compose_styles_states_animating_spring]
val animatingStyleSpring = Style {
    externalPadding(8.dp)
    border(3.dp, Color.Black)
    background(Color.White)
    size(100.dp)

    pressed {
        animate {
            borderColor(Color.Magenta)
            background(Color(0xFFB39DDB))
        }
        animate(spring(dampingRatio = Spring.DampingRatioMediumBouncy)) {
            size(120.dp)
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimatingStyleChangesSpring() {
    val interactionSource = remember { MutableInteractionSource() }
    val styleState = remember(interactionSource) { MutableStyleState(interactionSource) }
    Box(
        modifier = Modifier
            .clickable(
                interactionSource,
                enabled = true,
                indication = null,
                onClick = {

                }
            )
            .focusable(true, interactionSource)
            .styleable(styleState, animatingStyleSpring))
}
// [END android_compose_styles_states_animating_spring]

// [START android_compose_styles_states_custom_state]
enum class PlayerState {
    Stopped,
    Playing,
    Paused
}

val playerStateKey = StyleStateKey<PlayerState>(PlayerState.Stopped)
var MutableStyleState.playerState
    get() = this[playerStateKey]
    set(value) {
        this[playerStateKey] = value
    }

fun StyleScope.playerPlaying(value: Style) {
    state(playerStateKey, value, { key, state -> state[key] == PlayerState.Playing })
}

fun StyleScope.playerPaused(value: Style) {
    state(playerStateKey, value, { key, state -> state[key] == PlayerState.Paused })

}

@Composable
fun MediaPlayer(
    url: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    state: PlayerState = remember { PlayerState.Paused }
) {
    // Hoist style state, set playstate as a parameter,
    val styleState = remember { MutableStyleState(null) }
    // Set equal to incoming state to link the two together
    styleState.playerState = state
    Box(
        modifier = modifier.styleable(
            styleState,
            Style {
                size(100.dp)
                border(2.dp, Color.Black)

            },
            style,
        )
    ) {

        ///..
    }
}

@Preview
@Composable
fun StyleStateKeySample() {
    // Using the extension function to change the border color to green while playing
    val style = Style {
        borderColor(Color.Gray)
        playerPlaying {
            animate {
                borderColor(Color.Green)
            }
        }
        playerPaused {
            animate {
                borderColor(Color.Blue)
            }
        }
    }
    val styleState = remember { MutableStyleState(null) }
    styleState[playerStateKey] = PlayerState.Playing

    // Using the style in a composable that sets the state -> notice if you change the state parameter, the style changes. You can link this up to an ViewModel and change the state from there too. 
    MediaPlayer(
        url = "https://example.com/media/video",
        style = style,
        state = PlayerState.Stopped
    )
}
// [END android_compose_styles_states_custom_state]
