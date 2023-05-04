package com.example.compose.snippets.touchinput.focus

import androidx.compose.foundation.Indication
import androidx.compose.foundation.IndicationInstance
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridSpanLayoutProvider.LazyGridItemSpanScopeImpl.maxLineSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color.Companion.Blue
import androidx.compose.ui.graphics.Color.Companion.Green
import androidx.compose.ui.graphics.Color.Companion.Red
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.node.CanFocusChecker.down
import androidx.compose.ui.node.CanFocusChecker.next
import androidx.compose.ui.node.CanFocusChecker.right
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
@Composable
private fun BasicSample() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // [START android_compose_touchinput_focus_horizontal]
        Column {
            Row {
                TextButton({ }) { Text("First field") }
                TextButton({ }) { Text("Second field") }
            }
            Row {
                TextButton({ }) { Text("Third field") }
                TextButton({ }) { Text("Fourth field") }
            }
        }
        // [END android_compose_touchinput_focus_horizontal]
    }
}

@Preview
@Composable
private fun BasicSample2() {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        // [START android_compose_touchinput_focus_vertical]
        Row {
            Column {
                TextButton({ }) { Text("First field") }
                TextButton({ }) { Text("Second field") }
            }
            Column {
                TextButton({ }) { Text("Third field") }
                TextButton({ }) { Text("Fourth field") }
            }
        }
        // [END android_compose_touchinput_focus_vertical]
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Preview
@Composable
fun OverrideDefaultOrder() {
    // [START android_compose_touchinput_focus_override_refs]
    val (first, second, third, fourth) = remember { FocusRequester.createRefs() }
    // [END android_compose_touchinput_focus_override_refs]
    // [START android_compose_touchinput_focus_override]
    Column {
        Row {
            Button(Modifier.focusRequester(first)) { Text("First field") }
            Button(Modifier.focusRequester(third)) { Text("Third field") }
        }

        Row {
            Button(Modifier.focusRequester(second)) { Text("Second field") }
            Button(Modifier.focusRequester(fourth)) { Text("Fourth field") }
        }
    }
    // [END android_compose_touchinput_focus_override]

    // [START android_compose_touchinput_focus_override_use]
    Column {
        Row {
            Button(
              Modifier
                .focusRequester(first)
                .focusProperties { next = second }
            ) {
                Text("First field")
            }
            Button(
              Modifier
                .focusRequester(third)
                .focusProperties { next = fourth }
            ) {
                Text("Third field")
            }
        }

        Row {
            Button(
              Modifier
                .focusRequester(second)
                .focusProperties { next = third }
            ) {
                Text("Second field")
            }
            Button(
              Modifier
                .focusRequester(fourth)
                .focusProperties { next = first }
            ) {
                Text("Fourth field")
            }
        }
    }
    // [END android_compose_touchinput_focus_override_use]
}

@Preview
@Composable
fun OverrideTwoDimensionalOrder() {
    // [START android_compose_touchinput_focus_override_2d]
    Button(modifier = Modifier
      .focusRequester(fourth)
      .focusProperties {

        down = third
        right = second
      }
    ) {
        ...
    }
    // [END android_compose_touchinput_focus_override_2d]
}

@Composable
private fun FocusGroup() {
    // [START android_compose_touchinput_focus_group]
    LazyVerticalGrid(columns = GridCells.Fixed(4)) {
        item(span = { GridItemSpan(maxLineSpan) }) {
            Row(modifier = Modifier.focusGroup()) {
                FilterChipA()
                FilterChipB()
                FilterChipC()
            }
        }
        items(chocolates) {
            SweetsCard(sweets = it)
        }
    }
    // [END android_compose_touchinput_focus_group]

}

@Composable
private fun Focusable() {
    // [START android_compose_touchinput_focus_focusable]
    val color by remember { mutableStateOf(Green) }
    Box(
      Modifier
        .background(color)
        .onFocusChanged { color = if (it.isFocused) Blue else Green }
        .focusable()
    ) {
        Text("Focusable 1")
    }
    // [END android_compose_touchinput_focus_focusable]
}

@Composable
private fun Unfocusable() {
    // [START android_compose_touchinput_focus_unfocusable]
    var checked by remember { mutableStateOf(false) }

    Switch(
        checked = checked,
        onCheckedChange = { checked = it },
        // Prevent component from being focused
        modifier = Modifier
            .focusProperties { canFocus = false }
    )
    // [END android_compose_touchinput_focus_unfocusable]
}

@Composable
private fun RequestFocus() {
    // [START android_compose_touchinput_focus_request]
    val focusRequester = remember { FocusRequester() }

    TextField(modifier = Modifier.focusRequester(focusRequester))
    // [END android_compose_touchinput_focus_request]
}

@Composable
private fun RequestFocus2() {
    // [START android_compose_touchinput_focus_request2]
    val focusRequester = remember { FocusRequester() }
    var text by remember { mutableStateOf("") }

    TextField(
        value = text,
        onValueChange = { text = it },
        modifier = Modifier.focusRequester(focusRequester)
    )

    Button(onClick = { focusRequester.requestFocus() }) {
        Text("Request focus on TextField")
    }
    // [END android_compose_touchinput_focus_request2]
}

@Composable
private fun Capture() {
    // [START android_compose_touchinput_focus_capture]
    val textField = FocusRequester()

    TextField(
        value = text,
        onValueChange = {
            text = it

            if (it.text.length > 3) {
                textField.captureFocus()
            } else {
                textField.freeFocus()
            }
        },
        modifier = Modifier.focusRequester(textField)
    )
    // [END android_compose_touchinput_focus_capture]
}

@Composable
private fun ModifierOrder() {
    // [START android_compose_touchinput_focus_order_1]
  Modifier
    .focusProperties { right = item1 }
    .focusProperties { right = item2 }
    .focusable()
    // [END android_compose_touchinput_focus_order_1]
    // [START android_compose_touchinput_focus_order_2]
  Modifier
    .focusProperties { right = Default }
    .focusProperties { right = item1 }
    .focusProperties { right = item2 }
    .focusable()
    // [END android_compose_touchinput_focus_order_2]
}

// [START android_compose_touchinput_focus_order_overwrite]
@Composable
fun FancyButton(modifier: Modifier = Modifier) {
    Row(modifier.focusProperties { canFocus = false }) {
        Text("Click me")
        Button(onClick = { … }) { Text("OK") }
    }
}
// [END android_compose_touchinput_focus_order_overwrite]

@Composable
private fun CallFancyButton() {
// [START android_compose_touchinput_focus_order_overwrite_call]
    FancyButton(Modifier.focusProperties { canFocus = true })
// [END android_compose_touchinput_focus_order_overwrite_call]
}

@Composable
private fun ModifierOrder2() {
    // [START android_compose_touchinput_focus_order_request_1]
    Box(
      Modifier
        .focusable()
        .focusRequester()
        .onFocusChanged {}
    )
    // [END android_compose_touchinput_focus_order_request_1]
    // [START android_compose_touchinput_focus_order_request_2]
    Box(
      Modifier
        .onFocusChanged {}
        .focusRequester()
        .focusable()
    )
    // [END android_compose_touchinput_focus_order_request_2]
    // [START android_compose_touchinput_focus_order_request_3]
    Box(
      Modifier
        .focusRequester()
        .onFocusChanged {}
        .focusable()
    )
    // [END android_compose_touchinput_focus_order_request_3]
}

@Composable
private fun RedirectFocus() {
    // [START android_compose_touchinput_focus_redirect]
    val otherComposable = remember { FocusRequester() }

    Modifier.focusProperties {
        exit = { focusDirection ->
            when (focusDirection) {
                Right -> Cancel
                Down -> otherComposable
                else -> Default
            }
        }
    }
    // [END android_compose_touchinput_focus_redirect]

}

@Composable
private fun FocusAdvancing() {
    // [START android_compose_touchinput_focus_advancing]
    val focusManager = LocalFocusManager.current
    TextField(
        modifier = Modifier.onPreviewKeyEvent {
            when {
                KeyEventType.KeyUp == it.type && Key.Tab == it.key -> {
                    focusManager.moveFocus(FocusDirection.Next)
                    true
                }

                else -> false
            }
        }
    )
    // [END android_compose_touchinput_focus_advancing]
}

@Composable
private fun ReactToFocus() {
    // [START android_compose_touchinput_focus_react]
    var color by remember { mutableStateOf(Color.White) }
    Card(
        modifier = Modifier.onFocusChanged {
            color = if (it.isFocus) Red else White
        }.border(5.dp, color),
        onClick = { ... }
    )
    // [END android_compose_touchinput_focus_react]
}

// [START android_compose_touchinput_focus_advanced_cues]
private class MyHighlightIndicationInstance(isEnabledState: State<Boolean>) :
  IndicationInstance {
    private val isEnabled by isEnabledState
    override fun ContentDrawScope.drawIndication() {
        drawContent()
        if (isEnabled) {
            drawRect(size = size, color = Color.White, alpha = 0.2f)
        }
    }
}
// [END android_compose_touchinput_focus_advanced_cues]

// [START android_compose_touchinput_focus_indication]
class MyHighlightIndication : Indication {
    @Composable
    override fun rememberUpdatedInstance(interactionSource: InteractionSource):
            IndicationInstance {
        val isFocusedState = interactionSource.collectIsFocusedAsState()
        return remember(interactionSource) {
            MyHighlightIndicationInstance(isEnabledState = isFocusedState)
        }
    }
}
// [END android_compose_touchinput_focus_indication]

@Composable
private fun ApplyIndication() {
    // [START android_compose_touchinput_focus_apply_indication]
    val highlightIndication = remember { MyHighlightIndication() }
    var interactionSource = remember { MutableInteractionSource() }

    Card(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = highlightIndication,
                enabled = true,
                onClick = { … }
            )
    )
    // [END android_compose_touchinput_focus_apply_indication]
}