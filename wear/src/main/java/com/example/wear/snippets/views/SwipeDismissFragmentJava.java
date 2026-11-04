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

package com.example.wear.snippets.views;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.wear.widget.SwipeDismissFrameLayout;
import androidx.wear.widget.SwipeDismissFrameLayout.Callback;
import com.example.wear.R;

public class SwipeDismissFragmentJava {
    @SuppressLint("ValidFragment")
    // [START android_wear_swipe_dismiss_fragment_java]
    public class SwipeDismissFragment extends Fragment {
        private final Callback callback =
            new Callback() {
                @Override
                public void onSwipeStarted(SwipeDismissFrameLayout layout) {
                    // Optional
                }

                @Override
                public void onSwipeCanceled(SwipeDismissFrameLayout layout) {
                    // Optional
                }

                @Override
                public void onDismissed(SwipeDismissFrameLayout layout) {
                    // Code here for custom behavior, such as going up the
                    // back stack and destroying the fragment but staying in the app.
                }
            };

        @Override
        public View onCreateView(
                LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            SwipeDismissFrameLayout swipeLayout = new SwipeDismissFrameLayout(getActivity());

            // If the fragment should fill the screen (optional), then in the layout file,
            // in the androidx.wear.widget.SwipeDismissFrameLayout element,
            // set the android:layout_width and android:layout_height attributes
            // to "match_parent".

            View inflatedView =
                    inflater.inflate(R.layout.swipe_dismiss_frame_layout, swipeLayout, false);
            swipeLayout.addView(inflatedView);
            swipeLayout.addCallback(callback);

            return swipeLayout;
        }
    }
    // [END android_wear_swipe_dismiss_fragment_java]

    void addSwipeDismissCallback(Activity activity) {
        // [START android_wear_swipe_dismiss_callback_java]
        SwipeDismissFrameLayout testLayout =
            (SwipeDismissFrameLayout) activity.findViewById(R.id.swipe_dismiss_root);
        testLayout.addCallback(new SwipeDismissFrameLayout.Callback() {
            @Override
            public void onDismissed(SwipeDismissFrameLayout layout) {
                layout.setVisibility(View.GONE);
            }
          }
        );
        // [END android_wear_swipe_dismiss_callback_java]
    }
}
