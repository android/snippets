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

package com.example.compose.snippets.architecture.di

import android.app.Application
import android.content.ContentProvider
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import androidx.activity.ComponentActivity
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.components.ActivityComponent
import dagger.hilt.android.qualifiers.ActivityContext
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ActivityScoped
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Qualifier
import javax.inject.Singleton
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit

private object HiltAndroidApplicationSnippet {
    // [START android_di_hilt_android_application]
    @HiltAndroidApp
    class ExampleApplication : Application() {
        // ...
    }
    // [END android_di_hilt_android_application]
}

private object HiltAndroidEntryPointSnippet {
    // [START android_di_hilt_android_entry_point]
    @AndroidEntryPoint
    class ExampleActivity : ComponentActivity() {
        // ...
    }
    // [END android_di_hilt_android_entry_point]
}

// Note: Wrapper objects and helper classes enclosing @Inject or @Module declarations use `internal`
// instead of `private` because Dagger KSP rejects private @Inject targets and private @Module enclosures.
internal object HiltAndroidFieldInjectionSnippet {
    // [START android_di_hilt_android_field_injection]
    @AndroidEntryPoint
    class ExampleActivity : ComponentActivity() {

        @Inject lateinit var analytics: AnalyticsAdapter
        // ...
    }
    // [END android_di_hilt_android_field_injection]
}

// Shared across HiltAndroid.kt, HiltJetpack.kt, and HiltTesting.kt.
// [START android_di_hilt_android_constructor_injection]
class AnalyticsAdapter @Inject constructor(
    private val service: AnalyticsService
) {
    // ...
}
// [END android_di_hilt_android_constructor_injection]

// [START android_di_hilt_android_binds]
interface AnalyticsService {
    fun analyticsMethods()
}

// Constructor-injected, because Hilt needs to know how to
// provide instances of AnalyticsServiceImpl, too.
class AnalyticsServiceImpl @Inject constructor(
    // ...
) : AnalyticsService {
    // [START_EXCLUDE]
    override fun analyticsMethods() {}
    // [END_EXCLUDE]
}

@Module
@InstallIn(ActivityComponent::class)
abstract class AnalyticsModule {

    @Binds
    abstract fun bindAnalyticsService(
        analyticsServiceImpl: AnalyticsServiceImpl
    ): AnalyticsService
}
// [END android_di_hilt_android_binds]

internal object HiltAndroidProvidesSnippet {
    // [START android_di_hilt_android_provides]
    @Module
    @InstallIn(ActivityComponent::class)
    object AnalyticsModule {

        @Provides
        fun provideAnalyticsService(
            // Potential dependencies of this type.
        ): AnalyticsService {
            return Retrofit.Builder()
                .baseUrl("https://example.com")
                .build()
                .create(AnalyticsService::class.java)
        }
    }
    // [END android_di_hilt_android_provides]
}

// Shared across HiltAndroid.kt and HiltMultiModule.kt.
// [START android_di_hilt_android_qualifiers]
@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class AuthInterceptorOkHttpClient

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class OtherInterceptorOkHttpClient
// [END android_di_hilt_android_qualifiers]

internal class AuthInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(chain.request())
}

internal class OtherInterceptor @Inject constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response =
        chain.proceed(chain.request())
}

internal object HiltAndroidNetworkModuleSnippet {
    // [START android_di_hilt_android_network_module]
    @Module
    @InstallIn(SingletonComponent::class)
    object NetworkModule {

        @AuthInterceptorOkHttpClient
        @Provides
        fun provideAuthInterceptorOkHttpClient(
            authInterceptor: AuthInterceptor
        ): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .build()
        }

        @OtherInterceptorOkHttpClient
        @Provides
        fun provideOtherInterceptorOkHttpClient(
            otherInterceptor: OtherInterceptor
        ): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(otherInterceptor)
                .build()
        }
    }
    // [END android_di_hilt_android_network_module]
}

internal object HiltAndroidInjectQualifiedSnippet {
    // [START android_di_hilt_android_inject_qualified]
    // As a dependency of another class.
    @Module
    @InstallIn(ActivityComponent::class)
    object AnalyticsModule {

        @Provides
        fun provideAnalyticsService(
            @AuthInterceptorOkHttpClient okHttpClient: OkHttpClient
        ): AnalyticsService {
            return Retrofit.Builder()
                .baseUrl("https://example.com")
                .client(okHttpClient)
                .build()
                .create(AnalyticsService::class.java)
        }
    }

    // As a dependency of a constructor-injected class.
    // [START_EXCLUDE silent]
    /*
    // [END_EXCLUDE]
    class ExampleServiceImpl @Inject constructor(
        @AuthInterceptorOkHttpClient private val okHttpClient: OkHttpClient
    ) : ...
    // [START_EXCLUDE silent]
     */
    class ExampleServiceImpl @Inject constructor(
        @AuthInterceptorOkHttpClient private val okHttpClient: OkHttpClient
    )
    // [END_EXCLUDE]

    // At field injection.
    @AndroidEntryPoint
    class ExampleActivity : ComponentActivity() {

        @AuthInterceptorOkHttpClient
        @Inject lateinit var okHttpClient: OkHttpClient
    }
    // [END android_di_hilt_android_inject_qualified]
}

internal object HiltAndroidPredefinedQualifiersSnippet {
    // [START android_di_hilt_android_predefined_qualifiers]
    class AnalyticsAdapter @Inject constructor(
        @ActivityContext private val context: Context,
        private val service: AnalyticsService
    ) {
        // ...
    }
    // [END android_di_hilt_android_predefined_qualifiers]
}

internal object HiltAndroidActivityScopedSnippet {
    // [START android_di_hilt_android_activity_scoped]
    @ActivityScoped
    class AnalyticsAdapter @Inject constructor(
        private val service: AnalyticsService
    ) {
        // ...
    }
    // [END android_di_hilt_android_activity_scoped]
}

internal object HiltAndroidSingletonScopedBindsSnippet {
    // [START android_di_hilt_android_singleton_scoped]
    // If AnalyticsService is an interface.
    @Module
    @InstallIn(SingletonComponent::class)
    abstract class AnalyticsModule {

        @Singleton
        @Binds
        abstract fun bindAnalyticsService(
            analyticsServiceImpl: AnalyticsServiceImpl
        ): AnalyticsService
    }

    // [START_EXCLUDE silent]
    /*
    // [END_EXCLUDE]
    // If you don't own AnalyticsService.
    @Module
    @InstallIn(SingletonComponent::class)
    object AnalyticsModule {

        @Singleton
        @Provides
        fun provideAnalyticsService(): AnalyticsService {
            return Retrofit.Builder()
                .baseUrl("https://example.com")
                .build()
                .create(AnalyticsService::class.java)
        }
    }
    // [START_EXCLUDE silent]
     */
    // [END_EXCLUDE]
    // [END android_di_hilt_android_singleton_scoped]
}

internal object HiltAndroidSingletonScopedProvidesSnippet {
    @Module
    @InstallIn(SingletonComponent::class)
    object AnalyticsModule {

        @Singleton
        @Provides
        fun provideAnalyticsService(): AnalyticsService {
            return Retrofit.Builder()
                .baseUrl("https://example.com")
                .build()
                .create(AnalyticsService::class.java)
        }
    }
}

internal object HiltAndroidApplicationContextQualifiedSnippet {
    // [START android_di_hilt_android_application_context]
    class AnalyticsServiceImpl @Inject constructor(
        @ApplicationContext context: Context
    ) : AnalyticsService {
        // [START_EXCLUDE]
        override fun analyticsMethods() {}
        // [END_EXCLUDE]
    }

    // [START_EXCLUDE silent]
    /*
    // [END_EXCLUDE]
    // The Application binding is available without qualifiers.
    class AnalyticsServiceImpl @Inject constructor(
        application: Application
    ) : AnalyticsService {
        // ...
    }
    // [START_EXCLUDE silent]
     */
    // [END_EXCLUDE]
    // [END android_di_hilt_android_application_context]
}

internal object HiltAndroidApplicationContextUnqualifiedSnippet {
    class AnalyticsServiceImpl @Inject constructor(
        application: Application
    ) : AnalyticsService {
        override fun analyticsMethods() {}
    }
}

internal object HiltAndroidActivityContextQualifiedSnippet {
    // [START android_di_hilt_android_activity_context]
    class AnalyticsAdapter @Inject constructor(
        @ActivityContext context: Context
    ) {
        // ...
    }

    // [START_EXCLUDE silent]
    /*
    // [END_EXCLUDE]
    // The Activity binding is available without qualifiers.
    class AnalyticsAdapter @Inject constructor(
        activity: ComponentActivity
    ) {
        // ...
    }
    // [START_EXCLUDE silent]
     */
    // [END_EXCLUDE]
    // [END android_di_hilt_android_activity_context]
}

internal object HiltAndroidActivityContextUnqualifiedSnippet {
    class AnalyticsAdapter @Inject constructor(
        activity: ComponentActivity
    )
}

private object HiltAndroidContentProviderEntryPointSnippet {
    // [START android_di_hilt_android_content_provider_entry_point]
    class ExampleContentProvider : ContentProvider() {

        @EntryPoint
        @InstallIn(SingletonComponent::class)
        interface ExampleContentProviderEntryPoint {
            fun analyticsService(): AnalyticsService
        }

        // [START_EXCLUDE]
        override fun onCreate(): Boolean = true
        override fun query(
            uri: Uri,
            projection: Array<out String>?,
            selection: String?,
            selectionArgs: Array<out String>?,
            sortOrder: String?
        ): Cursor? = null
        override fun getType(uri: Uri): String? = null
        override fun insert(uri: Uri, values: ContentValues?): Uri? = null
        override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
        override fun update(
            uri: Uri,
            values: ContentValues?,
            selection: String?,
            selectionArgs: Array<out String>?
        ): Int = 0
        // [END_EXCLUDE]
    }
    // [END android_di_hilt_android_content_provider_entry_point]
}

private object HiltAndroidEntryPointAccessorsSnippet {
    // [START android_di_hilt_android_entry_point_accessors]
    class ExampleContentProvider : ContentProvider() {
        // [START_EXCLUDE]
        @EntryPoint
        @InstallIn(SingletonComponent::class)
        interface ExampleContentProviderEntryPoint {
            fun analyticsService(): AnalyticsService
        }

        override fun onCreate(): Boolean = true
        override fun getType(uri: Uri): String? = null
        override fun insert(uri: Uri, values: ContentValues?): Uri? = null
        override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int = 0
        override fun update(
            uri: Uri,
            values: ContentValues?,
            selection: String?,
            selectionArgs: Array<out String>?
        ): Int = 0
        // [END_EXCLUDE]

        override fun query(
            uri: Uri,
            projection: Array<out String>?,
            selection: String?,
            selectionArgs: Array<out String>?,
            sortOrder: String?
        ): Cursor {
            val appContext = context?.applicationContext ?: throw IllegalStateException()
            val hiltEntryPoint =
                EntryPointAccessors.fromApplication(
                    appContext,
                    ExampleContentProviderEntryPoint::class.java
                )

            val analyticsService = hiltEntryPoint.analyticsService()
            // [START_EXCLUDE]
            return MatrixCursor(emptyArray())
            // [END_EXCLUDE]
        }
    }
    // [END android_di_hilt_android_entry_point_accessors]
}
