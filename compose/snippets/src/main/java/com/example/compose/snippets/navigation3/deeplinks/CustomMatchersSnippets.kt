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

import androidx.navigation3.runtime.deeplink.DeepLinkMatcher
import androidx.navigation3.runtime.deeplink.DeepLinkRequest
import androidx.navigation3.runtime.deeplink.WrappedMatchResult

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
