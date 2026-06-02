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

package com.example.pbl.kotlin.alternative

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.UserChoiceBillingListener

private class AlternativeBillingWithUserChoiceInApp(
    private val context: Context,
    private val activity: Activity,
    private val productDetailsNewPlan: ProductDetails,
    private val offerTokenNewPlan: String,
    private val selectedOfferIndex: Int,
    private val oldToken: String,
    private val user: String
) {

    private fun initBillingClient() {
        // [START android_playbilling_alt_billing_choice_init]
        val purchasesUpdatedListener =
           PurchasesUpdatedListener { billingResult, purchases ->
               // Handle new Google Play purchase.
           }

        val userChoiceBillingListener =
           UserChoiceBillingListener { userChoiceDetails ->
               // Handle alternative billing choice.
           }

        val billingClient = BillingClient.newBuilder(context)
           .setListener(purchasesUpdatedListener)
           .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
           .enableUserChoiceBilling(userChoiceBillingListener)
           .build()
        // [END android_playbilling_alt_billing_choice_init]
    }

    // [START android_playbilling_alt_billing_choice_listener]
    private val userChoiceBillingListener =
        UserChoiceBillingListener { userChoiceDetails ->
            // Get the products being purchased by the user.
            val products = userChoiceDetails.products

            // Send external transaction token to developer backend server
            // this devBackend object is for demonstration purposes,
            // developers can implement this step however best fits their
            // app to backend communication.
            devBackend.sendExternalTransactionStarted(
                userChoiceDetails.externalTransactionToken,
                user
            )

            // Launch alternative billing
            // ...
            // The developer backend handles reporting the transaction
            // to Google Play's backend once the alternative billing
            // purchase is completed.
        }
    // [END android_playbilling_alt_billing_choice_listener]

    private fun subUpdate(billingClient: BillingClient) {
        // [START android_playbilling_alt_billing_choice_sub_update]
        // The external transaction ID from the current
        // alternative billing subscription.
        val externalTransactionId = "your_external_transaction_id"

        val billingFlowParams = BillingFlowParams.newBuilder()
            .setProductDetailsParamsList(
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        // Fetched using queryProductDetailsAsync.
                        .setProductDetails(productDetailsNewPlan)
                        // offerIdToken can be found in
                        // ProductDetails=>SubscriptionOfferDetails.
                        .setOfferToken(offerTokenNewPlan)
                        .build()
                )
            )
            .setSubscriptionUpdateParams(
                BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                    .setOriginalExternalTransactionId(externalTransactionId)
                    .build()
                )
            .build()

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)

        // When the user selects the alternative billing flow,
        // the UserChoiceBillingListener is triggered.
        // [END android_playbilling_alt_billing_choice_sub_update]
    }

    private fun getOfferToken() {
        // [START android_playbilling_alt_billing_choice_get_offer_token]
        val offerTokenNewPlan = productDetailsNewPlan.getSubscriptionOfferDetails()
             ?.getOrNull(selectedOfferIndex)
             ?.offerToken
             ?: ""
        // [END android_playbilling_alt_billing_choice_get_offer_token]
    }

    private fun launchPlayUpdate(billingClient: BillingClient) {
        // [START android_playbilling_alt_billing_choice_launch_play_update]
        val billingFlowParams =
            BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(
                    listOf(
                        BillingFlowParams.ProductDetailsParams.newBuilder()
                            // Fetched using queryProductDetailsAsync
                            .setProductDetails(productDetailsNewPlan)
                            // offerIdToken can be found in
                            // ProductDetails=>SubscriptionOfferDetails.
                            .setOfferToken(offerTokenNewPlan)
                            .build()
                        )
                )
                .setSubscriptionUpdateParams(
                    BillingFlowParams.SubscriptionUpdateParams.newBuilder()
                        // purchaseToken can be found in
                        // Purchase#getPurchaseToken
                        .setOldPurchaseToken(oldToken)
                        .setSubscriptionReplacementMode(BillingFlowParams.SubscriptionUpdateParams.ReplacementMode.CHARGE_FULL_PRICE)
                        .build()
                )
                .build()

        val billingResult = billingClient.launchBillingFlow(activity, billingFlowParams)
        // [END android_playbilling_alt_billing_choice_launch_play_update]
    }

    // Dummy devBackend object
    private object devBackend {
        fun sendExternalTransactionStarted(token: String, user: String) {}
    }
}
