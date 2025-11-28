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
package com.google.play.billing.samples.managedcatalogue;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;
import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.QueryProductDetailsParams.Product;
import com.google.android.material.button.MaterialButton;
import com.google.play.billing.samples.managedcatalogue.billing.BillingServiceClient;
import com.google.play.billing.samples.managedcatalogue.billing.BillingServiceClientListener;
import java.util.List;

/**
 * This is the trending movie activity class. It is launched when the user clicks on the trending movie card.
 */
public class TrendingMovieActivity extends AppCompatActivity implements BillingServiceClientListener {

  private static final String TAG = "TrendingMovieActivity";
    private MaterialButton buyButton, rentButton;
    private TextView movieTitleText, movieDesc;
    private String productId, productName, productDescription;
    private String buyOfferToken, buyFormattedPrice, rentOfferToken, rentFormattedPrice;
    private BillingServiceClient billingServiceClient;
    private ProductDetails currentProductDetails;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
        setContentView(R.layout.activity_trending_movie);

        buyButton = findViewById(R.id.buy_button);
        rentButton = findViewById(R.id.rent_button);
        movieTitleText = findViewById(R.id.movie_title_overlay);
        movieDesc = findViewById(R.id.movie_description_trending);
        productId = getIntent().getStringExtra("productId");
        productName = getIntent().getStringExtra("productName");
        productDescription = getIntent().getStringExtra("productDescription");

        billingServiceClient = new BillingServiceClient(this, this);
        queryProducts();

        movieTitleText.setText(productName);
        movieDesc.setText(productDescription);

        buyButton.setOnClickListener(v -> {
            if (currentProductDetails != null && buyOfferToken != null) {
                billingServiceClient.launchPurchase(this, currentProductDetails, buyOfferToken);
            } else {
                Toast.makeText(this, "Buy option not available.", Toast.LENGTH_SHORT).show();
            }
        });

        rentButton.setOnClickListener(v -> {
            if (currentProductDetails != null && rentOfferToken != null) {
                billingServiceClient.launchPurchase(this, currentProductDetails, rentOfferToken);
            } else {
                Toast.makeText(this, "Rent option not available.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void queryProducts() {
        List<Product> productList = List.of(
                Product.newBuilder()
                        .setProductId(productId)
                        .setProductType(BillingClient.ProductType.INAPP)
                        .build()
        );
        billingServiceClient.startBillingConnection(productList);
    }

    @Override
    public void onProductDetailsResponse(List<ProductDetails> productDetailsList) {
        if (productDetailsList != null && !productDetailsList.isEmpty()) {
            for (ProductDetails productDetails : productDetailsList) {
                if (productId.equals(productDetails.getProductId())) {
                    currentProductDetails = productDetails;

                    List<ProductDetails.OneTimePurchaseOfferDetails> offerDetailsList =
                            productDetails.getOneTimePurchaseOfferDetailsList();

                    if(offerDetailsList == null || offerDetailsList.isEmpty()) {
                        Log.i(TAG, "Offer details are missing");
                    }

                    for (ProductDetails.OneTimePurchaseOfferDetails offerDetails : offerDetailsList) {
                        if (offerDetails.getRentalDetails() != null) {
                            rentFormattedPrice = offerDetails.getFormattedPrice();
                            rentOfferToken = offerDetails.getOfferToken();
                        } else {
                            buyFormattedPrice = offerDetails.getFormattedPrice();
                            buyOfferToken = offerDetails.getOfferToken();
                        }
                    }
                    updateUIButtons();
                    return;
                }
            }
        } else {
            Log.e(TAG, "No product details found for " + productId);
        }
    }

     @Override
    public void onBillingSetupFailed(BillingResult billingResult) {
    runOnUiThread(
        () -> {
          Toast.makeText(
                  this, "Billing Error: " + billingResult.getDebugMessage(), Toast.LENGTH_LONG)
              .show();
        });
    }

    @Override
    public void onBillingError(String errorMsg) {
        runOnUiThread(
                () -> {
                    Toast.makeText(this, "Billing Error: " + errorMsg, Toast.LENGTH_LONG).show();
                });
    }

    private void updateUIButtons() {
    runOnUiThread(
        () -> {
          if (buyFormattedPrice != null) {
            buyButton.setText(getString(R.string.buy_for_button_text, buyFormattedPrice));
          }

          if (rentFormattedPrice != null) {
            rentButton.setText(getString(R.string.rent_for_button_text, rentFormattedPrice));
          }
        });
    }
}
