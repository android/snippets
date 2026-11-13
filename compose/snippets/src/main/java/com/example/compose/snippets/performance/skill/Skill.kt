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

package com.example.compose.snippets.performance.skill

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.ModifierNodeElement

// [START android_compose_performance_skill_modifier_node]
// Modifier factory
fun Modifier.circle(color: Color) = this then CircleElement(color)

// ModifierNodeElement
private data class CircleElement(
    val color: Color
) : ModifierNodeElement<CircleNode>() {
    override fun create() = CircleNode(color)

    override fun update(node: CircleNode) {
        node.color = color
    }
}

// Modifier.Node
private class CircleNode(
    var color: Color
) : DrawModifierNode, Modifier.Node() {
    override fun ContentDrawScope.draw() {
        drawCircle(color)
    }
}
// [END android_compose_performance_skill_modifier_node]
