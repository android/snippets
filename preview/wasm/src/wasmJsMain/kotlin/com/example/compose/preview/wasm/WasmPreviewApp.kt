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

package com.example.compose.preview.wasm

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.preview.wasm.model.ComponentCategory
import com.example.compose.preview.wasm.model.ComponentSnippet
import com.example.compose.preview.wasm.registry.SnippetRegistry
import com.example.compose.preview.wasm.theme.AndroidSnippetsTheme
import com.example.compose.preview.wasm.theme.ThemePreset
import com.example.compose.preview.wasm.theme.parseHexColor
import kotlinx.browser.window
import kotlinx.coroutines.delay

private fun extractSnippetId(search: String, hash: String): String? {
    val combined = "$search&$hash"
    return when {
        combined.contains("snippet=") -> combined.substringAfter("snippet=").substringBefore("&")
        hash.startsWith("#/snippet/") -> hash.removePrefix("#/snippet/").substringBefore("&")
        hash.startsWith("#/") && !hash.contains("=") -> hash.removePrefix("#/").substringBefore("&")
        else -> null
    }
}

private fun extractPreset(search: String, hash: String): ThemePreset {
    val combined = "$search&$hash"
    if (combined.contains("preset=")) {
        val p = combined.substringAfter("preset=").substringBefore("&")
        if (p != "custom") {
            ThemePreset.fromKey(p)?.let { return it }
        }
    }
    return ThemePreset.ANDROID_GREEN
}

private fun extractCustomSeed(search: String, hash: String): Color? {
    val combined = "$search&$hash"
    if (combined.contains("seed=")) {
        val rawSeed = combined.substringAfter("seed=").substringBefore("&")
        val s = if (rawSeed.startsWith("%23", ignoreCase = true)) {
            "#" + rawSeed.substring(3)
        } else if (rawSeed.startsWith("#")) {
            rawSeed
        } else {
            "#" + rawSeed
        }
        if (combined.contains("preset=custom") || !combined.contains("preset=")) {
            return parseHexColor(s)
        }
    }
    return null
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WasmPreviewApp() {
    val initialSearch = remember { window.location.search }
    val initialHash = remember { window.location.hash }

    var darkTheme by remember {
        mutableStateOf(initialSearch.contains("theme=dark") || initialHash.contains("theme=dark"))
    }
    var selectedPreset by remember {
        mutableStateOf(extractPreset(initialSearch, initialHash))
    }
    var customSeed by remember {
        mutableStateOf(extractCustomSeed(initialSearch, initialHash))
    }
    var showSidePicker by remember { mutableStateOf(true) }
    var selectedSnippetId by remember {
        mutableStateOf(extractSnippetId(initialSearch, initialHash))
    }
    var standaloneMode by remember {
        mutableStateOf(initialSearch.contains("standalone=true") || initialHash.contains("standalone=true"))
    }

    // Read URL parameters on startup or hashchange
    fun parseUrlParams() {
        val search = window.location.search
        val hash = window.location.hash
        val combined = "$search&$hash"

        val snippetId = extractSnippetId(search, hash)
        if (!snippetId.isNullOrBlank()) {
            selectedSnippetId = snippetId
        }

        if (combined.contains("theme=dark")) {
            darkTheme = true
        } else if (combined.contains("theme=light")) {
            darkTheme = false
        }

        if (combined.contains("preset=")) {
            val p = combined.substringAfter("preset=").substringBefore("&")
            if (p != "custom") {
                ThemePreset.fromKey(p)?.let {
                    selectedPreset = it
                    customSeed = null
                }
            }
        }

        if (combined.contains("seed=")) {
            val rawSeed = combined.substringAfter("seed=").substringBefore("&")
            val s = if (rawSeed.startsWith("%23", ignoreCase = true)) {
                "#" + rawSeed.substring(3)
            } else if (rawSeed.startsWith("#")) {
                rawSeed
            } else {
                "#" + rawSeed
            }
            if (combined.contains("preset=custom") || !combined.contains("preset=")) {
                parseHexColor(s)?.let {
                    customSeed = it
                }
            }
        }

        if (combined.contains("standalone=true")) {
            standaloneMode = true
        } else if (combined.contains("standalone=false")) {
            standaloneMode = false
        }
    }

    // Polling loop to detect hash or search changes across browsers safely without SAM conversion issues
    LaunchedEffect(Unit) {
        var lastHash = window.location.hash
        var lastSearch = window.location.search
        while (true) {
            delay(100)
            val currentHash = window.location.hash
            val currentSearch = window.location.search
            if (currentHash != lastHash || currentSearch != lastSearch) {
                lastHash = currentHash
                lastSearch = currentSearch
                parseUrlParams()
            }
        }
    }

    AndroidSnippetsTheme(
        darkTheme = darkTheme,
        preset = selectedPreset,
        customSeed = customSeed
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            val currentSnippet = selectedSnippetId?.let { SnippetRegistry.getById(it) }

            if (currentSnippet != null) {
                // Render specific snippet view
                SnippetDetailView(
                    snippet = currentSnippet,
                    darkTheme = darkTheme,
                    standalone = standaloneMode,
                    selectedPreset = selectedPreset,
                    customSeed = customSeed,
                    showSidePicker = showSidePicker,
                    onToggleSidePicker = { showSidePicker = !showSidePicker },
                    onSelectPreset = {
                        selectedPreset = it
                        customSeed = null
                    },
                    onSelectCustomSeed = { customSeed = it },
                    onToggleTheme = { darkTheme = !darkTheme },
                    onBack = {
                        selectedSnippetId = null
                        window.location.hash = ""
                    }
                )
            } else {
                // Render catalog / overview view
                SnippetCatalogView(
                    darkTheme = darkTheme,
                    onToggleTheme = { darkTheme = !darkTheme },
                    onSelectSnippet = { snippet ->
                        selectedSnippetId = snippet.id
                        window.location.hash = "/snippet/${snippet.id}"
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnippetDetailView(
    snippet: ComponentSnippet,
    darkTheme: Boolean,
    standalone: Boolean,
    selectedPreset: ThemePreset,
    customSeed: Color?,
    showSidePicker: Boolean,
    onToggleSidePicker: () -> Unit,
    onSelectPreset: (ThemePreset) -> Unit,
    onSelectCustomSeed: (Color?) -> Unit,
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
                    Icon(Icons.Filled.ArrowBack, contentDescription = "Back to list")
                }
            },
            actions = {
                IconButton(onClick = { showCode = !showCode }) {
                    Icon(Icons.Filled.Code, contentDescription = "Toggle Code")
                }
                IconButton(onClick = onToggleTheme) {
                    Icon(
                        if (darkTheme) Icons.Filled.Brightness7 else Icons.Filled.Brightness4,
                        contentDescription = "Toggle Theme"
                    )
                }
                if (standalone) {
                    IconButton(onClick = onToggleSidePicker) {
                        Icon(
                            Icons.Filled.Palette,
                            contentDescription = "Theme Builder",
                            tint = if (showSidePicker) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
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
                    selectedPreset = selectedPreset,
                    customSeed = customSeed,
                    darkTheme = darkTheme,
                    onSelectPreset = onSelectPreset,
                    onSelectCustomSeed = onSelectCustomSeed,
                    onToggleTheme = onToggleTheme,
                    onClose = onToggleSidePicker
                )
            }
        }
    }
}

@Composable
fun ThemePickerSidePanel(
    selectedPreset: ThemePreset,
    customSeed: Color?,
    darkTheme: Boolean,
    onSelectPreset: (ThemePreset) -> Unit,
    onSelectCustomSeed: (Color?) -> Unit,
    onToggleTheme: () -> Unit,
    onClose: () -> Unit
) {
    var hexInput by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    Surface(
        modifier = Modifier
            .width(280.dp)
            .fillMaxHeight(),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f),
        tonalElevation = 1.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Panel Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        Icons.Filled.Palette,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Column {
                        Text(
                            text = "Theme Builder",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Material 3 Colors",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                IconButton(onClick = onClose) {
                    Icon(Icons.Filled.Close, contentDescription = "Close Theme Panel")
                }
            }

            HorizontalDivider()

            // Mode Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (darkTheme) "Dark Mode" else "Light Mode",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium
                )
                IconButton(onClick = onToggleTheme) {
                    Icon(
                        if (darkTheme) Icons.Filled.Brightness7 else Icons.Filled.Brightness4,
                        contentDescription = "Toggle Dark Mode"
                    )
                }
            }

            HorizontalDivider()

            // Presets List
            Text(
                text = "PRESET PALETTES",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ThemePreset.entries.forEach { preset ->
                    val isSelected = customSeed == null && selectedPreset == preset
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onSelectPreset(preset)
                                onSelectCustomSeed(null)
                            },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surface
                            }
                        ),
                        border = if (isSelected) {
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        } else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(22.dp)
                                        .background(preset.seedColor, CircleShape)
                                        .border(1.dp, Color.White.copy(alpha = 0.5f), CircleShape)
                                )
                                Text(
                                    text = preset.displayName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) {
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    } else {
                                        MaterialTheme.colorScheme.onSurface
                                    }
                                )
                            }
                            if (isSelected) {
                                Icon(
                                    Icons.Filled.Check,
                                    contentDescription = "Active",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider()

            // Custom Seed Hex Input
            Text(
                text = "CUSTOM SEED COLOR",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = hexInput,
                    onValueChange = { hexInput = it },
                    placeholder = { Text("#FF5722") },
                    singleLine = true,
                    modifier = Modifier.weight(1f),
                    textStyle = MaterialTheme.typography.bodyMedium
                )
                Button(
                    onClick = {
                        val parsed = parseHexColor(hexInput)
                        if (parsed != null) {
                            onSelectCustomSeed(parsed)
                        }
                    },
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Apply")
                }
            }

            HorizontalDivider()

            // Active Scheme Tokens
            Text(
                text = "ACTIVE SCHEME TOKENS",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ColorTokenPreview(label = "Pri", color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                ColorTokenPreview(label = "Sec", color = MaterialTheme.colorScheme.secondary, modifier = Modifier.weight(1f))
                ColorTokenPreview(label = "Ter", color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.weight(1f))
                ColorTokenPreview(label = "Cnt", color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.weight(1f))
                ColorTokenPreview(label = "Srf", color = MaterialTheme.colorScheme.surface, modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun ColorTokenPreview(label: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp)
                .background(color, RoundedCornerShape(4.dp))
                .border(1.dp, MaterialTheme.colorScheme.outlineVariant, RoundedCornerShape(4.dp))
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 9.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SnippetCatalogView(
    darkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onSelectSnippet: (ComponentSnippet) -> Unit
) {
    var selectedCategory by remember { mutableStateOf<ComponentCategory?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    val filteredSnippets = remember(selectedCategory, searchQuery) {
        SnippetRegistry.allSnippets.filter { snippet ->
            val matchesCategory = selectedCategory == null || snippet.category == selectedCategory
            val matchesSearch = searchQuery.isBlank() ||
                snippet.title.contains(searchQuery, ignoreCase = true) ||
                snippet.description.contains(searchQuery, ignoreCase = true) ||
                snippet.tags.any { it.contains(searchQuery, ignoreCase = true) }
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Compose Material Previews (WASM)") },
                actions = {
                    IconButton(onClick = onToggleTheme) {
                        Icon(
                            if (darkTheme) Icons.Filled.Brightness7 else Icons.Filled.Brightness4,
                            contentDescription = "Toggle Theme"
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Search Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text("Search Material components...") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Category Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = selectedCategory == null,
                    onClick = { selectedCategory = null },
                    label = { Text("All") }
                )
                ComponentCategory.entries.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = {
                            selectedCategory = if (selectedCategory == category) null else category
                        },
                        label = { Text(category.displayName) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Component List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredSnippets) { snippet ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectSnippet(snippet) },
                        shape = RoundedCornerShape(12.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = snippet.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = snippet.category.displayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            Text(
                                text = snippet.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }
        }
    }
}
