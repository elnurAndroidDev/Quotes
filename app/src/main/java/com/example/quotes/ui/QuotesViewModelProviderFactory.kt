package com.example.quotes.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.quotes.repository.QuotesRepository

class QuotesViewModelProviderFactory(
    private val app: Application,
    private val repository: QuotesRepository
) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return QuotesViewModel(app, repository) as T
    }
}