package com.example.compose.snippets.adaptivelayouts

import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.text.Text


// [START android_compose_canonical_layouts_sample_my_feed]
@Composable
fun MyFeed(names: List<String>) {
    LazyVerticalGrid(
        // GridCells.Adaptive automatically adapts column count based on available width
        columns = GridCells.Adaptive(minSize = 180.dp),
    ) {
        items(names) { name ->
            Text(name)
        }
    }
}
// [END android_compose_canonical_layouts_sample_my_feed]