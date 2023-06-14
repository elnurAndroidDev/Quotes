package com.example.quotes.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.quotes.QuotesApplication
import com.example.quotes.Resource
import com.example.quotes.models.FavoriteQuoteDBModel
import com.example.quotes.models.QuoteUiModel
import com.example.quotes.models.QuotesResponse
import com.example.quotes.models.TranslationResponse
import com.example.quotes.repository.QuotesRepository
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.Response
import java.net.URL
import kotlin.concurrent.thread

class QuotesViewModel(
    app: Application,
    private val repository: QuotesRepository
) : AndroidViewModel(app) {

    val quotes: MutableLiveData<Resource<List<QuoteUiModel>>> = MutableLiveData()

    val translation: MutableLiveData<Resource<String>> = MutableLiveData()

    private val quotesList = ArrayList<QuoteUiModel>()

    init {
        getQuotesList()
    }

    fun getQuotesList() = viewModelScope.launch {
        try {
            if (hasInternet()) {

                if (quotesList.isEmpty())
                    quotes.postValue(Resource.Loading())
                val response = repository.getQuotesList()
                quotes.postValue(handleQuotesResponse(response))

            } else {
                addErrorItemToList("No Internet connection")
                quotes.postValue(Resource.Error(quotesList.toList()))
            }
        } catch (t: Throwable) {
            addErrorItemToList(t.message.toString())
            quotes.postValue(Resource.Error(quotesList.toList()))
        }
    }

    private fun addErrorItemToList(message: String) {
        if (quotesList.isNotEmpty() && quotesList.last().author == "App") {
            quotesList.remove(quotesList.last())
        }
        quotesList.add(
            QuoteUiModel(
                author = "App", content = message,
                liked = false
            )
        )
    }

    private fun handleQuotesResponse(response: Response<QuotesResponse>): Resource<List<QuoteUiModel>> {
        if (response.isSuccessful) {
            response.body()?.let {
                if (quotesList.isNotEmpty() && quotesList.last().author == "App")
                    quotesList.remove(quotesList.last())
                var count = 5
                for (quote in it) {
                    count--
                    quotesList.add(quote.toQuoteUiModel())
                    if (count == 0)
                        break
                }
                return Resource.Success(quotesList.toList())
            }
        }
        addErrorItemToList(response.message())
        return Resource.Error(quotesList.toList())
    }

    /*fun unlikeOnDelete(quoteContent: String) {
        if (quotesList.isNotEmpty()) {
            for (quote in quotesList) {
                if (quote.content == quoteContent && quote.liked)
                    quote.liked = false
            }
            quotes.postValue(Resource.Success(quotesList.toList()))
        }
    }

    fun cacheQuotes(quotes: List<QuoteServerModel>) = viewModelScope.launch {
        repository.cacheQuotes(quotes.map { it.toCachedQuoteModel() })
    }

    fun getQuotesFromCache() = viewModelScope.launch {
        repository.getCachedQuotes()
    }*/

    fun saveQuote(quote: QuoteUiModel) = viewModelScope.launch {
        repository.insertQuote(FavoriteQuoteDBModel(author = quote.author, content = quote.content))
    }

    fun deleteQuote(quote: QuoteUiModel) = viewModelScope.launch {
        repository.deleteQuote(quote.content)
    }

    fun translate(quote: String) {
        thread {
            translation.postValue(Resource.Loading())
            try {
                val baseUrl =
                    "https://script.google.com/macros/s/AKfycbzpxbLLAL9KmKMUiPD6-xiN0Mhbh-XnEUJql953L98vH8e-1TxT6QGgaJ6Kc4F5wJNzbg/exec"
                val params = "?text=$quote"
                val url = "$baseUrl$params"
                val responseString = URL(url).readText()
                val translationResponse =
                    Gson().fromJson(responseString, TranslationResponse::class.java)
                translation.postValue(Resource.Success(translationResponse.message))
            } catch (e: Exception) {
                translation.postValue(Resource.Error(e.message.toString()))
            }
        }
    }

    private fun hasInternet(): Boolean {
        val connectivityManager =
            getApplication<QuotesApplication>().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            networkCapabilities.hasTransport(TRANSPORT_WIFI) -> true
            networkCapabilities.hasTransport(TRANSPORT_CELLULAR) -> true
            networkCapabilities.hasTransport(TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }
}