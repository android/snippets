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

package com.example.wear.snippets.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.wear.widget.SwipeDismissFrameLayout
import com.example.wear.R

// [START android_wear_swipe_dismiss_fragment]
class SwipeDismissFragment : Fragment() {
    private val callback = object : SwipeDismissFrameLayout.Callback() {
        override fun onSwipeStarted(layout: SwipeDismissFrameLayout) {
            // Optional
        }

        override fun onSwipeCanceled(layout: SwipeDismissFrameLayout) {
            // Optional
        }

        override fun onDismissed(layout: SwipeDismissFrameLayout) {
            // Code here for custom behavior, such as going up the
            // back stack and destroying the fragment but staying in the app.
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        SwipeDismissFrameLayout(activity).apply {

            // If the fragment should fill the screen (optional), then in the layout file,
            // in the androidx.wear.widget.SwipeDismissFrameLayout element,
            // set the android:layout_width and android:layout_height attributes
            // to "match_parent".

            inflater.inflate(
                R.layout.swipe_dismiss_frame_layout,
                this,
                false
            ).also { inflatedView ->
                addView(inflatedView)
            }
            addCallback(callback)
        }
    // [START_EXCLUDE silent]

    fun addSwipeDismissCallback() {
        // [START android_wear_swipe_dismiss_callback]
        activity?.findViewById<SwipeDismissFrameLayout>(R.id.swipe_dismiss_root)?.apply {
            addCallback(object : SwipeDismissFrameLayout.Callback() {

                override fun onDismissed(layout: SwipeDismissFrameLayout) {
                    layout.visibility = View.GONE
                }
            })
        }
        // [END android_wear_swipe_dismiss_callback]
    }
    // [END_EXCLUDE]
}
// [END android_wear_swipe_dismiss_fragment]
