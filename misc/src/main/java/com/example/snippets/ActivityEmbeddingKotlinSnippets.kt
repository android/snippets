package com.example.snippets

import android.app.Activity
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.startup.Initializer
import androidx.window.WindowSdkExtensions
import androidx.window.core.ExperimentalWindowApi
import androidx.window.embedding.ActivityEmbeddingController
import androidx.window.embedding.ActivityFilter
import androidx.window.embedding.ActivityRule
import androidx.window.embedding.DividerAttributes
import androidx.window.embedding.EmbeddingAspectRatio
import androidx.window.embedding.RuleController
import androidx.window.embedding.SplitAttributes
import androidx.window.embedding.SplitAttributes.SplitType.Companion.SPLIT_TYPE_EXPAND
import androidx.window.embedding.SplitAttributes.SplitType.Companion.SPLIT_TYPE_HINGE
import androidx.window.embedding.SplitController
import androidx.window.embedding.SplitInfo
import androidx.window.embedding.SplitPairFilter
import androidx.window.embedding.SplitPairRule
import androidx.window.embedding.SplitPinRule
import androidx.window.embedding.SplitPlaceholderRule
import androidx.window.embedding.SplitRule
import androidx.window.java.embedding.SplitControllerCallbackAdapter
import androidx.window.layout.FoldingFeature
import java.util.concurrent.Executor
import kotlinx.coroutines.launch

class ActivityEmbeddingKotlinSnippets {

    class SnippetActivity: Activity() {

        private val context = this

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            // [START activity_embedding_split_attributes_calculator_kotlin]
            if (WindowSdkExtensions.getInstance().extensionVersion >= 2) {
                SplitController.getInstance(this).setSplitAttributesCalculator { params ->
                    val parentConfiguration = params.parentConfiguration
                    val builder = SplitAttributes.Builder()
                    return@setSplitAttributesCalculator if (parentConfiguration.screenWidthDp >= 840) {
                        // Side-by-side dual-pane layout for wide displays.
                        builder
                            .setLayoutDirection(SplitAttributes.LayoutDirection.LOCALE)
                            .build()
                    } else if (parentConfiguration.screenHeightDp >= 600) {
                        // Horizontal split for tall displays.
                        builder
                            .setLayoutDirection(SplitAttributes.LayoutDirection.BOTTOM_TO_TOP)
                            .build()
                    } else {
                        // Fallback to expand the secondary container.
                        builder
                            .setSplitType(SPLIT_TYPE_EXPAND)
                            .build()
                    }
                }
            }
            // [END activity_embedding_split_attributes_calculator_kotlin]

            // [START activity_embedding_split_attributes_calculator_tabletop_kotlin]
            if (WindowSdkExtensions.getInstance().extensionVersion >= 2) {
                SplitController.getInstance(this).setSplitAttributesCalculator { params ->
                    val tag = params.splitRuleTag
                    val parentWindowMetrics = params.parentWindowMetrics
                    val parentConfiguration = params.parentConfiguration
                    val foldingFeatures =
                        params.parentWindowLayoutInfo.displayFeatures.filterIsInstance<FoldingFeature>()
                    val feature = if (foldingFeatures.size == 1) foldingFeatures[0] else null
                    val builder = SplitAttributes.Builder()
                    builder.setSplitType(SPLIT_TYPE_HINGE)
                    return@setSplitAttributesCalculator if (feature?.isSeparating == true) {
                    // Horizontal split for tabletop posture.
                    builder
                        .setSplitType(SPLIT_TYPE_HINGE)
                        .setLayoutDirection(
                            if (feature.orientation == FoldingFeature.Orientation.HORIZONTAL) {
                                SplitAttributes.LayoutDirection.BOTTOM_TO_TOP
                            } else {
                                SplitAttributes.LayoutDirection.LOCALE
                            })
                        .build()
                    }
                    else if (parentConfiguration.screenWidthDp >= 840) {
                    // Side-by-side dual-pane layout for wide displays.
                    builder
                        .setLayoutDirection(SplitAttributes.LayoutDirection.LOCALE)
                        .build()
                    } else {
                        // No split for tall displays.
                        builder
                            .setSplitType(SPLIT_TYPE_EXPAND)
                            .build()
                    }
                }
            }
            // [END activity_embedding_split_attributes_calculator_tabletop_kotlin]

            // [START activity_embedding_splitPairFilter_kotlin]
            val splitPairFilter = SplitPairFilter(
               ComponentName(this, ListActivity::class.java),
               ComponentName(this, DetailActivity::class.java),
               null
            )
            // [END activity_embedding_splitPairFilter_kotlin]

            // [START activity_embedding_filterSet_kotlin]
            val filterSet = setOf(splitPairFilter)
            // [END activity_embedding_filterSet_kotlin]

            // [START activity_embedding_splitAttributes_kotlin]
            val splitAttributes: SplitAttributes = SplitAttributes.Builder()
                .setSplitType(SplitAttributes.SplitType.ratio(0.33f))
                .setLayoutDirection(SplitAttributes.LayoutDirection.LEFT_TO_RIGHT)
                .build()
            // [END activity_embedding_splitAttributes_kotlin]

            // [START activity_embedding_splitPairRule_kotlin]
            val splitPairRule = SplitPairRule.Builder(filterSet)
                .setDefaultSplitAttributes(splitAttributes)
                .setMinWidthDp(840)
                .setMinSmallestWidthDp(600)
                .setMaxAspectRatioInPortrait(EmbeddingAspectRatio.ratio(1.5f))
                .setFinishPrimaryWithSecondary(SplitRule.FinishBehavior.NEVER)
                .setFinishSecondaryWithPrimary(SplitRule.FinishBehavior.ALWAYS)
                .setClearTop(false)
                .build()
            // [END activity_embedding_splitPairRule_kotlin]

            // [START activity_embedding_ruleController_kotlin]
              val ruleController = RuleController.getInstance(this)
              ruleController.addRule(splitPairRule)
            // [END activity_embedding_ruleController_kotlin]

            // [START activity_embedding_placeholderActivityFilter_kotlin]
            val placeholderActivityFilter = ActivityFilter(
                ComponentName(this, ListActivity::class.java),
                null
            )
            // [END activity_embedding_placeholderActivityFilter_kotlin]

            // [START activity_embedding_placeholderActivityFilterSet_kotlin]
            val placeholderActivityFilterSet = setOf(placeholderActivityFilter)
            // [END activity_embedding_placeholderActivityFilterSet_kotlin]

            // [START activity_embedding_splitPlaceholderRule_kotlin]
            val splitPlaceholderRule = SplitPlaceholderRule.Builder(
                  placeholderActivityFilterSet,
                  Intent(context, PlaceholderActivity::class.java)
                ).setDefaultSplitAttributes(splitAttributes)
                 .setMinWidthDp(840)
                 .setMinSmallestWidthDp(600)
                 .setMaxAspectRatioInPortrait(EmbeddingAspectRatio.ratio(1.5f))
                 .setFinishPrimaryWithPlaceholder(SplitRule.FinishBehavior.ALWAYS)
                 .setSticky(false)
                 .build()
            // [END activity_embedding_splitPlaceholderRule_kotlin]

            // [START activity_embedding_addRuleSplitPlaceholderRule_kotlin]
            ruleController.addRule(splitPlaceholderRule)
            // [END activity_embedding_addRuleSplitPlaceholderRule_kotlin]

            // [START activity_embedding_expandedActivityFilter_kotlin]
            val expandedActivityFilter = ActivityFilter(
              ComponentName(this, ExpandedActivity::class.java),
              null
            )
            // [END activity_embedding_expandedActivityFilter_kotlin]

            // [START activity_embedding_expandedActivityFilterSet_kotlin]
            val expandedActivityFilterSet = setOf(expandedActivityFilter)
            // [END activity_embedding_expandedActivityFilterSet_kotlin]

            // [START activity_embedding_activityRule_kotlin]
            val activityRule = ActivityRule.Builder(expandedActivityFilterSet)
                .setAlwaysExpand(true)
                .build()
            // [END activity_embedding_activityRule_kotlin]

            // [START activity_embedding_addRuleActivityRule_kotlin]
            ruleController.addRule(activityRule)
            // [END activity_embedding_addRuleActivityRule_kotlin]

            // [START activity_embedding_splitAttributesBuilder_kotlin]
            val _splitAttributesBuilder: SplitAttributes.Builder = SplitAttributes.Builder()
                .setSplitType(SplitAttributes.SplitType.ratio(0.33f))
                .setLayoutDirection(SplitAttributes.LayoutDirection.LEFT_TO_RIGHT)

            if (WindowSdkExtensions.getInstance().extensionVersion >= 6) {
                _splitAttributesBuilder.setDividerAttributes(
                  DividerAttributes.DraggableDividerAttributes.Builder()
                    .setColor(getColor(R.color.divider_color))
                    .setWidthDp(4)
                    .setDragRange(DividerAttributes.DragRange.DRAG_RANGE_SYSTEM_DEFAULT)
                    .build()
                )
            }
            val _splitAttributes: SplitAttributes = _splitAttributesBuilder.build()
            // [END activity_embedding_splitAttributesBuilder_kotlin]

        // [START activity_embedding_isActivityEmbedded_kotlin]
        fun isActivityEmbedded(activity: Activity): Boolean {
            return ActivityEmbeddingController.getInstance(this).isActivityEmbedded(activity)
        }
        // [END activity_embedding_isActivityEmbedded_kotlin]

        }
    }


    // [START activity_embedding_DetailActivity_class_kotlin]
    class DetailActivity: AppCompatActivity() {
        fun onOpenSubdetail() {
          startActivity(Intent(this, SubdetailActivity::class.java))
        }
    }
    // [END activity_embedding_DetailActivity_class_kotlin]


    // [START activity_embedding_SplitInitializer_class_kotlin]
    class SplitInitializer : Initializer<RuleController> {

        override fun create(context: Context): RuleController {
            return RuleController.getInstance(context).apply {
                setRules(RuleController.parseRules(context, R.xml.main_split_config))
            }
        }

        override fun dependencies(): List<Class<out Initializer<*>>> {
            return emptyList()
        }
    }
    // [END activity_embedding_SplitInitializer_class_kotlin]


    /**
     * Function used by snippet.
     */
    fun classForItem(item: Int): Class<*> { return Class::class.java }

    // [START activity_embedding_MenuActivity_class_kotlin]
    inner class MenuActivity: AppCompatActivity() {
        fun onMenuItemSelected(selectedMenuItem: Int) {
            startActivity(Intent(this, classForItem(selectedMenuItem)))
        }
    }
    // [END activity_embedding_MenuActivity_class_kotlin]


    // [START activity_embedding_B_class_kotlin]
    class B: AppCompatActivity() {
        fun onOpenC() {
            startActivity(Intent(this, C::class.java))
        }
    }
    // [END activity_embedding_B_class_kotlin]


    class SnippetActivity2: Activity() {

        private val filterSet = HashSet<SplitPairFilter>()

        // [START activity_embedding_onCreate_RuleController_kotlin]
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            RuleController.getInstance(this)
                .addRule(SplitPairRule.Builder(filterSet).build())
            startActivity(Intent(this, DetailActivity::class.java))
        }
        // [END activity_embedding_onCreate_RuleController_kotlin]
    }


    class SplitDeviceActivity: AppCompatActivity() {

        @OptIn(ExperimentalWindowApi::class)
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            val splitController = SplitController.getInstance(this)
            // [START activity_embedding_onCreate_SplitControllerCallbackAdapter_kotlin]
            val layout = layoutInflater.inflate(R.layout.activity_main, null)
            val view = layout.findViewById<View>(R.id.infoButton)
            lifecycleScope.launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    splitController.splitInfoList(this@SplitDeviceActivity) // The activity instance.
                        .collect { list ->
                            view.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
                        }
                }
            }
            // [END activity_embedding_onCreate_SplitControllerCallbackAdapter_kotlin]
        }
    }


    class SnippetActivity3: AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?){
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_main)
            // [START activity_embedding_pinButton_kotlin]
            val pinButton: Button = findViewById(R.id.pinButton)
            pinButton.setOnClickListener {
                val splitAttributes: SplitAttributes = SplitAttributes.Builder()
                    .setSplitType(SplitAttributes.SplitType.ratio(0.66f))
                    .setLayoutDirection(SplitAttributes.LayoutDirection.LEFT_TO_RIGHT)
                    .build()

                val pinSplitRule = SplitPinRule.Builder()
                    .setSticky(true)
                    .setDefaultSplitAttributes(splitAttributes)
                    .build()

                SplitController.getInstance(applicationContext).pinTopActivityStack(taskId, pinSplitRule)
            }
            // [END activity_embedding_pinButton_kotlin]

            // [START activity_embedding_getSplitSupportStatus_kotlin]
            if (SplitController.getInstance(this).splitSupportStatus ==
                 SplitController.SplitSupportStatus.SPLIT_AVAILABLE) {
                 // Device supports split activity features.
            }
            // [END activity_embedding_getSplitSupportStatus_kotlin]
        }
    }


    /**
     * Used by snippets.
     */
    class ListActivity {}
    class SubdetailActivity {}
    class PlaceholderActivity {}
    class ExpandedActivity {}
    class C {}

}
