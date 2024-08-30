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

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi
import androidx.lifecycle.viewmodel.compose.saveable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onStart

private object SavingUIStateSnippets1 {
    // [START android_compose_state_saving_rememberSaveable]
    @Composable
    fun ChatBubble(
        message: Message
    ) {
        var showDetails by rememberSaveable { mutableStateOf(false) }

        ClickableText(
            text = AnnotatedString(message.content),
            onClick = { showDetails = !showDetails }
        )

        if (showDetails) {
            Text(message.timestamp)
        }
    }
    // [END android_compose_state_saving_rememberSaveable]
}

private object SavingUIStateSnippets2 {
    // [START android_compose_state_with_saver]
    @Composable
    fun rememberLazyListState(
        initialFirstVisibleItemIndex: Int = 0,
        initialFirstVisibleItemScrollOffset: Int = 0
    ): LazyListState {
        return rememberSaveable(saver = LazyListState.Saver) {
            LazyListState(
                initialFirstVisibleItemIndex, initialFirstVisibleItemScrollOffset
            )
        }
    }
    // [END android_compose_state_with_saver]
}

private object SavingUIStateSnippets3 {
    @OptIn(SavedStateHandleSaveableApi::class)
    // [START android_compose_state_apis_saveable]
    class ConversationViewModel(
        savedStateHandle: SavedStateHandle
    ) : ViewModel() {

        var message by savedStateHandle.saveable(stateSaver = TextFieldValue.Saver) {
            mutableStateOf(TextFieldValue(""))
        }
            private set

        fun update(newMessage: TextFieldValue) {
            message = newMessage
        }

        /*...*/
    }

    val viewModel = ConversationViewModel(SavedStateHandle())

    @Composable
    fun UserInput(/*...*/) {
        TextField(
            value = viewModel.message,
            onValueChange = { viewModel.update(it) }
        )
    }
    // [END android_compose_state_apis_saveable]
}

private object SavingUIStateSnippets4 {
    class Channel
    class ChannelsRepository {
        fun getAll() = MutableSharedFlow<List<Channel>>()
    }
    fun filter(channels: List<Channel>, type: ChannelsFilterType) = listOf<Channel>()

    // [START android_compose_state_apis_savedStateHandle]
    private const val CHANNEL_FILTER_SAVED_STATE_KEY = "ChannelFilterKey"

    class ChannelViewModel(
        channelsRepository: ChannelsRepository,
        private val savedStateHandle: SavedStateHandle
    ) : ViewModel() {

        private val savedFilterType: StateFlow<ChannelsFilterType> = savedStateHandle.getStateFlow(
            key = CHANNEL_FILTER_SAVED_STATE_KEY, initialValue = ChannelsFilterType.ALL_CHANNELS
        )

        private val filteredChannels: Flow<List<Channel>> =
            combine(channelsRepository.getAll(), savedFilterType) { channels, type ->
                filter(channels, type)
            }.onStart { emit(emptyList()) }

        fun setFiltering(requestType: ChannelsFilterType) {
            savedStateHandle[CHANNEL_FILTER_SAVED_STATE_KEY] = requestType
        }

        /*...*/
    }

    enum class ChannelsFilterType {
        ALL_CHANNELS, RECENT_CHANNELS, ARCHIVED_CHANNELS
    }
    // [END android_compose_state_apis_savedStateHandle]
}
