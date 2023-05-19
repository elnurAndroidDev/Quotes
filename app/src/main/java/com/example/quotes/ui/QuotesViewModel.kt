package com.example.quotes.ui

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotes.Resource
import com.example.quotes.models.QuoteDBModel
import com.example.quotes.models.QuoteUiModel
import com.example.quotes.models.QuotesResponse
import com.example.quotes.repository.QuotesRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class QuotesViewModel(
    private val repository: QuotesRepository
) : ViewModel() {

    val quotes: MutableLiveData<Resource<List<QuoteUiModel>>> = MutableLiveData()

    init {
        getQuotesList()
    }

    private fun getQuotesList() = viewModelScope.launch {
        quotes.postValue(Resource.Loading())
        val response = repository.getQuotesList()
        quotes.postValue(handleQuotesResponse(response))
    }

    private fun handleQuotesResponse(response: Response<QuotesResponse>): Resource<List<QuoteUiModel>> {
        if (response.isSuccessful) {
            response.body()?.let {
                val list = ArrayList<QuoteUiModel>()
                for (quote in it) {
                    list.add(quote.toQuoteUiModel())
                }
                return Resource.Success(list)
            }
        }
        return Resource.Error(response.message())
    }

    fun saveQuote(quote: QuoteUiModel) = viewModelScope.launch {
        repository.insertQuote(QuoteDBModel(author = quote.author, content = quote.content))
    }

    fun getSavedQuotes() = repository.getSavedQuotes()

    fun deleteQuote(quote: QuoteUiModel) = viewModelScope.launch {
        repository.deleteQuote(quote.content)
    }
}