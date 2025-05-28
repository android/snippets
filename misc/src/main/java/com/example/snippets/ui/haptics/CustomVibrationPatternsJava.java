package com.example.snippets.ui.haptics;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;

@RequiresApi(api = Build.VERSION_CODES.M)
public class CustomVibrationPatternsJava extends Activity {
    Vibrator vibrator = getApplicationContext().getSystemService(Vibrator.class);

    // [START android_ui_haptics_ramp_up]
    @RequiresApi(Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.VIBRATE)
    private void rampUpPattern(Vibrator vibrator) {
        long[] timings = new long[] {
                50, 50, 50, 50, 50, 100, 350, 25, 25, 25, 25, 200 };
        int[] amplitudes = new int[] {
                33, 51, 75, 113, 170, 255, 0, 38, 62, 100, 160, 255 };
        int repeatIndex = -1; // Don't repeat.

        vibrator.vibrate(VibrationEffect.createWaveform(
                timings, amplitudes, repeatIndex));
    }
    // [END android_ui_haptics_ramp_up]

    @RequiresApi(api = Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.VIBRATE)
    // [START android_ui_haptics_repeat]
    private void startVibrating() {
        long[] timings = new long[] { 50, 50, 100, 50, 50 };
        int[] amplitudes = new int[] { 64, 128, 255, 128, 64 };
        int repeat = 1; // Repeat from the second entry, index = 1.
        VibrationEffect repeatingEffect = VibrationEffect.createWaveform(
                timings, amplitudes, repeat);
            // repeatingEffect can be used in multiple places.

        vibrator.vibrate(repeatingEffect);
    }

    // [START_EXCLUDE]
    @RequiresPermission(Manifest.permission.VIBRATE)
    // [END_EXCLUDE]
    private void stopVibrating() {
        vibrator.cancel();
    }
    // [END android_ui_haptics_repeat]


    // [START android_ui_haptics_fallback]
    @RequiresApi(api = Build.VERSION_CODES.O)
    @RequiresPermission(Manifest.permission.VIBRATE)
    private void patternWithFallback() {
        long[] smoothTimings = new long[] { 50, 50, 100, 50, 50 };
        long[] onOffTimings = new long[] { 50, 100 };
        int[] amplitudes = new int[] { 64, 128, 255, 128, 64 };
        int repeatIndex = -1; // Don't repeat.

        if (vibrator.hasAmplitudeControl()) {
            vibrator.vibrate(VibrationEffect.createWaveform(
                    smoothTimings, amplitudes, repeatIndex));
        } else {
            vibrator.vibrate(VibrationEffect.createWaveform(
                    onOffTimings, repeatIndex));
        }
    }
    // [END android_ui_haptics_fallback]
}
