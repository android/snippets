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

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel

private class IntroEmailActivity : ComponentActivity() {
    // [START android_activities_intro_compose_email]
    fun composeEmail(addresses: Array<String>, subject: String) {
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            data = Uri.parse("mailto:") // Only email apps handle this.
            putExtra(Intent.EXTRA_EMAIL, addresses)
            putExtra(Intent.EXTRA_SUBJECT, subject)
        }
        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        }
    }
    // [END android_activities_intro_compose_email]
}

// [START android_activities_intro_example_activity]
class ExampleActivity : ComponentActivity() {
  private val viewModel: MyViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    handleIntent(intent)
    setContent {
      ComposeApp(viewModel)
    }
  }

  override fun onNewIntent(intent: Intent) {
    super.onNewIntent(intent)
    setIntent(intent)
    handleIntent(intent)
  }

  private fun handleIntent(intent: Intent?) {
    when (intent?.action) {
      Intent.ACTION_SEND -> {
        if ("text/plain" == intent.type) {
          intent.getStringExtra(Intent.EXTRA_TEXT)?.let {
            viewModel.handleText(it) // Update UI to reflect text being shared
          }
        } else if (intent.type?.startsWith("image/") == true) {
          (intent.getParcelableExtra(Intent.EXTRA_STREAM, Uri::class.java))?.let {
            viewModel.handleImage(it) // Update UI to reflect image being shared
          }
        }
      }

      Intent.ACTION_SEND_MULTIPLE -> {
          if (intent.type?.startsWith("image/") == true) {
              intent.getParcelableArrayListExtra(Intent.EXTRA_STREAM, Uri::class.java)?.let {
                  viewModel.handleMultipleImages(it) // Update UI to reflect multiple images being shared
              }
          } else {
              // Handle other types
          }
      }

      else -> {
          // Handle other intents
      }
    }
  }
}
// [END android_activities_intro_example_activity]

class MyViewModel : ViewModel() {
    fun handleText(text: String) {}
    fun handleImage(uri: Uri) {}
    fun handleMultipleImages(uris: ArrayList<Uri>) {}
}

@Composable
private fun ComposeApp(viewModel: MyViewModel) {}
