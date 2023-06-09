package com.example.quotes.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "cache_quotes"
)
data class CachedQuoteDBModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val author: String,
    val content: String,
    val liked: Boolean
) {
    fun toUiModel() = QuoteUiModel(author, content, liked)
}
