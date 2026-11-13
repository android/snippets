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

package com.example.compose.snippets.resources

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.compose.snippets.R

@Composable
private fun accessStringResourceExample() {
    // [START android_resources_providing_compose_string]
    stringResource(id = R.string.hello)
    // [END android_resources_providing_compose_string]
}

@Composable
private fun accessDrawableResourceExample() {
    // [START android_resources_providing_compose_drawable]
    painterResource(id = R.drawable.my_icon)
    // [END android_resources_providing_compose_drawable]
}

private fun getLocalizedStringExample(context: Context) {
    // [START android_resources_providing_get_string]
    // Retrieve a localized string resource.
    val greeting = context.getString(R.string.hello_world)
    // [END android_resources_providing_get_string]
}
