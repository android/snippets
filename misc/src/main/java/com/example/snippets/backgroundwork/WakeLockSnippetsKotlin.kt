package com.example.snippets.backgroundwork

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.os.PowerManager

// Snippets for doc page go here
class WakeLockSnippetsKotlin : Activity() {

    // [START android_backgroundwork_wakelock_create_kotlin]
    val wakeLock: PowerManager.WakeLock =
        (getSystemService(Context.POWER_SERVICE) as PowerManager).run {
            newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "MyClassName::MyWakelockTag").apply {
                acquire()
            }

        }
    // [END android_backgroundwork_wakelock_create_kotlin]

    override fun onCreate(savedInstanceState: Bundle?) {


        super.onCreate(savedInstanceState)
    }

    // [START android_backgroundwork_wakelock_release_kotlin]
    @Throws(MyException::class)
    fun doSomethingAndRelease() {
        wakeLock.apply {
            try {
                acquire()
                doTheWork()
            } finally {
                release()
            }
        }
    }
    // [END android_backgroundwork_wakelock_release_kotlin]

    private fun doTheWork() {

    }

}
