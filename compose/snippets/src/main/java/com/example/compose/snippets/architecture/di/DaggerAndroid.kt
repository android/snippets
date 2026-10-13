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
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.compose.snippets.architecture.di.DaggerAndroidActivityScopeDeclaration.ActivityScope
import com.example.compose.snippets.architecture.di.DaggerAndroidRepositoryDependencies.UserLocalDataSource
import com.example.compose.snippets.architecture.di.DaggerAndroidRepositoryDependencies.UserRemoteDataSource
import com.example.compose.snippets.architecture.di.DaggerAndroidRepositoryDependencies.UserRepository
import com.example.compose.snippets.architecture.di.DaggerAndroidSubcomponentFlow.LoginComponent
import dagger.Component
import dagger.Module
import dagger.Provides
import dagger.Subcomponent
import dagger.hilt.migration.DisableInstallInCheck
import javax.inject.Inject
import javax.inject.Scope
import javax.inject.Singleton
import okhttp3.OkHttpClient

object DaggerAndroidApplicationComponentBasic {
    // [START android_di_dagger_android_application_component]
    // Definition of the Application graph.
    @Component
    interface ApplicationComponent { /* ... */ }

    // appComponent lives in the Application class to share its lifecycle.
    class MyApplication : Application() {
        // Reference to the application graph that is used across the whole app.
        val appComponent = DaggerApplicationComponent.create()
    }
    // [END android_di_dagger_android_application_component]
}

object DaggerAndroidFieldInjection {
    class LoginViewModel @Inject constructor()

    // [START android_di_dagger_android_field_injection]
    class LoginActivity : Activity() {
        // You want Dagger to provide an instance of LoginViewModel from the graph.
        @Inject lateinit var loginViewModel: LoginViewModel
    }
    // [END android_di_dagger_android_field_injection]
}

object DaggerAndroidComponentInject {
    class LoginActivity : Activity()

    // [START android_di_dagger_android_component_inject]
    @Component
    interface ApplicationComponent {
        // This tells Dagger that LoginActivity requests injection so the graph needs to
        // satisfy all the dependencies of the fields that LoginActivity is requesting.
        fun inject(activity: LoginActivity)
    }
    // [END android_di_dagger_android_component_inject]
}

object DaggerAndroidActivityOnCreateInject {
    interface ApplicationComponent {
        fun inject(activity: LoginActivity)
    }

    class MyApplication : Application() {
        lateinit var appComponent: ApplicationComponent
    }

    // [START android_di_dagger_android_activity_on_create_inject]
    class LoginActivity : Activity() {
        // You want Dagger to provide an instance of LoginViewModel from the graph.
        @Inject lateinit var loginViewModel: LoginViewModel

        override fun onCreate(savedInstanceState: Bundle?) {
            // Make Dagger instantiate @Inject fields in LoginActivity.
            (applicationContext as MyApplication).appComponent.inject(this)
            // Now loginViewModel is available.

            super.onCreate(savedInstanceState)
        }
    }

    // @Inject tells Dagger how to create instances of LoginViewModel.
    class LoginViewModel @Inject constructor(
        private val userRepository: UserRepository
    ) { /* ... */ }
    // [END android_di_dagger_android_activity_on_create_inject]
}

object DaggerAndroidRepositoryDependencies {
    // [START android_di_dagger_android_repository_dependencies]
    class UserRepository @Inject constructor(
        private val localDataSource: UserLocalDataSource,
        private val remoteDataSource: UserRemoteDataSource
    ) { /* ... */ }

    class UserLocalDataSource @Inject constructor() { /* ... */ }
    class UserRemoteDataSource @Inject constructor(
        private val loginService: LoginRetrofitService
    ) { /* ... */ }
    // [END android_di_dagger_android_repository_dependencies]
}

@DisableInstallInCheck
// [START android_di_dagger_android_network_module]
// @Module informs Dagger that this class is a Dagger Module
@Module
class NetworkModule {

    // @Provides tell Dagger how to create instances of the type that this function
    // returns (i.e. LoginRetrofitService).
    // Function parameters are the dependencies of this type.
    @Provides
    fun provideLoginRetrofitService(): LoginRetrofitService {
        // Whenever Dagger needs to provide an instance of type LoginRetrofitService,
        // this code (the one inside the @Provides method) is run.
        return Retrofit.Builder()
            .baseUrl("https://example.com")
            .build()
            .create(LoginService::class.java)
    }
}
// [END android_di_dagger_android_network_module]

object DaggerAndroidNetworkModuleOkHttp {
    @DisableInstallInCheck
    // [START android_di_dagger_android_network_module_okhttp]
    @Module
    class NetworkModule {
        // Hypothetical dependency on LoginRetrofitService.
        @Provides
        fun provideLoginRetrofitService(
            okHttpClient: OkHttpClient
        ): LoginRetrofitService {
            // [START_EXCLUDE]
            return FakeLoginService()
            // [END_EXCLUDE]
        }
    }
    // [END android_di_dagger_android_network_module_okhttp]
}

object DaggerAndroidComponentModules {
    // [START android_di_dagger_android_component_modules]
    // The "modules" attribute in the @Component annotation tells Dagger what Modules
    // to include when building the graph.
    @Component(modules = [NetworkModule::class])
    interface ApplicationComponent {
        // ...
    }
    // [END android_di_dagger_android_component_modules]
}

object DaggerAndroidSingletonScope {
    class LoginViewModel @Inject constructor(
        private val userRepository: UserRepository
    )

    class LoginActivity : Activity() {
        @Inject lateinit var loginViewModel: LoginViewModel
    }

    // [START android_di_dagger_android_singleton_scope]
    @Singleton
    @Component(modules = [NetworkModule::class])
    interface ApplicationComponent {
        fun inject(activity: LoginActivity)
    }

    @Singleton
    class UserRepository @Inject constructor(
        private val localDataSource: UserLocalDataSource,
        private val remoteDataSource: UserRemoteDataSource
    ) { /* ... */ }

    // [START_EXCLUDE silent]
    @DisableInstallInCheck
    // [END_EXCLUDE]
    @Module
    class NetworkModule {
        // Way to scope types inside a Dagger Module.
        @Singleton
        @Provides
        fun provideLoginRetrofitService(): LoginRetrofitService {
            // [START_EXCLUDE]
            return FakeLoginService()
            // [END_EXCLUDE]
        }
    }
    // [END android_di_dagger_android_singleton_scope]
}

object DaggerAndroidLoginComponentEmpty {
    // [START android_di_dagger_android_login_component_empty]
    @Component
    interface LoginComponent {}
    // [END android_di_dagger_android_login_component_empty]
}

object DaggerAndroidLoginComponentInject {
    class LoginActivity : Activity()

    // [START android_di_dagger_android_login_component_inject]
    @Component
    interface LoginComponent {
        fun inject(activity: LoginActivity)
    }
    // [END android_di_dagger_android_login_component_inject]
}

object DaggerAndroidLoginSubcomponent {
    class LoginActivity : Activity()

    // [START android_di_dagger_android_login_subcomponent]
    // @Subcomponent annotation informs Dagger this interface is a Dagger Subcomponent.
    @Subcomponent
    interface LoginComponent {

        // This tells Dagger that LoginActivity requests injection from LoginComponent
        // so that this subcomponent graph needs to satisfy all the dependencies of the
        // fields that LoginActivity is injecting.
        fun inject(loginActivity: LoginActivity)
    }
    // [END android_di_dagger_android_login_subcomponent]
}

object DaggerAndroidLoginSubcomponentFactory {
    class LoginActivity : Activity()

    // [START android_di_dagger_android_login_subcomponent_factory]
    @Subcomponent
    interface LoginComponent {

        // Factory that is used to create instances of this subcomponent.
        @Subcomponent.Factory
        interface Factory {
            fun create(): LoginComponent
        }

        fun inject(loginActivity: LoginActivity)
    }
    // [END android_di_dagger_android_login_subcomponent_factory]
}

@DisableInstallInCheck
// [START android_di_dagger_android_subcomponents_module]
// The "subcomponents" attribute in the @Module annotation tells Dagger what
// Subcomponents are children of the Component this module is included in.
@Module(subcomponents = [LoginComponent::class])
class SubcomponentsModule {}
// [END android_di_dagger_android_subcomponents_module]

object DaggerAndroidComponentWithSubcomponentsModule {
    // [START android_di_dagger_android_component_with_subcomponents_module]
    // Including SubcomponentsModule, tell ApplicationComponent that
    // LoginComponent is its subcomponent.
    @Singleton
    @Component(modules = [NetworkModule::class, SubcomponentsModule::class])
    interface ApplicationComponent {
    }
    // [END android_di_dagger_android_component_with_subcomponents_module]
}

// [START android_di_dagger_android_expose_subcomponent_factory]
@Singleton
@Component(modules = [NetworkModule::class, SubcomponentsModule::class])
interface ApplicationComponent {
    // This function exposes the LoginComponent Factory out of the graph so consumers
    // can use it to obtain new instances of LoginComponent.
    fun loginComponent(): LoginComponent.Factory
}
// [END android_di_dagger_android_expose_subcomponent_factory]

object DaggerAndroidActivityLoginComponentField {
    // [START android_di_dagger_android_activity_login_component_field]
    class LoginActivity : Activity() {
        // Reference to the Login graph.
        lateinit var loginComponent: LoginComponent
        // ...
    }
    // [END android_di_dagger_android_activity_login_component_field]
}

object DaggerAndroidActivityScopeDeclaration {
    // [START android_di_dagger_android_activity_scope]
    // Definition of a custom scope called ActivityScope.
    @Scope
    @Retention(value = AnnotationRetention.RUNTIME)
    annotation class ActivityScope

    // Classes annotated with @ActivityScope are scoped to the graph and the same
    // instance of that type is provided every time the type is requested.
    @ActivityScope
    @Subcomponent
    interface LoginComponent { /* ... */ }

    // A unique instance of LoginViewModel is provided in Components
    // annotated with @ActivityScope.
    @ActivityScope
    class LoginViewModel @Inject constructor(
        private val userRepository: UserRepository
    ) { /* ... */ }
    // [END android_di_dagger_android_activity_scope]
}

object DaggerAndroidSubcomponentFlow {
    class MyDaggerApplication : Application() {
        val appComponent: ApplicationComponent = DaggerApplicationComponent.create()
    }

    // [START android_di_dagger_android_activity_create_subcomponent]
    class LoginActivity : Activity() {
        // Reference to the Login graph.
        lateinit var loginComponent: LoginComponent

        // Fields that need to be injected by the login graph.
        @Inject lateinit var loginViewModel: LoginViewModel

        override fun onCreate(savedInstanceState: Bundle?) {
            // Creation of the login graph using the application graph.
            loginComponent = (applicationContext as MyDaggerApplication)
                .appComponent.loginComponent().create()

            // Make Dagger instantiate @Inject fields in LoginActivity.
            loginComponent.inject(this)

            // Now loginViewModel is available.

            super.onCreate(savedInstanceState)
        }
    }
    // [END android_di_dagger_android_activity_create_subcomponent]

    // [START android_di_dagger_android_login_subcomponent_fragments]
    @ActivityScope
    @Subcomponent
    interface LoginComponent {

        @Subcomponent.Factory
        interface Factory {
            fun create(): LoginComponent
        }

        // All LoginActivity, LoginUsernameFragment and LoginPasswordFragment
        // request injection from LoginComponent. The graph needs to satisfy
        // all the dependencies of the fields those classes are injecting.
        fun inject(loginActivity: LoginActivity)
        fun inject(usernameFragment: LoginUsernameFragment)
        fun inject(passwordFragment: LoginPasswordFragment)
    }
    // [END android_di_dagger_android_login_subcomponent_fragments]

    // [START android_di_dagger_android_login_username_fragment]
    class LoginUsernameFragment : Fragment() {

        // Fields that need to be injected by the login graph.
        @Inject lateinit var loginViewModel: LoginViewModel

        override fun onAttach(context: Context) {
            super.onAttach(context)

            // Obtaining the login graph from LoginActivity and instantiate
            // the @Inject fields with objects from the graph.
            (activity as LoginActivity).loginComponent.inject(this)

            // Now you can access loginViewModel here and onCreateView too
            // (shared instance with the Activity and the other Fragment).
        }
    }
    // [END android_di_dagger_android_login_username_fragment]

    // [START android_di_dagger_android_login_password_fragment]
    class LoginPasswordFragment : Fragment() {

        // Fields that need to be injected by the login graph.
        @Inject lateinit var loginViewModel: LoginViewModel

        override fun onAttach(context: Context) {
            super.onAttach(context)

            (activity as LoginActivity).loginComponent.inject(this)

            // Now you can access loginViewModel here and onCreateView too
            // (shared instance with the Activity and the other Fragment).
        }
    }
    // [END android_di_dagger_android_login_password_fragment]

    private val fakeUserRepository = UserRepository(
        UserLocalDataSource(),
        UserRemoteDataSource(FakeLoginService())
    )

    // [START android_di_dagger_android_unit_test]
    @ActivityScope
    class LoginViewModel @Inject constructor(
        private val userRepository: UserRepository
    ) { /* ... */ }

    class LoginViewModelTest {

        @Test
        fun `Happy path`() {
            // You don't need Dagger to create an instance of LoginViewModel.
            // You can pass a fake or mock UserRepository.
            val viewModel = LoginViewModel(fakeUserRepository)
            // [START_EXCLUDE silent]
            /*
            // [END_EXCLUDE]
            assertEquals(...)
            // [START_EXCLUDE silent]
             */
            assertEquals(viewModel, viewModel)
            // [END_EXCLUDE]
        }
    }
    // [END android_di_dagger_android_unit_test]
}

// [START android_di_dagger_android_test_application_component]
// TestApplicationComponent extends from ApplicationComponent to have them both
// with the same interface methods. You need to include the modules of the
// component here as well, and you can replace the ones you want to override.
// This sample uses FakeNetworkModule instead of NetworkModule.
@Singleton
@Component(modules = [FakeNetworkModule::class, SubcomponentsModule::class])
interface TestApplicationComponent : ApplicationComponent {
}
// [END android_di_dagger_android_test_application_component]

@DisableInstallInCheck
// [START android_di_dagger_android_fake_network_module]
// In the FakeNetworkModule, pass a fake implementation of LoginRetrofitService
// that you can use in your tests.
@Module
class FakeNetworkModule {
    @Provides
    fun provideLoginRetrofitService(): LoginRetrofitService {
        return FakeLoginService()
    }
}
// [END android_di_dagger_android_fake_network_module]

object DaggerAndroidTestApplication {
    open class MyApplication : Application() {
        open val appComponent: ApplicationComponent = DaggerApplicationComponent.create()
    }

    // [START android_di_dagger_android_my_test_application]
    // Your test application needs an instance of the test graph.
    class MyTestApplication : MyApplication() {
        override val appComponent = DaggerTestApplicationComponent.create()
    }
    // [END android_di_dagger_android_my_test_application]
}

object DaggerAndroidModulesIncludes {
    @DisableInstallInCheck
    @Module
    class ModuleX

    // [START android_di_dagger_android_modules_includes]
    @Component(modules = [Module1::class, Module2::class])
    interface ApplicationComponent { /* ... */ }

    // [START_EXCLUDE silent]
    @DisableInstallInCheck
    // [END_EXCLUDE]
    @Module(includes = [ModuleX::class])
    class Module1 { /* ... */ }

    // [START_EXCLUDE silent]
    @DisableInstallInCheck
    // [END_EXCLUDE]
    @Module
    class Module2 { /* ... */ }
    // [END android_di_dagger_android_modules_includes]
}

object DaggerAndroidModulesDuplicateBad {
    @DisableInstallInCheck
    @Module
    class ModuleX

    // [START android_di_dagger_android_modules_duplicate_bad]
    // Bad practice: ModuleX is declared multiple times in this Dagger graph.
    @Component(modules = [Module1::class, Module2::class])
    interface ApplicationComponent { /* ... */ }

    // [START_EXCLUDE silent]
    @DisableInstallInCheck
    // [END_EXCLUDE]
    @Module(includes = [ModuleX::class])
    class Module1 { /* ... */ }

    // [START_EXCLUDE silent]
    @DisableInstallInCheck
    // [END_EXCLUDE]
    @Module(includes = [ModuleX::class])
    class Module2 { /* ... */ }
    // [END android_di_dagger_android_modules_duplicate_bad]
}

object DaggerAndroidModulesRefactorGood {
    @DisableInstallInCheck
    @Module
    class ModuleX

    // [START android_di_dagger_android_modules_refactor_good]
    @Component(modules = [Module1::class, Module2::class, ModuleX::class])
    interface ApplicationComponent { /* ... */ }

    // [START_EXCLUDE silent]
    @DisableInstallInCheck
    // [END_EXCLUDE]
    @Module
    class Module1 { /* ... */ }

    // [START_EXCLUDE silent]
    @DisableInstallInCheck
    // [END_EXCLUDE]
    @Module
    class Module2 { /* ... */ }
    // [END android_di_dagger_android_modules_refactor_good]
}

object DaggerAndroidModulesExtractCommonGood {
    // [START android_di_dagger_android_modules_extract_common_good]
    @Component(modules = [Module1::class, Module2::class, ModuleXCommon::class])
    interface ApplicationComponent { /* ... */ }

    // [START_EXCLUDE silent]
    @DisableInstallInCheck
    // [END_EXCLUDE]
    @Module
    class ModuleXCommon { /* ... */ }

    // [START_EXCLUDE silent]
    @DisableInstallInCheck
    // [END_EXCLUDE]
    @Module
    class ModuleXWithModule1SpecificDependencies { /* ... */ }

    // [START_EXCLUDE silent]
    @DisableInstallInCheck
    // [END_EXCLUDE]
    @Module
    class ModuleXWithModule2SpecificDependencies { /* ... */ }

    // [START_EXCLUDE silent]
    @DisableInstallInCheck
    // [END_EXCLUDE]
    @Module(includes = [ModuleXWithModule1SpecificDependencies::class])
    class Module1 { /* ... */ }

    // [START_EXCLUDE silent]
    @DisableInstallInCheck
    // [END_EXCLUDE]
    @Module(includes = [ModuleXWithModule2SpecificDependencies::class])
    class Module2 { /* ... */ }
    // [END android_di_dagger_android_modules_extract_common_good]
}

interface LoginRetrofitService

internal class FakeLoginService : LoginRetrofitService

private typealias LoginService = LoginRetrofitService

private class Retrofit {
    class Builder {
        fun baseUrl(url: String): Builder = this
        fun build(): Retrofit = Retrofit()
    }

    fun <T> create(service: Class<T>): LoginRetrofitService = FakeLoginService()
}

private annotation class Test

private fun assertEquals(expected: Any?, actual: Any?) {}
