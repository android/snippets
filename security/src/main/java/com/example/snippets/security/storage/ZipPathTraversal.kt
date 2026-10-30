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

package com.example.snippets.security.storage

import java.io.File
import java.io.IOException
import java.io.InputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipException
import java.util.zip.ZipInputStream

private class ZipEntryValidator {
    // [START android_security_storage_zip_new_file]
    companion object {
        @Throws(IOException::class)
        fun newFile(targetPath: File, zipEntry: ZipEntry): File {
            val name: String = zipEntry.name
            val f = File(targetPath, name)
            val canonicalPath = f.canonicalPath
            if (!canonicalPath.startsWith(
                    targetPath.canonicalPath + File.separator
                )
            ) {
                throw ZipException("Illegal name: $name")
            }
            return f
        }
    }
    // [END android_security_storage_zip_new_file]
}

private object ZipUnzipSnippet {
    // [START android_security_storage_zip_unzip]
    @Throws(IOException::class)
    fun unzip(inputStream: InputStream?, destinationDir: File) {
        if (!destinationDir.isDirectory) {
            throw IOException("Destination is not a directory.")
        }
        val files = destinationDir.list()
        if (files != null && files.isNotEmpty()) {
            throw IOException("Destination directory is not empty.")
        }
        ZipInputStream(inputStream).use { zipInputStream ->
            var zipEntry: ZipEntry
            while (zipInputStream.nextEntry.also { zipEntry = it } != null) {
                val targetFile = File(destinationDir, zipEntry.name)
                // ...
            }
        }
    }
    // [END android_security_storage_zip_unzip]
}
