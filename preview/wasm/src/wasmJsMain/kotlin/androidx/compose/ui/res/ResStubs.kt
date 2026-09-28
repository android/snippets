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

package androidx.compose.ui.res

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import com.example.compose.snippets.R
import com.example.compose.snippets.components.AppIcons

@Composable
fun painterResource(id: Int): Painter {
    val icon = when (id) {
        R.drawable.favorite -> AppIcons.Favorite
        R.drawable.favorite_filled -> AppIcons.FavoriteFilled
        R.drawable.fast_forward -> AppIcons.FastForward
        R.drawable.fast_forward_filled -> AppIcons.FastForwardFilled
        R.drawable.fast_rewind -> AppIcons.FastRewind
        R.drawable.fast_rewind_filled -> AppIcons.FastRewindFilled
        else -> null
    }
    return if (icon != null) {
        rememberVectorPainter(icon)
    } else {
        ColorPainter(Color(0xFF006D3B))
    }
}

@Composable
fun stringResource(id: Int): String = "Sample Text"
