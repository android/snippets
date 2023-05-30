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
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Text
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.Fragment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class RVActivity : ComponentActivity() {
    private lateinit var composeView: ComposeView
    private lateinit var recyclerView: RecyclerView
    private fun step2() {
        // [START android_compose_interop_migration_common_scenarios_recyclerview_step2]
        // recyclerView.layoutManager = LinearLayoutManager(context)
        composeView.setContent {
            LazyColumn(Modifier.fillMaxSize()) {
                // We use a LazyColumn since the layout manager of the RecyclerView is a vertical LinearLayoutManager
            }
        }
        // [END android_compose_interop_migration_common_scenarios_recyclerview_step2]
    }

    private fun step4() {
        // [START android_compose_interop_migration_common_scenarios_recyclerview_step4]
        val data = listOf<MyData>(/* ... */)
        composeView.setContent {
            LazyColumn(Modifier.fillMaxSize()) {
                items(data) {
                    ListItem(it)
                }
            }
        }
        // [END android_compose_interop_migration_common_scenarios_recyclerview_step4]
    }

    private fun commonUseCase1() {
        // [START android_compose_interop_migration_common_scenarios_recyclerview_common_use_case_1]
        val itemDecoration = DividerItemDecoration(recyclerView.context, LinearLayoutManager.VERTICAL)
        recyclerView.addItemDecoration(itemDecoration)
        // [END android_compose_interop_migration_common_scenarios_recyclerview_common_use_case_1]
    }

    @Composable
    fun commonUseCase2(data: List<MyData>) {
        // [START android_compose_interop_migration_common_scenarios_recyclerview_common_use_case_2]
        LazyColumn(Modifier.fillMaxSize()) {
            itemsIndexed(data) { index, d ->
                ListItem(d)
                if (index != data.size - 1) {
                    Divider()
                }
            }
        }
        // [END android_compose_interop_migration_common_scenarios_recyclerview_common_use_case_2]
    }
}

// [START android_compose_interop_migration_common_scenarios_recyclerview_step3]
@Composable
fun ListItem(data: MyData, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth()) {
        Text(text = data.name)
        // … other composables required for displaying `data`
    }
}
// [END android_compose_interop_migration_common_scenarios_recyclerview_step3]

// [START android_compose_interop_migration_common_scenarios_navigation_step_2]
class SampleActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // setContentView<ActivitySampleBinding>(this, R.layout.activity_sample)
        setContent {
            SampleApp(/* ... */)
        }
    }
}
// [END android_compose_interop_migration_common_scenarios_navigation_step_2]

private object MigrationCommonScenariosNavigationStep3 {
    // [START android_compose_interop_migration_common_scenarios_navigation_step_3]
    @Composable
    fun SampleApp() {
        val navController = rememberNavController()
        // ...
    }
    // [END android_compose_interop_migration_common_scenarios_navigation_step_3]
}

private object MigrationCommonScenariosNavigationStep4 {
    // [START android_compose_interop_migration_common_scenarios_navigation_step_4]
    @Composable
    fun SampleApp() {
        val navController = rememberNavController()

        SampleNavHost(navController = navController)
    }

    @Composable
    fun SampleNavHost(
        navController: NavHostController
    ) {
        NavHost(navController = navController, startDestination = "first") {
            // ...
        }
    }
    // [END android_compose_interop_migration_common_scenarios_navigation_step_4]
}

// [START android_compose_interop_migration_common_scenarios_navigation_step_5]
class FirstFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setContent {
                // FirstScreen(...) EXTRACT FROM HERE
            }
        }
    }
}

@Composable
fun SampleNavHost(
    navController: NavHostController
) {
    NavHost(navController = navController, startDestination = "first") {
        composable("first") {
            FirstScreen(/* ... */) // EXTRACT TO HERE
        }
        composable("second") {
            SecondScreen(/* ... */)
        }
        // ...
    }
}
// [END android_compose_interop_migration_common_scenarios_navigation_step_5]

// [START android_compose_interop_migration_common_scenarios_navigation_step_6]
@Composable
fun FirstScreen(
    // viewModel: FirstViewModel = viewModel(),
    viewModel: FirstViewModel = hiltViewModel(),
    onButtonClick: () -> Unit = {},
) {
    // ...
}
// [END android_compose_interop_migration_common_scenarios_navigation_step_6]

private object MigrationCommonScenariosNavigationStep7 {
    // [START android_compose_interop_migration_common_scenarios_navigation_step_7]
    @Composable
    fun SampleNavHost(
        navController: NavHostController
    ) {
        NavHost(navController = navController, startDestination = "first") {
            composable("first") {
                FirstScreen(
                    onButtonClick = {
                        // findNavController().navigate(firstScreenToSecondScreenAction)
                        navController.navigate("second_screen_route")
                    }
                )
            }
            composable("second") {
                SecondScreen(
                    onIconClick = {
                        // findNavController().navigate(secondScreenToThirdScreenAction)
                        navController.navigate("third_screen_route")
                    }
                )
            }
            // ...
        }
    }
// [END android_compose_interop_migration_common_scenarios_navigation_step_7]
}

/*
Fakes needed for snippets to build:
 */

@Composable
fun SampleApp() {
}

@Composable
fun SecondScreen(
    onIconClick: () -> Unit = {},
) {
}

class FirstViewModel : ViewModel()
data class MyData(
    val name: String
)
