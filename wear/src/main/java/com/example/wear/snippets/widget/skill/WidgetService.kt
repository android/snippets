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

// [START android_wear_widget_skill_service]
import android.content.Context
// [START_EXCLUDE silent]
import androidx.compose.remote.creation.compose.layout.RemoteAlignment
import androidx.compose.remote.creation.compose.layout.RemoteBox
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.layout.RemoteText
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
// [END_EXCLUDE]
import androidx.compose.remote.creation.compose.state.rc
// [START_EXCLUDE silent]
import androidx.compose.remote.creation.compose.state.rs
import androidx.compose.runtime.Composable
// [END_EXCLUDE]
import androidx.compose.ui.graphics.Color
import androidx.glance.wear.AssociateWithGlanceWearWidget
import androidx.glance.wear.GlanceWearWidget
import androidx.glance.wear.GlanceWearWidgetService
import androidx.glance.wear.WearWidgetBrush
import androidx.glance.wear.WearWidgetData
import androidx.glance.wear.WearWidgetDocument
import androidx.glance.wear.color
import androidx.glance.wear.core.ContainerInfo
import androidx.glance.wear.core.WearWidgetParams

@AssociateWithGlanceWearWidget(MyWidget::class)
class MyWidgetService : GlanceWearWidgetService() {
    override val widget: GlanceWearWidget = MyWidget()
}

class MyWidget : GlanceWearWidget() {
    override suspend fun provideWidgetData(
        context: Context,
        params: WearWidgetParams,
    ): WearWidgetData {
        val isLarge = params.containerType == ContainerInfo.CONTAINER_TYPE_LARGE
        return WearWidgetDocument(
            background = WearWidgetBrush.color(Color(0xFF1C1B1F).rc)
        ) {
            MyWidgetContent(isLarge = isLarge)
        }
    }
}
// [END android_wear_widget_skill_service]

@RemoteComposable
@Composable
fun MyWidgetContent(isLarge: Boolean = false) {
    RemoteBox(
        modifier = RemoteModifier.fillMaxSize(),
        contentAlignment = RemoteAlignment.Center,
    ) {
        RemoteText(
            text = "Widget Content".rs,
            color = Color.White.rc,
        )
    }
}
