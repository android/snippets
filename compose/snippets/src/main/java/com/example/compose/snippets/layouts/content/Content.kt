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

package com.example.compose.snippets.layouts.content

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.snippets.ui.theme.PastelBlue
import com.example.compose.snippets.ui.theme.PastelGreen
import com.example.compose.snippets.ui.theme.PastelOrange
import com.example.compose.snippets.ui.theme.PastelPink
import com.example.compose.snippets.ui.theme.PastelRed
import com.example.compose.snippets.ui.theme.PastelYellow

@Preview
@Composable
fun GreenRoundedBox(modifier: Modifier = Modifier, width: Dp = 100.dp, title: String = "") {
    RoundedBox(modifier = modifier
        .width(width)
        .background(PastelGreen),
        title = title
    )
}

@Preview
@Composable
fun BlueRoundedBox(modifier: Modifier = Modifier, width: Dp = 100.dp, title: String = "") {
    RoundedBox(modifier = modifier
        .width(width)
        .background(PastelBlue),
        title = title
    )
}

@Preview
@Composable
fun RedRoundedBox(modifier: Modifier = Modifier, width: Dp = 100.dp, title: String = "") {
    RoundedBox(modifier = modifier
        .width(width)
        .background(PastelRed),
        title = title
    )
}

@Preview
@Composable
fun PinkRoundedBox(modifier: Modifier = Modifier, width: Dp = 100.dp, title: String = "") {
    RoundedBox(modifier = modifier
        .width(width)
        .background(PastelPink),
        title = title
    )
}

@Preview
@Composable
fun YellowRoundedBox(modifier: Modifier = Modifier, width: Dp = 100.dp, title: String = "") {
    RoundedBox(modifier = modifier
        .width(width)
        .background(PastelYellow),
        title = title
    )
}

@Preview
@Composable
fun OrangeRoundedBox(modifier: Modifier = Modifier, width: Dp = 100.dp, title: String = "") {
    RoundedBox(modifier = Modifier
        .width(width)
        .background(PastelOrange)
        .then(modifier),
        title = title
    )
}


@Preview
@Composable
fun RoundedBox(modifier: Modifier = Modifier, title: String = "") {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(32.dp))
            .then(modifier)
            .height(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(title, fontSize = 24.sp, color = Color.DarkGray)
    }
}
