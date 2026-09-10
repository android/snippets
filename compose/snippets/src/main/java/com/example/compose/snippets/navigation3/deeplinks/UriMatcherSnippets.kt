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

package com.example.compose.snippets.navigation3.deeplinks

import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.DeepLinkSerializer
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.UriMatchResult
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.serializer

object UriMatcherSnippets {

    object UriMatcherSnippet {
        // [START android_compose_navigation3_deeplinks_uri_matcher]
        @Serializable
        data class UserProfileKey(val id: String) : NavKey

        val userProfilePattern = DeepLinkUri("www.example.com/users/{id}")
        val userProfileMatcher = UriDeepLinkMatcher(userProfilePattern, serializer<UserProfileKey>())

        val request = DeepLinkRequest(uri = "https://www.example.com/users/123")
        val matchResult = userProfileMatcher.match(request)
        val key = matchResult?.key // UserProfileKey(id = "123")
        // [END android_compose_navigation3_deeplinks_uri_matcher]
    }

    object UriMatcherPrimitivesSnippet {
        // [START android_compose_navigation3_deeplinks_uri_matcher_primitives]
        @Serializable
        data class UserProfileKey(val id: Int) : NavKey

        val matcher = UriDeepLinkMatcher(
            DeepLinkUri("www.example.com/users/{id}"),
            serializer<UserProfileKey>()
        )

        val request = DeepLinkRequest(uri = "https://www.example.com/users/123")
        val key = matcher.match(request)?.key // UserProfileKey(id = 123)
        // [END android_compose_navigation3_deeplinks_uri_matcher_primitives]
    }

    object UriMatcherEnumsSnippet {
        // [START android_compose_navigation3_deeplinks_uri_matcher_enums]
        enum class SortOrder { RELEVANCE, DATE, POPULARITY }

        @Serializable
        data class ProductsKey(val sort: SortOrder) : NavKey

        val matcher = UriDeepLinkMatcher(
            DeepLinkUri("www.example.com/products?sort={sort}"),
            serializer<ProductsKey>()
        )

        val request = DeepLinkRequest(uri = "https://www.example.com/products?sort=DATE")
        val key = matcher.match(request)?.key // ProductsKey(sort = SortOrder.DATE)
        // [END android_compose_navigation3_deeplinks_uri_matcher_enums]
    }

    object UriMatcherRepeatedQuerySnippet {
        // [START android_compose_navigation3_deeplinks_uri_matcher_repeated_query]
        @Serializable
        data class FilteredItemsKey(val ids: List<Int>) : NavKey

        val matcher = UriDeepLinkMatcher(
            DeepLinkUri("www.example.com/items?id={ids}"),
            serializer<FilteredItemsKey>()
        )

        val request = DeepLinkRequest(uri = "https://www.example.com/items?id=10&id=20")
        val key = matcher.match(request)?.key // FilteredItemsKey(ids = listOf(10, 20))
        // [END android_compose_navigation3_deeplinks_uri_matcher_repeated_query]
    }

    object UriMatcherQuerySnippet {
        // [START android_compose_navigation3_deeplinks_uri_matcher_query]
        enum class SortOrder { RELEVANCE, DATE, POPULARITY }

        @Serializable
        data class SearchFilters(
            val category: String,
            val sortBy: SortOrder = SortOrder.RELEVANCE
        )

        @Serializable
        data class SearchKey(
            val query: String,
            val page: Int = 1,
            // Flattened into {category} and {sortBy}
            val filters: SearchFilters
        ) : NavKey

        val matcher = UriDeepLinkMatcher(
            DeepLinkUri("www.example.com/search?q={query}&page={page}&category={category}&sortBy={sortBy}"),
            serializer<SearchKey>()
        )

        val request = DeepLinkRequest(uri = "https://www.example.com/search?q=kotlin&category=books&sortBy=DATE")
        val key = matcher.match(request)?.key
        // SearchKey(query = "kotlin", page = 1, filters = SearchFilters(category = "books", sortBy = SortOrder.DATE))
        // [END android_compose_navigation3_deeplinks_uri_matcher_query]
    }

    // [START android_compose_navigation3_deeplinks_filter_serializer]
    @Serializable
    data class Filter(val key: String, val value: String)

    object FilterSerializer : DeepLinkSerializer<Filter>() {
        override val serialName: String = "com.example.Filter"

        override fun deserialize(value: String): Filter {
            val parts = value.split(":", limit = 2)
            if (parts.size < 2) {
                throw SerializationException("Invalid filter: $value. Expected key:value.")
            }
            return Filter(key = parts[0], value = parts[1])
        }

        override fun serialize(value: Filter): String = "${value.key}:${value.value}"
    }
    // [END android_compose_navigation3_deeplinks_filter_serializer]

    object CustomObjectSnippet {
        // [START android_compose_navigation3_deeplinks_uri_matcher_custom_object]
        @Serializable
        data class CatalogKey(
            @Serializable(with = FilterSerializer::class)
            val filter: Filter
        ) : NavKey

        val matcher = UriDeepLinkMatcher(
            DeepLinkUri("www.example.com/catalog?filter={filter}"),
            serializer<CatalogKey>()
        )

        val request = DeepLinkRequest(uri = "https://www.example.com/catalog?filter=brand:google")
        val key = matcher.match(request)?.key // CatalogKey(filter = Filter("brand", "google"))
        // [END android_compose_navigation3_deeplinks_uri_matcher_custom_object]
    }

    object CustomSerializerCollectionSnippet {
        // [START android_compose_navigation3_deeplinks_custom_serializer]
        @Serializable
        data class SearchResultsKey(
            val query: String,
            val filters: List<@Serializable(with = FilterSerializer::class) Filter> = emptyList()
        ) : NavKey

        val searchResultsPattern = DeepLinkUri("www.example.com/search?q={query}&filter={filters}")
        val searchResultsMatcher = UriDeepLinkMatcher(searchResultsPattern, serializer<SearchResultsKey>())

        val request = DeepLinkRequest(uri = "https://www.example.com/search?q=phone&filter=brand:google&filter=color:hazel")
        val matchResult = searchResultsMatcher.match(request)
        val key = matchResult?.key
        // SearchResultsKey(query = "phone", filters = listOf(Filter("brand", "google"), Filter("color", "hazel")))
        // [END android_compose_navigation3_deeplinks_custom_serializer]
    }

    object CsvSnippet {
        // [START android_compose_navigation3_deeplinks_uri_matcher_csv]
        object IntListCsvSerializer : DeepLinkSerializer<List<Int>>() {
            override val serialName: String = "com.example.IntListCsv"

            override fun deserialize(value: String): List<Int> {
                if (value.isEmpty()) return emptyList()
                return value.split(",").map { it.trim().toInt() }
            }

            override fun serialize(value: List<Int>): String = value.joinToString(",")
        }

        @Serializable
        data class ItemListKey(
            @Serializable(with = IntListCsvSerializer::class)
            val ids: List<Int>
        ) : NavKey

        val itemListPattern = DeepLinkUri("www.example.com/items/{ids}")
        val itemListMatcher = UriDeepLinkMatcher(itemListPattern, serializer<ItemListKey>())

        val request = DeepLinkRequest(uri = "https://www.example.com/items/10,20,30")
        val key = itemListMatcher.match(request)?.key // ItemListKey(ids = listOf(10, 20, 30))
        // [END android_compose_navigation3_deeplinks_uri_matcher_csv]
    }

    object MismatchesSnippet {
        // [START android_compose_navigation3_deeplinks_uri_matcher_mismatches]
        enum class MapLayer { STANDARD, SATELLITE, TERRAIN }

        @Serializable
        data class LayerOptions(
            val style: String,
            val layer: MapLayer = MapLayer.STANDARD
        )

        @Serializable
        data class MapKey(
            val location: String,
            val zoom: Int = 12,
            val options: LayerOptions
        ) : NavKey

        val matcher = UriDeepLinkMatcher(
            DeepLinkUri("www.example.com/map/{location}?zoom={zoom}&style={style}&layer={layer}"),
            serializer<MapKey>()
        )
        // [END android_compose_navigation3_deeplinks_uri_matcher_mismatches]
    }

    // [START android_compose_navigation3_deeplinks_uri_matcher_unsupported]
    // [START_EXCLUDE silent]
    object InvalidMapScope {
        // [END_EXCLUDE]
        // Throws IllegalArgumentException: Map decoding is not supported.
        @Serializable
        data class InvalidKey(val tags: Map<String, String>) : NavKey
        // [START_EXCLUDE silent]
    }

    object InvalidListScope {
        // [END_EXCLUDE]
        // Throws SerializationException: Only collections of primitives are supported.
        @Serializable
        data class InvalidKey(val filters: List<Filter>) : NavKey
        // [START_EXCLUDE silent]
    }
    // [END_EXCLUDE]
    // [END android_compose_navigation3_deeplinks_uri_matcher_unsupported]

    // [START android_compose_navigation3_deeplinks_custom_uri_matcher]
    class LegacyPrefixUriDeepLinkMatcher<T : Any>(
        uriPattern: DeepLinkUri,
        serializer: KSerializer<T>
    ) : UriDeepLinkMatcher<T>(uriPattern, serializer) {

        override fun matchUri(uri: DeepLinkUri): UriMatchResult<T>? {
            val path = uri.path
            val normalizedUri = if (path != null && path.startsWith("/legacy/")) {
                DeepLinkUri(uri.toString().replaceFirst("/legacy", ""))
            } else {
                uri
            }
            return super.matchUri(normalizedUri)
        }
    }
    // [END android_compose_navigation3_deeplinks_custom_uri_matcher]
}
