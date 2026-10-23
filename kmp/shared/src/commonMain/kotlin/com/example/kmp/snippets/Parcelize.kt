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

package com.example.kmp.snippets

// [START android_kotlin_parcelize_multiplatform_common]
// Common code
// package example

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
// No `expect` keyword here
annotation class MyParcelize()

expect interface MyParcelable

@Target(AnnotationTarget.PROPERTY)
@Retention(AnnotationRetention.SOURCE)
expect annotation class MyIgnoredOnParcel()

@MyParcelize
class MyClass(
    val x: String,
    @MyIgnoredOnParcel val y: String = ""
) : MyParcelable
// [END android_kotlin_parcelize_multiplatform_common]
