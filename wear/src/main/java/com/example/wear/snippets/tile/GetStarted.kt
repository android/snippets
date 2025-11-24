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

package com.example.wear.snippets.tile

import android.content.Context
import android.graphics.Color
import androidx.wear.protolayout.DimensionBuilders.expand
import androidx.wear.protolayout.LayoutElementBuilders.FontSetting.roundness
import androidx.wear.protolayout.LayoutElementBuilders.FontSetting.weight
import androidx.wear.protolayout.LayoutElementBuilders.FontSetting.width
import androidx.wear.protolayout.material3.ButtonDefaults.filledButtonColors
import androidx.wear.protolayout.material3.ButtonDefaults.filledVariantButtonColors
import androidx.wear.protolayout.material3.MaterialScope
import androidx.wear.protolayout.material3.PrimaryLayoutMargins.Companion.MAX_PRIMARY_LAYOUT_MARGIN
import androidx.wear.protolayout.material3.Typography.BODY_MEDIUM
import androidx.wear.protolayout.material3.materialScope
import androidx.wear.protolayout.material3.primaryLayout
import androidx.wear.protolayout.material3.text
import androidx.wear.protolayout.material3.textButton
import androidx.wear.protolayout.material3.textEdgeButton
import androidx.wear.protolayout.modifiers.clickable
import androidx.wear.protolayout.types.argb
import androidx.wear.protolayout.types.layoutString
import androidx.wear.tiles.RequestBuilders
import androidx.wear.tiles.TileService

/*

ORIGINAL:

materialScope(
    context = context,
    deviceConfiguration = requestParams.deviceConfiguration, // requestParams is passed to onTileRequest
    defaultColorScheme = myFallbackColorScheme
) {
    // inside the MaterialScope, you can call functions like primaryLayout()
    primaryLayout(
        titleSlot = { text(text = "Title".layoutString) },
        mainSlot = { text(text = "Main Content".layoutString) },
        bottomSlot = { textEdgeButton(text = "Action".layoutString) }
    )
}

*/

private fun TileService.materialscope1(
    context: Context,
    requestParams: RequestBuilders.TileRequest,
    myFallbackColorScheme: androidx.wear.protolayout.material3.ColorScheme
) =
    // [START android_wear_tile_getstarted_materialscope1]
    materialScope(
        context = context,
        deviceConfiguration =
        requestParams.deviceConfiguration, // requestParams is passed to onTileRequest
        defaultColorScheme = myFallbackColorScheme
    ) {
        // inside the MaterialScope, you can call functions like primaryLayout()
        primaryLayout(
            titleSlot = { text(text = "Title".layoutString) },
            mainSlot = { text(text = "Main Content".layoutString) },
            bottomSlot = {
                textEdgeButton(
                    labelContent = { text("Action".layoutString) },
                    onClick = clickable()
                )
            }
        )
    }
// [START android_wear_tile_getstarted_materialscope1]

/*

ORIGINAL:

val myColorScheme =
    ColorScheme(
        primary = ...
        onPrimary = ...
        // 27 more
    )

materialScope(
  defaultColorScheme = myColorScheme
) {
  // If the user selects "no theme" in settings, myColorScheme is used.
  // Otherwise, the system-provided theme is used.
}

*/

private fun TileService.materialscope2(
    requestParams: RequestBuilders.TileRequest
) {
    // [START android_wear_tile_getstarted_materialscope2]
    val myColorScheme =
        androidx.wear.protolayout.material3.ColorScheme(
            primary = Color.rgb(0, 0, 255).argb, // Blue
            onPrimary = Color.rgb(255, 255, 255).argb, // White
            // 27 more
        )

    materialScope(
        context = this,
        deviceConfiguration = requestParams.deviceConfiguration,
        defaultColorScheme = myColorScheme
    ) {
        // If the user selects "no theme" in settings, myColorScheme is used.
        // Otherwise, the system-provided theme is used.
        // [START_EXCLUDE silent]
        text(text = "Placeholder".layoutString)
        // [END_EXCLUDE]
    }
    // [END android_wear_tile_getstarted_materialscope2]
}
/*

ORIGINAL:

textEdgeButton(
    colors = filledButtonColors() // default
    /* OR colors = filledTonalButtonColors() */
    /* OR colors = filledVariantButtonColors() */
    // ... other parameters
)

*/

private fun MaterialScope.textEdgeButton1() =
    // [START android_wear_tile_getstarted_textedgebutton1]
    textEdgeButton(
        colors =
        filledButtonColors()
            .copy(
                containerColor = colorScheme.tertiary,
                labelColor = colorScheme.onTertiary
            ),
        // ... other parameters
        // [START_EXCLUDE silent]
        onClick = clickable(),
        labelContent = { text("Hello, World".layoutString) }
        // [END_EXCLUDE]
    )
// [END android_wear_tile_getstarted_textedgebutton1]

/*

ORIGINAL:

textEdgeButton(
    colors =
        filledButtonColors()
            .copy(
                containerColor = colorScheme.tertiary,
                labelColor = colorScheme.onTertiary
            )
    // ... other parameters
)

*/

public fun MaterialScope.textEdgeButton2() =
    // [START android_wear_tile_getstarted_textedgebutton2]
    textEdgeButton(
        colors =
        filledButtonColors()
            .copy(
                containerColor = colorScheme.tertiary,
                labelColor = colorScheme.onTertiary
            ),
        // ... other parameters
        // [START_EXCLUDE silent]
        onClick = clickable(),
        labelContent = { text("".layoutString) }
        // [END_EXCLUDE]
    )
// [END android_wear_tile_getstarted_textedgebutton2]

private fun TileService.materialscope3(
    requestParams: RequestBuilders.TileRequest
) {
    // [START android_wear_tile_getstarted_materialscope3]
    val myColorScheme =
        androidx.wear.protolayout.material3.ColorScheme(
            primary = Color.rgb(0, 0, 255).argb,
            onPrimary = Color.rgb(255, 255, 255).argb,
        )

    materialScope(
        context = this,
        deviceConfiguration = requestParams.deviceConfiguration,
        allowDynamicTheme = false,
        defaultColorScheme = myColorScheme
    ) {
        // myColorScheme is *always* used.
        // [START_EXCLUDE silent]
        text("Placeholder".layoutString)
        // [END_EXCLUDE]
    }
    // [END android_wear_tile_getstarted_materialscope3]
}

/*

ORIGINAL:

textEdgeButton(
    colors = filledButtonColors() // default
    /* OR colors = filledTonalButtonColors() */
    /* OR colors = filledVariantButtonColors() */
    // ... other parameters
)

*/

private fun MaterialScope.textEdgeButton5() =
    // [START android_wear_tile_getstarted_textedgebutton5]
    textEdgeButton(
        colors = filledButtonColors(), // default
        /* OR colors = filledTonalButtonColors() */
        /* OR colors = filledVariantButtonColors() */
        // ... other parameters
        // [START_EXCLUDE silent]
        onClick = clickable(),
        labelContent = { text("Action".layoutString) }
        // [END_EXCLUDE]
    )
// [END android_wear_tile_getstarted_textedgebutton5]

/*

ORIGINAL:

textEdgeButton(
    colors =
        filledButtonColors()
            .copy(
                containerColor = colorScheme.tertiary,
                labelColor = colorScheme.onTertiary
            )
    // ... other parameters
)

*/

private fun MaterialScope.textEdgeButton6() =
    // [START android_wear_tile_getstarted_textedgebutton6]
    textEdgeButton(
        colors =
        filledButtonColors()
            .copy(
                containerColor = colorScheme.tertiary,
                labelColor = colorScheme.onTertiary
            ),
        // ... other parameters
        // [START_EXCLUDE silent]
        onClick = clickable(),
        labelContent = { text("Action".layoutString) }
        // [END_EXCLUDE]
    )
// [END android_wear_tile_getstarted_textedgebutton6]

/*

ORIGINAL:

textEdgeButton(
    colors =
        ButtonColors(
            // the materialScope makes colorScheme available
            containerColor = colorScheme.secondary,
            iconColor = colorScheme.secondaryDim,
            labelColor = colorScheme.onSecondary,
            secondaryLabelColor = colorScheme.onSecondary
        )
    // ... other parameters
)

*/

private fun MaterialScope.textEdgeButton3() =
    // [START android_wear_tile_getstarted_textedgebutton3]
    textEdgeButton(
        colors =
        androidx.wear.protolayout.material3.ButtonColors(
            // the materialScope makes colorScheme available
            containerColor = colorScheme.secondary,
            iconColor = colorScheme.secondaryDim,
            labelColor = colorScheme.onSecondary,
            secondaryLabelColor = colorScheme.onSecondary
        ),
        // ... other parameters
        // [START_EXCLUDE silent]
        onClick = clickable(),
        labelContent = { text("Action".layoutString) }
        // [END_EXCLUDE]
    )
// [END android_wear_tile_getstarted_textedgebutton3]

/*

ORIGINAL:

textEdgeButton(
    colors = filledButtonColors().copy(
        containerColor = android.graphics.Color.RED.argb, // Using named colors
        labelColor = 0xFFFFFF00.argb // Using a hex code for yellow
    )
    // ... other parameters
)

*/

private fun MaterialScope.textEdgeButton4() =
    // [START android_wear_tile_getstarted_textedgebutton4]
    textEdgeButton(
        colors = filledButtonColors().copy(
            containerColor = android.graphics.Color.RED.argb, // Using named colors
            labelColor = 0xFFFFFF00.toInt().argb // Using a hex code for yellow
        ),
        // ... other parameters
        // [START_EXCLUDE silent]
        onClick = clickable(),
        labelContent = { text("Action".layoutString) }
        // [END_EXCLUDE]
    )
// [END android_wear_tile_getstarted_textedgebutton4]

/*

ORIGINAL:

text(
    text = "Hello, World!".layoutString,
    typography = BODY_MEDIUM,
)

*/

private fun MaterialScope.text1() =
    // [START android_wear_tile_getstarted_text1]
    text(
        text = "Hello, World!".layoutString,
        typography = BODY_MEDIUM,
    )
// [END android_wear_tile_getstarted_text1]

/*

ORIGINAL:

text(
    text = "Hello, World".layoutString,
    italic = true,

    // Use elements defined in androidx.wear.protolayout.LayoutElementBuilders.FontSetting
    settings =
        listOf(weight(500), width(100F), roundness(100)),
)

*/

private fun MaterialScope.text2() =
    // [START android_wear_tile_getstarted_text2]
    text(
        text = "Hello, World".layoutString,
        italic = true,

        // Use elements defined in androidx.wear.protolayout.LayoutElementBuilders.FontSetting
        settings =
        listOf(
            weight(500),
            width(100F),
            roundness(100)
        ),
    )
// [END android_wear_tile_getstarted_text2]

/*

ORIGINAL:

textButton(
   height = expand(),
   width = expand(),
   shape = shapes.medium, // OR another value like shapes.full
   colors = filledVariantButtonColors(),
   labelContent = { text("Hello, World!".layoutString) },
)

*/

private fun MaterialScope.textButton1() =
    // [START android_wear_tile_getstarted_textButton1]
    textButton(
        height = expand(),
        width = expand(),
        shape = shapes.medium, // OR another value like shapes.full
        colors = filledVariantButtonColors(),
        labelContent = { text("Hello, World!".layoutString) },
        // [START_EXCLUDE silent]
        onClick = clickable()
        // [END_EXCLUDE]
    )
// [END android_wear_tile_getstarted_textButton1]

/*

ORIGINAL:

primaryLayout(
    mainSlot = {
        textButton(
            shape = shapes.small,
            /* ... */
        )
    },
    // margin constants defined in androidx.wear.protolayout.material3.PrimaryLayoutMargins
    margins = MAX_PRIMARY_LAYOUT_MARGIN,
)

*/

private fun MaterialScope.primaryLayout1() =
    // [START android_wear_tile_getstarted_primaryLayout1]
    primaryLayout(
        mainSlot = {
            textButton(
                shape = shapes.small,
                /* ... */
                // [START_EXCLUDE silent]
                onClick = clickable(),
                labelContent = { text("Action".layoutString) },
                colors = filledButtonColors()
                // [END_EXCLUDE]
            )
        },
        // margin constants defined in androidx.wear.protolayout.material3.PrimaryLayoutMargins
        margins = MAX_PRIMARY_LAYOUT_MARGIN,
    )
// [END android_wear_tile_getstarted_primaryLayout1]
