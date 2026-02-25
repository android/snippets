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

// [START android_ai_agentic_appfunctions]
/**
 * A note app's [AppFunction]s.
 */
class NoteFunctions(
    private val noteRepository: NoteRepository
) {
    /**
     * Lists all available notes.
     *
     * @param appFunctionContext The context in which the AppFunction is executed.
     */
    @AppFunction(isDescribedByKdoc = true)
    suspend fun listNotes(appFunctionContext: AppFunctionContext): List<Note>? {
        return noteRepository.appNotes.ifEmpty { null }?.toList()
    }

    /**
     * Adds a new note to the app.
     *
     * @param appFunctionContext The context in which the AppFunction is executed.
     * @param title The title of the note.
     * @param content The note's content.
     */
    @AppFunction(isDescribedByKdoc = true)
    suspend fun createNote(
        appFunctionContext: AppFunctionContext,
        title: String,
        content: String
    ): Note {
        return noteRepository.createNote(title, content)
    }

    /**
     * Edits a single note.
     *
     * @param appFunctionContext The context in which the AppFunction is executed.
     * @param noteId The target note's ID.
     * @param title The note's title if it should be updated.
     * @param content The new content if it should be updated.
     */
    @AppFunction(isDescribedByKdoc = true)
    suspend fun editNote(
        appFunctionContext: AppFunctionContext,
        noteId: Int,
        title: String?,
        content: String?,
    ): Note? {
        return noteRepository.updateNote(noteId, title, content)
    }
}

/**
 * A note.
 */
@AppFunctionSerializable(isDescribedByKdoc = true)
data class Note(
    /** The note's identifier */
    val id: Int,
    /** The note's title */
    val title: String,
    /** The note's content */
    val content: String)
// [END android_ai_agentic_appfunctions]

/**
 * Repository for [NoteFunctions].
 */
class NoteRepository {
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