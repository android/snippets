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
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.IntentExtrasKey
import androidx.navigation3.runtime.deeplink.RequestExtras
import androidx.navigation3.runtime.deeplink.RequestExtrasKey
import androidx.navigation3.runtime.deeplink.StaticKeyDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.actionExtra
import androidx.navigation3.runtime.deeplink.actionFilter
import androidx.navigation3.runtime.deeplink.invoke
import androidx.navigation3.runtime.deeplink.requestExtras
import androidx.navigation3.runtime.deeplink.withBackStack
import androidx.savedstate.SavedState
import androidx.savedstate.read
import kotlinx.serialization.Serializable
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
                put(DeepLinkRequest.Companion.MimeTypeExtrasKey, "image/png")
            }
        )
        // [END android_compose_navigation3_deeplinks_request]
    }

    fun requestExtrasDsl() {
        // [START android_compose_navigation3_deeplinks_extras_dsl]
        val extras: RequestExtras = requestExtras {
            put(DeepLinkRequest.Companion.MimeTypeExtrasKey, "application/json")
            put(DeepLinkRequest.ActionExtrasKey, Intent.ACTION_VIEW)
        }

        // Access typed values using the get operator
        val mimeType: String? = extras[DeepLinkRequest.Companion.MimeTypeExtrasKey]
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
        val mimeType = request.extras[DeepLinkRequest.Companion.MimeTypeExtrasKey] // "application/json"
        val intentExtras: SavedState? =
            request.extras[DeepLinkRequest.IntentExtrasKey]
        val userId: String? = intentExtras?.read { getStringOrNull("user_id") } // "123"
        val campaignId: String? = request.extras[CampaignIdExtrasKey] // "spring_promo"
        // [END android_compose_navigation3_deeplinks_request_intent]
    }
}

// [START android_compose_navigation3_deeplinks_match_request]
// 1. Instantiate your DeepLinkMatcher instances.
val homeMatcher = StaticKeyDeepLinkMatcher(HomeKey, listOf(DeepLinkMatcher.actionFilter(Intent.ACTION_VIEW)))

val userProfileMatcher = UriDeepLinkMatcher(
    DeepLinkUri("www.example.com/users/{id}"),
    serializer<UserProfileKey>()
).withBackStack { matchResult ->
    listOf(HomeKey, matchResult.key)
}

val telMatcher = TelDeepLinkMatcher()

// 2. Collate all of your DeepLinkMatcher instances.
//    Note: Collating matchers with different generic types requires wildcards,
//    erasing the specific generic types.
val deepLinkMatchers: List<DeepLinkMatcher<*, *>> = listOf(
    homeMatcher,
    userProfileMatcher,
    telMatcher
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ...

        // 3. Create a DeepLinkRequest from the incoming Intent
        val request = DeepLinkRequest(intent = intent)

        // 4. Match the request against all matchers and find the best match.
        //    Because DeepLinkMatcher.MatchResult implements Comparable, you can
        //    use maxOrNull() to find the best match.
        val matchResult = deepLinkMatchers
            .mapNotNull { it.match(request) } // List<DeepLinkMatcher.MatchResult<*>>
            .maxOrNull() // DeepLinkMatcher.MatchResult<*>?

        // 5. Create the back stack from the match result (or fall back to a default).
        val backStack: List<NavKey> = when (matchResult) {
            // If no match is found, use the default back stack (e.g., HomeKey)
            null -> listOf(HomeKey)
            // If a BackStackMatchResult is found, use the back stack from the result
            is BackStackMatchResult<*, *> -> {
                // Because star-projected matchers erase the key type, cast the back stack to List<NavKey>.
                @Suppress("UNCHECKED_CAST")
                matchResult.backStack as List<NavKey>
            }
            // Otherwise, use the key from the match result to make a single-item back stack
            else -> listOf(matchResult.key as NavKey)
        }
    }
}
// [END android_compose_navigation3_deeplinks_match_request]
