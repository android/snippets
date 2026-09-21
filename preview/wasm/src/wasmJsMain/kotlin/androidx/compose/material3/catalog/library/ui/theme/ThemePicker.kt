/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package androidx.compose.material3.catalog.library.ui.theme

import com.example.compose.snippets.components.AppIcons
import androidx.compose.ui.graphics.vector.rememberVectorPainter

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.catalog.library.model.ColorMode
import androidx.compose.material3.catalog.library.model.ExpressiveThemeMode
import androidx.compose.material3.catalog.library.model.FocusIndicationStyle
import androidx.compose.material3.catalog.library.model.FontScaleMode
import androidx.compose.material3.catalog.library.model.MaxFontScale
import androidx.compose.material3.catalog.library.model.MinFontScale
import androidx.compose.material3.catalog.library.model.TextDirection
import androidx.compose.material3.catalog.library.model.Theme
import androidx.compose.material3.catalog.library.model.ThemeColorMode
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.preview.wasm.theme.ThemePreset
import com.example.compose.preview.wasm.theme.parseHexColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemePicker(theme: Theme, onThemeChange: (theme: Theme) -> Unit) {
    val openExpressiveDialog = remember { mutableStateOf(false) }
    val expressiveThemeValue = remember { mutableStateOf(ExpressiveThemeMode.NonExpressive) }

    LazyColumn(
        contentPadding = PaddingValues(vertical = ThemePickerPadding),
        verticalArrangement = Arrangement.spacedBy(ThemePickerPadding),
    ) {
        item {
            Text(
                text = stringResource(id = R.string.theme),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = ThemePickerPadding),
            )
            // LazyVerticalGrid can't be used within LazyColumn due to nested scrolling
            val themeColorModes = ThemeColorMode.values()
            Column(modifier = Modifier.padding(ThemePickerPadding)) {
                Row(horizontalArrangement = Arrangement.spacedBy(ThemePickerPadding)) {
                    RadioButtonOption(
                        modifier = Modifier.weight(1f),
                        option = themeColorModes[0],
                        selected = themeColorModes[0] == theme.themeColorMode,
                        onClick = { onThemeChange(theme.copy(themeColorMode = it)) },
                    )
                    RadioButtonOption(
                        modifier = Modifier.weight(1f),
                        option = themeColorModes[1],
                        selected = themeColorModes[1] == theme.themeColorMode,
                        onClick = { onThemeChange(theme.copy(themeColorMode = it)) },
                    )
                }
                Row {
                    RadioButtonOption(
                        modifier = Modifier.weight(1f),
                        option = themeColorModes[2],
                        selected = themeColorModes[2] == theme.themeColorMode,
                        onClick = { onThemeChange(theme.copy(themeColorMode = it)) },
                    )
                }
            }
            HorizontalDivider(Modifier.padding(horizontal = ThemePickerPadding))
        }
        item {
            Text(
                text = "Preset Palettes",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = ThemePickerPadding),
            )
            Column(
                modifier = Modifier.padding(ThemePickerPadding),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ThemePreset.entries.forEach { preset ->
                    val isSelected = theme.customColor == null && theme.preset == preset
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onThemeChange(theme.copy(preset = preset, customColor = null))
                            },
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) {
                                MaterialTheme.colorScheme.primaryContainer
                            } else {
                                MaterialTheme.colorScheme.surfaceContainerLow
                            }
                        ),
                        border = if (isSelected) {
                            BorderStroke(2.dp, MaterialTheme.colorScheme.primary)
                        } else null
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(20.dp)
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
                                    rememberVectorPainter(AppIcons.Check),
                                    contentDescription = "Active",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
            HorizontalDivider(Modifier.padding(horizontal = ThemePickerPadding))
        }
        item {
            var hexInput by remember { mutableStateOf("") }
            Text(
                text = "Custom Seed Color",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = ThemePickerPadding),
            )
            Column(modifier = Modifier.padding(ThemePickerPadding), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = hexInput,
                        onValueChange = { hexInput = it },
                        placeholder = { Text("#5F6368") },
                        singleLine = true,
                        modifier = Modifier.weight(1f),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )
                    Button(
                        onClick = {
                            val parsed = parseHexColor(hexInput)
                            if (parsed != null) {
                                onThemeChange(theme.copy(customColor = parsed))
                            }
                        },
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Apply")
                    }
                }
                if (theme.customColor != null) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(theme.customColor, CircleShape)
                                .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape)
                        )
                        Text(
                            text = "Custom seed active",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }
            HorizontalDivider(Modifier.padding(horizontal = ThemePickerPadding))
        }
        item {
            Text(
                text = "Active Scheme Tokens",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = ThemePickerPadding),
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(ThemePickerPadding),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                ColorTokenSwatch(label = "Pri", color = MaterialTheme.colorScheme.primary, modifier = Modifier.weight(1f))
                ColorTokenSwatch(label = "Sec", color = MaterialTheme.colorScheme.secondary, modifier = Modifier.weight(1f))
                ColorTokenSwatch(label = "Ter", color = MaterialTheme.colorScheme.tertiary, modifier = Modifier.weight(1f))
                ColorTokenSwatch(label = "Cnt", color = MaterialTheme.colorScheme.primaryContainer, modifier = Modifier.weight(1f))
                ColorTokenSwatch(label = "Srf", color = MaterialTheme.colorScheme.surface, modifier = Modifier.weight(1f))
            }
            HorizontalDivider(Modifier.padding(horizontal = ThemePickerPadding))
        }
        item {
            Text(
                text = stringResource(id = R.string.text_direction),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = ThemePickerPadding),
            )
            val textDirections = TextDirection.values()
            Column(modifier = Modifier.padding(ThemePickerPadding)) {
                Row(horizontalArrangement = Arrangement.spacedBy(ThemePickerPadding)) {
                    RadioButtonOption(
                        modifier = Modifier.weight(1f),
                        option = textDirections[0],
                        selected = textDirections[0] == theme.textDirection,
                        onClick = { onThemeChange(theme.copy(textDirection = it)) },
                    )
                    RadioButtonOption(
                        modifier = Modifier.weight(1f),
                        option = textDirections[1],
                        selected = textDirections[1] == theme.textDirection,
                        onClick = { onThemeChange(theme.copy(textDirection = it)) },
                    )
                }
                Row {
                    RadioButtonOption(
                        modifier = Modifier.weight(1f),
                        option = textDirections[2],
                        selected = textDirections[2] == theme.textDirection,
                        onClick = { onThemeChange(theme.copy(textDirection = it)) },
                    )
                }
            }
            HorizontalDivider(Modifier.padding(horizontal = ThemePickerPadding))
        }
        item {
            Text(
                text = stringResource(id = R.string.font_scale),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(horizontal = ThemePickerPadding),
            )
            val fontScaleModes = FontScaleMode.values()
            Column(modifier = Modifier.padding(ThemePickerPadding)) {
                Row(horizontalArrangement = Arrangement.spacedBy(ThemePickerPadding)) {
                    RadioButtonOption(
                        modifier = Modifier.weight(1f),
                        option = fontScaleModes[0],
                        selected = fontScaleModes[0] == theme.fontScaleMode,
                        onClick = { onThemeChange(theme.copy(fontScaleMode = it)) },
                    )
                    RadioButtonOption(
                        modifier = Modifier.weight(1f),
                        option = fontScaleModes[1],
                        selected = fontScaleModes[1] == theme.fontScaleMode,
                        onClick = { onThemeChange(theme.copy(fontScaleMode = it)) },
                    )
                }
                var fontScale by remember(theme.fontScale) { mutableFloatStateOf(theme.fontScale) }
                CustomFontScaleSlider(
                    modifier = Modifier.padding(top = ThemePickerPadding),
                    enabled = theme.fontScaleMode == FontScaleMode.Custom,
                    fontScale = fontScale,
                    onValueChange = { fontScale = it },
                    onValueChangeFinished = { onThemeChange(theme.copy(fontScale = fontScale)) },
                )
            }
            HorizontalDivider(Modifier.padding(horizontal = ThemePickerPadding))
        }
        item {
            Row {
                Text(
                    text = stringResource(id = R.string.expressive_theme_mode),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier =
                        Modifier.padding(
                            start = ThemePickerPadding,
                            end = ThemePickerPadding / 2,
                        ),
                )
                Badge { Text(stringResource(R.string.experimental)) }
            }
            val expressiveThemeModes = ExpressiveThemeMode.values()
            Column(modifier = Modifier.padding(ThemePickerPadding)) {
                Row(horizontalArrangement = Arrangement.spacedBy(ThemePickerPadding)) {
                    RadioButtonOption(
                        modifier = Modifier.weight(1f),
                        option = expressiveThemeModes[0],
                        selected = expressiveThemeModes[0] == theme.expressiveThemeMode,
                        onClick = {
                            if (theme.expressiveThemeMode != it) {
                                expressiveThemeValue.value = it
                                openExpressiveDialog.value = true
                            }
                        },
                    )
                    RadioButtonOption(
                        modifier = Modifier.weight(1f),
                        option = expressiveThemeModes[1],
                        selected = expressiveThemeModes[1] == theme.expressiveThemeMode,
                        onClick = {
                            if (theme.expressiveThemeMode != it) {
                                expressiveThemeValue.value = it
                                openExpressiveDialog.value = true
                            }
                        },
                    )
                }
                SwitchSetting(
                    text = stringResource(id = R.string.mark_expressive_components),
                    modifier = Modifier.fillMaxWidth(),
                    checked = theme.markExpressiveComponents,
                    onCheckedChange = { checked ->
                        if (theme.markExpressiveComponents != checked) {
                            onThemeChange(theme.copy(markExpressiveComponents = checked))
                        }
                    },
                )
                SwitchSetting(
                    text = stringResource(id = R.string.display_only_expressive_components),
                    modifier = Modifier.fillMaxWidth(),
                    checked = theme.showOnlyExpressiveComponents,
                    onCheckedChange = { checked ->
                        if (theme.showOnlyExpressiveComponents != checked) {
                            onThemeChange(theme.copy(showOnlyExpressiveComponents = checked))
                        }
                    },
                )
            }
            HorizontalDivider(Modifier.padding(horizontal = ThemePickerPadding))
        }
        item {
            Row {
                Text(
                    text = stringResource(id = R.string.focus_indication_style),
                    style = MaterialTheme.typography.bodyMedium,
                    modifier =
                        Modifier.padding(
                            start = ThemePickerPadding,
                            end = ThemePickerPadding / 2,
                        ),
                )
                Badge { Text(stringResource(R.string.experimental)) }
            }
            val focusIndicationStyles = FocusIndicationStyle.values()
            Column(modifier = Modifier.padding(ThemePickerPadding)) {
                Row(horizontalArrangement = Arrangement.spacedBy(ThemePickerPadding)) {
                    RadioButtonOption(
                        modifier = Modifier.weight(1f),
                        option = focusIndicationStyles[0],
                        selected = focusIndicationStyles[0] == theme.focusIndicationStyle,
                        onClick = {
                            if (theme.focusIndicationStyle != it) {
                                onThemeChange(theme.copy(focusIndicationStyle = it))
                            }
                        },
                    )
                    RadioButtonOption(
                        modifier = Modifier.weight(1f),
                        option = focusIndicationStyles[1],
                        selected = focusIndicationStyles[1] == theme.focusIndicationStyle,
                        onClick = {
                            if (theme.focusIndicationStyle != it) {
                                onThemeChange(theme.copy(focusIndicationStyle = it))
                            }
                        },
                    )
                }
            }
        }
        item {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Button(onClick = { onThemeChange(Theme()) }) {
                    Text(text = stringResource(id = R.string.reset_all))
                }
            }
        }
    }
    if (openExpressiveDialog.value) {
        ExpressiveAlertDialog(
            onDismissRequest = {
                if (expressiveThemeValue.value == ExpressiveThemeMode.NonExpressive) {
                    expressiveThemeValue.value = ExpressiveThemeMode.Expressive
                } else {
                    expressiveThemeValue.value = ExpressiveThemeMode.NonExpressive
                }
                openExpressiveDialog.value = false
            },
            onDismissButtonClick = {
                if (expressiveThemeValue.value == ExpressiveThemeMode.NonExpressive) {
                    expressiveThemeValue.value = ExpressiveThemeMode.Expressive
                } else {
                    expressiveThemeValue.value = ExpressiveThemeMode.NonExpressive
                }
                openExpressiveDialog.value = false
            },
            onConfirmButtonClick = {
                onThemeChange(theme.copy(expressiveThemeMode = expressiveThemeValue.value))
                openExpressiveDialog.value = false
            },
        )
    }
}

@Composable
private fun <T> RadioButtonOption(
    option: T,
    selected: Boolean,
    onClick: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier =
            modifier
                .selectable(
                    selected = selected,
                    enabled = enabled,
                    onClick = { onClick(option) },
                    role = Role.RadioButton,
                )
                .minimumInteractiveComponentSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(ThemePickerPadding),
    ) {
        RadioButton(selected = selected, enabled = enabled, onClick = null)
        Text(text = option.toString(), style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun SwitchSetting(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier =
            modifier
                .selectable(
                    selected = checked,
                    enabled = enabled,
                    onClick = { onCheckedChange(!checked) },
                    role = Role.Switch,
                )
                .minimumInteractiveComponentSize(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.weight(1f),
        )
        Switch(checked = checked, onCheckedChange = null, enabled = enabled)
    }
}

@Composable
private fun CustomFontScaleSlider(
    modifier: Modifier = Modifier,
    enabled: Boolean,
    fontScale: Float,
    fontScaleMin: Float = MinFontScale,
    fontScaleMax: Float = MaxFontScale,
    onValueChange: (textScale: Float) -> Unit,
    onValueChangeFinished: () -> Unit,
) {
    Column(modifier = modifier) {
        Slider(
            value = fontScale,
            enabled = enabled,
            onValueChange = onValueChange,
            onValueChangeFinished = onValueChangeFinished,
            valueRange = fontScaleMin..fontScaleMax,
        )
        Text(
            text = stringResource(id = R.string.scale, fontScale),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun ExpressiveAlertDialog(
    onDismissRequest: () -> Unit,
    onConfirmButtonClick: () -> Unit,
    onDismissButtonClick: () -> Unit,
) {
    AlertDialog(
        icon = { Icon(painter = rememberVectorPainter(AppIcons.Warning), contentDescription = null) },
        title = { Text("Warning") },
        text = {
            Text(
                "Setting a new Material theme will reset the catalog and progress will be " +
                    "lost. Please confirm before proceeding."
            )
        },
        onDismissRequest = onDismissRequest,
        confirmButton = { Button(onClick = onConfirmButtonClick) { Text("Confirm") } },
        dismissButton = { Button(onClick = onDismissButtonClick) { Text("Cancel") } },
    )
}

private object R {
    object string {
        const val theme = 101
        const val expressive_theme_mode = 102
        const val color_mode = 103
        const val font_scale = 104
        const val text_direction = 105
        const val focus_indication_style = 106
        const val reset_all = 107
        const val mark_expressive_components = 108
        const val display_only_expressive_components = 109
        const val experimental = 110
        const val scale = 111
    }
}

private fun stringResource(id: Int, vararg args: Any): String = when (id) {
    R.string.theme -> "Theme"
    R.string.expressive_theme_mode -> "Expressive theme mode"
    R.string.color_mode -> "Color mode"
    R.string.font_scale -> "Font scale"
    R.string.text_direction -> "Text direction"
    R.string.focus_indication_style -> "Focus indication style"
    R.string.reset_all -> "Reset all"
    R.string.mark_expressive_components -> "Mark expressive components"
    R.string.display_only_expressive_components -> "Show only expressive components"
    R.string.experimental -> "Experimental"
    R.string.scale -> {
        val v = (args.firstOrNull() as? Float) ?: 1f
        "${((v * 100).toInt()) / 100f}"
    }
    else -> ""
}

private val ThemePickerPadding = 16.dp

@Composable
private fun ColorTokenSwatch(label: String, color: Color, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(26.dp)
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
