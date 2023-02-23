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

package com.example.compose.snippets.interop

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imeNestedScroll
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat

// [START android_compose_migrate_considerations_side_effect]
@Composable
fun rememberFirebaseAnalytics(user: User): FirebaseAnalytics {
    val analytics: FirebaseAnalytics = remember {
        FirebaseAnalytics()
    }

    // On every successful composition, update FirebaseAnalytics with
    // the userType from the current User, ensuring that future analytics
    // events have this metadata attached
    SideEffect {
        analytics.setUserProperty("userType", user.userType)
    }
    return analytics
}
// [END android_compose_migrate_considerations_side_effect]

// [START android_compose_migrate_considerations_window_insets]
class WindowInsetsExampleActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            MaterialTheme {
                MyScreen()
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MyScreen() {
    Box {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize() // fill the entire window
                .imePadding() // padding for the bottom for the IME
                .imeNestedScroll(), // scroll IME at the bottom
            content = { }
        )
        FloatingActionButton(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp) // normal 16dp of padding for FABs
                .navigationBarsPadding() // padding for navigation bar
                .imePadding(), // padding for when IME appears
            onClick = { }
        ) {
            Icon(imageVector = Icons.Filled.Add, contentDescription = "Add")
        }
    }
}
// [END android_compose_migrate_considerations_window_insets]

// [START android_compose_migrate_considerations_conditional_logic]
@Composable
fun MyComposable(showCautionIcon: Boolean) {
    if (showCautionIcon) {
        CautionIcon(/* ... */)
    }
}
// [START_EXCLUDE silent]
@Composable
fun CautionIcon() { }
// [END_EXCLUDE]
// [END android_compose_migrate_considerations_conditional_logic]

// [START android_compose_migrate_considerations_promote_encapsulation]
@Composable
fun AScreen() {
    var isEnabled by rememberSaveable { mutableStateOf(false) }

    Column {
        ImageWithEnabledOverlay(isEnabled)
        ControlPanelWithToggle(
            isEnabled = isEnabled,
            onEnabledChanged = { isEnabled = it }
        )
    }
}
// [START_EXCLUDE silent]
@Composable
fun ImageWithEnabledOverlay(isEnabled: Boolean) { }
@Composable
fun ControlPanelWithToggle(isEnabled: Boolean, onEnabledChanged: (Boolean) -> Unit) { }
// [END_EXCLUDE]
// [END android_compose_migrate_considerations_promote_encapsulation]

// STUB
class User(name: String = "") {
    val userType = "STUB"
}

class FirebaseAnalytics {
    fun setUserProperty(key: String, value: String) {
        // STUB
    }
}
