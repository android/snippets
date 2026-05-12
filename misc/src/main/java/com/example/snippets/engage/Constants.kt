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

package com.example.snippets.engage

@SuppressWarnings("unused")
// [START android_engage_constants_implementation]
object Constants {
    // Holds common values like attempt counts, publish types etc.
    const val REPEAT_INTERVAL = 24L
    const val MAX_PUBLISHING_ATTEMPTS = 3
    const val PUBLISH_TYPE_KEY = "PUBLISH_TYPE"
    const val PUBLISH_TYPE_RECOMMENDATIONS = "RECOMMENDATIONS"
    const val PUBLISH_TYPE_FEATURED = "FEATURED"
    // const val PUBLISH_TYPE_CONTINUATION = "CONTINUATION"
    // ...
    const val PUBLISH_TYPE_USER_ACCOUNT_MANAGEMENT = "USER_ACCOUNT_MANAGEMENT"
    // const val PUBLISH_TYPE_FOOD_SHOPPING_CARD = "FOOD_SHOPPING_CARD"
    // const val PUBLISH_TYPE_RESERVATION = "RESERVATION"
}
// [END android_engage_constants_implementation]

const val TAG: String = "EngageSDK"
