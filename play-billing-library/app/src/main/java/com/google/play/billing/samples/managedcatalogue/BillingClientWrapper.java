/*
 * Copyright 2023 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.play.billing.samples.managedcatalogue;


import android.app.Activity;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.android.billingclient.api.AcknowledgePurchaseParams;
import com.android.billingclient.api.AcknowledgePurchaseResponseListener;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingClient.ProductType;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingConfig;
import com.android.billingclient.api.BillingConfigResponseListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingFlowParams.ProductDetailsParams;
import com.android.billingclient.api.BillingFlowParams.SubscriptionUpdateParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.GetBillingConfigParams;
import com.android.billingclient.api.InAppMessageParams;
import com.android.billingclient.api.InAppMessageResult;
import com.android.billingclient.api.InAppMessageResponseListener;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryProductDetailsParams.Product;
import com.google.common.collect.ImmutableList;

import java.util.ArrayList;
import java.util.List;

/**
 * A wrapper for the Google Play Billing Library that handles all the billing logic.
 */
class BillingClientWrapper {

    private final Context context;
    private final Activity activity;

    public BillingClientWrapper(Context context, Activity activity) {
        this.context = context;
        this.activity = activity;
    }

    List<ProductDetails> productDetailsList;
    ProductDetails productDetails;
    public void initializeBillingClient() {
        // [START android_playbilling_initialize_java]
        PurchasesUpdatedListener purchasesUpdatedListener =
                (billingResult, purchases) -> {
                    // To be implemented in a later section.
                };

        BillingClient billingClient =
                BillingClient.newBuilder(context)
                        .setListener(purchasesUpdatedListener)
                        // Configure other settings.
                        .build();
        // [END android_playbilling_initialize_java]
    }

    public void startConnection() {
        BillingClient billingClient = BillingClient.newBuilder(context)
                .setListener((billingResult, list) -> {})
                .build();
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
                    }
                });
        // [END android_playbilling_startconnection_java]
    }

    public void enableAutoReconnect() {
        PurchasesUpdatedListener listener = (billingResult, list) -> {};
        // [START android_playbilling_enableautoreconnect_java]
        BillingClient billingClient =
                BillingClient.newBuilder(context)
                        .setListener(listener)
                        .enablePendingPurchases(PendingPurchasesParams.newBuilder()
                                .enableOneTimeProducts()
                                .build())
                        .build();
        // [END android_playbilling_enableautoreconnect_java]
    }

    public void queryProductDetails() {
        BillingClient billingClient = BillingClient.newBuilder(context)
                .setListener((billingResult, list) -> {})
                .build();
        // [START android_playbilling_queryproductdetails_java]
        QueryProductDetailsParams queryProductDetailsParams =
                QueryProductDetailsParams.newBuilder()
                        .setProductList(
                                ImmutableList.of(
                                        Product.newBuilder()
                                                .setProductId("product_id_example")
                                                .setProductType(ProductType.SUBS)
                                                .build()))
                        .build();

        billingClient.queryProductDetailsAsync(
                queryProductDetailsParams,
                (billingResult, productDetailsList) -> {
                    if (billingResult.getResponseCode() == BillingResponseCode.OK && productDetailsList != null) {
                        this.productDetailsList = productDetailsList.getProductDetailsList();
                        // Now that the list is populated, you can use it.
                        // For example, find a specific product.
                        if (!productDetailsList.getProductDetailsList().isEmpty()) {
                            this.productDetails = productDetailsList.getProductDetailsList().get(0);
                            // Any methods that require productDetails should be called from here.
                        }
                    }
                });
        // [END android_playbilling_queryproductdetails_java]
    }

    public void onPurchasesUpdated() {
        // [START android_playbilling_onpurchasesupdated_java]
        new PurchasesUpdatedListener() {
            @Override
            public void onPurchasesUpdated(
                    @NonNull BillingResult billingResult, @Nullable List<Purchase> purchases) {
                if (billingResult.getResponseCode() == BillingResponseCode.OK && purchases != null) {
                    for (Purchase purchase : purchases) {
                        // Process the purchase as described in the next section.
                    }
                } else if (billingResult.getResponseCode() == BillingResponseCode.USER_CANCELED) {
                    // Handle an error caused by a user canceling the purchase flow.
                } else {
                    // Handle any other error codes.
                }
            }
        };
        // [END android_playbilling_onpurchasesupdated_java]
    }

    public void consumeProduct(Purchase purchase) {
        BillingClient billingClient = BillingClient.newBuilder(context)
                .setListener((billingResult, list) -> {})
                .build();
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
        BillingClient client = BillingClient.newBuilder(context)
                .setListener((billingResult, list) -> {})
                .build();
        AcknowledgePurchaseResponseListener acknowledgePurchaseResponseListener =
                (billingResult) -> {};

        AcknowledgePurchaseParams acknowledgePurchaseParams =
                AcknowledgePurchaseParams.newBuilder().setPurchaseToken(purchase.getPurchaseToken()).build();
        client.acknowledgePurchase(acknowledgePurchaseParams, acknowledgePurchaseResponseListener);
        // [END android_playbilling_acknowledge_java]
    }

    public void handlePurchaseRecap() {
        // [START android_playbilling_handlepurchaserecap_java]
        final class HandlePurchase {
            void handlePurchase(Purchase purchase) {
                // Purchase retrieved from BillingClient#queryPurchasesAsync or your
                // onPurchasesUpdated.

                // Step 1: Send the purchase to your secure backend to verify the purchase
                // following
                // https://developer.android.com/google/play/billing/security#verify

                // Step 2: Update your entitlement storage with the purchase. If purchase is
                // in PENDING state then ensure the entitlement is marked as pending and the
                // user does not receive benefits yet. It is recommended that this step is
                // done on your secure backend and can combine in the API call to your
                // backend in step 1.

                // Step 3: Notify the user using appropriate messaging (delaying
                // notification if needed as discussed above).

                // Step 4: Notify Google the purchase was processed using the steps
                // discussed in the processing purchases section.
            }
        }
        // [END android_playbilling_handlepurchaserecap_java]
    }

    public void getBillingConfigAsync() {
        BillingClient billingClient = BillingClient.newBuilder(context)
                .setListener((billingResult, list) -> {})
                .build();
        // [START android_playbilling_getbillingconfig_java]
        // Use the default GetBillingConfigParams.
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
        BillingClient billingClient = BillingClient.newBuilder(context)
                .setListener((billingResult, list) -> {})
                .build();

        int replacementModeForBasePlan =
                SubscriptionUpdateParams.ReplacementMode.CHARGE_FULL_PRICE;
        int replacementModeForAddon =
                SubscriptionUpdateParams.ReplacementMode.CHARGE_FULL_PRICE;
        // ProductDetails obtained from queryProductDetailsAsync().
        ProductDetailsParams productDetails1 =
                ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build();
        ProductDetailsParams productDetails2 =
                ProductDetailsParams.newBuilder()
                        .setProductDetails(productDetails)
                        .build();
        ProductDetailsParams productDetails3 = ProductDetailsParams.newBuilder().build();

        ArrayList<ProductDetailsParams> newProductDetailsList = new ArrayList<>();
        newProductDetailsList.add(productDetails1);
        newProductDetailsList.add(productDetails2);
        newProductDetailsList.add(productDetails3);

        BillingFlowParams billingFlowParams =
                BillingFlowParams.newBuilder()
                        .setSubscriptionUpdateParams(
                                SubscriptionUpdateParams.newBuilder()
                                        .setOldPurchaseToken(purchaseTokenOfExistingSubscription)
                                        .build())
                        .setProductDetailsParamsList(newProductDetailsList)
                        .build();

        billingClient.launchBillingFlow(activity, billingFlowParams);
        // [END android_playbilling_subscription_replace_java]
    }

    public void changeSubscriptionPlanDeprecated() {
        if (productDetails == null || productDetails.getSubscriptionOfferDetails() == null || productDetails.getSubscriptionOfferDetails().isEmpty()) {
            // Can't launch the flow if product details or offers aren't loaded yet.
            return;
        }

        BillingClient billingClient = BillingClient.newBuilder(context)
                .setListener((billingResult, list) -> {})
                .build();
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
        BillingClient billingClient = BillingClient.newBuilder(context)
                .setListener((billingResult, list) -> {})
                .build();
        // [START android_playbilling_inappmessaging_java]
        InAppMessageParams inAppMessageParams =
                InAppMessageParams.newBuilder()
                        .addInAppMessageCategoryToShow(
                                InAppMessageParams.InAppMessageCategoryId.TRANSACTIONAL)
                        .build();

        billingClient.showInAppMessages(
                activity,
                inAppMessageParams,
                inAppMessageResult -> {
                    if (inAppMessageResult.getResponseCode()
                            == InAppMessageResult.InAppMessageResponseCode.NO_ACTION_NEEDED) {
                        // The flow has finished and there is no action needed from developers.
                    } else if (inAppMessageResult.getResponseCode()
                            == InAppMessageResult.InAppMessageResponseCode.SUBSCRIPTION_STATUS_UPDATED) {
                        // The subscription status changed. For example, a subscription
                        // has been recovered from a suspend state. Developers should
                        // expect the purchase token to be returned with this response
                        // code and use it with the Google Play Developer API.
                    }
                });
        // [END android_playbilling_inappmessaging_java]
    }
}
