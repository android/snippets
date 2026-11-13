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

package com.example.snippets.security.ipc

import android.content.Context
import android.database.Cursor
import android.util.Log
import androidx.activity.ComponentActivity
import java.io.File
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

private class UniqueFilenameCopyActivity : ComponentActivity() {
    fun copyToUniqueFile(fd: FileDescriptor) {
        // [START android_security_ipc_untrustworthy_filename_temp_file]
        // Code in
        // https://developer.android.com/training/secure-file-sharing/request-file#OpenFile
        // used to obtain file descriptor (fd).

        try {
            val inputStream = FileInputStream(fd)
            val tempFile = File.createTempFile("temp", null, cacheDir)
            val outputStream = FileOutputStream(tempFile)
            val buf = ByteArray(1024)
            var len: Int
            len = inputStream.read(buf)
            while (len > 0) {
                if (len != -1) {
                    outputStream.write(buf, 0, len)
                    len = inputStream.read(buf)
                }
            }
            inputStream.close()
            outputStream.close()
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("MainActivity", "File copy error.")
            return
        }
        // [END android_security_ipc_untrustworthy_filename_temp_file]
    }
}

private object UntrustworthyFilenameSanitizer {
    abstract class SanitizerBase(
        val context: Context,
        val returnCursor: Cursor,
        val nameIndex: Int,
        val fd: FileDescriptor,
    ) {
        // [START android_security_ipc_untrustworthy_filename_sanitize]
        protected fun sanitizeFilename(displayName: String): String {
            val badCharacters = arrayOf("..", "/")
            val segments = displayName.split("/")
            var fileName = segments[segments.size - 1]
            for (suspString in badCharacters) {
                fileName = fileName.replace(suspString, "_")
            }
            return fileName
        }

        val displayName = returnCursor.getString(nameIndex)
        val fileName = sanitizeFilename(displayName)
        val filePath = File(context.filesDir, fileName).path

        // saferOpenFile defined in Android developer documentation.
        val outputFile = saferOpenFile(filePath, context.filesDir.canonicalPath)

        // fd obtained using Requesting a shared file from Android developer
        // documentation.

        val inputStream = FileInputStream(fd)
        // [START_EXCLUDE silent]
    }

    fun copySanitizedFile(inputStream: FileInputStream, outputFile: File) {
        // [END_EXCLUDE]

        // Copy the contents of the file to the new file.
        try {
            val outputStream = FileOutputStream(outputFile)
            val buffer = ByteArray(1024)
            var length: Int
            while (inputStream.read(buffer).also { length = it } > 0) {
                outputStream.write(buffer, 0, length)
            }
        } catch (e: IOException) {
            // Handle exception.
        }
        // [END android_security_ipc_untrustworthy_filename_sanitize]
    }
}

private fun saferOpenFile(path: String, expectedDir: String?): File {
    val f = File(path)
    val canonicalPath = f.canonicalPath
    require(canonicalPath.startsWith(expectedDir!!))
    return f
}
