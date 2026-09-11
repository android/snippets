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

package com.example.compose.snippets.background

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result
import androidx.work.RxWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.testing.TestListenableWorkerBuilder
import androidx.work.testing.TestWorkerBuilder
import androidx.work.workDataOf
import io.reactivex.Single
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

private object SleepWorkerBasic {
    // [START android_background_testing_sleep_worker_basic]
    class SleepWorker(context: Context, parameters: WorkerParameters) :
        Worker(context, parameters) {

        override fun doWork(): Result {
            // Sleep on a background thread.
            Thread.sleep(1000)
            return Result.success()
        }
    }
    // [END android_background_testing_sleep_worker_basic]

    // [START android_background_testing_sleep_worker_test_basic]
    // Kotlin code uses the TestWorkerBuilder extension to build
    // the Worker
    @RunWith(AndroidJUnit4::class)
    class SleepWorkerTest {
        private lateinit var context: Context
        private lateinit var executor: Executor

        @Before
        fun setUp() {
            context = ApplicationProvider.getApplicationContext()
            executor = Executors.newSingleThreadExecutor()
        }

        @Test
        fun testSleepWorker() {
            val worker = TestWorkerBuilder<SleepWorker>(
                context = context,
                executor = executor
            ).build()

            val result = worker.doWork()
            assertThat(result, `is`(Result.success()))
        }
    }
    // [END android_background_testing_sleep_worker_test_basic]
}

private object SleepWorkerInputData {
    // [START android_background_testing_sleep_worker_input_data]
    class SleepWorker(context: Context, parameters: WorkerParameters) :
        Worker(context, parameters) {

        override fun doWork(): Result {
            // Sleep on a background thread.
            val sleepDuration = inputData.getLong(SLEEP_DURATION, 1000)
            Thread.sleep(sleepDuration)
            return Result.success()
        }

        companion object {
            const val SLEEP_DURATION = "SLEEP_DURATION"
        }
    }
    // [END android_background_testing_sleep_worker_input_data]

    // [START android_background_testing_sleep_worker_test_input_data]
    // Kotlin code uses the TestWorkerBuilder extension to build
    // the Worker
    @RunWith(AndroidJUnit4::class)
    class SleepWorkerTest {
        private lateinit var context: Context
        private lateinit var executor: Executor

        @Before
        fun setUp() {
            context = ApplicationProvider.getApplicationContext()
            executor = Executors.newSingleThreadExecutor()
        }

        @Test
        fun testSleepWorker() {
            val worker = TestWorkerBuilder<SleepWorker>(
                context = context,
                executor = executor,
                inputData = workDataOf("SLEEP_DURATION" to 1000L)
            ).build()

            val result = worker.doWork()
            assertThat(result, `is`(Result.success()))
        }
    }
    // [END android_background_testing_sleep_worker_test_input_data]
}

private object SleepWorkerCoroutine {
    // [START android_background_testing_coroutine_worker]
    class SleepWorker(context: Context, parameters: WorkerParameters) :
        CoroutineWorker(context, parameters) {
        override suspend fun doWork(): Result {
            delay(1000L) // milliseconds
            return Result.success()
        }
    }
    // [END android_background_testing_coroutine_worker]

    // [START android_background_testing_coroutine_worker_test]
    @RunWith(AndroidJUnit4::class)
    class SleepWorkerTest {
        private lateinit var context: Context

        @Before
        fun setUp() {
            context = ApplicationProvider.getApplicationContext()
        }

        @Test
        fun testSleepWorker() {
            val worker = TestListenableWorkerBuilder<SleepWorker>(context).build()
            runBlocking {
                val result = worker.doWork()
                assertThat(result, `is`(Result.success()))
            }
        }
    }
    // [END android_background_testing_coroutine_worker_test]
}

private object SleepWorkerRx {
    // [START android_background_testing_rx_worker]
    class SleepWorker(
        context: Context,
        parameters: WorkerParameters
    ) : RxWorker(context, parameters) {
        override fun createWork(): Single<Result> {
            return Single.just(Result.success())
                .delay(1000L, TimeUnit.MILLISECONDS)
        }
    }
    // [END android_background_testing_rx_worker]

    // [START android_background_testing_rx_worker_test]
    @RunWith(AndroidJUnit4::class)
    class SleepWorkerTest {
        private lateinit var context: Context

        @Before
        fun setUp() {
            context = ApplicationProvider.getApplicationContext()
        }

        @Test
        fun testSleepWorker() {
            val worker = TestListenableWorkerBuilder<SleepWorker>(context).build()
            worker.createWork().subscribe { result ->
                assertThat(result, `is`(Result.success()))
            }
        }
    }
    // [END android_background_testing_rx_worker_test]
}
