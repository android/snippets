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

package com.example.compose.preview.wasm.ui

import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.example.compose.snippets.components.AppIcons

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.catalog.library.model.Theme
import androidx.compose.material3.catalog.library.model.ThemeColorMode
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.preview.wasm.model.ComponentSnippet
import kotlinx.coroutines.delay

/**
 * Dedicated preview and code inspector for a single Composable snippet.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnippetDetailView(
    snippet: ComponentSnippet,
    theme: Theme,
    standalone: Boolean,
    showSidePicker: Boolean,
    onToggleSidePicker: () -> Unit,
    onThemeChange: (Theme) -> Unit,
    onToggleTheme: () -> Unit,
    onBack: () -> Unit
) {
    if (!standalone) {
        // In embedded / non-fullscreen mode, don't show the header toolbar for the snippet.
        // Just render the snippet cleanly.
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            snippet.composable()
        }
        return
    }

    var showCode by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        CenterAlignedTopAppBar(
            title = {
                Text(snippet.title)
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(painter = rememberVectorPainter(AppIcons.ArrowBack), contentDescription = "Back to list")
                }
            },
            actions = {
                IconButton(onClick = { showCode = !showCode }) {
                    Icon(painter = rememberVectorPainter(AppIcons.Code), contentDescription = "Toggle Code")
                }
                IconButton(onClick = onToggleTheme) {
                    Icon(
                        painter = rememberVectorPainter(if (theme.themeColorMode == ThemeColorMode.Dark) AppIcons.Brightness7 else AppIcons.Brightness4),
                        contentDescription = "Toggle Theme"
                    )
                }
                if (standalone) {
                    IconButton(onClick = onToggleSidePicker) {
                        Icon(
                            painter = rememberVectorPainter(AppIcons.Palette),
                            contentDescription = "Theme Options",
                            tint = if (showSidePicker) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            // Main composable preview area
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = snippet.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    if (showCode) {
                        val code = snippet.actualCode
                        @Suppress("DEPRECATION")
                        val clipboardManager = LocalClipboardManager.current
                        var copied by remember { mutableStateOf(false) }

                        LaunchedEffect(copied) {
                            if (copied) {
                                delay(2000)
                                copied = false
                            }
                        }

                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(8.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 4.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${snippet.title} — Source Code",
                                    style = MaterialTheme.typography.titleSmall,
                                    fontWeight = FontWeight.SemiBold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Button(
                                    onClick = {
                                        clipboardManager.setText(AnnotatedString(code))
                                        copied = true
                                    }
                                ) {
                                    Text(if (copied) "Copied!" else "Copy Code")
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Surface(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(16.dp)
                                        .verticalScroll(rememberScrollState())
                                ) {
                                    Text(
                                        text = code,
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            fontFamily = FontFamily.Monospace,
                                            fontSize = 13.sp,
                                            lineHeight = 20.sp
                                        ),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    } else {
                        snippet.composable()
                    }
                }
            }

            // Side Theme Picker Panel in fullscreen mode
            if (standalone && showSidePicker) {
                VerticalDivider(
                    modifier = Modifier.fillMaxHeight(),
                    color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                )
                ThemePickerSidePanel(
                    theme = theme,
                    onThemeChange = onThemeChange,
                    onClose = onToggleSidePicker
                )
            }
        }
    }
}
