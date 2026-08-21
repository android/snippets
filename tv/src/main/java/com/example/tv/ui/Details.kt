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

package com.example.tv.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.tv.R
import com.example.tv.data.Movie

// [START android_tv_compose_details]
@Composable
fun DetailsScreen(
    movie: Movie,
    modifier: Modifier = Modifier,
    onStartPlayback: (Movie) -> Unit = {}
) {
    Box(modifier = modifier.fillMaxSize()) {
        AsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = movie.backgroundImageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        Column(modifier = Modifier.padding(32.dp)) {
            Text(
                text = movie.title,
                style = MaterialTheme.typography.headlineMedium
            )
            Text(text = movie.description)
            Button(onClick = { onStartPlayback(movie) }) {
                Text(text = stringResource(id = R.string.startPlayback))
            }
        }
    }
}
// [END android_tv_compose_details]
