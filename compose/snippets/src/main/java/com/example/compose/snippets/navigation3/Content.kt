/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.compose.snippets.navigation3

import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.compose.snippets.ui.theme.PastelBlue
import com.example.compose.snippets.ui.theme.PastelGreen
import com.example.compose.snippets.ui.theme.PastelMauve
import com.example.compose.snippets.ui.theme.PastelOrange
import com.example.compose.snippets.ui.theme.PastelPink
import com.example.compose.snippets.ui.theme.PastelPurple
import com.example.compose.snippets.ui.theme.PastelRed
import com.example.compose.snippets.ui.theme.PastelYellow

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun ContentBase(
    title: String,
    modifier: Modifier = Modifier,
    onNext: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(48.dp))
    ) {
        Title(title)
        if (content != null) content()
        if (onNext != null) {
            Button(
                modifier = Modifier.align(Alignment.CenterHorizontally),
                onClick = onNext
            ) {
                Text("Next")
            }
        }
    }
}

@Composable
fun ColumnScope.Title(title: String) {
    Text(
        modifier = Modifier
            .padding(24.dp)
            .align(Alignment.CenterHorizontally),
        fontWeight = FontWeight.Bold,
        text = title
    )
}

@Composable
fun ContentRed(
    title: String,
    modifier: Modifier = Modifier,
    onNext: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) = ContentBase(
    title = title,
    modifier = modifier.background(PastelRed),
    onNext = onNext,
    content = content
)

@Composable
fun ContentOrange(
    title: String,
    modifier: Modifier = Modifier,
    onNext: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) = ContentBase(
    title = title,
    modifier = modifier.background(PastelOrange),
    onNext = onNext,
    content = content
)

@Composable
fun ContentYellow(
    title: String,
    modifier: Modifier = Modifier,
    onNext: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) = ContentBase(
    title = title,
    modifier = modifier.background(PastelYellow),
    onNext = onNext,
    content = content
)

@Composable
fun ContentGreen(
    title: String,
    modifier: Modifier = Modifier,
    onNext: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) = ContentBase(
    title = title,
    modifier = modifier.background(PastelGreen),
    onNext = onNext,
    content = content
)

@Composable
fun ContentBlue(
    title: String,
    modifier: Modifier = Modifier,
    onNext: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) = ContentBase(
    title = title,
    modifier = modifier.background(PastelBlue),
    onNext = onNext,
    content = content
)

@Composable
fun ContentMauve(
    title: String,
    modifier: Modifier = Modifier,
    onNext: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) = ContentBase(
    title = title,
    modifier = modifier.background(PastelMauve),
    onNext = onNext,
    content = content
)

@Composable
fun ContentPurple(
    title: String,
    modifier: Modifier = Modifier,
    onNext: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) = ContentBase(
    title = title,
    modifier = modifier.background(PastelPurple),
    onNext = onNext,
    content = content
)

@Composable
fun ContentPink(
    title: String,
    modifier: Modifier = Modifier,
    onNext: (() -> Unit)? = null,
    content: (@Composable () -> Unit)? = null,
) = ContentBase(
    title = title,
    modifier = modifier.background(PastelPink),
    onNext = onNext,
    content = content
)
