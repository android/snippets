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

package com.example.compose.snippets.test.othercomponents

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import androidx.test.core.app.ApplicationProvider
import androidx.test.rule.ServiceTestRule
import com.example.compose.snippets.test.othercomponents.LocalService.Companion.SEED_KEY
import java.util.concurrent.TimeoutException
import org.hamcrest.CoreMatchers.any
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Rule
import org.junit.Test

private class ServiceTest {
    // [START android_testing_other_components_services_rule]
    @get:Rule
    val serviceRule = ServiceTestRule()
    // [END android_testing_other_components_services_rule]

    // [START android_testing_other_components_services_bound]
    @Test
    @Throws(TimeoutException::class)
    fun testWithBoundService() {
        // Create the service Intent.
        val serviceIntent = Intent(
            ApplicationProvider.getApplicationContext<Context>(),
            LocalService::class.java
        ).apply {
            // Data can be passed to the service via the Intent.
            putExtra(SEED_KEY, 42L)
        }

        // Bind the service and grab a reference to the binder.
        val binder: IBinder = serviceRule.bindService(serviceIntent)

        // Get the reference to the service, or you can call
        // public methods on the binder directly.
        val service: LocalService = (binder as LocalService.LocalBinder).getService()

        // Verify that the service is working correctly.
        assertThat(service.getRandomInt(), `is`(any(Int::class.java)))
    }
    // [END android_testing_other_components_services_bound]
}

private class LocalService : Service() {
    companion object {
        const val SEED_KEY = "SEED_KEY"
    }

    inner class LocalBinder : Binder() {
        fun getService(): LocalService = this@LocalService
    }

    override fun onBind(intent: Intent?): IBinder = LocalBinder()

    fun getRandomInt(): Int = 42
}
