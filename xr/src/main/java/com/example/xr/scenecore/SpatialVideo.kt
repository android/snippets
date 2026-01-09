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
import androidx.lifecycle.lifecycleScope
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import androidx.xr.arcore.ArDevice
import androidx.xr.runtime.Session
import androidx.xr.runtime.math.FloatSize2d
import androidx.xr.runtime.math.Pose
import androidx.xr.runtime.math.Vector3
import androidx.xr.scenecore.SurfaceEntity
import androidx.xr.scenecore.Texture
import androidx.xr.scenecore.scene
import java.nio.file.Paths
import kotlinx.coroutines.launch

private fun ComponentActivity.surfaceEntityCreate(xrSession: Session) {
    // [START androidxr_scenecore_surfaceEntityCreate]
    val stereoSurfaceEntity = SurfaceEntity.create(
        session = xrSession,
        stereoMode = SurfaceEntity.StereoMode.SIDE_BY_SIDE,
        pose = Pose(Vector3(0.0f, 0.0f, -1.5f)),
        shape = SurfaceEntity.Shape.Quad(FloatSize2d(1.0f, 1.0f))
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
    val devicePose = ArDevice.getInstance(xrSession).state.value.devicePose
    val activitySpacePose = xrSession.scene.perceptionSpace.transformPoseTo(devicePose, xrSession.scene.activitySpace)

    // Set up the surface for playing a 180° video on a hemisphere.
    val hemisphereStereoSurfaceEntity =
        SurfaceEntity.create(
            session = xrSession,
            stereoMode = SurfaceEntity.StereoMode.SIDE_BY_SIDE,
            pose = activitySpacePose,
            shape = SurfaceEntity.Shape.Hemisphere(1.0f),
        )
    // ... and use the surface for playing the media.
    // [END androidxr_scenecore_surfaceEntityCreateSbs]
}

private fun ComponentActivity.surfaceEntityCreateTb(xrSession: Session) {
    // [START androidxr_scenecore_surfaceEntityCreateTb]
    val devicePose = ArDevice.getInstance(xrSession).state.value.devicePose
    val activitySpacePose = xrSession.scene.perceptionSpace.transformPoseTo(devicePose, xrSession.scene.activitySpace)
    // Set up the surface for playing a 360° video on a sphere.
    val sphereStereoSurfaceEntity =
        SurfaceEntity.create(
            session = xrSession,
            stereoMode = SurfaceEntity.StereoMode.TOP_BOTTOM,
            pose = activitySpacePose,
            shape = SurfaceEntity.Shape.Sphere(1.0f),
        )
    // ... and use the surface for playing the media.
    // [END androidxr_scenecore_surfaceEntityCreateTb]
}

private fun ComponentActivity.surfaceEntityCreateMVHEVC(xrSession: Session) {
    // [START androidxr_scenecore_surfaceEntityCreateMVHEVC]
    // Create the SurfaceEntity with the StereoMode corresponding to the MV-HEVC content
    val stereoSurfaceEntity = SurfaceEntity.create(
        session = xrSession,
        stereoMode = SurfaceEntity.StereoMode.MULTIVIEW_LEFT_PRIMARY,
        pose = Pose(Vector3(0.0f, 0.0f, -1.5f)),
        shape = SurfaceEntity.Shape.Quad(FloatSize2d(1.0f, 1.0f))
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

private fun ComponentActivity.surfaceEntityCreateDRM(xrSession: Session) {
    // [START androidxr_scenecore_surfaceEntityCreateDRM]
    // Create a SurfaceEntity with DRM content

    // Define the URI for your DRM-protected content and license server.
    val videoUri = "https://your-content-provider.com/video.mpd"
    val drmLicenseUrl = "https://your-license-server.com/license"

    // Create the SurfaceEntity with the PROTECTED content security level.
    val protectedSurfaceEntity = SurfaceEntity.create(
        session = xrSession,
        stereoMode = SurfaceEntity.StereoMode.SIDE_BY_SIDE,
        pose = Pose(Vector3(0.0f, 0.0f, -1.5f)),
        shape = SurfaceEntity.Shape.Quad(FloatSize2d(1.0f, 1.0f)),
        surfaceProtection = SurfaceEntity.SurfaceProtection.PROTECTED
    )

    // Build a MediaItem with the necessary DRM configuration.
    val mediaItem = MediaItem.Builder()
        .setUri(videoUri)
        .setDrmConfiguration(
            MediaItem.DrmConfiguration.Builder(C.WIDEVINE_UUID)
                .setLicenseUri(drmLicenseUrl)
                .build()
        )
        .build()

    // Initialize ExoPlayer and set the protected surface.
    val exoPlayer = ExoPlayer.Builder(this).build()
    exoPlayer.setVideoSurface(protectedSurfaceEntity.getSurface())

    // Set the media item and start playback.
    exoPlayer.setMediaItem(mediaItem)
    exoPlayer.prepare()
    exoPlayer.play()

    // [END androidxr_scenecore_surfaceEntityCreateDRM]
}

private fun ComponentActivity.surfaceEntityHDR(xrSession: Session) {
    // [START androidxr_scenecore_surfaceEntityHDR]
    // Define the color properties for your HDR video. These values should be specific
    // to your content.
    val hdrMetadata = SurfaceEntity.ContentColorMetadata(
        colorSpace = SurfaceEntity.ContentColorMetadata.ColorSpace.BT2020,
        colorTransfer = SurfaceEntity.ContentColorMetadata.ColorTransfer.ST2084, // PQ
        colorRange = SurfaceEntity.ContentColorMetadata.ColorRange.LIMITED,
        maxContentLightLevel = 1000 // Example: 1000 nits
    )

    // Create a SurfaceEntity, passing the HDR metadata at creation time.
    val hdrSurfaceEntity = SurfaceEntity.create(
        session = xrSession,
        stereoMode = SurfaceEntity.StereoMode.MONO,
        pose = Pose(Vector3(0.0f, 0.0f, -1.5f)),
        shape = SurfaceEntity.Shape.Quad(FloatSize2d(1.0f, 1.0f)),
    )
    hdrSurfaceEntity.contentColorMetadata = hdrMetadata

    // Initialize ExoPlayer and set the surface.
    val exoPlayer = ExoPlayer.Builder(this).build()
    exoPlayer.setVideoSurface(hdrSurfaceEntity.getSurface())

    // Define the URI for your HDR content.
    val videoUri = "https://your-content-provider.com/hdr_video.mp4"
    val mediaItem = MediaItem.fromUri(videoUri)

    // Set the media item and start playback.
    exoPlayer.setMediaItem(mediaItem)
    exoPlayer.prepare()
    exoPlayer.play()
    // [END androidxr_scenecore_surfaceEntityHDR]
}

private fun surfaceEntityEdgeFeathering(xrSession: Session) {
    // [START androidxr_scenecore_surfaceEntityEdgeFeathering]
    // Create a SurfaceEntity.
    val surfaceEntity = SurfaceEntity.create(
        session = xrSession,
        pose = Pose(Vector3(0.0f, 0.0f, -1.5f))
    )

    // Feather the edges of the surface.
    surfaceEntity.edgeFeatheringParams =
        SurfaceEntity.EdgeFeatheringParams.RectangleFeather(0.1f, 0.1f)
    // [END androidxr_scenecore_surfaceEntityEdgeFeathering]
}

private fun surfaceEntityAlphaMasking(xrSession: Session, activity: ComponentActivity) {
    // [START androidxr_scenecore_surfaceEntityAlphaMasking]
    // Create a SurfaceEntity.
    val surfaceEntity = SurfaceEntity.create(
        session = xrSession,
        pose = Pose(Vector3(0.0f, 0.0f, -1.5f))
    )

    // Load the texture in a coroutine scope.
    activity.lifecycleScope.launch {
        val alphaMaskTexture =
            Texture.create(
                xrSession,
                Paths.get("textures", "alpha_mask.png"),
            )

        // Apply the alpha mask.
        surfaceEntity.primaryAlphaMaskTexture = alphaMaskTexture

        // To remove the mask, set the property to null.
        surfaceEntity.primaryAlphaMaskTexture = null
    }
    // [END androidxr_scenecore_surfaceEntityAlphaMasking]
}
