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
