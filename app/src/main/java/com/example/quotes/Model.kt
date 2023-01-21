package com.example.quotes

import com.google.gson.Gson
import java.net.URL
import java.net.UnknownHostException
import kotlin.concurrent.thread

class Model(private val resManager: ResourceManager) {
    private var callback: ResultCallBack? = null
    private val noConnection by lazy { NoConnection(resManager) }
    private val serviceUnavailable by lazy { ServiceUnavailable(resManager) }

    fun init(callback: ResultCallBack) {
        this.callback = callback
    }

    fun getQuote() {
        thread {
            try {
                val response = URL(URL).readText()
                val quotes = Gson().fromJson(response, Array<Quote>::class.java)
                callback?.provideSuccess(quotes[0])
            } catch (e: Exception) {
                if (e is UnknownHostException) {
                    callback?.provideError(noConnection)
                } else {
                    callback?.provideError(serviceUnavailable)
                }
            }

        }
    }

    fun clear() {
        callback = null
    }


    private companion object {
        const val URL = "https://zenquotes.io/api/random"
    }
}