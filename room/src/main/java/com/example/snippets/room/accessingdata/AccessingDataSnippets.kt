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

package com.example.snippets.room.accessingdata

import androidx.paging.PagingSource
import androidx.room3.ColumnInfo
import androidx.room3.Dao
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Delete
import androidx.room3.Entity
import androidx.room3.Insert
import androidx.room3.MapColumn
import androidx.room3.OnConflictStrategy
import androidx.room3.PrimaryKey
import androidx.room3.Query
import androidx.room3.RoomDatabase
import androidx.room3.Update
import androidx.room3.Upsert
import androidx.room3.immediateTransaction
import androidx.room3.paging.PagingSourceDaoReturnTypeConverter
import androidx.room3.useReaderConnection
import androidx.room3.useWriterConnection
import androidx.room3.withReadTransaction
import androidx.room3.withWriteTransaction
import kotlinx.coroutines.flow.Flow

private object SimpleDaoExample {
    @Entity
    data class User(
        @PrimaryKey val uid: Int,
        @ColumnInfo(name = "first_name") val firstName: String,
        @ColumnInfo(name = "last_name") val lastName: String
    )

    // [START android_room3_accessing_data_simple_dao]
    @Dao
    interface UserDao {
        @Insert
        suspend fun insertAll(vararg users: User)

        @Delete
        suspend fun delete(user: User)

        @Query("SELECT * FROM user")
        suspend fun getAll(): List<User>
    }
    // [END android_room3_accessing_data_simple_dao]
}

private object InsertConvenienceExample {
    @Entity
    data class User(
        @PrimaryKey val uid: Int,
        @ColumnInfo(name = "first_name") val firstName: String,
        @ColumnInfo(name = "last_name") val lastName: String
    )

    // [START android_room3_accessing_data_insert_convenience]
    @Dao
    interface UserDao {
        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertUsers(vararg users: User)

        @Insert
        suspend fun insertBothUsers(user1: User, user2: User)

        @Insert
        suspend fun insertUsersAndFriends(user: User, friends: List<User>)
    }
    // [END android_room3_accessing_data_insert_convenience]
}

private object UpdateConvenienceExample {
    @Entity
    data class User(
        @PrimaryKey val uid: Int,
        @ColumnInfo(name = "first_name") val firstName: String,
        @ColumnInfo(name = "last_name") val lastName: String
    )

    // [START android_room3_accessing_data_update_convenience]
    @Dao
    interface UserDao {
        @Update
        suspend fun updateUsers(vararg users: User)
    }
    // [END android_room3_accessing_data_update_convenience]
}

private object DeleteConvenienceExample {
    @Entity
    data class User(
        @PrimaryKey val uid: Int,
        @ColumnInfo(name = "first_name") val firstName: String,
        @ColumnInfo(name = "last_name") val lastName: String
    )

    // [START android_room3_accessing_data_delete_convenience]
    @Dao
    interface UserDao {
        @Delete
        suspend fun deleteUsers(vararg users: User)
    }
    // [END android_room3_accessing_data_delete_convenience]
}

private object UpsertConvenienceExample {
    @Entity
    data class User(
        @PrimaryKey val uid: Int,
        @ColumnInfo(name = "first_name") val firstName: String,
        @ColumnInfo(name = "last_name") val lastName: String
    )

    // [START android_room3_accessing_data_upsert_convenience]
    @Dao
    interface UserDao {
        @Upsert
        suspend fun upsertUsers(vararg users: User)
    }
    // [END android_room3_accessing_data_upsert_convenience]
}

private object QueryExamples {
    @Entity
    data class User(
        @PrimaryKey val id: Int,
        val name: String,
        val age: Int,
        val region: String,
        @ColumnInfo(name = "first_name") val firstName: String,
        @ColumnInfo(name = "last_name") val lastName: String
    )

    @Entity
    data class Book(
        @PrimaryKey val id: Int,
        val name: String,
        val user_id: Int
    )

    @Entity
    data class Loan(
        @PrimaryKey val loanId: Int,
        val book_id: Int,
        val user_id: Int
    )

    @Dao
    interface UserDao {
        // [START android_room3_accessing_data_simple_query]
        @Query("SELECT * FROM user")
        suspend fun loadAllUsers(): List<User>
        // [END android_room3_accessing_data_simple_query]

        // [START android_room3_accessing_data_query_param]
        @Query("SELECT * FROM user WHERE age > :minAge")
        suspend fun loadAllUsersOlderThan(minAge: Int): Array<User>
        // [END android_room3_accessing_data_query_param]

        // [START android_room3_accessing_data_query_params]
        @Query("SELECT * FROM user WHERE age BETWEEN :minAge AND :maxAge")
        suspend fun loadAllUsersBetweenAges(minAge: Int, maxAge: Int): Array<User>

        @Query(
            """
            SELECT * FROM user
            WHERE first_name LIKE :search OR last_name LIKE :search
            """
        )
        suspend fun findUserWithName(search: String): List<User>
        // [END android_room3_accessing_data_query_params]

        // [START android_room3_accessing_data_query_collection]
        @Query("SELECT * FROM user WHERE region IN (:regions)")
        suspend fun loadUsersFromRegions(regions: List<String>): List<User>
        // [END android_room3_accessing_data_query_collection]

        // [START android_room3_accessing_data_query_multitable]
        @Query(
            """
            SELECT * FROM book
            INNER JOIN loan ON loan.book_id = book.id
            INNER JOIN user ON user.id = loan.user_id
            WHERE user.name LIKE :userName
            """
        )
        suspend fun findBooksBorrowedByName(userName: String): List<Book>
        // [END android_room3_accessing_data_query_multitable]

        // [START android_room3_accessing_data_multimap]
        @Query(
            """
            SELECT * FROM user
            JOIN book ON user.id = book.user_id
            """
        )
        suspend fun loadUserAndBookNames(): Map<User, List<Book>>
        // [END android_room3_accessing_data_multimap]

        // [START android_room3_accessing_data_multimap_groupby]
        @Query(
            """
            SELECT * FROM user
            JOIN book ON user.id = book.user_id
            GROUP BY user.name HAVING COUNT(book.id) >= 3
            """
        )
        suspend fun loadUserAndBookNamesGrouped(): Map<User, List<Book>>
        // [END android_room3_accessing_data_multimap_groupby]

        // [START android_room3_accessing_data_multimap_mapcolumn]
        @Query(
            """
            SELECT user.name AS username, book.name AS bookname FROM user
            JOIN book ON user.id = book.user_id
            """
        )
        suspend fun loadUserAndBookNamesColumns(): Map<
            @MapColumn(columnName = "username") String,
            List<@MapColumn(columnName = "bookname") String>
            >
        // [END android_room3_accessing_data_multimap_mapcolumn]
    }

    // [START android_room3_accessing_data_subset_query_tuple]
    data class NameTuple(
        @ColumnInfo(name = "first_name") val firstName: String,
        @ColumnInfo(name = "last_name") val lastName: String
    )
    // [END android_room3_accessing_data_subset_query_tuple]

    @Dao
    interface SubsetDao {
        // [START android_room3_accessing_data_subset_query_method]
        @Query("SELECT first_name, last_name FROM user")
        suspend fun loadFullName(): List<NameTuple>
        // [END android_room3_accessing_data_subset_query_method]
    }

    // [START android_room3_accessing_data_query_flow]
    interface UserBookDao {
        @Query(
            """
            SELECT user.name AS userName, book.name AS bookName
            FROM user, book
            WHERE user.id = book.user_id
            """
        )
        fun loadUserAndBookNames(): Flow<List<UserBook>>
    }

    data class UserBook(val userName: String, val bookName: String)
    // [END android_room3_accessing_data_query_flow]
}

private object PagingExample {
    @Entity(tableName = "users")
    data class User(
        @PrimaryKey val uid: Int,
        val label: String
    )

    // [START android_room3_accessing_data_paging]
    @Dao
    @DaoReturnTypeConverters(PagingSourceDaoReturnTypeConverter::class)
    interface UserDao {
        @Query("SELECT * FROM users WHERE label LIKE :query")
        fun pagingSource(query: String): PagingSource<Int, User>
    }
    // [END android_room3_accessing_data_paging]
}

private class ConnectionExamples(val roomDatabase: RoomDatabase) {
    suspend fun runConnectionReader(minAge: Int) {
        // [START android_room3_accessing_data_connection_reader]
        val result: List<Pair<Long, String>> =
            roomDatabase.useReaderConnection { connection ->
                connection.usePrepared(
                    "SELECT * FROM user WHERE age > :minAge LIMIT 5"
                ) { stmt ->
                    // Bind arguments if needed
                    stmt.bindLong(1, minAge.toLong())
                    buildList {
                        // Step through the results
                        while (stmt.step()) {
                            add(stmt.getLong(0) to stmt.getText(1))
                        }
                    }
                }
            }
        // [END android_room3_accessing_data_connection_reader]
    }

    suspend fun runConnectionWriter() {
        // [START android_room3_accessing_data_connection_transaction]
        roomDatabase.useWriterConnection { transactor ->
            transactor.immediateTransaction {
                // Perform transactional database operations using transactor
            }
        }
        // [END android_room3_accessing_data_connection_transaction]
    }
}

private object TransactionExample {
    @Entity
    data class User(@PrimaryKey val id: Int)

    @Dao
    interface UserDao {
        @Query("SELECT COUNT(*) FROM user")
        suspend fun countUsers(): Int

        @Insert
        suspend fun insert(user: User)

        @Update
        suspend fun update(user: User)
    }

    suspend fun runTransactionExamples(
        roomDatabase: RoomDatabase,
        userDao: UserDao,
        newUser: User,
        existingUser: User
    ) {
        // [START android_room3_accessing_data_highlevel_transaction]
        // Perform transactional read operations (DEFERRED transaction)
        val userCount = roomDatabase.withReadTransaction {
            userDao.countUsers()
        }

        // Perform transactional write operations (IMMEDIATE transaction)
        roomDatabase.withWriteTransaction {
            userDao.insert(newUser)
            userDao.update(existingUser)
        }
        // [END android_room3_accessing_data_highlevel_transaction]
    }
}
