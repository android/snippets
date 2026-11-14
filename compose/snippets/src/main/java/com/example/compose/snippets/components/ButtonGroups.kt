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

package com.example.compose.snippets.components

import androidx.compose.foundation.layout.FlexBox
import androidx.compose.foundation.layout.FlexDirection
import androidx.compose.foundation.layout.FlexWrap
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleButton
import androidx.compose.material3.ToggleButtonDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Preview
// [START android_compose_expressive_components_buttongroup]
@Composable
fun ButtonGroupSample() {
    val numButtons = 10
    ButtonGroup(
        overflowIndicator = { menuState ->
            ButtonGroupDefaults.OverflowIndicator(menuState = menuState)
        }
    ) {
        for (i in 0 until numButtons) {
            clickableItem(onClick = {}, label = "$i")
        }
    }
}
// [END android_compose_expressive_components_buttongroup]

@Preview
// [START android_compose_expressive_components_singleselectconnectedbuttongroup]
@Composable
fun SingleSelectConnectedButtonGroupSample() {
    val options = listOf("Work", "Restaurant", "Coffee", "Search", "Home")
    val unCheckedIcons =
        listOf(
            AppIcons.Work,
            AppIcons.Restaurant,
            AppIcons.Coffee,
            AppIcons.Search,
            AppIcons.Home,
        )
    val checkedIcons =
        listOf(
            AppIcons.WorkFilled,
            AppIcons.Restaurant,
            AppIcons.CoffeeFilled,
            AppIcons.Search,
            AppIcons.HomeFilled,
        )
    var selectedIndex by remember { mutableIntStateOf(0) }

    FlexBox(
        modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
        config = {
            direction(FlexDirection.Row)
            wrap(FlexWrap.Wrap)
            columnGap(ButtonGroupDefaults.ConnectedSpaceBetween)
            rowGap(2.dp)
        },
    ) {
        options.forEachIndexed { index, label ->
            ToggleButton(
                checked = selectedIndex == index,
                onCheckedChange = { selectedIndex = index },
                shapes =
                    when (index) {
                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                        options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                    },
                modifier = Modifier.semantics { role = Role.RadioButton },
            ) {
                Icon(
                    painter =
                        rememberVectorPainter(
                            if (selectedIndex == index) checkedIcons[index] else unCheckedIcons[index]
                        ),
                    contentDescription = null,
                )
                Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                Text(label)
            }
        }
    }
}
// [END android_compose_expressive_components_singleselectconnectedbuttongroup]

@Preview
// [START android_compose_expressive_components_multiselectconnectedbuttongroup]
@Composable
fun MultiSelectConnectedButtonGroupSample() {
    val options = listOf("Work", "Restaurant", "Coffee", "Search", "Home")
    val unCheckedIcons =
        listOf(
            AppIcons.Work,
            AppIcons.Restaurant,
            AppIcons.Coffee,
            AppIcons.Search,
            AppIcons.Home,
        )
    val checkedIcons =
        listOf(
            AppIcons.WorkFilled,
            AppIcons.Restaurant,
            AppIcons.CoffeeFilled,
            AppIcons.Search,
            AppIcons.HomeFilled,
        )
    val checked = remember { mutableStateListOf(false, false, false, false, false) }

    FlexBox(
        modifier = Modifier.padding(horizontal = 8.dp).fillMaxWidth(),
        config = {
            direction(FlexDirection.Row)
            wrap(FlexWrap.Wrap)
            columnGap(ButtonGroupDefaults.ConnectedSpaceBetween)
            rowGap(2.dp)
        },
    ) {
        options.forEachIndexed { index, label ->
            ToggleButton(
                checked = checked[index],
                onCheckedChange = { checked[index] = it },
                shapes =
                    when (index) {
                        0 -> ButtonGroupDefaults.connectedLeadingButtonShapes()
                        options.lastIndex -> ButtonGroupDefaults.connectedTrailingButtonShapes()
                        else -> ButtonGroupDefaults.connectedMiddleButtonShapes()
                    },
            ) {
                Icon(
                    painter =
                        rememberVectorPainter(
                            if (checked[index]) checkedIcons[index] else unCheckedIcons[index]
                        ),
                    contentDescription = null,
                )
                Spacer(Modifier.size(ToggleButtonDefaults.IconSpacing))
                Text(label)
            }
        }
    }
}
// [END android_compose_expressive_components_multiselectconnectedbuttongroup]
