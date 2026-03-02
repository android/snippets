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

import androidx.appcompat.app.AppCompatActivity;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.assertFalse;

public class DeviceCompatibilityModeTestJavaSnippets {

    // [START android_device_compatibility_mode_assert_isLetterboxed_java]
    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    @Test
    public void activity_launched_notLetterBoxed() {
        try (ActivityScenario<MainActivity> scenario =
            ActivityScenario.launch(MainActivity.class)) {
                scenario.onActivity( activity -> {
                    assertFalse(activity.isLetterboxed());
                });
            }
    }
    // [END android_device_compatibility_mode_assert_isLetterboxed_java]


    // Class used by snippets.

    class MainActivity extends AppCompatActivity {
        public boolean isLetterboxed() {
            return true;
        }
    }
}
