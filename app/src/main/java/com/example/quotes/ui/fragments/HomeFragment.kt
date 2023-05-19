package com.example.quotes.ui.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.quotes.R
import com.example.quotes.Resource
import com.example.quotes.adapters.QuotesAdapter
import com.example.quotes.databinding.FragmentHomeBinding
import com.example.quotes.models.QuoteUiModel
import com.example.quotes.ui.QuoteClickListener
import com.example.quotes.ui.QuotesViewModel
import com.example.quotes.ui.activities.MainActivity

class HomeFragment : Fragment(R.layout.fragment_home) {
    private lateinit var quotesAdapter: QuotesAdapter
    private lateinit var viewModel: QuotesViewModel

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel

        setupViewPager()

        viewModel.quotes.observe(viewLifecycleOwner) {
            when (it) {
                is Resource.Success -> {
                    binding.progressCircle.visibility = View.INVISIBLE
                    it.data?.let { response ->
                        quotesAdapter.differ.submitList(response)
                    }
                }

                is Resource.Error -> {
                    binding.progressCircle.visibility = View.INVISIBLE
                    it.message?.let { message ->
                        Log.d("MyTag", message)
                    }
                }

                is Resource.Loading -> {
                    binding.progressCircle.visibility = View.VISIBLE
                }

                else -> {}
            }
        }
    }

    private fun setupViewPager() {
        val quoteClickListener = object : QuoteClickListener {
            override fun likeOrUnLike(quote: QuoteUiModel) {
                if (!quote.liked) {
                    viewModel.saveQuote(quote)
                } else {
                    viewModel.deleteQuote(quote)
                }
                quote.liked = !quote.liked
            }

            override fun share(quote: QuoteUiModel) {

            }
        }
        quotesAdapter = QuotesAdapter(quoteClickListener)
        binding.viewPager.adapter = quotesAdapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}