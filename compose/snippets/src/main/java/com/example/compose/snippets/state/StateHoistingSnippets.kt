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

@file:Suppress("unused", "UNUSED_PARAMETER")

package com.example.compose.snippets.state

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.text.AnnotatedString
import androidx.lifecycle.ViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private object StateHoistingSnippets1 {
    // [START android_compose_state_hoisting_no_hoisting]
    @Composable
    fun ChatBubble(
        message: Message
    ) {
        var showDetails by rememberSaveable { mutableStateOf(false) } // Define the UI element expanded state

        ClickableText(
            text = AnnotatedString(message.content),
            onClick = { showDetails = !showDetails } // Apply simple UI logic
        )

        if (showDetails) {
            Text(message.timestamp)
        }
    }
    // [END android_compose_state_hoisting_no_hoisting]
}

private object StateHoistingSnippets2 {
    var messages = listOf<Message>()
    @Composable
    fun UserInput(onMessageSent: () -> Unit) {
    }
    @Composable
    fun JumpToBottom(onClicked: () -> Unit) {
    }

    // [START android_compose_state_hoisting_composables]
    @Composable
    private fun ConversationScreen(/*...*/) {
        val scope = rememberCoroutineScope()

        val lazyListState = rememberLazyListState() // State hoisted to the ConversationScreen

        MessagesList(messages, lazyListState) // Reuse same state in MessageList

        UserInput(
            onMessageSent = { // Apply UI logic to lazyListState
                scope.launch {
                    lazyListState.scrollToItem(0)
                }
            },
        )
    }

    @Composable
    private fun MessagesList(
        messages: List<Message>,
        lazyListState: LazyListState = rememberLazyListState() // LazyListState has a default value
    ) {

        LazyColumn(
            state = lazyListState // Pass hoisted state to LazyColumn
        ) {
            items(messages, key = { message -> message.id }) { item ->
                Message(/*...*/)
            }
        }

        val scope = rememberCoroutineScope()

        JumpToBottom(onClicked = {
            scope.launch {
                lazyListState.scrollToItem(0) // UI logic being applied to lazyListState
            }
        })
    }
    // [END android_compose_state_hoisting_composables]
}

private object StateHoistingSnippets3 {
    // [START android_compose_state_hoisting_plain_class]
    // LazyListState.kt

    @Stable
    class LazyListState constructor(
        firstVisibleItemIndex: Int = 0,
        firstVisibleItemScrollOffset: Int = 0
    ) : ScrollableState {
        /**
         *   The holder class for the current scroll position.
         */
        private val scrollPosition = LazyListScrollPosition(
            firstVisibleItemIndex, firstVisibleItemScrollOffset
        )

        suspend fun scrollToItem(/*...*/) { /*...*/ }

        override suspend fun scroll() { /*...*/ }

        suspend fun animateScrollToItem() { /*...*/ }
    }
    // [END android_compose_state_hoisting_plain_class]
}

private object StateHoistingSnippets4 {
    @Composable
    private fun MessagesList(messages: List<Message>, onSendMessage: (Message) -> Unit) {
    }

    // [START android_compose_state_hoisting_vm]
    class ConversationViewModel(
        channelId: String,
        messagesRepository: MessagesRepository
    ) : ViewModel() {

        val messages = messagesRepository
            .getLatestMessages(channelId)
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = emptyList()
            )

        // Business logic
        fun sendMessage(message: Message) { /* ... */ }
    }
    // [END android_compose_state_hoisting_vm]

    // [START android_compose_state_hoisting_vm_usage]
    @Composable
    private fun ConversationScreen(
        conversationViewModel: ConversationViewModel = viewModel()
    ) {

        val messages by conversationViewModel.messages.collectAsStateWithLifecycle()

        ConversationScreen(
            messages = messages,
            onSendMessage = { message: Message -> conversationViewModel.sendMessage(message) }
        )
    }

    @Composable
    private fun ConversationScreen(
        messages: List<Message>,
        onSendMessage: (Message) -> Unit
    ) {

        MessagesList(messages, onSendMessage)
        /* ... */
    }
    // [END android_compose_state_hoisting_vm_usage]
}

@OptIn(ExperimentalCoroutinesApi::class)
private object StateHoistingSnippets5 {
    class Suggestion
    class Repository {
        fun getSuggestions(s: String) = listOf<Suggestion>()
    }
    val repository = Repository()
    private fun getHandle(s: String) = ""
    private fun hasSocialHandleHint(s: String) = true

    // [START android_compose_state_hoisting_vm_ui_element_state]
    class ConversationViewModel(/*...*/) : ViewModel() {

        // Hoisted state
        var inputMessage by mutableStateOf("")
            private set

        val suggestions: StateFlow<List<Suggestion>> =
            snapshotFlow { inputMessage }
                .filter { hasSocialHandleHint(it) }
                .mapLatest { getHandle(it) }
                .mapLatest { repository.getSuggestions(it) }
                .stateIn(
                    scope = viewModelScope,
                    started = SharingStarted.WhileSubscribed(5_000),
                    initialValue = emptyList()
                )

        fun updateInput(newInput: String) {
            inputMessage = newInput
        }
    }
    // [END android_compose_state_hoisting_vm_ui_element_state]
}

private object StateHoistingSnippets6 {
    @Composable
    private fun ConversationScreen(onCloseDrawer: () -> Unit) {}
    enum class DrawerContent {
        Empty, Content
    }
    val content = DrawerContent.Content

    // [START android_compose_state_hoisting_vm_ui_element_state_caveat]
    class ConversationViewModel(/*...*/) : ViewModel() {

        val drawerState = DrawerState(initialValue = DrawerValue.Closed)

        private val _drawerContent = MutableStateFlow(DrawerContent.Empty)
        val drawerContent: StateFlow<DrawerContent> = _drawerContent.asStateFlow()

        fun closeDrawer(uiScope: CoroutineScope) {
            viewModelScope.launch {
                withContext(uiScope.coroutineContext) { // Use instead of the default context
                    drawerState.close()
                }
                // Fetch drawer content and update state
                _drawerContent.update { content }
            }
        }
    }

    // in Compose
    @Composable
    private fun ConversationScreen(
        conversationViewModel: ConversationViewModel = viewModel()
    ) {
        val scope = rememberCoroutineScope()

        ConversationScreen(onCloseDrawer = { conversationViewModel.closeDrawer(uiScope = scope) })
    }
    // [END android_compose_state_hoisting_vm_ui_element_state_caveat]
}

data class Message(var id: String = "", var content: String = "", var timestamp: String = "")

class LazyListScrollPosition(firstVisibleItemIndex: Int, firstVisibleItemScrollOffset: Int)

interface ScrollableState {
    suspend fun scroll()
}

abstract class MessagesRepository() {
    abstract fun getLatestMessages(channelId: String): StateFlow<List<Message>>
}
