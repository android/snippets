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

package com.example.snippets.backgroundwork.integrationtesting

import android.content.Context
import android.util.Log
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import org.junit.Before
import org.junit.runner.RunWith

// [START android_backgroundwork_integration_testing_setup]
@RunWith(AndroidJUnit4::class)
class BasicInstrumentationTest {
    @Before
    fun setup() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val config = Configuration.Builder()
            .setMinimumLoggingLevel(Log.DEBUG)
            .setExecutor(SynchronousExecutor())
            .build()

        // Initialize WorkManager for instrumentation tests.
        WorkManagerTestInitHelper.initializeTestWorkManager(context, config)
    }
}
// [END android_backgroundwork_integration_testing_setup]

// EchoWorker would normally live in src/main; it's in androidTest only to sit beside its test setup.
// [START android_backgroundwork_integration_testing_echo_worker]
class EchoWorker(context: Context, parameters: WorkerParameters) :
    Worker(context, parameters) {
    override fun doWork(): Result {
        return when (inputData.size()) {
            0 -> Result.failure()
            else -> Result.success(inputData)
        }
    }
}
// [END android_backgroundwork_integration_testing_echo_worker]
