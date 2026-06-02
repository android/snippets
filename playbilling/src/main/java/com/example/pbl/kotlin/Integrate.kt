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

import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.AcknowledgePurchaseResponseListener
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClient.BillingResponseCode
import com.android.billingclient.api.BillingClient.ProductType
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingConfig
import com.android.billingclient.api.BillingConfigResponseListener
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.ConsumeParams
import com.android.billingclient.api.GetBillingConfigParams
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryProductDetailsParams.Product
import com.android.billingclient.api.acknowledgePurchase
import com.android.billingclient.api.consumePurchase
import com.android.billingclient.api.queryProductDetails
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

private class Integrate(private val context: Context) : PurchasesUpdatedListener {

    // [START android_playbilling_initialize_billing_client]
    private val purchasesUpdatedListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            // To be implemented in a later section.
        }

    private var billingClient = BillingClient.newBuilder(context)
        .setListener(purchasesUpdatedListener)
        // Configure other settings.
        .build()
    // [END android_playbilling_initialize_billing_client]

    private fun connectToPlay() {
        // [START android_playbilling_connect_to_play]
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                }
            }
            override fun onBillingServiceDisconnected() {
                // If automatic service reconnection is enabled, this can be left empty (no-op)
                // because the library handles retries. You can still use this for non-retry
                // tasks like logging or updating the UI to reflect a disconnected state.
                // Otherwise, try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
        // [END android_playbilling_connect_to_play]
    }

    private fun enableAutoReconnection(listener: PurchasesUpdatedListener) {
        // [START android_playbilling_enable_auto_reconnection]
        val billingClient = BillingClient.newBuilder(context)
            .setListener(listener)
            .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
            .enableAutoServiceReconnection() // Add this line to enable reconnection
            .build()
        // [END android_playbilling_enable_auto_reconnection]
    }

    private fun queryProductDetails() {
        // [START android_playbilling_query_product_details]
        val queryProductDetailsParams =
            QueryProductDetailsParams.newBuilder()
                .setProductList(
                    listOf(
                        Product.newBuilder()
                            .setProductId("product_id_example")
                            .setProductType(ProductType.SUBS)
                            .build()
                    )
                )
                .build()

        billingClient.queryProductDetailsAsync(queryProductDetailsParams) {
                billingResult,
                queryProductDetailsResult ->
            if (billingResult.responseCode == BillingResponseCode.OK) {
                for (productDetails in queryProductDetailsResult.productDetailsList) {
                    // Process successfully retrieved product details here.
                }

                for (unfetchedProduct in queryProductDetailsResult.unfetchedProductList) {
                    // Handle any unfetched products as appropriate.
                }
            }
        }
        // [END android_playbilling_query_product_details]
    }

    // [START android_playbilling_query_kotlin_extensions]
    suspend fun processPurchases() {
        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId("product_id_example")
                .setProductType(BillingClient.ProductType.SUBS)
                .build()
        )
        val params = QueryProductDetailsParams.newBuilder()
        params.setProductList(productList)

        // leverage queryProductDetails Kotlin extension function
        val productDetailsResult = withContext(Dispatchers.IO) {
            billingClient.queryProductDetails(params.build())
        }

        // Process the result.
    }
    // [END android_playbilling_query_kotlin_extensions]

    // [START android_playbilling_on_purchases_updated]
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        if (billingResult.responseCode == BillingResponseCode.OK && purchases != null) {
            for (purchase in purchases) {
                // Process the purchase as described in the next section.
            }
        } else if (billingResult.responseCode == BillingResponseCode.USER_CANCELED) {
            // Handle an error caused by a user canceling the purchase flow.
        } else {
            // Handle any other error codes.
        }
    }
    // [END android_playbilling_on_purchases_updated]

    private suspend fun consumePurchase(purchase: Purchase, client: BillingClient) {
        // [START android_playbilling_consume_purchase]
        val consumeParams =
            ConsumeParams.newBuilder()
                .setPurchaseToken(purchase.getPurchaseToken())
                .build()
        val consumeResult = withContext(Dispatchers.IO) {
            client.consumePurchase(consumeParams)
        }
        // [END android_playbilling_consume_purchase]
    }

    private suspend fun acknowledgePurchase(purchase: Purchase) {
        // [START android_playbilling_acknowledge_purchase]
        val client: BillingClient = billingClient
        val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
            .setPurchaseToken(purchase.purchaseToken)
        val ackPurchaseResult = withContext(Dispatchers.IO) {
            client.acknowledgePurchase(acknowledgePurchaseParams.build())
        }
        // [END android_playbilling_acknowledge_purchase]
    }

    private fun getBillingConfig() {
        // [START android_playbilling_get_billing_config]
        // Use the default GetBillingConfigParams.
        val getBillingConfigParams = GetBillingConfigParams.newBuilder().build()
        billingClient.getBillingConfigAsync(
            getBillingConfigParams,
            object : BillingConfigResponseListener {
                override fun onBillingConfigResponse(
                    billingResult: BillingResult,
                    billingConfig: BillingConfig?
                ) {
                    if (billingResult.responseCode == BillingResponseCode.OK &&
                        billingConfig != null
                    ) {
                        val countryCode = billingConfig.countryCode
                        // ...
                    } else {
                        // Handle errors
                    }
                }
            }
        )
        // [END android_playbilling_get_billing_config]
    }
}
