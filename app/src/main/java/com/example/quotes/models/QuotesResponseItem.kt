package com.example.quotes.models

data class QuotesResponseItem(
    val a: String,
    val c: String,
    val h: String,
    val q: String
) {
    fun toQuote() = Quote(author = a, content = q)
}