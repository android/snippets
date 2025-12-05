/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.android.kotlin.style

import android.graphics.drawable.Icon
import androidx.compose.runtime.Composable
import java.io.File
import java.nio.charset.Charset
import java.util.logging.Logger
import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.FIELD
import kotlin.annotation.AnnotationTarget.FUNCTION
import kotlin.annotation.AnnotationTarget.PROPERTY_SETTER

private object StyleGuideSourceFiles {

    class MyResult()

    // [START android_style_guide_naming_basic]
    // MyClass.kt
    class MyClass {}
    // [END android_style_guide_naming_basic]


    // [START android_style_guide_naming_extending]
    // Bar.kt
    class Bar {}

    fun Runnable.toBar(): Bar = // [START_EXCLUDE]
        Bar()
    // [END_EXCLUDE]
    // [END android_style_guide_naming_extending]


    // [START android_style_guide_naming_map]
    // Map.kt
    fun <T, O> Set<T>.map(func: (T) -> O): List<O> = // [START_EXCLUDE]
        emptyList()

    // [END_EXCLUDE]
    fun <T, O> List<T>.map(func: (T) -> O): List<O> = // [START_EXCLUDE]
        emptyList()
    // [END_EXCLUDE]
    // [END android_style_guide_naming_map]

    // [START android_style_guide_naming_extensions]
    // extensions.kt
    fun MyClass.process() = // [START_EXCLUDE]
        Unit

    // [END_EXCLUDE]
    fun MyResult.print() = // [START_EXCLUDE]
        print("")
    // [END_EXCLUDE]
    // [START android_style_guide_naming_extensions]
}

private object StyleGuideFormattingBraces {
    private const val DEFAULT_VALUE = ""

    private fun styleGuideFormattingBracesSingleLine(string: String, value: Int) {

        // [START android_style_guide_formatting_braces_single_line]
        if (string.isEmpty()) return

        val result =
            if (string.isEmpty()) DEFAULT_VALUE else string

        when (value) {
            0 -> return
            // [START_EXCLUDE]
            else -> return
            // [END_EXCLUDE]
        }
        // [END android_style_guide_formatting_braces_single_line]
    }


    private fun styleGuideFormattingBracesMultiLine(string: String, otherParametersHere: Int) {

        // [START android_style_guide_formatting_braces_multi_line]
        if (string.isEmpty())
            return  // WRONG!

        if (string.isEmpty()) {
            return  // Okay
        }

        if (string.isEmpty()) return  // WRONG
        else doLotsOfProcessingOn(string, otherParametersHere)

        if (string.isEmpty()) {
            return  // Okay
        } else {
            doLotsOfProcessingOn(string, otherParametersHere)
        }
        // [END android_style_guide_formatting_braces_multi_line]
    }

    private fun doLotsOfProcessingOn(string: String, otherParametersHere: Int) {}

    private object StyleGuideFormattingBracesNonEmptyBlocks {

        open class MyClass {
            open fun foo() {}
        }

        private fun styleGuideFormattingBracesNonEmptyBlocksRunnable(): Runnable {
            // [START android_style_guide_formatting_braces_non_empty]
            return Runnable {
                while (condition()) {
                    foo()
                }
            }
            // [START_EXCLUDE silent]
        }

        private fun styleGuideFormattingBracesNonEmptyBlocksMyClass(): MyClass {
            // [END_EXCLUDE silent]

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
            // [END android_style_guide_formatting_braces_non_empty]
        }

        class ProblemException : Exception()

        private fun condition(): Boolean = false
        private fun foo() {}
        private fun something() {}
        private fun recover() {}
        private fun otherCondition(): Boolean = true
        private fun somethingElse() {}
        private fun lastThing() {}
    }

    private fun doSomething() {}
    private fun styleGuideFormattingBracesEmptyBlocks() {
        // [START android_style_guide_formatting_braces_empty_wrong]
        try {
            doSomething()
        } catch (e: Exception) {
        } // WRONG!
        // [END android_style_guide_formatting_braces_empty_wrong]

        // [START android_style_guide_formatting_braces_empty_okay]
        try {
            doSomething()
        } catch (e: Exception) {
        } // Okay
        // [END android_style_guide_formatting_braces_empty_okay]
    }

    private fun styleGuideFormattingBracesExpressionsSingle(string: String) {
        // [START android_style_guide_formatting_braces_expressions_single]
        val value = if (string.isEmpty()) 0 else 1  // Okay
        // [END android_style_guide_formatting_braces_expressions_single]
    }

    private fun styleGuideFormattingBracesExpressionsMultiWrong(string: String) {
        // [START android_style_guide_formatting_braces_expressions_multi_wrong]
        val value = if (string.isEmpty())  // WRONG!
            0
        else
            1
        // [END android_style_guide_formatting_braces_expressions_multi_wrong]
    }

    private fun styleGuideFormattingBracesExpressionsMultiOkay(string: String) {
        // [START android_style_guide_formatting_braces_expressions_multi_okay]
        val value = if (string.isEmpty()) { // Okay
            0
        } else {
            1
        }
        // [END android_style_guide_formatting_braces_expressions_multi_okay]
    }

    private object StyleGuideFormattingBracesFunctions {

        // [START android_style_guide_formatting_braces_functions]
        fun <T> Iterable<T>.joinToString(
            separator: CharSequence = ", ",
            prefix: CharSequence = "",
            postfix: CharSequence = ""
        ): String {
            // [START_EXCLUDE]
            return ""
            // [END_EXCLUDE]
        }
        // [END android_style_guide_formatting_braces_functions]

        private object ExpressionFunctionMulti {
            // [START android_style_guide_formatting_braces_expression_functions_multi]
            override fun toString(): String {
                return "Hey"
            }
            // [END android_style_guide_formatting_braces_expression_functions_multi]
        }

        private object ExpressionFunctionSingle {
            // [START android_style_guide_formatting_braces_expression_functions_single]
            override fun toString(): String = "Hey"
            // [END android_style_guide_formatting_braces_expression_functions_single]
        }
    }

    private class StyleGuideFormattingBracesProperties(file: File) {
        private class EncodingRegistry {
            fun getDefaultCharsetForPropertiesFiles(file: File): Charset? = null

            companion object {
                fun getInstance(): EncodingRegistry = EncodingRegistry()
            }
        }

        // [START android_style_guide_formatting_braces_properties_single]
        private val defaultCharset: Charset? =
            EncodingRegistry.getInstance().getDefaultCharsetForPropertiesFiles(file)
        // [END android_style_guide_formatting_braces_properties_single]

        // [START android_style_guide_formatting_braces_properties_set]
        var directory: File? = null
            set(value) {
                // [START_EXCLUDE]
                print("")
                // [END_EXCLUDE]
            }
        // [END android_style_guide_formatting_braces_properties_set]

        // [START android_style_guide_formatting_braces_properties_read_only]
        val defaultExtension: String get() = "kt"
        // [END android_style_guide_formatting_braces_properties_read_only]
    }
}

private object StyleGuideFormattingWhitespace {
    fun styleGuideFormattingWhitespaceWrong(list: List<String>, ints: List<Int>, it: Int) {
        // [START android_style_guide_formatting_whitespace_for_wrong]
        // WRONG!
        for (i in 0..1) {
        }
        // [END android_style_guide_formatting_whitespace_for_wrong]

        if (true) {
            // [START android_style_guide_formatting_whitespace_else_wrong]
            // WRONG!
        } else {
        }
        // [END android_style_guide_formatting_whitespace_else_wrong]

        // [START android_style_guide_formatting_whitespace_curly_wrong]
        // WRONG!
        if (list.isEmpty()) {
        }
        // [END android_style_guide_formatting_whitespace_curly_wrong]

        // [START android_style_guide_formatting_whitespace_binary_wrong]
        // WRONG!
        val two = 1 + 1
        // [END android_style_guide_formatting_whitespace_binary_wrong]

        // [START android_style_guide_formatting_whitespace_operator_wrong]
        // WRONG!
        ints.map { value -> value.toString() }
        // [END android_style_guide_formatting_whitespace_operator_wrong]

        // [START android_style_guide_formatting_whitespace_colons_wrong]
        // WRONG!
        val toString = Any::toString
        // [END android_style_guide_formatting_whitespace_colons_wrong]

        // [START android_style_guide_formatting_whitespace_dot_wrong]
        // WRONG
        it.toString()
        // [END android_style_guide_formatting_whitespace_dot_wrong]

        // [START android_style_guide_formatting_whitespace_range_wrong]
        // WRONG
        for (i in 1..4) {
            print(i)
        }
        // [END android_style_guide_formatting_whitespace_range_wrong]

        // [START android_style_guide_formatting_whitespace_colon_class_wrong]
        // WRONG!
        class Foo : Runnable
        // [END android_style_guide_formatting_whitespace_colon_class_wrong]
        {
            override fun run() {
                TODO("Not yet implemented")
            }
        }

        object {
            // [START android_style_guide_formatting_whitespace_colon_generic_wrong]
            // WRONG
            fun <T : Comparable<T>> max(a: T, b: T)
            // [END android_style_guide_formatting_whitespace_colon_generic_wrong]
            {
            }
        }

        // [START android_style_guide_formatting_whitespace_colon_generic_where_wrong]
        // WRONG
        fun <T> max(a: T, b: T) where T : Comparable<T>
        // [END android_style_guide_formatting_whitespace_colon_generic_where_wrong]
        {
        }

        // [START android_style_guide_formatting_whitespace_comma_val_wrong]
        // WRONG!
        val oneAndTwo = listOf(1, 2)
        // [END android_style_guide_formatting_whitespace_comma_val_wrong]
    }

    fun styleGuideFormattingWhitespaceOkay(list: List<String>, ints: List<Int>, it: Int) {
        // [START android_style_guide_formatting_whitespace_for_okay]
        // Okay
        for (i in 0..1) {
        }
        // [END android_style_guide_formatting_whitespace_for_okay]

        if (true) {
            // [START android_style_guide_formatting_whitespace_else_okay]
            // Okay
        } else {
        }
        // [END android_style_guide_formatting_whitespace_else_okay]

        // [START android_style_guide_formatting_whitespace_curly_okay]
        // Okay
        if (list.isEmpty()) {
        }
        // [END android_style_guide_formatting_whitespace_curly_okay]

        // [START android_style_guide_formatting_whitespace_colons_okay]
        // Okay
        val toString = Any::toString
        // [END android_style_guide_formatting_whitespace_colons_okay]

        // [START android_style_guide_formatting_whitespace_binary_okay]
        // Okay
        val two = 1 + 1
        // [END android_style_guide_formatting_whitespace_binary_okay]

        // [START android_style_guide_formatting_whitespace_operator_okay]
        // Okay
        ints.map { value -> value.toString() }
        // [END android_style_guide_formatting_whitespace_operator_okay]

        // [START android_style_guide_formatting_whitespace_dot_okay]
        // Okay
        it.toString()
        // [END android_style_guide_formatting_whitespace_dot_okay]

        // [START android_style_guide_formatting_whitespace_range_okay]
        // Okay
        for (i in 1..4) {
            print(i)
        }
        // [END android_style_guide_formatting_whitespace_range_okay]

        // [START android_style_guide_formatting_whitespace_colon_class_okay]
        // Okay
        class Foo : Runnable
        // [END android_style_guide_formatting_whitespace_colon_class_okay]
        {
            override fun run() {
                TODO("Not yet implemented")
            }
        }

        object {
            // [START android_style_guide_formatting_whitespace_colon_generic_okay]
            // Okay
            fun <T : Comparable<T>> max(a: T, b: T)
            // [END android_style_guide_formatting_whitespace_colon_generic_okay]
            {
            }
        }

        // [START android_style_guide_formatting_whitespace_colon_generic_where_okay]
        // Okay
        fun <T> max(a: T, b: T) where T : Comparable<T>
        // [END android_style_guide_formatting_whitespace_colon_generic_where_okay]
        {
        }

        // [START android_style_guide_formatting_whitespace_comma_val_okay]
        // Okay
        val oneAndTwo = listOf(1, 2)
        // [END android_style_guide_formatting_whitespace_comma_val_okay]

    }

    object StyleGuideFormattingWhitespaceWrong {
        // [START android_style_guide_formatting_comma_class_wrong]
        // WRONG!
        class Foo : Runnable
        // [END android_style_guide_formatting_comma_class_wrong]
        {
            override fun run() {
                TODO("Not yet implemented")
            }
        }

        // [START android_style_guide_formatting_double_slash_wrong]
        // WRONG!
        var debugging = false//disabled by default
        // [END android_style_guide_formatting_double_slash_wrong]
    }

    object StyleGuideFormattingWhitespaceOkay {
        // [START android_style_guide_formatting_comma_class_okay]
        // Okay
        class Foo : Runnable
        // [END android_style_guide_formatting_comma_class_okay]
        {
            override fun run() {
                TODO("Not yet implemented")
            }
        }

        // [START android_style_guide_formatting_double_slash_okay]
        // Okay
        var debugging = false // disabled by default
        // [END android_style_guide_formatting_double_slash_okay]
    }
}

private object StyleGuideFormattingSpecificConstructs {
    private object SpecificConstructsEnum {
        // [START android_style_guide_formatting_special_constructs_enum]
        enum class Answer { YES, NO, MAYBE }
        // [END android_style_guide_formatting_special_constructs_enum]
    }


    private class SpecificConstructsEnumSeparate {
        // [START android_style_guide_formatting_special_constructs_enum_separate]
        enum class Answer {
            YES,
            NO,

            MAYBE {
                override fun toString() = """¯\_(ツ)_/¯"""
            }
        }
        // [END android_style_guide_formatting_special_constructs_enum_separate]
    }


    // [START android_style_guide_formatting_special_constructs_annotations_construct]
    @Retention(SOURCE)
    @Target(FUNCTION, PROPERTY_SETTER, FIELD)
    annotation class Global
    // [END android_style_guide_formatting_special_constructs_annotations_construct]

    private class SpecificConstructsAnnotations {
        // [START android_style_guide_formatting_special_constructs_annotations_var]
        @JvmField
        @Volatile
        var disposable: Disposable? = null
        // [END android_style_guide_formatting_special_constructs_annotations_var]
    }

    private class SpecificConstructsAnnotationSingle {
        // [START android_style_guide_formatting_special_constructs_annotations_single]
        @field:[JvmField Volatile]
        var disposable: Disposable? = null
        // [END android_style_guide_formatting_special_constructs_annotations_single]
    }

    private object ImplicitReturnPropertyBefore {
        // [START android_style_guide_formatting_special_constructs_implicit_string]
        override fun toString(): String = "Hey"
        // [END android_style_guide_formatting_special_constructs_implicit_string]

        // [START android_style_guide_formatting_special_constructs_implicit_icon]
        private val ICON: Icon = IconLoader.getIcon("/icons/kotlin.png")
        // [END android_style_guide_formatting_special_constructs_implicit_icon]

    }

    private object ImplicitReturnPropertyAfter {
        // [START android_style_guide_formatting_special_constructs_implicit_string_after]
        // becomes
        override fun toString() = "Hey"
        // [END android_style_guide_formatting_special_constructs_implicit_string_after]

        // [START android_style_guide_formatting_special_constructs_implicit_icon_after]
        // becomes
        private val ICON = IconLoader.getIcon("/icons/kotlin.png")
        // [END android_style_guide_formatting_special_constructs_implicit_icon_after]
    }

    object IconLoader {
        fun getIcon(location: String): Icon = TODO()
    }
}

private object StyleGuideFormattingNaming {
    // [START android_style_guide_formatting_naming_composable]
    @Composable
    fun NameTag(name: String) {
        // [START_EXCLUDE]
        print("")
        // [END_EXCLUDE]
    }
// [END android_style_guide_formatting_naming_composable]

    object ConstantNames {

        object Joiner {
            fun on(char: Char) {}
        }

        class MyClass

        private val mutableInstance = ""
        private val mutableInstance2 = ""

        // [START android_style_guide_formatting_naming_constants]
        const val NUMBER = 5
        val NAMES = listOf("Alice", "Bob")
        val AGES = mapOf("Alice" to 35, "Bob" to 32)
        val COMMA_JOINER = Joiner.on(',') // Joiner is immutable
        val EMPTY_ARRAY = emptyArray<String>()
        // [END android_style_guide_formatting_naming_constants]

        // [START android_style_guide_formatting_naming_non_constants]
        val variable = "var"
        val nonConstScalar = "non-const"
        val mutableCollection: MutableSet<String> = HashSet()
        val mutableElements = listOf(mutableInstance)
        val mutableValues = mapOf("Alice" to mutableInstance, "Bob" to mutableInstance2)
        val logger = Logger.getLogger(MyClass::class.java.name)
        val nonEmptyArray = arrayOf("these", "can", "change")
        // [END android_style_guide_formatting_naming_non_constants]


        // [START android_style_guide_formatting_naming_backing]
        private var _table: Map<String, Int>? = null

        val table: Map<String, Int>
            get() {
                if (_table == null) {
                    _table = HashMap()
                }
                return _table ?: throw AssertionError()
            }
        // [END android_style_guide_formatting_naming_backing]
    }
}

class Disposable