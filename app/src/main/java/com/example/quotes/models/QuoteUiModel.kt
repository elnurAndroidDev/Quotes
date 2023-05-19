package com.example.quotes.models

data class QuoteUiModel(
    val author: String,
    val content: String,
    var liked: Boolean
)