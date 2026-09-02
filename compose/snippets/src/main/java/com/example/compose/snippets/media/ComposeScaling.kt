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

package com.example.compose.snippets.media

import android.app.Activity
import androidx.annotation.OptIn
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.derivedMediaQuery
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.ui.compose.material3.Player
import androidx.media3.ui.compose.material3.PlayerDefaults
import androidx.media3.ui.compose.material3.buttons.MuteButton

// [START android_media3_compose_scaling_non_immersive_insets]
@OptIn(UnstableApi::class)
@Composable
fun NonImmersiveVideoPlayer(
    player: Player,
    modifier: Modifier = Modifier
) {
    Player(
        player = player,
        modifier = modifier,
        showControls = true,
        // Top controls layout with safe area padding to prevent hardware notch/cutout overlap
        topControls = { activePlayer, showControls ->
            PlayerDefaults.TopControls(
                player = activePlayer,
                visible = showControls,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                MuteButton(
                    player = activePlayer,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(16.dp),
                    colors = IconButtonDefaults.filledIconButtonColors()
                )
            }
        },
        // Bottom controls layout incorporating safe area padding for navigation bars and rounded corners
        bottomControls = { activePlayer, showControls ->
            PlayerDefaults.BottomControls(
                player = activePlayer,
                visible = showControls,
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.safeDrawing)
            )
        },
        contentScale = ContentScale.Crop
    )
}
// [END android_media3_compose_scaling_non_immersive_insets]

// [START android_media3_compose_scaling_immersive_insets]
@Composable
fun ImmersiveVideoPlayer(
    player: Player,
    activity: Activity,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current

    // Manages the system UI lifecycle to create an edge-to-edge cinematic experience
    DisposableEffect(activity) {
        val window = activity.window
        val controller = WindowCompat.getInsetsController(window, view)

        // Hides status bars, navigation bars, and caption bars to maximize the video canvas
        controller.hide(
            WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.captionBar()
        )

        // Allows hidden system bars to temporarily reappear with a swipe gesture from the edge
        controller.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        onDispose {
            // Restores system UI visibility when leaving the composable
            controller.show(
                WindowInsetsCompat.Type.systemBars() or WindowInsetsCompat.Type.captionBar()
            )
        }
    }

    // Configure the Player composable with controls padded for cutouts...
}
// [END android_media3_compose_scaling_immersive_insets]

@Composable
private fun DynamicWindowSizing() {
    // [START android_media3_compose_scaling_derived_media_query]
    // Screen width is < WIDTH_DP_MEDIUM_LOWER_BOUND
    // Compact devices like portrait phones
    val narrowerThanMedium by derivedMediaQuery {
        windowWidth < WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND.dp
    }

    // Screen width is < WIDTH_DP_EXPANDED_LOWER_BOUND
    // Medium devices like foldables or small tablets
    val narrowerThanExpanded by derivedMediaQuery {
        windowWidth < WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND.dp
    }

    // Render the appropriate layout based on the screen width
    when {
        narrowerThanMedium -> SinglePaneLayout()
        narrowerThanExpanded -> TwoPaneLayout()
        else -> ThreePaneLayout()
    }
    // [END android_media3_compose_scaling_derived_media_query]
}

// [START android_media3_compose_scaling_player_fit]
@Composable
private fun MediaPlayer(
    player: Player,
    modifier: Modifier = Modifier
) {
    Player(
        player = player,
        modifier = modifier.fillMaxSize(), // Standard app window space boundaries
        contentScale = ContentScale.Fit
    )
}
// [END android_media3_compose_scaling_player_fit]

// [START android_media3_compose_scaling_dynamic_toggle]
@Composable
fun ZoomableVideoPlayer(
    player: Player,
    modifier: Modifier = Modifier
) {
    var contentScale by remember { mutableStateOf(ContentScale.Crop) }

    Player(
        player = player,
        modifier = modifier
            .fillMaxSize()
            // Handle double-tap to dynamically toggle between Crop and Fit scaling
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = {
                        contentScale = if (contentScale == ContentScale.Crop) {
                            ContentScale.Fit
                        } else {
                            ContentScale.Crop
                        }
                    }
                )
            },
        contentScale = contentScale
    )
}
// [END android_media3_compose_scaling_dynamic_toggle]

@Composable
private fun SinglePaneLayout() {}

@Composable
private fun TwoPaneLayout() {}

@Composable
private fun ThreePaneLayout() {}
