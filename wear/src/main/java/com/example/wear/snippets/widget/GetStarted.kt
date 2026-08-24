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

package com.example.wear.snippets.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.remote.creation.compose.action.Action
import androidx.compose.remote.creation.compose.layout.RemoteAlignment
import androidx.compose.remote.creation.compose.layout.RemoteBox
import androidx.compose.remote.creation.compose.layout.RemoteComposable
import androidx.compose.remote.creation.compose.layout.RemoteText
import androidx.compose.remote.creation.compose.modifier.RemoteModifier
import androidx.compose.remote.creation.compose.modifier.fillMaxSize
import androidx.compose.remote.creation.compose.modifier.padding
import androidx.compose.remote.creation.compose.state.RemoteColor
import androidx.compose.remote.creation.compose.state.rc
import androidx.compose.remote.creation.compose.state.rdp
import androidx.compose.remote.creation.compose.state.rs
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.glance.wear.AssociateWithGlanceWearWidget
import androidx.glance.wear.GlanceWearWidget
import androidx.glance.wear.GlanceWearWidgetService
import androidx.glance.wear.WearWidgetBrush
import androidx.glance.wear.WearWidgetData
import androidx.glance.wear.WearWidgetDocument
import androidx.glance.wear.color
import androidx.glance.wear.core.WearWidgetParams
import androidx.glance.wear.tooling.preview.RectangularLargeWidgetPreviewParams
import androidx.glance.wear.tooling.preview.RoundAllWidgetPreviewParams
import androidx.glance.wear.tooling.preview.SquircleAllWidgetPreviewParams
import androidx.glance.wear.tooling.preview.WearWidgetPreview
import androidx.wear.compose.remote.material3.RemoteCard

// [START android_wear_widget_service]
@AssociateWithGlanceWearWidget(HelloWidget::class)
class HelloWidgetService : GlanceWearWidgetService() {
    override val widget: GlanceWearWidget = HelloWidget()
}
// [END android_wear_widget_service]

// [START android_wear_widget_glance]
class HelloWidget : GlanceWearWidget() {
    override suspend fun provideWidgetData(
        context: Context,
        params: WearWidgetParams,
    ): WearWidgetData {
        return WearWidgetDocument(
            background = WearWidgetBrush.color(Color.Blue.rc),
        ) {
            HelloWidgetContent()
        }
    }
}
// [END android_wear_widget_glance]

// [START android_wear_widget_content]
@RemoteComposable
@Composable
fun HelloWidgetContent() {
    RemoteBox(
        modifier = RemoteModifier.fillMaxSize(),
        contentAlignment = RemoteAlignment.Center,
    ) {
        RemoteText(
            text = "Hello World".rs,
            color = Color.White.rc,
        )
    }
}
// [END android_wear_widget_content]

// [START android_wear_widget_modular_content]
@RemoteComposable
@Composable
fun ModularWidgetContent() {
    // Layer 2: Root container occupies full bounds without custom background clipping
    RemoteBox(
        modifier = RemoteModifier.fillMaxSize(),
        contentAlignment = RemoteAlignment.Center,
    ) {
        // Layer 3: Internal modular cards/buttons sit on top of the document canvas
        RemoteCard(
            onClick = Action.Empty,
            modifier = RemoteModifier.padding(16.rdp),
        ) {
            RemoteText(
                text = "Modular Card".rs,
                color = Color.White.rc,
            )
        }
    }
}
// [END android_wear_widget_modular_content]

// [START android_wear_widget_preview]
@Preview
@Composable
fun HelloWidgetPreview(
    @PreviewParameter(SquircleAllWidgetPreviewParams::class) params: WearWidgetParams,
) {
    WearWidgetPreview(
        widget = HelloWidget(),
        params = params,
    )
}
// [END android_wear_widget_preview]

// [START android_wear_widget_content_preview]
@Preview
@Composable
fun HelloWidgetContentPreview(
    @PreviewParameter(RoundAllWidgetPreviewParams::class) params: WearWidgetParams,
) {
    WearWidgetPreview(
        params = params,
        background = WearWidgetBrush.color(Color.Blue.rc),
    ) {
        HelloWidgetContent()
    }
}
// [END android_wear_widget_content_preview]

// [START android_wear_widget_catalog_preview]
@Preview(
    name = "Play Store Catalog Asset",
    device = "spec:width=1000dp,height=1000dp,dpi=320",
)
@Composable
fun HelloWidgetCatalogPreview(
    @PreviewParameter(RectangularLargeWidgetPreviewParams::class) params: WearWidgetParams,
) {
    WearWidgetPreview(
        widget = HelloWidget(),
        params = params,
    )
}
// [END android_wear_widget_catalog_preview]
