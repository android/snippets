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
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.xr.compose.spatial.Subspace
import androidx.xr.compose.subspace.SpatialExternalSurface
import androidx.xr.compose.subspace.StereoMode
import androidx.xr.compose.subspace.layout.SubspaceModifier
import androidx.xr.compose.subspace.layout.height
import androidx.xr.compose.subspace.layout.width

// [START androidxr_compose_SpatialExternalSurfaceStereo]
@Composable
fun SpatialExternalSurfaceContent() {
    val context = LocalContext.current
    Subspace {
        SpatialExternalSurface(
            modifier = SubspaceModifier
                .width(1200.dp)
                .height(676.dp),
            stereoMode = StereoMode.TopBottom,
        ) {
            val exoPlayer = remember { ExoPlayer.Builder(context).build() }
            val videoUri = Uri.Builder()
                .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
                .path("sbs_video.mp4")
                .build()
            val mediaItem = MediaItem.fromUri(videoUri)

            onSurfaceCreated { surface ->
                exoPlayer.setVideoSurface(surface)
                exoPlayer.setMediaItem(mediaItem)
                exoPlayer.prepare()
                exoPlayer.play()
            }
            onSurfaceDestroyed { exoPlayer.release() }
        }
    }
}
// [END androidxr_compose_SpatialExternalSurfaceStereo]
