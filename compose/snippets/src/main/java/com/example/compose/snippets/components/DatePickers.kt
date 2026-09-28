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

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import com.example.compose.snippets.ui.theme.SnippetsTheme
import kotlin.time.ExperimentalTime
import kotlin.time.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.format
import kotlinx.datetime.format.MonthNames
import kotlinx.datetime.format.char
import kotlinx.datetime.toLocalDateTime

@Preview
@Composable
private fun DatePickerPreview() {
    SnippetsTheme {
        DatePickerExamples()
    }
}

// [START android_compose_components_datepicker_examples]
// [START_EXCLUDE]
@OptIn(ExperimentalTime::class)
@Composable
fun DatePickerExamples() {
    var showModal by remember { mutableStateOf(false) }
    var showModalInput by remember { mutableStateOf(false) }
    var showRangeModal by remember { mutableStateOf(false) }
// [END_EXCLUDE]
    var selectedDate by remember { mutableStateOf<Long?>(null) }
// [START_EXCLUDE]
    var selectedDateRange by remember { mutableStateOf<Pair<Long?, Long?>>(null to null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Docked date picker:")
        DatePickerDocked()

        Text("Open modal picker on click")
        DatePickerFieldToModal()

        Text("Modal date pickers:")
        Button(onClick = { showModal = true }) {
            Text("Show Modal Date Picker")
        }
        Button(onClick = { showModalInput = true }) {
            Text("Show Modal Input Date Picker")
        }
// [END_EXCLUDE]
        if (selectedDate != null) {
            val date = Instant.fromEpochMilliseconds(selectedDate!!).toLocalDateTime(TimeZone.UTC).date
            val formattedDate = date.format(
                LocalDate.Format {
                    monthName(MonthNames.ENGLISH_ABBREVIATED)
                    char(' ')
                    day()
                    chars(", ")
                    year()
                }
            )
            Text("Selected date: $formattedDate")
        } else {
            Text("No date selected")
        }
// [START_EXCLUDE]

        Text("Date range pickers:")

        Button(onClick = { showRangeModal = true }) {
            Text("Show Modal Date Range Picker")
        }

        if (selectedDateRange.first != null && selectedDateRange.second != null) {
            val dateFormat = LocalDate.Format {
                monthName(MonthNames.ENGLISH_ABBREVIATED)
                char(' ')
                day()
                chars(", ")
                year()
            }
            val startDate = Instant.fromEpochMilliseconds(selectedDateRange.first!!).toLocalDateTime(TimeZone.UTC).date
            val endDate = Instant.fromEpochMilliseconds(selectedDateRange.second!!).toLocalDateTime(TimeZone.UTC).date
            val formattedStartDate = startDate.format(dateFormat)
            val formattedEndDate = endDate.format(dateFormat)
            Text("Selected date range: $formattedStartDate - $formattedEndDate")
        } else {
            Text("No date range selected")
        }
    }

    if (showModal) {
// [END_EXCLUDE]
        DatePickerModal(
            onDateSelected = {
                selectedDate = it
                showModal = false
            },
            onDismiss = { showModal = false }
        )
    }
// [START_EXCLUDE]

    if (showModalInput) {
        DatePickerModalInput(
            onDateSelected = {
                selectedDate = it
                showModalInput = false
            },
            onDismiss = { showModalInput = false }
        )
    }

    if (showRangeModal) {
        DateRangePickerModal(
            onDateRangeSelected = {
                selectedDateRange = it
                showRangeModal = false
            },
            onDismiss = { showRangeModal = false }
        )
    }
}
// [END android_compose_components_datepicker_examples]

@OptIn(ExperimentalMaterial3Api::class)
// [START android_compose_components_datepicker_modal]
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
// [END android_compose_components_datepicker_modal]

@OptIn(ExperimentalMaterial3Api::class)
// [START android_compose_components_datepicker_inputmodal]
@Composable
fun DatePickerModalInput(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState(initialDisplayMode = DisplayMode.Input)

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DatePicker(state = datePickerState)
    }
}
// [END android_compose_components_datepicker_inputmodal]

@OptIn(ExperimentalMaterial3Api::class)
// [START android_compose_components_datepicker_docked]
@Composable
fun DatePickerDocked() {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()
    val selectedDate = datePickerState.selectedDateMillis?.let {
        convertMillisToDate(it)
    } ?: ""

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedDate,
            onValueChange = { },
            label = { Text("DOB") },
            readOnly = true,
            trailingIcon = {
                IconButton(onClick = { showDatePicker = !showDatePicker }) {
                    Icon(
                        painter = rememberVectorPainter(AppIcons.DateRange),
                        contentDescription = "Select date"
                    )
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
        )

        if (showDatePicker) {
            Popup(
                onDismissRequest = { showDatePicker = false },
                alignment = Alignment.TopStart
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .offset(y = 64.dp)
                        .shadow(elevation = 4.dp)
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp)
                ) {
                    DatePicker(
                        state = datePickerState,
                        showModeToggle = false
                    )
                }
            }
        }
    }
}

@Composable
fun DatePickerFieldToModal(modifier: Modifier = Modifier) {
    var selectedDate by remember { mutableStateOf<Long?>(null) }
    var showModal by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = selectedDate?.let { convertMillisToDate(it) } ?: "",
        onValueChange = { },
        label = { Text("DOB") },
        placeholder = { Text("MM/DD/YYYY") },
        trailingIcon = {
            Icon(painter = rememberVectorPainter(AppIcons.DateRange), contentDescription = "Select date")
        },
        modifier = modifier
            .fillMaxWidth()
            .pointerInput(selectedDate) {
                awaitEachGesture {
                    // Modifier.clickable doesn't work for text fields, so we use Modifier.pointerInput
                    // in the Initial pass to observe events before the text field consumes them
                    // in the Main pass.
                    awaitFirstDown(pass = PointerEventPass.Initial)
                    val upEvent = waitForUpOrCancellation(pass = PointerEventPass.Initial)
                    if (upEvent != null) {
                        showModal = true
                    }
                }
            }
    )

    if (showModal) {
        DatePickerModal(
            onDateSelected = { selectedDate = it },
            onDismiss = { showModal = false }
        )
    }
}

@OptIn(ExperimentalTime::class)
fun convertMillisToDate(millis: Long): String {
    val formatter = LocalDate.Format {
        monthNumber()
        char('/')
        day()
        char('/')
        year()
    }
    val date = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.UTC).date
    return formatter.format(date)
}
// [END android_compose_components_datepicker_docked]

@OptIn(ExperimentalMaterial3Api::class)
// [START android_compose_components_datepicker_range]
@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        Pair(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                    )
                    onDismiss()
                }
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Select date range"
                )
            },
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp)
        )
    }
}
// [END android_compose_components_datepicker_range]
