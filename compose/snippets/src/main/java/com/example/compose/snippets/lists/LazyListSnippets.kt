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

@file:Suppress("unused", "UNUSED_VARIABLE")

package com.example.compose.snippets.lists

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.paging.Pager
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.compose.snippets.tooling.Preview
import com.example.compose.snippets.util.randomSampleImageUrl
import kotlin.random.Random
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

private object ListsSnippetsColumn {
    // [START android_compose_layouts_list_column]
    @Composable
    fun MessageList(messages: List<Message>) {
        Column {
            messages.forEach { message ->
                MessageRow(message)
            }
        }
    }
    // [END android_compose_layouts_list_column]
}

@Composable
private fun ListsSnippetsLazyListScope1() {
    // [START android_compose_layouts_lazy_column_basic]
    LazyColumn {
        // Add a single item
        item {
            Text(text = "First item")
        }

        // Add 5 items
        items(5) { index ->
            Text(text = "Item: $index")
        }

        // Add another single item
        item {
            Text(text = "Last item")
        }
    }
    // [END android_compose_layouts_lazy_column_basic]
}

private object ListsSnippetsLazyListScope2 {
    @Composable
    fun MessageList(messages: List<Message>) {
        // [START android_compose_layouts_lazy_column_basic_extension]
        /**
         * import androidx.compose.foundation.lazy.items
         */
        LazyColumn {
            items(messages) { message ->
                MessageRow(message)
            }
        }
        // [END android_compose_layouts_lazy_column_basic_extension]
    }
}
@Composable
private fun GridItemSpanExample() {
    // [START android_compose_layouts_lazy_vertical_grid_full_span]
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 30.dp)
    ) {
        item(span = {
            // LazyGridItemSpanScope:
            // maxLineSpan
            GridItemSpan(maxLineSpan)
        }) {
            CategoryCard("Fruits")
        }
        // ...
    }
    // [END android_compose_layouts_lazy_vertical_grid_full_span]
}
@Composable
private fun CategoryCard(category: String) {
}
@Composable
private fun ListsSnippetsContentPadding() {
    // [START android_compose_layouts_lazy_column_padding]
    LazyColumn(
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    ) {
        // ...
    }
    // [END android_compose_layouts_lazy_column_padding]
}

@Composable
private fun ListsSnippetsContentSpacing1() {
    // [START android_compose_layouts_lazy_content_spacing]
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // ...
    }
    // [END android_compose_layouts_lazy_content_spacing]
}

@Composable
private fun ListsSnippetsContentSpacing2() {
    // [START android_compose_layouts_lazy_row_content_spacing]
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        // ...
    }
    // [END android_compose_layouts_lazy_row_content_spacing]
}

@Composable
private fun ListsSnippetsContentSpacing3(photos: List<Photo>) {
    // [START android_compose_layouts_lazy_grid_content_spacing]
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(photos) { item ->
            PhotoItem(item)
        }
    }
    // [END android_compose_layouts_lazy_grid_content_spacing]
}

private object ListsSnippetsStickyHeaders1 {
    // [START android_compose_layouts_lazy_column_sticky_header]
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ListWithHeader(items: List<Item>) {
        LazyColumn {
            stickyHeader {
                Header()
            }

            items(items) { item ->
                ItemRow(item)
            }
        }
    }
    // [END android_compose_layouts_lazy_column_sticky_header]
}

private object ListsSnippetsStickyHeaders2 {
    // [START android_compose_layouts_lazy_column_sticky_header_multiple]
    // This ideally would be done in the ViewModel
    val grouped = contacts.groupBy { it.firstName[0] }

    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    fun ContactsList(grouped: Map<Char, List<Contact>>) {
        LazyColumn {
            grouped.forEach { (initial, contactsForInitial) ->
                stickyHeader {
                    CharacterHeader(initial)
                }

                items(contactsForInitial) { contact ->
                    ContactListItem(contact)
                }
            }
        }
    }
    // [END android_compose_layouts_lazy_column_sticky_header_multiple]
}

private object ListsSnippetsGrids {
    @Composable
    fun PhotoGrid(photos: List<Photo>) {
        // [START android_compose_layouts_lazy_grid_adaptive]
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 128.dp)
        ) {
            items(photos) { photo ->
                PhotoItem(photo)
            }
        }
        // [END android_compose_layouts_lazy_grid_adaptive]
    }
}

private object ListsSnippetsReactingScrollPosition1 {
    // [START android_compose_layouts_lazy_column_state]
    @Composable
    fun MessageList(messages: List<Message>) {
        // Remember our own LazyListState
        val listState = rememberLazyListState()

        // Provide it to LazyColumn
        LazyColumn(state = listState) {
            // ...
        }
    }
    // [END android_compose_layouts_lazy_column_state]
}

private object ListsSnippetsReactingScrollPosition2 {
    // [START android_compose_layouts_lazy_column_scroll_to_top]
    @OptIn(ExperimentalAnimationApi::class)
    @Composable
    fun MessageList(messages: List<Message>) {
        Box {
            val listState = rememberLazyListState()

            LazyColumn(state = listState) {
                // ...
            }

            // Show the button if the first visible item is past
            // the first item. We use a remembered derived state to
            // minimize unnecessary compositions
            val showButton by remember {
                derivedStateOf {
                    listState.firstVisibleItemIndex > 0
                }
            }

            AnimatedVisibility(visible = showButton) {
                ScrollToTopButton()
            }
        }
    }
    // [END android_compose_layouts_lazy_column_scroll_to_top]
}

@Composable
private fun ListsSnippetsReactingScrollPosition3(messages: List<Message>) {
    // [START android_compose_layouts_lazy_column_state_react_event]
    val listState = rememberLazyListState()

    LazyColumn(state = listState) {
        // ...
    }

    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .map { index -> index > 0 }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                MyAnalyticsService.sendScrolledPastFirstItemEvent()
            }
    }
    // [END android_compose_layouts_lazy_column_state_react_event]
}

private object ListsSnippetsControllingScrollPosition {
    // [START android_compose_layouts_lazy_column_animate_scroll_to_item]
    @Composable
    fun MessageList(messages: List<Message>) {
        val listState = rememberLazyListState()
        // Remember a CoroutineScope to be able to launch
        val coroutineScope = rememberCoroutineScope()

        LazyColumn(state = listState) {
            // ...
        }

        ScrollToTopButton(
            onClick = {
                coroutineScope.launch {
                    // Animate scroll to the first item
                    listState.animateScrollToItem(index = 0)
                }
            }
        )
    }
    // [END android_compose_layouts_lazy_column_animate_scroll_to_item]
}

private object ListsSnippetsPaging {
    // [START android_compose_layouts_lazy_column_paging]
    @Composable
    fun MessageList(pager: Pager<Int, Message>) {
        val lazyPagingItems = pager.flow.collectAsLazyPagingItems()

        LazyColumn {
            items(
                lazyPagingItems.itemCount,
                key = lazyPagingItems.itemKey { it.id }
            ) { index ->
                val message = lazyPagingItems[index]
                if (message != null) {
                    MessageRow(message)
                } else {
                    MessagePlaceholder()
                }
            }
        }
    }
    // [END android_compose_layouts_lazy_column_paging]
}

private object ListsSnippetsItemKeys {
    @Composable
    fun MessageList(messages: List<Message>) {
        // [START android_compose_layouts_lazy_column_item_keys]
        LazyColumn {
            items(
                items = messages,
                key = { message ->
                    // Return a stable + unique key for the item
                    message.id
                }
            ) { message ->
                MessageRow(message)
            }
        }
        // [END android_compose_layouts_lazy_column_item_keys]
    }
}
data class Book(val id: String)
@Composable
private fun LazyColumnRemembered() {
    val books = remember {
        listOf(Book("1"))
    }
    // [START android_compose_layouts_lazy_column_remembered_value]
    LazyColumn {
        items(books, key = { it.id }) {
            val rememberedValue = remember {
                Random.nextInt()
            }
        }
    }
    // [END android_compose_layouts_lazy_column_remembered_value]
}

@Composable
private fun LazyColumnAnyKey() {
    val books = remember {
        listOf(Book("1"))
    }
    // [START android_compose_layouts_lazy_column_any_key]
    LazyColumn {
        items(books, key = {
            // primitives, enums, Parcelable, etc.
        }) {
            // ...
        }
    }
    // [END android_compose_layouts_lazy_column_any_key]
}

@Composable
private fun LazyColumnRememberSaveable() {
    val books = remember {
        listOf(Book("1"))
    }
    // [START android_compose_layouts_lazy_column_any_key_saveable]
    LazyColumn {
        items(books, key = { it.id }) {
            val rememberedValue = rememberSaveable {
                Random.nextInt()
            }
        }
    }
    // [END android_compose_layouts_lazy_column_any_key_saveable]
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyItemAnimations() {
    val books = remember {
        listOf(Book("1"))
    }
    // [START android_compose_layouts_lazy_column_item_animation]
    LazyColumn {
        items(books, key = { it.id }) {
            Row(Modifier.animateItemPlacement()) {
                // ...
            }
        }
    }
    // [END android_compose_layouts_lazy_column_item_animation]
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun LazyItemAnimationWithSpec() {
    val books = remember {
        listOf(Book("1"))
    }
    // [START android_compose_layouts_lazy_column_item_animation_with_spec]
    LazyColumn {
        items(books, key = { it.id }) {
            Row(
                Modifier.animateItemPlacement(
                    tween(durationMillis = 250)
                )
            ) {
                // ...
            }
        }
    }
    // [END android_compose_layouts_lazy_column_item_animation_with_spec]
}

private object LazyListTipSnippets {

    // [START android_compose_layouts_lazy_item_0_size]
    @Composable
    fun Item(imageUrl: String) {
        AsyncImage(
            model = rememberAsyncImagePainter(model = imageUrl),
            modifier = Modifier.size(30.dp),
            contentDescription = null
            // ...
        )
    }
    // [END android_compose_layouts_lazy_item_0_size]

    @Composable
    private fun NestingScrollableComponentsBroken() {
        // DON'T DO THIS
        val state = rememberScrollState()
        // [START android_compose_layouts_lazy_item_nested_scrollable_dont]
        // throws IllegalStateException
        Column(
            modifier = Modifier.verticalScroll(state)
        ) {
            LazyColumn {
                // ...
            }
        }
        // [END android_compose_layouts_lazy_item_nested_scrollable_dont]
    }
    @Composable
    private fun NestingScrollableComponents() {
        val data = remember {
            listOf(Photo())
        }
        // [START android_compose_layouts_lazy_item_nested_scrollable]
        LazyColumn {
            item {
                Header()
            }
            items(data) { item ->
                PhotoItem(item)
            }
            item {
                Footer()
            }
        }
        // [END android_compose_layouts_lazy_item_nested_scrollable]
    }

    @Composable
    private fun NestedRowColumn() {
        val scrollState = rememberScrollState()
        // This is allowed
        // [START android_compose_layouts_lazy_item_nested_row_column]
        Row(
            modifier = Modifier.horizontalScroll(scrollState)
        ) {
            LazyColumn {
                // ...
            }
        }
        // [END android_compose_layouts_lazy_item_nested_row_column]
    }

    @Composable
    private fun NestedLazyColumnFixedSize() {
        val scrollState = rememberScrollState()
        // This is allowed
        // [START android_compose_layouts_lazy_item_nested_lazy_column_fixed_size]
        Column(
            modifier = Modifier.verticalScroll(scrollState)
        ) {
            LazyColumn(
                modifier = Modifier.height(200.dp)
            ) {
                // ...
            }
        }
        // [END android_compose_layouts_lazy_item_nested_lazy_column_fixed_size]
    }

    @Composable
    private fun Footer() {
    }
}

private object LazyGridTipSnippets {
    @Composable
    private fun LazyGridMultipleElements_Avoid() {
        // Avoid combining items that are logically different items, as they are handled as one
        // entity now, it can hurt performance. It'll also interfere with methods such as
        // scrollToItem()
        // [START android_compose_layouts_lazy_grid_multiple_elements]
        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp)
        ) {
            item { Item(0) }
            item {
                Item(1)
                Item(2)
            }
            item { Item(3) }
            // ...
        }
        // [END android_compose_layouts_lazy_grid_multiple_elements]
    }

    @Composable
    private fun LazyGridMultipleElementsDivider() {
        // Valid use case for putting two composables together, when they are logically part of
        // the same item.
        // [START android_compose_layouts_lazy_grid_multiple_elements_divider]
        LazyVerticalGrid(
            columns = GridCells.Adaptive(100.dp)
        ) {
            item { Item(0) }
            item {
                Item(1)
                Divider()
            }
            item { Item(2) }
            // ...
        }
        // [END android_compose_layouts_lazy_grid_multiple_elements_divider]
    }
    @Composable
    private fun Item(id: Int) {
    }
    @Composable
    private fun Divider() {
    }
}

private object CustomArrangements {
    // [START android_compose_layouts_lazy_custom_vertical_arrangement]
    object TopWithFooter : Arrangement.Vertical {
        override fun Density.arrange(
            totalSize: Int,
            sizes: IntArray,
            outPositions: IntArray
        ) {
            var y = 0
            sizes.forEachIndexed { index, size ->
                outPositions[index] = y
                y += size
            }
            if (y < totalSize) {
                val lastIndex = outPositions.lastIndex
                outPositions[lastIndex] = totalSize - sizes.last()
            }
        }
    }
    // [END android_compose_layouts_lazy_custom_vertical_arrangement]
}

@Composable
private fun ContentTypeExample() {
    // [START android_compose_layouts_lazy_content_type]
    LazyColumn {
        items(elements, contentType = { it.type }) {
            // ...
        }
    }
    // [END android_compose_layouts_lazy_content_type]
}

@Preview
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyStaggeredGridSnippet() {
    // [START android_compose_layouts_lazy_staggered_grid_adaptive]
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Adaptive(200.dp),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        content = {
            items(randomSizedPhotos) { photo ->
                AsyncImage(
                    model = photo,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().wrapContentHeight()
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )
    // [END android_compose_layouts_lazy_staggered_grid_adaptive]
}
@Preview
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun LazyStaggeredGridSnippetFixed() {
    // [START android_compose_layouts_lazy_staggered_grid_fixed]
    LazyVerticalStaggeredGrid(
        columns = StaggeredGridCells.Fixed(3),
        verticalItemSpacing = 4.dp,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        content = {
            items(randomSizedPhotos) { photo ->
                AsyncImage(
                    model = photo,
                    contentScale = ContentScale.Crop,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth().wrapContentHeight()
                )
            }
        },
        modifier = Modifier.fillMaxSize()
    )
    // [END android_compose_layouts_lazy_staggered_grid_fixed]
}
private class Message(val id: Long)
private class Item

private data class Contact(val firstName: String)
private val contacts = listOf<Contact>()

private class Photo
private val photos = listOf<Photo>()

@Composable
private fun MessageRow(message: Message) = Unit

@Composable
private fun MessagePlaceholder() = Unit

@Composable
private fun ItemRow(item: Item) = Unit

@Composable
private fun Header() = Unit

@Composable
private fun CharacterHeader(initial: Char) = Unit

@Composable
private fun ContactListItem(contact: Contact) = Unit

@Composable
private fun PhotoItem(photo: Photo) = Unit

@Composable
private fun ScrollToTopButton(onClick: () -> Unit = {}) = Unit

private object MyAnalyticsService {
    fun sendScrolledPastFirstItemEvent() = Unit
}

private class ContentTypeElement(val type: Long)
private val elements = listOf<ContentTypeElement>()

private val randomSizedPhotos = listOf(
    randomSampleImageUrl(width = 1600, height = 900),
    randomSampleImageUrl(width = 900, height = 1600),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 300, height = 400),
    randomSampleImageUrl(width = 1600, height = 900),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 1600, height = 900),
    randomSampleImageUrl(width = 900, height = 1600),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 300, height = 400),
    randomSampleImageUrl(width = 1600, height = 900),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 900, height = 1600),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 300, height = 400),
    randomSampleImageUrl(width = 1600, height = 900),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 300, height = 400),
    randomSampleImageUrl(width = 1600, height = 900),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 900, height = 1600),
    randomSampleImageUrl(width = 500, height = 500),
    randomSampleImageUrl(width = 300, height = 400),
    randomSampleImageUrl(width = 1600, height = 900),
    randomSampleImageUrl(width = 500, height = 500),
)
