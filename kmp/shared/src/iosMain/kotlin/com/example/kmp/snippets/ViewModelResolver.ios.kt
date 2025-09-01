package com.example.kmp.snippets

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.viewmodel.CreationExtras
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ObjCClass
import kotlinx.cinterop.getOriginalKotlinClass
import kotlin.reflect.KClass

// [START android_kmp_viewmodel_resolve_viewmodel]
// iosMain/ViewModelResolver.ios.kt

/**
 *   This function allows retrieving any ViewModel from Swift Code with generics. We only get
 *   [ObjCClass] type for the [modelClass], because the interop between Kotlin and Swift code
 *   doesn't preserve the generic class, but we can retrieve the original KClass in Kotlin.
 */
@BetaInteropApi
@Throws(IllegalArgumentException::class)
fun ViewModelStore.resolveViewModel(
    modelClass: ObjCClass,
    factory: ViewModelProvider.Factory,
    key: String?,
    extras: CreationExtras? = null,
): ViewModel {
    @Suppress("UNCHECKED_CAST")
    val vmClass = getOriginalKotlinClass(modelClass) as? KClass<ViewModel>
    require(vmClass != null) { "The modelClass parameter must be a ViewModel type." }

    val provider = ViewModelProvider.Companion.create(this, factory, extras ?: CreationExtras.Empty)
    return key?.let { provider[key, vmClass] } ?: provider[vmClass]
}
// [END android_kmp_viewmodel_resolve_viewmodel]
