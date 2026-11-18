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

package com.example.snippets.security.dcl

import android.util.Log
import java.io.File
import java.io.IOException

// [START android_security_dcl_read_only_native_load]
fun safelyLoadNativeLibrary(libraryFile: File) {
    val tag = "NativeLibLoader"

    // 1. Verify the file exists before attempting any operations
    if (!libraryFile.exists()) {
        Log.e(tag, "Native library file does not exist: ${libraryFile.absolutePath}")
        return
    }

    try {
        // 2. Attempt to make the file write-restricted first, then read-only.
        // Some Android runtimes benefit from setWritable(false) as a precursor.
        val successWritable = libraryFile.setWritable(false)
        val successReadOnly = libraryFile.setReadOnly()

        // 3. Handle filesystems where setting permissions failed/returned false
        if (!successReadOnly || libraryFile.canWrite()) {
            Log.w(
                tag,
                "Filesystem did not permit setting read-only on ${libraryFile.absolutePath}. " +
                    "Attempting fallback verification."
            )

            // If the filesystem is inherently read-only (like a mounted ISO or secure asset partition),
            // setReadOnly() might return false but canWrite() will also be false.
            if (libraryFile.canWrite()) {
                throw IOException("Unable to set file permissions to read-only on this partition/filesystem.")
            }
        }
    } catch (e: SecurityException) {
        Log.e(tag, "Security manager blocked setting permissions to read-only: ${e.message}", e)
        // Handle gracefully, or attempt fallback based on your app's security configuration
    } catch (e: IOException) {
        Log.e(tag, "Failed to apply read-only permissions due to filesystem constraints: ${e.message}", e)
    }

    // 4. Load the library safely
    try {
        System.load(libraryFile.absolutePath)
        Log.i(tag, "Successfully loaded native library: ${libraryFile.name}")
    } catch (e: UnsatisfiedLinkError) {
        Log.e(
            tag,
            "System blocked loading the library. Verify that the file is not writable " +
                "by other processes and is stored in a secure location (e.g., context.filesDir).",
            e
        )
    }
}
// [END android_security_dcl_read_only_native_load]
