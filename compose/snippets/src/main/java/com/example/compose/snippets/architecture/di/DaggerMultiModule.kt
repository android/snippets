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

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.example.compose.snippets.architecture.di.DaggerAndroidActivityScopeDeclaration.ActivityScope
import com.example.compose.snippets.architecture.di.DaggerMultiModuleAppComponentInitial.UserRepository
import com.example.compose.snippets.architecture.di.DaggerMultiModuleLoginActivityInject.LoginActivity
import com.example.compose.snippets.architecture.di.DaggerMultiModuleLoginViewModel.LoginViewModel
import dagger.Component
import javax.inject.Inject
import javax.inject.Singleton

object DaggerMultiModuleSubcomponentProblem {
    interface LoginComponent {
        interface Factory {
            fun create(): LoginComponent
        }

        fun inject(activity: LoginActivity)
    }

    interface ApplicationComponent {
        fun loginComponent(): LoginComponent.Factory
    }

    class MyDaggerApplication : Application() {
        lateinit var appComponent: ApplicationComponent
    }

    // [START android_di_dagger_multimodule_subcomponent_problem]
    class LoginActivity : Activity() {
        // [START_EXCLUDE]
        lateinit var loginComponent: LoginComponent
        // [END_EXCLUDE]

        override fun onCreate(savedInstanceState: Bundle?) {
            // Creation of the login graph using the application graph.
            loginComponent = (applicationContext as MyDaggerApplication)
                .appComponent.loginComponent().create()

            // Make Dagger instantiate @Inject fields in LoginActivity.
            loginComponent.inject(this)
            // [START_EXCLUDE]
            super.onCreate(savedInstanceState)
            // [END_EXCLUDE]
        }
    }
    // [END android_di_dagger_multimodule_subcomponent_problem]
}

object DaggerMultiModuleSubcomponentSolution {
    interface LoginComponent {
        interface Factory {
            fun create(): LoginComponent
        }

        fun inject(activity: LoginActivity)
    }

    interface ApplicationComponent {
        fun loginComponent(): LoginComponent.Factory
    }

    object DaggerApplicationComponent {
        fun create(): ApplicationComponent = object : ApplicationComponent {
            override fun loginComponent(): LoginComponent.Factory = object : LoginComponent.Factory {
                override fun create(): LoginComponent = object : LoginComponent {
                    override fun inject(activity: LoginActivity) {}
                }
            }
        }
    }

    // [START android_di_dagger_multimodule_login_component_provider]
    interface LoginComponentProvider {
        fun provideLoginComponent(): LoginComponent
    }
    // [END android_di_dagger_multimodule_login_component_provider]

    // [START android_di_dagger_multimodule_activity_use_provider]
    class LoginActivity : Activity() {
        // [START_EXCLUDE]
        lateinit var loginComponent: LoginComponent
        // [END_EXCLUDE]

        override fun onCreate(savedInstanceState: Bundle?) {
            loginComponent = (applicationContext as LoginComponentProvider)
                .provideLoginComponent()

            loginComponent.inject(this)
            // [START_EXCLUDE]
            super.onCreate(savedInstanceState)
            // [END_EXCLUDE]
        }
    }
    // [END android_di_dagger_multimodule_activity_use_provider]

    // [START android_di_dagger_multimodule_application_implement_provider]
    class MyApplication : Application(), LoginComponentProvider {
        // Reference to the application graph that is used across the whole app.
        val appComponent = DaggerApplicationComponent.create()

        override fun provideLoginComponent(): LoginComponent {
            return appComponent.loginComponent().create()
        }
    }
    // [END android_di_dagger_multimodule_application_implement_provider]
}

object DaggerMultiModuleAppComponentInitial {
    // [START android_di_dagger_multimodule_app_component_initial]
    // UserRepository's dependencies.
    class UserLocalDataSource @Inject constructor() { /* ... */ }
    class UserRemoteDataSource @Inject constructor() { /* ... */ }

    // UserRepository is scoped to AppComponent.
    @Singleton
    class UserRepository @Inject constructor(
        private val localDataSource: UserLocalDataSource,
        private val remoteDataSource: UserRemoteDataSource
    ) { /* ... */ }

    @Singleton
    @Component
    interface AppComponent { /* ... */ }
    // [END android_di_dagger_multimodule_app_component_initial]
}

object DaggerMultiModuleLoginViewModel {
    // [START android_di_dagger_multimodule_login_viewmodel]
    // LoginViewModel depends on UserRepository that is scoped to AppComponent.
    class LoginViewModel @Inject constructor(
        private val userRepository: UserRepository
    ) { /* ... */ }
    // [END android_di_dagger_multimodule_login_viewmodel]
}

object DaggerMultiModuleLoginComponentDependencies {
    @ActivityScope
    // [START android_di_dagger_multimodule_login_component_dependencies]
    // Use the dependencies attribute in the Component annotation to specify the
    // dependencies of this Component.
    @Component(dependencies = [AppComponent::class])
    interface LoginComponent {
        fun inject(activity: LoginActivity)
    }
    // [END android_di_dagger_multimodule_login_component_dependencies]
}

@ActivityScope
// [START android_di_dagger_multimodule_login_component_factory]
@Component(dependencies = [AppComponent::class])
interface LoginComponent {

    @Component.Factory
    interface Factory {
        // Takes an instance of AppComponent when creating
        // an instance of LoginComponent.
        fun create(appComponent: AppComponent): LoginComponent
    }

    fun inject(activity: LoginActivity)
}
// [END android_di_dagger_multimodule_login_component_factory]

object DaggerMultiModuleLoginActivityInject {
    class MyApplication : Application() {
        val appComponent: AppComponent = DaggerAppComponent.create()
    }

    // [START android_di_dagger_multimodule_login_activity_inject]
    class LoginActivity : Activity() {

        // You want Dagger to provide an instance of LoginViewModel from the Login graph.
        @Inject lateinit var loginViewModel: LoginViewModel

        override fun onCreate(savedInstanceState: Bundle?) {
            // Gets appComponent from MyApplication available in the base Gradle module.
            val appComponent = (applicationContext as MyApplication).appComponent

            // Creates a new instance of LoginComponent.
            // Injects the component to populate the @Inject fields.
            DaggerLoginComponent.factory().create(appComponent).inject(this)

            super.onCreate(savedInstanceState)

            // Now you can access loginViewModel.
        }
    }
    // [END android_di_dagger_multimodule_login_activity_inject]
}

// [START android_di_dagger_multimodule_app_component_expose_repository]
@Singleton
@Component
interface AppComponent {
    fun userRepository(): UserRepository
}
// [END android_di_dagger_multimodule_app_component_expose_repository]

object DaggerMultiModuleScopedDependentComponent {
    // [START android_di_dagger_multimodule_scoped_dependent_component]
    @ActivityScope
    @Component(dependencies = [AppComponent::class])
    interface LoginComponent { /* ... */ }

    @ActivityScope
    class LoginViewModel @Inject constructor(
        private val userRepository: UserRepository
    ) { /* ... */ }
    // [END android_di_dagger_multimodule_scoped_dependent_component]
}
