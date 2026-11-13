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

package com.example.snippets.security.crypto

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import java.io.IOException
import java.security.InvalidAlgorithmParameterException
import java.security.InvalidKeyException
import java.security.KeyStore
import java.security.KeyStoreException
import java.security.NoSuchAlgorithmException
import java.security.NoSuchProviderException
import java.security.UnrecoverableEntryException
import java.security.cert.CertificateException
import javax.crypto.BadPaddingException
import javax.crypto.Cipher
import javax.crypto.IllegalBlockSizeException
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

// [START android_security_hardcoded_secrets_keystore]
private val ANDROID_KEY_STORE_PROVIDER = "AndroidKeyStore"
private val ANDROID_KEY_STORE_ALIAS = "AES_KEY_DEMO"

@Throws(
    KeyStoreException::class,
    NoSuchAlgorithmException::class,
    NoSuchProviderException::class,
    InvalidAlgorithmParameterException::class
)
private fun createAndStoreSecretKey() {
    val builder: KeyGenParameterSpec.Builder = KeyGenParameterSpec.Builder(
        ANDROID_KEY_STORE_ALIAS,
        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
    )
    val keySpec: KeyGenParameterSpec = builder
        .setKeySize(256)
        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
        .setRandomizedEncryptionRequired(true)
        .build()
    val aesKeyGenerator: KeyGenerator =
        KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, ANDROID_KEY_STORE_PROVIDER)
    aesKeyGenerator.init(keySpec)
    val key: SecretKey = aesKeyGenerator.generateKey()
}

@Throws(
    KeyStoreException::class,
    UnrecoverableEntryException::class,
    NoSuchAlgorithmException::class,
    CertificateException::class,
    IOException::class,
    NoSuchPaddingException::class,
    InvalidKeyException::class,
    IllegalBlockSizeException::class,
    BadPaddingException::class
)
private fun encryptWithKeyStore(plainText: String): ByteArray? {
    // Initialize KeyStore.
    val keyStore: KeyStore = KeyStore.getInstance(ANDROID_KEY_STORE_PROVIDER)
    keyStore.load(null)
    // Retrieve the key with alias androidKeyStoreAlias created before.
    val keyEntry: KeyStore.SecretKeyEntry =
        keyStore.getEntry(ANDROID_KEY_STORE_ALIAS, null) as KeyStore.SecretKeyEntry
    val key: SecretKey = keyEntry.secretKey
    // Use the secret key at your convenience.
    val cipher: Cipher = Cipher.getInstance("AES/GCM/NoPadding")
    cipher.init(Cipher.ENCRYPT_MODE, key)
    return cipher.doFinal(plainText.toByteArray())
}
// [END android_security_hardcoded_secrets_keystore]
