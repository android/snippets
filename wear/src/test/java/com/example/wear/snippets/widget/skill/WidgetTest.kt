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

package com.example.wear.snippets.widget.skill

// [START android_wear_widget_skill_unit_test]
import android.annotation.SuppressLint
import android.content.Context
import androidx.glance.wear.core.ContainerInfo
import androidx.glance.wear.core.WearWidgetParams
import androidx.glance.wear.core.WidgetInstanceId
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@SuppressLint("RestrictedApi")
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MyWidgetTest {
    @Test
    fun provideWidgetData_serializesRemoteComposeDocument() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val widget = MyWidget()
        for (containerType in listOf(
            ContainerInfo.CONTAINER_TYPE_SMALL,
            ContainerInfo.CONTAINER_TYPE_LARGE,
        )) {
            val params = WearWidgetParams(
                instanceId = WidgetInstanceId(namespace = "test", id = 1),
                containerType = containerType,
                widthDp = 220f,
                heightDp = 120f,
                horizontalPaddingDp = 8f,
                verticalPaddingDp = 8f,
                cornerRadiusDp = 16f,
            )
            val data = widget.provideWidgetData(context, params)
            val rawContent = data.captureRawContent(context, params)
            assertNotNull(rawContent.rcDocument)
            assertTrue(rawContent.rcDocument.isNotEmpty())
        }
    }
}
// [END android_wear_widget_skill_unit_test]
