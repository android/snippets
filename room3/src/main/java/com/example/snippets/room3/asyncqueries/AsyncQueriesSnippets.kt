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

package com.example.snippets.room3.asyncqueries

import androidx.room3.Dao
import androidx.room3.DaoReturnTypeConverter
import androidx.room3.DaoReturnTypeConverters
import androidx.room3.Database
import androidx.room3.Entity
import androidx.room3.OperationType
import androidx.room3.PrimaryKey
import androidx.room3.ProvidedDaoReturnTypeConverter
import androidx.room3.Query
import androidx.room3.Room
import androidx.room3.RoomDatabase
import androidx.room3.RoomRawQuery
import androidx.tracing.trace
import java.util.Date
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

// Song entity defined at file level to be used by both examples
@Entity
data class Song(@PrimaryKey val id: Int)

private object OneShotObservableExamples {
    @Entity
    data class User(
        @PrimaryKey val id: Int,
        val region: String
    )

    // [START android_room3_async_queries_oneshot]
    @Dao
    interface UserDao {
        @Query("SELECT * FROM user WHERE id = :id")
        suspend fun loadUserById(id: Int): User

        @Query("SELECT * from user WHERE region IN (:regions)")
        suspend fun loadUsersByRegion(regions: List<String>): List<User>
    }
    // [END android_room3_async_queries_oneshot]

    // [START android_room3_async_queries_observable]
    @Dao
    interface ObservableUserDao {
        @Query("SELECT * FROM user WHERE id = :id")
        fun loadUserById(id: Int): Flow<User>

        @Query("SELECT * from user WHERE region IN (:regions)")
        fun loadUsersByRegion(regions: List<String>): Flow<List<User>>
    }
    // [END android_room3_async_queries_observable]
}

private object InvalidationTrackerExample {
    data class Artist(val id: Int, val name: String)
    data class TourState(val status: String)

    interface ArtistsDao {
        suspend fun getAllArtists(): List<Artist>
    }

    interface TourService {
        suspend fun fetchStates(artistIds: List<Int>): List<TourState>
    }

    lateinit var artistsDao: ArtistsDao
    lateinit var tourService: TourService

    fun associateTours(artists: List<Artist>, tours: List<TourState>, from: Date, to: Date): Map<Artist, TourState> {
        return artists.zip(tours).toMap()
    }

    // [START android_room3_async_queries_invalidation]
    fun getArtistTours(db: RoomDatabase, from: Date, to: Date): Flow<Map<Artist, TourState>> {
        return db.invalidationTracker.createFlow("Artist").map { _ ->
            val artists = artistsDao.getAllArtists()
            val tours = tourService.fetchStates(artists.map { it.id })
            associateTours(artists, tours, from, to)
        }
    }
    // [END android_room3_async_queries_invalidation]
}

// Custom converters examples
// [START android_room3_async_queries_custom_converter]
class TracedQuery<T>(val result: T)

object TracingDaoReturnTypeConverter {
    @DaoReturnTypeConverter([OperationType.READ])
    suspend fun <T> convert(
        rawQuery: RoomRawQuery,
        executeAndConvert: suspend () -> T
    ): TracedQuery<T> {
        val result = trace("TracedQuery: ${rawQuery.sql}") {
            executeAndConvert()
        }
        return TracedQuery(result)
    }
}
// [END android_room3_async_queries_custom_converter]

private object UseCustomConverterExample {
    // [START android_room3_async_queries_use_custom_converter]
    @Dao
    @DaoReturnTypeConverters(TracingDaoReturnTypeConverter::class)
    interface MusicDao {
        @Query("SELECT * FROM Song")
        suspend fun getAllSongs(): TracedQuery<List<Song>>
    }
    // [END android_room3_async_queries_use_custom_converter]
}

// Database class at file level to avoid KSP nesting issues
@Database(entities = [Song::class], version = 1)
abstract class MyDatabase : RoomDatabase()

private object ProvidedConverterExample {
    class Tracer {
        inline fun <T> trace(sectionName: String, block: () -> T): T {
            return block()
        }
    }
    val myLoggerInstance = Tracer()

    // [START android_room3_async_queries_provided_converter]
    @ProvidedDaoReturnTypeConverter
    class TracingDaoReturnTypeConverter(val tracer: Tracer) {
        @DaoReturnTypeConverter([OperationType.READ])
        suspend fun <T> convert(
            rawQuery: RoomRawQuery,
            executeAndConvert: suspend () -> T
        ): TracedQuery<T> {
            val result = tracer.trace("TracedQuery: ${rawQuery.sql}") {
                executeAndConvert()
            }
            return TracedQuery(result)
        }
    }
    // [END android_room3_async_queries_provided_converter]

    fun createDb(applicationContext: android.content.Context) {
        // [START android_room3_async_queries_register_provided]
        val db = Room.databaseBuilder<MyDatabase>(applicationContext, "database-name")
            .addDaoReturnTypeConverter(TracingDaoReturnTypeConverter(myLoggerInstance))
            .build()
        // [END android_room3_async_queries_register_provided]
    }
}
