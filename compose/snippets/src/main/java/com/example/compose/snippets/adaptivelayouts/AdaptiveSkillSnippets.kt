package com.example.compose.snippets.adaptivelayouts

import android.media.browse.MediaBrowser
import android.net.Uri
import androidx.annotation.RawRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.collections.toSet

 class CustomState(var values: Set<CustomStateValue>) {
    constructor() : this( emptySet())
}

data class CustomStateValue(var value: String) {
    companion object {
        fun valueOf(it: String): CustomStateValue {
            TODO("Not yet implemented")
        }
    }
}

// [START android_compose_custom_state_skill]

// this Saver implementation converts CustomState object which we don't know how to save
// to List<String> which we can save
private val CustomStateSaver = Saver<CustomState, List<String>>(
    save = { customState -> customState.values.map { it.value } },
    restore = { values -> CustomState(values.map { CustomStateValue.valueOf(it) }.toSet()) }
)

@Composable
fun MyComposable() {
    var customState by rememberSaveable(stateSaver = CustomStateSaver) {
        mutableStateOf(CustomState())
    }
}
// [END android_compose_custom_state_skill]


// [START android_compose_exoplayer_state_skill]
@UnstableApi
@Composable
private fun rememberExoPlayer(@RawRes videoResId: Int, fromUri: Unit.(Uri) -> MediaItem): ExoPlayer {
    val context = LocalContext.current
    var savedPositionMs by rememberSaveable(videoResId) { mutableLongStateOf(0L) }
    var savedPlayWhenReady by rememberSaveable(videoResId) { mutableStateOf(true) }

    val player = remember(videoResId) {
        ExoPlayer.Builder(context.applicationContext).build().apply {
            setMediaItem(MediaItem.fromUri(RawResourceDataSource.buildRawResourceUri(videoResId)))
            repeatMode = Player.REPEAT_MODE_ONE
            playWhenReady = savedPlayWhenReady
            seekTo(savedPositionMs)
            prepare()
        }
    }

    DisposableEffect(player) {
        val listener = object : Player.Listener {
            override fun onPlayWhenReadyChanged(playWhenReady: Boolean, reason: Int) {
                savedPlayWhenReady = playWhenReady
                savedPositionMs = player.currentPosition
            }
            override fun onPositionDiscontinuity(
                oldPosition: Player.PositionInfo,
                newPosition: Player.PositionInfo,
                reason: Int
            ) {
                savedPositionMs = newPosition.positionMs
            }
        }
        player.addListener(listener)
        onDispose {
            player.removeListener(listener)
            player.release()
        }
    }

    LaunchedEffect(player) {
        while (isActive) {
            savedPositionMs = player.currentPosition
            delay(200)
        }
    }

    LifecycleStartEffect(player) {
        if (savedPlayWhenReady) {
            player.play()
        }
        onStopOrDispose {
            val wasPlaying = player.playWhenReady
            savedPositionMs = player.currentPosition
            player.pause()
            savedPlayWhenReady = wasPlaying
        }
    }

    return player
}
// [END android_compose_exoplayer_state_skill]