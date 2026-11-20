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

package com.example.snippets.room.definingdata

import android.graphics.Bitmap
import androidx.room3.ColumnInfo
import androidx.room3.Entity
import androidx.room3.Fts4
import androidx.room3.FtsOptions
import androidx.room3.Ignore
import androidx.room3.Index
import androidx.room3.PrimaryKey

private object SimpleEntityExample {
    // [START android_room3_defining_data_simple_entity]
    @Entity
    data class User(
        @PrimaryKey val id: Int,
        val firstName: String,
        val lastName: String
    )
    // [END android_room3_defining_data_simple_entity]
}

private object CustomEntityExample {
    // [START android_room3_defining_data_custom_entity]
    @Entity(tableName = "users")
    data class User(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "first_name") val firstName: String,
        @ColumnInfo(name = "last_name") val lastName: String
    )
    // [END android_room3_defining_data_custom_entity]
}

private object PrimaryKeyExample {
    class User(
        // [START android_room3_defining_data_pk]
        @PrimaryKey val id: Int
        // [END android_room3_defining_data_pk]
    )
}

private object CompositePrimaryKeyExample {
    // [START android_room3_defining_data_composite_pk]
    @Entity(primaryKeys = ["firstName", "lastName"])
    data class User(
        val firstName: String,
        val lastName: String
    )
    // [END android_room3_defining_data_composite_pk]
}

private object IgnoreExample {
    // [START android_room3_defining_data_ignore]
    @Entity
    data class User(
        @PrimaryKey val id: Int,
        val firstName: String,
        val lastName: String,
        @Ignore val picture: Bitmap? = null
    )
    // [END android_room3_defining_data_ignore]
}

private object InheritedIgnoreExample {
    // [START android_room3_defining_data_inherited_ignore]
    open class User {
        var picture: Bitmap? = null
    }

    @Entity(ignoredColumns = ["picture"])
    data class RemoteUser(
        @PrimaryKey val id: Int,
        val hasVpn: Boolean
    ) : User()
    // [END android_room3_defining_data_inherited_ignore]
}

private object Fts4Example {
    // [START android_room3_defining_data_fts4]
    // Use `@Fts3` only if your app has strict disk space requirements.
    @Fts4
    @Entity(tableName = "users")
    data class User(
        // Specifying a primary key for an FTS-table-backed entity is optional,
        // but if you include one, it must an INTEGER type and column name "rowid".
        @PrimaryKey @ColumnInfo(name = "rowid") val id: Long,
        @ColumnInfo(name = "first_name") val firstName: String
    )
    // [END android_room3_defining_data_fts4]
}

private object Fts4TokenizerExample {
    // [START android_room3_defining_data_fts4_tokenizer]
    @Fts4(tokenizer = FtsOptions.TOKENIZER_UNICODE61)
    @Entity(tableName = "users")
    data class User(
        @PrimaryKey @ColumnInfo(name = "rowid") val id: Long,
        @ColumnInfo(name = "first_name") val firstName: String
    )
    // [END android_room3_defining_data_fts4_tokenizer]
}

private object IndexExample {
    // [START android_room3_defining_data_index]
    @Entity(indices = [Index(value = ["last_name", "address"])])
    data class User(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "first_name") val firstName: String,
        @ColumnInfo(name = "last_name") val lastName: String,
        val address: String?,
    )
    // [END android_room3_defining_data_index]
}

private object UniqueIndexExample {
    // [START android_room3_defining_data_unique_index]
    @Entity(indices = [Index(value = ["first_name", "last_name"], unique = true)])
    data class User(
        @PrimaryKey val id: Int,
        @ColumnInfo(name = "first_name") val firstName: String,
        @ColumnInfo(name = "last_name") val lastName: String,
    )
    // [END android_room3_defining_data_unique_index]
}
