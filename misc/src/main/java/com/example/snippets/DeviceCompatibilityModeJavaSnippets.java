/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.snippets;

import android.graphics.Rect;
import android.os.Build.VERSION_CODES;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity
import androidx.window.layout.WindowMetricsCalculator;

public class DeviceCompatibilityModeJavaSnippets {

    @RequiresApi(api=VERSION_CODES.N)
    // [START android_device_compatibility_mode_isLetterboxed_java]
    public boolean isLetterboxed(AppCompatActivity activity) {
        if (activity.isInMultiWindowMode()) {
            return false;
        }

        WindowMetricsCalculator wmc = WindowMetricsCalculator.getOrCreate();
        Rect currentBounds = wmc.computeCurrentWindowMetrics(activity).getBounds();
        Rect maxBounds = wmc.computeMaximumWindowMetrics(activity).getBounds();

        boolean isScreenPortrait = maxBounds.height() > maxBounds.width();

        return (isScreenPortrait)
            ? currentBounds.height() < maxBounds.height()
            : currentBounds.width() < maxBounds.width();
    }
    // [END android_device_compatibility_mode_isLetterboxed_java]

}
