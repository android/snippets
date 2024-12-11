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

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

// [START android_compose_touchinput_initialfocus_basics]
@Composable
fun InitialFocusScreen(
    onCardClick: (String) -> Unit = {}
) {
    // Remember the FocusRequester object
    val initialFocus = remember { FocusRequester() }

    Row {
        Card(
            onClick = { onCardClick("Card 1") },
            // Associate the card with the FocusRequester object.
            modifier = Modifier.focusRequester(initialFocus)
        ) {
            Text("Card 1", modifier = Modifier.padding(16.dp))
        }
        Card(onClick = { onCardClick("Card 2") }) {
            Text("Card 2", modifier = Modifier.padding(16.dp))
        }
        Card(onClick = { onCardClick("Card 3") }) {
            Text("Card 3", modifier = Modifier.padding(16.dp))
        }
    }

    LaunchedEffect(Unit) {
        // Request focus on the first card.
        initialFocus.requestFocus()
    }
}
// [END android_compose_touchinput_initialfocus_basics]

class InitialFocusEnablingContentReloadViewModel(
    private val pageSize: Int = 16
) : ViewModel() {

    private val itemIndex = MutableStateFlow(0)

    val cardData = itemIndex.map {
        (it..it + pageSize).map { index ->
            "Card $index"
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun reload() {
        nextPage()
    }

    private fun nextPage() {
        itemIndex.value += pageSize
    }
}

@Composable
fun InitialFocusWithScrollableContainerScreen(
    viewModel: InitialFocusEnablingContentReloadViewModel = viewModel(),
    onCardClick: (String) -> Unit = {}
) {
    val cardData by viewModel.cardData.collectAsStateWithLifecycle()
    InitialFocusWithScrollableContainer(cardData, onCardClick)
}

// [START android_compose_touchinput_initialfocus_with_scrollable_container]
@Composable
fun InitialFocusWithScrollableContainer(
    cardData: List<String>,
    onCardClick: (String) -> Unit = {}
) {
    val initialFocus = remember { FocusRequester() }

    // Flag to determine if it is safe to set initial focus or not.
    var isSafeToSetInitialFocus by remember {
        mutableStateOf(false)
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 32.dp),
    ) {
        itemsIndexed(cardData) { index, item ->
            val cardModifier = when (index) {
                // Associate the FocusRequester object with the first card
                0 ->
                    Modifier
                        .focusRequester(initialFocus)
                        .onGloballyPositioned {
                            // Set the flag true if the element in the viewport
                            isSafeToSetInitialFocus = true
                        }

                else -> Modifier
            }
            Card(
                onClick = { onCardClick(item) },
                modifier = cardModifier
            ) {
                Text(item, modifier = Modifier.padding(16.dp))
            }
        }
    }

    // The flag is the key to trigger the coroutine to set initial focus.
    LaunchedEffect(isSafeToSetInitialFocus) {
        // Your app should set initial focus only if the UI element is in viewport
        if (isSafeToSetInitialFocus) {
            initialFocus.requestFocus()
        }
    }
}
// [END android_compose_touchinput_initialfocus_with_scrollable_container]

// [START android_compose_touchinput_initialfocus_with_content_reload]
@Composable
fun InitialFocusWithContentReloadScreen(
    viewModel: InitialFocusEnablingContentReloadViewModel = viewModel(),
    onCardClick: (String) -> Unit = {}
) {
    val cardData by viewModel.cardData.collectAsStateWithLifecycle()
    val initialFocus = remember { FocusRequester() }
    val state = rememberLazyListState()

    // Recreate the flag when the cardData value changes by reloads
    var isSafeToSetInitialFocus by remember(cardData) {
        mutableStateOf(false)
    }

    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 32.dp),
        state = state,
    ) {
        itemsIndexed(cardData) { index, item ->
            val cardModifier = when (index) {
                // Associate the FocusRequester object with the first card
                0 ->
                    Modifier
                        .focusRequester(initialFocus)
                        .onGloballyPositioned {
                            // Set the flag true if the element in the viewport
                            isSafeToSetInitialFocus = true
                        }

                else -> Modifier
            }
            Card(
                onClick = { onCardClick(item) },
                modifier = cardModifier
            ) {
                Text(item, modifier = Modifier.padding(16.dp))
            }
        }
        item {
            // Click to reload the content
            Card(onClick = viewModel::reload) {
                Text("Reload", modifier = Modifier.padding(16.dp))
            }
        }
    }

    // The flag is the key to trigger the coroutine to set initial focus.
    LaunchedEffect(isSafeToSetInitialFocus) {
        // Scroll to the first item
        state.animateScrollToItem(0)
        // Your app should set initial focus only if the UI element is in viewport
        if (isSafeToSetInitialFocus) {
            initialFocus.requestFocus()
        }
    }
}
// [END android_compose_touchinput_initialfocus_with_content_reload]
