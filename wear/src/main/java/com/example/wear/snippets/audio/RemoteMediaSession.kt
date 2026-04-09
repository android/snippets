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

package com.example.wear.snippets.audio

import android.app.Application
import android.media.session.MediaSession
import android.media.session.MediaSessionManager
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import java.util.concurrent.Executor
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.guava.await

class RemoteMediaActivityViewModel(application: Application) : AndroidViewModel(application) {
    private val mediaSessionManager = application.getSystemService(MediaSessionManager::class.java)
    private val mainExecutor: Executor = ContextCompat.getMainExecutor(application)

    @RequiresApi(Build.VERSION_CODES.CUR_DEVELOPMENT)
    private val sessionFlow: Flow<List<MediaSession.Token>> = callbackFlow {
        // [START android_wear_remote_media_session_listener]
        val callback =
            MediaSessionManager.OnActiveSessionsChangedListener { controllers ->
                val tokens = controllers?.map { it.sessionToken } ?: emptyList()
                trySendBlocking(tokens)
            }
        // [END android_wear_remote_media_session_listener]
        // [START android_wear_remote_media_session_register_listener]
        mediaSessionManager.addOnActiveSessionsForPackageChangedListener(
            application.packageName,
            mainExecutor,
            callback,
        )
        // [END android_wear_remote_media_session_register_listener]
        // [START android_wear_remote_media_session_get_sessions]
        trySendBlocking(mediaSessionManager.getActiveSessionsForPackage(application.packageName))
        // [END android_wear_remote_media_session_get_sessions]
        // [START android_wear_remote_media_session_remove_listener]
        awaitClose { mediaSessionManager.removeOnActiveSessionsForPackageChangedListener(callback) }
        // [END android_wear_remote_media_session_remove_listener]
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @RequiresApi(37)
    // [START android_wear_remote_media_session_media_controller]
    private val controllerFlow: Flow<MediaController?> =
        sessionFlow
            .distinctUntilChanged()
            .flatMapLatest { tokens ->
                val token = tokens.firstOrNull() ?: return@flatMapLatest kotlinx.coroutines.flow.flowOf(null)
                callbackFlow {
                    val sessionToken = SessionToken.createSessionToken(application, token).await()
                    val controller = MediaController.Builder(application, sessionToken).buildAsync().await()
                    val listener = object : Player.Listener {
                        override fun onEvents(player: Player, events: Player.Events) {
                            trySendBlocking(controller)
                        }
                    }
                    controller.addListener(listener)
                    trySendBlocking(controller)
                    awaitClose {
                        controller.removeListener(listener)
                        controller.release()
                    }
                }
            }
    // [END android_wear_remote_media_session_media_controller]

    // [START android_wear_remote_media_session_listen_events]
    private val MediaController.controllerEventFlow: Flow<Unit>
        get() =
            callbackFlow<Unit> {
                val listener =
                    object : Player.Listener {
                        override fun onEvents(player: Player, events: Player.Events) {
                            trySendBlocking(Unit)
                        }
                    }
                this@controllerEventFlow.addListener(listener)
                awaitClose { this@controllerEventFlow.removeListener(listener) }
            }

    // [END android_wear_remote_media_session_listen_events]
}
