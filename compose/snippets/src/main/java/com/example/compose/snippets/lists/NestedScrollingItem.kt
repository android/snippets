package com.example.compose.snippets.lists

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

// [START android_compose_layouts_nested_scrolling]
@Composable
fun NestedScrollingRowsList(urls: List<String>) {
    LazyColumn {
        items(10) {
            LazyRow {
                item { Text("Row: $it") }
                items(urls.size) { index ->
                    // AsyncImage provided by Coil.
                    AsyncImage(
                        model = urls[index],
                        modifier = Modifier.size(150.dp),
                        contentDescription = null
                    )
                }
            }
        }
    }
}
// [END android_compose_layouts_nested_scrolling]