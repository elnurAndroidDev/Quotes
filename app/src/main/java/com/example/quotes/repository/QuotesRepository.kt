package com.example.quotes.repository

import com.example.quotes.api.RetrofitInstance
import com.example.quotes.db.QuotesDatabase
import com.example.quotes.models.QuoteDBModel
import com.example.quotes.models.QuoteDBModel2

class QuotesRepository(
    val db: QuotesDatabase
) {
    suspend fun getQuotesList() =
        RetrofitInstance.api.getQuotes()

    suspend fun insertQuote(quote: QuoteDBModel) = db.getQuoteDao().insert(quote)

    fun getSavedQuotes() = db.getQuoteDao().getAllQuotes()

    suspend fun deleteQuote(quoteContent: String) = db.getQuoteDao().delete(quoteContent)


    suspend fun cacheQuotes(quotes: List<QuoteDBModel2>) =
        db.getCachedQuoteDao().insertQuotes(quotes)

    suspend fun getCachedQuotes() = db.getCachedQuoteDao().getFromCache()

    suspend fun getCachedQuotesNumber() = db.getCachedQuoteDao().getCachedQuotesNumber()
}