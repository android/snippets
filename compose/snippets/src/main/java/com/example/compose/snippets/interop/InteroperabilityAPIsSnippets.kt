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

package com.example.compose.snippets.interop

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.AndroidViewBinding
import androidx.fragment.app.Fragment
import com.example.compose.snippets.MyActivity
import com.example.compose.snippets.R
import com.example.compose.snippets.databinding.ExampleLayoutBinding
import com.example.compose.snippets.databinding.FragmentExampleBinding
import com.example.compose.snippets.databinding.MyFragmentLayoutBinding

// [START android_compose_interop_apis_compose_in_views]
class ExampleActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent { // In here, we can call composables!
            MaterialTheme {
                Greeting(name = "compose")
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name!")
}
// [END android_compose_interop_apis_compose_in_views]

// [START android_compose_interop_apis_compose_in_fragment_xml]
class ExampleFragmentXml : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_example, container, false)
        val composeView = view.findViewById<ComposeView>(R.id.compose_view)
        composeView.apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                // In Compose world
                MaterialTheme {
                    Text("Hello Compose!")
                }
            }
        }
        return view
    }
}
// [END android_compose_interop_apis_compose_in_fragment_xml]

// [START android_compose_interop_apis_compose_in_fragment_view_binding]
class ExampleFragment : Fragment() {

    private var _binding: FragmentExampleBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExampleBinding.inflate(inflater, container, false)
        val view = binding.root
        binding.composeView.apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                // In Compose world
                MaterialTheme {
                    Text("Hello Compose!")
                }
            }
        }
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
// [END android_compose_interop_apis_compose_in_fragment_view_binding]

// [START android_compose_interop_apis_compose_in_fragment_no_xml]
class ExampleFragmentNoXml : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            // Dispose of the Composition when the view's LifecycleOwner
            // is destroyed
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    // In Compose world
                    Text("Hello Compose!")
                }
            }
        }
    }
}
// [END android_compose_interop_apis_compose_in_fragment_no_xml]

// [START android_compose_interop_apis_compose_in_fragment_multiple]
class ExampleFragmentMultipleComposeView : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View = LinearLayout(requireContext()).apply {
        addView(
            ComposeView(requireContext()).apply {
                setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
                )
                id = R.id.compose_view_x
                // ...
            }
        )
        addView(TextView(requireContext()))
        addView(
            ComposeView(requireContext()).apply {
                setViewCompositionStrategy(
                    ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
                )
                id = R.id.compose_view_y
                // ...
            }
        )
    }
}
// [END android_compose_interop_apis_compose_in_fragment_multiple]

// [START android_compose_interop_apis_views_in_compose]
@Composable
fun CustomView() {
    var selectedItem by remember { mutableStateOf(0) }

    // Adds view to Compose
    AndroidView(
        modifier = Modifier.fillMaxSize(), // Occupy the max size in the Compose UI tree
        factory = { context ->
            // Creates view
            MyView(context).apply {
                // Sets up listeners for View -> Compose communication
                setOnClickListener {
                    selectedItem = 1
                }
            }
        },
        update = { view ->
            // View's been inflated or state read in this block has been updated
            // Add logic here if necessary

            // As selectedItem is read here, AndroidView will recompose
            // whenever the state changes
            // Example of Compose -> View communication
            view.selectedItem = selectedItem
        }
    )
}

@Composable
fun ContentExample() {
    Column(Modifier.fillMaxSize()) {
        Text("Look at this CustomView!")
        CustomView()
    }
}

// [START_EXCLUDE silent]
class MyView(context: Context) : View(context) {
    var selectedItem: Int = 0
}
// [END_EXCLUDE silent]
// [END android_compose_interop_apis_views_in_compose]

// [START android_compose_interop_apis_android_view_binding]
@Composable
fun AndroidViewBindingExample() {
    AndroidViewBinding(ExampleLayoutBinding::inflate) {
        exampleView.setBackgroundColor(Color.GRAY)
    }
}
// [END android_compose_interop_apis_android_view_binding]

// [START android_compose_interop_apis_fragments_in_compose]
@Composable
fun FragmentInComposeExample() {
    AndroidViewBinding(MyFragmentLayoutBinding::inflate) {
        val myFragment = fragmentContainerView.getFragment<MyFragment>()
        // ...
    }
}

// [START_EXCLUDE silent]
class MyFragment : Fragment()
// [END_EXCLUDE]
// [END android_compose_interop_apis_fragments_in_compose]

// [START android_compose_interop_apis_composition_locals]
@Composable
fun ToastGreetingButton(greeting: String) {
    val context = LocalContext.current
    Button(onClick = {
        Toast.makeText(context, greeting, Toast.LENGTH_SHORT).show()
    }) {
        Text("Greet")
    }
}
// [END android_compose_interop_apis_composition_locals]

// [START android_compose_interop_apis_other_interactions]
// [START_EXCLUDE silent]
data class DataExample(val title: String)

val data = DataExample("Hi")

// [END_EXCLUDE]
class OtherInteractionsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // get data from savedInstanceState
        setContent {
            MaterialTheme {
                ExampleComposable(data, onButtonClick = {
                    startActivity(Intent(this, MyActivity::class.java))
                })
            }
        }
    }
}

@Composable
fun ExampleComposable(data: DataExample, onButtonClick: () -> Unit) {
    Button(onClick = onButtonClick) {
        Text(data.title)
    }
}
// [END android_compose_interop_apis_other_interactions]

// [START android_compose_interop_apis_broadcast_receivers]
@Composable
fun SystemBroadcastReceiver(
    systemAction: String,
    onSystemEvent: (intent: Intent?) -> Unit
) {
    // Grab the current context in this part of the UI tree
    val context = LocalContext.current

    // Safely use the latest onSystemEvent lambda passed to the function
    val currentOnSystemEvent by rememberUpdatedState(onSystemEvent)

    // If either context or systemAction changes, unregister and register again
    DisposableEffect(context, systemAction) {
        val intentFilter = IntentFilter(systemAction)
        val broadcast = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                currentOnSystemEvent(intent)
            }
        }

        context.registerReceiver(broadcast, intentFilter)

        // When the effect leaves the Composition, remove the callback
        onDispose {
            context.unregisterReceiver(broadcast)
        }
    }
}

@Composable
fun HomeScreen() {

    SystemBroadcastReceiver(Intent.ACTION_BATTERY_CHANGED) { batteryStatus ->
        val isCharging = /* Get from batteryStatus ... */ true
        /* Do something if the device is charging */
    }

    /* Rest of the HomeScreen */
}
// [END android_compose_interop_apis_broadcast_receivers]

// [START android_compose_interop_apis_layout_preview_composable]
@Preview
@Composable
fun GreetingPreview() {
    Greeting(name = "Android")
}
// [END android_compose_interop_apis_layout_preview_composable]
