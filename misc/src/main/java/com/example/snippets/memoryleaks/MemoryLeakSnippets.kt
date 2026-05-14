/*
 * Copyright 2024 The Android Open Source Project
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

package com.example.snippets.memoryleaks

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

// Mock classes for snippets to compile
class User(val name: String)
class Api {
    fun getUser(callback: (User) -> Unit) {}
    suspend fun getUser(): User = User("Mock")
}
val api = Api()

class UserFragmentBinding {
    val root: View? = null
    val name: TextView? = null
    companion object {
        fun bind(view: View): UserFragmentBinding = UserFragmentBinding()
        fun inflate(inflater: LayoutInflater, container: ViewGroup?, b: Boolean): UserFragmentBinding = UserFragmentBinding()
    }
}
val binding = UserFragmentBinding()
class UserViewModel {
    val user: Flow<User> = flow {}
}
val viewModel = UserViewModel()
annotation class Singleton
annotation class Inject
annotation class ApplicationContext
class ActivityImagePicker(val activity: Activity)

// Pattern 1: Example 1 - Repository retains a UI callback

// [START android_memory_leak_repository_callback_with_leak]
class UserRepositoryWithLeak {
    private var listener: ((User) -> Unit)? = null

    fun fetchUser(callback: (User) -> Unit) {
        // The repository retains the callback beyond the view lifecycle.
        listener = callback
        api.getUser { user ->
            listener?.invoke(user)
        }
    }
}

/* In the Fragment/UI layer:
repository.fetchUser { user ->
    binding.name.text = user.name
}
*/
// [END android_memory_leak_repository_callback_with_leak]

// [START android_memory_leak_repository_callback_recommended]
class UserRepositoryRecommended {
    suspend fun fetchUser(): User {
        return api.getUser()
    }
}

/* In the Fragment/UI layer:
viewLifecycleOwner.lifecycleScope.launch {
    val user = repository.fetchUser()
    binding.name.text = user.name
}
*/
// [END android_memory_leak_repository_callback_recommended]

// Pattern 1: Example 2 - Singleton depends on a UI-scoped object

// [START android_memory_leak_singleton_dependency_with_leak]
@Singleton
class ImageLoaderWithLeak @Inject constructor(
    private val activityImagePicker: ActivityImagePicker
)

class ActivityImagePickerWithLeak @Inject constructor(
    // Injecting Activity here makes this dependency activity-scoped.
    private val activity: Activity
)
// [END android_memory_leak_singleton_dependency_with_leak]

// [START android_memory_leak_singleton_dependency_recommended]
// Option 1: Pass the Activity dynamically for UI-scoped tasks (like image picking)
@Singleton
class ImageLoaderRecommended1 @Inject constructor() {
    fun pickImage(activity: Activity) { /* ... */ }
}

// Option 2: Inject Application Context for non-UI/background tasks (like disk caching or sharedPreferences)
@Singleton
class ImageLoaderRecommended2 @Inject constructor(
    @ApplicationContext private val context: Context
)
// [END android_memory_leak_singleton_dependency_recommended]

// Pattern 2: Example 1 - Fragment collects Flow with the incorrect lifecycle

// [START android_memory_leak_fragment_flow_with_leak]
class UserFragmentWithLeak : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = UserFragmentBinding.bind(view)

        lifecycleScope.launch {
            // This coroutine is tied to the fragment lifecycle, not the view lifecycle.
            viewModel.user.collect { user ->
                binding.name?.text = user.name
            }
        }
    }
}
// [END android_memory_leak_fragment_flow_with_leak]

// [START android_memory_leak_fragment_flow_recommended]
class UserFragmentRecommended : Fragment() {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val binding = UserFragmentBinding.bind(view)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.user.collect { user ->
                    binding.name?.text = user.name
                }
            }
        }
    }
}
// [END android_memory_leak_fragment_flow_recommended]

// Pattern 2: Example 2 - Delayed work captures an Activity

// [START android_memory_leak_delayed_work_with_leak]
// Singleton scope accepts a UI-bound callback
object UserRepositoryDelayedWithLeak {
    private val repositoryScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    // Accepts a callback that might capture a destroyed UI Context
    fun fetchUserDataWithDelay(onComplete: (String) -> Unit) {
        repositoryScope.launch {
            delay(5_000) 
            onComplete("User Data") // If onComplete references the Activity, it leaks!
        }
    }
}

class MainActivityWithLeak : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // The trailing lambda implicitly captures 'this' (MainActivity) to update the title
        UserRepositoryDelayedWithLeak.fetchUserDataWithDelay { data ->
            title = data 
        }
    }
}
// [END android_memory_leak_delayed_work_with_leak]

// [START android_memory_leak_delayed_work_recommended]
//  Expose data as a Flow and let the UI handle the lifecycle scope
object UserRepositoryDelayedRecommended {
    // A clean, stateless flow with no callback parameters
    fun getUserData(): Flow<String> = flow {
        delay(5_000)
        emit("User Data")
    }
}

class MainActivityRecommended : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Automatically cancels collection and releases MainActivity when destroyed
        lifecycleScope.launch {
            UserRepositoryDelayedRecommended.getUserData().collect { data ->
                title = data
            }
        }
    }
}
// [END android_memory_leak_delayed_work_recommended]

// Pattern 3: Example 2 - Fragment view binding is not cleared

// [START android_memory_leak_fragment_binding_with_leak]
class UserFragmentBindingWithLeak : Fragment() {
    private var binding: UserFragmentBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // The fragment keeps this binding field until it is cleared.
        val binding = UserFragmentBinding.inflate(inflater, container, false)
        this.binding = binding
        return binding.root ?: View(context)
    }
}
// [END android_memory_leak_fragment_binding_with_leak]

// [START android_memory_leak_fragment_binding_recommended]
class UserFragmentBindingRecommended : Fragment() {
    private var _binding: UserFragmentBinding? = null
    // This property makes it easy to use the binding without constantly checking for null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UserFragmentBinding.inflate(inflater, container, false)
        return binding.root ?: View(context)
    }

    override fun onDestroyView() {
        _binding = null // Explicitly releases the view hierarchy from memory
        super.onDestroyView()
    }
}
// [END android_memory_leak_fragment_binding_recommended]
