package com.example.compose.snippets.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
            Text("Dial time picker")
        }
        Button(onClick = {
            showDialExample = false
            showInputExample = true
        }) {
            Text("Input time picker")
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
// [END android_compose_components_dial]

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