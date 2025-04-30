package com.example.identity.credentialmanager

import android.app.Activity
import android.util.Log
import androidx.credentials.CreatePublicKeyCredentialRequest
import androidx.credentials.CreatePublicKeyCredentialResponse
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import androidx.credentials.GetPublicKeyCredentialOption
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialException

// This class is mostly copied from https://github.com/android/identity-samples/blob/main/WebView/CredentialManagerWebView/CredentialManagerHandler.kt.
class CredentialManagerHandler(private val activity: Activity) {
  private val mCredMan = CredentialManager.create(activity.applicationContext)
  private val TAG = "CredentialManagerHandler"
  /**
   * Encapsulates the create passkey API for credential manager in a less error-prone manner.
   *
   * @param request a create public key credential request JSON required by [CreatePublicKeyCredentialRequest].
   * @return [CreatePublicKeyCredentialResponse] containing the result of the credential creation.
   */
  suspend fun createPasskey(request: String): CreatePublicKeyCredentialResponse {
    val createRequest = CreatePublicKeyCredentialRequest(request)
    try {
      return mCredMan.createCredential(activity, createRequest) as CreatePublicKeyCredentialResponse
    } catch (e: CreateCredentialException) {
      // For error handling use guidance from https://developer.android.com/training/sign-in/passkeys
      Log.i(TAG, "Error creating credential: ErrMessage: ${e.errorMessage}, ErrType: ${e.type}")
      throw e
    }
  }

  /**
   * Encapsulates the get passkey API for credential manager in a less error-prone manner.
   *
   * @param request a get public key credential request JSON required by [GetCredentialRequest].
   * @return [GetCredentialResponse] containing the result of the credential retrieval.
   */
  suspend fun getPasskey(request: String): GetCredentialResponse {
    val getRequest = GetCredentialRequest(listOf(GetPublicKeyCredentialOption(request, null)))
    try {
      return mCredMan.getCredential(activity, getRequest)
    } catch (e: GetCredentialException) {
      // For error handling use guidance from https://developer.android.com/training/sign-in/passkeys
      Log.i(TAG, "Error retrieving credential: ${e.message}")
      throw e
    }
  }
}
