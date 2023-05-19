package com.example.quotes.repository

import com.example.quotes.api.RetrofitInstance
import com.example.quotes.db.QuotesDatabase
import com.example.quotes.models.QuoteDBModel

class QuotesRepository(
    val db: QuotesDatabase
) {
    suspend fun getQuotesList() =
        RetrofitInstance.api.getQuotes()

    suspend fun insertQuote(quote: QuoteDBModel) = db.getQuoteDao().insert(quote)

    fun getSavedQuotes() = db.getQuoteDao().getAllQuotes()

    suspend fun deleteQuote(quoteContent: String) = db.getQuoteDao().delete(quoteContent)
}