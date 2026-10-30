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

import java.io.BufferedInputStream
import java.io.FileInputStream
import java.io.IOException
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

private object DynamicCodeLoadingSnippet {
    // [START android_security_storage_dynamic_code_integrity]
    // package com.example.myapplication

    // import java.io.BufferedInputStream
    // import java.io.FileInputStream
    // import java.io.IOException
    // import java.security.MessageDigest
    // import java.security.NoSuchAlgorithmException

    object FileIntegrityChecker {
        @Throws(IOException::class, NoSuchAlgorithmException::class)
        fun getIntegrityHash(filePath: String?): String {
            val md = MessageDigest.getInstance("SHA-256") // You can choose other algorithms as needed.
            val buffer = ByteArray(8192)
            var bytesRead: Int
            BufferedInputStream(FileInputStream(filePath)).use { fis ->
                while (fis.read(buffer).also { bytesRead = it } != -1) {
                    md.update(buffer, 0, bytesRead)
                }
            }
            val digest = md.digest()
            return bytesToHex(digest)
        }

        private fun bytesToHex(bytes: ByteArray): String {
            val sb = StringBuilder(bytes.size * 2)
            for (b in bytes) {
                sb.append(String.format("%02x", b))
            }
            return sb.toString()
        }

        @Throws(IOException::class, NoSuchAlgorithmException::class)
        fun verifyIntegrity(filePath: String?, expectedHash: String): Boolean {
            val actualHash = getIntegrityHash(filePath)
            return actualHash == expectedHash
        }

        @Throws(Exception::class)
        @JvmStatic
        fun main(args: Array<String>) {
            val filePath = "/path/to/your/file"
            val expectedHash = "your_expected_hash_value"
            if (verifyIntegrity(filePath, expectedHash)) {
                println("File integrity is valid!")
            } else {
                println("File integrity is compromised!")
            }
        }
    }
    // [END android_security_storage_dynamic_code_integrity]
}
