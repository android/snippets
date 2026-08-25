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
import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkUri
import androidx.navigation3.runtime.deeplink.StaticKeyDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.UriDeepLinkMatcher
import androidx.navigation3.runtime.deeplink.actionFilter
import androidx.navigation3.runtime.deeplink.withBackStack
import kotlinx.serialization.serializer

object CreateMatchersSnippets {

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
