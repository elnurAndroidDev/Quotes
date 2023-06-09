package com.example.quotes.ui.activities.favorites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotes.models.QuoteUiModel
import com.example.quotes.repository.QuotesRepository
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: QuotesRepository) : ViewModel() {

    fun getSavedQuotes() = repository.getSavedQuotes()

    fun deleteQuote(quote: QuoteUiModel) = viewModelScope.launch {
        repository.deleteQuote(quote.content)
    }

}