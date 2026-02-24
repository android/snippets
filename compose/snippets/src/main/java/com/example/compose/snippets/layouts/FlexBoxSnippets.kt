/*
 * Copyright 2026 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

@file:OptIn(ExperimentalFlexBoxApi::class)

package com.example.compose.snippets.layouts

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalFlexBoxApi
import androidx.compose.foundation.layout.FlexAlignContent
import androidx.compose.foundation.layout.FlexAlignItems
import androidx.compose.foundation.layout.FlexAlignSelf
import androidx.compose.foundation.layout.FlexBasis
import androidx.compose.foundation.layout.FlexBox
import androidx.compose.foundation.layout.FlexDirection
import androidx.compose.foundation.layout.FlexJustifyContent
import androidx.compose.foundation.layout.FlexWrap
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.compose.snippets.layouts.content.BlueRoundedBox
import com.example.compose.snippets.layouts.content.GreenRoundedBox
import com.example.compose.snippets.layouts.content.OrangeRoundedBox
import com.example.compose.snippets.layouts.content.PinkRoundedBox
import com.example.compose.snippets.layouts.content.RedRoundedBox
import com.example.compose.snippets.ui.theme.PastelBlue
import com.example.compose.snippets.ui.theme.PastelRed

@Preview(showBackground = true)
@Composable
fun HelloWorld() {
    // [START android_compose_layouts_flexbox_basic_1]
    FlexBox(
        config = {
            direction = FlexDirection.Column
            alignItems = FlexAlignItems.Center
        }
    ) {
        Text(text = "Hello", fontSize = 48.sp)
        Text(text = "World!", fontSize = 48.sp)
    }
    // [END android_compose_layouts_flexbox_basic_1]
}

/**
 * A FlexBox that wraps with variable width items that grow equally to fill the available space
 */
@Preview(widthDp = 1200, showBackground = true, backgroundColor = 0xFF777777)
@Preview(widthDp = 840, showBackground = true, backgroundColor = 0xFF777777)
@Preview(widthDp = 600, showBackground = true, backgroundColor = 0xFF777777)
@Composable
fun WrapWithGrow() {
    // [START android_compose_layouts_flexbox_basic_2]
    FlexBox(
        config = {
            wrap = FlexWrap.Wrap
            gap(8.dp)
        }
    ) {
        RedRoundedBox()
        BlueRoundedBox()
        GreenRoundedBox(modifier = Modifier.width(350.dp).flex { grow = 1.0f })
        OrangeRoundedBox(modifier = Modifier.width(200.dp).flex { grow = 0.7f })
        PinkRoundedBox(modifier = Modifier.width(200.dp).flex { grow = 0.3f })
    }
    // [END android_compose_layouts_flexbox_basic_2]
}

@Composable
fun ContainerSnippet(){
    // [START android_compose_layouts_flexbox_container_1]
    FlexBox(
        config = {
            direction = FlexDirection.Column
            wrap = FlexWrap.Wrap
            alignItems = FlexAlignItems.Center
            alignContent = FlexAlignContent.SpaceAround
            justifyContent = FlexJustifyContent.Center
            gap(16.dp)
        }
    ) { // child items
    }
    // [END android_compose_layouts_flexbox_container_1]
}

@Preview
@Composable
fun ItemSnippet(){
    // [START android_compose_layouts_flexbox_item_1]
    FlexBox {
        RedRoundedBox(
            modifier = Modifier.flex {
                basis = FlexBasis.Auto
                grow = 1.0f
                shrink = 0.5f
            }
        )
    }
    // [END android_compose_layouts_flexbox_item_1]
}

@Preview(showBackground = true, widthDp = 600, backgroundColor = 0xFF777777)
annotation class FlexBasisPreview

@FlexBasisPreview
@Composable
fun BasisAuto() {
    // [START android_compose_layouts_flexbox_item_basis_1]
    FlexBox {
        RedRoundedBox(modifier = Modifier.flex { basis = FlexBasis.Auto })
        BlueRoundedBox(modifier = Modifier.flex { basis = FlexBasis.Auto })
    }
    // [END android_compose_layouts_flexbox_item_basis_1]
}

@FlexBasisPreview
@Composable
fun BasisDp() {
    // [START android_compose_layouts_flexbox_item_basis_2]
    FlexBox {
        RedRoundedBox(modifier = Modifier.flex { basis(200.dp) })
        BlueRoundedBox(modifier = Modifier.flex { basis(100.dp) })
    }
    // [END android_compose_layouts_flexbox_item_basis_2]
}

@FlexBasisPreview
@Composable
fun BasisPercent() {
    // [START android_compose_layouts_flexbox_item_basis_3]
    FlexBox {
        RedRoundedBox(modifier = Modifier.flex { basis(0.7f) })
        BlueRoundedBox(modifier = Modifier.flex { basis(0.3f) })
    }
    // [END android_compose_layouts_flexbox_item_basis_3]
}


@Preview(showBackground = true, widthDp = 600, backgroundColor = 0xFF777777)
annotation class FlexGrowPreview

@FlexGrowPreview
@Composable
fun FlexGrowSingleBefore() {
    FlexBox {
        RedRoundedBox(title = "100dp")
        BlueRoundedBox(title = "100dp")
        GreenRoundedBox(title = "100dp")
    }
}

@FlexGrowPreview
@Composable
fun FlexGrowSingle() {
    // [START android_compose_layouts_flexbox_item_grow_1]
    FlexBox {
        RedRoundedBox(title = "400dp", modifier = Modifier.flex { grow = 1f })
        BlueRoundedBox(title = "100dp")
        GreenRoundedBox(title = "100dp")
    }
    // [END android_compose_layouts_flexbox_item_grow_1]
}


@FlexGrowPreview
@Composable
fun FlexGrowMultiple() {
    // [START android_compose_layouts_flexbox_item_grow_2]
    FlexBox {
        RedRoundedBox(
            title = "150dp",
            modifier = Modifier.flex { grow = 1f }
        )
        BlueRoundedBox(
            title = "200dp",
            modifier = Modifier.flex { grow = 2f }
        )
        GreenRoundedBox(
            title = "250dp",
            modifier = Modifier.flex { grow = 3f }
        )
    }
    // [END android_compose_layouts_flexbox_item_grow_2]
}

@Preview(showBackground = true, widthDp = 700, backgroundColor = 0xFF777777)
@Preview(showBackground = true, widthDp = 500, backgroundColor = 0xFF777777)
@Preview(showBackground = true, widthDp = 450, backgroundColor = 0xFF777777)
@Composable
fun FlexShrink() {
    // [START android_compose_layouts_flexbox_item_shrink_1]
    FlexBox {
        Text(
            "The quick brown fox",
            fontSize = 36.sp,
            modifier = Modifier
                .background(PastelRed)
                .flex { shrink = 1f }
        )
        Text(
            "The quick brown fox",
            fontSize = 36.sp,
            modifier = Modifier
                .background(PastelBlue)
                .flex { shrink = 0f }
        )
    }
    // [END android_compose_layouts_flexbox_item_shrink_1]
}

@Preview(showBackground = true, widthDp = 500, heightDp = 200, backgroundColor = 0xFF777777)
@Composable
fun AlignSelfOverride() {
    // [START android_compose_layouts_flexbox_item_align_1]
    FlexBox(
        config = {
            alignItems = FlexAlignItems.Start
        }
    ) {
        RedRoundedBox()
        BlueRoundedBox(modifier = Modifier.flex { alignSelf = FlexAlignSelf.Center })
        GreenRoundedBox(modifier = Modifier.flex { alignSelf = FlexAlignSelf.End })
        PinkRoundedBox(modifier = Modifier.flex { alignSelf = FlexAlignSelf.Stretch })
        OrangeRoundedBox(modifier = Modifier.flex { alignSelf = FlexAlignSelf.Baseline })
    }
    // [END android_compose_layouts_flexbox_item_align_1]
}

@Preview(showBackground = true, backgroundColor = 0xFF777777)
@Composable
fun OrderDefault() {
    // [START android_compose_layouts_flexbox_item_order_1]
    FlexBox {
        // Declared first, but will be placed after visually
        RedRoundedBox(
            title = "World"
        )

        // Declared second, but will be placed first visually
        BlueRoundedBox(
            title = "Hello",
            modifier = Modifier.flex {
                order = -1
            }
        )
    }
    // [END android_compose_layouts_flexbox_item_order_1]
}

