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

package com.example.snippets.backgroundwork.aidl

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.os.Parcel
import android.os.Parcelable
import android.os.Process
import android.util.Log
import androidx.core.os.BundleCompat
import com.example.android.IRectInsideBundle
import com.example.android.IRemoteService

private object ImplementStubSnippet {
    // [START android_backgroundwork_aidl_implement_stub]
    private val binder = object : IRemoteService.Stub() {

        override fun getPid(): Int =
            Process.myPid()

        override fun basicTypes(
            anInt: Int,
            aLong: Long,
            aBoolean: Boolean,
            aFloat: Float,
            aDouble: Double,
            aString: String
        ) {
            // Does nothing.
        }
    }
    // [END android_backgroundwork_aidl_implement_stub]
}

// [START android_backgroundwork_aidl_remote_service]
class RemoteService : Service() {

    override fun onCreate() {
        super.onCreate()
    }

    override fun onBind(intent: Intent): IBinder {
        // Return the interface.
        return binder
    }

    private val binder = object : IRemoteService.Stub() {
        override fun getPid(): Int {
            return Process.myPid()
        }

        override fun basicTypes(
            anInt: Int,
            aLong: Long,
            aBoolean: Boolean,
            aFloat: Float,
            aDouble: Double,
            aString: String
        ) {
            // Does nothing.
        }
    }
}
// [END android_backgroundwork_aidl_remote_service]

private class ServiceConnectionSnippet {
    // [START android_backgroundwork_aidl_service_connection]
    var iRemoteService: IRemoteService? = null

    val mConnection = object : ServiceConnection {

        // Called when the connection with the service is established.
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            // Following the preceding example for an AIDL interface,
            // this gets an instance of the IRemoteInterface, which we can use to call on the service.
            iRemoteService = IRemoteService.Stub.asInterface(service)
        }

        // Called when the connection with the service disconnects unexpectedly.
        override fun onServiceDisconnected(className: ComponentName) {
            Log.e(TAG, "Service has unexpectedly disconnected")
            iRemoteService = null
        }
    }
    // [END android_backgroundwork_aidl_service_connection]

    private companion object {
        const val TAG = "RemoteServiceClient"
    }
}

// [START android_backgroundwork_aidl_parcelable_rect]
// import android.os.Parcel
// import android.os.Parcelable

class Rect() : Parcelable {
    var left: Int = 0
    var top: Int = 0
    var right: Int = 0
    var bottom: Int = 0

    companion object CREATOR : Parcelable.Creator<Rect> {
        override fun createFromParcel(parcel: Parcel): Rect {
            return Rect(parcel)
        }

        override fun newArray(size: Int): Array<Rect?> {
            return Array(size) { null }
        }
    }

    private constructor(inParcel: Parcel) : this() {
        readFromParcel(inParcel)
    }

    override fun writeToParcel(outParcel: Parcel, flags: Int) {
        outParcel.writeInt(left)
        outParcel.writeInt(top)
        outParcel.writeInt(right)
        outParcel.writeInt(bottom)
    }

    private fun readFromParcel(inParcel: Parcel) {
        left = inParcel.readInt()
        top = inParcel.readInt()
        right = inParcel.readInt()
        bottom = inParcel.readInt()
    }

    override fun describeContents(): Int {
        return 0
    }
}
// [END android_backgroundwork_aidl_parcelable_rect]

private class RectInsideBundleService : Service() {
    override fun onBind(intent: Intent): IBinder = binder

    // [START android_backgroundwork_aidl_bundle_classloader]
    private val binder = object : IRectInsideBundle.Stub() {
        override fun saveRect(bundle: Bundle) {
            bundle.classLoader = classLoader
            val rect = BundleCompat.getParcelable(bundle, "rect", Rect::class.java)
            process(rect) // Do more with the parcelable.
        }
    }
    // [END android_backgroundwork_aidl_bundle_classloader]

    private fun process(rect: Rect?) {}
}
