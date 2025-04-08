package com.example.snippets;

import android.app.Activity;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import org.junit.Rule;
import static org.junit.Assert.assertFalse;

public class DeviceCompatibilityModeTestJavaSnippets extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    // [START android_device_compatibility_mode_assert_isLetterboxed_java]
    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

    public void activity_launched_notLetterBoxed() {
        try (ActivityScenario<MainActivity> scenario =
            ActivityScenario.launch(MainActivity.class)) {
                scenario.onActivity( activity -> {
                    assertFalse(isLetterboxed(activity));
                });
            }
    }
    // [END android_device_compatibility_mode_assert_isLetterboxed_java]


    // Method used by snippets.
    public boolean isLetterboxed(Activity activity) {
        return true;
    }

}