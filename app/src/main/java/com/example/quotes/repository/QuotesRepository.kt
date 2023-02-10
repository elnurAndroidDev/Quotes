package com.example.quotes.repository

import com.example.quotes.api.RetrofitInstance
import com.example.quotes.db.QuotesDatabase

class QuotesRepository(
    val db: QuotesDatabase
) {
    suspend fun getQuotesList() =
        RetrofitInstance.api.getQuotes()
}