package com.example.compose.snippets.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Camera
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.TooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

@Composable
fun TooltipExamples(){
    Text(
        "Long press an icon to see the tooltip.",
        modifier = Modifier.fillMaxWidth().padding(16.dp),
        textAlign = TextAlign.Center
    )
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxSize()
    ) {
        PlainTooltipExample()
        RichTooltipExample()
        AdvancedRichTooltipExample()
    }
}

@Preview
@Composable
private fun TooltipExamplesPreview() {
    TooltipExamples()
}

@OptIn(ExperimentalMaterial3Api::class)
// [START android_compose_components_plaintooltipexample]
@Composable
fun PlainTooltipExample(
    modifier: Modifier = Modifier,
    plainTooltipText: String = "Add to favorites"
) {
    var tooltipState by remember { mutableStateOf(TooltipState()) }
    TooltipBox(
        modifier = modifier,
        positionProvider = TooltipDefaults.rememberPlainTooltipPositionProvider(),
        tooltip = {
            PlainTooltip { Text(plainTooltipText) }
        },
        state = tooltipState
    ) {
        IconButton(onClick = { /* Do something... */ }) {
            Icon(
                imageVector = Icons.Filled.Favorite,
                contentDescription = "Localized Description"
            )
        }
    }

    // Reset tooltipState after closing the tooltip.
    LaunchedEffect(tooltipState.isVisible) {
        if (!tooltipState.isVisible) {
            tooltipState = TooltipState()
        }
    }
}

// [END android_compose_components_plaintooltipexample]

@Preview
@Composable
private fun PlainTooltipSamplePreview() {
    PlainTooltipExample()
}

@OptIn(ExperimentalMaterial3Api::class)
// [START android_compose_components_richtooltipexample]
@Composable
fun RichTooltipExample(
    modifier: Modifier = Modifier,
    richTooltipSubheadText: String = "Rich Tooltip",
    richTooltipText: String = "Rich tooltips support multiple lines of informational text."
) {
    var tooltipState by remember { mutableStateOf(TooltipState(isPersistent = true)) }

    TooltipBox(
        modifier = modifier,
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            RichTooltip(
                title = { Text(richTooltipSubheadText) }
            ) {
                Text(richTooltipText)
            }
        },
        state = tooltipState
    ) {
        IconButton(onClick = { /* Icon button's click event */ }) {
            Icon(
                imageVector = Icons.Filled.Info,
                contentDescription = "Show more information"
            )
        }
    }

    // Reset tooltipState after closing the tooltip.
    LaunchedEffect(tooltipState.isVisible) {
        if (!tooltipState.isVisible) {
            tooltipState = TooltipState(isPersistent = true)
        }
    }
}
// [END android_compose_components_richtooltipexample]

@Preview
@Composable
private fun RichTooltipSamplePreview() {
    RichTooltipExample()
}

@OptIn(ExperimentalMaterial3Api::class)
// [START android_compose_components_advancedrichtooltipexample]
@Composable
fun AdvancedRichTooltipExample(
    modifier: Modifier = Modifier,
    richTooltipSubheadText: String = "Custom Rich Tooltip",
    richTooltipText: String = "Rich tooltips support multiple lines of informational text.",
    richTooltipActionText: String = "Dismiss"
) {
    var tooltipState by remember { mutableStateOf(TooltipState(isPersistent = true)) }

    TooltipBox(
        modifier = modifier,
        positionProvider = TooltipDefaults.rememberRichTooltipPositionProvider(),
        tooltip = {
            RichTooltip(
                title = { Text(richTooltipSubheadText) },
                action = {
                    Row {
                        TextButton(onClick = { tooltipState.dismiss() }) {
                            Text(richTooltipActionText)
                        }
                    }
                },
                caretSize = DpSize(32.dp, 16.dp)
            ) {
                Text(richTooltipText)
            }
        },
        state = tooltipState
    ) {
        IconButton(onClick = { tooltipState.dismiss() }) {
            Icon(
                imageVector = Icons.Filled.Camera,
                contentDescription = "Localized Description"
            )
        }
    }

    // Reset tooltipState after closing the tooltip.
    LaunchedEffect(tooltipState.isVisible) {
        if (!tooltipState.isVisible) {
            tooltipState = TooltipState(isPersistent = true)
        }
    }
}
// [END android_compose_components_advancedrichtooltipexample]

@Preview
@Composable
private fun RichTooltipWithCustomCaretSamplePreview() {
    AdvancedRichTooltipExample()
}
