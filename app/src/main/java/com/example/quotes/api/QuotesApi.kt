package com.example.quotes.api

import com.example.quotes.Quotes
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface QuotesApi {

    @GET("quotes")
    suspend fun getQuotes(
        @Query("page")
        pageNumber: Int = 1
    ): Response<Quotes>
}