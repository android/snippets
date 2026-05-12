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
