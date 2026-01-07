/*
 * Copyright 2023 Google LLC
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

package com.example.pbl;

import android.app.Activity;
import android.content.Context;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingClient.ProductType;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams;
import com.android.billingclient.api.BillingFlowParams.SubscriptionUpdateParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.GetBillingConfigParams;
import com.android.billingclient.api.InAppMessageParams;
import com.android.billingclient.api.InAppMessageResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.google.common.collect.ImmutableList;

import java.util.List;

/**
 * A wrapper for the Google Play Billing Library that handles all the billing logic.
 */
class BillingClientWrapper {

    private final Context context;
    private final Activity activity;
    private final BillingClient billingClient;

    private List<ProductDetails> productDetailsList;
    private ProductDetails productDetails;

    public BillingClientWrapper(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;

        // [START android_playbilling_initialize_java]
        // 1. Initialize the BillingClient.
        PurchasesUpdatedListener purchasesUpdatedListener =
                (billingResult, purchases) -> {
                    // To be implemented in a later section.
                    if (billingResult.getResponseCode() == BillingResponseCode.OK && purchases != null) {
                        for (Purchase purchase : (List<Purchase>) purchases) {
                            // Process the purchase.
                        }
                    } else if (billingResult.getResponseCode() == BillingResponseCode.USER_CANCELED) {
                        // Handle an error caused by a user canceling the purchase flow.
                    } else {
                        // Handle any other error codes.
                    }
                };

        this.billingClient =
                BillingClient.newBuilder(context)
                        .setListener(purchasesUpdatedListener)
                        // [START android_playbilling_enableautoreconnect_java]
                        .enablePendingPurchases(PendingPurchasesParams.newBuilder()
                                .enableOneTimeProducts()
                                .build())
                        // [END android_playbilling_enableautoreconnect_java]
                        .build();
        // [END android_playbilling_initialize_java]

        startConnection();
    }

    public void startConnection() {
        // [START android_playbilling_startconnection_java]
        billingClient.startConnection(
                new BillingClientStateListener() {
                    @Override
                    public void onBillingSetupFinished(BillingResult billingResult) {
                        if (billingResult.getResponseCode() == BillingResponseCode.OK) {
                            // The BillingClient is ready. You can query purchases here.
                            // It's a good practice to query products after the connection is established.
                            queryProductDetails();
                        }
                    }

                    @Override
                    public void onBillingServiceDisconnected() {
                        // Try to restart the connection on the next request to
                        // Google Play by calling the startConnection() method.
                        // This is automatically handled by the library when you call a method that requires a connection.
                    }
                });
        // [END android_playbilling_startconnection_java]
    }

    public void queryProductDetails() {
        // [START android_playbilling_queryproductdetails_java]
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                ImmutableList.of(
                                        QueryProductDetailsParams.Product.newBuilder()
                                                .setProductId("product_id_example")
                                                .setProductType(ProductType.SUBS)
                                                .build()))
                        .build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                (billingResult, fetchedProductDetailsList) -> {
                    if (billingResult.getResponseCode() == BillingResponseCode.OK && fetchedProductDetailsList != null) {
                        this.productDetailsList = fetchedProductDetailsList.getProductDetailsList();
                        // Now that the list is populated, you can use it.
                        // For example, find a specific product.
                        if (!this.productDetailsList.isEmpty()) {
                            this.productDetails = this.productDetailsList.get(0);
                            // Any methods that require productDetails should be called from here.
                        }
                    }
                });
        // [END android_playbilling_queryproductdetails_java]
    }

    public void consumeProduct(Purchase purchase) {
        // [START android_playbilling_consumeproduct_java]
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();

        ConsumeResponseListener listener =
                (billingResult, purchaseToken) -> {
                    if (billingResult.getResponseCode() == BillingResponseCode.OK) {
                        // Handle the success of the consume operation.
                    }
                };

        billingClient.consumeAsync(consumeParams, listener);
        // [END android_playbilling_consumeproduct_java]
    }

    public void acknowledgePurchase(Purchase purchase) {
        // [START android_playbilling_acknowledge_java]
        if (purchase.getPurchaseState() == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged()) {
                AcknowledgePurchaseParams acknowledgePurchaseParams =
                        AcknowledgePurchaseParams.newBuilder()
                                .setPurchaseToken(purchase.getPurchaseToken())
                                .build();
                billingClient.acknowledgePurchase(acknowledgePurchaseParams, (billingResult) -> {
                    // Acknowledgment handled.
                });
            }
        }
        // [END android_playbilling_acknowledge_java]
    }

    public void getBillingConfigAsync() {
        // [START android_playbilling_getbillingconfig_java]
        GetBillingConfigParams getBillingConfigParams = GetBillingConfigParams.newBuilder().build();
        billingClient.getBillingConfigAsync(
                getBillingConfigParams,
                (billingResult, billingConfig) -> {
                    if (billingResult.getResponseCode() == BillingResponseCode.OK && billingConfig != null) {
                        String countryCode = billingConfig.getCountryCode();
                    } else {
                        // TODO: Handle errors
                    }
                });
        // [END android_playbilling_getbillingconfig_java]
    }

    public void changeSubscriptionPlan() {
        if (productDetails == null) {
            // Can't launch the flow if product details aren't loaded yet.
            // You could initiate a query here or show an error message.
            return;
        }
        String purchaseTokenOfExistingSubscription = "purchase_token";
        // [START android_playbilling_subscription_replace_java]
        ProductDetailsParams productDetailsParams =
                ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build();

        BillingFlowParams billingFlowParams =
                BillingFlowParams.newBuilder()
                        .setSubscriptionUpdateParams(
                                SubscriptionUpdateParams.newBuilder()
                                        .setOldPurchaseToken(purchaseTokenOfExistingSubscription)
                                        .build())
                        .setProductDetailsParamsList(ImmutableList.of(productDetailsParams))
                        .build();

        billingClient.launchBillingFlow(activity, billingFlowParams);
        // [END android_playbilling_subscription_replace_java]
    }

    public void changeSubscriptionPlanDeprecated() {
        if (productDetails == null || productDetails.getSubscriptionOfferDetails() == null || productDetails.getSubscriptionOfferDetails().isEmpty()) {
            // Can't launch the flow if product details or offers aren't loaded yet.
            return;
        }

        int selectedOfferIndex = 0;
        // [START android_playbilling_subscription_update_deprecated_java]
        String offerToken =
                productDetails.getSubscriptionOfferDetails().get(selectedOfferIndex).getOfferToken();

        BillingFlowParams billingFlowParams =
                BillingFlowParams.newBuilder()
                        .setProductDetailsParamsList(
                                ImmutableList.of(
                                        ProductDetailsParams.newBuilder()
                                                // fetched via queryProductDetailsAsync
                                                .setProductDetails(productDetails)
                                                // offerToken can be found in
                                                // ProductDetails=>SubscriptionOfferDetails
                                                .setOfferToken(offerToken)
                                                .build()))
                        .setSubscriptionUpdateParams(
                                SubscriptionUpdateParams.newBuilder()
                                        // purchaseToken can be found in Purchase#getPurchaseToken
                                        .setOldPurchaseToken("old_purchase_token")
                                        .setSubscriptionReplacementMode(
                                                SubscriptionUpdateParams.ReplacementMode.CHARGE_FULL_PRICE)
                                        .build())
                        .build();

        BillingResult billingResult = billingClient.launchBillingFlow(activity, billingFlowParams);
        // ...
        // [END android_playbilling_subscription_update_deprecated_java]
    }

    public void inAppMessaging() {
        // [START android_playbilling_inappmessaging_java]
        InAppMessageParams inAppMessageParams =
                InAppMessageParams.newBuilder()
                        .addInAppMessageCategoryToShow(
                                InAppMessageParams.InAppMessageCategoryId.TRANSACTIONAL)
                        .build();
        billingClient.showInAppMessages(
                activity,
                inAppMessageParams,
                (inAppMessageResult) -> {
                    if (inAppMessageResult.getResponseCode()
                            == InAppMessageResult.InAppMessageResponseCode.NO_ACTION_NEEDED) {
                        // an in-app message was already displayed within the last day
                    }
                });
        // [END android_playbilling_inappmessaging_java]
    }

    // Unused methods from the original file have been removed for clarity
    // (e.g., onPurchasesUpdated, handlePurchaseRecap) as their logic is
    // now integrated into the PurchasesUpdatedListener in the constructor.
}
