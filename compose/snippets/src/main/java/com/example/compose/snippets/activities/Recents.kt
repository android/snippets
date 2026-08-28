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

package com.example.compose.snippets.activities

import android.app.Activity
import android.app.assist.AssistContent
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.R

private var documentCounter = 0

// [START android_activities_recents_new_document_intent]
private fun newDocumentIntent(context: Context): Intent =
    Intent(context, NewDocumentActivity::class.java).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT or Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS)
        putExtra(KEY_EXTRA_NEW_DOCUMENT_COUNTER, documentCounter++)
    }

@Composable
fun CreateDocumentButton() {
    val context = LocalContext.current
    Button(
        onClick = {
            val intent = newDocumentIntent(context)
            // Add FLAG_ACTIVITY_MULTIPLE_TASK if needed based on state
            context.startActivity(intent)
        }
    ) {
        Text("Create New Document")
    }
}
// [END android_activities_recents_new_document_intent]

// [START android_activities_recents_document_centric_activity]
class DocumentCentricActivity : ComponentActivity() {
    private var documentState by mutableStateOf(
        DocumentState(
            count = 0,
            textResId = R.string.hello_new_document_counter
        )
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val initialCount = intent.getIntExtra(KEY_EXTRA_NEW_DOCUMENT_COUNTER, 0)

        documentState = documentState.copy(count = initialCount)

        setContent {
            MaterialTheme {
                DocumentScreen(
                    count = documentState.count,
                    textResId = documentState.textResId
                )
            }
        }
    }

    override fun onNewIntent(newIntent: Intent) {
        super.onNewIntent(newIntent)
        // If FLAG_ACTIVITY_MULTIPLE_TASK has not been used, this Activity is reused.
        documentState = documentState.copy(
            textResId = R.string.reusing_document_counter
        )
    }

    data class DocumentState(val count: Int, @StringRes val textResId: Int)

    companion object {
        const val KEY_EXTRA_NEW_DOCUMENT_COUNTER = "KEY_EXTRA_NEW_DOCUMENT_COUNTER"
    }
}

@Composable
fun DocumentScreen(count: Int, @StringRes textResId: Int) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center
    ) {
        // UI reacts to whichever string resource ID was passed down
        Text(text = stringResource(id = textResId))
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "Counter: $count")
    }
}
// [END android_activities_recents_document_centric_activity]

// [START android_activities_recents_remove_task_button]
@Composable
fun RemoveTaskButton() {
    val context = LocalContext.current
    Button(
        onClick = {
            // It is good practice to remove a document from the overview stack if not needed anymore.
            (context as? Activity)?.finishAndRemoveTask()
        }
    ) {
        Text("Remove from Recents")
    }
}
// [END android_activities_recents_remove_task_button]

private class RetainFinishedActivity : ComponentActivity() {
    private fun getAndIncrement(): Int = 0

    // [START android_activities_recents_retain_finished_intent]
    private fun newDocumentIntent() =
            Intent(this, NewDocumentActivity::class.java).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT or
                        android.content.Intent.FLAG_ACTIVITY_RETAIN_IN_RECENTS)
                putExtra(KEY_EXTRA_NEW_DOCUMENT_COUNTER, getAndIncrement())
            }
    // [END android_activities_recents_retain_finished_intent]
}

// [START android_activities_recents_url_sharing]
class MainActivity : ComponentActivity() {

    // Track the current URL as state so the UI can update it during navigation
    private var currentWebUri by mutableStateOf("https://example.com/home")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                // Pass a lambda to your Compose UI so it can update the URL state
                // as the user navigates through your app.
                MainScreen(
                    onPageChanged = { newUrl -> currentWebUri = newUrl }
                )
            }
        }
    }

    override fun onProvideAssistContent(outContent: AssistContent) {
        super.onProvideAssistContent(outContent)

        // The system calls this when the user enters the Recents screen.
        // Provide the active URI tracked by the Compose state.
        outContent.webUri = Uri.parse(currentWebUri)
    }
}
// [END android_activities_recents_url_sharing]

private class NewDocumentActivity : ComponentActivity()
private const val KEY_EXTRA_NEW_DOCUMENT_COUNTER = "KEY_EXTRA_NEW_DOCUMENT_COUNTER"

@Composable
private fun AppTheme(content: @Composable () -> Unit) {
    content()
}

@Composable
private fun MainScreen(onPageChanged: (String) -> Unit) {}
