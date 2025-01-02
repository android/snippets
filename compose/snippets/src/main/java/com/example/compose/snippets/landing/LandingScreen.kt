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

package com.example.compose.snippets.landing

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.navigation.Destination

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LandingScreen(
    navigate: (Destination) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Text(text = "Android snippets",)
            })
        }
    ) { padding ->
        NavigationItems(modifier = Modifier.padding(padding)) { navigate(it) }
    }
}

@Composable
fun NavigationItems(
    modifier: Modifier = Modifier,
    navigate: (Destination) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        items(Destination.entries) { destination ->
            NavigationItem(destination) {
                navigate(
                    destination
                )
            }
        }
    }
}

@Composable
fun NavigationItem(destination: Destination, onClick: () -> Unit) {
    ListItem(
        headlineContent = {
            Text(destination.title)
        },
        modifier = Modifier
            .heightIn(min = 48.dp)
            .clickable {
                onClick()
            }
    )
}
