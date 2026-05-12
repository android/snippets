package com.example.snippets.engage

@SuppressWarnings("unused")
// [START android_engage_constants_implementation]
object Constants {
    //Holds common values like attempt counts, publish types etc.
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
