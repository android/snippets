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

package com.example.media

import android.content.Context
import android.os.Bundle
import android.provider.MediaStore
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.session.MediaSessionCompat
import android.util.Log
import androidx.media.MediaBrowserServiceCompat

private const val TAG = "AssistantSnippets"
private const val MEDIA_ID_EMPTY_ROOT = "@empty@"

private fun setSessionFlags(session: MediaSessionCompat) {
    // [START android_media_assistant_set_flags]
    session.setFlags(
        MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS or
            MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS
    )
    // [END android_media_assistant_set_flags]
}

private class SearchMediaSessionCallback : MediaSessionCompat.Callback() {
    private var isArtistFocus = false
    private var isAlbumFocus = false
    private var artist: String? = null
    private var album: String? = null

    // [START android_media_assistant_on_play_from_search]
    override fun onPlayFromSearch(query: String?, extras: Bundle?) {
        if (query.isNullOrEmpty()) {
            // The user provided generic string e.g. 'Play music'
            // Build appropriate playlist queue
        } else {
            // Build a queue based on songs that match "query" or "extras" param
            val mediaFocus: String? = extras?.getString(MediaStore.EXTRA_MEDIA_FOCUS)
            if (mediaFocus == MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE) {
                isArtistFocus = true
                artist = extras.getString(MediaStore.EXTRA_MEDIA_ARTIST)
            } else if (mediaFocus == MediaStore.Audio.Albums.ENTRY_CONTENT_TYPE) {
                isAlbumFocus = true
                album = extras.getString(MediaStore.EXTRA_MEDIA_ALBUM)
            }

            // Implement additional "extras" param filtering
        }

        // Implement your logic to retrieve the queue
        var result: String? = when {
            isArtistFocus -> artist?.also {
                searchMusicByArtist(it)
            }
            isAlbumFocus -> album?.also {
                searchMusicByAlbum(it)
            }
            else -> null
        }
        result = result ?: run {
            // No focus found, search by query for song title
            query?.also {
                searchMusicBySongTitle(it)
            }
        }

        if (result?.isNotEmpty() == true) {
            // Immediately start playing from the beginning of the search results
            // Implement your logic to start playing music
            playMusic(result)
        } else {
            // Handle no queue found. Stop playing if the app
            // is currently playing a song
        }
    }
    // [END android_media_assistant_on_play_from_search]

    private fun searchMusicByArtist(artist: String): String = artist
    private fun searchMusicByAlbum(album: String): String = album
    private fun searchMusicBySongTitle(title: String): String = title
    private fun playMusic(track: String) {}
}

private class MyMediaBrowserService : MediaBrowserServiceCompat() {
    private val packageValidator = PackageValidator()

    // [START android_media_assistant_on_get_root]
    override fun onGetRoot(
        clientPackageName: String,
        clientUid: Int,
        rootHints: Bundle?
    ): BrowserRoot? {

        // To ensure you are not allowing any arbitrary app to browse your app's contents, you
        // need to check the origin:
        if (!packageValidator.isCallerAllowed(this, clientPackageName, clientUid)) {
            // If the request comes from an untrusted package, return an empty browser root.
            // If you return null, then the media browser will not be able to connect and
            // no further calls will be made to other media browsing methods.
            Log.i(
                TAG, "OnGetRoot: Browsing NOT ALLOWED for unknown caller. Returning empty "
                    + "browser root so all apps can use MediaController. $clientPackageName"
            )
            return MediaBrowserServiceCompat.BrowserRoot(MEDIA_ID_EMPTY_ROOT, null)
        }

        // Return browser roots for browsing...
        // [START_EXCLUDE silent]
        return null
        // [END_EXCLUDE]
    }
    // [END android_media_assistant_on_get_root]

    override fun onLoadChildren(
        parentId: String,
        result: MediaBrowserServiceCompat.Result<MutableList<MediaBrowserCompat.MediaItem>>
    ) {
        result.sendResult(null)
    }
}

private class PackageValidator {
    fun isCallerAllowed(service: Context, packageName: String, uid: Int): Boolean = true
}
