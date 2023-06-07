package com.example.quotes.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "cache_quotes"
)
data class QuoteDBModel2(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val author: String,
    val content: String
) {
    fun toUiModel() = QuoteUiModel(author, content, false)
}
