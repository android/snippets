package com.example.snippets.backgroundwork;

import android.app.Activity;
import android.os.Bundle;
import android.os.PowerManager;

import androidx.annotation.Nullable;

public class WakeLockSnippetsJava extends Activity {

    PowerManager.WakeLock wakeLock;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {

        // [START android_backgroundwork_wakelock_create_java]
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        PowerManager.WakeLock wakeLock =
                powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyClassName::MyWakelockTag");
        wakeLock.acquire();
        // [END android_backgroundwork_wakelock_create_java]

        super.onCreate(savedInstanceState);
    }

    // [START android_backgroundwork_wakelock_release_java]
    void doSomethingAndRelease() throws MyException {
        try {
            wakeLock.acquire();
            doTheWork();
        } finally {
            wakeLock.release();
        }
    }
    // [END android_backgroundwork_wakelock_release_java]

    private void doTheWork() {
    }
}
