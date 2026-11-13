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

package com.example.snippets.connectivity

import android.nfc.cardemulation.HostApduService
import android.os.Bundle

// A top-level class, not a private wrapper, because the framework instantiates an HCE service.
// The page's manifest and apduservice.xml blocks show how an app registers it.
// [START android_connectivity_nfc_hce_service]
class MyHostApduService : HostApduService() {

    override fun processCommandApdu(commandApdu: ByteArray, extras: Bundle?): ByteArray {
        // [START_EXCLUDE]
        // processCommandApdu() must return a response APDU. 6F00 is the ISO/IEC 7816-4 status
        // word for "no precise diagnosis".
        return byteArrayOf(0x6F.toByte(), 0x00)
        // [END_EXCLUDE]
    }

    override fun onDeactivated(reason: Int) {
        // ...
    }
}
// [END android_connectivity_nfc_hce_service]
