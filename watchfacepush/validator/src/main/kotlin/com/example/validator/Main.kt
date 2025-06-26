package com.example.validator

import com.google.android.wearable.watchface.validator.client.DwfValidatorFactory
import java.io.File
import java.io.FileOutputStream
import kotlin.system.exitProcess

class Main {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            println("Watch Face Push validator test program")
            performValidation()
        }
    }
}

private fun performValidation() {
    val watchFaceFile = obtainTempWatchFaceFile()
    val appPackageName = "com.example.validator"

    // [START android_examples_wfp_validation]
    val validator = DwfValidatorFactory.create()
    val result = validator.validate(watchFaceFile, appPackageName)

    if (result.failures().isEmpty()) {
        val token = result.validationToken()
        println("Validation token: $token")

        // Validation success - continue with the token
        // ...
    } else {
        // There were failures, handle them accordingly - validation has failed.
        println(result.successes().size.toString() + " successes, " + result.failures().size.toString() + " failures")
        result.failures().forEach { failure ->
            println("FAILURE: ${failure.name()}: ${failure.failureMessage()}")
            // ...
        }
    }
    // [END android_examples_wfp_validation]
}

private fun obtainTempWatchFaceFile(): File {
    val resourceName = "watchface.apk"

    val inputStream = object {}.javaClass.classLoader.getResourceAsStream(resourceName)

    if (inputStream == null) {
        println("Error: Cannot find resource '$resourceName'")
        exitProcess(1)
    }

    val tempFile = File.createTempFile("validator-", ".apk")
    tempFile.deleteOnExit()

    FileOutputStream(tempFile).use { fos ->
        inputStream.copyTo(fos)
    }
    return tempFile
}