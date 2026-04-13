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

package com.example.compose.snippets.adaptivelayouts.foldaware

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.window.layout.FoldingFeature
import androidx.window.layout.WindowInfoTracker
import androidx.window.layout.WindowLayoutInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlin.contracts.ExperimentalContracts
import kotlin.contracts.contract

// Stubs for RxJava
private class Disposable {
    fun dispose() {}
}
private class Observable<T> {
    fun observeOn(scheduler: Any): Observable<T> = this
    fun subscribe(onNext: (T) -> Unit): Disposable = Disposable()
}
private object AndroidSchedulers {
    fun mainThread(): Any = Any()
}
// Stub extension method
private fun WindowInfoTracker.windowLayoutInfoObservable(activity: android.app.Activity): Observable<WindowLayoutInfo> = Observable()

// Stub for view binding
private class ActivityDisplayFeaturesBinding {
    val root: android.view.View = TODO()
    companion object {
        fun inflate(inflater: android.view.LayoutInflater): ActivityDisplayFeaturesBinding = TODO()
    }
}
private class ActivityRxBinding {
    val root: android.view.View = TODO()
    companion object {
        fun inflate(inflater: android.view.LayoutInflater): ActivityRxBinding = TODO()
    }
}
private class ActivitySplitLayoutBinding {
    fun getRoot(): android.view.View = TODO()
    companion object {
        fun inflate(inflater: android.view.LayoutInflater): ActivitySplitLayoutBinding = TODO()
    }
}

class DisplayFeaturesActivity : ComponentActivity() {

    private lateinit var binding: ActivityDisplayFeaturesBinding

    // [START android_compose_adaptivelayouts_foldaware_kotlin_flows]
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDisplayFeaturesBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lifecycleScope.launch(Dispatchers.Main) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                WindowInfoTracker.getOrCreate(this@DisplayFeaturesActivity)
                    .windowLayoutInfo(this@DisplayFeaturesActivity)
                    .collect { newLayoutInfo ->
                        // Use newLayoutInfo to update the layout.
                    }
            }
        }
    }
    // [END android_compose_adaptivelayouts_foldaware_kotlin_flows]
}

class RxActivity: ComponentActivity() {

    private lateinit var binding: ActivitySplitLayoutBinding

    private var disposable: Disposable? = null
    private lateinit var observable: Observable<WindowLayoutInfo>

   // [START android_compose_adaptivelayouts_foldaware_rxjava]
   override fun onCreate(savedInstanceState: Bundle?) {
       super.onCreate(savedInstanceState)

       binding = ActivitySplitLayoutBinding.inflate(getLayoutInflater());
       setContentView(binding.getRoot());

        // Create a new observable.
        observable = WindowInfoTracker.getOrCreate(this@RxActivity)
            .windowLayoutInfoObservable(this@RxActivity)
   }

   override fun onStart() {
       super.onStart()

        // Subscribe to receive WindowLayoutInfo updates.
        disposable?.dispose()
        disposable = observable
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { newLayoutInfo ->
            // Use newLayoutInfo to update the layout.
        }
   }

   override fun onStop() {
       super.onStop()

        // Dispose of the WindowLayoutInfo observable.
        disposable?.dispose()
   }
   // [END android_compose_adaptivelayouts_foldaware_rxjava]
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // [START android_compose_adaptivelayouts_foldaware_bounds]
        lifecycleScope.launch(Dispatchers.Main) {
            lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Safely collects from WindowInfoTracker when the lifecycle is
                // STARTED and stops collection when the lifecycle is STOPPED.
                WindowInfoTracker.getOrCreate(this@MainActivity)
                    .windowLayoutInfo(this@MainActivity)
                    .collect { layoutInfo ->
                        // New posture information.
                        val foldingFeature = layoutInfo.displayFeatures
                            .filterIsInstance<FoldingFeature>()
                            .firstOrNull()
                        // Use information from the foldingFeature object.
                    }
            }
        }
        // [END android_compose_adaptivelayouts_foldaware_bounds]
    }
}

@OptIn(ExperimentalContracts::class)
// [START android_compose_adaptivelayouts_foldaware_is_tabletop]
fun isTableTopPosture(foldFeature : FoldingFeature?) : Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.HALF_OPENED &&
            foldFeature.orientation == FoldingFeature.Orientation.HORIZONTAL
}
// [END android_compose_adaptivelayouts_foldaware_is_tabletop]

// Stub for TABLE_TOP and WindowSdkExtensions
private val TABLE_TOP = Any()
private class WindowSdkExtensions {
    val extensionsVersion: Int = 0
    companion object {
        fun getInstance(): WindowSdkExtensions = WindowSdkExtensions()
    }
}
// Stub extension property
private val WindowInfoTracker.supportedPostures: List<Any> get() = listOf(TABLE_TOP)

private fun checkTableTop(context: android.content.Context) {
    // [START android_compose_adaptivelayouts_foldaware_tabletop_check]
    if (WindowSdkExtensions.getInstance().extensionsVersion >= 6) {
        val postures = WindowInfoTracker.getOrCreate(context).supportedPostures
        if (postures.contains(TABLE_TOP)) {
            // Device supports tabletop posture.
       }
    }
    // [END android_compose_adaptivelayouts_foldaware_tabletop_check]
}

@OptIn(ExperimentalContracts::class)
// [START android_compose_adaptivelayouts_foldaware_is_book]
fun isBookPosture(foldFeature : FoldingFeature?) : Boolean {
    contract { returns(true) implies (foldFeature != null) }
    return foldFeature?.state == FoldingFeature.State.HALF_OPENED &&
            foldFeature.orientation == FoldingFeature.Orientation.VERTICAL
}
// [END android_compose_adaptivelayouts_foldaware_is_book]
