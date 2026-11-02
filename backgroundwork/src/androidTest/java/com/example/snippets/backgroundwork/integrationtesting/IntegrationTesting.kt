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
import androidx.test.core.app.ApplicationProvider
import androidx.test.core.app.ApplicationProvider.getApplicationContext
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.work.Configuration
import androidx.work.Constraints
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.testing.SynchronousExecutor
import androidx.work.testing.WorkManagerTestInitHelper
import androidx.work.workDataOf
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeUnit.MINUTES
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

private const val KEY_1 = "KEY_1"
private const val KEY_2 = "KEY_2"

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

// EchoWorker would normally live in src/main; it's in androidTest only so the tests below can use it.
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

@RunWith(AndroidJUnit4::class)
class EchoWorkerTest {
    private val applicationContext: Context = ApplicationProvider.getApplicationContext()
    private val myContext: Context = applicationContext

    @Before
    fun setup() {
        val config = Configuration.Builder()
            .setExecutor(SynchronousExecutor())
            .build()
        WorkManagerTestInitHelper.initializeTestWorkManager(applicationContext, config)
    }

    // [START android_backgroundwork_integration_testing_simple_echo_worker]
    @Test
    @Throws(Exception::class)
    fun testSimpleEchoWorker() {
        // Define input data.
        val input = workDataOf(KEY_1 to 1, KEY_2 to 2)

        // Create request.
        val request = OneTimeWorkRequestBuilder<EchoWorker>()
            .setInputData(input)
            .build()

        val workManager = WorkManager.getInstance(applicationContext)
        // Enqueue and wait for result. This also runs the Worker synchronously
        // because we are using a SynchronousExecutor.
        workManager.enqueue(request).result.get()
        // Get WorkInfo and outputData.
        val workInfo = checkNotNull(workManager.getWorkInfoById(request.id).get())
        val outputData = workInfo.outputData

        // Assert.
        assertThat(workInfo.state, `is`(WorkInfo.State.SUCCEEDED))
        assertThat(outputData, `is`(input))
    }
    // [END android_backgroundwork_integration_testing_simple_echo_worker]

    // [START android_backgroundwork_integration_testing_echo_worker_no_input]
    @Test
    @Throws(Exception::class)
    fun testEchoWorkerNoInput() {
        // Create request.
        val request = OneTimeWorkRequestBuilder<EchoWorker>()
            .build()

        val workManager = WorkManager.getInstance(applicationContext)
        // Enqueue and wait for result. This also runs the Worker synchronously
        // because we are using a SynchronousExecutor.
        workManager.enqueue(request).result.get()
        // Get WorkInfo.
        val workInfo = checkNotNull(workManager.getWorkInfoById(request.id).get())

        // Assert.
        assertThat(workInfo.state, `is`(WorkInfo.State.FAILED))
    }
    // [END android_backgroundwork_integration_testing_echo_worker_no_input]

    // [START android_backgroundwork_integration_testing_initial_delay]
    @Test
    @Throws(Exception::class)
    fun testWithInitialDelay() {
        // Define input data.
        val input = workDataOf(KEY_1 to 1, KEY_2 to 2)

        // Create request.
        val request = OneTimeWorkRequestBuilder<EchoWorker>()
            .setInputData(input)
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()

        val workManager = WorkManager.getInstance(getApplicationContext())
        // Get the TestDriver.
        val testDriver = checkNotNull(WorkManagerTestInitHelper.getTestDriver(getApplicationContext()))
        // Enqueue.
        workManager.enqueue(request).result.get()
        // Tells the WorkManager test framework that initial delays are now met.
        testDriver.setInitialDelayMet(request.id)
        // Get WorkInfo and outputData.
        val workInfo = checkNotNull(workManager.getWorkInfoById(request.id).get())
        val outputData = workInfo.outputData

        // Assert.
        assertThat(workInfo.state, `is`(WorkInfo.State.SUCCEEDED))
        assertThat(outputData, `is`(input))
    }
    // [END android_backgroundwork_integration_testing_initial_delay]

    // [START android_backgroundwork_integration_testing_constraints]
    @Test
    @Throws(Exception::class)
    fun testWithConstraints() {
        // Define input data.
        val input = workDataOf(KEY_1 to 1, KEY_2 to 2)

        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        // Create request.
        val request = OneTimeWorkRequestBuilder<EchoWorker>()
            .setInputData(input)
            .setConstraints(constraints)
            .build()

        val workManager = WorkManager.getInstance(myContext)
        val testDriver = checkNotNull(WorkManagerTestInitHelper.getTestDriver(myContext))
        // Enqueue.
        workManager.enqueue(request).result.get()
        // Tells the testing framework that all constraints are met.
        testDriver.setAllConstraintsMet(request.id)
        // Get WorkInfo and outputData.
        val workInfo = checkNotNull(workManager.getWorkInfoById(request.id).get())
        val outputData = workInfo.outputData

        // Assert.
        assertThat(workInfo.state, `is`(WorkInfo.State.SUCCEEDED))
        assertThat(outputData, `is`(input))
    }
    // [END android_backgroundwork_integration_testing_constraints]

    // [START android_backgroundwork_integration_testing_periodic_work]
    @Test
    @Throws(Exception::class)
    fun testPeriodicWork() {
        // Define input data.
        val input = workDataOf(KEY_1 to 1, KEY_2 to 2)

        // Create request.
        val request = PeriodicWorkRequestBuilder<EchoWorker>(15, MINUTES)
            .setInputData(input)
            .build()

        val workManager = WorkManager.getInstance(myContext)
        val testDriver = checkNotNull(WorkManagerTestInitHelper.getTestDriver(myContext))
        // Enqueue and wait for result.
        workManager.enqueue(request).result.get()
        // Tells the testing framework the period delay is met.
        testDriver.setPeriodDelayMet(request.id)
        // Get WorkInfo and outputData.
        val workInfo = checkNotNull(workManager.getWorkInfoById(request.id).get())

        // Assert.
        assertThat(workInfo.state, `is`(WorkInfo.State.ENQUEUED))
    }
    // [END android_backgroundwork_integration_testing_periodic_work]
}
