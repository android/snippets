package com.example.compose.snippets.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerExamples() {
    var showDialExample by remember { mutableStateOf(false) }
    var showInputExample by remember { mutableStateOf(false) }

    var selectedTime: TimePickerState? by remember { mutableStateOf(null) }

    val formatter = remember { SimpleDateFormat("hh:mm a", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp),
    ) {
        Button(onClick = {
            showDialExample = true
            showInputExample = false
        }) {
            Text("Dial")
        }
        Button(onClick = {
            showDialExample = false
            showInputExample = true
        }) {
            Text("Input")
        }
        if (selectedTime != null){
            val cal = Calendar.getInstance()
            cal.set(Calendar.HOUR_OF_DAY, selectedTime!!.hour)
            cal.set(Calendar.MINUTE, selectedTime!!.minute)
            cal.isLenient = false
            Text("Selected time = ${formatter.format(cal.time)}")
        }else{
            Text("No time selected.")
        }
    }

    when {
        showDialExample -> DialExample(
            onDismiss = { showDialExample = false },
            onConfirm = {
                time -> selectedTime = time
                showDialExample = false
            },
        )
        showInputExample -> InputExample(
            onDismiss = { showInputExample = false },
            onConfirm = {
                time -> selectedTime = time
                showInputExample = false
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
// [START android_compose_components_dial]
@Composable
fun DialExample(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    TimePickerDialog(
        onDismiss = { onDismiss() },
        onConfirm = { onConfirm(timePickerState) }
    ) {
        TimePicker(
            state = timePickerState,
        )
    }
}
// [START android_compose_components_dial]

@OptIn(ExperimentalMaterial3Api::class)
// [START android_compose_components_input]
@Composable
fun InputExample(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    TimePickerDialog(
        onDismiss = { onDismiss() },
        onConfirm = { onConfirm(timePickerState) }
    ) {
        TimeInput(
            state = timePickerState,
        )
    }
}
// [END android_compose_components_input]

// [START android_compose_components_timepickerdialog]
@Composable
fun TimePickerDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    content: @Composable () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton( onClick = { onDismiss() }) {
                Text("Dismiss")
            }
        },
        confirmButton =  {
            TextButton( onClick = { onConfirm() }) {
                Text("OK")
            }
        },
        text = { content() }
    )
}
// [END android_compose_components_timepickerdialog]