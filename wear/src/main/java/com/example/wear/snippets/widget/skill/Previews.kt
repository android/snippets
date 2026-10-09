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

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.glance.wear.core.WearWidgetParams
import androidx.glance.wear.tooling.preview.RectangularAllWidgetPreviewParams
import androidx.glance.wear.tooling.preview.RoundAllWidgetPreviewParams
import androidx.glance.wear.tooling.preview.SquircleAllWidgetPreviewParams
import androidx.glance.wear.tooling.preview.WearWidgetPreview

// [START android_wear_widget_skill_previews]
@Preview(
    name = "Squircle",
    device = "spec:width=1000dp,height=1000dp,dpi=320",
)
@Composable
fun MyWidgetSquirclePreview(
    @PreviewParameter(SquircleAllWidgetPreviewParams::class)
    params: WearWidgetParams,
) {
    WearWidgetPreview(MyWidget(), params)
}

@Preview(
    name = "Round",
    device = "spec:width=1000dp,height=1000dp,dpi=320",
)
@Composable
fun MyWidgetRoundPreview(
    @PreviewParameter(RoundAllWidgetPreviewParams::class)
    params: WearWidgetParams,
) {
    WearWidgetPreview(MyWidget(), params)
}

@Preview(
    name = "Widget Preview Asset",
    device = "spec:width=1000dp,height=1000dp,dpi=320",
)
@Composable
fun MyWidgetCatalogPreview(
    @PreviewParameter(RectangularAllWidgetPreviewParams::class)
    params: WearWidgetParams,
) {
    WearWidgetPreview(MyWidget(), params)
}
// [END android_wear_widget_skill_previews]
