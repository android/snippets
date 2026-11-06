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

private object PathTraversalSnippet {
    // [START android_security_storage_path_traversal_safer_open_file]
    @Throws(IllegalArgumentException::class)
    fun saferOpenFile(path: String, expectedDir: String?): File {
        val f = File(path)
        val canonicalPath = f.canonicalPath
        require(canonicalPath.startsWith(expectedDir!!))
        return f
    }
    // [END android_security_storage_path_traversal_safer_open_file]
}
