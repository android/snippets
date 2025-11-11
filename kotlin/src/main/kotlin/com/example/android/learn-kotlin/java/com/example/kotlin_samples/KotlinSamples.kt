package com.example.android.kotlin.snippets

import androidx.compose.ui.text.toUpperCase

// A top-level constant used by the snippets below.
private const val count = 42

/*
 * The following are function definitions from the provided snippets.
 * They are defined at the top level so they can be called by other snippets.
 * Some have been given unique names to prevent compilation errors, but their
 * bodies are unchanged.
 */

// [START kotlin_defining_a_function]
// From Snippet 6
fun generateAnswerString(): String {
    val answerString = if (count == 42) {
        "I have the answer."
    } else {
        "The answer eludes me"
    }

    return answerString
}
// [END kotlin_defining_a_function]

// [START kotlin_function_with_parameter]
// From Snippet 8
// Note: This function has the same signature as the one from Snippet 10 and 11.
// It is kept here as the primary example.
fun generateAnswerString(countThreshold: Int): String {
    val answerString = if (count > countThreshold) {
        "I have the answer."
    } else {
        "The answer eludes me."
    }

    return answerString
}
// [END kotlin_function_with_parameter]

// [START kotlin_function_with_explicit_return]
// From Snippet 10
fun generateAnswerStringWithExplicitReturn(countThreshold: Int): String {
    return if (count > countThreshold) {
        "I have the answer."
    } else {
        "The answer eludes me."
    }
}
// [END kotlin_function_with_explicit_return]

// [START kotlin_function_as_single_expression]
// From Snippet 11
fun generateAnswerStringAsExpression(countThreshold: Int): String = if (count > countThreshold) {
    "I have the answer"
} else {
    "The answer eludes me"
}
// [END kotlin_function_as_single_expression]

// [START kotlin_higher_order_function_definition]
// From Snippet 14
fun stringMapper(str: String, mapper: (String) -> Int): Int {
    // Invoke function
    return mapper(str)
}
// [END kotlin_higher_order_function_definition]


/**
 * The main entry point for this file. Running this function will execute
 * each of the original snippets in order.
 */
fun main() {
    println("--- Snippet 1 ---")
    runSnippet1()
    println("\n--- Snippet 2 ---")
    runSnippet2()
    println("\n--- Snippet 3 ---")
    runSnippet3()
    println("\n--- Snippet 4 ---")
    runSnippet4()
    println("\n--- Snippet 5 ---")
    runSnippet5()
    println("\n--- Snippet 7 (no output) ---")
    runSnippet7()
    println("\n--- Snippet 9 (no output) ---")
    runSnippet9()
    println("\n--- Snippet 12 (no output) ---")
    runSnippet12()
    println("\n--- Snippet 13 ---")
    runSnippet13()
    println("\n--- Snippet 15 (no output) ---")
    runSnippet15()
    println("\n--- Snippet 16 (no output) ---")
    runSnippet16()
}

fun runSnippet1() {
    // [START kotlin_control_flow_if_else_if]
    if (count == 42) {
        println("I have the answer.")
    } else if (count > 35) {
        println("The answer is close.")
    } else {
        println("The answer eludes me.")
    }
    // [END kotlin_control_flow_if_else_if]
}

fun runSnippet2() {
    // [START kotlin_control_flow_if_else]
    if (count == 42) {
        println("I have the answer.")
    } else {
        println("The answer eludes me.")
    }
    // [END kotlin_control_flow_if_else]
}

fun runSnippet3() {
    // [START kotlin_control_flow_if_as_expression]
    val answerString: String = if (count == 42) {
        "I have the answer."
    } else if (count > 35) {
        "The answer is close."
    } else {
        "The answer eludes me."
    }

    println(answerString)
    // [END kotlin_control_flow_if_as_expression]
}

fun runSnippet4() {
    // [START kotlin_control_flow_when_as_expression]
    val answerString = when {
        count == 42 -> "I have the answer."
        count > 35 -> "The answer is close."
        else -> "The answer eludes me."
    }

    println(answerString)
    // [END kotlin_control_flow_when_as_expression]
}

fun runSnippet5() {
    // [START kotlin_null_safety_smart_cast]
    val languageName: String? = null
    if (languageName != null) {
        // No need to write languageName?.toUpperCase()
        println(languageName.toUpperCase())
    }
    // [END kotlin_null_safety_smart_cast]
}

fun runSnippet7() {
    // [START kotlin_calling_a_function]
    val answerString = generateAnswerString()
    // [END kotlin_calling_a_function]
}

fun runSnippet9() {
    // [START kotlin_calling_a_function_with_argument]
    val answerString = generateAnswerString(42)
    // [END kotlin_calling_a_function_with_argument]
}

fun runSnippet12() {
    // [START kotlin_lambda_declaration]
    val stringLengthFunc: (String) -> Int = { input ->
        input.length
    }
    // [END kotlin_lambda_declaration]
}

fun runSnippet13() {
    // [START kotlin_lambda_invocation]
    val stringLengthFunc: (String) -> Int = { input ->
        input.length
    }

    val stringLength: Int = stringLengthFunc("Android")
    // [END kotlin_lambda_invocation]
    // The original snippet did not print the result, so we don't either.
}

fun runSnippet15() {
    // [START kotlin_higher_order_function_call_default]
    stringMapper("Android", { input ->
        input.length
    })
    // [END kotlin_higher_order_function_call_default]
}

fun runSnippet16() {
    // [START kotlin_higher_order_function_call_trailing_lambda]
    stringMapper("Android") { input ->
        input.length
    }
    // [END kotlin_higher_order_function_call_trailing_lambda]
}
