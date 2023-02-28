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

package com.example.compose.snippets.modifiers

import androidx.compose.animation.core.AnimationState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.paddingFromBaseline
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private object Padding {
    // [START android_compose_modifiers_padding]
    @Composable
    private fun Greeting(name: String) {
        Column(modifier = Modifier.padding(24.dp)) {
            Text(text = "Hello,")
            Text(text = name)
        }
    }
    // [END android_compose_modifiers_padding]
}

private object PaddingAndFill {
    // [START android_compose_modifiers_paddingandfill]
    @Composable
    private fun Greeting(name: String) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
        ) {
            Text(text = "Hello,")
            Text(text = name)
        }
    }
    // [END android_compose_modifiers_paddingandfill]
}

private object Order1 {
    val onClick = {}

    // [START android_compose_modifiers_order1]
    @Composable
    fun ArtistCard(/*...*/) {
        val padding = 16.dp
        Column(
            Modifier
                .clickable(onClick = onClick)
                .padding(padding)
                .fillMaxWidth()
        ) {
            // rest of the implementation
        }
    }
    // [END android_compose_modifiers_order1]
}

private object Order2 {
    val onClick = {}

    // [START android_compose_modifiers_order2]
    @Composable
    fun ArtistCard(/*...*/) {
        val padding = 16.dp
        Column(
            Modifier
                .padding(padding)
                .clickable(onClick = onClick)
                .fillMaxWidth()
        ) {
            // rest of the implementation
        }
    }
    // [END android_compose_modifiers_order2]
}

private object Size {
    // [START android_compose_modifiers_size]
    @Composable
    fun ArtistCard(/*...*/) {
        Row(
            modifier = Modifier.size(width = 400.dp, height = 100.dp)
        ) {
            Image(/*...*/)
            Column { /*...*/ }
        }
    }
    // [END android_compose_modifiers_size]
}

private object RequiredSize {
    // [START android_compose_modifiers_requiredsize]
    @Composable
    fun ArtistCard(/*...*/) {
        Row(
            modifier = Modifier.size(width = 400.dp, height = 100.dp)
        ) {
            Image(
                /*...*/
                modifier = Modifier.requiredSize(150.dp)
            )
            Column { /*...*/ }
        }
    }
    // [END android_compose_modifiers_requiredsize]
}

private object FillMaxHeight {
    // [START android_compose_modifiers_fillmaxheight]
    @Composable
    fun ArtistCard(/*...*/) {
        Row(
            modifier = Modifier.size(width = 400.dp, height = 100.dp)
        ) {
            Image(
                /*...*/
                modifier = Modifier.fillMaxHeight()
            )
            Column { /*...*/ }
        }
    }
    // [END android_compose_modifiers_fillmaxheight]
}

private object PaddingFromBaseline {
    // [START android_compose_modifiers_paddingfrombaseline]
    @Composable
    fun ArtistCard(artist: Artist) {
        Row(/*...*/) {
            Column {
                Text(
                    text = artist.name,
                    modifier = Modifier.paddingFromBaseline(top = 50.dp)
                )
                Text(artist.lastSeenOnline)
            }
        }
    }
    // [END android_compose_modifiers_paddingfrombaseline]
}

private object Offset {
    // [START android_compose_modifiers_offset]
    @Composable
    fun ArtistCard(artist: Artist) {
        Row(/*...*/) {
            Column {
                Text(artist.name)
                Text(
                    text = artist.lastSeenOnline,
                    modifier = Modifier.offset(x = 4.dp)
                )
            }
        }
    }
    // [END android_compose_modifiers_offset]
}

private object MatchParentSize {
    // [START android_compose_modifiers_matchparentsize]
    @Composable
    fun MatchParentSizeComposable() {
        Box {
            Spacer(
                Modifier
                    .matchParentSize()
                    .background(Color.LightGray)
            )
            ArtistCard()
        }
    }
    // [END android_compose_modifiers_matchparentsize]
}

private object Weight {
    // [START android_compose_modifiers_weight]
    @Composable
    fun ArtistCard(/*...*/) {
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Image(
                /*...*/
                modifier = Modifier.weight(2f)
            )
            Column(
                modifier = Modifier.weight(1f)
            ) {
                /*...*/
            }
        }
    }
    // [END android_compose_modifiers_weight]
}

private object ReusingModifiers {
    // [START android_compose_modifiers_reusingmodifiers]
    val reusableModifier = Modifier
        .fillMaxWidth()
        .background(Color.Red)
        .padding(12.dp)
    // [END android_compose_modifiers_reusingmodifiers]
}

private object AnimationInline {
    // [START android_compose_modifiers_animationinline]
    @Composable
    fun LoadingWheelAnimation() {
        val animatedState = animateFloatAsState(/*...*/)

        LoadingWheel(
            // Creation and allocation of this modifier will happen on every frame of the animation!
            modifier = Modifier
                .padding(12.dp)
                .background(Color.Gray),
            animatedState = animatedState
        )
    }
    // [END android_compose_modifiers_animationinline]
}

private object AnimationExtracted {
    // [START android_compose_modifiers_animationextracted]
    // Now, the allocation of the modifier happens here:
    val reusableModifier = Modifier
        .padding(12.dp)
        .background(Color.Gray)

    @Composable
    fun LoadingWheelAnimation() {
        val animatedState = animateFloatAsState(/*...*/)

        LoadingWheel(
            // No allocation, as we're just reusing the same instance
            modifier = reusableModifier,
            animatedState = animatedState
        )
    }
    // [END android_compose_modifiers_animationextracted]
}

private object UnscopedModifiers1 {
    // [START android_compose_modifiers_unscopedmodifiers1]
    val reusableModifier = Modifier
        .fillMaxWidth()
        .background(Color.Red)
        .padding(12.dp)

    @Composable
    fun AuthorField() {
        HeaderText(
            // ...
            modifier = reusableModifier
        )
        SubtitleText(
            // ...
            modifier = reusableModifier
        )
    }
    // [END android_compose_modifiers_unscopedmodifiers1]
}

private object UnscopedModifiers2 {
    // [START android_compose_modifiers_unscopedmodifiers2]
    val reusableItemModifier = Modifier
        .padding(bottom = 12.dp)
        .size(216.dp)
        .clip(CircleShape)

    @Composable
    private fun AuthorList(authors: List<Author>) {
        LazyColumn {
            items(authors) {
                AsyncImage(
                    // ...
                    modifier = reusableItemModifier,
                )
            }
        }
    }
    // [END android_compose_modifiers_unscopedmodifiers2]
}

@Composable
private fun ScopedModifiers1() {
    // [START android_compose_modifiers_scopedmodifiers1]
    Column(/*...*/) {
        val reusableItemModifier = Modifier
            .padding(bottom = 12.dp)
            // Align Modifier.Element requires a ColumnScope
            .align(Alignment.CenterHorizontally)
            .weight(1f)
        Text1(
            modifier = reusableItemModifier,
            // ...
        )
        Text2(
            modifier = reusableItemModifier
            // ...
        )
        // ...
    }
    // [END android_compose_modifiers_scopedmodifiers1]
}

@Composable
private fun ScopedModifiers2() {
    // [START android_compose_modifiers_scopedmodifiers2]
    Column(modifier = Modifier.fillMaxWidth()) {
        // Weight modifier is scoped to the Column composable
        val reusableItemModifier = Modifier.weight(1f)

        // Weight will be properly assigned here since this Text is a direct child of Column
        Text1(
            modifier = reusableItemModifier
            // ...
        )

        Box {
            Text2(
                // Weight won't do anything here since the Text composable is not a direct child of Column
                modifier = reusableItemModifier
                // ...
            )
        }
    }
    // [END android_compose_modifiers_scopedmodifiers2]
}

private object ChainingExtractedModifiers {
    val otherModifier = Modifier

    @Composable
    private fun Snippet() {
        // Note: In a real app, you would define the modifier outside of the composable where it is
        // needed. For the snippet in the documentation, putting it in one place is clearer.
        // [START android_compose_modifiers_chainingextractedmodifiers]
        val reusableModifier = Modifier
            .fillMaxWidth()
            .background(Color.Red)
            .padding(12.dp)

        // Append to your reusableModifier
        reusableModifier.clickable { /*...*/ }

        // Append your reusableModifier
        otherModifier.then(reusableModifier)
        // [END android_compose_modifiers_chainingextractedmodifiers]
    }
}

// ----- Classes and functions below are to simplify snippets above ----- //

private data class Artist(val name: String, val lastSeenOnline: String)
private data class Author(val name: String)

@Composable
private fun Image(modifier: Modifier = Modifier) {
    // Empty to allow Image(/*...*/) in snippets
}

@Composable
private fun ArtistCard() {
    // Empty for snippets
}

@Composable
private fun LoadingWheel(modifier: Modifier, animatedState: State<Float>) {
    // Empty for snippets
}

@Composable
private fun animateFloatAsState(): State<Float> {
    return AnimationState(initialValue = 0f)
}

@Composable
private fun HeaderText(modifier: Modifier = Modifier) {
    // Empty for snippets
}

@Composable
private fun SubtitleText(modifier: Modifier = Modifier) {
    // Empty for snippets
}

@Composable
private fun AsyncImage(modifier: Modifier = Modifier) {
    // Empty for snippets
}

@Composable
private fun Text1(modifier: Modifier = Modifier) {
    // Empty for snippets
}

@Composable
private fun Text2(modifier: Modifier = Modifier) {
    // Empty for snippets
}
