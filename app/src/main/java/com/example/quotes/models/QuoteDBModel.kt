package com.example.quotes.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "quotes"
)
data class QuoteDBModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val author: String,
    val content: String
) {
    fun toUiModel() = QuoteUiModel(author, content, true)
}
