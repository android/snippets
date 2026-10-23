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

@file:Suppress("unused", "UNUSED_VARIABLE", "UNUSED_PARAMETER")
@file:OptIn(androidx.lifecycle.viewmodel.compose.SavedStateHandleSaveableApi::class)

package com.example.compose.snippets.appstate

import android.app.Notification
import android.app.NotificationManager
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import androidx.appstate.statestore.StateStore
import androidx.appstate.transform.listener
import androidx.appstate.transform.transform
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.saveable
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

// [START_EXCLUDE]
private interface SearchApi {
    suspend fun search(query: String): List<String>
}

private class DebounceSnippetScope(
    private val coroutineScope: CoroutineScope,
    private val api: SearchApi,
) {
    // [END_EXCLUDE]
    // [START android_compose_appstate_transform_debounce]
    var query: String by mutableStateOf("")
    val searchResults: List<String> by transform<List<String>>(
        initialValue = emptyList(),
        scope = coroutineScope,
    ) {
        var results by remember { mutableStateOf(emptyList<String>()) }
        LaunchedEffect(query) {
            if (query.isBlank()) {
                results = emptyList()
                return@LaunchedEffect
            }
            delay(300.milliseconds)
            results = api.search(query)
        }
        results
    }
    // [END android_compose_appstate_transform_debounce]
// [START_EXCLUDE]
}

data class LogEntry(val id: String) {
    fun matchesQuery(query: String): Boolean = true
}

// [END_EXCLUDE]
// [START android_compose_appstate_transform_composable]
@Composable
fun LogViewerScreen(
    entriesState: State<List<LogEntry>>,
    filterQueryState: State<String>,
) {
    // Calls rememberCoroutineScope() internally and runs onUpdate on
    // Dispatchers.Default so main-thread UI recomposition isn't blocked
    val matchingEntries by transform<List<LogEntry>>(
        initialValue = emptyList(),
        dispatcher = Dispatchers.Default,
    ) {
        val entries by entriesState
        val filterQuery by filterQueryState
        if (filterQuery.isBlank()) {
            entries
        } else {
            entries.filter { entry ->
                entry.matchesQuery(filterQuery)
            }
        }
    }
    // Render matchingEntries...
}
// [END android_compose_appstate_transform_composable]

// [START_EXCLUDE]
private const val NOTIFICATION_ID = 1
private fun buildSessionCompleteNotification(taskId: String): Notification = Notification()

private object ListenerSnippetScope {
    private data object FocusSessionKey :
        androidx.appstate.statestore.StateStoreKey<FocusSession>(defaultValue = FocusSession.Idle)
    // [END_EXCLUDE]
    // [START android_compose_appstate_transform_listener]
    class FocusSessionManager(
        private val stateStore: StateStore,
        private val notificationManager: NotificationManager,
        scope: CoroutineScope,
    ) {
        val session: State<FocusSession> = stateStore.getState(FocusSessionKey)

        init {
            scope.launch {
                listener {
                    when (val current = session.value) {
                        is FocusSession.Running -> {
                            LaunchedEffect(current.endTimeMillis) {
                                val remainingMillis =
                                    current.endTimeMillis - System.currentTimeMillis()
                                if (remainingMillis > 0) {
                                    delay(remainingMillis)
                                }
                                stateStore.setState(FocusSessionKey, FocusSession.Idle)
                                notificationManager.notify(
                                    NOTIFICATION_ID,
                                    buildSessionCompleteNotification(current.taskId),
                                )
                            }
                        }
                        FocusSession.Idle,
                        is FocusSession.Paused -> Unit
                    }
                }
            }
        }
        // startFocus, pauseFocus, resumeFocus, endFocus...
    }
    // [END android_compose_appstate_transform_listener]
// [START_EXCLUDE]
}

data class Task(val id: String = "", val title: String = "", val isCompleted: Boolean = false, val category: Category? = null)

interface TasksRepository {
    val recentTasks: Flow<List<Task>>
    val tasksFlow: Flow<List<Task>>
    fun searchTasks(query: String): Flow<List<Task>>
    fun getTasks(query: String): Flow<List<Task>>
    fun observeTasks(showCompleted: Boolean): Flow<List<Task>>
}

private class ConditionalSnippetScope(
    private val coroutineScope: CoroutineScope,
    private val tasksRepository: TasksRepository,
) {
    // [END_EXCLUDE]
    // [START android_compose_appstate_transform_conditional]
    var searchQuery by mutableStateOf("")

    val displayedTasks: State<List<Task>> = transform(
        initialValue = emptyList(),
        scope = coroutineScope,
    ) {
        if (searchQuery.isBlank()) {
            val recentTasks by tasksRepository.recentTasks.collectAsState(
                initial = emptyList(),
            )
            recentTasks
        } else {
            val matchingTasks by remember(searchQuery) {
                tasksRepository.searchTasks(searchQuery)
            }.collectAsState(initial = emptyList())
            matchingTasks
        }
    }
    // [END android_compose_appstate_transform_conditional]
// [START_EXCLUDE]
}

enum class Category { WORK, PERSONAL }

enum class SortOrder(val comparator: Comparator<Task>) {
    BY_DEADLINE(compareBy { it.id })
}

data class DashboardUiState(
    val tasks: List<Task> = emptyList(),
    val selectedCategory: Category? = null,
    val focusSession: FocusSession = FocusSession.Idle,
    val isOnline: Boolean = true,
)

interface UserPrefsRepository {
    val sortOrderFlow: Flow<SortOrder>
}

private class FocusSessionManager(val session: State<FocusSession>)

private class MultipleStreamsSnippetScope(
    private val coroutineScope: CoroutineScope,
    private val tasksRepository: TasksRepository,
    private val userPrefsRepository: UserPrefsRepository,
    private val focusSessionManager: FocusSessionManager,
    private val networkMonitor: NetworkMonitor,
) {
    // [END_EXCLUDE]
    // [START android_compose_appstate_transform_multiple_streams]
    var showCompleted by mutableStateOf(false)
    var selectedCategory by mutableStateOf<Category?>(null)

    val dashboardState: State<DashboardUiState> = transform(
        initialValue = DashboardUiState(),
        scope = coroutineScope,
    ) {
        val tasks by tasksRepository.tasksFlow.collectAsState(initial = emptyList())
        val sortOrder by userPrefsRepository.sortOrderFlow.collectAsState(
            initial = SortOrder.BY_DEADLINE,
        )
        val focusSession by focusSessionManager.session
        val isOnline by networkMonitor.isOnline

        val visibleTasks = tasks
            .filter { task ->
                (showCompleted || !task.isCompleted) &&
                    (selectedCategory == null || task.category == selectedCategory)
            }
            .sortedWith(sortOrder.comparator)

        DashboardUiState(
            tasks = visibleTasks,
            selectedCategory = selectedCategory,
            focusSession = focusSession,
            isOnline = isOnline,
        )
    }
    // [END android_compose_appstate_transform_multiple_streams]
// [START_EXCLUDE]
}

interface MediaRepository {
    val allTracksFlow: Flow<List<Track>>
    val downloadedTracksFlow: Flow<List<Track>>
}

data class Track(val id: String, val isDownloaded: Boolean = false)

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val tracks: List<Track>, val isOnline: Boolean) : HomeUiState
}

private object ViewModelTransformScope {
    // [END_EXCLUDE]
    // [START android_compose_appstate_transform_viewmodel]
    class HomeViewModel(
        private val mediaRepository: MediaRepository,
        private val networkMonitor: NetworkMonitor,
    ) : ViewModel() {
        val uiState: State<HomeUiState> = transform<HomeUiState>(
            initialValue = HomeUiState.Loading,
            scope = viewModelScope,
        ) {
            val isOnline by networkMonitor.isOnline
            val tracks = if (isOnline) {
                mediaRepository.allTracksFlow.collectAsState(initial = null).value
            } else {
                mediaRepository.downloadedTracksFlow
                    .collectAsState(initial = null).value
            } ?: return@transform HomeUiState.Loading

            HomeUiState.Success(
                tracks = tracks,
                isOnline = isOnline,
            )
        }
    }
    // [END android_compose_appstate_transform_viewmodel]
// [START_EXCLUDE]
}

interface TaskPreferencesManager {
    val showCompleted: State<Boolean>
}

sealed interface TaskListUiState {
    data object Loading : TaskListUiState
    data class Success(val tasks: List<Task>) : TaskListUiState
}

private object DerivedVsTransformScope {
    // [END_EXCLUDE]
    // [START android_compose_appstate_transform_derived_vs_transform]
    class TaskListViewModel(
        private val tasksRepository: TasksRepository,
        private val preferencesManager: TaskPreferencesManager,
    ) : ViewModel() {
        var searchQuery by mutableStateOf("")
            private set

        // Computing a value from existing State<T>: use derivedStateOf
        val hasSearchQuery: State<Boolean> = derivedStateOf {
            searchQuery.isNotBlank()
        }

        // Combining State<T> with a Flow and remember(key): use transform
        val uiState: State<TaskListUiState> = transform<TaskListUiState>(
            initialValue = TaskListUiState.Loading,
            scope = viewModelScope,
        ) {
            val showCompleted by preferencesManager.showCompleted
            val tasks by remember(showCompleted) {
                tasksRepository.observeTasks(showCompleted)
            }.collectAsState(initial = null)

            val currentTasks = tasks ?: return@transform TaskListUiState.Loading
            val filteredTasks = currentTasks.filter { task ->
                task.title.contains(searchQuery, ignoreCase = true)
            }
            TaskListUiState.Success(filteredTasks)
        }
    }
    // [END android_compose_appstate_transform_derived_vs_transform]
// [START_EXCLUDE]
}

// [END_EXCLUDE]
// [START android_compose_appstate_state_beyond_ui_viewmodel]
sealed interface TaskListState {
    data object Loading : TaskListState
    data class Success(
        val tasks: List<Task>,
        val selectedTaskIds: Set<String>,
        val isOnline: Boolean = true,
    ) : TaskListState
}

@HiltViewModel
class TaskListViewModel @Inject constructor(
    private val tasksRepository: TasksRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    var selectedTaskIds by mutableStateOf(emptySet<String>())
        private set

    var searchQuery by savedStateHandle.saveable { mutableStateOf("") }
        private set

    fun updateSearchQuery(query: String) {
        Snapshot.withMutableSnapshot {
            searchQuery = query
            selectedTaskIds = emptySet()
        }
    }

    val tasksState: State<TaskListState> = transform<TaskListState>(
        initialValue = TaskListState.Loading,
        scope = viewModelScope,
    ) {
        val tasks = remember(searchQuery) {
            tasksRepository.getTasks(searchQuery)
        }.collectAsState(initial = null).value
            ?: return@transform TaskListState.Loading

        TaskListState.Success(
            tasks = tasks,
            selectedTaskIds = selectedTaskIds,
        )
    }
}
// [END android_compose_appstate_state_beyond_ui_viewmodel]

// [START android_compose_appstate_transform_network_monitor]
interface NetworkMonitor {
    val isOnline: State<Boolean>
}

@Singleton
class LiveNetworkMonitor @Inject constructor(
    @ApplicationContext context: Context,
) : NetworkMonitor {
    private val connectivityManager =
        context.getSystemService(ConnectivityManager::class.java)

    private val _isOnline = mutableStateOf(checkCurrentConnectivity())
    override val isOnline: State<Boolean> = _isOnline

    private val callback = object : ConnectivityManager.NetworkCallback() {
        override fun onCapabilitiesChanged(
            network: Network,
            networkCapabilities: NetworkCapabilities,
        ) {
            _isOnline.value = networkCapabilities.hasCapability(
                NetworkCapabilities.NET_CAPABILITY_INTERNET
            )
        }

        override fun onLost(network: Network) {
            _isOnline.value = false
        }
    }

    init {
        connectivityManager.registerDefaultNetworkCallback(callback)
    }

    private fun checkCurrentConnectivity(): Boolean {
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities =
            connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        )
    }
}
// [END android_compose_appstate_transform_network_monitor]

// [START_EXCLUDE]
private class ViewModelOnlineScope(
    private val searchQuery: String,
    private val tasksRepository: TasksRepository,
    private val selectedTaskIds: Set<String>,
    private val networkMonitor: NetworkMonitor,
    private val viewModelScope: CoroutineScope,
) {
    // [END_EXCLUDE]
    // [START android_compose_appstate_transform_viewmodel_online]
    val tasksState: State<TaskListState> = transform<TaskListState>(
        initialValue = TaskListState.Loading,
        scope = viewModelScope,
    ) {
        val tasks = remember(searchQuery) {
            tasksRepository.getTasks(searchQuery)
        }.collectAsState(initial = null).value
            ?: return@transform TaskListState.Loading

        TaskListState.Success(
            tasks = tasks,
            selectedTaskIds = selectedTaskIds,
            isOnline = networkMonitor.isOnline.value,
        )
    }
    // [END android_compose_appstate_transform_viewmodel_online]
// [START_EXCLUDE]
}
// [END_EXCLUDE]
