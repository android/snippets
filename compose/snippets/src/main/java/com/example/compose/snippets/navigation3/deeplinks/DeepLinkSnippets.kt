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
import androidx.navigation3.runtime.deeplink.actionExtra
import androidx.navigation3.runtime.deeplink.actionFilter
import androidx.navigation3.runtime.deeplink.invoke
import androidx.navigation3.runtime.deeplink.requestExtras
import androidx.navigation3.runtime.deeplink.withBackStack
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

// Define keys used in snippets
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

@Serializable
data object MovieKey : NavKey

@Serializable
data class MovieDetailsKey(val id: String) : NavKey

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
    val filters: SearchFilters
) : NavKey

enum class UserDetailsTab { INFO, ACTIVITY, SETTINGS }

@Serializable
data class UserDetailsKey(val id: Int, val initialTab: UserDetailsTab = UserDetailsTab.INFO) : NavKey

val DeepLinkRequest.Companion.MimeTypeExtrasKey: RequestExtrasKey<String>
    inline get() = DeepLinkRequest.Companion.MimeTypeExtrasKey

// [START android_compose_navigation3_deeplinks_custom_serializer]
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

@Serializable
data class SearchResultsKey(
    val query: String,
    val filters: List<@Serializable(with = FilterSerializer::class) Filter> = emptyList()
) : NavKey
// [START_EXCLUDE]
fun customSerializerUsage() {
// [END_EXCLUDE]
    val searchResultsPattern = DeepLinkUri("www.example.com/search?q={query}&filter={filters}")
    val searchResultsMatcher = UriDeepLinkMatcher(searchResultsPattern, serializer<SearchResultsKey>())

    val request = DeepLinkRequest(uri = "https://www.example.com/search?q=phone&filter=brand:pixel&filter=color:hazel")
    val matchResult = searchResultsMatcher.match(request)
    val key = matchResult?.key
// SearchResultsKey(query = "phone", filters = listOf(Filter("brand", "pixel"), Filter("color", "hazel")))
// [START_EXCLUDE]
}
// [END_EXCLUDE]
// [END android_compose_navigation3_deeplinks_custom_serializer]

// [START android_compose_navigation3_deeplinks_uri_matcher_unsupported]
// Throws IllegalArgumentException: Map decoding is not supported.
@Serializable
data class InvalidMapKey(val tags: Map<String, String>) : NavKey

// Throws SerializationException: Only collections of primitives are supported.
@Serializable
data class InvalidListKey(val filters: List<Filter>) : NavKey

// Fix: Annotate the element with @Serializable(with = FilterSerializer::class)
@Serializable
data class ValidListKey(
    val filters: List<@Serializable(with = FilterSerializer::class) Filter>
) : NavKey
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
        if (other !is TelMatchResult) {
            // Determine precedence relative to other MatchResult types (e.g. UriMatchResult)
            return 1
        }

        // An exact match wins over a wildcard/prefix match
        if (isExactMatch && !other.isExactMatch) return 1
        if (!isExactMatch && other.isExactMatch) return -1

        // The more specific (longer) pattern wins (e.g., tel:1800* versus tel:*)
        val lengthDiff = this.patternLength - other.patternLength
        if (lengthDiff != 0) {
            return lengthDiff
        }

        return 0
    }
}
// [END android_compose_navigation3_deeplinks_custom_matcher_result]

class DeepLinkSnippets {

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

    fun requestFromIntent() {
        // [START android_compose_navigation3_deeplinks_request_intent]
        val intent = Intent(Intent.ACTION_VIEW).apply {
            data = Uri.parse("https://www.example.com/item/42")
            type = "application/json"
            putExtra("user_id", "123")
        }

        val request = DeepLinkRequest(
            intent = intent,
            extras = requestExtras {
                put(SnippetCustomExtras.CampaignIdExtrasKey, "spring_promo")
            }
        )

        // The resulting DeepLinkRequest contains:
        val uri = request.uri // "https://www.example.com/item/42"
        val action = request.extras[DeepLinkRequest.ActionExtrasKey] // "android.intent.action.VIEW"
        val mimeType = request.extras[DeepLinkRequest.MimeTypeExtrasKey] // "application/json"
        val intentExtras: android.os.Bundle? =
            request.extras[DeepLinkRequest.IntentExtrasKey]
        val userId: String? = intentExtras?.getString("user_id") // "123"
        val campaignId: String? = request.extras[SnippetCustomExtras.CampaignIdExtrasKey] // "spring_promo"
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

    object SnippetCustomExtras {
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

    fun uriMatcher() {
        // [START android_compose_navigation3_deeplinks_uri_matcher]
        val userProfilePattern = DeepLinkUri("www.example.com/users/{id}")
        val userProfileMatcher = UriDeepLinkMatcher(userProfilePattern, serializer<UserProfileKey>())

        val request = DeepLinkRequest(uri = "https://www.example.com/users/123")
        val matchResult = userProfileMatcher.match(request)
        val key = matchResult?.key // UserProfileKey(id = "123")
        // [END android_compose_navigation3_deeplinks_uri_matcher]
    }

    fun uriMatcherQuery() {
        // [START android_compose_navigation3_deeplinks_uri_matcher_query]
        val searchPattern = DeepLinkUri("www.example.com/search?q={query}&page={page}&category={category}&sortBy={sortBy}")
        val searchMatcher = UriDeepLinkMatcher(searchPattern, serializer<SearchKey>())

        val request = DeepLinkRequest(uri = "https://www.example.com/search?q=kotlin&category=books&sortBy=DATE")
        val matchResult = searchMatcher.match(request)

        val key = matchResult?.key // SearchKey(query = "kotlin", page = 1, filters = SearchFilters(category = "books", sortBy = SortOrder.DATE))
        // [END android_compose_navigation3_deeplinks_uri_matcher_query]
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
    // [START_EXCLUDE]
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
// [START_EXCLUDE]
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
