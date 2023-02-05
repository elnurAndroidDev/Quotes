package com.example.quotes.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "quotes"
)
data class Quote(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val author: String,
    val content: String
)
