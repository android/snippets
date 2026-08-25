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

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.ActionExtrasKey
import androidx.navigation3.runtime.deeplink.BackStackMatchResult
import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.DeepLinkRequest.Companion.MimeTypeExtrasKey
import androidx.navigation3.runtime.deeplink.DeepLinkSerializer
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.IntentExtrasKey
import androidx.navigation3.runtime.deeplink.RequestExtras
import androidx.navigation3.runtime.deeplink.RequestExtrasKey
import androidx.navigation3.runtime.deeplink.StaticKeyDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.UriMatchResult
import androidx.navigation3.runtime.deeplink.WrappedMatchResult
import androidx.navigation3.runtime.deeplink.actionExtra
import androidx.navigation3.runtime.deeplink.actionFilter
import androidx.navigation3.runtime.deeplink.invoke
import androidx.navigation3.runtime.deeplink.requestExtras
import androidx.navigation3.runtime.deeplink.withBackStack
import androidx.savedstate.SavedState
import androidx.savedstate.read
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.multibindings.IntoSet
import javax.inject.Inject
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.serializer

// Shared keys used across snippets
@Serializable
data object HomeKey : NavKey

@Serializable
data class UserProfileKey(val id: String) : NavKey

@Serializable
data class UserListKey(val page: Int = 0) : NavKey

@Serializable
data class Gallery(val id: String) : NavKey

@Serializable
data class Editor(val id: String) : NavKey

@Serializable
data object PreferencesScreen : NavKey

@Serializable
data class DialerKey(val phoneNumber: String?) : NavKey

val DeepLinkRequest.Companion.MimeTypeExtrasKey: RequestExtrasKey<String>
    inline get() = DeepLinkRequest.Companion.MimeTypeExtrasKey

fun DeepLinkRequest.Companion.mimeTypeExtra(mimeType: String): RequestExtras =
    requestExtras { put(MimeTypeExtrasKey, mimeType) }

// [START android_compose_navigation3_deeplinks_filter_serializer]
@Serializable
data class Filter(val key: String, val value: String)

object FilterSerializer : DeepLinkSerializer<Filter>() {
    override val serialName: String = "com.example.Filter"

    override fun deserialize(value: String): Filter {
        val parts = value.split(":", limit = 2)
        if (parts.size < 2) {
            throw SerializationException("Invalid filter: $value. Expected 'key:value'.")
        }
        return Filter(key = parts[0], value = parts[1])
    }

    override fun serialize(value: Filter): String = "${value.key}:${value.value}"
}
// [END android_compose_navigation3_deeplinks_filter_serializer]

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

// [START android_compose_navigation3_deeplinks_custom_matcher]
class TelDeepLinkMatcher : DeepLinkMatcher<DialerKey, DeepLinkMatcher.MatchResult<DialerKey>>() {
    override fun matchRequest(request: DeepLinkRequest): MatchResult<DialerKey>? {
        val uri = request.uri ?: return null
        if (uri.scheme != "tel") return null

        // Note: schemeSpecificPart is only available on Android
        val phoneNumber = uri.schemeSpecificPart ?: return null
        return MatchResult(DialerKey(phoneNumber = phoneNumber))
    }
}
// [END android_compose_navigation3_deeplinks_custom_matcher]

// [START android_compose_navigation3_deeplinks_custom_matcher_result]
class TelMatchResult(
    key: DialerKey,
    val isExactMatch: Boolean,
    val patternLength: Int
) : DeepLinkMatcher.MatchResult<DialerKey>(key) {
    override fun compareTo(other: DeepLinkMatcher.MatchResult<DialerKey>): Int {
        // Unwrap if the other result is wrapped in a BackStackMatchResult or custom WrappedMatchResult
        val target = if (other is WrappedMatchResult<*>) other.matchResult else other
        if (target !is TelMatchResult) {
            // Determine precedence relative to other MatchResult types (e.g. UriMatchResult)
            return 1
        }

        // An exact match wins over a wildcard/prefix match
        if (isExactMatch && !target.isExactMatch) return 1
        if (!isExactMatch && target.isExactMatch) return -1

        // The more specific (longer) pattern wins (e.g., tel:1800* versus tel:*)
        val lengthDiff = this.patternLength - target.patternLength
        if (lengthDiff != 0) {
            return lengthDiff
        }

        return 0
    }
}
// [END android_compose_navigation3_deeplinks_custom_matcher_result]

object DeepLinkSnippets {

    fun requestCreation() {
        // [START android_compose_navigation3_deeplinks_request]
        // Create a request with a String URI
        val request = DeepLinkRequest(uri = "https://www.example.com/home")

        // Create a request from a DeepLinkUri
        val deepLinkUri = DeepLinkUri("https://www.example.com/home")
        val requestFromUri = DeepLinkRequest(uri = deepLinkUri)

        // Create a request with a URI and action
        val requestWithAction = DeepLinkRequest(
            uri = "https://www.example.com/home",
            extras = DeepLinkRequest.actionExtra("android.intent.action.VIEW")
        )

        // Create a request with URI, action and mimeType
        val requestWithMimeType = DeepLinkRequest(
            uri = "https://www.example.com/image",
            extras = requestExtras {
                put(DeepLinkRequest.ActionExtrasKey, "android.intent.action.VIEW")
                put(DeepLinkRequest.MimeTypeExtrasKey, "image/png")
            }
        )
        // [END android_compose_navigation3_deeplinks_request]
    }

    object RequestIntentSnippet {
        // [START android_compose_navigation3_deeplinks_request_intent]
        object CampaignIdExtrasKey : RequestExtrasKey<String>

        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://www.example.com/item/42")
            type = "application/json"
            putExtra("user_id", "123")
        }

        val request = DeepLinkRequest(
            intent = intent,
            extras = requestExtras {
                put(CampaignIdExtrasKey, "spring_promo")
            }
        )

        // The resulting DeepLinkRequest contains:
        val uri = request.uri // "https://www.example.com/item/42"
        val action = request.extras[DeepLinkRequest.ActionExtrasKey] // "android.intent.action.VIEW"
        val mimeType = request.extras[DeepLinkRequest.MimeTypeExtrasKey] // "application/json"
        val intentExtras: SavedState? =
            request.extras[DeepLinkRequest.IntentExtrasKey]
        val userId: String? = intentExtras?.read { getStringOrNull("user_id") } // "123"
        val campaignId: String? = request.extras[CampaignIdExtrasKey] // "spring_promo"
        // [END android_compose_navigation3_deeplinks_request_intent]
    }

    fun requestExtrasDsl() {
        // [START android_compose_navigation3_deeplinks_extras_dsl]
        val extras: RequestExtras = requestExtras {
            put(DeepLinkRequest.MimeTypeExtrasKey, "application/json")
            put(DeepLinkRequest.ActionExtrasKey, Intent.ACTION_VIEW)
        }

        // Access typed values using the get operator
        val mimeType: String? = extras[DeepLinkRequest.MimeTypeExtrasKey]
        val action: String? = extras[DeepLinkRequest.ActionExtrasKey]

        // Create extras using helper functions and combine them
        val mimeTypeExtras: RequestExtras = DeepLinkRequest.mimeTypeExtra("application/json")
        val combinedExtras: RequestExtras = extras + DeepLinkRequest.actionExtra(Intent.ACTION_VIEW)
        // [END android_compose_navigation3_deeplinks_extras_dsl]
    }

    object CustomExtrasSnippet {
        // [START android_compose_navigation3_deeplinks_custom_extras]
        // Define a custom typed key:
        object CampaignIdExtrasKey : RequestExtrasKey<String>

        val customExtras: RequestExtras = requestExtras {
            put(CampaignIdExtrasKey, "123")
        }

        val campaignId: String? = customExtras[CampaignIdExtrasKey]
        // [END android_compose_navigation3_deeplinks_custom_extras]
    }

    // [START android_compose_navigation3_deeplinks_filter]
    val viewFilter = DeepLinkMatcher.actionFilter(Intent.ACTION_VIEW)
    val editFilter = DeepLinkMatcher.actionFilter(Intent.ACTION_EDIT)

    val imageUriPattern = DeepLinkUri("www.example.com/image/{id}")

    val viewMatcher = UriDeepLinkMatcher(imageUriPattern, serializer<Gallery>(), filters = listOf(viewFilter))
    val editMatcher = UriDeepLinkMatcher(imageUriPattern, serializer<Editor>(), filters = listOf(editFilter))
    // [END android_compose_navigation3_deeplinks_filter]

    fun filterLambda() {
        // [START android_compose_navigation3_deeplinks_filter_lambda]
        val myFilter = DeepLinkMatcher.Filter { request -> request.uri != null }
        // [END android_compose_navigation3_deeplinks_filter_lambda]
    }

    fun staticMatcher() {
        // [START android_compose_navigation3_deeplinks_static_matcher]
        val preferencesActionFilter = DeepLinkMatcher.actionFilter(Intent.ACTION_APPLICATION_PREFERENCES)

        val preferencesActionDeepLinkMatcher = StaticKeyDeepLinkMatcher(PreferencesScreen, listOf(preferencesActionFilter))
        // [END android_compose_navigation3_deeplinks_static_matcher]
    }

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

    fun backStackMatcher() {
        // [START android_compose_navigation3_deeplinks_backstack]
        val homeMatcher = StaticKeyDeepLinkMatcher(HomeKey, listOf(DeepLinkMatcher.actionFilter(Intent.ACTION_VIEW)))

        val userListMatcher = UriDeepLinkMatcher(
            DeepLinkUri("www.example.com/users?page={page}"),
            serializer<UserListKey>()
        ).withBackStack { matchResult ->
            listOf(HomeKey, matchResult.key)
        }

        val userProfileMatcher = UriDeepLinkMatcher(
            DeepLinkUri("www.example.com/users/{id}"),
            serializer<UserProfileKey>()
        ).withBackStack { matchResult ->
            listOf(HomeKey, UserListKey(), matchResult.key)
        }
        // [END android_compose_navigation3_deeplinks_backstack]
    }
}

// [START android_compose_navigation3_deeplinks_match_request]
val deepLinkMatchers = listOf(
    StaticKeyDeepLinkMatcher(HomeKey, listOf(DeepLinkMatcher.actionFilter(Intent.ACTION_VIEW))),
    UriDeepLinkMatcher(DeepLinkUri("www.example.com/users/{id}"), serializer<UserProfileKey>()),
    TelDeepLinkMatcher()
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ...
        val request = DeepLinkRequest(intent = intent)
        val matchResult = deepLinkMatchers
            .mapNotNull { it.match(request) }
            .maxOrNull()

        val backStack = when (matchResult) {
            null -> listOf(HomeKey)
            is BackStackMatchResult<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                matchResult.backStack as List<NavKey>
            }
            else -> listOf(matchResult.key)
        }
    }
}
// [END android_compose_navigation3_deeplinks_match_request]

// [START android_compose_navigation3_deeplinks_modularization_feature]
@Module
@InstallIn(ActivityRetainedComponent::class)
object FeatureADeepLinkModule {
    @IntoSet
    @Provides
    // [START_EXCLUDE silent]
    @JvmSuppressWildcards
    // [END_EXCLUDE]
    fun provideUserMatcher(): DeepLinkMatcher<*, *> {
        return UriDeepLinkMatcher(
            DeepLinkUri("www.example.com/users/{id}"),
            serializer<UserProfileKey>()
        )
    }
}
// [END android_compose_navigation3_deeplinks_modularization_feature]

// [START android_compose_navigation3_deeplinks_modularization_app]
// [START_EXCLUDE silent]
@dagger.hilt.android.AndroidEntryPoint
// [END_EXCLUDE]
class MainActivityWithDI : ComponentActivity() {
    @Inject
    lateinit var deepLinkMatchers: Set<@JvmSuppressWildcards DeepLinkMatcher<*, *>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val request = DeepLinkRequest(intent = intent)
        val matchResult = deepLinkMatchers
            .mapNotNull { it.match(request) }
            .maxOrNull()

        val backStack = when (matchResult) {
            null -> listOf(HomeKey)
            is BackStackMatchResult<*, *> -> {
                @Suppress("UNCHECKED_CAST")
                matchResult.backStack as List<NavKey>
            }
            else -> listOf(matchResult.key)
        }
    }
}
// [END android_compose_navigation3_deeplinks_modularization_app]
