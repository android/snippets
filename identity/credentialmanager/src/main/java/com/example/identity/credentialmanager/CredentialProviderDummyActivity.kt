/*
 * Copyright 2025 The Android Open Source Project
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

package com.example.identity.credentialmanager

import android.annotation.SuppressLint
import android.app.Activity
import android.app.PendingIntent
import android.content.Intent
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.os.PersistableBundle
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.biometric.BiometricPrompt.AuthenticationCallback
import androidx.biometric.BiometricPrompt.AuthenticationResult
import androidx.credentials.CreatePasswordRequest
import androidx.credentials.CreatePasswordResponse
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPasswordOption
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.PasswordCredential
import androidx.credentials.PublicKeyCredential
import androidx.credentials.provider.BeginCreateCredentialRequest
import androidx.credentials.provider.BeginCreateCredentialResponse
import androidx.credentials.provider.BeginCreatePasswordCredentialRequest
import androidx.credentials.provider.BeginCreatePublicKeyCredentialRequest
import androidx.credentials.provider.CallingAppInfo
import androidx.credentials.provider.CreateEntry
import androidx.credentials.provider.PendingIntentHandler
import androidx.credentials.webauthn.AuthenticatorAssertionResponse
import androidx.credentials.webauthn.AuthenticatorAttestationResponse
import androidx.credentials.webauthn.FidoPublicKeyCredential
import androidx.credentials.webauthn.PublicKeyCredentialCreationOptions
import androidx.credentials.webauthn.PublicKeyCredentialRequestOptions
import androidx.fragment.app.FragmentActivity
import java.math.BigInteger
import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.MessageDigest
import java.security.SecureRandom
import java.security.Signature
import java.security.interfaces.ECPrivateKey
import java.security.spec.ECGenParameterSpec
import java.security.spec.ECParameterSpec
import java.security.spec.ECPoint
import java.security.spec.EllipticCurve

class CredentialProviderDummyActivity : FragmentActivity() {

    private val PERSONAL_ACCOUNT_ID: String = ""
    private val FAMILY_ACCOUNT_ID: String = ""
    private val CREATE_PASSWORD_INTENT: String = ""

    @RequiresApi(VERSION_CODES.M)
    // [START android_identity_credential_provider_handle_passkey]
    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        // ...

        val request =
            PendingIntentHandler.retrieveProviderCreateCredentialRequest(intent)

        val accountId = intent.getStringExtra(CredentialsRepo.EXTRA_KEY_ACCOUNT_ID)
        if (request != null && request.callingRequest is CreatePublicKeyCredentialRequest) {
            val publicKeyRequest: CreatePublicKeyCredentialRequest =
                request.callingRequest as CreatePublicKeyCredentialRequest
            createPasskey(
                publicKeyRequest.requestJson,
                request.callingAppInfo,
                publicKeyRequest.clientDataHash,
                accountId
            )
        }
    }

    @SuppressLint("RestrictedApi")
    fun createPasskey(
        requestJson: String,
        callingAppInfo: CallingAppInfo?,
        clientDataHash: ByteArray?,
        accountId: String?
    ) {
        val request = PublicKeyCredentialCreationOptions(requestJson)

        val biometricPrompt = BiometricPrompt(
            this,
            { }, // Pass in your own executor
            object : AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    finish()
                }

                @RequiresApi(VERSION_CODES.P)
                override fun onAuthenticationSucceeded(
                    result: AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)

                    // Generate a credentialId
                    val credentialId = ByteArray(32)
                    SecureRandom().nextBytes(credentialId)

                    // Generate a credential key pair
                    val spec = ECGenParameterSpec("secp256r1")
                    val keyPairGen = KeyPairGenerator.getInstance("EC")
                    keyPairGen.initialize(spec)
                    val keyPair = keyPairGen.genKeyPair()

                    // Save passkey in your database as per your own implementation

                    // Create AuthenticatorAttestationResponse object to pass to
                    // FidoPublicKeyCredential

                    val response = AuthenticatorAttestationResponse(
                        requestOptions = request,
                        credentialId = credentialId,
                        credentialPublicKey = getPublicKeyFromKeyPair(keyPair),
                        origin = appInfoToOrigin(callingAppInfo!!),
                        up = true,
                        uv = true,
                        be = true,
                        bs = true,
                        packageName = callingAppInfo.packageName
                    )

                    val credential = FidoPublicKeyCredential(
                        rawId = credentialId,
                        response = response,
                        authenticatorAttachment = "", // Add your authenticator attachment
                    )
                    val result = Intent()

                    val createPublicKeyCredResponse =
                        CreatePublicKeyCredentialResponse(credential.json())

                    // Set the CreateCredentialResponse as the result of the Activity
                    PendingIntentHandler.setCreateCredentialResponse(
                        result,
                        createPublicKeyCredResponse
                    )
                    setResult(RESULT_OK, result)
                    finish()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Use your screen lock")
            .setSubtitle("Create passkey for ${request.rp.name}")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG
                /* or BiometricManager.Authenticators.DEVICE_CREDENTIAL */
            )
            .build()
        biometricPrompt.authenticate(promptInfo)
    }

    @RequiresApi(VERSION_CODES.P)
    fun appInfoToOrigin(info: CallingAppInfo): String {
        val cert = info.signingInfo.apkContentsSigners[0].toByteArray()
        val md = MessageDigest.getInstance("SHA-256")
        val certHash = md.digest(cert)
        // This is the format for origin
        return "android:apk-key-hash:${b64Encode(certHash)}"
    }
    // [END android_identity_credential_provider_handle_passkey]

    @RequiresApi(VERSION_CODES.M)
    // [START android_identity_credential_provider_password_creation]
    fun processCreateCredentialRequest(
        request: BeginCreateCredentialRequest
    ): BeginCreateCredentialResponse? {
        when (request) {
            is BeginCreatePublicKeyCredentialRequest -> {
                // Request is passkey type
                return handleCreatePasskeyQuery(request)
            }

            is BeginCreatePasswordCredentialRequest -> {
                // Request is password type
                return handleCreatePasswordQuery(request)
            }
        }
        return null
    }

    @RequiresApi(VERSION_CODES.M)
    private fun handleCreatePasswordQuery(
        request: BeginCreatePasswordCredentialRequest
    ): BeginCreateCredentialResponse {
        val createEntries: MutableList<CreateEntry> = mutableListOf()

        // Adding two create entries - one for storing credentials to the 'Personal'
        // account, and one for storing them to the 'Family' account. These
        // accounts are local to this sample app only.
        createEntries.add(
            CreateEntry(
                PERSONAL_ACCOUNT_ID,
                createNewPendingIntent(PERSONAL_ACCOUNT_ID, CREATE_PASSWORD_INTENT)
            )
        )
        createEntries.add(
            CreateEntry(
                FAMILY_ACCOUNT_ID,
                createNewPendingIntent(FAMILY_ACCOUNT_ID, CREATE_PASSWORD_INTENT)
            )
        )

        return BeginCreateCredentialResponse(createEntries)
    }
    // [END android_identity_credential_provider_password_creation]

    @RequiresApi(VERSION_CODES.M)
    fun handleEntrySelectionForPasswordCreation(
        mDatabase: MyDatabase
    ) {
        // [START android_identity_credential_provider_entry_selection_password_creation]
        val createRequest = PendingIntentHandler.retrieveProviderCreateCredentialRequest(intent)
        val accountId = intent.getStringExtra(CredentialsRepo.EXTRA_KEY_ACCOUNT_ID)

        if (createRequest == null) {
            return
        }

        val request: CreatePasswordRequest = createRequest.callingRequest as CreatePasswordRequest

        // Fetch the ID and password from the request and save it in your database
        mDatabase.addNewPassword(
            PasswordInfo(
                request.id,
                request.password,
                createRequest.callingAppInfo.packageName
            )
        )

        // Set the final response back
        val result = Intent()
        val response = CreatePasswordResponse()
        PendingIntentHandler.setCreateCredentialResponse(result, response)
        setResult(Activity.RESULT_OK, result)
        finish()
        // [END android_identity_credential_provider_entry_selection_password_creation]
    }

    @RequiresApi(VERSION_CODES.P)
    private fun handleUserSelectionForPasskeys(
        mDatabase: MyDatabase
    ) {
        // [START android_identity_credential_provider_user_pk_selection]
        val getRequest = PendingIntentHandler.retrieveProviderGetCredentialRequest(intent)
        val publicKeyRequest = getRequest?.credentialOptions?.first() as GetPublicKeyCredentialOption

        val requestInfo = intent.getBundleExtra("CREDENTIAL_DATA")
        val credIdEnc = requestInfo?.getString("credId").orEmpty()

        // Get the saved passkey from your database based on the credential ID from the PublicKeyRequest
        val passkey = mDatabase.getPasskey(credIdEnc)

        // Decode the credential ID, private key and user ID
        val credId = b64Decode(credIdEnc)
        val privateKey = b64Decode(passkey.credPrivateKey)
        val uid = b64Decode(passkey.uid)

        val origin = appInfoToOrigin(getRequest.callingAppInfo)
        val packageName = getRequest.callingAppInfo.packageName

        validatePasskey(
            publicKeyRequest.requestJson,
            origin,
            packageName,
            uid,
            passkey.username,
            credId,
            privateKey
        )
        // [END android_identity_credential_provider_user_pk_selection]
    }

    @SuppressLint("RestrictedApi")
    @RequiresApi(VERSION_CODES.M)
    private fun validatePasskey(
        requestJson: String,
        origin: String,
        packageName: String,
        uid: ByteArray,
        username: String,
        credId: ByteArray,
        privateKeyBytes: ByteArray,
    ) {
        // [START android_identity_credential_provider_user_validation_biometric]
        val request = PublicKeyCredentialRequestOptions(requestJson)
        val privateKey: ECPrivateKey = convertPrivateKey(privateKeyBytes)

        val biometricPrompt = BiometricPrompt(
            this,
            { }, // Pass in your own executor
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(
                    errorCode: Int,
                    errString: CharSequence
                ) {
                    super.onAuthenticationError(errorCode, errString)
                    finish()
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    finish()
                }

                override fun onAuthenticationSucceeded(
                    result: BiometricPrompt.AuthenticationResult
                ) {
                    super.onAuthenticationSucceeded(result)
                    val response = AuthenticatorAssertionResponse(
                        requestOptions = request,
                        credentialId = credId,
                        origin = origin,
                        up = true,
                        uv = true,
                        be = true,
                        bs = true,
                        userHandle = uid,
                        packageName = packageName
                    )

                    val sig = Signature.getInstance("SHA256withECDSA")
                    sig.initSign(privateKey)
                    sig.update(response.dataToSign())
                    response.signature = sig.sign()

                    val credential = FidoPublicKeyCredential(
                        rawId = credId,
                        response = response,
                        authenticatorAttachment = "", // Add your authenticator attachment
                    )
                    val result = Intent()
                    val passkeyCredential = PublicKeyCredential(credential.json())
                    PendingIntentHandler.setGetCredentialResponse(
                        result, GetCredentialResponse(passkeyCredential)
                    )
                    setResult(RESULT_OK, result)
                    finish()
                }
            }
        )

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle("Use your screen lock")
            .setSubtitle("Use passkey for ${request.rpId}")
            .setAllowedAuthenticators(
                BiometricManager.Authenticators.BIOMETRIC_STRONG
                /* or BiometricManager.Authenticators.DEVICE_CREDENTIAL */
            )
            .build()
        biometricPrompt.authenticate(promptInfo)
        // [END android_identity_credential_provider_user_validation_biometric]
    }

    @RequiresApi(VERSION_CODES.M)
    private fun handleUserSelectionForPasswordAuthentication(
        mDatabase: MyDatabase,
        callingAppInfo: CallingAppInfo,
    ) {
        // [START android_identity_credential_provider_user_selection_password]
        val getRequest = PendingIntentHandler.retrieveProviderGetCredentialRequest(intent)

        val passwordOption = getRequest?.credentialOptions?.first() as GetPasswordOption

        val username = passwordOption.allowedUserIds.first()
        // Fetch the credentials for the calling app package name
        val creds = mDatabase.getCredentials(callingAppInfo.packageName)
        val passwords = creds.passwords
        val it = passwords.iterator()
        var password = ""
        while (it.hasNext()) {
            val passwordItemCurrent = it.next()
            if (passwordItemCurrent.username == username) {
                password = passwordItemCurrent.password
                break
            }
        }
        // [END android_identity_credential_provider_user_selection_password]

        // [START android_identity_credential_provider_set_response]
        // Set the response back
        val result = Intent()
        val passwordCredential = PasswordCredential(username, password)
        PendingIntentHandler.setGetCredentialResponse(
            result, GetCredentialResponse(passwordCredential)
        )
        setResult(Activity.RESULT_OK, result)
        finish()
        // [END android_identity_credential_provider_set_response]
    }

    // [START android_identity_credential_pending_intent]
    fun createSettingsPendingIntent(): PendingIntent { // [END android_identity_credential_pending_intent]
        return PendingIntent.getBroadcast(this, 0, Intent(), PendingIntent.FLAG_IMMUTABLE)
    }

    private fun getPublicKeyFromKeyPair(keyPair: KeyPair): ByteArray {
        return byteArrayOf()
    }

    private fun b64Encode(certHash: ByteArray) {}

    private fun handleCreatePasskeyQuery(
        request: BeginCreatePublicKeyCredentialRequest
    ): BeginCreateCredentialResponse {
        return BeginCreateCredentialResponse()
    }

    private fun createNewPendingIntent(
        accountId: String,
        intent: String
    ): PendingIntent {
        return PendingIntent.getBroadcast(this, 0, Intent(), PendingIntent.FLAG_IMMUTABLE)
    }

    private fun b64Decode(encodedString: String): ByteArray {
        return byteArrayOf()
    }

    private fun convertPrivateKey(privateKeyBytes: ByteArray): ECPrivateKey {
        return ECPrivateKeyImpl()
    }
}

object CredentialsRepo {
    const val EXTRA_KEY_ACCOUNT_ID: String = ""
}

class MyDatabase {
    fun addNewPassword(passwordInfo: PasswordInfo) {}

    fun getPasskey(credIdEnc: String): PasskeyInfo {
        return PasskeyInfo()
    }

    fun getCredentials(packageName: String): CredentialsInfo {
        return CredentialsInfo()
    }
}

data class PasswordInfo(
    val id: String = "",
    val password: String = "",
    val packageName: String = "",
    val username: String = ""
)

data class PasskeyInfo(
    val credPrivateKey: String = "",
    val uid: String = "",
    val username: String = ""
)

data class CredentialsInfo(
    val passwords: List<PasswordInfo> = listOf()
)

class ECPrivateKeyImpl : ECPrivateKey {
    override fun getAlgorithm(): String = ""
    override fun getFormat(): String = ""
    override fun getEncoded(): ByteArray = byteArrayOf()
    override fun getParams(): ECParameterSpec {
        return ECParameterSpec(
            EllipticCurve(
                { 0 },
                BigInteger.ZERO,
                BigInteger.ZERO
            ),
            ECPoint(
                BigInteger.ZERO,
                BigInteger.ZERO
            ),
            BigInteger.ZERO,
            0
        )
    }
    override fun getS(): BigInteger = BigInteger.ZERO
}
