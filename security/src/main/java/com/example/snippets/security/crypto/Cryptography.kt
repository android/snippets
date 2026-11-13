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

import android.annotation.SuppressLint
import java.security.Key
import java.security.MessageDigest
import java.security.PrivateKey
import java.security.PublicKey
import java.security.SecureRandom
import java.security.Security
import java.security.Signature
import java.security.spec.MGF1ParameterSpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.OAEPParameterSpec
import javax.crypto.spec.PSource

private fun encryptMessage(plaintextBytes: ByteArray) {
    // [START android_security_cryptography_encrypt_message]
    // [START_EXCLUDE silent]
    /*
    // [END_EXCLUDE]
    val plaintext: ByteArray = ...
    // [START_EXCLUDE silent]
     */
    val plaintext: ByteArray = plaintextBytes
    // [END_EXCLUDE]
    val keygen = KeyGenerator.getInstance("AES")
    keygen.init(256)
    val key: SecretKey = keygen.generateKey()
    val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
    cipher.init(Cipher.ENCRYPT_MODE, key)
    val ciphertext: ByteArray = cipher.doFinal(plaintext)
    val iv: ByteArray = cipher.iv
    // [END android_security_cryptography_encrypt_message]
}

private fun generateMessageDigest(messageBytes: ByteArray) {
    // [START android_security_cryptography_message_digest]
    // [START_EXCLUDE silent]
    /*
    // [END_EXCLUDE]
    val message: ByteArray = ...
    // [START_EXCLUDE silent]
     */
    val message: ByteArray = messageBytes
    // [END_EXCLUDE]
    val md = MessageDigest.getInstance("SHA-256")
    val digest: ByteArray = md.digest(message)
    // [END android_security_cryptography_message_digest]
}

private fun generateSignature(messageBytes: ByteArray, privateKey: PrivateKey) {
    // [START android_security_cryptography_generate_signature]
    // [START_EXCLUDE silent]
    /*
    // [END_EXCLUDE]
    val message: ByteArray = ...
    val key: PrivateKey = ...
    // [START_EXCLUDE silent]
     */
    val message: ByteArray = messageBytes
    val key: PrivateKey = privateKey
    // [END_EXCLUDE]
    val s = Signature.getInstance("SHA256withECDSA")
        .apply {
            initSign(key)
            update(message)
        }
    val signature: ByteArray = s.sign()
    // [END android_security_cryptography_generate_signature]
}

private fun verifySignature(
    messageBytes: ByteArray,
    signatureBytes: ByteArray,
    publicKey: PublicKey,
) {
    // [START android_security_cryptography_verify_signature]
    // [START_EXCLUDE silent]
    /*
    // [END_EXCLUDE]
    val message: ByteArray = ...
    val signature: ByteArray = ...
    val key: PublicKey = ...
    // [START_EXCLUDE silent]
     */
    val message: ByteArray = messageBytes
    val signature: ByteArray = signatureBytes
    val key: PublicKey = publicKey
    // [END_EXCLUDE]
    val s = Signature.getInstance("SHA256withECDSA")
        .apply {
            initVerify(key)
            update(message)
        }
    val valid: Boolean = s.verify(signature)
    // [END android_security_cryptography_verify_signature]
}

private fun configureOaepMgf1Digest(rsaKey: Key) {
    // [START android_security_cryptography_oaep_mgf1_digest]
    // [START_EXCLUDE silent]
    /*
    // [END_EXCLUDE]
    val key: Key = ...
    // [START_EXCLUDE silent]
     */
    val key: Key = rsaKey
    // [END_EXCLUDE]
    val cipher = Cipher.getInstance("RSA/ECB/OAEPPadding")
        .apply {
            // To use SHA-256 the main digest and SHA-1 as the MGF1 digest.
            init(
                Cipher.ENCRYPT_MODE,
                key,
                OAEPParameterSpec(
                    "SHA-256",
                    "MGF1",
                    MGF1ParameterSpec.SHA1,
                    PSource.PSpecified.DEFAULT
                )
            )
            // To use SHA-256 for both digests.
            init(
                Cipher.ENCRYPT_MODE,
                key,
                OAEPParameterSpec(
                    "SHA-256",
                    "MGF1",
                    MGF1ParameterSpec.SHA256,
                    PSource.PSpecified.DEFAULT
                )
            )
        }
    // [END android_security_cryptography_oaep_mgf1_digest]
}

private fun bouncyCastleDeprecated() {
    // [START android_security_cryptography_bouncycastle_deprecated]
    Cipher.getInstance("AES/CBC/PKCS7PADDING", "BC")
    // OR
    Cipher.getInstance("AES/CBC/PKCS7PADDING", Security.getProvider("BC"))
    // [END android_security_cryptography_bouncycastle_deprecated]
}

private fun pbeExplicitIv(secretKey: SecretKey) {
    // [START android_security_cryptography_pbe_explicit_iv]
    // [START_EXCLUDE silent]
    /*
    // [END_EXCLUDE]
    val key: SecretKey = ...
    // [START_EXCLUDE silent]
     */
    val key: SecretKey = secretKey
    // [END_EXCLUDE]
    val cipher = Cipher.getInstance("PBEWITHSHA256AND256BITAES-CBC-BC")
    val iv = ByteArray(16)
    SecureRandom().nextBytes(iv)
    cipher.init(Cipher.ENCRYPT_MODE, key, IvParameterSpec(iv))
    // [END android_security_cryptography_pbe_explicit_iv]
}

@SuppressLint("DeletedProvider")
private fun cryptoProviderRemoved() {
    // [START android_security_cryptography_crypto_provider_removed]
    SecureRandom.getInstance("SHA1PRNG", "Crypto")
    // [END android_security_cryptography_crypto_provider_removed]
}
