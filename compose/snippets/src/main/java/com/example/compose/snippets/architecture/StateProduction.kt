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

package com.example.compose.snippets.architecture

import androidx.annotation.MainThread
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.Snapshot
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException
import kotlin.random.Random
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private object StateProductionSnippet1 {
    // [START android_architecture_state_production_dice_roll_compose_state]
    @Stable
    interface DiceUiState {
        val firstDieValue: Int?
        val secondDieValue: Int?
        val numberOfRolls: Int?
    }

    private class MutableDiceUiState : DiceUiState {
        override var firstDieValue: Int? by mutableStateOf(null)
        override var secondDieValue: Int? by mutableStateOf(null)
        override var numberOfRolls: Int by mutableStateOf(0)
    }

    class DiceRollViewModel : ViewModel() {

        private val _uiState = MutableDiceUiState()
        val uiState: DiceUiState = _uiState

        // Called from the UI
        fun rollDice() {
            _uiState.firstDieValue = Random.nextInt(from = 1, until = 7)
            _uiState.secondDieValue = Random.nextInt(from = 1, until = 7)
            _uiState.numberOfRolls = _uiState.numberOfRolls + 1
        }
    }
    // [END android_architecture_state_production_dice_roll_compose_state]
}

private object StateProductionSnippet2 {
    // [START android_architecture_state_production_dice_roll_state_flow]
    data class DiceUiState(
        val firstDieValue: Int? = null,
        val secondDieValue: Int? = null,
        val numberOfRolls: Int = 0,
    )

    class DiceRollViewModel : ViewModel() {

        private val _uiState = MutableStateFlow(DiceUiState())
        val uiState: StateFlow<DiceUiState> = _uiState.asStateFlow()

        // Called from the UI
        fun rollDice() {
            _uiState.update { currentState ->
                currentState.copy(
                    firstDieValue = Random.nextInt(from = 1, until = 7),
                    secondDieValue = Random.nextInt(from = 1, until = 7),
                    numberOfRolls = currentState.numberOfRolls + 1,
                )
            }
        }
    }
    // [END android_architecture_state_production_dice_roll_state_flow]
}

private object StateProductionSnippet3 {
    data class Task(val title: String, val description: String)
    interface TasksRepository {
        suspend fun saveTask(task: Task)
    }
    private fun getErrorMessage(e: Exception): String = e.message ?: ""

    // [START android_architecture_state_production_add_edit_task_compose_state]
    @Stable
    interface AddEditTaskUiState {
        val title: String
        val description: String
        val isTaskCompleted: Boolean
        val isLoading: Boolean
        val userMessage: String?
        val isTaskSaved: Boolean
    }

    private class MutableAddEditTaskUiState : AddEditTaskUiState {
        override var title: String by mutableStateOf("")
        override var description: String by mutableStateOf("")
        override var isTaskCompleted: Boolean by mutableStateOf(false)
        override var isLoading: Boolean by mutableStateOf(false)
        override var userMessage: String? by mutableStateOf<String?>(null)
        override var isTaskSaved: Boolean by mutableStateOf(false)
    }

    class AddEditTaskViewModel(
        /* [START_EXCLUDE silent] */
        private val tasksRepository: TasksRepository = object : TasksRepository {
            override suspend fun saveTask(task: Task) {}
        }
        /* [END_EXCLUDE] */
    ) : ViewModel() {

        private val _uiState = MutableAddEditTaskUiState()
        val uiState: AddEditTaskUiState = _uiState

        private fun createNewTask() {
            viewModelScope.launch {
                val newTask = Task(uiState.title, uiState.description)
                try {
                    tasksRepository.saveTask(newTask)
                    // Write data into the UI state.
                    _uiState.isTaskSaved = true
                } catch (cancellationException: CancellationException) {
                    throw cancellationException
                } catch (exception: Exception) {
                    _uiState.userMessage = getErrorMessage(exception)
                }
            }
        }
    }
    // [END android_architecture_state_production_add_edit_task_compose_state]
}

private object StateProductionSnippet4 {
    data class Task(val title: String, val description: String)
    interface TasksRepository {
        suspend fun saveTask(task: Task)
    }
    private fun getErrorMessage(e: Exception): String = e.message ?: ""

    // [START android_architecture_state_production_add_edit_task_state_flow]
    data class AddEditTaskUiState(
        val title: String = "",
        val description: String = "",
        val isTaskCompleted: Boolean = false,
        val isLoading: Boolean = false,
        val userMessage: String? = null,
        val isTaskSaved: Boolean = false
    )

    class AddEditTaskViewModel(
        /* [START_EXCLUDE silent] */
        private val tasksRepository: TasksRepository = object : TasksRepository {
            override suspend fun saveTask(task: Task) {}
        }
        /* [END_EXCLUDE] */
    ) : ViewModel() {

        private val _uiState = MutableStateFlow(AddEditTaskUiState())
        val uiState: StateFlow<AddEditTaskUiState> = _uiState.asStateFlow()

        private fun createNewTask() {
            viewModelScope.launch {
                val newTask = Task(uiState.value.title, uiState.value.description)
                try {
                    tasksRepository.saveTask(newTask)
                    // Write data into the UI state.
                    _uiState.update {
                        it.copy(isTaskSaved = true)
                    }
                } catch (cancellationException: CancellationException) {
                    throw cancellationException
                } catch (exception: Exception) {
                    _uiState.update {
                        it.copy(userMessage = getErrorMessage(exception))
                    }
                }
            }
        }
    }
    // [END android_architecture_state_production_add_edit_task_state_flow]
}

private object StateProductionSnippet5 {
    @Stable
    interface DiceUiState {
        var firstDieValue: Int?
        var secondDieValue: Int?
        var numberOfRolls: Int
    }

    private class MutableDiceUiState : DiceUiState {
        override var firstDieValue: Int? by mutableStateOf(null)
        override var secondDieValue: Int? by mutableStateOf(null)
        override var numberOfRolls: Int by mutableStateOf(0)
    }

    private object SlowRandom {
        fun nextInt(from: Int, until: Int): Int = Random.nextInt(from, until)
    }

    // [START android_architecture_state_production_dice_roll_background_compose_state]
    class DiceRollViewModel(
        private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
    ) : ViewModel() {

        private val _uiState = MutableDiceUiState()
        val uiState: DiceUiState = _uiState

        // Called from the UI
        fun rollDice() {
            viewModelScope.launch {
                // Other Coroutines that may be called from the current context
                /* ... */
                withContext(defaultDispatcher) {
                    Snapshot.withMutableSnapshot {
                        _uiState.firstDieValue = SlowRandom.nextInt(from = 1, until = 7)
                        _uiState.secondDieValue = SlowRandom.nextInt(from = 1, until = 7)
                        _uiState.numberOfRolls = _uiState.numberOfRolls + 1
                    }
                }
            }
        }
    }
    // [END android_architecture_state_production_dice_roll_background_compose_state]
}

private object StateProductionSnippet6 {
    data class DiceUiState(
        val firstDieValue: Int? = null,
        val secondDieValue: Int? = null,
        val numberOfRolls: Int = 0,
    )

    private object SlowRandom {
        fun nextInt(from: Int, until: Int): Int = Random.nextInt(from, until)
    }

    // [START android_architecture_state_production_dice_roll_background_state_flow]
    class DiceRollViewModel(
        private val defaultDispatcher: CoroutineDispatcher = Dispatchers.Default
    ) : ViewModel() {

        private val _uiState = MutableStateFlow(DiceUiState())
        val uiState: StateFlow<DiceUiState> = _uiState.asStateFlow()

        // Called from the UI
        fun rollDice() {
            viewModelScope.launch {
                // Other Coroutines that may be called from the current context
                /* ... */
                withContext(defaultDispatcher) {
                    _uiState.update { currentState ->
                        currentState.copy(
                            firstDieValue = SlowRandom.nextInt(from = 1, until = 7),
                            secondDieValue = SlowRandom.nextInt(from = 1, until = 7),
                            numberOfRolls = currentState.numberOfRolls + 1,
                        )
                    }
                }
            }
        }
    }
    // [END android_architecture_state_production_dice_roll_background_state_flow]
}

private object StateProductionSnippet7 {
    data class Author(val id: String)
    data class Topic(val id: String)
    sealed interface InterestsUiState {
        data object Loading : InterestsUiState
        data class Interests(val authors: List<Author>, val topics: List<Topic>) : InterestsUiState
    }

    interface AuthorsRepository {
        fun getAuthorsStream(): Flow<List<Author>>
    }

    interface TopicsRepository {
        fun getTopicsStream(): Flow<List<Topic>>
    }

    // [START android_architecture_state_production_interests_viewmodel]
    class InterestsViewModel(
        authorsRepository: AuthorsRepository,
        topicsRepository: TopicsRepository
    ) : ViewModel() {

        val uiState = combine(
            authorsRepository.getAuthorsStream(),
            topicsRepository.getTopicsStream(),
        ) { availableAuthors, availableTopics ->
            InterestsUiState.Interests(
                authors = availableAuthors,
                topics = availableTopics
            )
        }
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = InterestsUiState.Loading
            )
    }
    // [END android_architecture_state_production_interests_viewmodel]
}

object StateProductionSnippet8 {
    data class Task(val name: String)
    data class Async<T>(val data: Task? = null)
    data class TaskDetailUiState(val task: Task? = null, val isTaskDeleted: Boolean = false)

    interface TasksRepository {
        fun getTaskStream(taskId: String): Flow<Async<Task>>
        suspend fun deleteTask(taskId: String)
    }

    // [START android_architecture_state_production_task_detail_compose_state]
    class TaskDetailViewModel @Inject constructor(
        private val tasksRepository: TasksRepository,
        savedStateHandle: SavedStateHandle
    ) : ViewModel() {

        /* [START_EXCLUDE silent] */
        private val taskId: String = ""
        /* [END_EXCLUDE] */
        private var _isTaskDeleted by mutableStateOf(false)
        private val _task = tasksRepository.getTaskStream(taskId)

        val uiState: StateFlow<TaskDetailUiState> = combine(
            snapshotFlow { _isTaskDeleted },
            _task
        ) { isTaskDeleted, taskAsync ->
            TaskDetailUiState(
                task = taskAsync.data,
                isTaskDeleted = isTaskDeleted
            )
        }
            // Convert the result to the appropriate observable API for the UI
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = TaskDetailUiState()
            )

        fun deleteTask() = viewModelScope.launch {
            tasksRepository.deleteTask(taskId)
            _isTaskDeleted = true
        }
    }
    // [END android_architecture_state_production_task_detail_compose_state]
}

object StateProductionSnippet9 {
    data class Task(val name: String)
    data class Async<T>(val data: Task? = null)
    data class TaskDetailUiState(val task: Task? = null, val isTaskDeleted: Boolean = false)

    interface TasksRepository {
        fun getTaskStream(taskId: String): Flow<Async<Task>>
        suspend fun deleteTask(taskId: String)
    }

    // [START android_architecture_state_production_task_detail_state_flow]
    class TaskDetailViewModel @Inject constructor(
        private val tasksRepository: TasksRepository,
        savedStateHandle: SavedStateHandle
    ) : ViewModel() {

        /* [START_EXCLUDE silent] */
        private val taskId: String = ""
        /* [END_EXCLUDE] */
        private val _isTaskDeleted = MutableStateFlow(false)
        private val _task = tasksRepository.getTaskStream(taskId)

        val uiState: StateFlow<TaskDetailUiState> = combine(
            _isTaskDeleted,
            _task
        ) { isTaskDeleted, taskAsync ->
            TaskDetailUiState(
                task = taskAsync.data,
                isTaskDeleted = isTaskDeleted
            )
        }
            // Convert the result to the appropriate observable API for the UI
            .stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = TaskDetailUiState()
            )

        fun deleteTask() = viewModelScope.launch {
            tasksRepository.deleteTask(taskId)
            _isTaskDeleted.update { true }
        }
    }
    // [END android_architecture_state_production_task_detail_state_flow]
}

private object StateProductionSnippet10 {
    // [START android_architecture_state_production_viewmodel_initialize]
    class MyViewModel : ViewModel() {

        private var initializeCalled = false

        // This function is idempotent provided it is only called from the UI thread.
        @MainThread
        fun initialize() {
            if (initializeCalled) return
            initializeCalled = true

            viewModelScope.launch {
                // seed the state production pipeline
            }
        }
    }
    // [END android_architecture_state_production_viewmodel_initialize]
}
