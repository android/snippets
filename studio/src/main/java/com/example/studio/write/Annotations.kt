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

package com.example.studio.write

import android.Manifest
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.util.AttributeSet
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.CheckResult
import androidx.annotation.FloatRange
import androidx.annotation.IntRange
import androidx.annotation.RequiresPermission
import androidx.annotation.Size
import androidx.annotation.StringRes
import androidx.annotation.VisibleForTesting
import java.io.IOException

// [START android_studio_write_annotations_typedef]
import androidx.annotation.IntDef
//...
// Define the list of accepted constants and declare the NavigationMode annotation.
@Retention(AnnotationRetention.SOURCE)
@IntDef(NAVIGATION_MODE_STANDARD, NAVIGATION_MODE_LIST, NAVIGATION_MODE_TABS)
annotation class NavigationMode

// Declare the constants.
const val NAVIGATION_MODE_STANDARD = 0
const val NAVIGATION_MODE_LIST = 1
const val NAVIGATION_MODE_TABS = 2

abstract class ActionBar {

    // Decorate the target methods with the annotation.
    // Attach the annotation.
    @get:NavigationMode
    @setparam:NavigationMode
    abstract var navigationMode: Int
}
// [END android_studio_write_annotations_typedef]

class LayoutInflaterSnippet : LayoutInflaterParent() {
    // [START android_studio_write_annotations_nullness]
    // ...
    /** Annotation not used because of the safe-call operator(?)**/
    override fun onCreateView(
        name: String?,
        context: Context,
        attrs: AttributeSet
    ): View? {
        // [START_EXCLUDE]
        return null
        // [END_EXCLUDE]
    }
    // ...
    // [END android_studio_write_annotations_nullness]
}

abstract class TitleSnippet {
    // [START android_studio_write_annotations_resource_string]
    abstract fun setTitle(@StringRes resId: Int)
    // [END android_studio_write_annotations_resource_string]
}

// [START android_studio_write_annotations_size]
fun getLocation(button: View, @Size(min=1) location: IntArray) {
    button.getLocationOnScreen(location)
}
// [END android_studio_write_annotations_size]

abstract class WallpaperSnippet {
    // [START android_studio_write_annotations_requires_permission]
    @RequiresPermission(Manifest.permission.SET_WALLPAPER)
    @Throws(IOException::class)
    abstract fun setWallpaper(bitmap: Bitmap)
    // [END android_studio_write_annotations_requires_permission]
}

// [START android_studio_write_annotations_requires_permission_all_of]
@RequiresPermission(allOf = [
    Manifest.permission.READ_EXTERNAL_STORAGE,
    Manifest.permission.ACCESS_MEDIA_LOCATION
])
fun copyImageFile(dest: String, source: String) {
    // ...
}
// [END android_studio_write_annotations_requires_permission_all_of]

// [START android_studio_write_annotations_action_request_discoverable]
@RequiresPermission(android.Manifest.permission.BLUETOOTH)
const val ACTION_REQUEST_DISCOVERABLE = "android.bluetooth.adapter.action.REQUEST_DISCOVERABLE"
// [END android_studio_write_annotations_action_request_discoverable]

// [START android_studio_write_annotations_requires_permission_read_write]
@RequiresPermission.Read(RequiresPermission(READ_HISTORY_BOOKMARKS))
@RequiresPermission.Write(RequiresPermission(WRITE_HISTORY_BOOKMARKS))
val BOOKMARKS_URI = Uri.parse("content://browser/bookmarks")
// [END android_studio_write_annotations_requires_permission_read_write]

abstract class ActivitySnippet {
    // [START android_studio_write_annotations_indirect_permissions]
    abstract fun startActivity(@RequiresPermission intent: Intent, bundle: Bundle?)
    // [END android_studio_write_annotations_indirect_permissions]
}

// [START android_studio_write_annotations_action_call]
@RequiresPermission(Manifest.permission.CALL_PHONE)
const val ACTION_CALL = "android.intent.action.CALL"
// [END android_studio_write_annotations_action_call]

abstract class PermissionCheckerSnippet {
    // [START android_studio_write_annotations_check_result]
    @CheckResult(suggest = "#enforcePermission(String,int,int,String)")
    abstract fun checkPermission(permission: String, pid: Int, uid: Int): Int
    // [END android_studio_write_annotations_check_result]
}

class CallSuperSnippet : BaseActivity() {
    // [START android_studio_write_annotations_call_super]
    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
    }
    // [END android_studio_write_annotations_call_super]
}

// [START android_studio_write_annotations_visible_for_testing]
@VisibleForTesting(otherwise = VisibleForTesting.PRIVATE)
fun myMethod() {
    // ...
}
// [END android_studio_write_annotations_visible_for_testing]

open class LayoutInflaterParent {
    open fun onCreateView(
        name: String?,
        context: Context,
        attrs: AttributeSet,
    ): View? = null
}

open class BaseActivity {
    open fun onCreate(savedInstanceState: Bundle?) {}
}

private const val READ_HISTORY_BOOKMARKS = "com.android.browser.permission.READ_HISTORY_BOOKMARKS"
private const val WRITE_HISTORY_BOOKMARKS = "com.android.browser.permission.WRITE_HISTORY_BOOKMARKS"
