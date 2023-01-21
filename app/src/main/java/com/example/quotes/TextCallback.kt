package com.example.quotes

interface TextCallback {
    fun provideText(quote: String, author: String)
}