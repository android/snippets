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

import com.example.compose.snippets.architecture.di.DaggerBasicsApplicationGraph.ApplicationGraph
import com.example.compose.snippets.architecture.di.DaggerBasicsCore.UserLocalDataSource
import com.example.compose.snippets.architecture.di.DaggerBasicsCore.UserRemoteDataSource
import com.example.compose.snippets.architecture.di.DaggerBasicsCore.UserRepository
import com.example.compose.snippets.architecture.di.DaggerDaggerBasicsApplicationGraph_ApplicationGraph as DaggerApplicationGraph
import dagger.Component
import javax.inject.Inject
import javax.inject.Scope
import javax.inject.Singleton

private object DaggerBasicsNoInject {
    // [START android_di_dagger_basics_user_repository_no_inject]
    class UserRepository(
        private val localDataSource: UserLocalDataSource,
        private val remoteDataSource: UserRemoteDataSource
    ) { /* ... */ }
    // [END android_di_dagger_basics_user_repository_no_inject]
}

// internal required for Dagger KSP generated Java classes.
internal object DaggerBasicsCore {
    // [START android_di_dagger_basics_user_repository_inject]
    // @Inject lets Dagger know how to create instances of this object.
    class UserRepository @Inject constructor(
        private val localDataSource: UserLocalDataSource,
        private val remoteDataSource: UserRemoteDataSource
    ) { /* ... */ }
    // [END android_di_dagger_basics_user_repository_inject]

    // [START android_di_dagger_basics_data_sources_inject]
    // @Inject lets Dagger know how to create instances of these objects.
    class UserLocalDataSource @Inject constructor() { /* ... */ }
    class UserRemoteDataSource @Inject constructor() { /* ... */ }
    // [END android_di_dagger_basics_data_sources_inject]
}

// internal required for Dagger KSP generated Java classes.
internal object DaggerBasicsApplicationGraph {
    // [START android_di_dagger_basics_application_graph]
    // @Component makes Dagger create a graph of dependencies.
    @Component
    interface ApplicationGraph {
        // The return type  of functions inside the component interface is
        // what can be provided from the container.
        fun repository(): UserRepository
    }
    // [END android_di_dagger_basics_application_graph]
}

private fun createApplicationGraph() {
    // [START android_di_dagger_basics_create_graph]
    // Create an instance of the application graph.
    val applicationGraph: ApplicationGraph = DaggerApplicationGraph.create()
    // Grab an instance of UserRepository from the application graph.
    val userRepository: UserRepository = applicationGraph.repository()
    // [END android_di_dagger_basics_create_graph]
}

private fun verifyUnscopedInstances() {
    // [START android_di_dagger_basics_unscoped_instances]
    val applicationGraph: ApplicationGraph = DaggerApplicationGraph.create()

    val userRepository: UserRepository = applicationGraph.repository()
    val userRepository2: UserRepository = applicationGraph.repository()

    assert(userRepository != userRepository2)
    // [END android_di_dagger_basics_unscoped_instances]
}

// internal required for Dagger KSP generated Java classes.
internal object DaggerBasicsSingletonScope {
    // [START android_di_dagger_basics_singleton_scope]
    // Scope annotations on a @Component interface informs Dagger that classes annotated
    // with this annotation (i.e. @Singleton) are bound to the life of the graph and so
    // the same instance of that type is provided every time the type is requested.
    @Singleton
    @Component
    interface ApplicationGraph {
        fun repository(): UserRepository
    }

    // Scope this class to a component using @Singleton scope (i.e. ApplicationGraph).
    @Singleton
    class UserRepository @Inject constructor(
        private val localDataSource: UserLocalDataSource,
        private val remoteDataSource: UserRemoteDataSource
    ) { /* ... */ }
    // [END android_di_dagger_basics_singleton_scope]
}

// internal required for Dagger KSP generated Java classes.
internal object DaggerBasicsCustomScope {
    class UserService @Inject constructor()

    // [START android_di_dagger_basics_custom_scope_definition]
    // Creates MyCustomScope.
    @Scope
    @MustBeDocumented
    @Retention(value = AnnotationRetention.RUNTIME)
    annotation class MyCustomScope
    // [END android_di_dagger_basics_custom_scope_definition]

    // [START android_di_dagger_basics_custom_scope_usage]
    @MyCustomScope
    @Component
    interface ApplicationGraph {
        fun repository(): UserRepository
    }

    @MyCustomScope
    class UserRepository @Inject constructor(
        private val localDataSource: UserLocalDataSource,
        private val service: UserService
    ) { /* ... */ }
    // [END android_di_dagger_basics_custom_scope_usage]
}

private fun verifyScopedInstances() {
    // [START android_di_dagger_basics_scoped_instances]
    val applicationGraph: ApplicationGraph = DaggerApplicationGraph.create()

    val userRepository: UserRepository = applicationGraph.repository()
    val userRepository2: UserRepository = applicationGraph.repository()

    assert(userRepository == userRepository2)
    // [END android_di_dagger_basics_scoped_instances]
}
