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

package com.example.compose.snippets.layouts

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.R

// [START android_compose_layout_basics_1]
@Composable
fun ArtistCard() {
    Text("Alfred Sisley")
    Text("3 minutes ago")
}
// [END android_compose_layout_basics_1]

// [START android_compose_layout_basics_2]
@Composable
fun ArtistCardColumn() {
    Column {
        Text("Alfred Sisley")
        Text("3 minutes ago")
    }
}
// [END android_compose_layout_basics_2]

// [START android_compose_layout_basics_3]
@Composable
fun ArtistCardRow(artist: Artist) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(bitmap = artist.image, contentDescription = "Artist image")
        Column {
            Text(artist.name)
            Text(artist.lastSeenOnline)
        }
    }
}

// [START_EXCLUDE silent]
class Artist {
    val image: ImageBitmap = ImageBitmap(0, 0)
    val name = ""
    val lastSeenOnline = ""
}
// [END_EXCLUDE]
// [END android_compose_layout_basics_3]

// [START android_compose_layout_basics_4]
@Composable
fun ArtistAvatar(artist: Artist) {
    Box {
        Image(bitmap = artist.image, contentDescription = "Artist image")
        Icon(Icons.Filled.Check, contentDescription = "Check mark")
    }
}
// [END android_compose_layout_basics_4]

// [START android_compose_layout_basics_5]
@Composable
fun ArtistCardArrangement(artist: Artist) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.End
    ) {
        Image(bitmap = artist.image, contentDescription = "Artist image")
        Column { /*...*/ }
    }
}
// [END android_compose_layout_basics_5]

// [START android_compose_layout_basics_6]
@Composable
fun SearchResult() {
    Row {
        Image(
            // [START_EXCLUDE]
            painter = painterResource(id = R.drawable.dog), contentDescription = ""
            // [END_EXCLUDE]
        )
        Column {
            Text(
                // [START_EXCLUDE]
                "Hello"
                // [END_EXCLUDE]
            )
            Text(
                // [START_EXCLUDE]
                "World"
                // [END_EXCLUDE]
            )
        }
    }
}
// [END android_compose_layout_basics_6]

// [START android_compose_layout_basics_7]
@Composable
fun ArtistCardModifiers(
    artist: Artist,
    onClick: () -> Unit
) {
    val padding = 16.dp
    Column(
        Modifier
            .clickable(onClick = onClick)
            .padding(padding)
            .fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) { /*...*/ }
        Spacer(Modifier.size(padding))
        Card(
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        ) { /*...*/ }
    }
}
// [END android_compose_layout_basics_7]

// [START android_compose_layout_basics_8]
@Composable
fun WithConstraintsComposable() {
    BoxWithConstraints {
        Text("My minHeight is $minHeight while my maxWidth is $maxWidth")
    }
}
// [END android_compose_layout_basics_8]

// [START android_compose_layout_basics_9]
@Composable
fun HomeScreen(/*...*/) {
    ModalNavigationDrawer(drawerContent = { /* ... */ }) {
        Scaffold(
            topBar = { /*...*/ }
        ) { contentPadding ->
            // [START_EXCLUDE]
            Box(Modifier.padding(contentPadding)) { }
            // [END_EXCLUDE]
        }
    }
}
// [END android_compose_layout_basics_9]
