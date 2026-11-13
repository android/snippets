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

package com.example.compose.snippets.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingToolbarDefaults
import androidx.compose.material3.FloatingToolbarDefaults.floatingToolbarVerticalNestedScroll
import androidx.compose.material3.FloatingToolbarExitDirection
import androidx.compose.material3.HorizontalFloatingToolbar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalFloatingToolbar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex

@Preview
// [START android_compose_expressive_components_expandablehorizontalfloatingtoolbar]
@Composable
fun ExpandableHorizontalFloatingToolbarSample() {
    var expanded by rememberSaveable { mutableStateOf(true) }
    Scaffold { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            HorizontalFloatingToolbar(
                modifier =
                    Modifier.align(Alignment.BottomCenter)
                        .offset(y = -FloatingToolbarDefaults.ScreenOffset)
                        .zIndex(1f),
                expanded = expanded,
                leadingContent = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(painter = rememberVectorPainter(AppIcons.Edit), contentDescription = "Edit")
                    }
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(painter = rememberVectorPainter(AppIcons.Favorite), contentDescription = "Favorite")
                    }
                },
                trailingContent = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(painter = rememberVectorPainter(AppIcons.Share), contentDescription = "Share")
                    }
                },
                content = {
                    FilledIconButton(
                        modifier = Modifier.width(64.dp),
                        onClick = { /* doSomething() */ },
                    ) {
                        Icon(painter = rememberVectorPainter(AppIcons.Add), contentDescription = "Add")
                    }
                },
            )
            LazyColumn(
                modifier =
                    Modifier.floatingToolbarVerticalNestedScroll(
                        expanded = expanded,
                        onExpand = { expanded = true },
                        onCollapse = { expanded = false },
                    ),
                state = rememberLazyListState(),
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val list = (0..75).map { it.toString() }
                items(count = list.size) {
                    Text(
                        text = list[it],
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    )
                }
            }
        }
    }
}
// [END android_compose_expressive_components_expandablehorizontalfloatingtoolbar]

@Preview
// [START android_compose_expressive_components_expandableverticalfloatingtoolbar]
@Composable
fun ExpandableVerticalFloatingToolbarSample() {
    var expanded by rememberSaveable { mutableStateOf(true) }
    Scaffold { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            VerticalFloatingToolbar(
                modifier =
                    Modifier.align(Alignment.CenterEnd)
                        .offset(x = -FloatingToolbarDefaults.ScreenOffset)
                        .zIndex(1f),
                expanded = expanded,
                leadingContent = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(painter = rememberVectorPainter(AppIcons.Edit), contentDescription = "Edit")
                    }
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(painter = rememberVectorPainter(AppIcons.Favorite), contentDescription = "Favorite")
                    }
                },
                trailingContent = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(painter = rememberVectorPainter(AppIcons.Share), contentDescription = "Share")
                    }
                },
                content = {
                    FilledIconButton(
                        modifier = Modifier.height(64.dp),
                        onClick = { /* doSomething() */ },
                    ) {
                        Icon(painter = rememberVectorPainter(AppIcons.Add), contentDescription = "Add")
                    }
                },
            )
            LazyColumn(
                modifier =
                    Modifier.floatingToolbarVerticalNestedScroll(
                        expanded = expanded,
                        onExpand = { expanded = true },
                        onCollapse = { expanded = false },
                    ),
                state = rememberLazyListState(),
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val list = (0..75).map { it.toString() }
                items(count = list.size) {
                    Text(
                        text = list[it],
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    )
                }
            }
        }
    }
}
// [END android_compose_expressive_components_expandableverticalfloatingtoolbar]

@Preview
// [START android_compose_expressive_components_scrollablehorizontalfloatingtoolbar]
@Composable
fun ScrollableHorizontalFloatingToolbarSample() {
    val exitAlwaysScrollBehavior =
        FloatingToolbarDefaults.exitAlwaysScrollBehavior(
            exitDirection = FloatingToolbarExitDirection.Bottom
        )
    Scaffold(modifier = Modifier.nestedScroll(exitAlwaysScrollBehavior)) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            HorizontalFloatingToolbar(
                modifier =
                    Modifier.align(Alignment.BottomCenter)
                        .offset(y = -FloatingToolbarDefaults.ScreenOffset)
                        .zIndex(1f),
                expanded = true,
                leadingContent = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(painter = rememberVectorPainter(AppIcons.Edit), contentDescription = "Edit")
                    }
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(painter = rememberVectorPainter(AppIcons.Favorite), contentDescription = "Favorite")
                    }
                },
                trailingContent = {
                    IconButton(onClick = { /* doSomething() */ }) {
                        Icon(painter = rememberVectorPainter(AppIcons.Share), contentDescription = "Share")
                    }
                },
                content = {
                    FilledIconButton(
                        modifier = Modifier.width(64.dp),
                        onClick = { /* doSomething() */ },
                    ) {
                        Icon(painter = rememberVectorPainter(AppIcons.Add), contentDescription = "Add")
                    }
                },
                scrollBehavior = exitAlwaysScrollBehavior,
            )
            LazyColumn(
                state = rememberLazyListState(),
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val list = (0..75).map { it.toString() }
                items(count = list.size) {
                    Text(
                        text = list[it],
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                    )
                }
            }
        }
    }
}
// [END android_compose_expressive_components_scrollablehorizontalfloatingtoolbar]
