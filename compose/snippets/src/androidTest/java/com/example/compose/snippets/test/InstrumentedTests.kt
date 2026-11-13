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

package com.example.compose.snippets.test

import android.os.Parcel
import android.os.Parcelable
import androidx.test.filters.SmallTest
import androidx.test.runner.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@Suppress("DEPRECATION")
private object LogHistorySnippet {
    // [START android_instrumented_tests_log_history]
    // import android.os.Parcel
    // import android.text.TextUtils.writeToParcel
    // import androidx.test.filters.SmallTest
    // import androidx.test.runner.AndroidJUnit4
    // import com.google.common.truth.Truth.assertThat
    // import org.junit.Before
    // import org.junit.Test
    // import org.junit.runner.RunWith

    const val TEST_STRING = "This is a string"
    const val TEST_LONG = 12345678L

    // @RunWith is required only if you use a mix of JUnit3 and JUnit4.
    @RunWith(AndroidJUnit4::class)
    @SmallTest
    class LogHistoryAndroidUnitTest {
        private lateinit var logHistory: LogHistory

        @Before
        fun createLogHistory() {
            logHistory = LogHistory()
        }

        @Test
        fun logHistory_ParcelableWriteRead() {
            val parcel = Parcel.obtain()
            logHistory.apply {
                // Set up the Parcelable object to send and receive.
                addEntry(TEST_STRING, TEST_LONG)

                // Write the data.
                writeToParcel(parcel, describeContents())
            }

            // After you're done with writing, you need to reset the parcel for reading.
            parcel.setDataPosition(0)

            // Read the data.
            val createdFromParcel: LogHistory = LogHistory.CREATOR.createFromParcel(parcel)
            createdFromParcel.getData().also { createdFromParcelData: List<Pair<String, Long>> ->

                // Verify that the received data is correct.
                assertThat(createdFromParcelData.size).isEqualTo(1)
                assertThat(createdFromParcelData[0].first).isEqualTo(TEST_STRING)
                assertThat(createdFromParcelData[0].second).isEqualTo(TEST_LONG)
            }
        }
    }
    // [END android_instrumented_tests_log_history]
}

private class LogHistory() : Parcelable {
    private val data = mutableListOf<Pair<String, Long>>()

    constructor(parcel: Parcel) : this() {
        val size = parcel.readInt()
        repeat(size) {
            val first = parcel.readString().orEmpty()
            val second = parcel.readLong()
            data.add(Pair(first, second))
        }
    }

    fun addEntry(text: String, timestamp: Long) {
        data.add(Pair(text, timestamp))
    }

    fun getData(): List<Pair<String, Long>> = data

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(data.size)
        for ((first, second) in data) {
            parcel.writeString(first)
            parcel.writeLong(second)
        }
    }

    override fun describeContents(): Int = 0

    companion object CREATOR : Parcelable.Creator<LogHistory> {
        override fun createFromParcel(parcel: Parcel): LogHistory = LogHistory(parcel)

        override fun newArray(size: Int): Array<LogHistory?> = arrayOfNulls(size)
    }
}
