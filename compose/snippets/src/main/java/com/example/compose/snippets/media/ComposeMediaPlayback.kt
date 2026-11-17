package com.example.compose.snippets.media

import android.app.Application
import android.content.ComponentName
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.session.MediaController
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import androidx.media3.session.SessionToken
import androidx.media3.ui.compose.material3.Player
import com.google.common.util.concurrent.ListenableFuture

class PlayerViewModel(application: Application) : AndroidViewModel(application) {
    // Instantiate ExoPlayer using applicationContext to prevent Activity leaks
    val player: ExoPlayer = ExoPlayer.Builder(application).build()
    fun prepare(mediaUri: Uri) {
        val mediaItem = MediaItem.fromUri(mediaUri)
        player.setMediaItem(mediaItem)
        player.prepare()
    }

    fun onStart() {
        player.play()
    }

    fun onStop() {
        player.pause()
    }

    // Unified release: Called automatically when the ViewModel is destroyed
    override fun onCleared() {
        super.onCleared()
        player.release()
    }
}

@Composable
fun VideoPlayerScreen(
    mediaUri: Uri,
    modifier: Modifier = Modifier,
    viewModel: PlayerViewModel = viewModel()
) {
    // 1. Prepare media item when the URI changes
    LaunchedEffect(mediaUri) {
        viewModel.prepare(mediaUri)
    }
    // 2. Coordinate foreground/background playback lifecycle
    LifecycleStartEffect(viewModel) {
        viewModel.onStart()
        onStopOrDispose {
            viewModel.onStop()
        }
    }

    // 3. Render video surface and controls
    Player(
        modifier = modifier.fillMaxSize(),
        player = viewModel.player
    )
}

class PlaybackService : MediaSessionService() {
    private var mediaSession: MediaSession? = null
    override fun onCreate() {
        super.onCreate()
        val player = ExoPlayer.Builder(this).build()
        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? =
        mediaSession

    // Release resources when the background service is permanently destroyed
    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
            mediaSession = null
        }
        super.onDestroy()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player
        if (player?.playWhenReady != true || player.playbackState == ExoPlayer.STATE_ENDED) {
            stopSelf()
        }
    }
}

class PlaybackControlViewModel(application: Application) : AndroidViewModel(application) {
    private var controllerFuture: ListenableFuture<MediaController>? = null
    var mediaController by mutableStateOf<MediaController?>(null)
        private set

    fun connect(serviceClass: Class<*>) {
        if (controllerFuture != null) return
        val context = getApplication<Application>()
        val sessionToken = SessionToken(context, ComponentName(context, serviceClass))
        val future = MediaController.Builder(context, sessionToken).buildAsync()
        future.addListener(
            {
                mediaController = future.get()
            },
            ContextCompat.getMainExecutor(context)
        )
        controllerFuture = future
    }

    fun disconnect() {
        controllerFuture?.let { MediaController.releaseFuture(it) }
        controllerFuture = null
        mediaController = null
    }

    override fun onCleared() {
        super.onCleared()
        disconnect()
    }
}

@Composable
fun PlaybackControlScreen(
    modifier: Modifier = Modifier,
    viewModel: PlaybackControlViewModel = viewModel()
) {
    // Connect to the service on ON_START, disconnect on ON_STOP
    LifecycleStartEffect(viewModel) {
        viewModel.connect(PlaybackService::class.java)
        onStopOrDispose {
            viewModel.disconnect()
        }
    }
    viewModel.mediaController?.let { controller ->
        PlayerControls(
            isPlaying = controller.isPlaying,
            onPlay = { controller.play() },
            onPause = { controller.pause() },
            onNext = { controller.seekToNextMediaItem() },
            onPrevious = { controller.seekToPreviousMediaItem() }
        )
    }
}

@Composable
fun PlayerControls(
    isPlaying: Boolean,
    onPlay: () -> Unit,
    onPause: () -> Unit,
    onNext: () -> Unit,
    onPrevious: () -> Unit
) {
    // Custom Player UI
}
