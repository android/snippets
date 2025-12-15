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

package com.example.compose.snippets.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.RememberObserver
import androidx.compose.runtime.remember
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

private object RememberAndRetainObserverSnippets1 {
    interface RememberObserver : androidx.compose.runtime.RememberObserver {
        override fun onAbandoned() {}
        override fun onForgotten() {}
        override fun onRemembered() {}
    }

    // [START android_compose_state_observers_initialize_in_remember]
    class MyComposeObject : RememberObserver {
        private val job = Job()
        private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

        init {
            // Not recommended: This will cause work to begin during composition instead of
            // with other effects. Move this into onRemembered().
            coroutineScope.launch { loadData() }
        }

        override fun onRemembered() {
            // Recommended: Move any cancellable or effect-driven work into the onRemembered
            // callback. If implementing RetainObserver, this should go in onRetained.
            coroutineScope.launch { loadData() }
        }

        private suspend fun loadData() { /* ... */ }

        // ...
    }
    // [END android_compose_state_observers_initialize_in_remember]
}

private object RememberAndRetainObserverSnippets2 {
    interface RememberObserver : androidx.compose.runtime.RememberObserver {
        override fun onAbandoned() {}
        override fun onForgotten() {}
        override fun onRemembered() {}
    }

    // [START android_compose_state_observers_teardown_in_forget]
    class MyComposeObject : RememberObserver {
        private val job = Job()
        private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

        // ...

        override fun onForgotten() {
            // Cancel work launched from onRemembered. If implementing RetainObserver, onRetired
            // should cancel work launched from onRetained.
            job.cancel()
        }

        override fun onAbandoned() {
            // If any work was launched by the constructor as part of remembering the object,
            // you must cancel that work in this callback. For work done as part of the construction
            // during retain, this code should will appear in onUnused.
            job.cancel()
        }
    }
    // [END android_compose_state_observers_teardown_in_forget]
}

private object RememberAndRetainObserverSnippets3 {
    interface RememberObserver : androidx.compose.runtime.RememberObserver {
        override fun onAbandoned() {}
        override fun onForgotten() {}
        override fun onRemembered() {}
    }

    // [START android_compose_state_observers_private_implementation]
    abstract class MyManager

    class MyComposeManager : MyManager() {
        // Callers that construct this object must manually call initialize and teardown
        fun initialize() { /*...*/ }
        fun teardown() { /*...*/ }
    }

    @Composable
    fun rememberMyManager(): MyManager {
        // Protect the RememberObserver implementation by never exposing it outside the library
        return remember {
            object : RememberObserver {
                val manager = MyComposeManager()
                override fun onRemembered() = manager.initialize()
                override fun onForgotten() = manager.teardown()
                override fun onAbandoned() { /* Nothing to do if manager hasn't initialized */ }
            }
        }.manager
    }
    // [END android_compose_state_observers_private_implementation]
}

private object RememberAndRetainObserverSnippets4 {
    class Foo : RememberObserver {
        override fun onRemembered() {}
        override fun onForgotten() {}
        override fun onAbandoned() {}
    }

    class Bar(val foo: Foo)

    @Composable fun rememberFoo() = remember { Foo() }

    @Composable
    fun Snippet() {
        // [START android_compose_state_observers_transitive_remember]
        val foo: Foo = rememberFoo()

        // Acceptable:
        val bar: Bar = remember { Bar(foo) }

        // Recommended key usage:
        val barWithKey: Bar = remember(foo) { Bar(foo) }
        // [END android_compose_state_observers_transitive_remember]
    }
}

private object RememberAndRetainObserverSnippets5 {
    class Foo : RememberObserver {
        override fun onRemembered() {}
        override fun onForgotten() {}
        override fun onAbandoned() {}
    }

    class Bar(val foo: Foo)

    @Composable fun rememberFoo() = remember { Foo() }

    // [START android_compose_state_observers_transitive_remember_params]
    @Composable
    fun MyComposable(
        parameter: Foo
    ) {
        // Acceptable:
        val derivedValue = remember { Bar(parameter) }

        // Also Acceptable:
        val derivedValueWithKey = remember(parameter) { Bar(parameter) }
    }
    // [END android_compose_state_observers_transitive_remember_params]
}