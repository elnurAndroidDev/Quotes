package com.example.quotes.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.View
import androidx.lifecycle.ViewModelProvider
import com.example.quotes.R
import com.example.quotes.Resource
import com.example.quotes.adapters.QuotesAdapter
import com.example.quotes.db.QuotesDatabase
import com.example.quotes.repository.QuotesRepository
import com.example.quotes.ui.QuotesViewModel
import com.example.quotes.ui.QuotesViewModelProviderFactory
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: QuotesViewModel
    private lateinit var quotesAdapter: QuotesAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val newsRepository = QuotesRepository(QuotesDatabase(requireContext()))
        val viewModelProviderFactory = QuotesViewModelProviderFactory(newsRepository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[QuotesViewModel::class.java]

        setupViewPager()

        viewModel.quotes.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    it.data?.let { response ->
                        quotesAdapter.differ.submitList(response.map { qri ->
                            qri.toQuote()
                        })
                    }
                }
                is Resource.Error -> {
                    it.message?.let { message ->
                        Log.d("MyTag", message)
                    }
                }
                is Resource.Loading -> {}
            }
        }
    }

    private fun setupViewPager() {
        quotesAdapter = QuotesAdapter()
        viewPager.adapter = quotesAdapter
    }
}