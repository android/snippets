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

import java.io.File

annotation class Composable
annotation class Test

// [START android_kotlin_style_single_class]
class MyClassStyle { }
// [END android_kotlin_style_single_class]

// [START android_kotlin_style_single_class_with_extensions]
class BarStyle { }
fun Runnable.toBarStyle(): BarStyle = BarStyle()
// [END android_kotlin_style_single_class_with_extensions]

// [START android_kotlin_style_collection_extensions]
fun <T, O> Set<T>.map(func: (T) -> O): List<O> = emptyList()
fun <T, O> List<T>.map(func: (T) -> O): List<O> = emptyList()
// [END android_kotlin_style_collection_extensions]

// [START android_kotlin_style_extensions]
fun MyClassStyle.process() {}
class MyResultStyle
fun MyResultStyle.print() {}
// [END android_kotlin_style_extensions]

fun testBraces(string: String, DEFAULT_VALUE: String, value: Int) {
    // [START android_kotlin_style_conditional_no_braces]
    if (string.isEmpty()) return

    val result =
        if (string.isEmpty()) DEFAULT_VALUE else string

    when (value) {
        0 -> return
        // …
    }
    // [END android_kotlin_style_conditional_no_braces]
}

fun testBracesRequired(string: String, otherParametersHere: String) {
    // [START android_kotlin_style_braces_required]
    if (string.isEmpty()) {
        return  // Okay
    }

    if (string.isEmpty()) {
        return  // Okay
    } else {
        doLotsOfProcessingOn(string, otherParametersHere)
    }
    // [END android_kotlin_style_braces_required]
}
fun doLotsOfProcessingOn(a: String, b: String) {}

fun condition(): Boolean = true
fun foo() {}

fun testNonEmptyBlocks() {
    // [START android_kotlin_style_non_empty_blocks]
    val r = Runnable {
        while (condition()) {
            foo()
        }
    }
    // [END android_kotlin_style_non_empty_blocks]
}

open class MyClassBlock
class ProblemException : Exception()
fun something() {}
fun recover() {}
fun otherCondition(): Boolean = true
fun somethingElse() {}
fun lastThing() {}

fun testEgyptianBrackets() {
    // [START android_kotlin_style_egyptian_brackets]
    val obj = object : MyClassBlock() {
        fun foo() {
            if (condition()) {
                try {
                    something()
                } catch (e: ProblemException) {
                    recover()
                }
            } else if (otherCondition()) {
                somethingElse()
            } else {
                lastThing()
            }
        }
    }
    // [END android_kotlin_style_egyptian_brackets]
}

fun doSomething() {}

fun testEmptyBlocks() {
    // [START android_kotlin_style_empty_blocks]
    try {
        doSomething()
    } catch (e: Exception) {
    } // Okay
    // [END android_kotlin_style_empty_blocks]
}

fun testConditionalExpression(string: String) {
    // [START android_kotlin_style_conditional_expression]
    val value = if (string.isEmpty()) 0 else 1  // Okay
    // [END android_kotlin_style_conditional_expression]

    // [START android_kotlin_style_conditional_expression_multiline]
    val value2 = if (string.isEmpty()) { // Okay
        0
    } else {
        1
    }
    // [END android_kotlin_style_conditional_expression_multiline]
}

// [START android_kotlin_style_function_signature]
fun <T> Iterable<T>.joinToStringStyle(
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = ""
): String {
    return ""
}
// [END android_kotlin_style_function_signature]

class FunctionExpression {
    // [START android_kotlin_style_function_egyptian]
    override fun toString(): String {
        return "Hey"
    }
    // [END android_kotlin_style_function_egyptian]
}

class FunctionExpression2 {
    // [START android_kotlin_style_function_expression]
    override fun toString(): String = "Hey"
    // [END android_kotlin_style_function_expression]
}

class FileProperties
class EncodingRegistry {
    companion object {
        fun getInstance(): EncodingRegistry = EncodingRegistry()
    }
    fun getDefaultCharsetForPropertiesFiles(f: File): String = ""
}

class PropertyInitializer {
    val file = File("")
    // [START android_kotlin_style_property_initializer]
    private val defaultCharset: String? =
        EncodingRegistry.getInstance().getDefaultCharsetForPropertiesFiles(file)
    // [END android_kotlin_style_property_initializer]
}

class PropertyAccessors {
    // [START android_kotlin_style_property_accessors]
    var directory: File? = null
        set(value) {
            // …
        }
    // [END android_kotlin_style_property_accessors]

    // [START android_kotlin_style_property_readonly]
    val defaultExtension: String get() = "kt"
    // [END android_kotlin_style_property_readonly]
}

fun testHorizontalSpacing(list: List<Int>, ints: List<Int>, condition: () -> Boolean) {
    // [START android_kotlin_style_horizontal_keywords]
    // Okay
    for (i in 0..1) {
    }
    // [END android_kotlin_style_horizontal_keywords]

    if (condition()) {
    // [START android_kotlin_style_horizontal_braces]
    // Okay
    } else {
    // [END android_kotlin_style_horizontal_braces]
    }

    // [START android_kotlin_style_horizontal_curly_braces]
    // Okay
    if (list.isEmpty()) {
    }
    // [END android_kotlin_style_horizontal_curly_braces]

    // [START android_kotlin_style_horizontal_operators]
    // Okay
    val two = 1 + 1
    // [END android_kotlin_style_horizontal_operators]

    // [START android_kotlin_style_horizontal_lambda_arrow]
    // Okay
    ints.map { value -> value.toString() }
    // [END android_kotlin_style_horizontal_lambda_arrow]

    // [START android_kotlin_style_horizontal_member_reference]
    // Okay
    val toString = Any::toString
    // [END android_kotlin_style_horizontal_member_reference]

    // [START android_kotlin_style_horizontal_dot_separator]
    // Okay
    val it = Any()
    it.toString()
    // [END android_kotlin_style_horizontal_dot_separator]

    // [START android_kotlin_style_horizontal_range_operator]
    // Okay
    for (i in 1..4) {
      print(i)
    }
    // [END android_kotlin_style_horizontal_range_operator]
}

// [START android_kotlin_style_horizontal_base_class]
// Okay
class FooStyle : Runnable {
    override fun run() {}
}
// [END android_kotlin_style_horizontal_base_class]

class ColonSpacingGeneric {
    // [START android_kotlin_style_horizontal_generic]
    // Okay
    fun <T : Comparable<T>> max(a: T, b: T) {}
    // [END android_kotlin_style_horizontal_generic]

    // [START android_kotlin_style_horizontal_where]
    // Okay
    fun <T> max2(a: T, b: T) where T : Comparable<T> {}
    // [END android_kotlin_style_horizontal_where]
}

class CommaSpacing {
    // [START android_kotlin_style_horizontal_comma]
    // Okay
    val oneAndTwo = listOf(1, 2)
    // [END android_kotlin_style_horizontal_comma]
}

// [START android_kotlin_style_horizontal_interface]
// Okay
class FooStyle2 : Runnable {
    override fun run() {}
}
// [END android_kotlin_style_horizontal_interface]

class DoubleSlashSpacing {
    // [START android_kotlin_style_horizontal_double_slash]
    // Okay
    var debugging = false // disabled by default
    // [END android_kotlin_style_horizontal_double_slash]
}

// [START android_kotlin_style_enum_single]
enum class AnswerSingle { YES, NO, MAYBE }
// [END android_kotlin_style_enum_single]

// [START android_kotlin_style_enum_multi]
enum class AnswerMulti {
    YES,
    NO,

    MAYBE {
        override fun toString() = """¯\_(ツ)_/¯"""
    }
}
// [END android_kotlin_style_enum_multi]

// [START android_kotlin_style_annotations_separate]
@Retention(AnnotationRetention.SOURCE)
@Target(AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY_SETTER, AnnotationTarget.FIELD)
annotation class GlobalStyle
// [END android_kotlin_style_annotations_separate]

class AnnotationsSingle {
    // [START android_kotlin_style_annotations_single_line]
    @JvmField @Volatile
    var disposable: Any? = null
    // [END android_kotlin_style_annotations_single_line]

    // [START android_kotlin_style_annotations_single_declaration]
    @Volatile var disposable2: Any? = null

    @Test fun selectAll() {
        // …
    }
    // [END android_kotlin_style_annotations_single_declaration]

    // [START android_kotlin_style_annotations_use_site]
    @field:[Volatile]
    var disposable3: Any? = null
    // [END android_kotlin_style_annotations_use_site]
}

class ImplicitReturn {
    // [START android_kotlin_style_implicit_return]
    override fun toString() = "Hey"
    // [END android_kotlin_style_implicit_return]

    // [START android_kotlin_style_implicit_property]
    private val ICON = "icon"
    // [END android_kotlin_style_implicit_property]
}

class TestNaming {
    // [START android_kotlin_style_test_naming]
    @Test fun pop_emptyStack() {
        // …
    }
    // [END android_kotlin_style_test_naming]
}

// [START android_kotlin_style_composable_naming]
@Composable
fun NameTag(name: String) {
    // …
}
// [END android_kotlin_style_composable_naming]

class ComposableNaming {
    // [START android_kotlin_style_camelcase_naming]
    fun testEveryPossibleCase() {}
    // [END android_kotlin_style_camelcase_naming]
}

class Constants {
    // [START android_kotlin_style_constants]
    val NUMBER = 5
    val NAMES = listOf("Alice", "Bob")
    val AGES = mapOf("Alice" to 35, "Bob" to 32)
    // [END android_kotlin_style_constants]
}

class NonConstants {
    // [START android_kotlin_style_non_constants]
    val variable = "var"
    val nonConstScalar = "non-const"
    val mutableCollection: MutableSet<String> = HashSet()
    // [END android_kotlin_style_non_constants]
}

class BackingProperties {
    // [START android_kotlin_style_backing_properties]
    private var _table: Map<String, Int>? = null

    val table: Map<String, Int>
        get() {
            if (_table == null) {
                _table = HashMap()
            }
            return _table ?: throw AssertionError()
        }
    // [END android_kotlin_style_backing_properties]
}

class KDoc {
    // [START android_kotlin_style_kdoc_block]
    /**
     * Multiple lines of KDoc text are written here,
     * wrapped normally…
     */
    fun method(arg: String) {
        // …
    }
    // [END android_kotlin_style_kdoc_block]

    // [START android_kotlin_style_kdoc_single]
    /** An especially short bit of KDoc. */
    // [END android_kotlin_style_kdoc_single]
}
