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

package com.example.snippets.security.jni

import android.content.Context
import android.os.ParcelFileDescriptor
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.annotation.Keep
import java.io.File
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

// [START android_security_jni_bounds_kotlin]
object SecureNativeDataProcessor {

    private const val MAX_PACKET_SIZE = 2048

    init {
        System.loadLibrary("secure_bridge")
    }

    // Keep raw external declarations private so callers cannot bypass validation
    @JvmStatic
    private external fun processPacketNative(packet: ByteArray, length: Int): Int

    @JvmStatic
    fun processPacketSafely(packet: ByteArray): Int {
        require(packet.isNotEmpty()) {
            "Packet must not be empty"
        }
        require(packet.size <= MAX_PACKET_SIZE) {
            "Packet size (${packet.size}) exceeds maximum ($MAX_PACKET_SIZE)"
        }
        return processPacketNative(packet, packet.size)
    }
}
// [END android_security_jni_bounds_kotlin]

// [START android_security_jni_toctou_fd_kotlin]
class SecureNativeFileLoader(private val context: Context) {

    companion object {
        init {
            System.loadLibrary("secure_bridge")
        }
    }

    fun readVerifiedHeader(fileName: String): String {
        val baseDir = context.filesDir.canonicalFile
        val targetFile = File(baseDir, fileName).canonicalFile
        require(targetFile.path.startsWith("${baseDir.path}${File.separator}")) {
            "Invalid file path outside app files directory"
        }

        // Open the file in managed code and pass only the integer FD to C++
        // to remove the managed-to-native path re-open race window.
        return ParcelFileDescriptor.open(
            targetFile,
            ParcelFileDescriptor.MODE_READ_ONLY,
        ).use { pfd ->
            readHeaderFromFdNative(pfd.fd)
        }
    }

    private external fun readHeaderFromFdNative(fd: Int): String
}
// [END android_security_jni_toctou_fd_kotlin]

// [START android_security_jni_keystore_kotlin]
object SecureNativeCryptoBridge {

    private const val ANDROID_KEYSTORE = "AndroidKeyStore"
    private const val KEY_ALIAS = "app_master_aes_key"
    private const val TRANSFORMATION = "AES/GCM/NoPadding"

    init {
        System.loadLibrary("secure_bridge")
    }

    @JvmStatic
    private external fun framePlaintextNative(plaintext: ByteArray): ByteArray

    @JvmStatic
    fun encryptAndProcess(payload: ByteArray): ByteArray {
        val framedPlaintext = framePlaintextNative(payload)
        try {
            val cipher = Cipher.getInstance(TRANSFORMATION)
            cipher.init(Cipher.ENCRYPT_MODE, getOrCreateSecretKey())
            val iv = cipher.iv
            val encrypted = cipher.doFinal(framedPlaintext)

            // Prepend IV length (1 byte) + IV + authenticated ciphertext
            val output = ByteArray(1 + iv.size + encrypted.size)
            output[0] = iv.size.toByte()
            System.arraycopy(iv, 0, output, 1, iv.size)
            System.arraycopy(encrypted, 0, output, 1 + iv.size, encrypted.size)
            return output
        } finally {
            framedPlaintext.fill(0)
        }
    }

    private fun getOrCreateSecretKey(): SecretKey {
        val keyStore = KeyStore.getInstance(ANDROID_KEYSTORE).apply { load(null) }
        val existing = keyStore.getEntry(KEY_ALIAS, null) as? KeyStore.SecretKeyEntry
        if (existing != null) {
            return existing.secretKey
        }
        val keyGenerator = KeyGenerator.getInstance(
            KeyProperties.KEY_ALGORITHM_AES,
            ANDROID_KEYSTORE,
        )
        val spec = KeyGenParameterSpec.Builder(
            KEY_ALIAS,
            KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT,
        )
            .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
            .setKeySize(256)
            .build()
        keyGenerator.init(spec)
        return keyGenerator.generateKey()
    }
}
// [END android_security_jni_keystore_kotlin]

@Keep
interface NativeStatusListener {
    @Keep
    fun onStatusUpdate(statusCode: Int)
}

class SecureSessionMonitor {
    companion object {
        init {
            System.loadLibrary("secure_bridge")
        }
    }

    external fun registerListener(listener: NativeStatusListener)
    external fun dispatchBackgroundCheck()
    external fun unregisterListener()
}

@Keep
class ConnectionOptions(
    @Keep val host: String,
    @Keep val accountId: String,
) {
    @Keep
    fun buildAuthHeader(): String {
        check(accountId.isNotBlank()) { "Account ID must not be blank" }
        return "$host:$accountId"
    }
}

object SecureConfigParser {
    init {
        System.loadLibrary("secure_bridge")
    }

    @JvmStatic
    external fun validateOptions(options: ConnectionOptions): Boolean
}
