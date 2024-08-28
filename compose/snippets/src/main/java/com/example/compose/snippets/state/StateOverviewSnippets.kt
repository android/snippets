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

@file:Suppress("unused")

package com.example.compose.snippets.state

import android.content.res.Resources
import android.graphics.BitmapShader
import android.graphics.Shader
import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.saveable.mapSaver
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.ShaderBrush
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass

// [START android_compose_state_overview]
@Composable
private fun HelloContent() {
    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Hello!",
            modifier = Modifier.padding(bottom = 8.dp),
            style = MaterialTheme.typography.bodyMedium
        )
        OutlinedTextField(
            value = "",
            onValueChange = { },
            label = { Text("Name") }
        )
    }
}
// [END android_compose_state_overview]

private object StateSnippet1 {
    // [START android_compose_state_remember]
    @Composable
    fun HelloContent() {
        Column(modifier = Modifier.padding(16.dp)) {
            var name by remember { mutableStateOf("") }
            if (name.isNotEmpty()) {
                Text(
                    text = "Hello, $name!",
                    modifier = Modifier.padding(bottom = 8.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Name") }
            )
        }
    }
    // [END android_compose_state_remember]
}

private object StateSnippet2 {
    // [START android_compose_state_hoisting]
    @Composable
    fun HelloScreen() {
        var name by rememberSaveable { mutableStateOf("") }

        HelloContent(name = name, onNameChange = { name = it })
    }

    @Composable
    fun HelloContent(name: String, onNameChange: (String) -> Unit) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Hello, $name",
                modifier = Modifier.padding(bottom = 8.dp),
                style = MaterialTheme.typography.bodyMedium
            )
            OutlinedTextField(value = name, onValueChange = onNameChange, label = { Text("Name") })
        }
    }
    // [END android_compose_state_hoisting]
}

private object StateSnippet3 {
    // [START android_compose_state_restoring_parcelize]
    @Parcelize
    data class City(val name: String, val country: String) : Parcelable

    @Composable
    fun CityScreen() {
        var selectedCity = rememberSaveable {
            mutableStateOf(City("Madrid", "Spain"))
        }
    }
    // [END android_compose_state_restoring_parcelize]
}

private object StateSnippet4 {
    // [START android_compose_state_restoring_mapSaver]
    data class City(val name: String, val country: String)

    val CitySaver = run {
        val nameKey = "Name"
        val countryKey = "Country"
        mapSaver(
            save = { mapOf(nameKey to it.name, countryKey to it.country) },
            restore = { City(it[nameKey] as String, it[countryKey] as String) }
        )
    }

    @Composable
    fun CityScreen() {
        var selectedCity = rememberSaveable(stateSaver = CitySaver) {
            mutableStateOf(City("Madrid", "Spain"))
        }
    }
    // [END android_compose_state_restoring_mapSaver]
}

@Composable
private fun StateSnippets5() {
    // [START android_compose_state_restoring_listSaver]
    data class City(val name: String, val country: String)

    val CitySaver = listSaver<City, Any>(
        save = { listOf(it.name, it.country) },
        restore = { City(it[0] as String, it[1] as String) }
    )

    @Composable
    fun CityScreen() {
        var selectedCity = rememberSaveable(stateSaver = CitySaver) {
            mutableStateOf(City("Madrid", "Spain"))
        }
    }
    // [END android_compose_state_restoring_listSaver]
}

@Composable
private fun RememberKeysSnippet1() {
    // [START android_compose_state_remember_definition]
    var name by remember { mutableStateOf("") }
    // [END android_compose_state_remember_definition]
}

@Composable
private fun RememberKeysSnippet2(@DrawableRes avatarRes: Int) {
    val res = LocalContext.current.resources

    // [START android_compose_state_remember_brush]
    val brush = remember {
        ShaderBrush(
            BitmapShader(
                ImageBitmap.imageResource(res, avatarRes).asAndroidBitmap(),
                Shader.TileMode.REPEAT,
                Shader.TileMode.REPEAT
            )
        )
    }
    // [END android_compose_state_remember_brush]
}

// [START android_compose_state_remember_keys_brush]
@Composable
private fun BackgroundBanner(
    @DrawableRes avatarRes: Int,
    modifier: Modifier = Modifier,
    res: Resources = LocalContext.current.resources
) {
    val brush = remember(key1 = avatarRes) {
        ShaderBrush(
            BitmapShader(
                ImageBitmap.imageResource(res, avatarRes).asAndroidBitmap(),
                Shader.TileMode.REPEAT,
                Shader.TileMode.REPEAT
            )
        )
    }

    Box(
        modifier = modifier.background(brush)
    ) {
        /* ... */
    }
}
// [END android_compose_state_remember_keys_brush]

// [START android_compose_state_remember_keys_app_state]
@Composable
private fun rememberMyAppState(
    windowSizeClass: WindowSizeClass
): MyAppState {
    return remember(windowSizeClass) {
        MyAppState(windowSizeClass)
    }
}

@Stable
class MyAppState(
    private val windowSizeClass: WindowSizeClass
) { /* ... */ }
// [END android_compose_state_remember_keys_app_state]

@Composable
private fun RememberKeysSnippet3() {
    val typedQuery = ""
    // [START android_compose_state_rememberSaveable_keys]
    var userTypedQuery by rememberSaveable(typedQuery, stateSaver = TextFieldValue.Saver) {
        mutableStateOf(
            TextFieldValue(text = typedQuery, selection = TextRange(typedQuery.length))
        )
    }
    // [END android_compose_state_rememberSaveable_keys]
}

/**
 * Add fake com.example.compose.snippets.state.Parcelize and com.example.compose.snippets.state.Parcelable to avoid adding dependency on
 * kotlin-parcelize just for snippets
 */
private annotation class Parcelize
private interface Parcelable
