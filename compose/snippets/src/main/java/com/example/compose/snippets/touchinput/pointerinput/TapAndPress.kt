/*
 * Copyright 2023 The Android Open Source Project
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

@file:Suppress("unused")

package com.example.compose.snippets.touchinput.pointerinput

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.RadioButtonUnchecked
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.example.compose.snippets.R
import com.example.compose.snippets.ui.theme.SnippetsTheme
import com.example.compose.snippets.util.rememberRandomSampleImageUrl

private class Photo(
    val id: Int,
    val url: String,
    val highResUrl: String
)

@Preview
@Composable
private fun MyApp() {
    val photos = List(100) {
        val url = rememberRandomSampleImageUrl(width = 256)
        Photo(it, url, url.replace("256", "1024"))
    }
    SnippetsTheme {
        // Uncomment the sample you want to run
        ImageGrid(photos)
//        ImageGridContextMenu(photos)
//        MultiselectMode(photos)
    }
}

@Composable
private fun ClickableSurfaceSample() {
    SnippetsTheme {
        // [START android_compose_touchinput_pointerinput_onclick]
        Surface(onClick = { /* handle click */ }) {
            Text("Click me!", Modifier.padding(24.dp))
        }
        // [END android_compose_touchinput_pointerinput_onclick]
    }
}

// [START android_compose_touchinput_pointerinput_clickable]
@Composable
private fun ImageGrid(photos: List<Photo>) {
    var activePhotoId by rememberSaveable { mutableStateOf<Int?>(null) }
    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) {
        items(photos, { it.id }) { photo ->
            ImageItem(
                photo,
                // [START android_compose_touchinput_pointerinput_clickable_highlight]
                Modifier.clickable { activePhotoId = photo.id }
                // [END android_compose_touchinput_pointerinput_clickable_highlight]
            )
        }
    }
    if (activePhotoId != null) {
        FullScreenImage(
            photo = photos.first { it.id == activePhotoId },
            onDismiss = { activePhotoId = null }
        )
    }
}
// [END android_compose_touchinput_pointerinput_clickable]

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ImageGridContextMenu(photos: List<Photo>) {
    var activePhotoId by rememberSaveable { mutableStateOf<Int?>(null) }
    // [START android_compose_touchinput_pointerinput_long_clickable]
    var contextMenuPhotoId by rememberSaveable { mutableStateOf<Int?>(null) }
    val haptics = LocalHapticFeedback.current
    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) {
        items(photos, { it.id }) { photo ->
            ImageItem(
                photo,
                // [START android_compose_touchinput_pointerinput_long_clickable_highlight]
                Modifier
                    .combinedClickable(
                        onClick = { activePhotoId = photo.id },
                        onLongClick = {
                            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                            contextMenuPhotoId = photo.id
                        },
                        onLongClickLabel = stringResource(R.string.open_context_menu)
                    )
                // [END android_compose_touchinput_pointerinput_long_clickable_highlight]
            )
        }
    }
    if (contextMenuPhotoId != null) {
        PhotoActionsSheet(
            photo = photos.first { it.id == contextMenuPhotoId },
            onDismissSheet = { contextMenuPhotoId = null }
        )
    }
    // [END android_compose_touchinput_pointerinput_long_clickable]
    if (activePhotoId != null) {
        FullScreenImage(
            photo = photos.first { it.id == activePhotoId },
            onDismiss = { activePhotoId = null }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MultiselectMode(photos: List<Photo>) {
    // [START android_compose_touchinput_pointerinput_multiselect]
    var activePhotoId by rememberSaveable { mutableStateOf<Int?>(null) }
    var selectedIds by rememberSaveable { mutableStateOf(setOf<Int>()) }
    val inSelectionMode = selectedIds.isNotEmpty()

    LazyVerticalGrid(columns = GridCells.Adaptive(minSize = 128.dp)) {
        items(photos, { it.id }) { photo ->
            val selected = selectedIds.contains(photo.id)
            SelectableImageItem(
                photo = photo,
                inSelectionMode = inSelectionMode,
                selected = selected,
                Modifier
                    .combinedClickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = {
                            if (inSelectionMode) {
                                if (selected) selectedIds -= photo.id else selectedIds += photo.id
                            } else {
                                activePhotoId = photo.id
                            }
                        },
                        onLongClick = { selectedIds += photo.id },
                    )
            )
        }
    }
    if (inSelectionMode) {
        ElevatedButton(
            onClick = { selectedIds = emptySet() },
            contentPadding = PaddingValues(8.dp),
            modifier = Modifier.padding(8.dp)
        ) {
            Icon(
                Icons.Default.Close, null
            )
            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
            Text(selectedIds.count().toString())
        }
    }
    // [END android_compose_touchinput_pointerinput_multiselect]
    if (activePhotoId != null) {
        FullScreenImage(
            photo = photos.first { it.id == activePhotoId },
            onDismiss = { activePhotoId = null }
        )
    }
}

@Composable
private fun ImageItem(photo: Photo, modifier: Modifier = Modifier) {
    Image(
        painter = rememberAsyncImagePainter(model = photo.url),
        contentDescription = null,
        modifier = modifier.aspectRatio(1f)
    )
}

@Composable
private fun SelectableImageItem(
    photo: Photo,
    inSelectionMode: Boolean,
    selected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .background(MaterialTheme.colorScheme.tertiaryContainer)
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = photo.url),
            contentDescription = null,
            modifier = Modifier
                .matchParentSize()
                .then(
                    if (selected) Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(12.dp))
                    else Modifier
                )
        )
        if (inSelectionMode) {
            val icon = if (selected) Icons.Filled.CheckCircle else Icons.Filled.RadioButtonUnchecked
            val tint =
                if (selected) MaterialTheme.colorScheme.primary else Color.White.copy(alpha = 0.7f)

            Icon(icon, null, tint = tint, modifier = Modifier.padding(6.dp))
        }
    }
}

@Composable
private fun FullScreenImage(
    photo: Photo,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Scrim(onDismiss, Modifier.fillMaxSize())
        ImageWithZoom(photo, Modifier.aspectRatio(1f))
    }
}

// [START android_compose_touchinput_pointerinput_scrim]
@Composable
private fun Scrim(onClose: () -> Unit, modifier: Modifier = Modifier) {
    val strClose = stringResource(R.string.close)
    Box(
        modifier
            // handle pointer input
            // [START android_compose_touchinput_pointerinput_scrim_highlight]
            .pointerInput(onClose) { detectTapGestures { onClose() } }
            // [END android_compose_touchinput_pointerinput_scrim_highlight]
            // handle accessibility services
            .semantics(mergeDescendants = true) {
                contentDescription = strClose
                onClick {
                    onClose()
                    true
                }
            }
            // handle physical keyboard input
            .onKeyEvent {
                if (it.key == Key.Escape) {
                    onClose()
                    true
                } else {
                    false
                }
            }
            // draw scrim
            .background(Color.DarkGray.copy(alpha = 0.75f))
    )
}
// [END android_compose_touchinput_pointerinput_scrim]

@Composable
private fun ImageWithZoom(photo: Photo, modifier: Modifier = Modifier) {
    // [START android_compose_touchinput_pointerinput_double_tap_zoom]
    var zoomed by remember { mutableStateOf(false) }
    var zoomOffset by remember { mutableStateOf(Offset.Zero) }
    Image(
        painter = rememberAsyncImagePainter(model = photo.highResUrl),
        contentDescription = null,
        modifier = modifier
            // [START android_compose_touchinput_pointerinput_double_tap_zoom_highlight]
            .pointerInput(Unit) {
                detectTapGestures(
                    onDoubleTap = { tapOffset ->
                        zoomOffset = if (zoomed) Offset.Zero else
                            calculateOffset(tapOffset, size)
                        zoomed = !zoomed
                    }
                )
            }
            // [END android_compose_touchinput_pointerinput_double_tap_zoom_highlight]
            .graphicsLayer {
                scaleX = if (zoomed) 2f else 1f
                scaleY = if (zoomed) 2f else 1f
                translationX = zoomOffset.x
                translationY = zoomOffset.y
            }
    )
    // [END android_compose_touchinput_pointerinput_double_tap_zoom]
}

private fun calculateOffset(tapOffset: Offset, size: IntSize): Offset {
    val offsetX = (-(tapOffset.x - (size.width / 2f)) * 2f)
        .coerceIn(-size.width / 2f, size.width / 2f)
    return Offset(offsetX, 0f)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PhotoActionsSheet(
    @Suppress("UNUSED_PARAMETER") photo: Photo,
    onDismissSheet: () -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismissSheet
    ) {
        ListItem(
            headlineContent = { Text("Add to album") },
            leadingContent = { Icon(Icons.Default.Add, null) }
        )
        ListItem(
            headlineContent = { Text("Add to favorites") },
            leadingContent = { Icon(Icons.Default.FavoriteBorder, null) }
        )
        ListItem(
            headlineContent = { Text("Share") },
            leadingContent = { Icon(Icons.Default.Share, null) }
        )
        ListItem(
            headlineContent = { Text("Remove") },
            leadingContent = { Icon(Icons.Default.DeleteOutline, null) }
        )
    }
}
