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

package com.example.compose.snippets.components

import androidx.compose.ui.graphics.vector.rememberVectorPainter

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

// [START android_compose_components_themebuilder]
/**
 * Component data model representing a Material 3 catalog item,
 * defined after AndroidX Material 3 Catalog:
 * https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/material3/material3/integration-tests/material3-catalog/src/main/java/androidx/compose/material3/catalog/library/model/Components.kt
 */
data class CatalogComponent(
    val id: Int,
    val name: String,
    val description: String,
    val category: String = "Common",
    val guidelinesUrl: String = "",
    val docsUrl: String = "",
    val sourceUrl: String = "",
    val examples: List<CatalogExample> = emptyList(),
) {
    val hasExpressiveExamples: Boolean
        get() = examples.any { it.isExpressive }
}

data class CatalogExample(
    val name: String,
    val description: String,
    val sourceUrl: String = "",
    val isExpressive: Boolean = false,
)

private const val ComponentGuidelinesUrl = "https://m3.material.io/components"
private const val StyleGuidelinesUrl = "https://m3.material.io/styles"
private const val DocsUrl = "https://developer.android.com/reference/kotlin/androidx/compose/material3"
private const val Material3SourceUrl =
    "https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:compose/material3/material3/src/commonMain/kotlin/androidx/compose/material3"

private var nextCatalogId: Int = 1
private fun nextId(): Int = nextCatalogId.also { nextCatalogId += 1 }

/**
 * Material 3 Catalog component definitions matching AndroidX Components.kt
 */
val Material3CatalogComponents = listOf(
    CatalogComponent(
        id = nextId(),
        name = "Buttons",
        description = "Buttons help people initiate actions, from sending an email, to sharing a document, to liking a post.",
        category = "Actions",
        guidelinesUrl = "$ComponentGuidelinesUrl/buttons",
        docsUrl = "$DocsUrl#button",
        sourceUrl = "$Material3SourceUrl/Button.kt",
        examples = listOf(
            CatalogExample("ButtonSample", "Filled button example"),
            CatalogExample("FilledTonalButtonSample", "Tonal button example"),
            CatalogExample("ElevatedButtonSample", "Elevated button example"),
            CatalogExample("OutlinedButtonSample", "Outlined button example"),
            CatalogExample("TextButtonSample", "Text button example"),
            CatalogExample("ButtonWithIconSample", "Button with leading icon"),
            CatalogExample("ButtonWithAnimatedShapeSample", "Animated shape button", isExpressive = true)
        )
    ),
    CatalogComponent(
        id = nextId(),
        name = "Card",
        description = "Cards contain content and actions that relate information about a subject.",
        category = "Containment",
        guidelinesUrl = "$StyleGuidelinesUrl/cards",
        docsUrl = "$DocsUrl#card",
        sourceUrl = "$Material3SourceUrl/Card.kt",
        examples = listOf(
            CatalogExample("CardSample", "Filled card sample"),
            CatalogExample("ElevatedCardSample", "Elevated card sample"),
            CatalogExample("OutlinedCardSample", "Outlined card sample"),
            CatalogExample("ClickableCardSample", "Clickable card sample")
        )
    ),
    CatalogComponent(
        id = nextId(),
        name = "Chips",
        description = "Chips help people enter information, make selections, filter content, or trigger actions.",
        category = "Selection",
        guidelinesUrl = "$ComponentGuidelinesUrl/chips",
        docsUrl = "$DocsUrl#assistchip",
        sourceUrl = "$Material3SourceUrl/Chip.kt",
        examples = listOf(
            CatalogExample("AssistChipSample", "Assist chip sample"),
            CatalogExample("FilterChipSample", "Filter chip sample"),
            CatalogExample("ElevatedAssistChipSample", "Elevated assist chip sample"),
            CatalogExample("SuggestionChipSample", "Suggestion chip sample"),
            CatalogExample("InputChipSample", "Input chip sample")
        )
    ),
    CatalogComponent(
        id = nextId(),
        name = "Badge",
        description = "A badge can contain dynamic information, such as the presence of a new notification or a number of pending requests.",
        category = "Feedback",
        guidelinesUrl = "$ComponentGuidelinesUrl/badge",
        docsUrl = "$DocsUrl#badge",
        sourceUrl = "$Material3SourceUrl/Badge.kt",
        examples = listOf(
            CatalogExample("NavigationBarItemWithBadge", "Badge on navigation item")
        )
    ),
    CatalogComponent(
        id = nextId(),
        name = "Floating action buttons",
        description = "A floating action button (FAB) represents the primary action of a screen.",
        category = "Actions",
        guidelinesUrl = "$ComponentGuidelinesUrl/floating-action-buttons",
        docsUrl = "$DocsUrl#floatingactionbutton",
        sourceUrl = "$Material3SourceUrl/FloatingActionButton.kt",
        examples = listOf(
            CatalogExample("FloatingActionButtonSample", "Regular FAB"),
            CatalogExample("LargeFloatingActionButtonSample", "Large FAB"),
            CatalogExample("ExtendedFloatingActionButtonSample", "Extended FAB with text"),
            CatalogExample("AnimatedFloatingActionButtonSample", "Animated FAB", isExpressive = true)
        )
    ),
    CatalogComponent(
        id = nextId(),
        name = "Progress indicators",
        description = "Progress indicators inform users about the status of ongoing processes, such as loading an app or submitting a form.",
        category = "Feedback",
        guidelinesUrl = "$ComponentGuidelinesUrl/progress-indicators",
        docsUrl = "$DocsUrl#linearprogressindicator",
        sourceUrl = "$Material3SourceUrl/ProgressIndicator.kt",
        examples = listOf(
            CatalogExample("LinearProgressIndicatorSample", "Linear progress indicator"),
            CatalogExample("CircularProgressIndicatorSample", "Circular progress indicator"),
            CatalogExample("LinearWavyProgressIndicatorSample", "Linear wavy progress", isExpressive = true),
            CatalogExample("CircularWavyProgressIndicatorSample", "Circular wavy progress", isExpressive = true)
        )
    ),
    CatalogComponent(
        id = nextId(),
        name = "Checkboxes",
        description = "Checkboxes allow users to select one or more items from a set.",
        category = "Selection",
        guidelinesUrl = "$ComponentGuidelinesUrl/checkbox",
        docsUrl = "$DocsUrl#checkbox",
        sourceUrl = "$Material3SourceUrl/Checkbox.kt",
        examples = listOf(
            CatalogExample("CheckboxSample", "Standard checkbox"),
            CatalogExample("CheckboxWithTextSample", "Checkbox with text label")
        )
    ),
    CatalogComponent(
        id = nextId(),
        name = "Switches",
        description = "Switches toggle the state of a single item on or off.",
        category = "Selection",
        guidelinesUrl = "$ComponentGuidelinesUrl/switch",
        docsUrl = "$DocsUrl#switch",
        sourceUrl = "$Material3SourceUrl/Switch.kt",
        examples = listOf(
            CatalogExample("SwitchSample", "Standard switch"),
            CatalogExample("SwitchWithThumbIconSample", "Switch with custom thumb icon")
        )
    ),
    CatalogComponent(
        id = nextId(),
        name = "Sliders",
        description = "Sliders allow users to make selections from a range of values.",
        category = "Selection",
        guidelinesUrl = "$ComponentGuidelinesUrl/sliders",
        docsUrl = "$DocsUrl#slider",
        sourceUrl = "$Material3SourceUrl/Slider.kt",
        examples = listOf(
            CatalogExample("SliderSample", "Continuous slider"),
            CatalogExample("StepsSliderSample", "Discrete step slider"),
            CatalogExample("RangeSliderSample", "Range selection slider")
        )
    ),
    CatalogComponent(
        id = nextId(),
        name = "Radio buttons",
        description = "Radio buttons allow users to select one option from a set.",
        category = "Selection",
        guidelinesUrl = "$ComponentGuidelinesUrl/radio-button",
        docsUrl = "$DocsUrl#radiobutton",
        sourceUrl = "$Material3SourceUrl/RadioButton.kt",
        examples = listOf(
            CatalogExample("RadioButtonSample", "Single radio button"),
            CatalogExample("RadioGroupSample", "Radio button group selection")
        )
    ),
    CatalogComponent(
        id = nextId(),
        name = "Segmented Button",
        description = "Segmented buttons help people select options, switch views, or sort elements.",
        category = "Actions",
        guidelinesUrl = "$ComponentGuidelinesUrl/segmented-buttons",
        docsUrl = "$DocsUrl#segmentedbutton",
        sourceUrl = "$Material3SourceUrl/SegmentedButton.kt",
        examples = listOf(
            CatalogExample("SegmentedButtonSingleSelectSample", "Single choice segmented button"),
            CatalogExample("SegmentedButtonMultiSelectSample", "Multi choice segmented button")
        )
    ),
    CatalogComponent(
        id = nextId(),
        name = "Top app bar",
        description = "Top app bars display information and actions relating to the current screen.",
        category = "Navigation",
        guidelinesUrl = "$ComponentGuidelinesUrl/top-app-bars",
        docsUrl = "$DocsUrl#topappbar",
        sourceUrl = "$Material3SourceUrl/AppBar.kt",
        examples = listOf(
            CatalogExample("SimpleTopAppBar", "Center-aligned and small top app bars"),
            CatalogExample("MediumTopAppBar", "Medium collapsible top app bar"),
            CatalogExample("LargeTopAppBar", "Large collapsible top app bar")
        )
    ),
    CatalogComponent(
        id = nextId(),
        name = "Bottom App Bar",
        description = "A bottom app bar displays navigation and key actions at the bottom of mobile screens.",
        category = "Navigation",
        guidelinesUrl = "$ComponentGuidelinesUrl/bottom-app-bars",
        docsUrl = "$DocsUrl#bottomappbar",
        sourceUrl = "$Material3SourceUrl/AppBar.kt",
        examples = listOf(
            CatalogExample("SimpleBottomAppBar", "Standard bottom app bar"),
            CatalogExample("BottomAppBarWithFAB", "Bottom app bar with integrated FAB")
        )
    ),
    CatalogComponent(
        id = nextId(),
        name = "Navigation bar",
        description = "Navigation bars offer convenient navigation between top-level destinations in an app.",
        category = "Navigation",
        guidelinesUrl = "$ComponentGuidelinesUrl/navigation-bar",
        docsUrl = "$DocsUrl#navigationbar",
        sourceUrl = "$Material3SourceUrl/NavigationBar.kt",
        examples = listOf(
            CatalogExample("NavigationBarSample", "Standard navigation bar with icons and labels"),
            CatalogExample("ShortNavigationBarSample", "Compact navigation bar", isExpressive = true)
        )
    )
)

/**
 * Full Material 3 Theme Builder / Catalog Showcase Composable.
 * Renders the component catalog styled with the active MaterialTheme tokens,
 * allowing live inspection of color roles, typography, and component states.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeBuilderPreview() {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var expandedComponentId by remember { mutableIntStateOf(-1) }

    // Sample interactive states
    var switchState by remember { mutableStateOf(true) }
    var checkboxState by remember { mutableStateOf(true) }
    var radioSelected by remember { mutableIntStateOf(0) }
    var sliderValue by remember { mutableFloatStateOf(0.6f) }
    var selectedChipIndex by remember { mutableIntStateOf(0) }

    val categories = remember {
        listOf("All", "Actions", "Containment", "Selection", "Feedback", "Navigation")
    }

    val filteredComponents = remember(searchQuery, selectedCategory) {
        Material3CatalogComponents.filter { comp ->
            val matchesCategory = selectedCategory == "All" || comp.category.equals(selectedCategory, ignoreCase = true)
            val matchesSearch = searchQuery.isBlank() ||
                comp.name.contains(searchQuery, ignoreCase = true) ||
                comp.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesSearch
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Material 3 Catalog", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Text(
                            "Themed Components Preview",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = {}) {
                        Icon(painter = rememberVectorPainter(AppIcons.Menu), contentDescription = "Menu")
                    }
                },
                actions = {
                    BadgedBox(badge = { Badge { Text("${filteredComponents.size}") } }) {
                        Icon(painter = rememberVectorPainter(AppIcons.Notifications), contentDescription = "Notifications")
                    }
                    Spacer(Modifier.width(12.dp))
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Search and Filter Header
            Surface(
                color = MaterialTheme.colorScheme.surfaceContainerLow,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search Material 3 components...") },
                        leadingIcon = { Icon(painter = rememberVectorPainter(AppIcons.Search), contentDescription = null) },
                        trailingIcon = if (searchQuery.isNotEmpty()) {
                            {
                                IconButton(onClick = { searchQuery = "" }) {
                                    Icon(painter = rememberVectorPainter(AppIcons.Close), contentDescription = "Clear")
                                }
                            }
                        } else null,
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(Modifier.height(8.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        items(categories) { cat ->
                            FilterChip(
                                selected = selectedCategory == cat,
                                onClick = { selectedCategory = cat },
                                label = { Text(cat) },
                                leadingIcon = if (selectedCategory == cat) {
                                    { Icon(painter = rememberVectorPainter(AppIcons.Check), contentDescription = null, modifier = Modifier.size(16.dp)) }
                                } else null
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            // Component List
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredComponents) { component ->
                    ElevatedCard(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainerLow
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            // Header: Component name & Category badge
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = component.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                                SuggestionChip(
                                    onClick = {},
                                    label = { Text(component.category, style = MaterialTheme.typography.labelSmall) }
                                )
                            }

                            Spacer(Modifier.height(4.dp))
                            Text(
                                text = component.description,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                maxLines = if (expandedComponentId == component.id) Int.MAX_VALUE else 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(Modifier.height(12.dp))

                            // Interactive Live Themed Preview of the Component
                            Surface(
                                color = MaterialTheme.colorScheme.surface,
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(12.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    when (component.name) {
                                        "Buttons" -> {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(8.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Button(onClick = {}) { Text("Filled") }
                                                FilledTonalButton(onClick = {}) { Text("Tonal") }
                                                OutlinedButton(onClick = {}) { Text("Outlined") }
                                            }
                                        }
                                        "Card" -> {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                Card(
                                                    colors = CardDefaults.cardColors(
                                                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                                    ),
                                                    modifier = Modifier.weight(1f)
                                                ) {
                                                    Column(modifier = Modifier.padding(8.dp)) {
                                                        Text("Primary Card", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                                        Text("Container token", style = MaterialTheme.typography.labelSmall)
                                                    }
                                                }
                                                OutlinedCard(modifier = Modifier.weight(1f)) {
                                                    Column(modifier = Modifier.padding(8.dp)) {
                                                        Text("Outlined Card", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                                                        Text("Surface outline", style = MaterialTheme.typography.labelSmall)
                                                    }
                                                }
                                            }
                                        }
                                        "Chips" -> {
                                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                                listOf("Design", "Tokens", "Themes").forEachIndexed { index, tag ->
                                                    FilterChip(
                                                        selected = selectedChipIndex == index,
                                                        onClick = { selectedChipIndex = index },
                                                        label = { Text(tag) }
                                                    )
                                                }
                                            }
                                        }
                                        "Badge" -> {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(24.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                BadgedBox(badge = { Badge { Text("8") } }) {
                                                    Icon(painter = rememberVectorPainter(AppIcons.Notifications), contentDescription = null)
                                                }
                                                BadgedBox(badge = { Badge { Text("99+") } }) {
                                                    Icon(painter = rememberVectorPainter(AppIcons.Favorite), contentDescription = null)
                                                }
                                                BadgedBox(badge = { Badge() }) {
                                                    Icon(painter = rememberVectorPainter(AppIcons.Info), contentDescription = null)
                                                }
                                            }
                                        }
                                        "Floating action buttons" -> {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                FloatingActionButton(
                                                    onClick = {},
                                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                                ) {
                                                    Icon(painter = rememberVectorPainter(AppIcons.Add), contentDescription = "Add")
                                                }
                                                FloatingActionButton(
                                                    onClick = {},
                                                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                                                    contentColor = MaterialTheme.colorScheme.onTertiaryContainer
                                                ) {
                                                    Icon(painter = rememberVectorPainter(AppIcons.AutoAwesome), contentDescription = "Expressive")
                                                }
                                            }
                                        }
                                        "Progress indicators" -> {
                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(10.dp),
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                modifier = Modifier.fillMaxWidth()
                                            ) {
                                                LinearProgressIndicator(progress = { 0.7f }, modifier = Modifier.fillMaxWidth(0.8f))
                                                CircularProgressIndicator(modifier = Modifier.size(32.dp))
                                            }
                                        }
                                        "Checkboxes" -> {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Checkbox(checked = checkboxState, onCheckedChange = { checkboxState = it })
                                                Spacer(Modifier.width(8.dp))
                                                Text("Checkbox is ${if (checkboxState) "Checked" else "Unchecked"}")
                                            }
                                        }
                                        "Switches" -> {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Switch(checked = switchState, onCheckedChange = { switchState = it })
                                                Spacer(Modifier.width(8.dp))
                                                Text("Switch is ${if (switchState) "ON" else "OFF"}")
                                            }
                                        }
                                        "Sliders" -> {
                                            Column(modifier = Modifier.fillMaxWidth(0.9f)) {
                                                Slider(value = sliderValue, onValueChange = { sliderValue = it })
                                                Text(
                                                    text = "Value: ${(sliderValue * 100).toInt()}%",
                                                    style = MaterialTheme.typography.labelSmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        }
                                        "Radio buttons" -> {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                listOf("Option A", "Option B").forEachIndexed { i, label ->
                                                    Row(
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        modifier = Modifier.clickable { radioSelected = i }
                                                    ) {
                                                        RadioButton(selected = radioSelected == i, onClick = { radioSelected = i })
                                                        Text(label, style = MaterialTheme.typography.bodySmall)
                                                    }
                                                }
                                            }
                                        }
                                        "Segmented Button" -> {
                                            Row(
                                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                listOf("Daily", "Weekly", "Monthly").forEachIndexed { i, lbl ->
                                                    FilterChip(
                                                        selected = radioSelected == i,
                                                        onClick = { radioSelected = i },
                                                        label = { Text(lbl) }
                                                    )
                                                }
                                            }
                                        }
                                        else -> {
                                            Text(
                                                text = "${component.examples.size} samples available",
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.primary
                                            )
                                        }
                                    }
                                }
                            }

                            // Footer: Sample count & Expand details
                            Spacer(Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${component.examples.size} AndroidX catalog examples",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                TextButton(
                                    onClick = {
                                        expandedComponentId = if (expandedComponentId == component.id) -1 else component.id
                                    }
                                ) {
                                    Text(if (expandedComponentId == component.id) "Show Less" else "View Examples")
                                }
                            }

                            // Expanded list of examples
                            if (expandedComponentId == component.id) {
                                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = "Official AndroidX Examples:",
                                        style = MaterialTheme.typography.labelMedium,
                                        fontWeight = FontWeight.Bold
                                    )
                                    component.examples.forEach { ex ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Text("• ${ex.name}", style = MaterialTheme.typography.bodySmall)
                                            if (ex.isExpressive) {
                                                SuggestionChip(
                                                    onClick = {},
                                                    label = { Text("Expressive", fontSize = 10.sp) }
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
// [END android_compose_components_themebuilder]
