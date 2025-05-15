package com.example.compose.snippets.components

import android.graphics.drawable.Icon
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import kotlinx.coroutines.delay
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import com.example.compose.snippets.R
import androidx.compose.ui.res.painterResource

@Preview
@Composable
fun ToggleIconButtonExample() {
    // isToggled initial value should be read from a view model or persistent storage.
    var isToggled by rememberSaveable { mutableStateOf(false) }

    IconButton(
        onClick = { isToggled = !isToggled }
    ) {
        Icon(
            imageVector = if (isToggled) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
            contentDescription = if (isToggled) "Selected icon button" else "Unselected icon button."
        )
    }
}
@Composable
fun MomentaryIconButton(
    unselectedImage: Int,
    selectedImage: Int,
    contentDescription: String,
    modifier: Modifier = Modifier,
    stepDelay: Long = 100L, // Minimum value is 1L milliseconds.
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val pressedListener by rememberUpdatedState(onClick)

    LaunchedEffect(isPressed) {
        while (isPressed) {
            delay(stepDelay.coerceIn(1L, Long.MAX_VALUE))
            pressedListener()
        }
    }

    IconButton(
        modifier = modifier,
        onClick = onClick,
        interactionSource = interactionSource
    ) {
        Icon(
            painter =  if (isPressed)  painterResource(id = selectedImage) else painterResource(id = unselectedImage) ,
            contentDescription = contentDescription,
        )
    }
}

@Preview()
@Composable
fun MomentaryIconButtonExample() {
    var pressedCount by remember { mutableIntStateOf(0) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MomentaryIconButton(
            unselectedImage = R.drawable.fast_rewind,
            selectedImage = R.drawable.fast_rewind_filled,
            stepDelay = 100L,
            onClick = { pressedCount -= 1 },
            contentDescription = "Decrease count button"
        )
        Spacer(modifier = Modifier)
        Text("advanced by $pressedCount frames")
        Spacer(modifier = Modifier)
        MomentaryIconButton(
            unselectedImage = R.drawable.fast_forward,
            selectedImage = R.drawable.fast_forward_filled,
            contentDescription = "Increase count button",
            stepDelay = 100L,
            onClick = { pressedCount += 1 }
        )
    }
}
