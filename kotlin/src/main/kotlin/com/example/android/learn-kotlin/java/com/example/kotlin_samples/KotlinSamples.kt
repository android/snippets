package com.example.kotlin_samples

// A top-level constant used by several snippets.
private const val count = 42

/**
 * This function consolidates the output of all language snippets
 * into a single string for display in the UI.
 */
fun getLanguageSamplesOutput(): String {
    return """
    --- Control Flow ---
    ${controlFlowSnippets()}

    --- Null Safety ---
    ${nullSafetySnippets()}

    --- Functions ---
    ${functionSnippets()}

    --- Higher-Order Functions ---
    ${higherOrderFunctionSnippets()}
    """.trimIndent()
}

private fun controlFlowSnippets(): String {
    val output = StringBuilder()

    // [START kotlin_control_flow_if_else_if]
    val result1 = if (count == 42) {
        "I have the answer."
    } else if (count > 35) {
        "The answer is close."
    } else {
        "The answer eludes me."
    }
    output.appendLine("if-else if-else: $result1")
    // [END kotlin_control_flow_if_else_if]

    // [START kotlin_control_flow_if_else]
    val result2 = if (count == 42) {
        "I have the answer."
    } else {
        "The answer eludes me."
    }
    output.appendLine("if-else: $result2")
    // [END kotlin_control_flow_if_else]

    // [START kotlin_control_flow_if_as_expression]
    val answerStringFromIf: String = if (count == 42) {
        "I have the answer."
    } else if (count > 35) {
        "The answer is close."
    } else {
        "The answer eludes me."
    }
    output.appendLine("if as expression: $answerStringFromIf")
    // [END kotlin_control_flow_if_as_expression]

    // [START kotlin_control_flow_when_as_expression]
    val answerStringFromWhen = when {
        count == 42 -> "I have the answer."
        count > 35 -> "The answer is close."
        else -> "The answer eludes me."
    }
    output.appendLine("when as expression: $answerStringFromWhen")
    // [END kotlin_control_flow_when_as_expression]

    return output.toString()
}

private fun nullSafetySnippets(): String {
    // [START kotlin_null_safety_smart_cast]
    val languageName: String? = "Kotlin"
    var result = "Language name is null"
    if (languageName != null) {
        // No need to write languageName?.uppercase()
        result = languageName.uppercase()
    }
    return "Smart cast result: $result"
    // [END kotlin_null_safety_smart_cast]
}

// [START kotlin_defining_a_function]
private fun generateAnswerString(): String {
    return if (count == 42) {
        "I have the answer."
    } else {
        "The answer eludes me"
    }
}
// [END kotlin_defining_a_function]

// [START kotlin_function_with_parameter]
private fun generateAnswerStringWithParam(countThreshold: Int): String {
    return if (count > countThreshold) {
        "I have the answer."
    } else {
        "The answer eludes me."
    }
}
// [END kotlin_function_with_parameter]


private fun functionSnippets(): String {
    val output = StringBuilder()

    // [START kotlin_calling_a_function]
    val answerString = generateAnswerString()
    output.appendLine("Function call: $answerString")
    // [END kotlin_calling_a_function]

    // [START kotlin_calling_a_function_with_argument]
    val answerStringWithArg = generateAnswerStringWithParam(41)
    output.appendLine("Function with argument: $answerStringWithArg")
    // [END kotlin_calling_a_function_with_argument]

    return output.toString()
}

// [START kotlin_higher_order_function_definition]
private fun stringMapper(str: String, mapper: (String) -> Int): Int {
    return mapper(str)
}
// [END kotlin_higher_order_function_definition]

private fun higherOrderFunctionSnippets(): String {
    val output = StringBuilder()

    // [START kotlin_lambda_declaration]
    val stringLengthFunc: (String) -> Int = { input ->
        input.length
    }
    // [END kotlin_lambda_declaration]

    // [START kotlin_lambda_invocation]
    val stringLength: Int = stringLengthFunc("Android")
    output.appendLine("Lambda invocation: Length of 'Android' is $stringLength")
    // [END kotlin_lambda_invocation]

    // [START kotlin_higher_order_function_call_default]
    val length1 = stringMapper("Android") { input ->
        input.length
    }
    output.appendLine("Trailing lambda call: Length is $length1")
    // [END kotlin_higher_order_function_call_default]

    return output.toString()
}
