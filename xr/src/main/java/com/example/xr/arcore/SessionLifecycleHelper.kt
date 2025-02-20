package com.example.xr.arcore

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.xr.runtime.Session

/**
 * This is a dummy version of [SessionLifecycleHelper](https://cs.android.com/androidx/platform/frameworks/support/+/androidx-main:xr/arcore/integration-tests/whitebox/src/main/kotlin/androidx/xr/arcore/apps/whitebox/common/SessionLifecycleHelper.kt).
 * This will be removed when Session becomes a LifecycleOwner in cl/726643897.
 */
class SessionLifecycleHelper(
    val onCreateCallback: (Session) -> Unit,

    val onResumeCallback: (() -> Unit)? = null,
    ) : DefaultLifecycleObserver {

}