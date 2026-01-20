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

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Carousel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import coil.compose.AsyncImage
import com.example.tv.R
import com.example.tv.data.Movie
import com.example.tv.data.Section

class TvMoviesActivity : ComponentActivity() {
    companion object {
        val FEATURED = listOf(
            Movie(
                "The Shawshank Redemption",
                "Two imprisoned men bond over a number of years",
                "https://example.com/shawshank_poster.jpg",
                "https://example.com/shawshank_background.jpg"
            ),
            Movie(
                "Pulp Fiction",
                "The lives of two mob hitmen, a boxer, a gangster and his wife",
                "https://example.com/pulp_fiction_poster.jpg",
                "https://example.com/pulp_fiction_background.jpg"
            )
        )
        val SECTIONS = listOf(
            Section(
                "Favorites", listOf(
                    Movie(
                        "The Shawshank Redemption",
                        "Two imprisoned men bond over a number of years",
                        "https://example.com/shawshank_poster.jpg",
                        "https://example.com/shawshank_background.jpg"
                    ),
                    Movie(
                        "The Godfather",
                        "The aging patriarch of an organized crime dynasty transfers control to his son",
                        "https://example.com/godfather_poster.jpg",
                        "https://example.com/godfather_background.jpg"
                    ),
                    Movie(
                        "The Dark Knight",
                        "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham",
                        "https://example.com/dark_knight_poster.jpg",
                        "https://example.com/dark_knight_background.jpg"
                    ),
                    Movie(
                        "Pulp Fiction",
                        "The lives of two mob hitmen, a boxer, a gangster and his wife",
                        "https://example.com/pulp_fiction_poster.jpg",
                        "https://example.com/pulp_fiction_background.jpg"
                    ),
                    Movie(
                        "Fight Club",
                        "An insomniac office worker and a devil-may-care soapmaker form an underground fight club",
                        "https://example.com/fight_club_poster.jpg",
                        "https://example.com/fight_club_background.jpg"
                    ),
                )
            ),
            Section(
                "Comedy", listOf(
                    Movie(
                        "Role Models",
                        "Two salesmen have to undergo community service to mentor two boys",
                        "https://example.com/role_models_poster.jpg",
                        "https://example.com/role_models_background.jpg"
                    ),
                    Movie(
                        "Airplane! (1980)",
                        description = "A former pilot with a fear of flying find himself burdened with landing a plane safely",
                        "https://example.com/airplane_poster.jpg",
                        "https://example.com/airplane_background.jpg"
                    ),
                    Movie(
                        "Monty Python's Life of Brian",
                        "Brian inadvertently becomes the face of a revolutionary group in Roman Israel as he is hailed the prophet",
                        "https://example.com/monty_python_poster.jpg",
                        "https://example.com/monty_python_background.jpg"
                    ),
                    Movie(
                        "The Hangover",
                        "Three buddies wake up from a bachelor party",
                        "https://example.com/hangover_poster.jpg",
                        "https://example.com/hangover_background.jpg"
                    )
                )
            ),
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Scaffold(modifier = Modifier.fillMaxSize()) { padding ->
                Column {
                    FeaturedCarousel(FEATURED)
                    CatalogBrowser(SECTIONS, Modifier.padding(padding))
                }
            }
        }
    }
}

// [START android_compose_tv_catalog_browser]
@Composable
fun CatalogBrowser(
    sectionList: List<Section>,
    modifier: Modifier = Modifier,
    onItemSelected: (Movie) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(sectionList) { section ->
            SectionRow(section, onItemSelected = onItemSelected)
        }
    }
}
// [END android_compose_tv_catalog_browser]

// [START android_compose_tv_catalog_browser_section]
@Composable
fun SectionRow(
    section: Section,
    modifier: Modifier = Modifier,
    onItemSelected: (Movie) -> Unit = {},
) {
    Text(
        text = section.title,
        style = MaterialTheme.typography.headlineSmall,
    )
    MovieCatalog(
        section.movieList,
        onClick = { movie -> onItemSelected(movie) },
        modifier
    )
}
// [END android_compose_tv_catalog_browser_section]

// [START android_compose_tv_lazyrow]
@Composable
fun MovieCatalog(
    movies: List<Movie>,
    onClick: (Movie) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(movies) { movie ->
            MovieCard(
                movie = movie,
                onClick = { onClick(movie) }
            )
        }
    }
}
// [END android_compose_tv_lazyrow]

// [START android_compose_tv_movie_card]
@Composable
fun MovieCard(
    movie: Movie,
    modifier: Modifier = Modifier,
    onClick: (Movie) -> Unit = {},
) {
    Card(
        modifier = modifier,
        onClick = { onClick(movie) }) {
        AsyncImage(
            model = movie.thumbnailUrl,
            contentDescription = movie.title,
        )
    }
}
// [END android_compose_tv_movie_card]

// [START android_compose_tv_featured_carousel]
@OptIn(ExperimentalTvMaterial3Api::class)
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
                    id = R.drawable.movie_placeholder
                ),
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Text(
                text = content.title,
                style = MaterialTheme.typography.headlineMedium.copy(color = androidx.compose.ui.graphics.Color.White),
                modifier = Modifier
                    .padding(16.dp)
                    .background(androidx.compose.ui.graphics.Color.Black.copy(alpha = 0.5f))
                    .padding(8.dp)
            )
        }
    }
}
// [END android_compose_tv_featured_carousel]
