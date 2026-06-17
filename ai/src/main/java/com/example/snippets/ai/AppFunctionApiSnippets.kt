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

import androidx.appfunctions.AppFunctionContext
import androidx.appfunctions.AppFunctionSerializable
import androidx.appfunctions.service.AppFunction
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

// [START android_appfunction_snippet]
/**
 * A note app's [AppFunction]s.
 */
class NoteFunctions @Inject constructor(
    private val noteRepository: NoteRepository
) {
    /**
     * List all available notes in the app.
     *
     * @param appFunctionContext The execution context.
     * @return A list of [Note] objects, or null if no notes exist.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun listNotes(appFunctionContext: AppFunctionContext): List<Note>? {
        return noteRepository.appNotes.ifEmpty { null }?.toList()
    }

    /**
     * Create a new note with a title and body content.
     *
     * @param appFunctionContext The execution context.
     * @param title The title of the note.
     * @param content The body content of the note.
     * @return The created [Note] object including its generated ID.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun createNote(
        appFunctionContext: AppFunctionContext,
        title: String,
        content: String
    ): Note {
        return noteRepository.createNote(title, content)
    }

    /**
     * Update the title or content of an existing note.
     * Required workflow: Call [listNotes] first to obtain valid note IDs.
     *
     * @param appFunctionContext The execution context.
     * @param noteId The unique identifier of the note to edit.
     * @param title The new title. If null, the existing title is preserved.
     * @param content The new content. If null, the existing content is preserved.
     * @return The updated [Note], or null if the [noteId] was not found.
     */
    @AppFunction(isDescribedByKDoc = true)
    suspend fun editNote(
        appFunctionContext: AppFunctionContext,
        noteId: Int,
        title: String?,
        content: String?,
    ): Note? {
        return noteRepository.updateNote(noteId, title, content)
    }
}
// [END android_appfunction_snippet]

// [START android_appfunction_serializable]
/**
 * A note.
 */
@AppFunctionSerializable(isDescribedByKDoc = true)
data class Note(
    /** The note's identifier */
    val id: Int,
    /** The note's title */
    val title: String,
    /** The note's content */
    val content: String
)
// [END android_appfunction_serializable]

/**
 * Repository for [NoteFunctions].
 */
@Singleton
class NoteRepository @Inject constructor() {
    private val _appNotes = mutableListOf<Note>()
    val appNotes: List<Note> = _appNotes

    private var noteIdCounter: AtomicInteger = AtomicInteger(0)

    fun createNote(title: String, content: String): Note {
        val note = Note(noteIdCounter.getAndIncrement(), title, content)
        _appNotes.add(note)
        return note
    }

    fun updateNote(noteId: Int, title: String?, content: String?): Note? {
        val index = _appNotes.indexOfFirst { it.id == noteId }
        if (index == -1) {
            return null
        }
        val oldNote = _appNotes[index]
        val newNote = oldNote.copy(
            title = title ?: oldNote.title,
            content = content ?: oldNote.content
        )
        _appNotes[index] = newNote
        return newNote
    }
}
