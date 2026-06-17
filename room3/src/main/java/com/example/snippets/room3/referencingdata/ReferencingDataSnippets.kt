// Copyright 2026 The Android Open Source Project
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.example.snippets.room3.referencingdata

import android.content.Context
import androidx.room3.*
import java.util.Date

// [START room_3_referencing_data_converter]
object Converters {
  @ColumnTypeConverter
  fun fromTimestamp(value: Long?): Date? {
    return value?.let { Date(it) }
  }

  @ColumnTypeConverter
  fun dateToTimestamp(date: Date?): Long? {
    return date?.time
  }
}
// [END room_3_referencing_data_converter]

// [START room_3_referencing_data_db]
@Database(entities = [User::class], version = 1)
@ColumnTypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
  abstract fun userDao(): UserDao
}
// [END room_3_referencing_data_db]

// [START room_3_referencing_data_usage]
@Entity
data class User(
  @PrimaryKey val id: Long,
  val name: String,
  val birthday: Date?
)

@Dao
interface UserDao {
  @Query("SELECT * FROM user WHERE birthday = :targetDate")
  suspend fun findUsersBornOnDate(targetDate: Date): List<User>
}
// [END room_3_referencing_data_usage]

// Provided converter example
class ExampleType

// [START room_3_referencing_data_provided]
@ProvidedColumnTypeConverter
class ExampleConverter {
  @ColumnTypeConverter
  fun StringToExample(string: String?): ExampleType? {
    return string?.let { ExampleType() }
  }

  @ColumnTypeConverter
  fun ExampleToString(example: ExampleType?): String? {
    return example?.toString()
  }
}
// [END room_3_referencing_data_provided]

@Entity
data class DummyEntity(@PrimaryKey val id: Int)

@Database(entities = [DummyEntity::class], version = 1)
@ColumnTypeConverters(ExampleConverter::class)
abstract class MyDatabase : RoomDatabase()

val exampleConverterInstance = ExampleConverter()

fun createProvidedDb(applicationContext: Context) {
    // [START room_3_referencing_data_register_provided]
    val db = Room.databaseBuilder<MyDatabase>(applicationContext, "database-name")
      .addColumnTypeConverter(exampleConverterInstance)
      .build()
    // [END room_3_referencing_data_register_provided]
}
