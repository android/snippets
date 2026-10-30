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
import android.util.Log
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.Signature
import java.util.Enumeration

private const val TAG = "Keystore"

private fun generateKeyPair(alias: String) {
    // [START android_security_keystore_generate_key_pair]
    /*
     * Generate a new EC key pair entry in the Android Keystore by
     * using the KeyPairGenerator API. The private key can only be
     * used for signing or verification and only with SHA-256 or
     * SHA-512 as the message digest.
     */
    val kpg: KeyPairGenerator = KeyPairGenerator.getInstance(
        KeyProperties.KEY_ALGORITHM_EC,
        "AndroidKeyStore"
    )
    val parameterSpec: KeyGenParameterSpec = KeyGenParameterSpec.Builder(
        alias,
        KeyProperties.PURPOSE_SIGN or KeyProperties.PURPOSE_VERIFY
    ).run {
        setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
        build()
    }

    kpg.initialize(parameterSpec)

    val kp = kpg.generateKeyPair()
    // [END android_security_keystore_generate_key_pair]
}

private fun listEntries() {
    // [START android_security_keystore_list_entries]
    /*
     * Load the Android KeyStore instance using the
     * AndroidKeyStore provider to list the currently stored entries.
     */
    val ks: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }
    val aliases: Enumeration<String> = ks.aliases()
    // [END android_security_keystore_list_entries]
}

private fun signData(alias: String, data: ByteArray): ByteArray? {
    // [START android_security_keystore_sign_data]
    /*
     * Use a PrivateKey in the KeyStore to create a signature over
     * some data.
     */
    val ks: KeyStore = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }
    val entry: KeyStore.Entry = ks.getEntry(alias, null)
    if (entry !is KeyStore.PrivateKeyEntry) {
        Log.w(TAG, "Not an instance of a PrivateKeyEntry")
        return null
    }
    val signature: ByteArray = Signature.getInstance("SHA256withECDSA").run {
        initSign(entry.privateKey)
        update(data)
        sign()
    }
    // [END android_security_keystore_sign_data]
    return signature
}

private fun verifyData(alias: String, data: ByteArray, signature: ByteArray): Boolean {
    // [START android_security_keystore_verify_data]
    /*
     * Verify a signature previously made by a private key in the
     * KeyStore. This uses the X.509 certificate attached to the
     * private key in the KeyStore to validate a previously
     * generated signature.
     */
    val ks = KeyStore.getInstance("AndroidKeyStore").apply {
        load(null)
    }
    val entry = ks.getEntry(alias, null) as? KeyStore.PrivateKeyEntry
    if (entry == null) {
        Log.w(TAG, "Not an instance of a PrivateKeyEntry")
        return false
    }
    val valid: Boolean = Signature.getInstance("SHA256withECDSA").run {
        initVerify(entry.certificate)
        update(data)
        verify(signature)
    }
    // [END android_security_keystore_verify_data]
    return valid
}
