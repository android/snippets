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

package com.example.snippets.ai

import android.content.Context
import androidx.annotation.RequiresApi
import androidx.appfunctions.AppFunctionDeclaration
import androidx.appfunctions.AppFunctionElementNotFoundException
import androidx.appfunctions.AppFunctionInvalidArgumentException
import androidx.appfunctions.AppFunctionManager
import androidx.appfunctions.AppFunctionSerializable
import androidx.appfunctions.AppFunctionService
import androidx.appfunctions.AppFunctionServiceEntryPoint
import dagger.hilt.android.AndroidEntryPoint
import java.time.LocalDateTime
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// [START android_appfunction_serializable]
/** The title and content of a new task. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class CreateTaskParams(
    /** The title of the task. */
    val title: String?,
    /** The content of the task. */
    val content: String?,
)

/** The user-created task. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class Task(
    /** The ID of the task. */
    val id: String,
    /** The title of the task. */
    val title: String,
    /** The content of the task. */
    val content: String,
)
// [END android_appfunction_serializable]

// [START android_appfunction_snippet]
@RequiresApi(36)
@AndroidEntryPoint
@AppFunctionServiceEntryPoint(
    serviceName = "TaskAppFunctionService",
    appFunctionXmlFileName = "task_app_function_service",
)
abstract class BaseTaskAppFunctionService : AppFunctionService() {
    @Inject internal lateinit var taskRepository: TaskRepository

    /**
     * Creates a task with a title, content, or both.
     *
     * @param createTaskParams The title and content of the task. At least one
     *   of them is required.
     * @return The created task, including its ID.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun createTask(
        createTaskParams: CreateTaskParams,
    ): Task = withContext(Dispatchers.IO) {
        // Developers can use predefined exceptions to let the agent know
        // why it failed.
        if (createTaskParams.title == null && createTaskParams.content == null) {
            throw AppFunctionInvalidArgumentException("A task needs a title, content, or both.")
        }

        val id = taskRepository.createTask(
            createTaskParams.title,
            createTaskParams.content
        )

        return@withContext taskRepository
            .getTask(id)
            ?.toTask()
            ?: throw AppFunctionElementNotFoundException("Task not found for ID = $id")
    }

    // Maps internal TaskEntity
    private fun TaskEntity.toTask() = Task(id = id, title = title, content = description)
}
// [END android_appfunction_snippet]

data class TaskEntity(val id: String, val title: String, val description: String)

@Singleton
class TaskRepository @Inject constructor() {
    private val tasks = ConcurrentHashMap<String, TaskEntity>()
    private var counter = AtomicInteger(0)

    fun createTask(title: String?, content: String?): String {
        val id = counter.incrementAndGet().toString()
        tasks[id] = TaskEntity(id, title ?: "", content ?: "")
        return id
    }

    fun getTask(id: String): TaskEntity? = tasks[id]
}

@RequiresApi(36)
@AppFunctionServiceEntryPoint(
    serviceName = "IllustrativeAppFunctionsService",
    appFunctionXmlFileName = "illustrative_app_functions_service",
)
abstract class IllustrativeAppFunctions : AppFunctionService() {
    // [START android_appfunction_create_task]
    /**
     * Create a new task or reminder with a title, due time, and location.
     *
     * @param title The descriptive title of the task (e.g., "Pick up my package").
     * @param dueDateTime The specific date and time when the task should be completed.
     * @param location The physical location associated with the task (e.g., "Work").
     * @return The created Task
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun createTask(
        title: String,
        dueDateTime: LocalDateTime? = null,
        location: String? = null,
    ): Task = TODO()
    // [END android_appfunction_create_task]

    // [START android_appfunction_media_playlist]
    /**
     * Create a new music playlist based on a natural language query.
     *
     * @param query The description used to generate the playlist (e.g., "top jazz albums from 2026").
     * @return The final created playlist based on songs.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun createPlaylistFromQuery(
        query: String,
    ): Playlist = TODO()
    // [END android_appfunction_media_playlist]

    // [START android_appfunction_cross_app_workflow]
    /**
     * Search for emails matching a query or sender name to retrieve content like recipes.
     *
     * @param query The search term or contact name (e.g., "Lisa noodle recipe").
     * @return A list of matching email summaries containing the requested information.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun searchEmails(
        query: String,
    ): List<EmailSummary> = TODO()

    /**
     * Add a list of items or ingredients to the user's active shopping list.
     *
     * @param items The names of the ingredients or products to add to the list.
     * @return The final shopping list with new items added
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun addItemsToShoppingList(
        items: List<String>,
    ): ShoppingList = TODO()
    // [END android_appfunction_cross_app_workflow]

    // [START android_appfunction_calendar_event]
    /**
     * Schedule a new event on the user's primary calendar.
     *
     * @param title The name of the calendar event (e.g., "Mom's birthday party").
     * @param startDateTime The specific date and time the event is scheduled to begin.
     * @return The created Event object.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun createCalendarEvent(
        title: String,
        startDateTime: LocalDateTime,
    ): Event = TODO()
    // [END android_appfunction_calendar_event]

    // [START android_appfunction_kdoc_refined]
    /**
     * Searches for message recipients by name or email.
     * @param query Search string for name/email. If null, returns 3 most recent contacts.
     * @return List of "Recipient" objects matching the query.
     */
    @AppFunctionDeclaration(isDescribedByKDoc = true)
    suspend fun searchRecipients(
        query: String? = null,
    ): List<Recipient> = TODO()
    // [END android_appfunction_kdoc_refined]
}

@RequiresApi(36)
@AppFunctionServiceEntryPoint(
    serviceName = "IllustrativeGatedAppFunctionsService",
    appFunctionXmlFileName = "illustrative_gated_app_functions_service",
)
abstract class IllustrativeGatedAppFunctions : AppFunctionService() {
    // [START android_appfunction_disabled_by_default]
    @AppFunctionDeclaration(isEnabled = false, isDescribedByKDoc = true)
    suspend fun createTask(
        createTaskParams: CreateTaskParams,
    ): Task = TODO()
    // [END android_appfunction_disabled_by_default]
}

// [START android_appfunction_toggle_availability]
suspend fun onFeatureEnabled(context: Context) {
    try {
        AppFunctionManager.getInstance(context)
            ?.setAppFunctionEnabled(
                BaseTaskAppFunctionServiceIds.CREATE_TASK_ID,
                AppFunctionManager.APP_FUNCTION_STATE_ENABLED,
            )
    } catch (e: Exception) {
        // Handle exception: AppFunctions indexation may not be fully completed
        // upon initial app startup.
    }
}

suspend fun onFeatureDisabled(context: Context) {
    try {
        AppFunctionManager.getInstance(context)
            ?.setAppFunctionEnabled(
                BaseTaskAppFunctionServiceIds.CREATE_TASK_ID,
                AppFunctionManager.APP_FUNCTION_STATE_DISABLED,
            )
    } catch (e: Exception) {
        // Handle exception
    }
}
// [END android_appfunction_toggle_availability]

object BaseTaskAppFunctionServiceIds {
    const val CREATE_TASK_ID = "com.example.snippets.ai.BaseTaskAppFunctionService#createTask"
}

@AppFunctionSerializable(isDescribedByKDoc = true)
data class Playlist(val id: String, val name: String)

@AppFunctionSerializable(isDescribedByKDoc = true)
data class EmailSummary(val id: String, val subject: String)

@AppFunctionSerializable(isDescribedByKDoc = true)
data class ShoppingList(val id: String, val items: List<String>)

@AppFunctionSerializable(isDescribedByKDoc = true)
data class Event(val id: String, val title: String)

/** A contact that can receive messages. */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class Recipient(
    /** The unique ID of the contact. */
    val id: String,
    /** The display name of the contact. */
    val name: String,
    /** The email address of the contact. */
    val email: String,
)
