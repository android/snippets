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

package com.example.compose.snippets.interop

import android.net.Uri
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.rememberAsyncImagePainter
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

/*
* Copyright 2022 The Android Open Source Project
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
// [START android_compose_libraries_activity_contract]
@Composable
fun GetContentExample() {
    var imageUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
    }
    Column {
        Button(onClick = { launcher.launch("image/*") }) {
            Text(text = "Load Image")
        }
        Image(
            painter = rememberAsyncImagePainter(imageUri),
            contentDescription = "My Image"
        )
    }
}
// [END android_compose_libraries_activity_contract]

@Composable
private fun BackButtonExample() {
    // [START android_compose_libraries_back_button]
    var backHandlingEnabled by remember { mutableStateOf(true) }
    BackHandler(backHandlingEnabled) {
        // Handle back press
    }
    // [END android_compose_libraries_back_button]
}

private object ViewModelExamples {
    // [START android_compose_libraries_viewmodel]
    class MyViewModel : ViewModel() { /*...*/ }

    // import androidx.lifecycle.viewmodel.compose.viewModel
    @Composable
    fun MyScreen(
        viewModel: MyViewModel = viewModel()
    ) {
        // use viewModel here
    }
    // [END android_compose_libraries_viewmodel]
}

private object ViewModelExample2 {
    // [START android_compose_libraries_viewmodel_2]
    class MyViewModel : ViewModel() { /*...*/ }
    // import androidx.lifecycle.viewmodel.compose.viewModel
    @Composable
    fun MyScreen(
        // Returns the same instance as long as the activity is alive,
        // just as if you grabbed the instance from an Activity or Fragment
        viewModel: MyViewModel = viewModel()
    ) { /* ... */ }

    @Composable
    fun MyScreen2(
        viewModel: MyViewModel = viewModel() // Same instance as in MyScreen
    ) { /* ... */ }
    // [END android_compose_libraries_viewmodel_2]
}

private object StreamData {

    class MyViewModel : ViewModel() {
        val exampleLiveData: LiveData<String> = MutableLiveData("")
    }
    // [START android_compose_libraries_stream_of_data]
    // import androidx.lifecycle.viewmodel.compose.viewModel
    @Composable
    fun MyScreen(
        viewModel: MyViewModel = viewModel()
    ) {
        val dataExample = viewModel.exampleLiveData.observeAsState()

        // Because the state is read here,
        // MyScreen recomposes whenever dataExample changes.
        dataExample.value?.let {
            ShowData(dataExample)
        }
    }
    // [END android_compose_libraries_stream_of_data]

    @Composable
    fun ShowData(value: State<String?>) {
    }
}

private object HiltExample3 {
    // [START android_compose_libraries_hilt_3]
    @HiltViewModel
    class MyViewModel @Inject constructor(
        private val savedStateHandle: SavedStateHandle,
        private val repository: ExampleRepository
    ) : ViewModel() { /* ... */ }

    // import androidx.lifecycle.viewmodel.compose.viewModel
    @Composable
    fun MyScreen(
        viewModel: MyViewModel = viewModel()
    ) { /* ... */ }

    // [END android_compose_libraries_hilt_3]
    interface ExampleRepository
}

private object HiltViewModel {
    @HiltViewModel
    class MyViewModel @Inject constructor() : ViewModel() { /* ... */ }
    // [START android_compose_libraries_hilt_viewmodel]
    // import androidx.hilt.navigation.compose.hiltViewModel

    @Composable
    fun MyApp() {
        val navController = rememberNavController()
        val startRoute = "example"
        NavHost(navController, startDestination = startRoute) {
            composable("example") { backStackEntry ->
                // Creates a ViewModel from the current BackStackEntry
                // Available in the androidx.hilt:hilt-navigation-compose artifact
                val viewModel = hiltViewModel<MyViewModel>()
                MyScreen(viewModel)
            }
            /* ... */
        }
    }
    // [END android_compose_libraries_hilt_viewmodel]

    @Composable
    private fun MyScreen(viewModel: MyViewModel) {
    }
}

private object HiltViewModelBackStack {
    @HiltViewModel
    class MyViewModel @Inject constructor() : ViewModel() { /* ... */ }

    @HiltViewModel
    class ParentViewModel @Inject constructor() : ViewModel() { /* ... */ }
    // [START android_compose_libraries_hilt_viewmodel_back_stack]
    // import androidx.hilt.navigation.compose.hiltViewModel
    // import androidx.navigation.compose.getBackStackEntry

    @Composable
    fun MyApp() {
        val navController = rememberNavController()
        val startRoute = "example"
        val innerStartRoute = "exampleWithRoute"
        NavHost(navController, startDestination = startRoute) {
            navigation(startDestination = innerStartRoute, route = "Parent") {
                // ...
                composable("exampleWithRoute") { backStackEntry ->
                    val parentEntry = remember(backStackEntry) {
                        navController.getBackStackEntry("Parent")
                    }
                    val parentViewModel = hiltViewModel<ParentViewModel>(parentEntry)
                    ExampleWithRouteScreen(parentViewModel)
                }
            }
        }
    }
    // [END android_compose_libraries_hilt_viewmodel_back_stack]

    @Composable
    private fun ExampleWithRouteScreen(viewModel: ParentViewModel) {
    }
}

private object PagingExample {
    // [START android_compose_libraries_paging_example]
    @Composable
    fun MyScreen(flow: Flow<PagingData<String>>) {
        val lazyPagingItems = flow.collectAsLazyPagingItems()
        LazyColumn {
            items(
                lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it }
            ) { index ->
                val item = lazyPagingItems[index]
                Text("Item is $item")
            }
        }
    }
    // [END android_compose_libraries_paging_example]
}

private object MapsExample {
    // [START android_compose_libraries_maps_example]
    @Composable
    fun MapsExample() {
        val singapore = LatLng(1.35, 103.87)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(singapore, 10f)
        }
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = singapore),
                title = "Singapore",
                snippet = "Marker in Singapore"
            )
        }
    }
    // [END android_compose_libraries_maps_example]
}
