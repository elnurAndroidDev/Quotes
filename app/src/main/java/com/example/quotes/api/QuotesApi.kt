package com.example.quotes.api

import com.example.quotes.models.QuotesResponse
import retrofit2.Response
import retrofit2.http.GET

interface QuotesApi {

    @GET("quotes")
    suspend fun getQuotes(): Response<QuotesResponse>
}