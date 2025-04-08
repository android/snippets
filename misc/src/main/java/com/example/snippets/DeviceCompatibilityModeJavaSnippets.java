package com.example.snippets;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.window.layout.WindowMetricsCalculator;

public class DeviceCompatibilityModeJavaSnippets extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @RequiresApi(api=VERSION_CODES.N)
    // [START android_device_compatibility_mode_isLetterboxed_java]
    public boolean isLetterboxed(Activity activity) {
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