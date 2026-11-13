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

package com.example.studio.write

// [START android_studio_write_annotations_typedef_flags]
import androidx.annotation.IntDef
// ...

@IntDef(
    flag = true,
    value = [
        DISPLAY_USE_LOGO,
        DISPLAY_SHOW_HOME,
        DISPLAY_HOME_AS_UP,
        DISPLAY_SHOW_TITLE,
        DISPLAY_SHOW_CUSTOM
    ]
)
@Retention(AnnotationRetention.SOURCE)
annotation class DisplayOptions
// ...
// [END android_studio_write_annotations_typedef_flags]

private const val DISPLAY_USE_LOGO = 1
private const val DISPLAY_SHOW_HOME = 2
private const val DISPLAY_HOME_AS_UP = 4
private const val DISPLAY_SHOW_TITLE = 8
private const val DISPLAY_SHOW_CUSTOM = 16
