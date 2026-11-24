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

package com.example.snippets.security.inputvalidation

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteQueryBuilder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Process
import android.util.Log
import android.util.Xml
import androidx.activity.ComponentActivity
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.regex.Pattern
import java.util.zip.ZipException
import java.util.zip.ZipInputStream
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException

// Placeholder state managers for compilation
class SessionManager private constructor() {
    val isLoggedIn: Boolean = false

    companion object {
        private val instance = SessionManager()
        fun getInstance(): SessionManager = instance
    }
}

class SubscriptionManager private constructor() {
    val isPremium: Boolean = false

    companion object {
        private val instance = SubscriptionManager()
        fun getInstance(): SubscriptionManager = instance
    }
}

// [START android_security_input_canonical_path]
object CanonicalPathValidator {

    @Throws(IOException::class, SecurityException::class)
    fun resolveSafeFile(context: Context, userInputFilename: String): File {
        val baseDir = File(context.filesDir, "allowed_directory").canonicalFile
        val baseDirPath = if (baseDir.path.endsWith(File.separator)) {
            baseDir.path
        } else {
            baseDir.path + File.separator
        }

        val requestedCanonicalFile = File(baseDir, userInputFilename).canonicalFile
        if (!requestedCanonicalFile.path.startsWith(baseDirPath)) {
            throw SecurityException("Directory traversal attempt blocked")
        }
        return requestedCanonicalFile
    }
}
// [END android_security_input_canonical_path]

// [START android_security_input_sqlite_parameterized]
object SafeUserAccountDao {

    fun getUserData(db: SQLiteDatabase, userInputUsername: String): Cursor {
        return db.rawQuery(
            "SELECT * FROM users WHERE username = ?",
            arrayOf(userInputUsername),
        )
    }

    fun updateUserDepartment(
        db: SQLiteDatabase,
        userInputUsername: String,
        department: String,
    ): Int {
        val values = ContentValues().apply { put("department", department) }
        return db.update("users", values, "username = ?", arrayOf(userInputUsername))
    }
}
// [END android_security_input_sqlite_parameterized]

// [START android_security_input_content_resolver_query]
object SafePartnerDirectoryClient {

    fun queryUserData(contentResolver: ContentResolver, userInputId: String): Cursor? {
        val targetUri = Uri.parse("content://com.example.provider/users")
        val projection = arrayOf("id", "username", "email")
        return contentResolver.query(
            targetUri,
            projection,
            "id = ?",
            arrayOf(userInputId),
            null,
        )
    }

    fun deleteUserById(contentResolver: ContentResolver, userInputId: String): Int {
        val targetUri = Uri.parse("content://com.example.provider/users")
        return contentResolver.delete(targetUri, "id = ?", arrayOf(userInputId))
    }
}
// [END android_security_input_content_resolver_query]

// [START android_security_input_deeplink_router]
class DeepLinkRouterActivity : ComponentActivity() {
    companion object {
        private const val ALLOWED_SCHEME = "myapp"
        private const val ALLOWED_HOST = "payment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intentData: Uri? = intent?.data
        if (intentData != null &&
            intentData.scheme == ALLOWED_SCHEME &&
            intentData.host == ALLOWED_HOST
        ) {
            if (SessionManager.getInstance().isLoggedIn &&
                SubscriptionManager.getInstance().isPremium
            ) {
                routeToPaymentScreen()
            } else {
                routeToLoginScreen()
            }
        } else {
            finish()
        }
    }

    private fun routeToPaymentScreen() {
        // Navigate to verified payment flow
    }

    private fun routeToLoginScreen() {
        // Redirect unauthenticated or non-entitled user
    }
}
// [END android_security_input_deeplink_router]

// [START android_security_input_deeplink_params]
object SafeOrderDeepLinkParser {

    private val POSITIVE_INT_PATTERN: Pattern = Pattern.compile("^[1-9][0-9]*$")
    private val PROMO_CODE_PATTERN: Pattern = Pattern.compile("^[A-Z0-9]{5,10}$")

    @Throws(IllegalArgumentException::class)
    fun parseItemId(deepLinkUri: Uri): Int {
        val segments = deepLinkUri.pathSegments
        if (segments.size != 2 || segments[0] != "items") {
            throw IllegalArgumentException("Invalid deep link path layout")
        }
        val rawItemId = segments[1]
        if (!POSITIVE_INT_PATTERN.matcher(rawItemId).matches()) {
            throw IllegalArgumentException("Item ID must be a positive integer")
        }
        val itemId = rawItemId.toIntOrNull()
            ?: throw IllegalArgumentException("Item ID exceeds 32-bit integer bounds")

        val promoCode = deepLinkUri.getQueryParameter("promoCode")
        if (promoCode != null && !PROMO_CODE_PATTERN.matcher(promoCode).matches()) {
            throw IllegalArgumentException("Invalid promoCode format")
        }
        return itemId
    }
}
// [END android_security_input_deeplink_params]

// [START android_security_input_dirty_stream_import]
object SafeAttachmentImporter {

    private const val MAX_ATTACHMENT_BYTES = 10L * 1024L * 1024L // 10 MB

    @Throws(IOException::class)
    fun importSharedAttachmentSafely(context: Context, attachmentUri: Uri): File {
        val inputStream = context.contentResolver.openInputStream(attachmentUri)
            ?: throw IOException("Unable to open shared attachment stream")

        return inputStream.use { input ->
            // Ignore OpenableColumns.DISPLAY_NAME and generate an app-controlled filename
            val importsDir = File(context.cacheDir, "shared_imports").apply { mkdirs() }
            val outputFile = File.createTempFile("import_", ".bin", importsDir)

            try {
                FileOutputStream(outputFile).use { output ->
                    val buffer = ByteArray(8192)
                    var totalBytes = 0L
                    var read = input.read(buffer)
                    while (read != -1) {
                        totalBytes += read
                        if (totalBytes > MAX_ATTACHMENT_BYTES) {
                            throw IOException("Attachment exceeds maximum allowed size")
                        }
                        output.write(buffer, 0, read)
                        read = input.read(buffer)
                    }
                }
                outputFile
            } catch (e: Exception) {
                outputFile.delete()
                throw e
            }
        }
    }
}
// [END android_security_input_dirty_stream_import]

// [START android_security_input_content_resolver_uri]
object SafeExternalMediaLoader {

    private const val MAX_SHARED_MEDIA_BYTES = 10 * 1024 * 1024 // 10 MB

    @SuppressLint("ObsoleteSdkInt")
    @Throws(SecurityException::class, IOException::class)
    fun loadExternalMediaSafely(context: Context, mediaUri: Uri): ByteArray {
        if (mediaUri.scheme != ContentResolver.SCHEME_CONTENT) {
            throw SecurityException("Only content:// URIs are permitted")
        }
        val authority = mediaUri.authority
            ?: throw SecurityException("Missing ContentProvider authority")

        val providerInfo = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.resolveContentProvider(
                authority,
                PackageManager.ComponentInfoFlags.of(0),
            )
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.resolveContentProvider(authority, 0)
        } ?: throw SecurityException("Unknown ContentProvider authority")

        if (providerInfo.packageName == context.packageName ||
            providerInfo.applicationInfo.uid == Process.myUid()
        ) {
            throw SecurityException("Refusing to proxy internal or shared-UID ContentProvider")
        }
        if (!providerInfo.exported) {
            val granted = context.checkUriPermission(
                mediaUri,
                Process.myPid(),
                Process.myUid(),
                Intent.FLAG_GRANT_READ_URI_PERMISSION,
            ) == PackageManager.PERMISSION_GRANTED
            if (!granted) {
                throw SecurityException("No read URI permission grant for non-exported provider")
            }
        }

        val inputStream = context.contentResolver.openInputStream(mediaUri)
            ?: throw IOException("Unable to open external media stream")
        return inputStream.use { stream ->
            val out = ByteArrayOutputStream()
            val buf = ByteArray(8192)
            var total = 0
            var n = stream.read(buf)
            while (n != -1) {
                total += n
                if (total > MAX_SHARED_MEDIA_BYTES) {
                    throw IOException("Media stream exceeds size limit")
                }
                out.write(buf, 0, n)
                n = stream.read(buf)
            }
            out.toByteArray()
        }
    }
}
// [END android_security_input_content_resolver_uri]

// [START android_security_input_sqlite_query_builder]
object SafeAuditLogQueryHelper {

    private val AUDIT_PROJECTION_MAP = mapOf(
        "_id" to "_id",
        "event_type" to "event_type",
        "created_at" to "created_at",
    )

    private val SAFE_SORT_ORDER_PATTERN: Pattern =
        Pattern.compile("^(_id|event_type|created_at)(\\s+(ASC|DESC))?$", Pattern.CASE_INSENSITIVE)

    @SuppressLint("ObsoleteSdkInt")
    fun queryAuditEventsSafely(
        db: SQLiteDatabase,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?,
    ): Cursor {
        val validatedSortOrder = if (sortOrder == null) {
            "created_at DESC"
        } else {
            val trimmed = sortOrder.trim()
            if (!SAFE_SORT_ORDER_PATTERN.matcher(trimmed).matches()) {
                throw IllegalArgumentException("Invalid sortOrder clause")
            }
            trimmed
        }

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R && !selection.isNullOrEmpty()) {
            // Strict SQL grammar enforcement requires API 30+; reject arbitrary caller selection on older APIs
            throw IllegalArgumentException("Caller-supplied selection requires API 30+")
        }

        val qb = SQLiteQueryBuilder().apply {
            tables = "audit_events"
            setProjectionMap(AUDIT_PROJECTION_MAP)
            setStrict(true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                // setStrictColumns and setStrictGrammar were added in Android 11 (API 30)
                setStrictColumns(true)
                setStrictGrammar(true)
            }
        }

        val safeProjection = projection ?: AUDIT_PROJECTION_MAP.keys.toTypedArray()
        return qb.query(
            db,
            safeProjection,
            selection,
            selectionArgs,
            null,
            null,
            validatedSortOrder,
        )
    }
}
// [END android_security_input_sqlite_query_builder]

// [START android_security_input_zip_slip_extract]
object SafeZipExtractor {

    private const val MAX_ZIP_ENTRIES = 256
    private const val MAX_ZIP_TOTAL_BYTES = 50L * 1024L * 1024L // 50 MB

    @Throws(IOException::class)
    fun extractZipSafely(archiveStream: InputStream, destinationDir: File): List<File> {
        if (!destinationDir.exists() && !destinationDir.mkdirs()) {
            throw IOException("Failed to create destination directory")
        }
        if (!destinationDir.isDirectory) {
            throw IOException("Destination path is not a directory")
        }
        val existing = destinationDir.list()
        if (existing == null || existing.isNotEmpty()) {
            throw IOException("Destination directory must be readable and empty before extraction")
        }

        val destCanonicalFile = destinationDir.canonicalFile
        val destCanonicalPrefix = if (destCanonicalFile.path.endsWith(File.separator)) {
            destCanonicalFile.path
        } else {
            destCanonicalFile.path + File.separator
        }

        val extracted = mutableListOf<File>()
        var entryCount = 0
        var totalBytes = 0L
        val buffer = ByteArray(8192)

        try {
            ZipInputStream(archiveStream).use { zipIn ->
                var entry = zipIn.nextEntry
                while (entry != null) {
                    entryCount++
                    if (entryCount > MAX_ZIP_ENTRIES) {
                        throw ZipException("Archive exceeds maximum allowed entry count")
                    }

                    // Entries resolving to destinationDir itself (such as "./") fail closed here
                    val targetFile = File(destCanonicalFile, entry.name).canonicalFile
                    if (!targetFile.path.startsWith(destCanonicalPrefix)) {
                        throw ZipException("Blocked Zip Slip traversal entry: ${entry.name}")
                    }

                    if (entry.isDirectory) {
                        if (!targetFile.exists() && !targetFile.mkdirs()) {
                            throw IOException("Failed to create archive subdirectory")
                        }
                    } else {
                        if (targetFile.exists()) {
                            throw ZipException("Duplicate archive file entry: ${entry.name}")
                        }
                        val parent = targetFile.parentFile
                        if (parent != null && !parent.exists() && !parent.mkdirs()) {
                            throw IOException("Failed to create parent directory for entry")
                        }
                        FileOutputStream(targetFile).use { out ->
                            var read = zipIn.read(buffer)
                            while (read != -1) {
                                totalBytes += read
                                if (totalBytes > MAX_ZIP_TOTAL_BYTES) {
                                    throw ZipException("Archive exceeds maximum uncompressed size")
                                }
                                out.write(buffer, 0, read)
                                read = zipIn.read(buffer)
                            }
                        }
                        extracted.add(targetFile)
                    }
                    zipIn.closeEntry()
                    entry = zipIn.nextEntry
                }
            }
            return extracted
        } catch (e: Exception) {
            destinationDir.listFiles()?.forEach { it.deleteRecursively() }
            throw e
        }
    }
}
// [END android_security_input_zip_slip_extract]

// [START android_security_input_xxe_xml_pull_parser]
object SafeXmlFeedParser {

    private const val MAX_XML_BYTES = 1 * 1024 * 1024 // 1 MB

    @Throws(IOException::class, XmlPullParserException::class)
    fun parseFeedTitlesSafely(xmlStream: InputStream): List<String> {
        val boundedBytes = readBoundedBytes(xmlStream, MAX_XML_BYTES)

        val parser = Xml.newPullParser().apply {
            setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true)
            setFeature(XmlPullParser.FEATURE_PROCESS_DOCDECL, false)
            setInput(ByteArrayInputStream(boundedBytes), "UTF-8")
        }

        val titles = mutableListOf<String>()
        var insideTitle = false
        val currentText = StringBuilder()

        var eventType = parser.nextToken()
        while (eventType != XmlPullParser.END_DOCUMENT) {
            when (eventType) {
                XmlPullParser.DOCDECL -> {
                    throw XmlPullParserException("DTD declarations are forbidden")
                }
                XmlPullParser.ENTITY_REF -> {
                    // Predefined/character entities (&amp;, &#38;) resolve to non-null parser.text;
                    // custom or external entity references return null when DTD processing is off
                    val resolved = parser.text
                        ?: throw XmlPullParserException("Unresolved entity reference forbidden")
                    if (insideTitle) {
                        currentText.append(resolved)
                    }
                }
                XmlPullParser.START_TAG -> {
                    if (parser.name == "title") {
                        insideTitle = true
                        currentText.clear()
                    }
                }
                XmlPullParser.TEXT, XmlPullParser.CDSECT -> {
                    if (insideTitle) {
                        currentText.append(parser.text)
                    }
                }
                XmlPullParser.END_TAG -> {
                    if (parser.name == "title" && insideTitle) {
                        val trimmed = currentText.toString().trim()
                        if (trimmed.isNotEmpty()) {
                            titles.add(trimmed)
                        }
                        insideTitle = false
                    }
                }
            }
            eventType = parser.nextToken()
        }
        return titles
    }

    private fun readBoundedBytes(input: InputStream, maxBytes: Int): ByteArray {
        val out = ByteArrayOutputStream()
        val buf = ByteArray(8192)
        var total = 0
        var read = input.read(buf)
        while (read != -1) {
            total += read
            if (total > maxBytes) {
                throw IOException("XML input exceeds maximum allowed size")
            }
            out.write(buf, 0, read)
            read = input.read(buf)
        }
        return out.toByteArray()
    }
}
// [END android_security_input_xxe_xml_pull_parser]

// [START android_security_input_error_handling]
object SafeInputErrorHandler {

    private const val TAG = "SafeInputErrorHandler"

    fun loadReportSummarySafely(
        context: Context,
        userInputFilename: String,
        db: SQLiteDatabase,
        onStatus: (String) -> Unit,
    ) {
        try {
            val file = CanonicalPathValidator.resolveSafeFile(context, userInputFilename)
            val firstLine = file.bufferedReader().use { it.readLine().orEmpty() }
            db.rawQuery("SELECT _id FROM reports WHERE summary = ?", arrayOf(firstLine)).close()
            onStatus("Report verified")
        } catch (e: SecurityException) {
            Log.w(TAG, "Rejected invalid report path")
            onStatus("Unable to load the requested report.")
        } catch (e: SQLiteException) {
            Log.e(TAG, "Database query failed while verifying report")
            onStatus("Unable to load the requested report.")
        } catch (e: IOException) {
            Log.w(TAG, "I/O failure while reading report")
            onStatus("Unable to load the requested report.")
        }
    }
}
// [END android_security_input_error_handling]
