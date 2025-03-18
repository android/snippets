package com.example.snippets;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.startup.Initializer;
import androidx.window.WindowSdkExtensions;
import androidx.window.core.ExperimentalWindowApi;
import androidx.window.embedding.ActivityEmbeddingController;
import androidx.window.embedding.ActivityFilter;
import androidx.window.embedding.ActivityRule;
import androidx.window.embedding.DividerAttributes;
import androidx.window.embedding.EmbeddingAspectRatio;
import androidx.window.embedding.RuleController;
import androidx.window.embedding.SplitAttributes;
import androidx.window.embedding.SplitAttributes.SplitType;
import androidx.window.embedding.SplitController;
import androidx.window.embedding.SplitPairFilter;
import androidx.window.embedding.SplitPairRule;
import androidx.window.embedding.SplitPinRule;
import androidx.window.embedding.SplitPlaceholderRule;
import androidx.window.embedding.SplitRule;
import androidx.window.java.embedding.SplitControllerCallbackAdapter;
import androidx.window.layout.FoldingFeature;
import androidx.window.layout.WindowMetrics;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ActivityEmbeddingJavaSnippets {

    static class SnippetsActivity extends Activity {

        private Context context;

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // [START activity_embedding_split_attributes_calculator_java]
            if (WindowSdkExtensions.getInstance().getExtensionVersion() >= 2) {
                SplitController.getInstance(this).setSplitAttributesCalculator(params -> {
                    Configuration parentConfiguration = params.getParentConfiguration();
                    SplitAttributes.Builder builder = new SplitAttributes.Builder();
                    if (parentConfiguration.screenWidthDp >= 840) {
                        // Side-by-side dual-pane layout for wide displays.
                        return builder
                            .setLayoutDirection(SplitAttributes.LayoutDirection.LOCALE)
                            .build();
                    } else if (parentConfiguration.screenHeightDp >= 600) {
                        // Horizontal split for tall displays.
                        return builder
                            .setLayoutDirection(SplitAttributes.LayoutDirection.BOTTOM_TO_TOP)
                            .build();
                    } else {
                        // Fallback to expand the secondary container.
                        return builder
                            .setSplitType(SplitType.SPLIT_TYPE_EXPAND)
                            .build();
                    }
                });
            }
            // [END activity_embedding_split_attributes_calculator_java]

            // [START activity_embedding_split_attributes_calculator_tabletop_java]
            if (WindowSdkExtensions.getInstance().getExtensionVersion() >= 2) {
                SplitController.getInstance(this).setSplitAttributesCalculator(params -> {
                    String tag = params.getSplitRuleTag();
                    WindowMetrics parentWindowMetrics = params.getParentWindowMetrics();
                    Configuration parentConfiguration = params.getParentConfiguration();
                    List<FoldingFeature> foldingFeatures =
                        params.getParentWindowLayoutInfo().getDisplayFeatures().stream().filter(
                                item -> item instanceof FoldingFeature)
                            .map(item -> (FoldingFeature) item)
                            .collect(Collectors.toList());
                    FoldingFeature feature = foldingFeatures.size() == 1 ? foldingFeatures.get(0) : null;
                    SplitAttributes.Builder builder = new SplitAttributes.Builder();
                    builder.setSplitType(SplitType.SPLIT_TYPE_HINGE);
                    if (feature != null && feature.isSeparating()) {
                        // Horizontal slit for tabletop posture.
                        return builder
                            .setSplitType(SplitType.SPLIT_TYPE_HINGE)
                            .setLayoutDirection(
                                feature.getOrientation() == FoldingFeature.Orientation.HORIZONTAL
                                    ? SplitAttributes.LayoutDirection.BOTTOM_TO_TOP
                                    : SplitAttributes.LayoutDirection.LOCALE)
                            .build();
                    }
                    else if (parentConfiguration.screenWidthDp >= 840) {
                        // Side-by-side dual-pane layout for wide displays.
                        return builder
                            .setLayoutDirection(SplitAttributes.LayoutDirection.LOCALE)
                            .build();
                    } else {
                        // No split for tall displays.
                        return builder
                            .setSplitType(SplitType.SPLIT_TYPE_EXPAND)
                            .build();
                    }
                });
            }
            // [END activity_embedding_split_attributes_calculator_tabletop_java]

            // [START activity_embedding_splitPairFilter_java]
            SplitPairFilter splitPairFilter = new SplitPairFilter(
               new ComponentName(this, ListActivity.class),
               new ComponentName(this, DetailActivity.class),
               null
            );
            // [END activity_embedding_splitPairFilter_java]

            // [START activity_embedding_filterSet_java]
            Set<SplitPairFilter> filterSet = new HashSet<>();
            filterSet.add(splitPairFilter);
            // [END activity_embedding_filterSet_java]

            // [START activity_embedding_splitAttributes_java]
            SplitAttributes splitAttributes = new SplitAttributes.Builder()
                  .setSplitType(SplitAttributes.SplitType.ratio(0.33f))
                  .setLayoutDirection(SplitAttributes.LayoutDirection.LEFT_TO_RIGHT)
                  .build();
            // [END activity_embedding_splitAttributes_java]

            // [START activity_embedding_splitPairRule_java]
            SplitPairRule splitPairRule = new SplitPairRule.Builder(filterSet)
                .setDefaultSplitAttributes(splitAttributes)
                .setMinWidthDp(840)
                .setMinSmallestWidthDp(600)
                .setMaxAspectRatioInPortrait(EmbeddingAspectRatio.ratio(1.5f))
                .setFinishPrimaryWithSecondary(SplitRule.FinishBehavior.NEVER)
                .setFinishSecondaryWithPrimary(SplitRule.FinishBehavior.ALWAYS)
                .setClearTop(false)
                .build();
            // [END activity_embedding_splitPairRule_java]

            // [START activity_embedding_ruleController_java]
            RuleController ruleController = RuleController.getInstance(this);
            ruleController.addRule(splitPairRule);
            // [END activity_embedding_ruleController_java]

            // [START activity_embedding_placeholderActivityFilter_java]
            ActivityFilter placeholderActivityFilter = new ActivityFilter(
                new ComponentName(this, ListActivity.class),
                null
            );
            // [END activity_embedding_placeholderActivityFilter_java]

            // [START activity_embedding_placeholderActivityFilterSet_java]
            Set<ActivityFilter> placeholderActivityFilterSet = new HashSet<>();
            placeholderActivityFilterSet.add(placeholderActivityFilter);
            // [END activity_embedding_placeholderActivityFilterSet_java]

            // [START activity_embedding_splitPlaceholderRule_java]
            SplitPlaceholderRule splitPlaceholderRule = new SplitPlaceholderRule.Builder(
                  placeholderActivityFilterSet,
                  new Intent(this, PlaceholderActivity.class)
                ).setDefaultSplitAttributes(splitAttributes)
                 .setMinWidthDp(840)
                 .setMinSmallestWidthDp(600)
                 .setMaxAspectRatioInPortrait(EmbeddingAspectRatio.ratio(1.5f))
                 .setFinishPrimaryWithPlaceholder(SplitRule.FinishBehavior.ALWAYS)
                 .setSticky(false)
                 .build();
            // [END activity_embedding_splitPlaceholderRule_java]

            // [START activity_embedding_addRuleSplitPlaceholderRule_java]
            ruleController.addRule(splitPlaceholderRule);
            // [END activity_embedding_addRuleSplitPlaceholderRule_java]

            // [START activity_embedding_expandedActivityFilter_java]
            ActivityFilter expandedActivityFilter = new ActivityFilter(
                new ComponentName(this, ExpandedActivity.class),
               null
            );
            // [END activity_embedding_expandedActivityFilter_java]

            // [START activity_embedding_expandedActivityFilterSet_java]
            Set<ActivityFilter> expandedActivityFilterSet = new HashSet<>();
            expandedActivityFilterSet.add(expandedActivityFilter);
            // [END activity_embedding_expandedActivityFilterSet_java]

            // [START activity_embedding_activityRule_java]
            ActivityRule activityRule = new ActivityRule.Builder(
                expandedActivityFilterSet
            ).setAlwaysExpand(true)
             .build();
            // [END activity_embedding_activityRule_java]

            // [START activity_embedding_addRuleActivityRule_java]
            ruleController.addRule(activityRule);
            // [END activity_embedding_addRuleActivityRule_java]

            // [START activity_embedding_splitAttributesBuilder_java]
            SplitAttributes.Builder _splitAttributesBuilder = new SplitAttributes.Builder()
                .setSplitType(SplitAttributes.SplitType.ratio(0.33f))
                .setLayoutDirection(SplitAttributes.LayoutDirection.LEFT_TO_RIGHT);

            if (WindowSdkExtensions.getInstance().getExtensionVersion() >= 6) {
                _splitAttributesBuilder.setDividerAttributes(
                  new DividerAttributes.DraggableDividerAttributes.Builder()
                    .setColor(ContextCompat.getColor(this, R.color.divider_color))
                    .setWidthDp(4)
                    .setDragRange(DividerAttributes.DragRange.DRAG_RANGE_SYSTEM_DEFAULT)
                    .build()
                );
            }
            SplitAttributes _splitAttributes = _splitAttributesBuilder.build();
            // [END activity_embedding_splitAttributesBuilder_java]

        }


        // [START activity_embedding_isActivityEmbedded_java]
        boolean isActivityEmbedded(Activity activity) {
            return ActivityEmbeddingController.getInstance(context).isActivityEmbedded(activity);
        }
        // [END activity_embedding_isActivityEmbedded_java]

    }


    /** @noinspection InnerClassMayBeStatic*/
    // [START activity_embedding_DetailActivity_class_java]
    public class DetailActivity  extends AppCompatActivity {
        void onOpenSubdetail() {
            startActivity(new Intent(this, SubdetailActivity.class));
        }
    }
    // [END activity_embedding_DetailActivity_class_java]


    /** @noinspection InnerClassMayBeStatic*/
    // [START activity_embedding_SplitInitializer_class_java]
    public class SplitInitializer implements Initializer<RuleController> {

        @NonNull
        @Override
        public RuleController create(@NonNull Context context) {
            RuleController ruleController = RuleController.getInstance(context);
            ruleController.setRules(
                RuleController.parseRules(context, R.xml.main_split_config)
            );
             return ruleController;
         }

         @NonNull
         @Override
         public List<Class<? extends Initializer<?>>> dependencies() {
             return Collections.emptyList();
         }
    }
    // [END activity_embedding_SplitInitializer_class_java]


    /**
     * Function used by snippet.
     */
    private Class<?> classForItem(int item) { return Class.class; }

    /** @noinspection InnerClassMayBeStatic*/
    // [START activity_embedding_MenuActivity_class_java]
    public class MenuActivity extends AppCompatActivity{
        void onMenuItemSelected(int selectedMenuItem) {
            startActivity(new Intent(this, classForItem(selectedMenuItem)));
        }
    }
    // [END activity_embedding_MenuActivity_class_java]


    /** @noinspection InnerClassMayBeStatic*/
    // [START activity_embedding_B_class_java]
    public class B extends AppCompatActivity{
        void onOpenC() {
            startActivity(new Intent(this, C.class));
        }
    }
    // [END activity_embedding_B_class_java]


    static class SnippetActivity2 extends Activity {

        private Set<SplitPairFilter> filterSet = new HashSet<>();

        // [START activity_embedding_onCreate_RuleController_java]
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            RuleController.getInstance(this)
                .addRule(new SplitPairRule.Builder(filterSet).build());
            startActivity(new Intent(this, DetailActivity.class));
        }
        // [END activity_embedding_onCreate_RuleController_java]

    }


    static class SnippetActivity3 extends AppCompatActivity {

        @OptIn(markerClass = ExperimentalWindowApi.class)
        // [START activity_embedding_onCreate_SplitControllerCallbackAdapter_java]
        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            new SplitControllerCallbackAdapter(SplitController.getInstance(this))
                .addSplitListener(
                    this,
                    Runnable::run,
                    splitInfoList -> {
                        View layout = getLayoutInflater().inflate(R.layout.activity_main, null);
                        layout.findViewById(R.id.infoButton).setVisibility(
                            splitInfoList.isEmpty() ? View.VISIBLE : View.GONE);
                    });
        }
        // [END activity_embedding_onCreate_SplitControllerCallbackAdapter_java]

    }

    static class SnippetActivity4 extends Activity {

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);

            // [START activity_embedding_pinButton_java]
            Button pinButton = findViewById(R.id.pinButton);
            pinButton.setOnClickListener( (view) -> {
                SplitAttributes splitAttributes = new SplitAttributes.Builder()
                    .setSplitType(SplitAttributes.SplitType.ratio(0.66f))
                    .setLayoutDirection(SplitAttributes.LayoutDirection.LEFT_TO_RIGHT)
                    .build();

                SplitPinRule pinSplitRule = new SplitPinRule.Builder()
                    .setSticky(true)
                    .setDefaultSplitAttributes(splitAttributes)
                    .build();

                SplitController.getInstance(
                    getApplicationContext()).pinTopActivityStack(getTaskId(),
                    pinSplitRule);
            });
            // [END activity_embedding_pinButton_java]

            // [START activity_embedding_getSplitSupportStatus_java]
            if (SplitController.getInstance(this).getSplitSupportStatus() ==
                 SplitController.SplitSupportStatus.SPLIT_AVAILABLE) {
                 // Device supports split activity features.
            }
            // [END activity_embedding_getSplitSupportStatus_java]

        }
    }


    /**
     * Classes used by snippets.
     */
    static class ListActivity {}
    static class SubdetailActivity {}
    static class PlaceholderActivity {}
    static class ExpandedActivity {}
    static class C {}

}
