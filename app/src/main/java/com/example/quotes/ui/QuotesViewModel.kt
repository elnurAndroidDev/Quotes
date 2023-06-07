package com.example.quotes.ui

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_ETHERNET
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.quotes.QuotesApplication
import com.example.quotes.Resource
import com.example.quotes.models.QuoteDBModel
import com.example.quotes.models.QuoteServerModel
import com.example.quotes.models.QuoteUiModel
import com.example.quotes.models.QuotesResponse
import com.example.quotes.repository.QuotesRepository
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class QuotesViewModel(
    app: Application,
    private val repository: QuotesRepository
) : AndroidViewModel(app) {

    val quotes: MutableLiveData<Resource<List<QuoteUiModel>>> = MutableLiveData()

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
            when (t) {
                is IOException -> addErrorItemToList("Network failure")
                else -> addErrorItemToList("Conversion error")
            }
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

    fun cacheQuotes(quotes: List<QuoteServerModel>) = viewModelScope.launch {
        repository.cacheQuotes(quotes.map { it.toCachedQuoteModel() })
    }

    fun getQuotesFromCache() = viewModelScope.launch {
        repository.getCachedQuotes()
    }

    fun saveQuote(quote: QuoteUiModel) = viewModelScope.launch {
        repository.insertQuote(QuoteDBModel(author = quote.author, content = quote.content))
    }

    fun getSavedQuotes() = repository.getSavedQuotes()

    fun deleteQuote(quote: QuoteUiModel) = viewModelScope.launch {
        repository.deleteQuote(quote.content)
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