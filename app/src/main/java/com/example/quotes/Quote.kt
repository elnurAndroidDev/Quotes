package com.example.quotes

import com.google.gson.annotations.SerializedName

data class Quote(
    @SerializedName("q")
    private val quote: String,
    @SerializedName("a")
    private val author: String,
    @SerializedName("h")
    private val html: String
) {
    fun getQuote() = quote
    fun getAuthor() = author
}
