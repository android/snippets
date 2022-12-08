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

package com.example.compose.snippets.designsystems

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
// [START android_compose_designsystems_interop_appcompattheme]
import com.google.accompanist.themeadapter.appcompat.AppCompatTheme

class AppCompatThemeExample : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            // Colors and typography have been read from the
            // View-based theme used in this Activity
            // Shapes are the default for M2 as this didn't exist in M1
            AppCompatTheme {
                // Your app-level composable here
            }
        }
    }
}
// [END android_compose_designsystems_interop_appcompattheme]
