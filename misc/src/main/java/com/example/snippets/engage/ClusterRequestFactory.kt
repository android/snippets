package com.example.snippets.engage

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import com.example.snippets.R

class AppData {
    // Example data class using in the app like Post, Movie, Music etc.
    val title: String = "title"
    val author: String = "author"
}

class AppDataRepository {
    fun getRecommendations() : List<AppData> {
        return emptyList()
    }
}

@SuppressLint("UseKtx")
@SuppressWarnings("unused")
// [START android_engage_cluster_request_factory_implementation]
class ClusterRequestFactory(context: Context) {

    // [START_EXCLUDE]
    private val signInCardAction = context.resources.getString(R.string.sign_in_card_action_text)
    private val appDataRepository = AppDataRepository()
    // [END_EXCLUDE]

    private val signInCard =
        com.google.android.engage.common.datamodel.SignInCardEntity.Builder()
            .addPosterImage(
                com.google.android.engage.common.datamodel.Image.Builder()
                    .setImageUri(Uri.parse("http://www.x.com/image.png"))
                    .setImageHeightInPixel(500)
                    .setImageWidthInPixel(500)
                    .build()
            )
            .setActionText(signInCardAction)
            .setActionUri(Uri.parse("https://xyz.com/signin"))
            .build()


    fun constructRecommendationClustersRequest(): com.google.android.engage.service.PublishRecommendationClustersRequest {

        val items = appDataRepository.getRecommendations()
        val recommendationCluster = com.google.android.engage.common.datamodel.RecommendationCluster.Builder()
        for (item in items) {
            recommendationCluster.addEntity(ItemToEntityConverter.convert(item))
        }
        return com.google.android.engage.service.PublishRecommendationClustersRequest.Builder()
            .addRecommendationCluster(recommendationCluster.build())
            .build()
    }

    fun constructUserAccountManagementClusterRequest(): com.google.android.engage.service.PublishUserAccountManagementRequest =
        com.google.android.engage.service.PublishUserAccountManagementRequest.Builder()
            .setSignInCardEntity(signInCard)
            .build()
}
// [END android_engage_cluster_request_factory_implementation]

// [START android_engage_platform_specific_playback_uris]
val platformSpecificPlaybackUris = listOf(
    com.google.android.engage.common.datamodel.PlatformSpecificUri.Builder()
        .setPlatformType(com.google.android.engage.common.datamodel.PlatformType.TYPE_ANDROID_TV)
        .setActionUri(Uri.parse("https://www.example.com/tv/play/123"))
        .build(),
    com.google.android.engage.common.datamodel.PlatformSpecificUri.Builder()
        .setPlatformType(com.google.android.engage.common.datamodel.PlatformType.TYPE_ANDROID_MOBILE)
        .setActionUri(Uri.parse("https://www.example.com/mobile/play/123"))
        .build()
)
// [START android_engage_platform_specific_playback_uris]

// [START android_engage_account_profile_example]
val accountProfile = com.google.android.engage.common.datamodel.AccountProfile.Builder()
    .setAccountId("user_123")
    .setProfileId("profile_456")
    .setLocale("en-US")
    .build()
// [END android_engage_account_profile_example]