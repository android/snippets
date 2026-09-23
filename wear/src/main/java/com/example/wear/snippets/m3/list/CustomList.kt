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

package com.example.wear.snippets.m3.list

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.TransformingLazyColumnState
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.CardDefaults
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.SurfaceTransformation
import androidx.wear.compose.material3.Text
import androidx.wear.compose.material3.lazy.rememberTransformationSpec
import androidx.wear.compose.material3.lazy.transformedHeight
import androidx.wear.compose.ui.tooling.preview.WearPreviewDevices
import androidx.wear.compose.ui.tooling.preview.WearPreviewFontScales
import kotlinx.coroutines.launch

// [START android_wear_list_custom_card]
@Composable
fun BoardingPassCard(
    flightNumber: String,
    origin: String,
    destination: String,
    gate: String,
    seat: String,
    departureTime: String,
    modifier: Modifier = Modifier,
    transformation: SurfaceTransformation? = null,
    shape: Shape = RoundedCornerShape(18.dp),
    statusBadge: @Composable () -> Unit = {}
) {
    // 1. Create morphing container painter
    val backgroundPainter = ColorPainter(MaterialTheme.colorScheme.surfaceContainer)
    val finalPainter = if (transformation != null) {
        remember(transformation, backgroundPainter, shape) {
            transformation.createContainerPainter(backgroundPainter, shape, border = null)
        }
    } else {
        backgroundPainter
    }

    Column(
        modifier = Modifier
            // 2a. Container layer: Scales, fades, and tilts the whole card surface
            .then(
                if (transformation != null) {
                    Modifier.graphicsLayer {
                        transformation.run { applyContainerTransformation() }
                    }
                } else Modifier
            )
            // 2b. Caller modifier: Includes Modifier.transformedHeight in a list
            .then(modifier)
            .fillMaxWidth()
            // 2c. Shape clip: Only needed without a transformation, because the
            // painter from createContainerPainter clips itself to the shape
            .then(if (transformation == null) Modifier.clip(shape) else Modifier)
            // 2d. Morphing background: Drawn inside the transformed container layer
            .drawBehind {
                with(finalPainter) {
                    draw(size)
                }
            }
            // 2e. Content layer: Fades content earlier and clips children to shape
            .then(
                if (transformation != null) {
                    Modifier.graphicsLayer {
                        this.shape = shape
                        this.clip = true
                        transformation.run { applyContentTransformation() }
                    }
                } else Modifier
            )
            .padding(horizontal = 14.dp, vertical = 10.dp)
    ) {
        // Card content goes here
        // [START_EXCLUDE silent]
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = flightNumber,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            statusBadge()
        }
        Spacer(modifier = Modifier.height(4.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = origin,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = " -> ",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 6.dp)
            )
            Text(
                text = destination,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "GATE $gate", style = MaterialTheme.typography.labelSmall)
            Text(text = "SEAT $seat", style = MaterialTheme.typography.labelSmall)
            Text(text = departureTime, style = MaterialTheme.typography.labelSmall)
        }
        // [END_EXCLUDE]
    }
}
// [END android_wear_list_custom_card]

// [START android_wear_list_custom_usage]
@Composable
fun BoardingPassListSample(flights: List<FlightInfo>) {
    val listState = rememberTransformingLazyColumnState()
    val transformationSpec = rememberTransformationSpec()

    // [START_EXCLUDE silent]
    ScrollBroadcastReceiver(listState)
    // [END_EXCLUDE]

    ScreenScaffold(scrollState = listState) { contentPadding ->
        TransformingLazyColumn(
            state = listState,
            contentPadding = contentPadding,
            modifier = Modifier.fillMaxSize()
        ) {
            items(flights.size) { index ->
                val flight = flights[index]
                BoardingPassCard(
                    flightNumber = flight.number,
                    origin = flight.origin,
                    destination = flight.destination,
                    gate = flight.gate,
                    seat = flight.seat,
                    departureTime = flight.time,
                    modifier = Modifier
                        .transformedHeight(this, transformationSpec)
                        .minimumVerticalContentPadding(
                            CardDefaults.minimumVerticalListContentPadding
                        ),
                    transformation = SurfaceTransformation(transformationSpec)
                )
            }
        }
    }
}
// [END android_wear_list_custom_usage]

data class FlightInfo(
    val number: String,
    val origin: String,
    val destination: String,
    val gate: String,
    val seat: String,
    val time: String,
)

@WearPreviewDevices
@WearPreviewFontScales
@Composable
fun BoardingPassListSamplePreview() {
    BoardingPassListSample(
        flights = listOf(
            FlightInfo("UA 124", "SFO", "JFK", "G4", "12A", "10:45"),
            FlightInfo("BA 287", "LHR", "SFO", "B12", "4F", "14:20"),
            FlightInfo("NH 107", "HND", "SFO", "I9", "21C", "18:05"),
            FlightInfo("LH 455", "SFO", "FRA", "G9", "8K", "19:30"),
            FlightInfo("QF 74", "SFO", "SYD", "G3", "15B", "22:15"),
        )
    )
}

const val ACTION_SCROLL = "com.example.wear.ACTION_SCROLL"

/**
 * Listens for the [ACTION_SCROLL] broadcast intent to trigger a deterministic scroll animation
 * down and back up on [listState].
 *
 * ### Purpose
 * This receiver is used to record the scrolling video (`compose-tlc-custom.mp4`) shown in the
 * "Custom composables in lists" section on developer.android.com:
 * - Page: https://developer.android.com/training/wearables/compose/lists?version=3#custom-composables-in-lists
 * - DevSite asset path: `/images/wear/compose-tlc-custom.mp4`
 *
 * It is excluded from public documentation snippets via `[START_EXCLUDE silent]`.
 *
 * ### How to record the video
 * 1. Deploy [BoardingPassListSamplePreview] (or an Activity hosting [BoardingPassListSample]) to a
 *    Wear OS device or emulator.
 * 2. Run the accompanying `./record_wear.sh` script located in this directory, which starts
 *    `adb shell screenrecord`, broadcasts `com.example.wear.ACTION_SCROLL`, waits for the 10.5s
 *    scroll animation to finish, and compresses the resulting MP4 with `ffmpeg` for DevSite.
 */
@Composable
fun ScrollBroadcastReceiver(listState: TransformingLazyColumnState) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val receiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (intent?.action == ACTION_SCROLL) {
                    coroutineScope.launch {
                        // Smoothly scroll down by 400 pixels over 5250ms (5.25s)
                        listState.animateScrollBy(
                            value = 400f,
                            animationSpec = tween(
                                durationMillis = 5250,
                                easing = FastOutSlowInEasing
                            )
                        )
                        // And smoothly scroll back up over 5250ms (total ~10.5s)
                        listState.animateScrollBy(
                            value = -400f,
                            animationSpec = tween(
                                durationMillis = 5250,
                                easing = FastOutSlowInEasing
                            )
                        )
                    }
                }
            }
        }

        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> {
                    ContextCompat.registerReceiver(
                        context,
                        receiver,
                        IntentFilter(ACTION_SCROLL),
                        ContextCompat.RECEIVER_EXPORTED
                    )
                }
                Lifecycle.Event.ON_PAUSE -> {
                    context.unregisterReceiver(receiver)
                }
                else -> {}
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            // Catch any edge cases where onDispose hits before ON_PAUSE
            try {
                context.unregisterReceiver(receiver)
            } catch (_: IllegalArgumentException) {
                // Receiver was already unregistered
            }
        }
    }
}
