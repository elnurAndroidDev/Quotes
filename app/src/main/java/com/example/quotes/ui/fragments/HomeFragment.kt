package com.example.quotes.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.quotes.R
import com.example.quotes.ui.MainActivity
import com.example.quotes.ui.QuotesViewModel

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: QuotesViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = (activity as MainActivity).viewModel
    }
}