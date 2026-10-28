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

package com.example.snippets.backgroundwork.wakelock

import android.os.PowerManager
import com.example.snippets.backgroundwork.MyException

private class WakeLockReleaseBad(private val wakeLock: PowerManager.WakeLock) {
    // [START android_backgroundwork_wakelock_best_practices_release_bad]
    @Throws(MyException::class)
    fun doSomethingAndRelease() {
        wakeLock.apply {
            acquire()
            doTheWork() // can potentially throw MyException.
            release() // does not run if an exception is thrown.
        }
    }
    // [END android_backgroundwork_wakelock_best_practices_release_bad]

    private fun doTheWork() {}
}

private class WakeLockReleaseGood(private val wakeLock: PowerManager.WakeLock) {
    // [START android_backgroundwork_wakelock_best_practices_release_good]
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
    // [END android_backgroundwork_wakelock_best_practices_release_good]

    private fun doTheWork() {}
}
