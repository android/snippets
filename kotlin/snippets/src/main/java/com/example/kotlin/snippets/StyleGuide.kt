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

package com.example.kotlin.snippets

@file:Suppress("unused", "UNUSED_VARIABLE", "UNUSED_PARAMETER", "UnusedReceiverParameter", "SpellCheckingInspection", "NonAsciiCharacters")

// [START android_kotlin_style_guide_1]
// MyClass.kt
class MyClass { }
// [END android_kotlin_style_guide_1]

// [START android_kotlin_style_guide_2]
// Bar.kt
class Bar { }
fun Runnable.toBar(): Bar =
// [START_EXCLUDE]
    Bar()
// [END_EXCLUDE]
// [END android_kotlin_style_guide_2]

// [START android_kotlin_style_guide_3]
// Map.kt
fun <T, O> Set<T>.map(func: (T) -> O): List<O> =
// [START_EXCLUDE]
    emptyList()
// [END_EXCLUDE]
fun <T, O> List<T>.map(func: (T) -> O): List<O> =
// [START_EXCLUDE]
    emptyList()
// [END_EXCLUDE]
// [END android_kotlin_style_guide_3]

// [START_EXCLUDE silent]
class MyResult { }
// [END_EXCLUDE]

// [START android_kotlin_style_guide_4]
// extensions.kt
fun MyClass.process() =
// [START_EXCLUDE]
    Unit
// [END_EXCLUDE]
fun MyResult.print() =
// [START_EXCLUDE]
    Unit
// [END_EXCLUDE]
// [END android_kotlin_style_guide_4]

// [START android_kotlin_style_guide_5]
/*
 * Copyright 2017 Google, Inc.
 *
 * ...
 */
// [END android_kotlin_style_guide_5]

// [START android_kotlin_style_guide_6]
/**
 * Copyright 2017 Google, Inc.
 *
 * ...
 */
// [END android_kotlin_style_guide_6]

// [START android_kotlin_style_guide_7]
// Copyright 2017 Google, Inc.
//
// ...
// [END android_kotlin_style_guide_7]

// [START_EXCLUDE silent]
private val string = ""
private val DEFAULT_VALUE = ""
private val value = 0
private fun doLotsOfProcessingOn(s: String, o: Any) {}
private val otherParametersHere = ""
// [END_EXCLUDE silent]

// [START android_kotlin_style_guide_8]
// [START_EXCLUDE silent]
private fun checkBraces1() {
// [END_EXCLUDE silent]
if (string.isEmpty()) return

val result =
    if (string.isEmpty()) DEFAULT_VALUE else string

when (value) {
    0 -> return
    // …
}
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_8]

// [START android_kotlin_style_guide_9]
// [START_EXCLUDE silent]
private fun checkBraces2() {
// [END_EXCLUDE silent]
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
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_9]

// [START_EXCLUDE silent]
private fun condition() = true
private fun otherCondition() = false
private fun foo() {}
private fun something() {}
private fun somethingElse() {}
private fun lastThing() {}
private fun recover() {}
private class ProblemException : Exception()
// [END_EXCLUDE silent]

// [START android_kotlin_style_guide_10]
// [START_EXCLUDE silent]
private fun checkBraces3(): Any {
    if (true) {
// [END_EXCLUDE silent]
return Runnable {
    while (condition()) {
        foo()
    }
}
// [START_EXCLUDE silent]
    }
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
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_10]

// [START_EXCLUDE silent]
private fun doSomething() {}
// [END_EXCLUDE silent]

// [START android_kotlin_style_guide_11]
// [START_EXCLUDE silent]
private fun checkEmptyBlocks1() {
// [END_EXCLUDE silent]
try {
    doSomething()
} catch (e: Exception) {} // WRONG!
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_11]

// [START android_kotlin_style_guide_12]
// [START_EXCLUDE silent]
private fun checkEmptyBlocks2() {
// [END_EXCLUDE silent]
try {
    doSomething()
} catch (e: Exception) {
} // Okay
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_12]

// [START android_kotlin_style_guide_13]
// [START_EXCLUDE silent]
private fun checkExpressions1() {
// [END_EXCLUDE silent]
val value = if (string.isEmpty()) 0 else 1  // Okay
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_13]

// [START android_kotlin_style_guide_14]
// [START_EXCLUDE silent]
private fun checkExpressions2() {
// [END_EXCLUDE silent]
val value = if (string.isEmpty())  // WRONG!
    0
else
    1
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_14]

// [START android_kotlin_style_guide_15]
val defaultExtension: String get() = "kt"
// [END android_kotlin_style_guide_15]

// [START android_kotlin_style_guide_16]
// [START_EXCLUDE silent]
private fun loop1() {
// [END_EXCLUDE silent]
// WRONG!
for(i in 0..1) {
}
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_16]

// [START android_kotlin_style_guide_17]
// [START_EXCLUDE silent]
private fun loop2() {
// [END_EXCLUDE silent]
// Okay
for (i in 0..1) {
}
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_17]

// [START android_kotlin_style_guide_18]
// [START_EXCLUDE silent]
private fun else1() {
    if (true) {
// [END_EXCLUDE silent]
// WRONG!
}else {
}
// [START_EXCLUDE silent]
    }
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_18]

// [START android_kotlin_style_guide_19]
// [START_EXCLUDE silent]
private fun else2() {
    if (true) {
// [END_EXCLUDE silent]
// Okay
} else {
}
// [START_EXCLUDE silent]
    }
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_19]

// [START_EXCLUDE silent]
private val list = emptyList<String>()
// [END_EXCLUDE silent]

// [START android_kotlin_style_guide_20]
// [START_EXCLUDE silent]
private fun if1() {
// [END_EXCLUDE silent]
// WRONG!
if (list.isEmpty()){
}
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_20]

// [START android_kotlin_style_guide_21]
// [START_EXCLUDE silent]
private fun if2() {
// [END_EXCLUDE silent]
// Okay
if (list.isEmpty()) {
}
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_21]

// [START android_kotlin_style_guide_22]
// [START_EXCLUDE silent]
private fun sum1() {
// [END_EXCLUDE silent]
// WRONG!
val two = 1+1
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_22]

// [START android_kotlin_style_guide_23]
// [START_EXCLUDE silent]
private fun sum2() {
// [END_EXCLUDE silent]
// Okay
val two = 1 + 1
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_23]

// [START_EXCLUDE silent]
private val ints = listOf(1, 2, 3)
// [END_EXCLUDE silent]

// [START android_kotlin_style_guide_24]
// [START_EXCLUDE silent]
private fun arr1() {
// [END_EXCLUDE silent]
// WRONG!
ints.map { value->value.toString() }
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_24]

// [START android_kotlin_style_guide_25]
// [START_EXCLUDE silent]
private fun arr2() {
// [END_EXCLUDE silent]
// Okay
ints.map { value -> value.toString() }
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_25]

// [START android_kotlin_style_guide_26]
// [START_EXCLUDE silent]
private fun ref1() {
// [END_EXCLUDE silent]
// WRONG!
val toString = Any :: toString
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_26]

// [START android_kotlin_style_guide_27]
// [START_EXCLUDE silent]
private fun ref2() {
// [END_EXCLUDE silent]
// Okay
val toString = Any::toString
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_27]

// [START android_kotlin_style_guide_28]
// [START_EXCLUDE silent]
private fun <T> T.letTest1() {
    this.let {
// [END_EXCLUDE silent]
// WRONG
it . toString()
// [START_EXCLUDE silent]
    }
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_28]

// [START android_kotlin_style_guide_29]
// [START_EXCLUDE silent]
private fun <T> T.letTest2() {
    this.let {
// [END_EXCLUDE silent]
// Okay
it.toString()
// [START_EXCLUDE silent]
    }
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_29]

// [START android_kotlin_style_guide_30]
// [START_EXCLUDE silent]
private fun range1() {
// [END_EXCLUDE silent]
// WRONG
for (i in 1 .. 4) {
  print(i)
}
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_30]

// [START android_kotlin_style_guide_31]
// [START_EXCLUDE silent]
private fun range2() {
// [END_EXCLUDE silent]
// Okay
for (i in 1..4) {
  print(i)
}
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_31]

// [START android_kotlin_style_guide_32]
enum class Answer {
    YES,
    NO,

    MAYBE {
        override fun toString() = """¯\_(ツ)_/¯"""
    }
}
// [END android_kotlin_style_guide_32]

// [START_EXCLUDE silent]
private annotation class SOURCE
private annotation class FUNCTION
private annotation class PROPERTY_SETTER
private annotation class FIELD
private annotation class Retention(val value: SOURCE)
private annotation class Target(vararg val allowedTargets: Any)
// [END_EXCLUDE silent]

// [START android_kotlin_style_guide_33]
@Retention(SOURCE)
@Target(FUNCTION, PROPERTY_SETTER, FIELD)
annotation class Global
// [END android_kotlin_style_guide_33]

// [START_EXCLUDE silent]
private class Snippet34 {
class Disposable
// [END_EXCLUDE silent]
// [START android_kotlin_style_guide_34]
@JvmField @Volatile
var disposable: Disposable? = null
// [END android_kotlin_style_guide_34]
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]

// [START_EXCLUDE silent]
private class Snippet35 {
class Disposable
annotation class Test
// [END_EXCLUDE silent]
// [START android_kotlin_style_guide_35]
@Volatile var disposable: Disposable? = null

@Test fun selectAll() {
    // …
}
// [END android_kotlin_style_guide_35]
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]

// [START_EXCLUDE silent]
private class Snippet36 {
class Disposable
// [END_EXCLUDE silent]
// [START android_kotlin_style_guide_36]
@field:[JvmStatic Volatile]
var disposable: Disposable? = null
// [END android_kotlin_style_guide_36]
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]

// [START_EXCLUDE silent]
private class Snippet37 {
private fun old() {
    val a = object {
// [END_EXCLUDE silent]
// [START android_kotlin_style_guide_37]
override fun toString(): String = "Hey"
// becomes
// [START_EXCLUDE silent]
    }
    val b = object {
// [END_EXCLUDE silent]
override fun toString() = "Hey"
// [START_EXCLUDE silent]
    }
}
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_37]

// [START_EXCLUDE silent]
private class Snippet38 {
class Icon
object IconLoader { fun getIcon(s: String) = Icon() }
// [END_EXCLUDE silent]
// [START android_kotlin_style_guide_38]
private val ICON: Icon = IconLoader.getIcon("/icons/kotlin.png")
// becomes
// [START_EXCLUDE silent]
/*
// [END_EXCLUDE silent]
private val ICON = IconLoader.getIcon("/icons/kotlin.png")
// [START_EXCLUDE silent]
*/
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_38]

// [START android_kotlin_style_guide_39]
// Okay
// [START_EXCLUDE silent]
/*
// [END_EXCLUDE silent]
package com.example.deepspace
// WRONG!
package com.example.deepSpace
// WRONG!
package com.example.deep_space
// [START_EXCLUDE silent]
*/
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_39]

// [START_EXCLUDE silent]
private class Snippet40 {
annotation class Test
// [END_EXCLUDE silent]
// [START android_kotlin_style_guide_40]
@Test fun pop_emptyStack() {
    // …
}
// [END android_kotlin_style_guide_40]
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]

// [START_EXCLUDE silent]
private class Snippet41 {
annotation class Composable
// [END_EXCLUDE silent]
// [START android_kotlin_style_guide_41]
@Composable
fun NameTag(name: String) {
    // …
}
// [END android_kotlin_style_guide_41]
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]

// [START android_kotlin_style_guide_42]
// [START_EXCLUDE silent]
private class Snippet42 {
// [END_EXCLUDE silent]
// WRONG!
fun `test every possible case`() {}
// OK
fun testEveryPossibleCase() {}
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_42]

// [START_EXCLUDE silent]
private class Snippet43 {
class SomeMutableType
object Joiner {
    fun on(c: Char) = this
}
// [END_EXCLUDE silent]
// [START android_kotlin_style_guide_43]
const val NUMBER = 5
val NAMES = listOf("Alice", "Bob")
val AGES = mapOf("Alice" to 35, "Bob" to 32)
val COMMA_JOINER = Joiner.on(',') // Joiner is immutable
val EMPTY_ARRAY = arrayOf<SomeMutableType>()
// [END android_kotlin_style_guide_43]
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]

// [START_EXCLUDE silent]
private class Snippet44 {
val mutableInstance = ""
val mutableInstance2 = ""
object Logger { fun getLogger(s: String) = this }
// [END_EXCLUDE silent]
// [START android_kotlin_style_guide_44]
val variable = "var"
val nonConstScalar = "non-const"
val mutableCollection: MutableSet<String> = HashSet()
val mutableElements = listOf(mutableInstance)
val mutableValues = mapOf("Alice" to mutableInstance, "Bob" to mutableInstance2)
val logger = Logger.getLogger(MyClass::class.java.name)
val nonEmptyArray = arrayOf("these", "can", "change")
// [END android_kotlin_style_guide_44]
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]

// [START_EXCLUDE silent]
private class Snippet45 {
// [END_EXCLUDE silent]
// [START android_kotlin_style_guide_45]
private var _table: Map<String, Int>? = null

val table: Map<String, Int>
    get() {
        if (_table == null) {
            _table = HashMap()
        }
        return _table ?: throw AssertionError()
    }
// [END android_kotlin_style_guide_45]

// [START_EXCLUDE silent]
private class Snippet46 {
// [END_EXCLUDE silent]
// [START android_kotlin_style_guide_46]
/**
 * Multiple lines of KDoc text are written here,
 * wrapped normally…
 */
fun method(arg: String) {
    // …
}
// [END android_kotlin_style_guide_46]
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]

// [START_EXCLUDE silent]
private class Snippet47 {
// [END_EXCLUDE silent]
// [START android_kotlin_style_guide_47]
/** An especially short bit of KDoc. */
// [START_EXCLUDE silent]
fun empty() {}
// [END_EXCLUDE silent]
// [END android_kotlin_style_guide_47]
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
// [START_EXCLUDE silent]
}
// [END_EXCLUDE silent]
