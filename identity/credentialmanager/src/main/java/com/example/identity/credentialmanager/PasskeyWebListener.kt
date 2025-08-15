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

import android.app.Activity
import android.net.Uri
import android.util.Log
import android.webkit.WebView
import android.widget.Toast
import androidx.annotation.UiThread
import androidx.credentials.PublicKeyCredential
import androidx.credentials.exceptions.CreateCredentialException
import androidx.credentials.exceptions.GetCredentialException
import androidx.webkit.JavaScriptReplyProxy
import androidx.webkit.WebMessageCompat
import androidx.webkit.WebViewCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

// Placeholder for TAG log value.
const val TAG = ""

// This class is mostly copied from https://github.com/android/identity-samples/blob/main/WebView/CredentialManagerWebView/PasskeyWebListener.kt.

// [START android_identity_create_listener_passkeys]
// The class talking to Javascript should inherit:
class PasskeyWebListener(
    private val activity: Activity,
    private val coroutineScope: CoroutineScope,
    private val credentialManagerHandler: CredentialManagerHandler
) : WebViewCompat.WebMessageListener {
    /** havePendingRequest is true if there is an outstanding WebAuthn request.
     There is only ever one request outstanding at a time. */
    private var havePendingRequest = false

    /** pendingRequestIsDoomed is true if the WebView has navigated since
     starting a request. The FIDO module cannot be canceled, but the response
     will never be delivered in this case. */
    private var pendingRequestIsDoomed = false

    /** replyChannel is the port that the page is listening for a response on.
     It is valid if havePendingRequest is true. */
    private var replyChannel: ReplyChannel? = null

    /**
     * Called by the page during a WebAuthn request.
     *
     * @param view Creates the WebView.
     * @param message The message sent from the client using injected JavaScript.
     * @param sourceOrigin The origin of the HTTPS request. Should not be null.
     * @param isMainFrame Should be set to true. Embedded frames are not
     supported.
     * @param replyProxy Passed in by JavaScript. Allows replying when wrapped in
     the Channel.
     * @return The message response.
     */
    @UiThread
    override fun onPostMessage(
        view: WebView,
        message: WebMessageCompat,
        sourceOrigin: Uri,
        isMainFrame: Boolean,
        replyProxy: JavaScriptReplyProxy,
    ) {
        val messageData = message.data ?: return
        onRequest(
            messageData,
            sourceOrigin,
            isMainFrame,
            JavaScriptReplyChannel(replyProxy)
        )
    }

    private fun onRequest(
        msg: String,
        sourceOrigin: Uri,
        isMainFrame: Boolean,
        reply: ReplyChannel,
    ) {
        msg?.let {
            val jsonObj = JSONObject(msg)
            val type = jsonObj.getString(TYPE_KEY)
            val message = jsonObj.getString(REQUEST_KEY)

            if (havePendingRequest) {
                postErrorMessage(reply, "The request already in progress", type)
                return
            }

            replyChannel = reply
            if (!isMainFrame) {
                reportFailure("Requests from subframes are not supported", type)
                return
            }
            val originScheme = sourceOrigin.scheme
            if (originScheme == null || originScheme.lowercase() != "https") {
                reportFailure("WebAuthn not permitted for current URL", type)
                return
            }

            // Verify that origin belongs to your website,
            // it's because the unknown origin may gain credential info.
            // if (isUnknownOrigin(originScheme)) {
            // return
            // }

            havePendingRequest = true
            pendingRequestIsDoomed = false

            // Use a temporary "replyCurrent" variable to send the data back, while
            // resetting the main "replyChannel" variable to null so it’s ready for
            // the next request.
            val replyCurrent = replyChannel
            if (replyCurrent == null) {
                Log.i(TAG, "The reply channel was null, cannot continue")
                return
            }

            when (type) {
                CREATE_UNIQUE_KEY ->
                    this.coroutineScope.launch {
                        handleCreateFlow(credentialManagerHandler, message, replyCurrent)
                    }

                GET_UNIQUE_KEY -> this.coroutineScope.launch {
                    handleGetFlow(credentialManagerHandler, message, replyCurrent)
                }

                else -> Log.i(TAG, "Incorrect request json")
            }
        }
    }

    private suspend fun handleCreateFlow(
        credentialManagerHandler: CredentialManagerHandler,
        message: String,
        reply: ReplyChannel,
    ) {
        try {
            havePendingRequest = false
            pendingRequestIsDoomed = false
            val response = credentialManagerHandler.createPasskey(message)
            val successArray = ArrayList<Any>()
            successArray.add("success")
            successArray.add(JSONObject(response.registrationResponseJson))
            successArray.add(CREATE_UNIQUE_KEY)
            reply.send(JSONArray(successArray).toString())
            replyChannel = null // setting initial replyChannel for the next request
        } catch (e: CreateCredentialException) {
            reportFailure(
                "Error: ${e.errorMessage} w type: ${e.type} w obj: $e",
                CREATE_UNIQUE_KEY
            )
        } catch (t: Throwable) {
            reportFailure("Error: ${t.message}", CREATE_UNIQUE_KEY)
        }
    }

    companion object {
        /** INTERFACE_NAME is the name of the MessagePort that must be injected into pages. */
        const val INTERFACE_NAME = "__webauthn_interface__"
        const val TYPE_KEY = "type"
        const val REQUEST_KEY = "request"
        const val CREATE_UNIQUE_KEY = "create"
        const val GET_UNIQUE_KEY = "get"
        /** INJECTED_VAL is the minified version of the JavaScript code described at this class
         * heading. The non minified form is found at credmanweb/javascript/encode.js.*/
        const val INJECTED_VAL = """
            var __webauthn_interface__,__webauthn_hooks__;!function(e){console.log("In the hook."),__webauthn_interface__.addEventListener("message",function e(n){var r=JSON.parse(n.data),t=r[2];"get"===t?o(r):"create"===t?u(r):console.log("Incorrect response format for reply")});var n=null,r=null,t=null,a=null;function o(e){if(null!==n&&null!==t){if("success"!=e[0]){var r=t;n=null,t=null,r(new DOMException(e[1],"NotAllowedError"));return}var a=i(e[1]),o=n;n=null,t=null,o(a)}}function l(e){var n=e.length%4;return Uint8Array.from(atob(e.replace(/-/g,"+").replace(/_/g,"/").padEnd(e.length+(0===n?0:4-n),"=")),function(e){return e.charCodeAt(0)}).buffer}function s(e){return btoa(Array.from(new Uint8Array(e),function(e){return String.fromCharCode(e)}).join("")).replace(/\+/g,"-").replace(/\//g,"_").replace(/=+${'$'}/,"")}function u(e){if(null===r||null===a){console.log("Here: "+r+" and reject: "+a);return}if(console.log("Output back: "+e),"success"!=e[0]){var n=a;r=null,a=null,n(new DOMException(e[1],"NotAllowedError"));return}var t=i(e[1]),o=r;r=null,a=null,o(t)}function i(e){return console.log("Here is the response from credential manager: "+e),e.rawId=l(e.rawId),e.response.clientDataJSON=l(e.response.clientDataJSON),e.response.hasOwnProperty("attestationObject")&&(e.response.attestationObject=l(e.response.attestationObject)),e.response.hasOwnProperty("authenticatorData")&&(e.response.authenticatorData=l(e.response.authenticatorData)),e.response.hasOwnProperty("signature")&&(e.response.signature=l(e.response.signature)),e.response.hasOwnProperty("userHandle")&&(e.response.userHandle=l(e.response.userHandle)),e.getClientExtensionResults=function e(){return{}},e}e.create=function n(t){if(!("publicKey"in t))return e.originalCreateFunction(t);var o=new Promise(function(e,n){r=e,a=n}),l=t.publicKey;if(l.hasOwnProperty("challenge")){var u=s(l.challenge);l.challenge=u}if(l.hasOwnProperty("user")&&l.user.hasOwnProperty("id")){var i=s(l.user.id);l.user.id=i}var c=JSON.stringify({type:"create",request:l});return __webauthn_interface__.postMessage(c),o},e.get=function r(a){if(!("publicKey"in a))return e.originalGetFunction(a);var o=new Promise(function(e,r){n=e,t=r}),l=a.publicKey;if(l.hasOwnProperty("challenge")){var u=s(l.challenge);l.challenge=u}var i=JSON.stringify({type:"get",request:l});return __webauthn_interface__.postMessage(i),o},e.onReplyGet=o,e.CM_base64url_decode=l,e.CM_base64url_encode=s,e.onReplyCreate=u}(__webauthn_hooks__||(__webauthn_hooks__={})),__webauthn_hooks__.originalGetFunction=navigator.credentials.get,__webauthn_hooks__.originalCreateFunction=navigator.credentials.create,navigator.credentials.get=__webauthn_hooks__.get,navigator.credentials.create=__webauthn_hooks__.create,window.PublicKeyCredential=function(){},window.PublicKeyCredential.isUserVerifyingPlatformAuthenticatorAvailable=function(){return Promise.resolve(!1)};
        """
    }
    // [END android_identity_create_listener_passkeys]

    // Handles the get flow in a less error-prone way
    private suspend fun handleGetFlow(
        credentialManagerHandler: CredentialManagerHandler,
        message: String,
        reply: ReplyChannel,
    ) {
        try {
            havePendingRequest = false
            pendingRequestIsDoomed = false
            val r = credentialManagerHandler.getPasskey(message)
            val successArray = ArrayList<Any>()
            successArray.add("success")
            successArray.add(
                JSONObject(
                    (r.credential as PublicKeyCredential).authenticationResponseJson
                )
            )
            successArray.add(GET_UNIQUE_KEY)
            reply.send(JSONArray(successArray).toString())
            replyChannel = null // setting initial replyChannel for next request given temp 'reply'
        } catch (e: GetCredentialException) {
            reportFailure("Error: ${e.errorMessage} w type: ${e.type} w obj: $e", GET_UNIQUE_KEY)
        } catch (t: Throwable) {
            reportFailure("Error: ${t.message}", GET_UNIQUE_KEY)
        }
    }

    /** Sends an error result to the page.  */
    private fun reportFailure(message: String, type: String) {
        havePendingRequest = false
        pendingRequestIsDoomed = false
        val reply: ReplyChannel = replyChannel!! // verifies non null by throwing NPE
        replyChannel = null
        postErrorMessage(reply, message, type)
    }

    private fun postErrorMessage(reply: ReplyChannel, errorMessage: String, type: String) {
        Log.i(TAG, "Sending error message back to the page via replyChannel $errorMessage")
        val array: MutableList<Any?> = ArrayList()
        array.add("error")
        array.add(errorMessage)
        array.add(type)
        reply.send(JSONArray(array).toString())
        var toastMsg = errorMessage
        Toast.makeText(this.activity.applicationContext, toastMsg, Toast.LENGTH_SHORT).show()
    }

    // [START android_identity_javascript_reply_channel]
    // The setup for the reply channel allows communication with JavaScript.
    private class JavaScriptReplyChannel(private val reply: JavaScriptReplyProxy) :
        ReplyChannel {
        override fun send(message: String?) {
            try {
                reply.postMessage(message!!)
            } catch (t: Throwable) {
                Log.i(TAG, "Reply failure due to: " + t.message)
            }
        }
    }

    // ReplyChannel is the interface where replies to the embedded site are
    // sent. This allows for testing since AndroidX bans mocking its objects.
    interface ReplyChannel {
        fun send(message: String?)
    }
    // [END android_identity_javascript_reply_channel]
}
