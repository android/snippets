/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.compose.snippets.touchinput.focus

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusRestorer
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class Section(val name: String, val catalog: List<CatalogItem>)

class FocusRestorationScreenViewModel : ViewModel() {

    companion object {
        private val sectionListA = listOf(
            Section("Section A", CatalogItem.createCatalog(16)),
            Section("Section B", CatalogItem.createCatalog(8, startValue = 18)),
            Section("Section C", CatalogItem.createCatalog(16, startValue = 27)),
        )

        private val sectionListB = listOf(
            Section("Section D", CatalogItem.createCatalog(8, startValue = 100)),
            Section("Section F", CatalogItem.createCatalog(16, startValue = 109)),
            Section("Section E", CatalogItem.createCatalog(8, startValue = 126)),
        )

        private val sectionSet = listOf(sectionListA, sectionListB)
    }

    private val currentSet = MutableStateFlow(0)

    val sections = currentSet.map {
        sectionSet[it]
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(), emptyList())

    fun nextPage() {
        currentSet.value = (currentSet.value + 1) % sectionSet.size
    }
}

@Composable
fun FocusRestorationScreen(
    modifier: Modifier = Modifier,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    focusRestorationScreenViewModel: FocusRestorationScreenViewModel = viewModel()
) {
    val sections by focusRestorationScreenViewModel.sections.collectAsStateWithLifecycle()
    val state = rememberLazyListState()
    val focusRequester = remember { FocusRequester() }
    val scrollToTop = remember {
        {
            coroutineScope.launch {
                state.scrollToItem(0)
                focusRequester.requestFocus()
            }
        }
    }

    CatalogWithSection(
        sections = sections,
        state = state,
        reload = {
            focusRestorationScreenViewModel.nextPage()
            scrollToTop()
        },
        scrollToTop = { scrollToTop() },
        modifier = modifier.focusRequester(focusRequester)
    )
}

@Composable
private fun CatalogWithSection(
    sections: List<Section>,
    reload: () -> Unit,
    scrollToTop: () -> Unit,
    modifier: Modifier = Modifier,
    state: LazyListState = rememberLazyListState(),
) {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(32.dp),
        state = state,
        modifier = modifier
    ) {
        items(sections) {
            CatalogSection(it)
        }
        item {
            Controls(
                reload = reload,
                scrollToTop = scrollToTop,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CatalogSection(
    section: Section,
    modifier: Modifier = Modifier,
    horizontalOffset: Dp = 16.dp,
    coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
    val bringIntoViewRequester = remember { BringIntoViewRequester() }

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
            .bringIntoViewRequester(bringIntoViewRequester)
            .onFocusChanged { focusState ->
                // Bring the Column into view port when any CatalogItemCard get focused,
                // so that users can see the section title and cards at the same time.
                if (focusState.hasFocus) {
                    coroutineScope.launch {
                        bringIntoViewRequester.bringIntoView()
                    }
                }
            },
    ) {
        Text(
            section.name,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(horizontal = horizontalOffset)
        )
        SectionCatalog(section.catalog)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SectionCatalog(
    catalog: List<CatalogItem>,
    modifier: Modifier = Modifier,
    horizontalOffset: Dp = 16.dp,
) {
// [START android_compose_touchinput_focus_restoration_manually]
    val focusRequester = remember(catalog) { FocusRequester() }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = horizontalOffset),
        modifier = modifier
            .focusRequester(focusRequester)
            .focusProperties {
                exit = {
                    focusRequester.saveFocusedChild()
                    FocusRequester.Default
                }
                enter = {
                    if (focusRequester.restoreFocusedChild()) {
                        FocusRequester.Cancel
                    } else {
                        FocusRequester.Default
                    }
                }
            }
    ) {
        items(catalog) {
            CatalogItemCard(it, modifier = Modifier.width(128.dp))
        }
    }
// [END android_compose_touchinput_focus_restoration_manually]
}

@Composable
private fun CatalogItemCard(
    catalogItem: CatalogItem,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Card(onClick = onClick, modifier = modifier.aspectRatio(9f / 16f)) {
        Text("${catalogItem.value}", modifier = Modifier.padding(16.dp))
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun Controls(
    reload: () -> Unit,
    scrollToTop: () -> Unit,
    modifier: Modifier = Modifier,
) {
    // [START android_compose_touchinput_focus_restoration_with_row]
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
            .focusRestorer()
            .focusGroup()
    ) {
        BackToTopCard(onClick = scrollToTop, modifier = Modifier.width(128.dp))
        ReloadCard(onClick = reload, modifier = Modifier.width(128.dp))
    }
    // [END android_compose_touchinput_focus_restoration_with_row]
}

@Composable
private fun ReloadCard(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    SquareCard(modifier = modifier, onClick = onClick) {
        Text("Reload")
    }
}

@Composable
private fun BackToTopCard(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    SquareCard(modifier = modifier, onClick = onClick) {
        Text("To top")
    }
}

@Composable
private fun SquareCard(
    onClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Card(onClick = onClick, modifier = modifier.aspectRatio(1f)) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
            content = content
        )
    }
}
