package com.example.compose.snippets.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button

@Preview
@Composable
fun SearchBarExamples() {
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        var currentExample by remember { mutableStateOf<String?>(null) }

        when (currentExample) {
            "basic" -> SearchBarBasicFilterList()
            "advanced" -> AppSearchBar()
            else -> {
                Button(onClick = { currentExample = "basic" }){
                    Text("Basic search bar with filter")
                }
                Button(onClick = { currentExample = "advanced" }){
                    Text("Advanced search bar with filter")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
// [START android_compose_components_searchbarbasicfilterlist]
@Composable
fun SearchBarBasicFilterList(modifier: Modifier = Modifier) {
    var text by rememberSaveable { mutableStateOf("") }
    var expanded by rememberSaveable { mutableStateOf(false) }
    Box(
        modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = text,
                    onQueryChange = { text = it },
                    onSearch = { expanded = false },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text("Hinted search text") }
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            Column(Modifier.verticalScroll(rememberScrollState())) {
                repeat(4) { index ->
                    val resultText = "Suggestion $index"
                    ListItem(
                        headlineContent = { Text(resultText) },
                        supportingContent = { Text("Additional info") },
                        modifier = Modifier
                            .clickable {
                                text = resultText
                                expanded = false
                            }
                            .fillMaxWidth()
                    )
                }
            }
        }
    }
}
// [END android_compose_components_searchbarbasicfilterlist]

@Preview(showBackground = true)
@Composable
private fun SearchBarBasicFilterListPreview() {
    SearchBarBasicFilterList()
}

// [START android_compose_components_searchbarfilterlist]
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBarFilterList(
    list: List<String>,
    modifier: Modifier = Modifier
) {
    var text by rememberSaveable { mutableStateOf("") }
    val filteredList by remember {
        derivedStateOf {
            list.filter { it.lowercase().contains(text.lowercase()) }
        }
    }
    var expanded by rememberSaveable { mutableStateOf(false) }

    Box(
        modifier
            .fillMaxSize()
            .semantics { isTraversalGroup = true }
    ) {
        SearchBar(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .semantics { traversalIndex = 0f },
            inputField = {
                SearchBarDefaults.InputField(
                    query = text,
                    onQueryChange = { text = it },
                    onSearch = { expanded = false },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    placeholder = { Text("Hinted search text") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
                    trailingIcon = { Icon(Icons.Default.MoreVert, contentDescription = "More options") },
                )
            },
            expanded = expanded,
            onExpandedChange = { expanded = it },
        ) {
            LazyColumn {
                items(count = filteredList.size) { index ->
                    val resultText = filteredList[index]
                    ListItem(
                        headlineContent = { Text(resultText) },
                        supportingContent = { Text("Additional info") },
                        leadingContent = {
                            Icon(
                                Icons.Filled.Star,
                                contentDescription = "Starred item"
                            )
                        },
                        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
                        modifier = Modifier
                            .clickable {
                                text = resultText
                                expanded = false
                            }
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                    )
                }
            }
        }
        LazyColumn(
            contentPadding = PaddingValues(
                start = 16.dp,
                top = 72.dp,
                end = 16.dp,
                bottom = 16.dp
            ),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.semantics {
                traversalIndex = 1f
            },
        ) {
            items(count = filteredList.size) {
                Text(text = filteredList[it])
            }
        }
    }
}
// [END android_compose_components_searchbarfilterlist]

@Preview(showBackground = true)
@Composable
fun AppSearchBar(modifier: Modifier = Modifier) {
    SearchBarFilterList(
        list = listOf(
            "Cupcake",
            "Donut",
            "Eclair",
            "Froyo",
            "Gingerbread",
            "Honeycomb",
            "Ice Cream Sandwich",
            "Jelly Bean",
            "KitKat",
            "Lollipop",
            "Marshmallow",
            "Nougat",
            "Oreo",
            "Pie"
        ),
        modifier
    )
}

