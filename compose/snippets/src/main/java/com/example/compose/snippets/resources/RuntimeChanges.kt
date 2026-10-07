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

package com.example.compose.snippets.resources

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.os.ConfigurationCompat
import java.time.LocalDate
import java.time.format.DateTimeFormatter

// [START android_resources_runtime_changes_date_text]
@Composable
fun DateText(year: Int, dayOfYear: Int) {
    val dateTimeFormatter = DateTimeFormatter.ofPattern(
        "MMM dd",
        ConfigurationCompat.getLocales(LocalConfiguration.current)[0]
    )
    Text(
        dateTimeFormatter.format(LocalDate.ofYearDay(year, dayOfYear))
    )
}
// [END android_resources_runtime_changes_date_text]
