/*
 * Copyright 2022 The Android Open Source Project
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

@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.compose.snippets.interop

import android.content.Context
import android.os.Bundle
import android.util.AttributeSet
import android.widget.LinearLayout
import android.widget.TextView
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.ComposeView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

// [START android_compose_interop_existing_arch_viewmodels_in_compose]
class GreetingActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                Column {
                    GreetingScreen("user1")
                    GreetingScreen("user2")
                }
            }
        }
    }
}

@Composable
fun GreetingScreen(
    userId: String,
    viewModel: GreetingViewModel = viewModel(  
        factory = GreetingViewModelFactory(userId)  
    )
) {
    val messageUser by viewModel.message.observeAsState("")
    Text(messageUser)
}

class GreetingViewModel(private val userId: String) : ViewModel() {
    private val _message = MutableLiveData("Hi $userId")
    val message: LiveData<String> = _message
}

class GreetingViewModelFactory(private val userId: String) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return GreetingViewModel(userId) as T
    }
}
// [END android_compose_interop_existing_arch_viewmodels_in_compose]

// [START android_compose_interop_existing_arch_viewmodels_in_compose_nav_graph]
@Composable
fun MyApp() {
    NavHost(rememberNavController(), startDestination = "profile/{userId}") {
        /* ... */
        composable("profile/{userId}") { backStackEntry ->
            GreetingScreen(backStackEntry.arguments?.getString("userId") ?: "")
        }
    }
}
// [END android_compose_interop_existing_arch_viewmodels_in_compose_nav_graph]

// [START android_compose_interop_existing_arch_views_sot]
class CustomViewGroup @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : LinearLayout(context, attrs, defStyle) {

    // Source of truth in the View system as mutableStateOf
    // to make it thread-safe for Compose
    private var text by mutableStateOf("")

    private val textView: TextView

    init {
        orientation = VERTICAL

        textView = TextView(context)
        val composeView = ComposeView(context).apply {
            setContent {
                MaterialTheme {
                    TextField(value = text, onValueChange = { updateState(it) })
                }
            }
        }

        addView(textView)
        addView(composeView)
    }

    // Update both the source of truth and the TextView
    private fun updateState(newValue: String) {
        text = newValue
        textView.text = newValue
    }
}
// [END android_compose_interop_existing_arch_views_sot]
