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

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import com.android.billingclient.api.BillingClient.ProductType;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ProductDetails;
import com.android.billingclient.api.QueryProductDetailsParams.Product;
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.play.billing.samples.managedcatalogue.billing.BillingServiceClient;
import com.google.play.billing.samples.managedcatalogue.billing.BillingServiceClientListener;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

/** This is the main activity class */
public class MainActivity extends AppCompatActivity implements BillingServiceClientListener {

  private static final String TRENDING_MOVIE_PRODUCT_ID = "trending_movie_1";
  private static final String UPCOMING_MOVIE_PRODUCT_ID = "upcoming_movie_1";
  private static final String TAG = "BillingServiceClient";

  private BillingServiceClient billingServiceClient;
  private NestedScrollView landingPage;

  private TextView trendingMovieTitle, trendingMovieDesc, trendingMoviePrice;
  private TextView upcomingMovieTitle, upcomingMovieDesc, upcomingMoviePrice;
  private MaterialCardView trendingMovieCard, upcomingMovieCard;
  private AtomicBoolean isTrendingProductFound = new AtomicBoolean(false);
  private AtomicBoolean isUpcomingProductFound = new AtomicBoolean(false);
  private String trendingProductName, upcomingProductName;
  private String trendingProductDescription, upcomingProductDescription;
  private MaterialButton licenseButton, githubButton, codelabButton;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    landingPage = findViewById(R.id.landing_page);
    trendingMovieCard = findViewById(R.id.trending_movie_card);
    upcomingMovieCard = findViewById(R.id.upcoming_movie_card);

    trendingMovieTitle = findViewById(R.id.trending_movie_title);
    trendingMovieDesc = findViewById(R.id.trending_movie_desc);
    trendingMoviePrice = findViewById(R.id.trending_movie_price);
    // Upcoming movie section
    upcomingMovieTitle = findViewById(R.id.upcoming_movie_title);
    upcomingMovieDesc = findViewById(R.id.upcoming_movie_desc);
    upcomingMoviePrice = findViewById(R.id.upcoming_movie_price);

    billingServiceClient = new BillingServiceClient(this, this);
    trendingMovieCard.setOnClickListener(
        v -> {
          if (isTrendingProductFound.get()) {
            Intent intent = new Intent(MainActivity.this, TrendingMovieActivity.class);
            intent.putExtra("productId", TRENDING_MOVIE_PRODUCT_ID);
            intent.putExtra("productName", trendingProductName);
            intent.putExtra("productDescription", trendingProductDescription);
            startActivity(intent);
          }
        });
    upcomingMovieCard.setOnClickListener(
        v -> {
          if (isUpcomingProductFound.get()) {
            Intent intent = new Intent(MainActivity.this, UpcomingMovieActivity.class);
            intent.putExtra("productId", UPCOMING_MOVIE_PRODUCT_ID);
            intent.putExtra("productName", upcomingProductName);
            intent.putExtra("productDescription", upcomingProductDescription);
            startActivity(intent);
          }
        });

    licenseButton = findViewById(R.id.license_button);
    licenseButton.setOnClickListener(
        v -> {
          startActivity(new Intent(this, OssLicensesMenuActivity.class));
        });
    githubButton = findViewById(R.id.github_button);
    githubButton.setOnClickListener(
        v -> {
          Intent intent = new Intent(Intent.ACTION_VIEW);
          intent.setData(Uri.parse(getString(R.string.github_url)));
          startActivity(intent);
        });
    codelabButton = findViewById(R.id.codelabs_button);
    codelabButton.setOnClickListener(
        v -> {
          Intent intent = new Intent(Intent.ACTION_VIEW);
          intent.setData(Uri.parse(getString(R.string.codelab_url)));
          startActivity(intent);
        });

    queryProducts();
  }

  private void queryProducts() {
    List<Product> productList =
        List.of(
            Product.newBuilder()
                .setProductId(TRENDING_MOVIE_PRODUCT_ID)
                .setProductType(ProductType.INAPP)
                .build(),
            Product.newBuilder()
                .setProductId(UPCOMING_MOVIE_PRODUCT_ID)
                .setProductType(ProductType.INAPP)
                .build());
    billingServiceClient.startBillingConnection(productList);
  }

  @Override
  public void onProductDetailsResponse(List<ProductDetails> productDetailsList) {
    runOnUiThread(
        () -> {
          for (ProductDetails productDetails : productDetailsList) {
            if (TRENDING_MOVIE_PRODUCT_ID.equals(productDetails.getProductId())) {
              isTrendingProductFound.set(true);
              trendingProductName = productDetails.getName();
              trendingProductDescription = productDetails.getDescription();
              trendingProductDescription = trendingProductDescription.replace("\n", "");
              trendingMovieTitle.setText(trendingProductName);
              trendingMovieDesc.setText(R.string.default_movie_desc);

              List<ProductDetails.OneTimePurchaseOfferDetails> offerDetailsList =
                  productDetails.getOneTimePurchaseOfferDetailsList();
              String priceToDisplay = null;
              for (ProductDetails.OneTimePurchaseOfferDetails offerDetails : offerDetailsList) {
                if (offerDetails.getRentalDetails() != null) {
                  priceToDisplay = offerDetails.getFormattedPrice();
                  break;
                } else if (priceToDisplay == null) {
                  priceToDisplay = offerDetails.getFormattedPrice();
                }
              }
              if (priceToDisplay != null) {
                trendingMoviePrice.setText(getString(R.string.default_movie_price, priceToDisplay));
              } else {
                trendingMoviePrice.setText(R.string.price_unavailable);
              }
            }
            if (UPCOMING_MOVIE_PRODUCT_ID.equals(productDetails.getProductId())) {
              isUpcomingProductFound.set(true);
              // Setting these to pass in intents
              upcomingProductName = productDetails.getName();
              upcomingProductDescription = productDetails.getDescription();
              upcomingProductDescription = upcomingProductDescription.replace("\n", "");
              upcomingMovieTitle.setText(upcomingProductName);

              List<ProductDetails.OneTimePurchaseOfferDetails> offerDetailsList =
                  productDetails.getOneTimePurchaseOfferDetailsList();
              String priceToDisplay = null;
              for (ProductDetails.OneTimePurchaseOfferDetails offerDetails : offerDetailsList) {
                if (offerDetails.getPreorderDetails() != null) {
                  long releaseTimeMillis =
                      offerDetails.getPreorderDetails().getPreorderReleaseTimeMillis();
                  SimpleDateFormat sdf =
                      new SimpleDateFormat("MMM d, yyyy", Locale.getDefault());
                  String formattedDate = sdf.format(new Date(releaseTimeMillis));
                  upcomingMovieDesc.setText(getString(R.string.release_date_text, formattedDate));
                  priceToDisplay = offerDetails.getFormattedPrice();
                } else if (priceToDisplay == null) {
                  upcomingMovieDesc.setText(R.string.coming_soon_text);
                  priceToDisplay = offerDetails.getFormattedPrice();
                }
              }
              if (priceToDisplay != null) {
                upcomingMoviePrice.setText(
                    getString(R.string.preorder_at_button_text, priceToDisplay));
              } else {
                upcomingMoviePrice.setText(R.string.price_unavailable);
              }
            }
          }
          if (!isUpcomingProductFound.get()) {
            upcomingMovieDesc.setText(R.string.movie_unavailable_text);
          }
          if (!isTrendingProductFound.get()) {
            trendingMovieDesc.setText(R.string.movie_unavailable_text);
          }
        });
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

  @Override
  protected void onDestroy() {
    super.onDestroy();
    if (billingServiceClient != null) {
      billingServiceClient.endBillingConnection();
    }
  }
}
