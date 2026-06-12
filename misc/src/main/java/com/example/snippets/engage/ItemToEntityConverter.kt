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

package com.example.snippets.engage

import com.google.android.engage.books.datamodel.EbookEntity

// [START android_engage_item_to_entity_converter_implementation]
object ItemToEntityConverter {
    // Converts app's local models to appropriate engage entity models.
    // Use `{VERTICAL}.md` in the `references/schemas/` directory to identify the correct Engage entities.
    // This is an example of using EbookEntity model.
    fun convert(item: AppData): EbookEntity {
        return EbookEntity.Builder()
            // Implement required data mapping logic here.
            .setName(item.title)
            .addAuthor(item.author)
            .build()
    }
}
// [END android_engage_item_to_entity_converter_implementation]

// [START android_engage_dual_content_rating_example]
fun convertMovie(movie: MovieData): MovieEntity {
    val ratingSystem = RatingSystem.Builder()
        .setAgencyName("MPAA")
        .setRating("PG-13")
        .build()
    return MovieEntity.Builder()
        .setEntityId(movie.id)
        .setName(movie.title)
        // ... other fields
        .addContentRating(ratingSystem) // Recommended API
        .addContentRatingsLegacy(listOf("MPAA:PG-13")) // Legacy API for backward compatibility
        .build()
}
// [END android_engage_dual_content_rating_example]
