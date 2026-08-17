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
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.deeplink.ActionExtrasKey
import androidx.navigation3.runtime.deeplink.BackStackMatchResult
import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.DeepLinkRequest.Companion.MimeTypeExtrasKey
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.RequestExtrasKey
import androidx.navigation3.runtime.deeplink.StaticKeyDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
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
import kotlinx.serialization.Serializable
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

enum class SortOrder { RELEVANCE, DATE, RATING }

@Serializable
data class SearchFilters(val category: String, val sortBy: SortOrder)

@Serializable
data class SearchKey(val query: String, val filters: SearchFilters) : NavKey

enum class UserDetailsTab { INFO, ACTIVITY, SETTINGS }

@Serializable
data class UserDetailsKey(val id: Int, val initialTab: UserDetailsTab = UserDetailsTab.INFO) : NavKey

object CampaignIdExtrasKey : RequestExtrasKey<String>

class DeepLinkSnippets {

    fun requestCreation() {
        // [START android_compose_navigation3_deeplinks_request]
        // Create a request with only a URI
        val request = DeepLinkRequest(uri = "https://www.example.com/home")

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
                put(MimeTypeExtrasKey, "image/png")
            }
        )
        // [END android_compose_navigation3_deeplinks_request]
    }

    fun requestFromIntent(intent: Intent) {
        // [START android_compose_navigation3_deeplinks_request_intent]
        val request = DeepLinkRequest(intent = intent)
        // [END android_compose_navigation3_deeplinks_request_intent]
    }

    fun requestExtrasDsl() {
        // [START android_compose_navigation3_deeplinks_extras_dsl]
        val extras: Map<String, Any> = requestExtras {
            put(MimeTypeExtrasKey, "application/json")
            put(DeepLinkRequest.ActionExtrasKey, Intent.ACTION_VIEW)
        }

        val mimeTypeExtraMap: Map<String, Any> = DeepLinkRequest.mimeTypeExtra("application/json")
        // [END android_compose_navigation3_deeplinks_extras_dsl]

        // [START android_compose_navigation3_deeplinks_custom_extras]
        // object CampaignIdExtrasKey : RequestExtrasKey<String> // Defined outside

        val customExtras = requestExtras {
            put(CampaignIdExtrasKey, "123")
        }
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
        // @Serializable data class UserProfileKey(val id: String): NavKey // Defined outside

        val userProfilePatternUri = DeepLinkUri("www.example.com/users/{id}")
        val userProfileMatcher = UriDeepLinkMatcher(userProfilePatternUri, serializer<UserProfileKey>())

        val request = DeepLinkRequest("https://www.example.com/users/123")
        val matchResult = userProfileMatcher.match(request)
        val key = matchResult?.key // UserProfileKey(id = "123")
        // [END android_compose_navigation3_deeplinks_uri_matcher]
    }

    fun uriMatcherQuery() {
        // [START android_compose_navigation3_deeplinks_uri_matcher_query]
        // Defined outside: SortOrder, SearchFilters, SearchKey

        val pattern = DeepLinkUri("www.example.com/search?q={query}&category={category}&sortBy={sortBy}")
        val matcher = UriDeepLinkMatcher(pattern, serializer<SearchKey>())
        val request = DeepLinkRequest("https://www.example.com/search?q=kotlin&category=books&sortBy=DATE")
        val matchResult = matcher.match(request)
        val key = matchResult?.key // SearchKey(query = "kotlin", filters = SearchFilters(category = "books", sortBy = SortOrder.DATE))
        // [END android_compose_navigation3_deeplinks_uri_matcher_query]
    }

    fun uriMatcherValidation() {
        // [START android_compose_navigation3_deeplinks_uri_matcher_validation]
        // Defined outside: UserDetailsTab, UserDetailsKey

        val matcher = UriDeepLinkMatcher(
            DeepLinkUri("example.com/user/{id}?tab={initialTab}"),
            serializer<UserDetailsKey>()
        )
        // [END android_compose_navigation3_deeplinks_uri_matcher_validation]
    }

    // [START android_compose_navigation3_deeplinks_custom_matcher]
    // @Serializable data class DialerKey(val phoneNumber: String?) : NavKey // Defined outside

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
           
            // The more specific (longer) pattern wins (e.g., tel:1800* vs tel:*)
            val lengthDiff = this.patternLength - other.patternLength
            if (lengthDiff != 0) {
                return lengthDiff
            }
           
            return 0
        }
    }
    // [END android_compose_navigation3_deeplinks_custom_matcher_result]

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
    DeepLinkSnippets.TelDeepLinkMatcher()
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
    fun provideUserMatcher(): DeepLinkMatcher<*, *> {
        return UriDeepLinkMatcher(
            DeepLinkUri("www.example.com/users/{id}"),
            serializer<UserProfileKey>()
        )
    }
}
// [END android_compose_navigation3_deeplinks_modularization_feature]

// [START android_compose_navigation3_deeplinks_modularization_app]
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
