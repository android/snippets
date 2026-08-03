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

package com.example.android.basics

// [START android_kotlin_adopt_person]
data class Person(var firstName: String?, var lastName: String?)
// [END android_kotlin_adopt_person]

fun String.foo(): String = this

fun testExtensionFunction() {
    // [START android_kotlin_adopt_foo]
    // [START_EXCLUDE]
    val placeholderBefore = ""
    // [END_EXCLUDE]

    val myString: String = "hello"
    val fooString = myString.foo()

    // [START_EXCLUDE]
    val placeholderAfter = ""
    // [END_EXCLUDE]
    // [END android_kotlin_adopt_foo]
}

class Foo {
    fun baz() {}
    fun zap() {}
}

fun testLet() {
    // [START android_kotlin_adopt_let]
    val nullableFoo: Foo? = Foo()

    // This lambda executes only if nullableFoo is not null
    // and `foo` is of the non-nullable Foo type
    nullableFoo?.let { foo ->
        foo.baz()
        foo.zap()
    }
    // [END android_kotlin_adopt_let]
}

fun testSmartCast() {
    // [START android_kotlin_adopt_smartcast]
    val nullableFoo: Foo? = null
    if (nullableFoo != null) {
        nullableFoo.baz() // Using !! or ?. isn't required; the Kotlin compiler infers non-nullability
        nullableFoo.zap() // from guard condition; smart casts nullableFoo to Foo inside this block
    }
    // [END android_kotlin_adopt_smartcast]
}
