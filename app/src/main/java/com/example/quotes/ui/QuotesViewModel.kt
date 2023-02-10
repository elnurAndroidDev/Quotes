package com.example.quotes.ui

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotes.Resource
import com.example.quotes.models.QuotesResponse
import com.example.quotes.repository.QuotesRepository
import kotlinx.coroutines.launch
import retrofit2.Response

class QuotesViewModel(
    private val repository: QuotesRepository
) : ViewModel() {

    val quotes: MutableLiveData<Resource<QuotesResponse>> = MutableLiveData()

    fun getQuotesList() = viewModelScope.launch {
        quotes.postValue(Resource.Loading())
        val response = repository.getQuotesList()
        quotes.postValue(handleQuotesResponse(response))
    }

    private fun handleQuotesResponse(response: Response<QuotesResponse>): Resource<QuotesResponse> {
        if (response.isSuccessful) {
            response.body()?.let {
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }
}