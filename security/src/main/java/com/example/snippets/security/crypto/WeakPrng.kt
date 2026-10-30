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

import java.security.SecureRandom
import java.util.Random

private fun strongSecureRandom() {
    // [START android_security_weak_prng_strong]
    val rand = SecureRandom.getInstanceStrong()
    // [END android_security_weak_prng_strong]
}

private object SecureRandomExample {
    // [START android_security_weak_prng_securerandom]
    // import java.security.SecureRandom

    object generateRandom {
        @JvmStatic
        fun main(args: Array<String>) {
            // Create instance of SecureRandom class.
            val rand = SecureRandom()

            // Generate random integers in range 0 to 999.
            val rand_int = rand.nextInt(1000)

            // Use rand_int for security & authentication.
        }
    }
    // [END android_security_weak_prng_securerandom]
}

private object UtilRandomExample {
    // [START android_security_weak_prng_util_random]
    // import java.util.Random

    object generateRandom {
        @JvmStatic
        fun main(args: Array<String>) {
            // Create instance of SecureRandom class.
            val rand = Random()

            // Generate random integers in range 0 to 999.
            val rand_int = rand.nextInt(1000)
        }
    }
    // [END android_security_weak_prng_util_random]
}
