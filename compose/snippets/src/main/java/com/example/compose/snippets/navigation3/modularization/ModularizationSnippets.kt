/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.compose.snippets.navigation3.modularization

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.example.compose.snippets.navigation3.ContentGreen
import com.example.compose.snippets.navigation3.ContentRed
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject
import kotlin.collections.emptyList

data object KeyA : NavKey
data object KeyA2 : NavKey

// [START android_compose_navigation3_modularization_1]
// import androidx.navigation3.runtime.EntryProviderScope
// import androidx.navigation3.runtime.NavKey

fun EntryProviderScope<NavKey>.featureAEntryBuilder() {
    entry<KeyA> {
        ContentRed("Screen A") {
            // Content for screen A
        }
    }
    entry<KeyA2> {
        ContentGreen("Screen A2") {
            // Content for screen A2
        }
    }
}
// [END android_compose_navigation3_modularization_1]


@Composable
fun ModularizationApp() {
    // [START android_compose_navigation3_modularization_2]
    // import androidx.navigation3.runtime.entryProvider
    // import androidx.navigation3.ui.NavDisplay
    NavDisplay(
        entryProvider = entryProvider {
            featureAEntryBuilder()
        },
        // [START_EXCLUDE]
        backStack = emptyList<NavKey>()
        // [END_EXCLUDE]
    )
    // [END android_compose_navigation3_modularization_2]

}

// [START android_compose_navigation3_modularization_3]
// import dagger.Module
// import dagger.Provides
// import dagger.hilt.InstallIn
// import dagger.hilt.android.components.ActivityRetainedComponent
// import dagger.multibindings.IntoSet

@Module
@InstallIn(ActivityRetainedComponent::class)
object FeatureAModule {

    @IntoSet
    @Provides
    fun provideFeatureAEntryBuilder() : EntryProviderScope<NavKey>.() -> Unit = {
        featureAEntryBuilder()
    }
}
// [END android_compose_navigation3_modularization_3]

// [START android_compose_navigation3_modularization_4]
// import android.os.Bundle
// import androidx.activity.ComponentActivity
// import androidx.activity.compose.setContent
// import androidx.navigation3.runtime.EntryProviderScope
// import androidx.navigation3.runtime.NavKey
// import androidx.navigation3.runtime.entryProvider
// import androidx.navigation3.ui.NavDisplay
// import javax.inject.Inject

class MainActivity : ComponentActivity() {

    @Inject
    lateinit var entryBuilders: Set<@JvmSuppressWildcards EntryProviderScope<NavKey>.() -> Unit>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NavDisplay(
                entryProvider = entryProvider {
                    entryBuilders.forEach { builder -> this.builder() }
                },
                // [START_EXCLUDE]
                backStack = emptyList<NavKey>()
                // [END_EXCLUDE]
            )
        }
    }
}
// [END android_compose_navigation3_modularization_4]



