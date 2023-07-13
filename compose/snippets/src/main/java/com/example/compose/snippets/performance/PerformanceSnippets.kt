/*
 * Copyright 2022 The Android Open Source Project
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

@file:Suppress("unused", "UNUSED_PARAMETER", "UNUSED_VARIABLE")

package com.example.compose.snippets.performance

import android.annotation.SuppressLint
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset

private object ContactsListBadSort {
    // [START android_compose_performance_contactslistbadsort]
    @Composable
    fun ContactList(
        contacts: List<Contact>,
        comparator: Comparator<Contact>,
        modifier: Modifier = Modifier
    ) {
        LazyColumn(modifier) {
            // DON’T DO THIS
            items(contacts.sortedWith(comparator)) { contact ->
                // ...
            }
        }
    }
    // [END android_compose_performance_contactslistbadsort]
}

private object ContactsListGoodSort {
    // [START android_compose_performance_contactslistgoodsort]
    @Composable
    fun ContactList(
        contacts: List<Contact>,
        comparator: Comparator<Contact>,
        modifier: Modifier = Modifier
    ) {
        val sortedContacts = remember(contacts, comparator) {
            contacts.sortedWith(comparator)
        }

        LazyColumn(modifier) {
            items(sortedContacts) {
                // ...
            }
        }
    }
    // [END android_compose_performance_contactslistgoodsort]
}

private object NotesListNoKey {
    // [START android_compose_performance_listnokey]
    @Composable
    fun NotesList(notes: List<Note>) {
        LazyColumn {
            items(
                items = notes
            ) { note ->
                NoteRow(note)
            }
        }
    }
    // [END android_compose_performance_listnokey]
}

private object NotesListKey {
    // [START android_compose_performance_listkey]
    @Composable
    fun NotesList(notes: List<Note>) {
        LazyColumn {
            items(
                items = notes,
                key = { note ->
                    // Return a stable, unique key for the note
                    note.id
                }
            ) { note ->
                NoteRow(note)
            }
        }
    }
    // [END android_compose_performance_listkey]
}

private object RememberListBad {
    @SuppressLint("FrequentlyChangedStateReadInComposition")
    @Composable
    fun RememberListStateBad() {
        // [START android_compose_performance_rememberliststatebad]
        val listState = rememberLazyListState()

        LazyColumn(state = listState) {
            // ...
        }

        val showButton = listState.firstVisibleItemIndex > 0

        AnimatedVisibility(visible = showButton) {
            ScrollToTopButton()
        }
        // [END android_compose_performance_rememberliststatebad]
    }
}

private object RememberListGood {
    @Composable
    fun RememberListGood() {
        // [START android_compose_performance_rememberliststategood]
        val listState = rememberLazyListState()

        LazyColumn(state = listState) {
            // ...
        }

        val showButton by remember {
            derivedStateOf {
                listState.firstVisibleItemIndex > 0
            }
        }

        AnimatedVisibility(visible = showButton) {
            ScrollToTopButton()
        }
        // [END android_compose_performance_rememberliststategood]
    }
}

private object DeferredReadBefore {
    // [START android_compose_performance_deferredreadbefore]
    @Composable
    fun SnackDetail() {
        // ...

        Box(Modifier.fillMaxSize()) { // Recomposition Scope Start
            val scroll = rememberScrollState(0)
            // ...
            Title(snack, scroll.value)
            // ...
        } // Recomposition Scope End
    }

    @Composable
    private fun Title(snack: Snack, scroll: Int) {
        // ...
        val offset = with(LocalDensity.current) { scroll.toDp() }

        Column(
            modifier = Modifier
                .offset(y = offset)
        ) {
            // ...
        }
    }
    // [END android_compose_performance_deferredreadbefore]
}

private object DeferredReadGood {
    // [START android_compose_performance_deferredreadafter]
    @Composable
    fun SnackDetail() {
        // ...

        Box(Modifier.fillMaxSize()) { // Recomposition Scope Start
            val scroll = rememberScrollState(0)
            // ...
            Title(snack) { scroll.value }
            // ...
        } // Recomposition Scope End
    }

    @Composable
    private fun Title(snack: Snack, scrollProvider: () -> Int) {
        // ...
        val offset = with(LocalDensity.current) { scrollProvider().toDp() }
        Column(
            modifier = Modifier
                .offset(y = offset)
        ) {
            // ...
        }
    }
    // [END android_compose_performance_deferredreadafter]
}

// [START android_compose_performance_deferredreadafter2]
@Composable
private fun Title(snack: Snack, scrollProvider: () -> Int) {
    // ...
    Column(
        modifier = Modifier
            .offset { IntOffset(x = 0, y = scrollProvider()) }
    ) {
        // ...
    }
}
// [END android_compose_performance_deferredreadafter2]

@Composable
fun AnimateColorBefore() {
    // [START android_compose_performance_animatecolorbefore]
    // Here, assume animateColorBetween() is a function that swaps between
    // two colors
    val color by animateColorBetween(Color.Cyan, Color.Magenta)

    Box(
        Modifier
            .fillMaxSize()
            .background(color)
    )
    // [END android_compose_performance_animatecolorbefore]
}

@Composable
fun AnimateColorAfter() {
// [START android_compose_performance_animatecolorafter]
    val color by animateColorBetween(Color.Cyan, Color.Magenta)
    Box(
        Modifier
            .fillMaxSize()
            .drawBehind {
                drawRect(color)
            }
    )
// [END android_compose_performance_animatecolorafter]
}

private object BackwardsWrite {
    // [START android_compose_performance_backwardswrite]
    @Composable
    fun BadComposable() {
        var count by remember { mutableStateOf(0) }

        // Causes recomposition on click
        Button(onClick = { count++ }, Modifier.wrapContentSize()) {
            Text("Recompose")
        }

        Text("$count")
        count++ // Backwards write, writing to state after it has been read</b>
    }
    // [END android_compose_performance_backwardswrite]
}

/***
 * Fakes needed for snippets to build:
 ***/

private data class Contact(val name: String) : Comparable<Contact> {
    override fun compareTo(other: Contact): Int {
        TODO("Not yet implemented")
    }
}

class Note {
    val id: Int = 0
}

class NoteRow(note: Any)

@Composable
private fun ScrollToTopButton(onClick: () -> Unit = {}) = Unit

val snack = Snack()

class Snack

@Composable
private fun animateColorBetween(color1: Color, color2: Color): State<Color> {
    return remember { mutableStateOf(color1) }
}
