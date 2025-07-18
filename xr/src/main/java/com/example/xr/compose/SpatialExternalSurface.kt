/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.xr.compose

import android.content.ContentResolver
import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ExperimentalComposeApi
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.SpatialExternalSurface
import androidx.xr.compose.subspace.StereoMode
import androidx.xr.compose.subspace.SurfaceProtection
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.width

// [START androidxr_compose_SpatialExternalSurfaceStereo]
@OptIn(ExperimentalComposeApi::class)
@Suppress("RestrictedApi") // b/416066566
@Composable
fun SpatialExternalSurfaceContent() {
    val context = LocalContext.current
    Subspace {
        SpatialExternalSurface(
            modifier = SubspaceModifier
                .width(1200.dp) // Default width is 400.dp if no width modifier is specified
                .height(676.dp), // Default height is 400.dp if no height modifier is specified
            // Use StereoMode.Mono, StereoMode.SideBySide, or StereoMode.TopBottom, depending
            // upon which type of content you are rendering: monoscopic content, side-by-side stereo
            // content, or top-bottom stereo content
            stereoMode = StereoMode.SideBySide,
        ) {
            val exoPlayer = remember { ExoPlayer.Builder(context).build() }
            val videoUri = Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                // Represents a side-by-side stereo video, where each frame contains a pair of
                // video frames arranged side-by-side. The frame on the left represents the left
                // eye view, and the frame on the right represents the right eye view.
                .path("sbs_video.mp4")
                .build()
            val mediaItem = MediaItem.fromUri(videoUri)

            // onSurfaceCreated is invoked only one time, when the Surface is created
            onSurfaceCreated { surface ->
                exoPlayer.setVideoSurface(surface)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.play()
            }
            // onSurfaceDestroyed is invoked when the SpatialExternalSurface composable and its
            // associated Surface are destroyed
            onSurfaceDestroyed { exoPlayer.release() }
        }
    }
}
// [END androidxr_compose_SpatialExternalSurfaceStereo]

// [START androidxr_compose_SpatialExternalSurfaceDRM]
@OptIn(ExperimentalComposeApi::class)
@Suppress("RestrictedApi") // b/416066566
@Composable
fun DrmSpatialVideoPlayer() {
    val context = LocalContext.current
    Subspace {
        SpatialExternalSurface(
            modifier = SubspaceModifier
                .width(1200.dp)
                .height(676.dp),
            stereoMode = StereoMode.SideBySide,
            surfaceProtection = SurfaceProtection.Protected
        ) {
            val exoPlayer = remember { ExoPlayer.Builder(context).build() }

            // Define the URI for your DRM-protected content and license server.
            val videoUri = "https://your-content-provider.com/video.mpd"
            val drmLicenseUrl = "https://your-license-server.com/license"

            // Build a MediaItem with the necessary DRM configuration.
            val mediaItem = MediaItem.Builder()
                .setUri(videoUri)
                .setDrmConfiguration(
                    MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                        .setLicenseUri(drmLicenseUrl)
                        .build()
                )
                .build()

            onSurfaceCreated { surface ->
                // The created surface is secure and can be used by the player.
                exoPlayer.setVideoSurface(surface)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.play()
            }

            onSurfaceDestroyed { exoPlayer.release() }
        }
    }
}
// [END androidxr_compose_SpatialExternalSurfaceDRM]