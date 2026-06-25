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

package com.example.snippets.room.index

import android.content.Context
import androidx.room3.ColumnInfo
import androidx.room3.Dao
import androidx.room3.Database
import androidx.room3.Delete
import androidx.room3.Entity
import androidx.room3.Insert
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.sqlite.driver.AndroidSQLiteDriver

// [START android_room3_index_entity]
@Entity
data class User(
    @PrimaryKey val uid: Int,
    @ColumnInfo(name = "first_name") val firstName: String,
    @ColumnInfo(name = "last_name") val lastName: String
)
// [END android_room3_index_entity]

// [START android_room3_index_dao]
@Dao
interface UserDao {
    @Query("SELECT * FROM user")
    suspend fun getAll(): List<User>

    @Query("SELECT * FROM user WHERE uid IN (:userIds)")
    suspend fun loadAllByIds(userIds: IntArray): List<User>

    @Query(
        """
        SELECT * FROM user
        WHERE first_name LIKE :first AND last_name LIKE :last LIMIT 1
        """
    )
    suspend fun findByName(first: String, last: String): User

    @Insert
    suspend fun insertAll(vararg users: User)

    @Delete
    suspend fun delete(user: User)
}
// [END android_room3_index_dao]

// [START android_room3_index_database]
@Database(entities = [User::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}
// [END android_room3_index_database]

class IndexSnippets {
    fun createDb(applicationContext: Context): AppDatabase {
        // [START android_room3_index_create]
        val db =
            Room.databaseBuilder<AppDatabase>(applicationContext, "database-name")
                .setDriver(AndroidSQLiteDriver())
                .build()
        // [END android_room3_index_create]
        return db
    }

    suspend fun useDb(db: AppDatabase) {
        // [START android_room3_index_usage]
        val userDao = db.userDao()
        val users: List<User> = userDao.getAll()
        // [END android_room3_index_usage]
    }
}
