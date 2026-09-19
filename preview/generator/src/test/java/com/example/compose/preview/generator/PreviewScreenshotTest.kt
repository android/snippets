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

package com.example.compose.preview.generator

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.takahirom.roborazzi.captureRoboImage
import java.io.File
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

val AndroidGreen = Color(0xFF3DDC84)
val AndroidGreenDark = Color(0xFF006D3B)
val AndroidGreenContainer = Color(0xFFC7F3D6)
val AndroidBlue = Color(0xFF4285F4)
val AndroidBlueDark = Color(0xFF00639B)
val AndroidBlueContainer = Color(0xFFCDE5FF)

private val PreviewColorScheme = lightColorScheme(
    primary = AndroidGreenDark,
    onPrimary = Color.White,
    primaryContainer = AndroidGreenContainer,
    onPrimaryContainer = AndroidGreenDark,
    secondary = AndroidBlueDark,
    onSecondary = Color.White,
    secondaryContainer = AndroidBlueContainer,
    onSecondaryContainer = AndroidBlueDark,
    background = Color(0xFFF8FAF9),
    surface = Color.White,
    surfaceVariant = Color(0xFFF1F5F2)
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [36], qualifiers = "w480dp-h270dp-xxxhdpi")
class PreviewScreenshotTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private fun getOutputDir(): File {
        var dir: File? = File(".").canonicalFile
        while (dir != null && !File(dir, "settings.gradle.kts").exists()) {
            dir = dir.parentFile
        }
        val root = dir ?: File(".")
        val screenshotsDir = File(root, "preview/wasm/src/wasmJsMain/resources/screenshots")
        screenshotsDir.mkdirs()
        return screenshotsDir
    }

    private fun captureComponent(id: String, content: @Composable () -> Unit) {
        val outputDir = getOutputDir()
        composeTestRule.setContent {
            MaterialTheme(colorScheme = PreviewColorScheme) {
                Box(
                    modifier = Modifier
                        .size(480.dp, 270.dp)
                        .background(Color(0xFFF8FAF9))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    content()
                }
            }
        }
        composeTestRule.onRoot().captureRoboImage(
            filePath = File(outputDir, "$id.png").absolutePath
        )
    }

    @Test
    fun testButtonExamples() = captureComponent("button-examples") {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                Button(onClick = {}, shapes = ButtonDefaults.shapes()) { Text("Filled") }
                FilledTonalButton(onClick = {}, shapes = ButtonDefaults.shapes()) { Text("Tonal") }
                ElevatedButton(onClick = {}, shapes = ButtonDefaults.shapes()) { Text("Elevated") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedButton(onClick = {}, shapes = ButtonDefaults.shapes()) { Text("Outlined") }
                TextButton(onClick = {}, shapes = ButtonDefaults.shapes()) { Text("Text") }
                Button(onClick = {}, shape = ButtonDefaults.squareShape) { Text("Square") }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                SplitButtonLayout(
                    leadingButton = {
                        SplitButtonDefaults.LeadingButton(onClick = {}) {
                            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize))
                            Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                            Text("Edit")
                        }
                    },
                    trailingButton = {
                        SplitButtonDefaults.TrailingButton(checked = false, onCheckedChange = {}) {
                            Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(SplitButtonDefaults.TrailingIconSize))
                        }
                    }
                )
                ButtonGroup(
                    overflowIndicator = { menuState -> ButtonGroupDefaults.OverflowIndicator(menuState) }
                ) {
                    toggleableItem(checked = true, label = "Day", onCheckedChange = {})
                    toggleableItem(checked = false, label = "Week", onCheckedChange = {})
                }
            }
        }
    }

    @Test
    fun testFloatingActionButton() = captureComponent("floating-action-button") {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            HorizontalFloatingToolbar(
                expanded = true,
                floatingActionButton = {
                    FloatingToolbarDefaults.VibrantFloatingActionButton(onClick = {}) {
                        Icon(Icons.Default.Add, contentDescription = "Add")
                    }
                },
                content = {
                    IconButton(onClick = {}) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
                    IconButton(onClick = {}) { Icon(Icons.Default.Favorite, contentDescription = "Favorite") }
                    IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, contentDescription = "More") }
                }
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SmallFloatingActionButton(onClick = {}) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
                FloatingActionButton(onClick = {}) {
                    Icon(Icons.Default.Edit, contentDescription = "Edit")
                }
                ExtendedFloatingActionButton(
                    onClick = {},
                    icon = { Icon(Icons.Default.Add, null) },
                    text = { Text("Extended") }
                )
            }
        }
    }

    @Test
    fun testSingleChoiceSegmentedButton() = captureComponent("single-choice-segmented-button") {
        SingleChoiceSegmentedButtonRow {
            SegmentedButton(selected = true, onClick = {}, shape = SegmentedButtonDefaults.itemShape(0, 3)) { Text("Day") }
            SegmentedButton(selected = false, onClick = {}, shape = SegmentedButtonDefaults.itemShape(1, 3)) { Text("Month") }
            SegmentedButton(selected = false, onClick = {}, shape = SegmentedButtonDefaults.itemShape(2, 3)) { Text("Week") }
        }
    }

    @Test
    fun testMultiChoiceSegmentedButton() = captureComponent("multi-choice-segmented-button") {
        MultiChoiceSegmentedButtonRow {
            SegmentedButton(
                checked = true,
                onCheckedChange = {},
                shape = SegmentedButtonDefaults.itemShape(0, 3),
                icon = { SegmentedButtonDefaults.Icon(true) }
            ) { Text("Walk") }
            SegmentedButton(
                checked = false,
                onCheckedChange = {},
                shape = SegmentedButtonDefaults.itemShape(1, 3),
                icon = { SegmentedButtonDefaults.Icon(false) }
            ) { Text("Ride") }
            SegmentedButton(
                checked = false,
                onCheckedChange = {},
                shape = SegmentedButtonDefaults.itemShape(2, 3),
                icon = { SegmentedButtonDefaults.Icon(false) }
            ) { Text("Drive") }
        }
    }

    @Test
    fun testSegmentedButton() = captureComponent("segmented-button") {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Single Choice", fontSize = 13.sp, color = AndroidGreenDark, fontWeight = FontWeight.SemiBold)
            SingleChoiceSegmentedButtonRow {
                SegmentedButton(selected = true, onClick = {}, shape = SegmentedButtonDefaults.itemShape(0, 3)) { Text("Day") }
                SegmentedButton(selected = false, onClick = {}, shape = SegmentedButtonDefaults.itemShape(1, 3)) { Text("Week") }
                SegmentedButton(selected = false, onClick = {}, shape = SegmentedButtonDefaults.itemShape(2, 3)) { Text("Month") }
            }
            Text("Multi Choice", fontSize = 13.sp, color = AndroidBlueDark, fontWeight = FontWeight.SemiBold)
            MultiChoiceSegmentedButtonRow {
                SegmentedButton(checked = true, onCheckedChange = {}, shape = SegmentedButtonDefaults.itemShape(0, 2)) { Text("Walking") }
                SegmentedButton(checked = false, onCheckedChange = {}, shape = SegmentedButtonDefaults.itemShape(1, 2)) { Text("Transit") }
            }
        }
    }

    @Test
    fun testFilledButton() = captureComponent("filled-button") {
        Button(onClick = {}, shapes = ButtonDefaults.shapes()) { Text("Filled Button") }
    }

    @Test
    fun testFilledTonalButton() = captureComponent("filled-tonal-button") {
        FilledTonalButton(onClick = {}, shapes = ButtonDefaults.shapes()) { Text("Filled Tonal Button") }
    }

    @Test
    fun testElevatedButton() = captureComponent("elevated-button") {
        ElevatedButton(onClick = {}, shapes = ButtonDefaults.shapes()) { Text("Elevated Button") }
    }

    @Test
    fun testOutlinedButton() = captureComponent("outlined-button") {
        OutlinedButton(onClick = {}, shapes = ButtonDefaults.shapes()) { Text("Outlined Button") }
    }

    @Test
    fun testTextButton() = captureComponent("text-button") {
        TextButton(onClick = {}, shapes = ButtonDefaults.shapes()) { Text("Text Button") }
    }

    @Test
    fun testButtonWithAnimatedShape() = captureComponent("button-with-animated-shape") {
        Button(onClick = {}, shapes = ButtonDefaults.shapes()) { Text("Animated Shape") }
    }

    @Test
    fun testSquareButton() = captureComponent("square-button") {
        Button(onClick = {}, shape = ButtonDefaults.squareShape) { Text("Square Button") }
    }

    @Test
    fun testButtonWithIcon() = captureComponent("button-with-icon") {
        val size = ButtonDefaults.MediumContainerHeight
        Button(
            onClick = {},
            modifier = Modifier.heightIn(size),
            contentPadding = ButtonDefaults.contentPaddingFor(size, hasStartIcon = true)
        ) {
            Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(ButtonDefaults.iconSizeFor(size)))
            Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(size)))
            Text("Edit", style = ButtonDefaults.textStyleFor(size))
        }
    }

    @Test
    fun testSplitButton() = captureComponent("split-button") {
        SplitButtonLayout(
            leadingButton = {
                SplitButtonDefaults.LeadingButton(onClick = {}) {
                    Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(SplitButtonDefaults.LeadingIconSize))
                    Spacer(Modifier.size(ButtonDefaults.IconSpacing))
                    Text("Edit")
                }
            },
            trailingButton = {
                SplitButtonDefaults.TrailingButton(checked = false, onCheckedChange = {}) {
                    Icon(Icons.Default.KeyboardArrowDown, contentDescription = null, modifier = Modifier.size(SplitButtonDefaults.TrailingIconSize))
                }
            }
        )
    }

    @Test
    fun testButtonGroup() = captureComponent("button-group") {
        ButtonGroup(
            overflowIndicator = { menuState -> ButtonGroupDefaults.OverflowIndicator(menuState) }
        ) {
            toggleableItem(checked = true, label = "Day", onCheckedChange = {})
            toggleableItem(checked = false, label = "Week", onCheckedChange = {})
            toggleableItem(checked = false, label = "Month", onCheckedChange = {})
        }
    }

    @Test
    fun testFab() = captureComponent("fab") {
        FloatingActionButton(onClick = {}) {
            Icon(Icons.Default.Add, contentDescription = "Floating action button")
        }
    }

    @Test
    fun testExtendedFab() = captureComponent("extended-fab") {
        ExtendedFloatingActionButton(
            onClick = {},
            icon = { Icon(Icons.Default.Edit, null) },
            text = { Text("Extended FAB") }
        )
    }

    @Test
    fun testSmallFab() = captureComponent("small-fab") {
        SmallFloatingActionButton(onClick = {}) {
            Icon(Icons.Default.Add, contentDescription = "Small FAB")
        }
    }

    @Test
    fun testLargeFab() = captureComponent("large-fab") {
        LargeFloatingActionButton(onClick = {}) {
            Icon(Icons.Default.Add, contentDescription = "Large FAB")
        }
    }

    @Test
    fun testFloatingToolbar() = captureComponent("floating-toolbar") {
        HorizontalFloatingToolbar(
            expanded = true,
            floatingActionButton = {
                FloatingToolbarDefaults.VibrantFloatingActionButton(onClick = {}) {
                    Icon(Icons.Default.Add, contentDescription = "Add")
                }
            },
            content = {
                IconButton(onClick = {}) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
                IconButton(onClick = {}) { Icon(Icons.Default.Favorite, contentDescription = "Favorite") }
                IconButton(onClick = {}) { Icon(Icons.Default.MoreVert, contentDescription = "More") }
            }
        )
    }

    @Test
    fun testBadgeExamples() = captureComponent("badge-examples") {
        Row(horizontalArrangement = Arrangement.spacedBy(36.dp), verticalAlignment = Alignment.CenterVertically) {
            BadgedBox(badge = { Badge { Text("8") } }) {
                Icon(Icons.Default.Mail, contentDescription = "Mail", modifier = Modifier.size(36.dp), tint = AndroidGreenDark)
            }
            BadgedBox(badge = { Badge { Text("99+") } }) {
                Icon(Icons.Default.Notifications, contentDescription = "Notifications", modifier = Modifier.size(36.dp), tint = AndroidGreenDark)
            }
            BadgedBox(badge = { Badge() }) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Cart", modifier = Modifier.size(36.dp), tint = AndroidGreenDark)
            }
        }
    }

    @Test
    fun testProgressIndicator() = captureComponent("progress-indicator") {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(24.dp), verticalAlignment = Alignment.CenterVertically) {
                LoadingIndicator(modifier = Modifier.size(36.dp))
                ContainedLoadingIndicator()
                CircularProgressIndicator(progress = { 0.7f }, modifier = Modifier.size(36.dp))
            }
            LinearProgressIndicator(progress = { 0.65f }, modifier = Modifier.fillMaxWidth())
        }
    }

    @Test
    fun testIndeterminateProgressIndicator() = captureComponent("indeterminate-progress-indicator") {
        CircularProgressIndicator()
    }

    @Test
    fun testDeterminateProgressIndicator() = captureComponent("determinate-progress-indicator") {
        LinearProgressIndicator(progress = { 0.7f }, modifier = Modifier.fillMaxWidth(0.8f))
    }

    @Test
    fun testLoadingIndicator() = captureComponent("loading-indicator") {
        LoadingIndicator(modifier = Modifier.size(48.dp))
    }

    @Test
    fun testContainedLoadingIndicator() = captureComponent("contained-loading-indicator") {
        ContainedLoadingIndicator()
    }

    @Test
    fun testTooltipExamples() = captureComponent("tooltip-examples") {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 6.dp,
            modifier = Modifier.fillMaxWidth(0.85f)
        ) {
            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Info, contentDescription = null, tint = AndroidGreenDark, modifier = Modifier.size(32.dp))
                Spacer(Modifier.width(16.dp))
                Column {
                    Text("Plain & Rich Tooltips", fontWeight = FontWeight.Bold, color = AndroidGreenDark, fontSize = 15.sp)
                    Spacer(Modifier.height(4.dp))
                    Text("Provides contextual help & descriptions on hover or long press.", fontSize = 13.sp, color = Color.Gray)
                }
            }
        }
    }

    @Test
    fun testBottomSheet() = captureComponent("bottom-sheet") {
        Surface(
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {
            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(40.dp, 4.dp).background(Color.LightGray, RoundedCornerShape(2.dp)))
                Spacer(Modifier.height(16.dp))
                Text("Modal Bottom Sheet", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(6.dp))
                Text("Presents auxiliary actions and content over the screen.", fontSize = 13.sp, color = Color.Gray)
                Spacer(Modifier.height(16.dp))
                Button(onClick = {}) { Text("Close Sheet") }
            }
        }
    }

    @Test
    fun testCardExamples() = captureComponent("card-examples") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Card(Modifier.size(130.dp, 160.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text("Filled Card", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Surface tint style card.", fontSize = 12.sp, color = Color.Gray)
                }
            }
            ElevatedCard(Modifier.size(130.dp, 160.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text("Elevated", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = AndroidGreenDark)
                    Spacer(Modifier.height(6.dp))
                    Text("Shadow depth emphasis.", fontSize = 12.sp, color = Color.Gray)
                }
            }
            OutlinedCard(Modifier.size(130.dp, 160.dp)) {
                Column(Modifier.padding(12.dp)) {
                    Text("Outlined", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Spacer(Modifier.height(6.dp))
                    Text("Distinct border line style.", fontSize = 12.sp, color = Color.Gray)
                }
            }
        }
    }

    @Test
    fun testCarouselExamples() = captureComponent("carousel-examples") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(3) { i ->
                Card(
                    modifier = Modifier.size(130.dp, 170.dp),
                    colors = CardDefaults.cardColors(containerColor = if (i == 0) AndroidGreenContainer else MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(if (i == 0) Icons.Default.Star else Icons.Default.Favorite, null, tint = AndroidGreenDark)
                            Spacer(Modifier.height(8.dp))
                            Text("Card ${i + 1}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun testDialogExamples() = captureComponent("dialog-examples") {
        AlertDialog(
            onDismissRequest = {},
            icon = { Icon(Icons.Default.Info, null, tint = AndroidGreenDark) },
            title = { Text("Confirm Action") },
            text = { Text("Material 3 dialog with Android Green accent theme.") },
            confirmButton = { TextButton(onClick = {}) { Text("Confirm") } },
            dismissButton = { TextButton(onClick = {}) { Text("Dismiss") } }
        )
    }

    @Test
    fun testDividerExamples() = captureComponent("divider-examples") {
        Column(Modifier.fillMaxWidth(0.85f), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text("Horizontal Divider (2 dp)", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            HorizontalDivider(thickness = 2.dp, color = AndroidGreenDark)
            Text("Subtle Divider (1 dp)", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            HorizontalDivider(thickness = 1.dp)
            Spacer(Modifier.height(6.dp))
            Row(Modifier.height(50.dp), horizontalArrangement = Arrangement.spacedBy(16.dp), verticalAlignment = Alignment.CenterVertically) {
                Text("Left")
                VerticalDivider(thickness = 2.dp, color = AndroidBlueDark)
                Text("Center")
                VerticalDivider(thickness = 1.dp)
                Text("Right")
            }
        }
    }

    @Test
    fun testScaffoldExample() = captureComponent("scaffold-example") {
        Scaffold(
            modifier = Modifier.size(400.dp, 220.dp),
            topBar = {
                TopAppBar(
                    title = { Text("Scaffold TopBar", fontSize = 15.sp) },
                    navigationIcon = { Icon(Icons.Default.Menu, null, Modifier.padding(start = 8.dp)) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = AndroidGreenContainer)
                )
            },
            floatingActionButton = {
                FloatingActionButton(onClick = {}, modifier = Modifier.size(42.dp)) {
                    Icon(Icons.Default.Add, null)
                }
            },
            bottomBar = {
                NavigationBar(Modifier.height(50.dp)) {
                    NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home", fontSize = 10.sp) })
                    NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile", fontSize = 10.sp) })
                }
            }
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize().padding(12.dp)) {
                Text("Scaffold Content Body Area", fontSize = 13.sp, color = Color.Gray)
            }
        }
    }

    @Test
    fun testAppBarExamples() = captureComponent("app-bar-examples") {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth(0.9f)) {
            CenterAlignedTopAppBar(
                title = { Text("Center Aligned", fontSize = 15.sp) },
                navigationIcon = { IconButton(onClick = {}) { Icon(Icons.Default.ArrowBack, null) } },
                actions = { IconButton(onClick = {}) { Icon(Icons.Default.Settings, null) } }
            )
            TopAppBar(
                title = { Text("Small Top Bar", fontSize = 15.sp) },
                navigationIcon = { IconButton(onClick = {}) { Icon(Icons.Default.Menu, null) } },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = AndroidGreenContainer)
            )
        }
    }

    @Test
    fun testNavigationExamples() = captureComponent("navigation-examples") {
        Column(verticalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth(0.9f)) {
            NavigationBar {
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Search, null) }, label = { Text("Search") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Settings, null) }, label = { Text("Settings") })
            }
        }
    }

    @Test
    fun testNavigationDrawer() = captureComponent("navigation-drawer") {
        ModalDrawerSheet(modifier = Modifier.width(260.dp)) {
            Text("Mailbox", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            HorizontalDivider()
            NavigationDrawerItem(label = { Text("Inbox (12)") }, selected = true, onClick = {}, icon = { Icon(Icons.Default.Mail, null) })
            NavigationDrawerItem(label = { Text("Sent") }, selected = false, onClick = {}, icon = { Icon(Icons.Default.Send, null) })
            NavigationDrawerItem(label = { Text("Favorites") }, selected = false, onClick = {}, icon = { Icon(Icons.Default.Favorite, null) })
        }
    }

    @Test
    fun testCheckboxExamples() = captureComponent("checkbox-examples") {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TriStateCheckbox(state = ToggleableState.Indeterminate, onClick = {})
                Spacer(Modifier.width(8.dp))
                Text("Parent Tri-State Checkbox", fontWeight = FontWeight.Medium)
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 24.dp)) {
                Checkbox(checked = true, onCheckedChange = {})
                Spacer(Modifier.width(8.dp))
                Text("Option 1 (Checked)")
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(start = 24.dp)) {
                Checkbox(checked = false, onCheckedChange = {})
                Spacer(Modifier.width(8.dp))
                Text("Option 2 (Unchecked)")
            }
        }
    }

    @Test
    fun testChipExamples() = captureComponent("chip-examples") {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = {}, label = { Text("Assist") }, leadingIcon = { Icon(Icons.Default.Star, null, Modifier.size(18.dp)) })
            FilterChip(selected = true, onClick = {}, label = { Text("Filter") }, leadingIcon = { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) })
            InputChip(selected = false, onClick = {}, label = { Text("Input") })
            SuggestionChip(onClick = {}, label = { Text("Suggestion") })
        }
    }

    @Test
    fun testDatePicker() = captureComponent("date-picker") {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 6.dp,
            modifier = Modifier.size(320.dp, 240.dp)
        ) {
            Column(Modifier.padding(16.dp)) {
                Text("Select Date", fontSize = 12.sp, color = AndroidGreenDark)
                Text("Thu, Sep 19", fontSize = 22.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("September 2026", fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    Icon(Icons.Default.KeyboardArrowDown, null, Modifier.size(18.dp))
                }
                Spacer(Modifier.height(10.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf("S","M","T","W","T","F","S").forEach { Text(it, fontSize = 11.sp, color = Color.Gray) }
                }
                Spacer(Modifier.height(6.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    listOf("15","16","17","18","19","20","21").forEach {
                        Box(
                            Modifier.size(26.dp).background(if (it == "19") AndroidGreenDark else Color.Transparent, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(it, fontSize = 11.sp, color = if (it == "19") Color.White else Color.Black)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun testMenuExamples() = captureComponent("menu-examples") {
        Surface(
            shape = RoundedCornerShape(8.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            modifier = Modifier.width(180.dp)
        ) {
            Column(Modifier.padding(vertical = 4.dp)) {
                DropdownMenuItem(text = { Text("Edit") }, onClick = {}, leadingIcon = { Icon(Icons.Default.Edit, null) })
                DropdownMenuItem(text = { Text("Share") }, onClick = {}, leadingIcon = { Icon(Icons.Default.Share, null) })
                HorizontalDivider()
                DropdownMenuItem(text = { Text("Delete", color = MaterialTheme.colorScheme.error) }, onClick = {}, leadingIcon = { Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.error) })
            }
        }
    }

    @Test
    fun testRadioButton() = captureComponent("radio-button") {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = true, onClick = {})
                Spacer(Modifier.width(8.dp))
                Text("Option A (Selected)")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = false, onClick = {})
                Spacer(Modifier.width(8.dp))
                Text("Option B (Unselected)")
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                RadioButton(selected = false, onClick = {})
                Spacer(Modifier.width(8.dp))
                Text("Option C (Unselected)")
            }
        }
    }

    @Test
    fun testSliderExamples() = captureComponent("slider-examples") {
        Column(Modifier.fillMaxWidth(0.85f), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Text("Continuous Slider (60%)", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Slider(value = 0.6f, onValueChange = {})
            Text("Range Slider (20% - 80%)", fontSize = 13.sp, fontWeight = FontWeight.Medium)
            RangeSlider(value = 0.2f..0.8f, onValueChange = {})
        }
    }

    @Test
    fun testSwitchExamples() = captureComponent("switch-examples") {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Switch(checked = true, onCheckedChange = {})
                Text("Enabled Switch")
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Switch(
                    checked = true,
                    onCheckedChange = {},
                    thumbContent = { Icon(Icons.Default.Check, null, Modifier.size(SwitchDefaults.IconSize)) }
                )
                Text("Switch with Icon")
            }
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Switch(checked = false, onCheckedChange = {})
                Text("Disabled State")
            }
        }
    }

    @Test
    fun testTimePicker() = captureComponent("time-picker") {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 6.dp,
            modifier = Modifier.size(300.dp, 190.dp)
        ) {
            Column(Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Select time", fontSize = 12.sp, color = AndroidGreenDark, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(14.dp))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.Center) {
                    Surface(color = AndroidGreenContainer, shape = RoundedCornerShape(8.dp), modifier = Modifier.size(64.dp, 56.dp)) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("10", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = AndroidGreenDark)
                        }
                    }
                    Text(" : ", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                    Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp), modifier = Modifier.size(64.dp, 56.dp)) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text("30", fontSize = 32.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    Spacer(Modifier.width(14.dp))
                    Column {
                        Surface(color = AndroidGreenContainer, shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp), modifier = Modifier.size(44.dp, 28.dp)) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("AM", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AndroidGreenDark)
                            }
                        }
                        Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp), modifier = Modifier.size(44.dp, 28.dp)) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Text("PM", fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    @Test
    fun testSearchBar() = captureComponent("search-bar") {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = "Material 3 Search",
                    onQueryChange = {},
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    placeholder = { Text("Search components...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) },
                    trailingIcon = { Icon(Icons.Default.Close, null) }
                )
            },
            expanded = false,
            onExpandedChange = {},
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {}
    }

    @Test
    fun testSwipeToDismiss() = captureComponent("swipe-to-dismiss") {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.errorContainer,
            modifier = Modifier.fillMaxWidth(0.88f).height(68.dp)
        ) {
            Row(
                Modifier.fillMaxSize().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.onErrorContainer)
                Card(
                    modifier = Modifier.fillMaxHeight().weight(1f).padding(start = 12.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.CenterStart) {
                        Text("Swipeable Item (Swipe to delete)", fontSize = 13.sp)
                    }
                }
            }
        }
    }

    @Test
    fun testBadge() = captureComponent("badge") {
        BadgedBox(badge = { Badge { Text("8") } }) {
            Icon(Icons.Default.Mail, contentDescription = "Mail", modifier = Modifier.size(36.dp), tint = AndroidGreenDark)
        }
    }

    @Test
    fun testBadgeInteractive() = captureComponent("badge-interactive") {
        BadgedBox(badge = { Badge { Text("99+") } }) {
            Icon(Icons.Default.Notifications, contentDescription = "Notifications", modifier = Modifier.size(36.dp), tint = AndroidGreenDark)
        }
    }

    @Test
    fun testFilledCard() = captureComponent("filled-card") {
        Card(Modifier.size(240.dp, 140.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Filled Card", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                Text("High-emphasis surface container card.", fontSize = 13.sp, color = Color.Gray)
            }
        }
    }

    @Test
    fun testElevatedCard() = captureComponent("elevated-card") {
        ElevatedCard(Modifier.size(240.dp, 140.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Elevated Card", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AndroidGreenDark)
                Spacer(Modifier.height(8.dp))
                Text("Card with shadow elevation for visual separation.", fontSize = 13.sp, color = Color.Gray)
            }
        }
    }

    @Test
    fun testOutlinedCard() = captureComponent("outlined-card") {
        OutlinedCard(Modifier.size(240.dp, 140.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Outlined Card", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(Modifier.height(8.dp))
                Text("Card with subtle border outline.", fontSize = 13.sp, color = Color.Gray)
            }
        }
    }

    @Test
    fun testMultiBrowseCarousel() = captureComponent("multi-browse-carousel") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(3) { i ->
                Card(
                    modifier = Modifier.size(130.dp, 170.dp),
                    colors = CardDefaults.cardColors(containerColor = if (i == 0) AndroidGreenContainer else MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(if (i == 0) Icons.Default.Star else Icons.Default.Favorite, null, tint = AndroidGreenDark)
                            Spacer(Modifier.height(8.dp))
                            Text("Item ${i + 1}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun testUncontainedCarousel() = captureComponent("uncontained-carousel") {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            repeat(3) { i ->
                Card(
                    modifier = Modifier.size(130.dp, 170.dp),
                    colors = CardDefaults.cardColors(containerColor = if (i == 1) AndroidBlueContainer else MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Box(Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Image, null, tint = AndroidBlueDark)
                            Spacer(Modifier.height(8.dp))
                            Text("Slide ${i + 1}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        }
                    }
                }
            }
        }
    }

    @Test
    fun testAlertDialog() = captureComponent("alert-dialog") {
        AlertDialog(
            onDismissRequest = {},
            icon = { Icon(Icons.Default.Info, null, tint = AndroidGreenDark) },
            title = { Text("Alert Dialog") },
            text = { Text("Presents urgent information or decisions.") },
            confirmButton = { TextButton(onClick = {}) { Text("Confirm") } },
            dismissButton = { TextButton(onClick = {}) { Text("Dismiss") } }
        )
    }

    @Test
    fun testMinimalDialog() = captureComponent("minimal-dialog") {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 6.dp,
            modifier = Modifier.size(280.dp, 160.dp)
        ) {
            Box(Modifier.fillMaxSize().padding(16.dp), contentAlignment = Alignment.Center) {
                Text("Minimal Dialog Content", fontWeight = FontWeight.Medium)
            }
        }
    }

    @Test
    fun testDialogWithImage() = captureComponent("dialog-with-image") {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 6.dp,
            modifier = Modifier.size(300.dp, 200.dp)
        ) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Image, null, Modifier.size(48.dp), tint = AndroidGreenDark)
                Spacer(Modifier.height(12.dp))
                Text("Dialog with Image", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Informative dialog with graphic.", fontSize = 13.sp, color = Color.Gray)
            }
        }
    }

    @Test
    fun testHorizontalDivider() = captureComponent("horizontal-divider") {
        Column(Modifier.fillMaxWidth(0.85f), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Text("Above Divider", fontSize = 14.sp)
            HorizontalDivider(thickness = 2.dp, color = AndroidGreenDark)
            Text("Below Divider", fontSize = 14.sp)
        }
    }

    @Test
    fun testVerticalDivider() = captureComponent("vertical-divider") {
        Row(Modifier.height(80.dp), horizontalArrangement = Arrangement.spacedBy(20.dp), verticalAlignment = Alignment.CenterVertically) {
            Text("Left Content")
            VerticalDivider(thickness = 2.dp, color = AndroidBlueDark)
            Text("Right Content")
        }
    }

    @Test
    fun testCenterAlignedTopAppBar() = captureComponent("center-aligned-top-app-bar") {
        CenterAlignedTopAppBar(
            title = { Text("Center Aligned") },
            navigationIcon = { IconButton(onClick = {}) { Icon(Icons.Default.Menu, null) } },
            actions = { IconButton(onClick = {}) { Icon(Icons.Default.Settings, null) } },
            modifier = Modifier.fillMaxWidth(0.9f)
        )
    }

    @Test
    fun testSmallTopAppBar() = captureComponent("small-top-app-bar") {
        TopAppBar(
            title = { Text("Small Top Bar") },
            navigationIcon = { IconButton(onClick = {}) { Icon(Icons.Default.Menu, null) } },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = AndroidGreenContainer),
            modifier = Modifier.fillMaxWidth(0.9f)
        )
    }

    @Test
    fun testMediumTopAppBar() = captureComponent("medium-top-app-bar") {
        MediumTopAppBar(
            title = { Text("Medium Top Bar") },
            navigationIcon = { IconButton(onClick = {}) { Icon(Icons.Default.Menu, null) } },
            modifier = Modifier.fillMaxWidth(0.9f)
        )
    }

    @Test
    fun testLargeTopAppBar() = captureComponent("large-top-app-bar") {
        LargeTopAppBar(
            title = { Text("Large Top Bar") },
            navigationIcon = { IconButton(onClick = {}) { Icon(Icons.Default.Menu, null) } },
            modifier = Modifier.fillMaxWidth(0.9f)
        )
    }

    @Test
    fun testNavigationBar() = captureComponent("navigation-bar") {
        NavigationBar(Modifier.fillMaxWidth(0.9f)) {
            NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
            NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Search, null) }, label = { Text("Search") })
            NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profile") })
        }
    }

    @Test
    fun testNavigationRail() = captureComponent("navigation-rail") {
        NavigationRail(Modifier.height(200.dp)) {
            NavigationRailItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Home") })
            NavigationRailItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Search, null) }, label = { Text("Search") })
        }
    }

    @Test
    fun testModalNavigationDrawer() = captureComponent("modal-navigation-drawer") {
        ModalDrawerSheet(modifier = Modifier.width(260.dp)) {
            Text("Navigation Drawer", modifier = Modifier.padding(16.dp), fontWeight = FontWeight.Bold, fontSize = 16.sp)
            HorizontalDivider()
            NavigationDrawerItem(label = { Text("Home") }, selected = true, onClick = {}, icon = { Icon(Icons.Default.Home, null) })
            NavigationDrawerItem(label = { Text("Settings") }, selected = false, onClick = {}, icon = { Icon(Icons.Default.Settings, null) })
        }
    }

    @Test
    fun testCheckbox() = captureComponent("checkbox") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Checkbox(checked = true, onCheckedChange = {})
            Spacer(Modifier.width(8.dp))
            Text("Checked Option")
        }
    }

    @Test
    fun testParentCheckbox() = captureComponent("parent-checkbox") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            TriStateCheckbox(state = ToggleableState.Indeterminate, onClick = {})
            Spacer(Modifier.width(8.dp))
            Text("Indeterminate Parent")
        }
    }

    @Test
    fun testAssistChip() = captureComponent("assist-chip") {
        AssistChip(onClick = {}, label = { Text("Assist Chip") }, leadingIcon = { Icon(Icons.Default.Settings, null) })
    }

    @Test
    fun testFilterChip() = captureComponent("filter-chip") {
        FilterChip(selected = true, onClick = {}, label = { Text("Filter Chip") }, leadingIcon = { Icon(Icons.Default.Check, null) })
    }

    @Test
    fun testInputChip() = captureComponent("input-chip") {
        InputChip(selected = true, onClick = {}, label = { Text("Input Chip") }, trailingIcon = { Icon(Icons.Default.Close, null) })
    }

    @Test
    fun testSuggestionChip() = captureComponent("suggestion-chip") {
        SuggestionChip(onClick = {}, label = { Text("Suggestion Chip") })
    }

    @Test
    fun testDatePickerModal() = captureComponent("date-picker-modal") {
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 6.dp, modifier = Modifier.size(320.dp, 200.dp)) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Select Date (Modal)", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Surface(color = AndroidGreenContainer, shape = RoundedCornerShape(8.dp), modifier = Modifier.padding(8.dp)) {
                        Text("Oct 24, 2024", modifier = Modifier.padding(12.dp), fontWeight = FontWeight.SemiBold, color = AndroidGreenDark)
                    }
                }
            }
        }
    }

    @Test
    fun testDatePickerInputModal() = captureComponent("date-picker-input-modal") {
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 6.dp, modifier = Modifier.size(320.dp, 160.dp)) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Date Input Modal", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(value = "10/24/2024", onValueChange = {}, label = { Text("Date") })
            }
        }
    }

    @Test
    fun testDatePickerDocked() = captureComponent("date-picker-docked") {
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 6.dp, modifier = Modifier.size(320.dp, 160.dp)) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Docked Date Picker", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Text("Docked calendar presentation for large screens.", fontSize = 13.sp, color = Color.Gray)
            }
        }
    }

    @Test
    fun testDateRangePicker() = captureComponent("date-range-picker") {
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 6.dp, modifier = Modifier.size(340.dp, 180.dp)) {
            Column(Modifier.padding(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Date Range Picker", fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Surface(color = AndroidGreenContainer, shape = RoundedCornerShape(6.dp)) {
                        Text("Oct 12", modifier = Modifier.padding(8.dp), fontWeight = FontWeight.Bold, color = AndroidGreenDark)
                    }
                    Text("–")
                    Surface(color = AndroidGreenContainer, shape = RoundedCornerShape(6.dp)) {
                        Text("Oct 24", modifier = Modifier.padding(8.dp), fontWeight = FontWeight.Bold, color = AndroidGreenDark)
                    }
                }
            }
        }
    }

    @Test
    fun testMinimalDropdownMenu() = captureComponent("minimal-dropdown-menu") {
        Surface(shape = RoundedCornerShape(8.dp), shadowElevation = 6.dp, color = MaterialTheme.colorScheme.surface, modifier = Modifier.width(180.dp)) {
            Column {
                DropdownMenuItem(text = { Text("Profile") }, onClick = {})
                DropdownMenuItem(text = { Text("Settings") }, onClick = {})
            }
        }
    }

    @Test
    fun testScrollableDropdownMenu() = captureComponent("scrollable-dropdown-menu") {
        Surface(shape = RoundedCornerShape(8.dp), shadowElevation = 6.dp, color = MaterialTheme.colorScheme.surface, modifier = Modifier.width(180.dp)) {
            Column {
                DropdownMenuItem(text = { Text("Option 1") }, onClick = {})
                DropdownMenuItem(text = { Text("Option 2") }, onClick = {})
                DropdownMenuItem(text = { Text("Option 3") }, onClick = {})
            }
        }
    }

    @Test
    fun testDropdownMenuWithDetails() = captureComponent("dropdown-menu-with-details") {
        Surface(shape = RoundedCornerShape(8.dp), shadowElevation = 6.dp, color = MaterialTheme.colorScheme.surface, modifier = Modifier.width(220.dp)) {
            Column {
                DropdownMenuItem(text = { Text("Refresh") }, onClick = {}, leadingIcon = { Icon(Icons.Default.Refresh, null) })
                DropdownMenuItem(text = { Text("Settings") }, onClick = {}, leadingIcon = { Icon(Icons.Default.Settings, null) })
            }
        }
    }

    @Test
    fun testRadioButtonSingle() = captureComponent("radio-button-single") {
        Row(verticalAlignment = Alignment.CenterVertically) {
            RadioButton(selected = true, onClick = {})
            Spacer(Modifier.width(8.dp))
            Text("Selected Radio Button")
        }
    }

    @Test
    fun testSearchBarSimple() = captureComponent("search-bar-simple") {
        SearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = "Search",
                    onQueryChange = {},
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    placeholder = { Text("Search...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) }
                )
            },
            expanded = false,
            onExpandedChange = {},
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {}
    }

    @Test
    fun testDockedSearchBar() = captureComponent("docked-search-bar") {
        DockedSearchBar(
            inputField = {
                SearchBarDefaults.InputField(
                    query = "Docked Search",
                    onQueryChange = {},
                    onSearch = {},
                    expanded = false,
                    onExpandedChange = {},
                    placeholder = { Text("Docked search...") },
                    leadingIcon = { Icon(Icons.Default.Search, null) }
                )
            },
            expanded = false,
            onExpandedChange = {},
            modifier = Modifier.fillMaxWidth(0.9f)
        ) {}
    }

    @Test
    fun testContinuousSlider() = captureComponent("continuous-slider") {
        Slider(value = 0.6f, onValueChange = {}, modifier = Modifier.fillMaxWidth(0.85f))
    }

    @Test
    fun testDiscreteSlider() = captureComponent("discrete-slider") {
        Slider(value = 0.4f, onValueChange = {}, steps = 4, modifier = Modifier.fillMaxWidth(0.85f))
    }

    @Test
    fun testRangeSlider() = captureComponent("range-slider") {
        RangeSlider(value = 0.2f..0.8f, onValueChange = {}, modifier = Modifier.fillMaxWidth(0.85f))
    }

    @Test
    fun testMinimalSwitch() = captureComponent("minimal-switch") {
        Switch(checked = true, onCheckedChange = {})
    }

    @Test
    fun testSwitchWithIcon() = captureComponent("switch-with-icon") {
        Switch(
            checked = true,
            onCheckedChange = {},
            thumbContent = { Icon(Icons.Default.Check, null, Modifier.size(SwitchDefaults.IconSize)) }
        )
    }

    @Test
    fun testSwipeToDismissItem() = captureComponent("swipe-to-dismiss-item") {
        Surface(
            shape = RoundedCornerShape(12.dp),
            color = MaterialTheme.colorScheme.errorContainer,
            modifier = Modifier.fillMaxWidth(0.88f).height(68.dp)
        ) {
            Row(
                Modifier.fillMaxSize().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Delete, null, tint = MaterialTheme.colorScheme.onErrorContainer)
                Card(
                    modifier = Modifier.fillMaxHeight().weight(1f).padding(start = 12.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Box(Modifier.fillMaxSize().padding(12.dp), contentAlignment = Alignment.CenterStart) {
                        Text("Swipe to dismiss item", fontSize = 13.sp)
                    }
                }
            }
        }
    }

    @Test
    fun testDialTimePicker() = captureComponent("dial-time-picker") {
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 6.dp, modifier = Modifier.size(300.dp, 220.dp)) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Dial Time Picker", fontWeight = FontWeight.Bold)
            }
        }
    }

    @Test
    fun testInputTimePicker() = captureComponent("input-time-picker") {
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 6.dp, modifier = Modifier.size(300.dp, 160.dp)) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Input Time Picker", fontWeight = FontWeight.Bold)
            }
        }
    }

    @Test
    fun testPlainTooltip() = captureComponent("plain-tooltip") {
        Surface(shape = RoundedCornerShape(8.dp), color = MaterialTheme.colorScheme.surfaceVariant, shadowElevation = 4.dp, modifier = Modifier.padding(16.dp)) {
            Text("Plain Tooltip Label", modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp), fontSize = 13.sp)
        }
    }

    @Test
    fun testRichTooltip() = captureComponent("rich-tooltip") {
        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 6.dp, modifier = Modifier.padding(16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("Rich Tooltip Title", fontWeight = FontWeight.Bold, color = AndroidGreenDark)
                Spacer(Modifier.height(4.dp))
                Text("Detailed description with contextual guidance.", fontSize = 13.sp, color = Color.Gray)
            }
        }
    }

    @Test
    fun testPartialBottomSheet() = captureComponent("partial-bottom-sheet") {
        Surface(shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp), color = MaterialTheme.colorScheme.surface, shadowElevation = 8.dp, modifier = Modifier.fillMaxWidth(0.9f)) {
            Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Box(Modifier.size(40.dp, 4.dp).background(Color.LightGray, RoundedCornerShape(2.dp)))
                Spacer(Modifier.height(16.dp))
                Text("Partial Bottom Sheet", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }

    @Test
    fun testScaffold() = captureComponent("scaffold") {
        Scaffold(
            modifier = Modifier.size(400.dp, 220.dp),
            topBar = {
                TopAppBar(
                    title = { Text("Scaffold", fontSize = 15.sp) },
                    navigationIcon = { Icon(Icons.Default.Menu, null, Modifier.padding(start = 8.dp)) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = AndroidGreenContainer)
                )
            }
        ) { padding ->
            Box(Modifier.padding(padding).fillMaxSize().padding(12.dp)) {
                Text("Scaffold Content", fontSize = 13.sp, color = Color.Gray)
            }
        }
    }
}

