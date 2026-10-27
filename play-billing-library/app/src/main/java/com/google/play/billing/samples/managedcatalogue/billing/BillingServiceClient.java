/* Copyright 2022 Google LLC
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
package com.google.play.billing.samples.managedcatalogue.billing;

import android.app.Activity;
import android.util.Log;
import androidx.annotation.VisibleForTesting;
import androidx.appcompat.app.AppCompatActivity;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClient.BillingResponseCode;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.PendingPurchasesParams;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.ProductDetailsResponseListener;
import com.android.billingclient.api.QueryProductDetailsParams;
import com.android.billingclient.api.QueryProductDetailsParams.Product;
import com.android.billingclient.api.QueryProductDetailsResult;
import com.google.common.collect.ImmutableList;
import java.util.List;

/**
 * Manages interactions with the Google Play Billing Library for handling pre-orders.
 *
 * <p>This class encapsulates the setup of the {@link BillingClient}, manages the connection
 * lifecycle (including retries), queries product details, and notifies a listener.
 */
public class BillingServiceClient {

  private static final String TAG = "BillingServiceClient";
  private BillingClient billingClient;
  private final AppCompatActivity activity;
  private final BillingServiceClientListener listener;

  public BillingServiceClient(AppCompatActivity activity, BillingServiceClientListener listener) {
    this.activity = activity;
    this.listener = listener;
    billingClient = createBillingClient();
  }

  // Constructor for testing
  @VisibleForTesting
  BillingServiceClient(
      AppCompatActivity activity,
      BillingServiceClientListener listener,
      BillingClient billingClient) {
    this.activity = activity;
    this.listener = listener;
    this.billingClient = billingClient;
  }

  /**
   * Starts the billing connection with Google Play. This method should be called exactly once
   * before any other methods in this class.
   *
   * @param productList The list of products to query for after the connection is established.
   */
  public void startBillingConnection(List<Product> productList) {
    billingClient.startConnection(
        new BillingClientStateListener() {
          @Override
          public void onBillingSetupFinished(BillingResult billingResult) {
            if (billingResult.getResponseCode() == BillingResponseCode.OK) {
              Log.d(TAG, "Billing Client Connection Successful");
              queryProductDetails(productList);
            } else {
              Log.e(TAG, "Billing Client Connection Failed: " + billingResult.getDebugMessage());
              listener.onBillingSetupFailed(billingResult); // Propagate the error to the listener to show a message to the user.
            }
          }

          @Override
          public void onBillingServiceDisconnected() {
            Log.e(TAG, "Billing Client Connection Lost");
            listener.onBillingError("Billing Connection Lost");
          }
        });
  }

  /**
   * Launches the billing flow for the product with the given offer token.
   *
   * @param activity The activity instance from which the billing flow will be launched.
   * @param productDetails The product details of the product to purchase.
   * @param offerToken The offer token of the product to purchase.
   * @return The result of the billing flow.
   */
  public void launchPurchase(Activity activity, ProductDetails productDetails, String offerToken) {
    ImmutableList<BillingFlowParams.ProductDetailsParams> productDetailsParamsList =
        ImmutableList.of(
            BillingFlowParams.ProductDetailsParams.newBuilder()
                .setProductDetails(productDetails)
                .setOfferToken(offerToken)
                .build());
    BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
        .setProductDetailsParamsList(productDetailsParamsList)
        .build();
    billingClient.launchBillingFlow(activity, billingFlowParams);
}

  /**
   * Ends the billing connection with Google Play. This method should be called when the app is
   * closed.
   */
  public void endBillingConnection() {
    if (billingClient != null && billingClient.isReady()) {
      billingClient.endConnection();
      billingClient = null;
    }
  }

  private BillingClient createBillingClient() {
    return BillingClient.newBuilder(activity)
        .enablePendingPurchases(PendingPurchasesParams.newBuilder().enableOneTimeProducts().build())
        // For one-time products, add a listener to acknowledge the purchases. This will notify
        // Google the purchase was processed.
        // For client-only apps, use billingClient.acknowledgePurchase().
        // If you have a secure backend, you must acknowledge purchases on your server using the
        // server-side API.
        // See https://developer.android.com/google/play/billing/security#acknowledge
        .setListener((billingResult, purchases) -> {})
        .enableAutoServiceReconnection()
        .build();
  }

  private void queryProductDetails(List<Product> productList) {
    QueryProductDetailsParams queryProductDetailsParams =
        QueryProductDetailsParams.newBuilder().setProductList(productList).build();

    billingClient.queryProductDetailsAsync(
        queryProductDetailsParams,
        new ProductDetailsResponseListener() {
          @Override
          public void onProductDetailsResponse(
              BillingResult billingResult, QueryProductDetailsResult productDetailsResponse) {
            if (billingResult.getResponseCode() == BillingResponseCode.OK) {
              List<ProductDetails> productDetailsList =
                  productDetailsResponse.getProductDetailsList();
                listener.onProductDetailsResponse(productDetailsList);
            } else {
              Log.e(TAG, "QueryProductDetailsAsync Failed: " + billingResult.getDebugMessage());
              listener.onBillingError("Query Products Failed: " + billingResult.getResponseCode());
            }
          }
        });
  }
}
