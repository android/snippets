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

package com.example.xr.scenecore

import android.content.ContentResolver
import android.net.Uri
import androidx.activity.ComponentActivity
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.xr.runtime.Session
import androidx.xr.runtime.math.Pose
import androidx.xr.runtime.math.Vector3
import androidx.xr.scenecore.SurfaceEntity
import androidx.xr.scenecore.scene

private fun ComponentActivity.surfaceEntityCreate(xrSession: Session) {
    // [START androidxr_scenecore_surfaceEntityCreate]
    val stereoSurfaceEntity = SurfaceEntity.create(
        xrSession,
        SurfaceEntity.StereoMode.SIDE_BY_SIDE,
        Pose(Vector3(0.0f, 0.0f, -1.5f)),
        SurfaceEntity.CanvasShape.Quad(1.0f, 1.0f)
    )
    val videoUri = Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .path("sbs_video.mp4")
        .build()
    val mediaItem = MediaItem.fromUri(videoUri)

    val exoPlayer = ExoPlayer.Builder(this).build()
    exoPlayer.setVideoSurface(stereoSurfaceEntity.getSurface())
    exoPlayer.setMediaItem(mediaItem)
    exoPlayer.prepare()
    exoPlayer.play()
    // [END androidxr_scenecore_surfaceEntityCreate]
}

private fun ComponentActivity.surfaceEntityCreateSbs(xrSession: Session) {
    // [START androidxr_scenecore_surfaceEntityCreateSbs]
    // Set up the surface for playing a 180° video on a hemisphere.
    val hemisphereStereoSurfaceEntity =
        SurfaceEntity.create(
            xrSession,
            SurfaceEntity.StereoMode.SIDE_BY_SIDE,
            xrSession.scene.spatialUser.head?.transformPoseTo(
                Pose.Identity,
                xrSession.scene.activitySpace
            )!!,
            SurfaceEntity.CanvasShape.Vr180Hemisphere(1.0f),
        )
    // ... and use the surface for playing the media.
    // [END androidxr_scenecore_surfaceEntityCreateSbs]
}

private fun ComponentActivity.surfaceEntityCreateTb(xrSession: Session) {
    // [START androidxr_scenecore_surfaceEntityCreateTb]
    // Set up the surface for playing a 360° video on a sphere.
    val sphereStereoSurfaceEntity =
        SurfaceEntity.create(
            xrSession,
            SurfaceEntity.StereoMode.TOP_BOTTOM,
            xrSession.scene.spatialUser.head?.transformPoseTo(
                Pose.Identity,
                xrSession.scene.activitySpace
            )!!,
            SurfaceEntity.CanvasShape.Vr360Sphere(1.0f),
        )
    // ... and use the surface for playing the media.
    // [END androidxr_scenecore_surfaceEntityCreateTb]
}

private fun ComponentActivity.surfaceEntityCreateMVHEVC(xrSession: Session) {
    // [START androidxr_scenecore_surfaceEntityCreateMVHEVC]
    // Create the SurfaceEntity with the StereoMode corresponding to the MV-HEVC content
    val stereoSurfaceEntity = SurfaceEntity.create(
        xrSession,
        SurfaceEntity.StereoMode.MULTIVIEW_LEFT_PRIMARY,
        Pose(Vector3(0.0f, 0.0f, -1.5f)),
        SurfaceEntity.CanvasShape.Quad(1.0f, 1.0f)
    )
    val videoUri = Uri.Builder()
        .scheme(ContentResolver.SCHEME_ANDROID_RESOURCE)
        .path("mvhevc_video.mp4")
        .build()
    val mediaItem = MediaItem.fromUri(videoUri)

    val exoPlayer = ExoPlayer.Builder(this).build()
    exoPlayer.setVideoSurface(stereoSurfaceEntity.getSurface())
    exoPlayer.setMediaItem(mediaItem)
    exoPlayer.prepare()
    exoPlayer.play()
    // [END androidxr_scenecore_surfaceEntityCreateMVHEVC]
}
