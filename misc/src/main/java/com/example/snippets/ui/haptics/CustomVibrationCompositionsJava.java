package com.example.snippets.ui.haptics;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import android.os.VibrationEffect;
import android.os.Vibrator;

import androidx.annotation.RequiresApi;
import androidx.annotation.RequiresPermission;

@RequiresApi(Build.VERSION_CODES.S)
public class CustomVibrationCompositionsJava extends Activity {
    Vibrator vibrator = getApplicationContext().getSystemService(Vibrator.class);

    @RequiresPermission(Manifest.permission.VIBRATE)
    // [START android_ui_haptics_composed_vibration_effect]
    private void createComposedVibrationEffect() {
        vibrator.vibrate(
                VibrationEffect.startComposition()
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_SLOW_RISE)
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_CLICK)
                        .compose());
    }
    // [END android_ui_haptics_composed_vibration_effect]

    @RequiresPermission(Manifest.permission.VIBRATE)
    // [START android_ui_haptics_gap_between_primitives]
    private void gapBetweenPrimitives() {
        int delayMs = 100;
        vibrator.vibrate(
                VibrationEffect.startComposition()
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_SPIN, 0.8f)
                        .addPrimitive(VibrationEffect.Composition.PRIMITIVE_SPIN, 0.6f)
                        .addPrimitive(
                                VibrationEffect.Composition.PRIMITIVE_THUD, 1.0f, delayMs)
                        .compose());
    }
    // [END android_ui_haptics_gap_between_primitives]

    @RequiresPermission(Manifest.permission.VIBRATE)
    private void checkPrimitivesSupport() {
        // [START android_ui_haptics_check_single_primitive_support]
        int primitive = VibrationEffect.Composition.PRIMITIVE_LOW_TICK;

        if (vibrator.areAllPrimitivesSupported(primitive)) {
            vibrator.vibrate(VibrationEffect.startComposition()
                    .addPrimitive(primitive).compose());
        } else {
            // Play a predefined effect or custom pattern as a fallback.
        }
        // [END android_ui_haptics_check_single_primitive_support]

        // [START android_ui_haptics_check_multiple_primitives_support]
        boolean[] supported = vibrator.arePrimitivesSupported(
                VibrationEffect.Composition.PRIMITIVE_LOW_TICK,
                VibrationEffect.Composition.PRIMITIVE_TICK,
                VibrationEffect.Composition.PRIMITIVE_CLICK);
        // [END android_ui_haptics_check_multiple_primitives_support]
    }
}
