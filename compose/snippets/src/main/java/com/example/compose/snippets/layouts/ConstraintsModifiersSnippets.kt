/*
 * Copyright 2023 The Android Open Source Project
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

package com.example.compose.snippets.layouts

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.R

val containerModifier = Modifier.requiredSize(300.dp, 200.dp).background(Color(0xFFD1F1FE))

@Preview
@Composable
private fun Demo1() {
    Box(containerModifier, contentAlignment = Alignment.Center) {
        // [START android_compose_layout_constraints_modifiers_1]
        Image(
            painterResource(R.drawable.hero),
            contentDescription = null,
            Modifier
                .fillMaxSize()
                .size(50.dp)
        )
        // [END android_compose_layout_constraints_modifiers_1]
    }
}

@Preview
@Composable
private fun Demo2() {
    Box(containerModifier, contentAlignment = Alignment.Center) {
        // [START android_compose_layout_constraints_modifiers_2]
        Image(
            painterResource(R.drawable.hero),
            contentDescription = null,
            Modifier
                .fillMaxSize()
                .wrapContentSize()
                .size(50.dp)
        )
        // [END android_compose_layout_constraints_modifiers_2]
    }
}

@Preview
@Composable
private fun Demo3() {
    Box(containerModifier, contentAlignment = Alignment.Center) {
        // [START android_compose_layout_constraints_modifiers_3]
        Image(
            painterResource(R.drawable.hero),
            contentDescription = null,
            Modifier
                .clip(CircleShape)
                .padding(10.dp)
                .size(100.dp)
        )
        // [END android_compose_layout_constraints_modifiers_3]
    }
}
