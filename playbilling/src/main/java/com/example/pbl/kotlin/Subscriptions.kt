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

package com.example.pbl.kotlin

import android.app.Activity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams.SubscriptionProductReplacementParams
import com.android.billingclient.api.BillingFlowParams.SubscriptionUpdateParams
import com.android.billingclient.api.InAppMessageParams
import com.android.billingclient.api.InAppMessageParams.InAppMessageCategoryId
import com.android.billingclient.api.InAppMessageResponseListener
import com.android.billingclient.api.InAppMessageResult
import com.android.billingclient.api.InAppMessageResult.InAppMessageResponseCode
import com.android.billingclient.api.ProductDetails

private class Subscriptions(
    private val billingClient: BillingClient,
    private val activity: Activity,
    private val productDetails: ProductDetails,
    private val productDetails1: ProductDetails,
    private val productDetails2: ProductDetails,
    private val productDetails3: ProductDetails,
    private val selectedOfferIndex: Int
) {

    private fun productReplacement() {
        // [START android_playbilling_subscriptions_product_replacement]
        val billingClient: BillingClient = this.billingClient
        val replacementModeForBasePlan: Int = 1
        val replacementModeForAddon: Int = 1

        val purchaseTokenOfExistingSubscription: String = "your_old_purchase_token"

        // ProductDetails instances obtained from queryProductDetailsAsync();

        val productDetailsParams1 =
            ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails1) // Required: Set the ProductDetails object
                .setSubscriptionProductReplacementParams(
                    SubscriptionProductReplacementParams.newBuilder()
                        .setOldProductId("old_product_id_1")
                        .setReplacementMode(replacementModeForBasePlan)
                        .build()
                )
                .build()

        val productDetailsParams2 =
            ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails2) // Required: Set the ProductDetails object
                .setSubscriptionProductReplacementParams(
                    SubscriptionProductReplacementParams.newBuilder()
                        .setOldProductId("old_product_id_2")
                        .setReplacementMode(replacementModeForAddon)
                        .build()
                )
                .build()

        // Example for a third item without replacement params
        val productDetailsParams3 =
            ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails3) // Required: Set the ProductDetails object
                .build()

        val newProductDetailsList = listOf(
            productDetailsParams1,
            productDetailsParams2,
            productDetailsParams3
        )

        val billingFlowParams =
            BillingFlowParams.newBuilder()
                .setSubscriptionUpdateParams(
                    SubscriptionUpdateParams.newBuilder()
                        .setOldPurchaseToken(purchaseTokenOfExistingSubscription)
                        .build()
                )
                .setProductDetailsParamsList(newProductDetailsList)
                .build()

        // To launch the billing flow:
        // billingClient.launchBillingFlow(activity, billingFlowParams)
        // [END android_playbilling_subscriptions_product_replacement]
    }

    private fun showInAppMessages() {
        // [START android_playbilling_subscriptions_show_in_app_messages]
        val inAppMessageParams = InAppMessageParams.newBuilder()
            .addInAppMessageCategoryToShow(InAppMessageCategoryId.TRANSACTIONAL)
            .build()

        // Note: To display the in-app message, PBL requires an activity instance that
        // can provide a valid window token. This token is necessary for the Play Store
        // to display the message overlay correctly on top of the application's window.
        // The passed Activity must be in a state where its window is created and
        // attached to the WindowManager.
        billingClient.showInAppMessages(
            activity,
            inAppMessageParams,
            object : InAppMessageResponseListener {
                override fun onInAppMessageResponse(inAppMessageResult: InAppMessageResult) {
                    if (inAppMessageResult.responseCode == InAppMessageResponseCode.NO_ACTION_NEEDED) {
                        // The flow has finished and there is no action needed from developers.
                    } else if (inAppMessageResult.responseCode
                        == InAppMessageResponseCode.SUBSCRIPTION_STATUS_UPDATED
                    ) {
                        // The subscription status changed. For example, a subscription
                        // is recovered from a suspended state, or a user confirms a
                        // price increase. Developers should expect the purchase
                        // token to be returned with this response code and use
                        // the purchase token with the Google Play Developer API.
                    }
                }
            }
        )
        // [END android_playbilling_subscriptions_show_in_app_messages]
    }
}
