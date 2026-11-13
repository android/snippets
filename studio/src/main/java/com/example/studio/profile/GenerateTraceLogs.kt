/*
 * Copyright 2026 The Android Open Source Project
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

package com.example.studio.profile

import android.os.Debug
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun startAndStopTracing() {
    // [START android_studio_profile_generate_trace_logs_start_stop]
    // Starts recording a trace log with the name you provide. For example, the
    // following code tells the system to start recording a .trace file to the
    // device with the name "sample.trace".
    Debug.startMethodTracing("sample")

    // The system begins buffering the generated trace data, until your
    // application calls <code><a href="/reference/android/os/Debug.html#stopMethodTracing()">stopMethodTracing()</a></code>, at which time it writes
    // the buffered data to the output file.
    Debug.stopMethodTracing()
    // [END android_studio_profile_generate_trace_logs_start_stop]
}

fun saveMultipleLogs() {
    // [START android_studio_profile_generate_trace_logs_save_multiple]
    // Uses the <code><a href="/reference/java/text/SimpleDateFormat.html">SimpleDateFormat</a></code> class to create a String with
    // the current date and time.
    val dateFormat: DateFormat = SimpleDateFormat("dd_MM_yyyy_hh_mm_ss", Locale.getDefault())
    val logDate: String = dateFormat.format(Date())
    // Applies the date and time to the name of the trace log.
    Debug.startMethodTracing("sample-$logDate")
    // [END android_studio_profile_generate_trace_logs_save_multiple]
}
