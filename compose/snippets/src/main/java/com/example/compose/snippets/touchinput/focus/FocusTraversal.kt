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

package com.example.compose.snippets.touchinput.focus

import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component1
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component2
import androidx.compose.ui.focus.FocusRequester.Companion.FocusRequesterFactory.component3
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp

@Composable
internal fun OneDimensionalFocusTraversalExamples(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Section("One-dimensional focus traversal", modifier = modifier) {
        SubSection("Focus traversal example") {
            OneDimensionalFocusTraversal(
                modifier = Modifier.focusGroup(),
                onClick = onClick,
            )
        }
        SubSection("Z-shaped style") {
            OneDimensionalFocusTraversalInZShape(
                modifier = Modifier.focusGroup(),
                onClick = onClick,
            )
        }
        SubSection("Override") {
            OneDimensionalFocusTraversalOverride(
                modifier = Modifier.focusGroup(),
                onClick = onClick,
            )
        }
    }
}

@Composable
internal fun TwoDimensionalFocusTraversalExamples(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    Section("Two-dimensional focus traversal", modifier = modifier) {
        TwoDimensionalFocusTraversal(
            modifier = Modifier.focusGroup(),
            onClick = onClick,
        )
        SubSection("Override") {
            TwoDimensionalFocusTraversalOverride(
                modifier = Modifier.focusGroup(),
                onClick = onClick,
            )
        }
        SubSection("Focus group") {
            TwoDimensionalFocusTraversalWithFocusGroup(
                modifier = Modifier.focusGroup(),
                onClick = onClick,
            )
        }
        SubSection("On enter callback") {
            OnEnterCallback(
                modifier = Modifier.focusGroup(),
                onClick = onClick,
            )
        }
    }
}

@Composable
fun OneDimensionalFocusTraversal(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // [START android_compose_touchinput_focus_one_dimensional_traversal]
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Focus target
            Card(onClick = onClick, modifier = Modifier.width(160.dp)) {
                Text(text = "1st　card", modifier = Modifier.padding(32.dp))
            }
            // Focus target
            Card(
                onClick = onClick,
                modifier = Modifier.Companion
                    .width(160.dp)
                    .offset(x = 176.dp)
            ) {
                Text(text = "4th　card", modifier = Modifier.padding(32.dp))
            }
            // Focus target
            Card(
                onClick = onClick,
                modifier = Modifier
                    .width(160.dp)
                    .offset(y = -(104.dp))
            ) {
                Text(
                    text = "3rd　card",
                    modifier = Modifier.padding(32.dp)
                )
            }
        }
        // Focus target
        Card(onClick = onClick, modifier = Modifier.width(160.dp)) {
            Text(text = "2nd　card", modifier = Modifier.padding(32.dp))
        }
    }
    // [END android_compose_touchinput_focus_one_dimensional_traversal]
}

@Composable
fun OneDimensionalFocusTraversalInZShape(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // [START android_compose_touchinput_focus_one_dimensional_traversal_in_z_shape]
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        // modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Focus target
            Card(onClick = onClick, modifier = Modifier.width(160.dp)) {
                Text(text = "1st　card", modifier = Modifier.padding(32.dp))
            }
            // Focus target
            Card(onClick = onClick, modifier = Modifier.width(160.dp)) {
                Text(text = "2nd　card", modifier = Modifier.padding(32.dp))
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Focus target
            Card(onClick = onClick, modifier = Modifier.width(160.dp)) {
                Text(text = "3rd　card", modifier = Modifier.padding(32.dp))
            }
            // Focus target
            Card(onClick = onClick, modifier = Modifier.width(160.dp)) {
                Text(text = "4th　card", modifier = Modifier.padding(32.dp))
            }
        }
    }
    // [END android_compose_touchinput_focus_one_dimensional_traversal_in_z_shape]
}

@Composable
fun OneDimensionalFocusTraversalOverride(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // [START android_compose_touchinput_focus_one_dimensional_traversal_override]
    val (first, second, third) = remember { FocusRequester.createRefs() }
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Focus target
            Card(
                onClick = onClick,
                modifier = Modifier
                    .focusRequester(first)
                    .focusProperties {
                        next = second // Set focus move to 2nd card wth the Tab key
                    }

            ) {
                Text(text = "1st　card", modifier = Modifier.padding(32.dp))
            }
            // Focus target
            Card(
                onClick = onClick,
                modifier = Modifier
                    .focusRequester(third)
                    .focusProperties {
                        next = first // Set focus move to 1st card wth the Tab key
                    }
            ) {
                Text(text = "3nd　card", modifier = Modifier.padding(32.dp))
            }
        }
        // Focus target
        Card(
            onClick = onClick,
            modifier =
            Modifier
                .focusRequester(second)
                .focusProperties {
                    next = third // Set focus move to third card wth the Tab key
                }
        ) {
            Text(text = "2nd　card", modifier = Modifier.padding(32.dp))
        }
    }
    // [END android_compose_touchinput_focus_one_dimensional_traversal_override]
}

@Composable
fun TwoDimensionalFocusTraversal(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // [START android_compose_touchinput_focus_two_dimensional_traversal]
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Focus target
            Card(onClick = onClick, modifier = Modifier.width(160.dp)) {
                Text(text = "1st　card", modifier = Modifier.padding(32.dp))
            }
            // Focus target
            Card(onClick = onClick, modifier = Modifier.width(160.dp)) {
                Text(text = "2nd　card", modifier = Modifier.padding(32.dp))
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            // Focus target
            Card(onClick = onClick, modifier = Modifier.width(160.dp)) {
                Text(text = "3rd card", modifier = Modifier.padding(32.dp))
            }
            Spacer(modifier = Modifier.width(160.dp)) // This is NOT a focus target
            // Focus target
            Card(onClick = onClick, modifier = Modifier.width(160.dp)) {
                Text(text = "4th card", modifier = Modifier.padding(32.dp))
            }
        }
    }
    // [END android_compose_touchinput_focus_two_dimensional_traversal]
}

@Composable
fun TwoDimensionalFocusTraversalOverride(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // [START android_compose_touchinput_focus_two_dimensional_traversal_override]
    val firstCard = remember { FocusRequester() }
    Row(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        // Focus target
        Card(
            onClick = onClick,
            modifier = Modifier
                .width(160.dp)
                .focusRequester(firstCard) // Associate with a FocusRequester object
        ) {
            Text(text = "1st　card", modifier = Modifier.padding(32.dp))
        }
        // Focus target
        Card(
            onClick = onClick,
            modifier = Modifier
                .width(160.dp)
                .focusProperties {
                    right = firstCard // Set focus move to 1st card
                }
        ) {
            Text(text = "2nd　card", modifier = Modifier.padding(32.dp))
        }
    }
    // [END android_compose_touchinput_focus_two_dimensional_traversal_override]
}

@Composable
fun TwoDimensionalFocusTraversalWithFocusGroup(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {
    // [START android_compose_touchinput_focus_two_dimensional_traversal_with_focus_group]
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.focusGroup()
        ) {
            // Focus target
            Card(onClick = onClick, modifier = Modifier.width(160.dp)) {
                Text(text = "1st　card", modifier = Modifier.padding(32.dp))
            }
            // Focus target
            Card(onClick = onClick, modifier = Modifier.width(160.dp)) {
                Text(text = "2nd　card", modifier = Modifier.padding(32.dp))
            }
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.focusGroup()
        ) {
            // Focus target
            Card(onClick = onClick, modifier = Modifier.width(160.dp)) {
                Text(text = "3rd card", modifier = Modifier.padding(32.dp))
            }
            Spacer(modifier = Modifier.width(160.dp)) // This is NOT a focus target
            // Focus target
            Card(onClick = onClick, modifier = Modifier.width(160.dp)) {
                Text(text = "4th card", modifier = Modifier.padding(32.dp))
            }
        }
    }
    // [END android_compose_touchinput_focus_two_dimensional_traversal_with_focus_group]
}

@Composable
fun OnEnterCallback(
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {}
) {

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = modifier
    ) {
        // Focus target
        Card(onClick = onClick, modifier = Modifier.width(160.dp)) {
            Text(text = "1st　card", modifier = Modifier.padding(32.dp))
        }
        // [START android_compose_touchinput_focus_on_enter_callback]
        var isInGroup by remember { mutableStateOf(false) }
        val backgroundColor = if (isInGroup) {
            MaterialTheme.colorScheme.primary
        } else {
            MaterialTheme.colorScheme.background
        }
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .focusProperties {
                    onEnter = {
                        isInGroup = true
                    }
                    onExit = {
                        isInGroup = false
                    }
                }
                .focusGroup()
                .background(backgroundColor)
        ) {
            // Focus target
            Card(onClick = onClick, modifier = Modifier.width(160.dp)) {
                Text(text = "2nd　card", modifier = Modifier.padding(32.dp))
            }
            // Focus target
            Card(onClick = onClick, modifier = Modifier.width(160.dp)) {
                Text(text = "3rd　card", modifier = Modifier.padding(32.dp))
            }
        }
        // [END android_compose_touchinput_focus_on_enter_callback]
    }
}
