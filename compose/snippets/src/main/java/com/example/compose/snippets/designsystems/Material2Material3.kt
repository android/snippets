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

@file:Suppress(
    "unused",
    "UNUSED_VARIABLE",
    "UNUSED_PARAMETER",
    "UnusedMaterial3ScaffoldPaddingParameter",
    "CoroutineCreationDuringComposition"
)
@file:android.annotation.SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter",
    "CoroutineCreationDuringComposition"
)

package com.example.compose.snippets.designsystems

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.DrawerDefaults
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

private object M2M3ExperimentalSnippets {
    // [START android_compose_m2_m3_experimental_api]
    // import androidx.compose.material3.ExperimentalMaterial3Api

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun AppComposable() {
        // M3 composables
    }
    // [END android_compose_m2_m3_experimental_api]
}

private object M2M3ThemingSnippets {
    val AppColorScheme = lightColorScheme()
    val AppTypography = Typography()
    val AppShapes = Shapes()

    @Composable
    fun Theming() {
        // [START android_compose_m2_m3_theme]
        // import androidx.compose.material3.MaterialTheme

        MaterialTheme(
            colorScheme = AppColorScheme,
            typography = AppTypography,
            shapes = AppShapes
        ) {
            // M3 content
        }
        // [END android_compose_m2_m3_theme]
    }
}

private object M2M3ColorSnippets {
    val darkTheme = false

    // [START android_compose_m2_m3_color_scheme]
    // import androidx.compose.material3.lightColorScheme
    // import androidx.compose.material3.darkColorScheme

    val AppLightColorScheme = lightColorScheme(
        // M3 light Color parameters
    )
    val AppDarkColorScheme = darkColorScheme(
        // M3 dark Color parameters
    )
    val AppColorScheme = if (darkTheme) {
        AppDarkColorScheme
    } else {
        AppLightColorScheme
    }
    // [END android_compose_m2_m3_color_scheme]
}

private object M2M3IsLightSnippets {
    // [START android_compose_m2_m3_theme_elevation]
    // import androidx.compose.material3.lightColorScheme
    // import androidx.compose.material3.darkColorScheme
    // import androidx.compose.material3.MaterialTheme

    val LocalCardElevation = staticCompositionLocalOf { Dp.Unspecified }

    @Composable
    private fun AppTheme(
        darkTheme: Boolean = isSystemInDarkTheme(),
        content: @Composable () -> Unit
    ) {
        val cardElevation = if (darkTheme) 4.dp else 0.dp
        CompositionLocalProvider(LocalCardElevation provides cardElevation) {
            val colorScheme = if (darkTheme) darkColorScheme(/* [START_EXCLUDE] */) else lightColorScheme(/* [END_EXCLUDE] */)
            MaterialTheme(
                colorScheme = colorScheme,
                content = content
            )
        }
    }

    @Composable
    fun AppComposable() {
        AppTheme {
            val cardElevation = LocalCardElevation.current
            // [START_EXCLUDE silent]
            cardElevation.toString()
            // [END_EXCLUDE]
            // ...
        }
    }
    // [END android_compose_m2_m3_theme_elevation]
}

private object M2M3TypographySnippets {
    // [START android_compose_m2_m3_typography]
    // import androidx.compose.material3.Typography

    val AppTypography = Typography(
        // M3 TextStyle parameters
    )
    // [END android_compose_m2_m3_typography]
}

private object M2M3ShapesSnippets {
    // [START android_compose_m2_m3_shapes]
    // import androidx.compose.material3.Shapes

    val AppShapes = Shapes(
        // M3 Shape parameters
    )
    // [END android_compose_m2_m3_shapes]
}

private object M2M3ScaffoldSnippets {
    @Composable
    fun ScaffoldBasic() {
        // [START android_compose_m2_m3_scaffold_basic]
        // import androidx.compose.material3.Scaffold

        Scaffold(
            // [START_EXCLUDE silent]
            content = {}
            // [END_EXCLUDE]
            // M3 scaffold parameters
        )
        // [END android_compose_m2_m3_scaffold_basic]
    }

    @Composable
    fun ScaffoldContainerColor() {
        // [START android_compose_m2_m3_scaffold_container_color]
        // import androidx.compose.material3.Scaffold

        Scaffold(
            containerColor = /* [START_EXCLUDE] */ MaterialTheme.colorScheme.background /* [END_EXCLUDE] */,
            content = { /* [START_EXCLUDE] */ /* [END_EXCLUDE] */ }
        )
        // [END android_compose_m2_m3_scaffold_container_color]
    }

    @Composable
    fun ScaffoldSnackbar() {
        // [START android_compose_m2_m3_scaffold_snackbar]
        // import androidx.compose.material3.Scaffold
        // import androidx.compose.material3.SnackbarHost
        // import androidx.compose.material3.SnackbarHostState

        val snackbarHostState = remember { SnackbarHostState() }
        val scope = rememberCoroutineScope()

        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            content = {
                // [START_EXCLUDE silent]
                Text("Content")
                // [END_EXCLUDE]
                // ...
                scope.launch {
                    snackbarHostState.showSnackbar(/* [START_EXCLUDE] */ "Message" /* [END_EXCLUDE] */)
                }
            }
        )
        // [END android_compose_m2_m3_scaffold_snackbar]
    }

    @Composable
    fun NavigationDrawer() {
        // [START android_compose_m2_m3_navigation_drawer]
        // import androidx.compose.material3.DrawerValue
        // import androidx.compose.material3.ModalDrawerSheet
        // import androidx.compose.material3.ModalNavigationDrawer
        // import androidx.compose.material3.Scaffold
        // import androidx.compose.material3.rememberDrawerState

        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerShape = /* [START_EXCLUDE] */ DrawerDefaults.shape /* [END_EXCLUDE] */,
                    drawerTonalElevation = /* [START_EXCLUDE] */ DrawerDefaults.PermanentDrawerElevation /* [END_EXCLUDE] */,
                    drawerContainerColor = /* [START_EXCLUDE] */ DrawerDefaults.modalContainerColor /* [END_EXCLUDE] */,
                    drawerContentColor = /* [START_EXCLUDE] */ DrawerDefaults.modalContainerColor /* [END_EXCLUDE] */,
                    content = { /* [START_EXCLUDE] */ /* [END_EXCLUDE] */ }
                )
            },
            gesturesEnabled = /* [START_EXCLUDE] */ true /* [END_EXCLUDE] */,
            scrimColor = /* [START_EXCLUDE] */ DrawerDefaults.scrimColor /* [END_EXCLUDE] */,
            content = {
                Scaffold(
                    content = {
                        // [START_EXCLUDE silent]
                        Text("Content")
                        // [END_EXCLUDE]
                        // ...
                        scope.launch {
                            drawerState.open()
                        }
                    }
                )
            }
        )
        // [END android_compose_m2_m3_navigation_drawer]
    }
}

private object M2M3TopAppBarSnippets {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopAppBarBasic() {
        // [START android_compose_m2_m3_top_app_bar_basic]
        // import androidx.compose.material3.TopAppBar

        TopAppBar(/* [START_EXCLUDE] */ title = { Text("Title") } /* [END_EXCLUDE] */)
        // [END android_compose_m2_m3_top_app_bar_basic]
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun TopAppBarScrollBehavior() {
        // [START android_compose_m2_m3_top_app_bar_scroll_behavior]
        // import androidx.compose.material3.Scaffold
        // import androidx.compose.material3.TopAppBar
        // import androidx.compose.material3.TopAppBarDefaults

        val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

        Scaffold(
            modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
            topBar = {
                TopAppBar(
                    scrollBehavior = scrollBehavior,
                    /* [START_EXCLUDE] */
                    title = { Text("Title") }
                    /* [END_EXCLUDE] */
                    // ...
                )
            },
            content = {
                LazyColumn {
                    // [START_EXCLUDE silent]
                    items(listOf(1)) { Text("Item $it") }
                    // [END_EXCLUDE]
                    // ...
                }
            }
        )
        // [END android_compose_m2_m3_top_app_bar_scroll_behavior]
    }
}

private object M2M3NavigationBarSnippets {
    @Composable
    fun NavigationBarExample() {
        // [START android_compose_m2_m3_navigation_bar]
        // import androidx.compose.material3.NavigationBar
        // import androidx.compose.material3.NavigationBarItem

        NavigationBar {
            NavigationBarItem(/* [START_EXCLUDE] */ selected = true, onClick = {}, icon = {} /* [END_EXCLUDE] */)
            NavigationBarItem(/* [START_EXCLUDE] */ selected = false, onClick = {}, icon = {} /* [END_EXCLUDE] */)
            NavigationBarItem(/* [START_EXCLUDE] */ selected = false, onClick = {}, icon = {} /* [END_EXCLUDE] */)
        }
        // [END android_compose_m2_m3_navigation_bar]
    }
}

private object M2M3ButtonsSnippets {
    @Composable
    fun ButtonsExample() {
        // [START android_compose_m2_m3_buttons]
        // import androidx.compose.material3.Button
        // import androidx.compose.material3.ExtendedFloatingActionButton
        // import androidx.compose.material3.FloatingActionButton
        // import androidx.compose.material3.IconButton
        // import androidx.compose.material3.IconToggleButton
        // import androidx.compose.material3.OutlinedButton
        // import androidx.compose.material3.TextButton

        // M3 buttons
        Button(/* [START_EXCLUDE] */ onClick = {} /* [END_EXCLUDE] */) { /* [START_EXCLUDE] */ /* [END_EXCLUDE] */ }
        OutlinedButton(/* [START_EXCLUDE] */ onClick = {} /* [END_EXCLUDE] */) { /* [START_EXCLUDE] */ /* [END_EXCLUDE] */ }
        TextButton(/* [START_EXCLUDE] */ onClick = {} /* [END_EXCLUDE] */) { /* [START_EXCLUDE] */ /* [END_EXCLUDE] */ }
        // M3 icon buttons
        IconButton(/* [START_EXCLUDE] */ onClick = {} /* [END_EXCLUDE] */) { /* [START_EXCLUDE] */ /* [END_EXCLUDE] */ }
        IconToggleButton(/* [START_EXCLUDE] */ checked = false, onCheckedChange = {} /* [END_EXCLUDE] */) { /* [START_EXCLUDE] */ /* [END_EXCLUDE] */ }
        // M3 FABs
        FloatingActionButton(/* [START_EXCLUDE] */ onClick = {} /* [END_EXCLUDE] */) { /* [START_EXCLUDE] */ /* [END_EXCLUDE] */ }
        ExtendedFloatingActionButton(/* [START_EXCLUDE] */ onClick = {} /* [END_EXCLUDE] */) { /* [START_EXCLUDE] */ /* [END_EXCLUDE] */ }
        // [END android_compose_m2_m3_buttons]
    }
}

private object M2M3SwitchSnippets {
    @Composable
    fun SwitchExample() {
        // [START android_compose_m2_m3_switch]
        // import androidx.compose.material3.Switch

        Switch(/* [START_EXCLUDE] */ checked = false, onCheckedChange = {} /* [END_EXCLUDE] */)
        // [END android_compose_m2_m3_switch]
    }
}

private object M2M3SurfaceElevationSnippets {
    @Composable
    fun SurfaceElevation() {
        // [START android_compose_m2_m3_surface_elevation]
        // import androidx.compose.material3.Surface

        Surface(
            shadowElevation = /* [START_EXCLUDE] */ 2.dp /* [END_EXCLUDE] */,
            tonalElevation = /* [START_EXCLUDE] */ 2.dp /* [END_EXCLUDE] */
        ) { /* [START_EXCLUDE] */ /* [END_EXCLUDE] */ }
        // [END android_compose_m2_m3_surface_elevation]
    }
}

private object M2M3EmphasisIconSnippets {
    @Composable
    fun EmphasisIcon() {
        // [START android_compose_m2_m3_icon_emphasis]
        // import androidx.compose.material3.LocalContentColor

        // High emphasis
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface) {
            Icon(/* [START_EXCLUDE] */ imageVector = Icons.Default.Edit, contentDescription = null /* [END_EXCLUDE] */)
        }
        // Medium emphasis
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            Icon(/* [START_EXCLUDE] */ imageVector = Icons.Default.Edit, contentDescription = null /* [END_EXCLUDE] */)
        }
        // Disabled emphasis
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)) {
            Icon(/* [START_EXCLUDE] */ imageVector = Icons.Default.Edit, contentDescription = null /* [END_EXCLUDE] */)
        }
        // [END android_compose_m2_m3_icon_emphasis]
    }
}

private object M2M3EmphasisTextSnippets {
    @Composable
    fun EmphasisText() {
        // [START android_compose_m2_m3_text_emphasis]
        // import androidx.compose.material3.LocalContentColor

        // High emphasis
        Text(
            /* [START_EXCLUDE] */
            text = "High emphasis",
            /* [END_EXCLUDE] */
            // ...
            fontWeight = FontWeight.Bold
        )
        // Medium emphasis
        Text(
            /* [START_EXCLUDE] */
            text = "Medium emphasis",
            /* [END_EXCLUDE] */
            // ...
            fontWeight = FontWeight.Normal
        )
        // Disabled emphasis
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)) {
            Text(
                /* [START_EXCLUDE] */
                text = "Disabled emphasis",
                /* [END_EXCLUDE] */
                // ...
                fontWeight = FontWeight.Normal
            )
        }
        // [END android_compose_m2_m3_text_emphasis]
    }
}

private object M2M3BadgeSnippets {
    @Composable
    fun BadgeExample() {
        // [START android_compose_m2_m3_badge_container_color]
        Badge(
            containerColor = MaterialTheme.colorScheme.primary
        ) { /* [START_EXCLUDE] */ /* [END_EXCLUDE] */ }
        // [END android_compose_m2_m3_badge_container_color]
    }
}
