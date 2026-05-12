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
import java.util.logging.Logger
import java.nio.charset.Charset
import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY_SETTER

annotation class Composable
annotation class Test
class Disposable
val mutableInstance = Any()
val mutableInstance2 = Any()
class SomeMutableType
object Joiner {
    fun on(c: Char): Joiner = this
}
@Target(FIELD)
annotation class JvmStatic

private object SingleClassSnippet {
    // [START android_kotlin_style_single_class]
    // MyClass.kt
    class MyClass { }
    // [END android_kotlin_style_single_class]
}

private object SingleClassWithExtensionsSnippet {
    // [START android_kotlin_style_single_class_with_extensions]
    // Bar.kt
    class Bar { }

    fun Runnable.toBar(): Bar = Bar()
    // [END android_kotlin_style_single_class_with_extensions]
}

private object CollectionExtensionsSnippet {
    // [START android_kotlin_style_collection_extensions]
    // Map.kt
    fun <T, O> Set<T>.map(func: (T) -> O): List<O> = emptyList()
    fun <T, O> List<T>.map(func: (T) -> O): List<O> = emptyList()
    // [END android_kotlin_style_collection_extensions]
}

private object ExtensionsSnippet {
    class MyResult

    // [START android_kotlin_style_extensions]
    // extensions.kt
    fun MyClass.process() = { /* ... */ }
    fun MyResult.print() = { /* ... */ }
    // [END android_kotlin_style_extensions]
}

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

fun condition(): Boolean = true
fun foo() {}

fun testNonEmptyBlocks(): Runnable {
    // [START android_kotlin_style_non_empty_blocks]
    return Runnable {
        while (condition()) {
            foo()
        }
    }
    // [END android_kotlin_style_non_empty_blocks]
}

open class MyClass {
    open fun foo() {}
}
class ProblemException : Exception()
fun something() {}
fun recover() {}
fun otherCondition(): Boolean = true
fun somethingElse() {}
fun lastThing() {}

fun testEgyptianBrackets(): Any {
    // [START android_kotlin_style_egyptian_brackets]
    return object : MyClass() {
        override fun foo() {
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
    val value = if (string.isEmpty()) 0 else 1 // Okay
    // [END android_kotlin_style_conditional_expression]
}

fun testConditionalExpression2(string: String) {
    // [START android_kotlin_style_conditional_expression_multiline]
    val value = if (string.isEmpty()) { // Okay
        0
    } else {
        1
    }
    // [END android_kotlin_style_conditional_expression_multiline]
}

// [START android_kotlin_style_function_signature]
fun <T> Iterable<T>.joinToString(
    separator: CharSequence = ", ",
    prefix: CharSequence = "",
    postfix: CharSequence = ""
): String {
    // [START_EXCLUDE]
    return ""
    // [END_EXCLUDE]
}
// [END android_kotlin_style_function_signature]

private object FunctionExpressionSnippet {
    // [START android_kotlin_style_function_egyptian]
    override fun toString(): String {
        return "Hey"
    }
    // [END android_kotlin_style_function_egyptian]
}

private object FunctionExpression2Snippet {
    // [START android_kotlin_style_function_expression]
    override fun toString(): String = "Hey"
    // [END android_kotlin_style_function_expression]
}

class EncodingRegistry {
    companion object {
        fun getInstance(): EncodingRegistry = EncodingRegistry()
    }
    fun getDefaultCharsetForPropertiesFiles(f: File): Charset? = null
}
class PropertyInitializerSnippet {
    val file = File("")
    // [START android_kotlin_style_property_initializer]
    private val defaultCharset: Charset? =
        EncodingRegistry.getInstance().getDefaultCharsetForPropertiesFiles(file)
    // [END android_kotlin_style_property_initializer]
}

private object PropertyAccessorsSnippet {
    // [START android_kotlin_style_property_accessors]
    var directory: File? = null
        set(value) {
            // …
        }
    // [END android_kotlin_style_property_accessors]
}

private object PropertyAccessors2Snippet {
    // [START android_kotlin_style_property_readonly]
    val defaultExtension: String get() = "kt"
    // [END android_kotlin_style_property_readonly]
}

fun testHorizontalSpacing(list: List<Int>, ints: List<Int>, condition: () -> Boolean, it: Any) {
    // [START android_kotlin_style_horizontal_keywords]
    // Okay
    for (i in 0..1) {
    }
    // [END android_kotlin_style_horizontal_keywords]

    if (condition()) {
    // [START android_kotlin_style_horizontal_braces]
    // Okay
    } else {
    }
    // [END android_kotlin_style_horizontal_braces]

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
    it.toString()
    // [END android_kotlin_style_horizontal_dot_separator]

    // [START android_kotlin_style_horizontal_range_operator]
    // Okay
    for (i in 1..4) {
      print(i)
    }
    // [END android_kotlin_style_horizontal_range_operator]
}

private object BaseClassSpacingSnippet {
    // [START android_kotlin_style_horizontal_base_class]
    // Okay
    class Foo : Runnable
    // [END android_kotlin_style_horizontal_base_class]
    {
        override fun run() {}
    }
}

private object GenericSpacingSnippet {
    // [START android_kotlin_style_horizontal_generic]
    // Okay
    fun <T : Comparable<T>> max(a: T, b: T)
    // [END android_kotlin_style_horizontal_generic]
    {
        // ...
    }
}

private object WhereSpacingSnippet {
    // [START android_kotlin_style_horizontal_where]
    // Okay
    fun <T> max(a: T, b: T) where T : Comparable<T> {}
    // [END android_kotlin_style_horizontal_where]
}

private object CommaSpacingSnippet {
    // [START android_kotlin_style_horizontal_comma]
    // Okay
    val oneAndTwo = listOf(1, 2)
    // [END android_kotlin_style_horizontal_comma]
}

private object InterfaceSpacingSnippet {
    // [START android_kotlin_style_horizontal_interface]
    // Okay
    class Foo : Runnable
    // [END android_kotlin_style_horizontal_interface]
    {
        override fun run() {}
    }
}

private object DoubleSlashSpacingSnippet {
    // [START android_kotlin_style_horizontal_double_slash]
    // Okay
    var debugging = false // disabled by default
    // [END android_kotlin_style_horizontal_double_slash]
}

// [START android_kotlin_style_enum_single]
enum class Answer { YES, NO, MAYBE }
// [END android_kotlin_style_enum_single]

private object EnumMultiSnippet {
    // [START android_kotlin_style_enum_multi]
    enum class Answer {
        YES,
        NO,

        MAYBE {
            override fun toString() = """¯\_(ツ)_/¯"""
        }
    }
    // [END android_kotlin_style_enum_multi]
}

// [START android_kotlin_style_annotations_separate]
@Retention(SOURCE)
@Target(FUNCTION, PROPERTY_SETTER, FIELD)
annotation class Global
// [END android_kotlin_style_annotations_separate]

private object AnnotationsSingleLineSnippet {
    // [START android_kotlin_style_annotations_single_line]
    @JvmField @Volatile
    var disposable: Disposable? = null
    // [END android_kotlin_style_annotations_single_line]
}

private object AnnotationsSingleDeclarationSnippet {
    // [START android_kotlin_style_annotations_single_declaration]
    @Volatile var disposable: Disposable? = null

    @Test fun selectAll() {
        // …
    }
    // [END android_kotlin_style_annotations_single_declaration]
}

private object AnnotationsUseSiteSnippet {
    // [START android_kotlin_style_annotations_use_site]
    @field:[JvmStatic Volatile]
    var disposable: Disposable? = null
    // [END android_kotlin_style_annotations_use_site]
}

private object TestNamingSnippet {
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

private object ConstantsSnippet {
    // [START android_kotlin_style_constants]
    const val NUMBER = 5
    val NAMES = listOf("Alice", "Bob")
    val AGES = mapOf("Alice" to 35, "Bob" to 32)
    val COMMA_JOINED = NAMES.joinToString(", ")
    val EMPTY_ARRAY = arrayOf<SomeMutableType>()
    // [END android_kotlin_style_constants]
}

private object NonConstantsSnippet {
    // [START android_kotlin_style_non_constants]
    val variable = "var"
    val nonConstScalar = "non-const"
    val mutableCollection: MutableSet<String> = HashSet()
    val mutableElements = listOf(mutableInstance)
    val mutableValues = mapOf("Alice" to mutableInstance, "Bob" to mutableInstance2)
    val logger = Logger.getLogger(MyClass::class.java.name)
    val nonEmptyArray = arrayOf("these", "can", "change")
    // [END android_kotlin_style_non_constants]
}

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

private object KDocSnippet {
    // [START android_kotlin_style_kdoc_block]
    /**
     * Multiple lines of KDoc text are written here,
     * wrapped normally…
     */
    fun method(arg: String) {
        // …
    }
    // [END android_kotlin_style_kdoc_block]
}

private object KDocSingleSnippet {
    // [START android_kotlin_style_kdoc_single]
    /** An especially short bit of KDoc. */
    // [END android_kotlin_style_kdoc_single]
}
