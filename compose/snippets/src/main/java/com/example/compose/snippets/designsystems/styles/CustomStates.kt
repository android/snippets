@file:OptIn(ExperimentalFoundationStyleApi::class)

package com.example.compose.snippets.designsystems.styles

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.style.ExperimentalFoundationStyleApi
import androidx.compose.foundation.style.MutableStyleState
import androidx.compose.foundation.style.Style
import androidx.compose.foundation.style.StyleScope
import androidx.compose.foundation.style.StyleStateKey
import androidx.compose.foundation.style.styleable
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp


// [START android_compose_styles_custom_key_1]
enum class PlayerState {
    Stopped,
    Playing,
    Paused
}

val playerStateKey = StyleStateKey(PlayerState.Stopped)
// [END android_compose_styles_custom_key_1]

// [START android_compose_styles_custom_key_2]
// Extension Function on MutableStyleState to query and set the current playState
var MutableStyleState.playerState
    get() = this[playerStateKey]
    set(value) { this[playerStateKey] = value }

fun StyleScope.playerPlaying(value: Style) {
    state(playerStateKey, value, { key, state -> state[key] == PlayerState.Playing })
}
fun StyleScope.playerPaused(value: Style) {
    state(playerStateKey, value, { key, state -> state[key] == PlayerState.Paused })
}
// [END android_compose_styles_custom_key_2]

// [START android_compose_styles_link_to_custom_state]
@Composable
fun MediaPlayer(
    url: String,
    modifier: Modifier = Modifier,
    style: Style = Style,
    state: PlayerState = remember { PlayerState.Paused }
) {
    // Hoist style state, set playerState as a parameter,
    val styleState = remember { MutableStyleState(null) }
    // Set equal to incoming state to link the two together
    styleState.playerState = state
    Box(
        //..
    ) {
        ///..
    }
}
// [END android_compose_styles_link_to_custom_state]

private object Step2StyleState {
    // [START android_compose_styles_link_to_custom_state_pass]
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
            modifier = modifier.styleable(styleState, style)) {
            ///..
        }
    }
    // [END android_compose_styles_link_to_custom_state_pass]
}
private object Step3StyleState {
    // [START android_compose_styles_link_to_custom_state_key]
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
        MediaPlayer(url = "https://example.com/media/video",
            style = style,
            state = PlayerState.Stopped)
    }
    // [END android_compose_styles_link_to_custom_state_key]
}

private object Step4FullSnippetState {
    // [START android_compose_styles_state_full_snippet]
    enum class PlayerState {
        Stopped,
        Playing,
        Paused
    }
    val playerStateKey = StyleStateKey<PlayerState>(PlayerState.Stopped)
    var MutableStyleState.playerState
        get() = this[playerStateKey]
        set(value) { this[playerStateKey] = value }

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
            modifier = modifier.styleable(styleState, Style {
                size(100.dp)
                border(2.dp, Color.Red)

            }, style, )) {

            ///..
        }
    }
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
        MediaPlayer(url = "https://example.com/media/video",
            style = style,
            state = PlayerState.Stopped)
    }
    // [END android_compose_styles_state_full_snippet]
}
