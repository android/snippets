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

@file:OptIn(androidx.tv.material3.ExperimentalTvMaterial3Api::class)

package com.example.tv.snippets

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Card
import androidx.tv.material3.Carousel
import coil.compose.AsyncImage
import com.example.tv.R
import com.example.tv.data.Movie
import com.example.tv.data.Section

// Snippets from browse.md

// [START android_tv_compose_browse_define_catalog]
@Composable
fun CatalogBrowser(
   featuredContentList: List<Movie>,
   sectionList: List<Section>,
   modifier: Modifier = Modifier,
   onItemSelected: (Movie) -> Unit = {},
) {
// ToDo: add implementation
}
// [END android_tv_compose_browse_define_catalog]

// [START android_tv_compose_browse_lazy_column]
@Composable
fun CatalogBrowserLazyColumn(
   featuredContentList: List<Movie>,
   sectionList: List<Section>,
   modifier: Modifier = Modifier,
   onItemSelected: (Movie) -> Unit = {},
) {
  LazyColumn(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    items(sectionList) { section ->
      Section(section, onItemSelected = onItemSelected)
    }
  }
}
// [END android_tv_compose_browse_lazy_column]

// [START android_tv_compose_browse_section]
@Composable
fun Section(
  section: Section,
  modifier: Modifier = Modifier,
  onItemSelected: (Movie) -> Unit = {},
) {
  Text(
    text = section.title,
    style = MaterialTheme.typography.headlineSmall,
  )
  LazyRow(
     modifier = modifier,
     horizontalArrangement = Arrangement.spacedBy(8.dp)
  ) {
    items(section.movieList){ movie ->
    MovieCard(
         movie = movie,
         onClick = { onItemSelected(movie) }
       )
    }
  }
}
// [END android_tv_compose_browse_section]

// [START android_tv_compose_browse_movie_card]
@Composable
fun MovieCard(
   movie: Movie,
   modifier: Modifier = Modifier,
   onClick: () -> Unit = {}
) {
   Card(modifier = modifier, onClick = onClick){
    AsyncImage(
       model = movie.thumbnailUrl,
       contentDescription = movie.title,
     )
   }
}
// [END android_tv_compose_browse_movie_card]

// [START android_tv_compose_browse_carousel]
@Composable
fun FeaturedCarousel(
  featuredContentList: List<Movie>,
  modifier: Modifier = Modifier,
) {
  Carousel(
    itemCount = featuredContentList.size,
    modifier = modifier,
  ) { index ->
    val content = featuredContentList[index]
    Box {
      AsyncImage(
        model = content.backgroundImageUrl,
        contentDescription = content.description,
        placeholder = painterResource(
          id = R.drawable.placeholder
        ),
        contentScale = ContentScale.Crop,
        modifier = Modifier.fillMaxSize()
      )
      Text(text = content.title)
    }
  }
}
// [END android_tv_compose_browse_carousel]

// [START android_tv_compose_browse_full]
@Composable
fun CatalogBrowserFull(
   featuredContentList: List<Movie>,
   sectionList: List<Section>,
   modifier: Modifier = Modifier,
   onItemSelected: (Movie) -> Unit = {},
) {
  LazyColumn(
    modifier = modifier.fillMaxSize(),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {

    item {
      FeaturedCarousel(featuredContentList)
    }

    items(sectionList) { section ->
      Section(section, onItemSelected = onItemSelected)
    }
  }
}
// [END android_tv_compose_browse_full]

// Snippets from details.md

// [START android_tv_compose_details]
@Composable
fun DetailsScreen(
  movie: Movie,
  modifier: Modifier = Modifier,
  onStartPlayback: (Movie) -> Unit = {}
) {
  Box(modifier = modifier.fillMaxSize()){
     AsyncImage(
       modifier = Modifier.fillMaxSize(),
       model = movie.backgroundImageUrl,
       contentDescription = null,
       contentScale = ContentScale.Crop,
     )
     Column(modifier = Modifier.padding(32.dp)){
       Text(
          text = movie.title,
          style = MaterialTheme.typography.headlineMedium
       )
       Text(text = movie.description)
       Button(onClick = { onStartPlayback(movie) }){
         Text(text = stringResource(id = R.string.startPlayback))
       }
     }
  }
}
// [END android_tv_compose_details]
