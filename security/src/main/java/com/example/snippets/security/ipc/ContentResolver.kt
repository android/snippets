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
import android.content.pm.PackageManager
import android.content.pm.ProviderInfo
import android.net.Uri
import android.os.ParcelFileDescriptor
import android.os.Process
import android.system.Os
import android.system.OsConstants
import android.system.StructStat
import java.io.File

private object ContentResolverMitigations {
    // [START android_security_ipc_content_resolver_validate_file]
    fun isValidFile(ctx: Context, pfd: ParcelFileDescriptor, fileUri: Uri): Boolean {
        // Canonicalize to resolve symlinks and path traversals.
        val fdCanonical = File(fileUri.path!!).canonicalPath

        val pfdStat: StructStat = Os.fstat(pfd.fileDescriptor)

        // Lstat doesn't follow the symlink.
        val canonicalFileStat: StructStat = Os.lstat(fdCanonical)

        // Since we canonicalized (followed the links) the path already,
        // the path shouldn't point to symlink unless it was changed in the
        // meantime.
        if (OsConstants.S_ISLNK(canonicalFileStat.st_mode)) {
            return false
        }

        val sameFile =
            pfdStat.st_dev == canonicalFileStat.st_dev &&
                pfdStat.st_ino == canonicalFileStat.st_ino

        if (!sameFile) {
            return false
        }

        return !isBlockedPath(ctx, fdCanonical)
    }

    fun isBlockedPath(ctx: Context, fdCanonical: String): Boolean {
        // Paths that should rarely be exposed.
        if (fdCanonical.startsWith("/proc/") ||
            fdCanonical.startsWith("/data/misc/")
        ) {
            return true
        }

        // Implement logic to block desired directories. For example, specify
        // the entire app data/ directory to block all access.
        return false
    }
    // [END android_security_ipc_content_resolver_validate_file]

    // [START android_security_ipc_content_resolver_belongs_to_current_app]
    fun belongsToCurrentApplication(ctx: Context, uri: Uri): Boolean {
        val authority: String = uri.authority.toString()
        val info: ProviderInfo =
            ctx.packageManager.resolveContentProvider(authority, 0)!!

        return ctx.packageName.equals(info.packageName)
    }
    // [END android_security_ipc_content_resolver_belongs_to_current_app]

    // [START android_security_ipc_content_resolver_is_exported]
    fun isExported(ctx: Context, uri: Uri): Boolean {
        val authority = uri.authority.toString()
        val info: ProviderInfo =
            ctx.packageManager.resolveContentProvider(authority, 0)!!

        return info.exported
    }
    // [END android_security_ipc_content_resolver_is_exported]

    // [START android_security_ipc_content_resolver_was_granted_permission_own_app]
    // [START android_security_ipc_content_resolver_was_granted_permission_trusted_app]
    // grantFlag is one of: FLAG_GRANT_READ_URI_PERMISSION or FLAG_GRANT_WRITE_URI_PERMISSION.
    fun wasGrantedPermission(ctx: Context, uri: Uri?, grantFlag: Int): Boolean {
        val pid: Int = Process.myPid()
        val uid: Int = Process.myUid()
        return ctx.checkUriPermission(uri, pid, uid, grantFlag) ==
            PackageManager.PERMISSION_GRANTED
    }
    // [END android_security_ipc_content_resolver_was_granted_permission_trusted_app]
    // [END android_security_ipc_content_resolver_was_granted_permission_own_app]
}
