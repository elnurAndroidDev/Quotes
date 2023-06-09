package com.example.quotes.models

data class QuoteServerModel(
    val a: String,
    val c: String,
    val h: String,
    val q: String
) {
    fun toQuoteUiModel(liked: Boolean = false) = QuoteUiModel(
        author = a,
        content = q,
        liked = liked
    )

    fun toCachedQuoteModel(liked: Boolean = false) = CachedQuoteDBModel(
        author = a,
        content = q,
        liked = liked
    )
}