package com.example.compose.snippets.layouts.grid

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.android.tools.screenshot.PreviewTest


@Preview
@PreviewTest
@Composable
fun GridExamplePreviewTest() {
    GridExample()
}

@Preview
@PreviewTest
@Composable
fun SixCardsPreviewTest() {
    SixCards()
}

@Preview
@PreviewTest
@Composable
fun DefineGridPreviewTest() {
    DefineGrid()
}

@Preview
@PreviewTest
@Composable
fun DefineGridAndPlaceItemsPreviewTest() {
    DefineGridAndPlaceItems()
}

@Preview
@PreviewTest
@Composable
fun DefineExplicitAndImplicitGridTracksPreviewTest() {
    DefineExplicitAndImplicitGridTracks()
}
