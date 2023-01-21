package com.example.quotes

interface ResultCallBack {
    fun provideSuccess(quote: Quote)
    fun provideError(error: Failure)
}