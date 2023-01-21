package com.example.quotes

class AppViewModel(private val model: Model) {
    private var callback: TextCallback? = null

    fun init(callback: TextCallback) {
        this.callback = callback
        model.init(object : ResultCallBack {
            override fun provideSuccess(quote: Quote) {
                callback.provideText(quote.getQuote(), quote.getAuthor())
            }

            override fun provideError(error: Failure) {
                callback.provideText(error.getMessage(), error.getHead())
            }
        })
    }

    fun getQuote() {
        model.getQuote()
    }

    fun clear() {
        callback = null
        model.clear()
    }
}