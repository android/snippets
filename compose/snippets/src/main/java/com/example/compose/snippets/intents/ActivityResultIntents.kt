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

package com.example.compose.snippets.intents

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.media.RingtoneManager
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts.GetContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.activity.result.contract.ActivityResultContracts.TakePicturePreview
import androidx.activity.result.launch
import androidx.core.app.ActivityOptionsCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData

private class RegisterActivityResultExample : ComponentActivity() {
    // [START android_intents_result_register]
    val getContent = registerForActivityResult(GetContent()) { uri: Uri? ->
        // Handle the returned Uri.
    }
    // [END android_intents_result_register]
}

private class LaunchActivityResultExample : ComponentActivity() {
    // [START android_intents_result_launch]
    val getContent = registerForActivityResult(GetContent()) { uri: Uri? ->
        // Handle the returned Uri.
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        // [START_EXCLUDE]
        super.onCreate(savedInstanceState)
        // [END_EXCLUDE]

        val selectButton = findViewById<Button>(R.id.select_button)

        selectButton.setOnClickListener {
            // Pass in the mime type you want to let the user select
            // as the input.
            getContent.launch("image/*")
        }
    }
    // [END android_intents_result_launch]
}

private object SeparateClassScope {
    // [START android_intents_result_separate_class]
    class MyLifecycleObserver(private val registry: ActivityResultRegistry) :
        DefaultLifecycleObserver {
        lateinit var getContent: ActivityResultLauncher<String>

        override fun onCreate(owner: LifecycleOwner) {
            getContent = registry.register("key", owner, GetContent()) { uri ->
                // Handle the returned Uri.
            }
        }

        fun selectImage() {
            getContent.launch("image/*")
        }
    }

    class MyFragment : Fragment() {
        lateinit var observer: MyLifecycleObserver

        override fun onCreate(savedInstanceState: Bundle?) {
            // [START_EXCLUDE]
            super.onCreate(savedInstanceState)
            // [END_EXCLUDE]

            observer = MyLifecycleObserver(requireActivity().activityResultRegistry)
            lifecycle.addObserver(observer)
        }

        override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
            val selectButton = view.findViewById<Button>(R.id.select_button)

            selectButton.setOnClickListener {
                // Open the activity to select an image.
                observer.selectImage()
            }
        }
    }
    // [END android_intents_result_separate_class]
}

// [START android_intents_result_test_fragment]
class MyFragment(
    private val registry: ActivityResultRegistry
) : Fragment() {
    val thumbnailLiveData = MutableLiveData<Bitmap?>()

    val takePicture = registerForActivityResult(TakePicturePreview(), registry) {
            bitmap: Bitmap? ->
        thumbnailLiveData.setValue(bitmap)
    }

    // ...
}
// [END android_intents_result_test_fragment]

private fun createTestRegistry(expectedResult: Bitmap) {
    // [START android_intents_result_test_registry]
    val testRegistry = object : ActivityResultRegistry() {
        override fun <I, O> onLaunch(
            requestCode: Int,
            contract: ActivityResultContract<I, O>,
            input: I,
            options: ActivityOptionsCompat?
        ) {
            dispatchResult(requestCode, expectedResult)
        }
    }
    // [END android_intents_result_test_registry]
}

private class MyFragmentTest {
    // [START android_intents_result_test_complete]
    @Test
    fun activityResultTest() {
        // Create an expected result Bitmap.
        val expectedResult = Bitmap.createBitmap(1, 1, Bitmap.Config.RGBA_F16)

        // Create the test ActivityResultRegistry.
        val testRegistry = object : ActivityResultRegistry() {
            override fun <I, O> onLaunch(
                requestCode: Int,
                contract: ActivityResultContract<I, O>,
                input: I,
                options: ActivityOptionsCompat?
            ) {
                dispatchResult(requestCode, expectedResult)
            }
        }

        // Use the launchFragmentInContainer method that takes a
        // lambda to construct the Fragment with the testRegistry.
        with(launchFragmentInContainer { MyFragment(testRegistry) }) {
            onFragment { fragment ->
                // Trigger the ActivityResultLauncher.
                fragment.takePicture()
                // Verify the result is set.
                assertThat(fragment.thumbnailLiveData.value)
                    .isSameInstanceAs(expectedResult)
            }
        }
    }
    // [END android_intents_result_test_complete]
}

// [START android_intents_result_custom_contract]
class PickRingtone : ActivityResultContract<Int, Uri?>() {
    override fun createIntent(context: Context, ringtoneType: Int) =
        Intent(RingtoneManager.ACTION_RINGTONE_PICKER).apply {
            putExtra(RingtoneManager.EXTRA_RINGTONE_TYPE, ringtoneType)
        }

    override fun parseResult(resultCode: Int, result: Intent?): Uri? {
        if (resultCode != Activity.RESULT_OK) {
            return null
        }
        return result?.getParcelableExtra(RingtoneManager.EXTRA_RINGTONE_PICKED_URI)
    }
}
// [END android_intents_result_custom_contract]

private class StartActivityForResultExample : ComponentActivity() {
    // [START android_intents_result_start_for_result]
    val startForResult =
        registerForActivityResult(StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                val intent = result.data
                // Handle the Intent.
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        // [START_EXCLUDE]
        super.onCreate(savedInstanceState)
        // [END_EXCLUDE]

        val startButton = findViewById<Button>(R.id.start_button)

        startButton.setOnClickListener {
            // Use the Kotlin extension in activity-ktx
            // passing it the Intent you want to start.
            startForResult.launch(Intent(this, ResultProducingActivity::class.java))
        }
    }
    // [END android_intents_result_start_for_result]
}

private class ResultProducingActivity : ComponentActivity()

private class FragmentScenarioStub<F : Fragment>(val fragment: F) {
    fun onFragment(action: (F) -> Unit) = action(fragment)
}

private fun <F : Fragment> launchFragmentInContainer(
    instantiate: () -> F
): FragmentScenarioStub<F> = FragmentScenarioStub(instantiate())

private annotation class RunWith(val value: kotlin.reflect.KClass<*>)
private class AndroidJUnit4
private annotation class Test

private object InstrumentationRegistry {
    class InstrumentationStub {
        val targetContext: Context
            get() = throw UnsupportedOperationException()
    }
    fun getInstrumentation(): InstrumentationStub = InstrumentationStub()
}

private class SubjectStub<T>(private val actual: T) {
    fun isSameInstanceAs(expected: T) {}
}

private fun <T> assertThat(actual: T): SubjectStub<T> = SubjectStub(actual)

private operator fun ActivityResultLauncher<Void?>.invoke() {
    launch()
}
