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

@file:Suppress("unused")

package com.example.compose.snippets.appstate

import android.app.Application
import androidx.appstate.datastore.PersistToDataStore
import androidx.appstate.datastore.syncToDataStore
import androidx.appstate.statestore.StateStore
import androidx.appstate.statestore.StateStoreKey
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.datastore.dataStoreFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable

// [START android_compose_appstate_statestore_define_key]
@Serializable
sealed interface FocusSession {
    @Serializable
    data object Idle : FocusSession

    @Serializable
    data class Running(
        val taskId: String,
        val endTimeMillis: Long,
    ) : FocusSession

    @Serializable
    data class Paused(
        val taskId: String,
        val remainingMillis: Long,
    ) : FocusSession
}

@Serializable
private data object FocusSessionKey :
    StateStoreKey<FocusSession>(defaultValue = FocusSession.Idle)
// [END android_compose_appstate_statestore_define_key]

// [START_EXCLUDE]
private object CreateInstanceScope {
    // [END_EXCLUDE]
    // [START android_compose_appstate_statestore_create_instance]
    // Instantiate at the application or DI module level
    val stateStore = StateStore()

    // Inject into managers or repositories that encapsulate their own keys
    class FocusSessionManager(
        private val stateStore: StateStore,
    )
    // [END android_compose_appstate_statestore_create_instance]
// [START_EXCLUDE]
}

private object ReadStateScope {
    // [END_EXCLUDE]
    // [START android_compose_appstate_statestore_read_state]
    class FocusSessionManager(
        private val stateStore: StateStore,
    ) {
        val session: State<FocusSession> = stateStore.getState(FocusSessionKey)
    }

    class FocusTimerViewModel(
        focusSessionManager: FocusSessionManager,
    ) : ViewModel() {
        val session: State<FocusSession> = focusSessionManager.session
    }

    @Composable
    fun FocusBanner(viewModel: FocusTimerViewModel = viewModel()) {
        // Reads state reactively; recomposes when FocusSessionKey updates
        when (val session = viewModel.session.value) {
            FocusSession.Idle -> Text(text = "No active focus session")
            is FocusSession.Running -> Text(text = "Focusing on ${session.taskId}")
            is FocusSession.Paused -> Text(text = "Paused: ${session.taskId}")
        }
    }
    // [END android_compose_appstate_statestore_read_state]
// [START_EXCLUDE]
}

private object WriteStateScope {
    // [END_EXCLUDE]
    // [START android_compose_appstate_statestore_write_state]
    class FocusSessionManager(
        private val stateStore: StateStore,
    ) {
        val session: State<FocusSession> = stateStore.getState(FocusSessionKey)

        fun startFocus(taskId: String, endTimeMillis: Long) {
            stateStore.setState(
                FocusSessionKey,
                FocusSession.Running(
                    taskId = taskId,
                    endTimeMillis = endTimeMillis,
                ),
            )
        }

        fun endFocus() {
            stateStore.setState(FocusSessionKey, FocusSession.Idle)
        }
    }
    // [END android_compose_appstate_statestore_write_state]
// [START_EXCLUDE]
}

private object UpdateStateScope {
    // [END_EXCLUDE]
    // [START android_compose_appstate_statestore_update_state]
    class FocusSessionManager(
        private val stateStore: StateStore,
    ) {
        val session: State<FocusSession> = stateStore.getState(FocusSessionKey)

        fun pauseFocus(nowMillis: Long) {
            stateStore.updateState(FocusSessionKey) { current ->
                if (current is FocusSession.Running) {
                    FocusSession.Paused(
                        taskId = current.taskId,
                        remainingMillis = (current.endTimeMillis - nowMillis)
                            .coerceAtLeast(0L),
                    )
                } else {
                    current
                }
            }
        }

        fun resumeFocus(nowMillis: Long) {
            stateStore.updateState(FocusSessionKey) { current ->
                if (current is FocusSession.Paused) {
                    FocusSession.Running(
                        taskId = current.taskId,
                        endTimeMillis = nowMillis + current.remainingMillis,
                    )
                } else {
                    current
                }
            }
        }
    }
    // [END android_compose_appstate_statestore_update_state]
// [START_EXCLUDE]
}

private object PersistKeyScope {
    // [END_EXCLUDE]
    // [START android_compose_appstate_statestore_persist_key]
    @Serializable
    private data object FocusSessionKey :
        StateStoreKey<FocusSession>(defaultValue = FocusSession.Idle),
        PersistToDataStore
    // [END android_compose_appstate_statestore_persist_key]
// [START_EXCLUDE]
}

private object SyncDataStoreScope {
    class FocusSessionManager(
        private val stateStore: StateStore,
    )
    // [END_EXCLUDE]
    // [START android_compose_appstate_statestore_sync_datastore]
    class ProductivityApplication : Application() {
        val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

        val stateStore = StateStore().also { store ->
            // Restore saved values and automatically persist future changes
            // for all PersistToDataStore keys across managers
            applicationScope.launch {
                store.syncToDataStore(
                    path = dataStoreFile("statestore.json").absolutePath,
                    coroutineScope = applicationScope,
                )
            }
        }

        val focusSessionManager = FocusSessionManager(stateStore)
    }
    // [END android_compose_appstate_statestore_sync_datastore]
// [START_EXCLUDE]
}
// [END_EXCLUDE]
