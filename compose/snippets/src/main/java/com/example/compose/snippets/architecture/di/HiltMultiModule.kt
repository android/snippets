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

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import dagger.BindsInstance
import dagger.Component
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import okhttp3.OkHttpClient

// [START android_di_hilt_multimodule_dependencies]
// LoginModuleDependencies.kt - File in the app module.

@EntryPoint
@InstallIn(SingletonComponent::class)
interface LoginModuleDependencies {

    @AuthInterceptorOkHttpClient
    fun okHttpClient(): OkHttpClient
}
// [END android_di_hilt_multimodule_dependencies]

// [START android_di_hilt_multimodule_component]
// LoginComponent.kt - File in the login module.

@Component(dependencies = [LoginModuleDependencies::class])
interface LoginComponent {

    fun inject(activity: LoginActivity)

    @Component.Builder
    interface Builder {
        fun context(@BindsInstance context: Context): Builder
        fun appDependencies(loginModuleDependencies: LoginModuleDependencies): Builder
        fun build(): LoginComponent
    }
}
// [END android_di_hilt_multimodule_component]

// [START android_di_hilt_multimodule_analytics_adapter]
// LoginAnalyticsAdapter.kt - File in the login module.

class LoginAnalyticsAdapter @Inject constructor(
    @AuthInterceptorOkHttpClient okHttpClient: OkHttpClient
) { /* ... */ }
// [END android_di_hilt_multimodule_analytics_adapter]

// [START android_di_hilt_multimodule_login_activity]
// LoginActivity.kt - File in the login module.

class LoginActivity : AppCompatActivity() {

    @Inject
    lateinit var loginAnalyticsAdapter: LoginAnalyticsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        DaggerLoginComponent.builder()
            .context(this)
            .appDependencies(
                EntryPointAccessors.fromApplication(
                    applicationContext,
                    LoginModuleDependencies::class.java
                )
            )
            .build()
            .inject(this)

        super.onCreate(savedInstanceState)
        // ...
    }
}
// [END android_di_hilt_multimodule_login_activity]
