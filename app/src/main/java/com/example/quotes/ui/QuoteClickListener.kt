package com.example.quotes.ui

import com.example.quotes.models.QuoteUiModel

interface QuoteClickListener {
    fun likeOrUnLike(quote: QuoteUiModel)
    fun share(quote: QuoteUiModel)

    fun translate(quoteContent: String)
}