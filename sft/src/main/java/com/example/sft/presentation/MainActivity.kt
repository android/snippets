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

package com.example.sft.presentation

import android.Manifest
import android.app.Activity
import android.health.connect.HealthPermissions
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.wear.compose.foundation.lazy.TransformingLazyColumn
import androidx.wear.compose.foundation.lazy.items
import androidx.wear.compose.foundation.lazy.rememberTransformingLazyColumnState
import androidx.wear.compose.material3.AppScaffold
import androidx.wear.compose.material3.Button
import androidx.wear.compose.material3.ButtonDefaults
import androidx.wear.compose.material3.ListHeader
import androidx.wear.compose.material3.MaterialTheme
import androidx.wear.compose.material3.ScreenScaffold
import androidx.wear.compose.material3.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.sft.R
import com.example.sft.presentation.theme.WearAppTheme
import com.google.android.horologist.compose.layout.ColumnItemType
import com.google.android.horologist.compose.layout.rememberResponsiveColumnPadding

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: SFTExerciseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = SFTExerciseViewModel(this)
        setContent {
            WearApp()
        }
    }

    override fun onStart() {
        super.onStart()
        viewModel.showActiveSessionIfExists(this)
    }


    @Composable
    fun WearApp() {

        val navController = rememberSwipeDismissableNavController()

        WearAppTheme {
            AppScaffold {
                SwipeDismissableNavHost(navController = navController, startDestination = "menu") {
                    composable("menu") {
                        ExerciseSelectionScreen(
                            //  viewModel = viewModel,
                        )
                    }
                    composable("settings") {
                        SettingsScreen()
                    }
                }
            }
        }
    }

    @Composable
    fun ExerciseSelectionScreen(
        modifier: Modifier = Modifier,
    ) {
        val context = LocalContext.current
        val exerciseRepository = remember { ExerciseRepository() }
        var exercises by remember { mutableStateOf<List<ExerciseRepository.Exercise>>(emptyList()) }
        var permissionsGranted by remember { mutableStateOf(false) }


        val permissionLauncher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions.all { it.value }) {
                permissionsGranted = true
            } else {
                // Handle permission denial
            }
        }


        LaunchedEffect(Unit) {
            exercises = exerciseRepository.exercises
            val permissions = mutableListOf(
                Manifest.permission.BODY_SENSORS,
                Manifest.permission.ACTIVITY_RECOGNITION,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
            ).apply {
                //Granular heart rate permission for Wear 6+
                if (Build.VERSION.SDK_INT >= 36) {
                    this.add(HealthPermissions.READ_HEART_RATE)
                }
            }.toTypedArray()
            permissionLauncher.launch(permissions)
        }

        val scrollState = rememberTransformingLazyColumnState()
        val contentPadding = rememberResponsiveColumnPadding(
            first = ColumnItemType.ListHeader,
            last = ColumnItemType.Button
        )

        ScreenScaffold(
            scrollState = scrollState,
            contentPadding = contentPadding
        ) { contentPadding ->
            if (permissionsGranted) {
                TransformingLazyColumn(
                    state = scrollState,
                    contentPadding = contentPadding,
                    modifier = Modifier.fillMaxSize()
                ) {
                    item {
                        ListHeader { Text(text = "Workouts") }
                    }
                    items(exercises) { exercise ->
                        Button(
                            label = {
                                Text(
                                    text = context.getString(exercise.displayName),
                                    modifier = modifier.fillMaxWidth()
                                )
                            },
                            onClick = {
                                viewModel.newSystemRenderedSession(
                                    exerciseType = exercise.exerciseType,
                                    exerciseIcon = exercise.icon,
                                    exerciseName = exercise.displayName,
                                    topMetricRecord = exercise.metricTypes,
                                    activity = (context as? Activity)
                                )
                            },
                            modifier = modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Button(
                            label = {
                                Text(
                                    text = stringResource(R.string.settings),
                                    modifier = modifier.fillMaxWidth()
                                )
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                contentColor = MaterialTheme.colorScheme.onSurface,
                            ),
                            onClick = { },
                            modifier = modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Permissions are required to use this app.",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }

    @Composable
    fun SettingsScreen(modifier: Modifier = Modifier) {
        val listState = rememberTransformingLazyColumnState()
        ScreenScaffold(scrollState = listState) {
            TransformingLazyColumn(
                state = listState
            ) {
                item {
                    ListHeader(modifier = modifier.fillMaxSize()) { Text(text = "Settings") }
                }
                item {
                    Button(
                        label = {
                            Text(
                                text = stringResource(R.string.measureUnits),
                                modifier = modifier.fillMaxWidth()
                            )
                        },
                        onClick = { },
                        modifier = modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}